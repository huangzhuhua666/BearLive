package com.hzh.bearlive.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.hzh.bearlive.app.MyApplication;
import com.hzh.bearlive.view.ChoosePicDialog;
import com.tencent.TIMUserProfile;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import java.io.File;
import java.io.IOException;

/**
 * 图片选择工具
 */
public class ChoosePicHelper {

    public enum PicType {
        Avatar, Cover
    }

    private static final int FROM_CAMERA = 100;
    private static final int FROM_ALBUM = 101;
    private static final int CROP = 102;

    private Activity mActivity;
    private Fragment mFragment;
    private TIMUserProfile mUserProfile;
    private Uri mCameraFileUri;
    private Uri mCropUri = null;
    private PicType mPicType;
    private OnChooseResultListener mListener;

    private int mCurrentVersion;

    public ChoosePicHelper(Activity activity, PicType type) {
        mActivity = activity;
        mPicType = type;
        mUserProfile = MyApplication.getSelfProfile();
        mCurrentVersion = Build.VERSION.SDK_INT;

    }

    public ChoosePicHelper(Fragment fragment, PicType type) {
        mFragment = fragment;
        mActivity = mFragment.getActivity();
        mPicType = type;
        mUserProfile = MyApplication.getSelfProfile();
        mCurrentVersion = Build.VERSION.SDK_INT;

    }

    public void showChoosePicDialog() {
        ChoosePicDialog dialog = new ChoosePicDialog(mActivity);
        dialog.setOnChooseListener(new ChoosePicDialog.OnChooseListener() {
            @Override
            public void onCamera() {
                //拍照
                takePic();
            }

            @Override
            public void onAlbum() {
                //从相册获取图片
                getFromAlbum();
            }
        });
        dialog.show();

    }

    /**
     * 拍照
     */
    private void takePic() {
        mCameraFileUri = createPicUri(false);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (mCurrentVersion < 24) {
            //小于7.0版本
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraFileUri);
        } else {
            //大于7.0版本
            ContentValues value = new ContentValues(1);
            value.put(MediaStore.Images.Media.DATA, mCameraFileUri.getPath());
            Uri uri = getImageContentUri(mCameraFileUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        if (mFragment == null) {
            mActivity.startActivityForResult(intent, FROM_CAMERA);
        } else {
            mFragment.startActivityForResult(intent, FROM_CAMERA);
        }

    }

    /**
     * 从相册获取图片
     */
    private void getFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (mFragment == null) {
            mActivity.startActivityForResult(intent, FROM_ALBUM);
        } else {
            mFragment.startActivityForResult(intent, FROM_ALBUM);
        }

    }

    /**
     * 获取图片的Uri
     *
     * @return Uri
     */
    private Uri createPicUri(boolean isCrop) {
        String dirPath = Environment.getExternalStorageDirectory() + "/"
                + mActivity.getApplication().getApplicationInfo().packageName;
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String id = "";
        if (mUserProfile != null) {
            id = mUserProfile.getIdentifier();
        }
        String fileName;
        if (isCrop) {
            fileName = id + System.currentTimeMillis() + "_crop.jpg";
        } else {
            fileName = id + ".jpg";
        }
        File picFile = new File(dirPath, fileName);
        if (picFile.exists()) {
            picFile.delete();
        }
        try {
            picFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(picFile);

    }

    /**
     * 转换content://uri
     *
     * @param uri uri
     * @return uri
     */
    private Uri getImageContentUri(Uri uri) {
        String filePath = uri.getPath();

        Cursor cursor = mActivity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=?",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();

            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            ContentValues value = new ContentValues();
            value.put(MediaStore.Images.Media.DATA, filePath);

            return mActivity.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value);
        }

    }

    /**
     * Activity返回结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_CAMERA) {
            //拍照获取照片
            if (resultCode == Activity.RESULT_OK) {
                startCrop(mCameraFileUri);
            }
        } else if (requestCode == FROM_ALBUM) {
            //从相册获取照片
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                startCrop(uri);
            }
        } else if (requestCode == CROP) {
            //裁剪
            if (resultCode == Activity.RESULT_OK) {
                //上传到服务器保存起来
                uploadToCos(mCropUri.getPath());
            }
        }

    }

    /**
     * 裁剪图片
     *
     * @param uri 图片的uri
     */
    private void startCrop(Uri uri) {
        mCropUri = createPicUri(true);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("crop", "true");
        if (mPicType == PicType.Avatar) {
            intent.putExtra("aspectX", 300);
            intent.putExtra("aspectY", 300);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
        } else if (mPicType == PicType.Cover) {
            intent.putExtra("aspectX", 500);
            intent.putExtra("aspectY", 300);
            intent.putExtra("outputX", 500);
            intent.putExtra("outputY", 300);
        }
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        if (mCurrentVersion < 24) {
            //小于7.0版本
            intent.setDataAndType(uri, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri);
        } else {
            //大于7.0版本
            String scheme = uri.getScheme();
            if (scheme.equals("content")) {
                intent.setDataAndType(uri, "image/*");
            } else {
                Uri contentUri = getImageContentUri(uri);
                intent.setDataAndType(contentUri, "image/*");
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri);
        }
        if (mFragment == null) {
            mActivity.startActivityForResult(intent, CROP);
        } else {
            mFragment.startActivityForResult(intent, CROP);
        }

    }

    /**
     * 上传图片到腾讯Cos
     *
     * @param srcPath srcPath
     */
    private void uploadToCos(String srcPath) {
        boolean isAvatar = false;
        switch (mPicType) {
            case Avatar:
                isAvatar = true;
                break;
            case Cover:
                isAvatar = false;
                break;
            default:
                break;
        }
        TencentCosHelper.uploadPic(srcPath, isAvatar, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                //上传成功
                if (mListener != null) {
                    mListener.onSuccess(cosXmlResult.accessUrl);
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException e, CosXmlServiceException e1) {
                //上传失败
                if (mListener != null) {
                    mListener.onFail(e != null ? e.toString() : e1.toString());
                }
            }
        });

    }

    public void setOnChooseResultListener(OnChooseResultListener listener) {
        mListener = listener;

    }

    public interface OnChooseResultListener {

        void onSuccess(String url);

        void onFail(String msg);

    }

}
