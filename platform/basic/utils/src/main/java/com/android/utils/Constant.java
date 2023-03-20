package com.android.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;

/**
 * created by jiangshide on 2019-08-17.
 * email:18311271399@163.com
 */
public final class Constant {
    public static String UPDATE_APK = "UPDATE_APK";
    public static String STORAGE_UPDATE = "STORAGE_UPDATE";

    public static String SYSTEM_STATUS_BAR_COLOR = "systemStatusBarColor";
    public static String SYSTEM_STATUS_BAR_ICON_COLOR = "systemStatusBarIconColor";

    public static String SYSTEM_NAVIGATION_COLOR = "systemNavigationColor";
    public static String SYSTEM_NAVIGATION_ICON_COLOR = "systemNavigationIconColor";

    public static String APP_COLOR_BG = "appColorBg";
    public static String APP_COLOR_FONT = "appColorFont";
    public static String APP_COLOR_FONT_LIGHT = "appColorFontLight";

    public static String APP_FONT_SIZE = "appFontSize";

    public static String APP_PROGRESS_DRAWABLE = "appProgressDrawable";

    public static int getColor(int color) {
        String colorStr = SPUtil.getString(APP_COLOR_BG);
        if (!TextUtils.isEmpty(colorStr)) {
            return Color.parseColor(colorStr);
        }
        return ContextCompat.getColor(AppUtil.getApplicationContext(), color);
    }
}
