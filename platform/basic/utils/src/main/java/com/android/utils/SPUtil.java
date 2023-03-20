package com.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

/**
 * created by jiangshide on 2014-12-11.
 * email:18311271399@163.com
 */
public final class SPUtil {

  public static SharedPreferences.Editor getEdit() {
    SharedPreferences sharedPreferences = AppUtil.getApplicationContext()
        .getSharedPreferences(AppUtil.getPackageName(), Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    return editor;
  }

  public static SharedPreferences getSharedPreferences() {
    return AppUtil.getApplicationContext()
        .getSharedPreferences(AppUtil.getPackageName(), Context.MODE_PRIVATE);
  }

  public static boolean putBoolean(String key, boolean value) {
    return getEdit().putBoolean(key, value).commit();
  }

  public static boolean putFloat(String key, float value) {
    return getEdit().putFloat(key, value).commit();
  }

  public static boolean putInt(String key, int value) {
    return getEdit().putInt(key, value).commit();
  }

  public static boolean putLong(String key, long value) {
    return getEdit().putLong(key, value).commit();
  }

  public static boolean putString(String key, String value) {
    LogUtil.e("key:", key, " | value:", value);
    return getEdit().putString(key, value).commit();
  }

  public static boolean put(Object clazz) {
    return put(null, clazz);
  }

  public static boolean put(String key, Object clazz) {
    LogUtil.e("key:", key, " | clazz:", clazz);
    try {
      putString(TextUtils.isEmpty(key) ? clazz.getClass().getSimpleName() : key,
          new Gson().toJson(clazz));
    } catch (Exception e) {
      LogUtil.e(e);
    }
    return false;
  }

  public static boolean put(List<?> list) {
    return put(null, list);
  }

  public static boolean put(String key, List<?> list) {
    if (list == null || list.size() == 0) return false;
    try {
      return putString(TextUtils.isEmpty(key) ? list.get(0).getClass().getCanonicalName() : key,
          new Gson().toJson(list));
    } catch (Exception e) {
      LogUtil.e(e);
    }
    return false;
  }

  public static boolean getBoolean(String key) {
    return getSharedPreferences().getBoolean(key, false);
  }

  public static boolean getBoolean(String key,Boolean defaultValue) {
    return getSharedPreferences().getBoolean(key, defaultValue);
  }

  public static float getFloat(String key) {
    return getSharedPreferences().getFloat(key, -1);
  }

  public static int getInt(String key) {
    return getSharedPreferences().getInt(key, -1);
  }

  public static int getInt(String key,int defaultValue) {
    return getSharedPreferences().getInt(key, defaultValue);
  }

  public static long getLong(String key) {
    return getSharedPreferences().getLong(key, -1);
  }

  public static String getString(String key) {
    String value = getSharedPreferences().getString(key, "");
    return value;
  }

  public static <T> T get(Class<T> clazz) {
    return get(null, clazz);
  }

  public static <T> T get(String key, Class<T> clazz) {
    String json = getString(TextUtils.isEmpty(key) ? clazz.getSimpleName() : key);
    if (TextUtils.isEmpty(json)) return null;
    try {
      return new Gson().fromJson(json, clazz);
    } catch (Exception e) {
      LogUtil.e(e);
    }
    return null;
  }

  public static <T> List<T> getList(Class<T> clazz) {
    return getList(null, clazz);
  }

  public static <T> List<T> getList(String key, Class<T> clazz) {
    String json = getString(TextUtils.isEmpty(key) ? clazz.getCanonicalName() : key);
    if (TextUtils.isEmpty(json)) return null;
    try {
      //return new Gson().fromJson(json, new TypeToken<List<T>>() {
      //}.getType());
      List<T> list = new ArrayList<>();
      JsonArray array = new JsonParser().parse(json).getAsJsonArray();
      for (JsonElement jsonElement : array) {
        list.add(new Gson().fromJson(jsonElement, clazz));
      }
      return list;
    } catch (Exception e) {
      LogUtil.e(e);
    }
    return null;
  }

  public static boolean clear() {
    return getEdit().clear().commit();
  }

  public static boolean clear(String key) {
    return getEdit().remove(key).commit();
  }
}
