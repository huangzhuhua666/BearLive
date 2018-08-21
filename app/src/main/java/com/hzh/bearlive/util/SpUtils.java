package com.hzh.bearlive.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {

    private SpUtils(){}

    private static SharedPreferences sp;

    public static void putBoolean(Context context, String key, boolean value) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).apply();

    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defaultValue);

    }

}
