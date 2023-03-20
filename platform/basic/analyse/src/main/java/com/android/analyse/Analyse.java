package com.android.analyse;

import android.text.TextUtils;
import android.view.View;
import com.android.utils.AppUtil;
import com.baidu.mobstat.StatService;
import java.util.HashMap;

/**
 * created by jiangshide on 2020-02-09.
 * email:18311271399@163.com
 */
public class Analyse {

  public static void event(String kv) {
    if (TextUtils.isEmpty(kv) || !kv.contains(":")) return;
    event(kv.split(":"));
  }

  public static void event(String... kv) {
    event(kv[0], kv[1]);
  }

  public static void event(String key, String value) {
    StatService.onEvent(AppUtil.getApplicationContext(), key, value);
  }

  public static void event(String key, String value, int times) {
    StatService.onEvent(AppUtil.getApplicationContext(), key, value, times);
  }

  public static void event(View view, HashMap<String, String> hashMap) {
    StatService.setAttributes(view, hashMap);
  }

  public static void eventStart(String key, String value) {
    StatService.onEventStart(AppUtil.getApplicationContext(), key, value);
  }

  public static void eventEnd(String key, String value) {
    StatService.onEventEnd(AppUtil.getApplicationContext(), key, value);
  }
}
