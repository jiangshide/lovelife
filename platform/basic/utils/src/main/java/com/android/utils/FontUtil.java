package com.android.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * created by jiangshide on 2020/5/29.
 * email:18311271399@163.com
 */
public class FontUtil {
  /**
   * 字体
   */
  private static Typeface typeFace;

  private static FontUtil _FontUtil;

  public FontUtil(Context context) {
    typeFace = Typeface.createFromAsset(context.getAssets(),
        "fonts/iconfont.ttf");
  }

  public FontUtil(Context context,String path){
    typeFace = Typeface.createFromAsset(context.getAssets(),
        path);
  }

  public static FontUtil getInstance(Context context) {
    if (_FontUtil == null) {
      _FontUtil = new FontUtil(context);
    }
    return _FontUtil;
  }

  public Typeface getTypeFace() {
    return typeFace;
  }
}
