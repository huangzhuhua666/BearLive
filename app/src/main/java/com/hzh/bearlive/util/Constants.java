package com.hzh.bearlive.util;

/**
 * 常量
 */
public class Constants {

    //腾讯互动直播APP_ID
    public static final int ILIVE_APP_ID = 1400115870;

    //腾讯互动直播ACCOUNT_TYPE
    public static final int ILIVE_ACCOUNT_TYPE = 32262;

    //腾讯云对象存储APP_ID
    public static final String COS_APP_ID = "1257191458";

    //腾讯云对象存储桶所在地域
    public static final String COS_REGION = "ap-guangzhou";

    //腾讯云对象存储SecretId
    public static final String COS_SECRET_ID = "AKIDEG3arsv8PTHp79QQggQu74lCNIyPpePv";

    //腾讯云对象存储SecretKey
    public static final String COS_SECRET_KEY = "QcYFhusoVxA8g2B6oJaeV8XifPvDI7fx";

    //腾讯云对象存储桶名称
    public static final String COS_BUCKET = "bearlive-1257191458";

    //腾讯云对象存储SecretKey、签名有效时间
    public static final long COS_DURATION = 600L;

    //SharePreferences-key
    public static final String IS_FIRST_LOGIN = "is_first_login";

    //服务器的Url
    public static final String BASE_URL = "http://hzhbearlive.butterfly.mopaasapp.com/roomServlet";

    //创建直播房间的action
    public static final String ACTION_CREATE = "create";

    //进入直播房间的action
    public static final String ACTION_JOIN = "join";

    //退出直播房间的action
    public static final String ACTION_QUIT = "quit";

    //获取直播房间列表的action
    public static final String ACTION_GET_LIST = "getList";

    //获取观众列表的action
    public static final String ACTION_GET_WATCHER = "getWatcher";

    //心跳包检测的action
    public static final String ACTION_HEART_BEAT = "heartBeat";

}
