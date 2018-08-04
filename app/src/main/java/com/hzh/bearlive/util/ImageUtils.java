package com.hzh.bearlive.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

/**
 * 图片加载的工具类
 */
public class ImageUtils {

    /**
     * 加载圆形图片
     *
     * @param context   Context
     * @param resId     resId
     * @param imageView ImageView
     */
    public static void loadRound(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(imageView);

    }

    /**
     * 加载圆形图片
     *
     * @param context   Context
     * @param url       Url
     * @param imageView ImageView
     */
    public static void loadRound(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(imageView);

    }

    /**
     * 加载正常图片
     *
     * @param context   Context
     * @param url       Url
     * @param imageView ImageView
     */
    public static void load(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);

    }

    /**
     * 加载正常图片
     *
     * @param context   Context
     * @param resId     resId
     * @param imageView ImageView
     */
    public static void load(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .into(imageView);

    }

}
