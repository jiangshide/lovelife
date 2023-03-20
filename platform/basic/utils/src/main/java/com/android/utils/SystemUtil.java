package com.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 系统信息相关工具类
 * created by jiangshide on 2014-06-18.
 * email:18311271399@163.com
 */
public final class SystemUtil {

  public static final int notificationId = 0x123456;
  private static String appName;
  public static int REQUEST_CODE_IMAGE = 1000;

  public static boolean isDebug() {
    try {
      ApplicationInfo applicationInfo = AppUtil.getApplicationContext().getApplicationInfo();
      return (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 获取当前应用程序名称
   */
  public static String getAppName(Context context) {
    if (TextUtils.isEmpty(appName)) {
      PackageManager pm = context.getPackageManager();
      Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
      mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
      List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
      String currPkg = context.getPackageName();
      for (ResolveInfo resolveInfo : resolveInfos) {
        String pkg = resolveInfo.activityInfo.packageName;
        if (pkg.equals(currPkg)) {
          appName = (String) resolveInfo.loadLabel(pm);
        }
      }
    }
    return appName;
  }

  /**
   * 获取当前应用程序的版本名称
   */
  public static String getAppVersion(Context context) {
    String version = "0";
    try {
      version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
    } catch (NameNotFoundException e) {
      throw new RuntimeException(SystemUtil.class.getName() + "the application not found");
    }
    return version;
  }

  /**
   * 获取当前应用程序的版本号
   */
  public static int getAppVersionCode(Context context) {
    int version = 0;
    try {
      version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
    } catch (NameNotFoundException e) {
      throw new RuntimeException(SystemUtil.class.getName() + "the application not found");
    }
    return version;
  }

  /**
   * 获得设备硬件uuid
   * 使用硬件信息，计算出一个随机数
   *
   * @return 设备硬件uuid
   */
  public static String getDeviceUUID() {
    try {
      String dev = "3883756" +
          Build.BOARD.length() % 10 +
          Build.BRAND.length() % 10 +
          Build.DEVICE.length() % 10 +
          Build.HARDWARE.length() % 10 +
          Build.ID.length() % 10 +
          Build.MODEL.length() % 10 +
          Build.PRODUCT.length() % 10 +
          Build.SERIAL.length() % 10;
      return new UUID(dev.hashCode(),
          Build.SERIAL.hashCode()).toString();
    } catch (Exception ex) {
      ex.printStackTrace();
      return "";
    }
  }

  /**
   * 判断当前应用程序是否在后台运行
   */
  public static boolean isBackground(Context context) {
    ActivityManager activityManager =
        (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
    for (RunningAppProcessInfo appProcess : appProcesses) {
      if (appProcess.processName.equals(context.getPackageName())) {
        if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
          // 后台运行
          return true;
        } else {
          // 前台运行
          return false;
        }
      }
    }
    return false;
  }

  /**
   * 获取手机IMEI码
   */
  @SuppressLint("MissingPermission")
  public static String getPhoneIMEI() {
    try {
      TelephonyManager tm = (TelephonyManager) AppUtil.getApplicationContext()
          .getSystemService(Context.TELEPHONY_SERVICE);
      return tm.getDeviceId();
    } catch (Exception e) {
      LogUtil.e(e);
    }
    return "";
  }

  /**
   * 获取当前手机系统语言。
   *
   * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
   */
  public static String getSystemLanguage() {
    return Locale.getDefault().getLanguage();
  }

  /**
   * 获取当前系统上的语言列表(Locale列表)
   *
   * @return 语言列表
   */
  public static Locale[] getSystemLanguageList() {
    return Locale.getAvailableLocales();
  }

  /**
   * 获取手机系统SDK版本
   */
  public static int getSDKVersion() {
    return Build.VERSION.SDK_INT;
  }

  /**
   * 获取手机系统内核版本
   */
  public static String getKernelVersion() {
    String kernelVersion = "";
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream("/proc/version");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return kernelVersion;
    }
    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
    String info = "";
    String line = "";
    try {
      while ((line = bufferedReader.readLine()) != null) {
        info += line;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bufferedReader.close();
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      if (info != "") {
        final String keyword = "version ";
        int index = info.indexOf(keyword);
        line = info.substring(index + keyword.length());
        index = line.indexOf(" ");
        kernelVersion = line.substring(0, index);
      }
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
    return kernelVersion;
  }

  /**
   * 判断手机是否处于睡眠状态
   */
  public static boolean isSleeping(Context context) {
    KeyguardManager kgMgr = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();
    return isSleeping;
  }

  /**
   * 获取手机的可用内存大小
   *
   * @param type 1:availMem,2:,3:totalMem
   * @return 当前内存大小
   */
  public static long getDeviceMemory(int type) {
    ActivityManager am = (ActivityManager) AppUtil.getApplicationContext()
        .getSystemService(Context.ACTIVITY_SERVICE);
    MemoryInfo mi = new MemoryInfo();
    LogUtil.e("memory~totalMem:", mi.totalMem, " | availMem:", mi.availMem, " | threshold:",
        mi.threshold);
    am.getMemoryInfo(mi);
    if (type == 3) {
      return (mi.totalMem / (1024 * 1024));
    } else if (type == 2) {
      return (mi.threshold / (1024 * 1024));
    }
    return (mi.availMem / (1024 * 1024));
  }

  /**
   * 清理后台进程与服务
   *
   * @param cxt 应用上下文对象context
   * @return 被清理的数量
   */
  @SuppressLint("MissingPermission")
  public static int gc(Context cxt) {
    long i = getDeviceMemory(1);
    int count = 0; // 清理掉的进程数
    ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
    // 获取正在运行的service列表
    List<RunningServiceInfo> serviceList = am.getRunningServices(100);
    if (serviceList != null) {
      for (RunningServiceInfo service : serviceList) {
        if (service.pid == android.os.Process.myPid()) {
          continue;
        }
        try {
          android.os.Process.killProcess(service.pid);
          count++;
        } catch (Exception e) {
          e.getStackTrace();
          continue;
        }
      }
    }

    // 获取正在运行的进程列表
    List<RunningAppProcessInfo> processList = am.getRunningAppProcesses();
    if (processList != null) {
      for (RunningAppProcessInfo process : processList) {
        // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
        // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
        if (process.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
          // pkgList 得到该进程下运行的包名
          String[] pkgList = process.pkgList;
          for (String pkgName : pkgList) {
            try {
              am.killBackgroundProcesses(pkgName);
              count++;
            } catch (Exception e) { // 防止意外发生
              e.getStackTrace();
              continue;
            }
          }
        }
      }
    }
    return count;
  }

  //系统Intent

  /**
   * 在桌面创建快捷方式
   */
  public void createDeskShortCut(Context cxt, int icon, String title, Class<?> cls) {
    // 创建快捷方式的Intent
    Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
    // 不允许重复创建
    shortcutIntent.putExtra("duplicate", false);
    // 需要现实的名称
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
    // 快捷图片
    Parcelable ico = Intent.ShortcutIconResource.fromContext(cxt.getApplicationContext(), icon);
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, ico);
    Intent intent = new Intent(cxt, cls);
    // 下面两个属性是为了当应用程序卸载时桌面上的快捷方式会删除
    intent.setAction("android.intent.action.MAIN");
    intent.addCategory("android.intent.category.LAUNCHER");
    // 点击快捷图片，运行的程序主入口
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
    // 发送广播。OK
    cxt.sendBroadcast(shortcutIntent);
  }

  /**
   * 回到home，后台运行
   */
  public static void goHome(Context context) {
    Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
    mHomeIntent.addCategory(Intent.CATEGORY_HOME);
    mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    context.startActivity(mHomeIntent);
  }

  /**
   * 调用系统跳转到拨打电话页面
   */
  public static void call(Context context, String mobile) {
    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile));
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  /**
   * 调用系统发送短信，跳转发短信界面
   */
  public static void sendSMS(Context cxt, String number, String smsBody) {
    Uri smsToUri = Uri.parse("smsto:" + number);
    Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
    intent.putExtra("sms_body", smsBody);
    cxt.startActivity(intent);
  }

  /**
   * 调用系统短信接口，后台发送短信
   */
  public static void sendSMSBackground(String destinationAddress, String text,
      PendingIntent sentIntent, PendingIntent deliveryIntent) {
    SmsManager smsManager = SmsManager.getDefault();
    smsManager.sendTextMessage(destinationAddress, null, text, sentIntent, deliveryIntent);
  }

  public static void sendGroupSMS(Context context, String mobile, String content) {
    ArrayList<String> arrayList = new ArrayList<>();
    arrayList.add(mobile);
    sendGroupSMS(context, arrayList, content);
  }

  /**
   * 调用系统群发短信
   */
  public static void sendGroupSMS(Context context, ArrayList<String> mobileList, String content) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < mobileList.size(); i++) {
      stringBuilder.append(mobileList.get(i));
      stringBuilder.append(";");
    }
    if (stringBuilder.length() > 0) {
      stringBuilder.deleteCharAt(mobileList.size() - 1);
    }
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.putExtra("address", stringBuilder.toString());
    intent.putExtra("sms_body", content);
    //        intent.setType("vnd.android-dir/mms-sms");
    context.startActivity(intent);
  }

  /**
   * 调用系统邮件，跳转发送邮件界面
   */
  public static void sendEmail(Context context, String... emails) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("message/rfc822"); // 设置邮件格式
    intent.putExtra(Intent.EXTRA_EMAIL, emails); // 接收人
    intent.putExtra(Intent.EXTRA_CC, emails); // 抄送人
    intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
    intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
    context.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));

    //Uri uri = Uri.parse("mailto:"+emails[0]);
    //Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
    //intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
    //intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
    //intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
    //context.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
  }

  /**
   * 调用系统邮件，跳转发送邮件界面 无默认值
   */
  public static void sendEmail(Context context, String email) {
    Intent intent = new Intent(Intent.ACTION_SENDTO);
    intent.setData(Uri.parse("mailto:" + email));
    intent.putExtra(Intent.EXTRA_SUBJECT, "");
    intent.putExtra(Intent.EXTRA_TEXT, "");
    context.startActivity(intent);
  }

  /**
   * android获取一个用于打开HTML文件的intent
   */
  public static Intent getHtmlFileIntent(String param) {
    Uri uri = Uri.parse(param)
        .buildUpon()
        .encodedAuthority("com.android.htmlfileprovider")
        .scheme("content")
        .encodedPath(param)
        .build();
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.setDataAndType(uri, "text/html");
    return intent;
  }

  /**
   * android获取一个用于打开PDF文件的intent
   */
  public static Intent getPdfFileIntent(String url) {
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.addCategory("android.intent.category.DEFAULT");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Uri uri = Uri.fromFile(new File(url));
    intent.setDataAndType(uri, "application/pdf");
    return intent;
  }

  /**
   * android获取一个用于打开PDF文件的intent
   * from:net
   */
  public static Intent getPdfNetFileIntent(String url) {
    Intent intent = new Intent();
    intent.setAction("android.intent.action.VIEW");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Uri content_url = Uri.parse(url);
    intent.setData(content_url);
    return intent;
  }

  /**
   * android获取一个用于打开文本文件的intent
   */
  public static Intent getTextFileIntent(String param, boolean paramBoolean) {
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.addCategory("android.intent.category.DEFAULT");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (paramBoolean) {
      Uri uri1 = Uri.parse(param);
      intent.setDataAndType(uri1, "text/plain");
    } else {
      Uri uri2 = Uri.fromFile(new File(param));
      intent.setDataAndType(uri2, "text/plain");
    }
    return intent;
  }

  /**
   * android获取一个用于打开音频文件的intent
   */
  public static Intent getAudioFileIntent(String param) {
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra("oneshot", 0);
    intent.putExtra("configchange", 0);
    Uri uri = Uri.fromFile(new File(param));
    intent.setDataAndType(uri, "audio/*");
    return intent;
  }

  /**
   * android获取一个用于打开视频文件的intent
   */
  public static Intent getVideoFileIntent(String param) {
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra("oneshot", 0);
    intent.putExtra("configchange", 0);
    Uri uri = Uri.fromFile(new File(param));
    intent.setDataAndType(uri, "video/*");
    return intent;
  }

  /**
   * android 获取音视频bitrate
   */
  public static int getBitRate(String url) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(url);
    String bitRate =
        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
    if (!TextUtils.isEmpty(bitRate)) {
      return Integer.parseInt(bitRate) / 8192;
    }
    return 1024;
  }

  /**
   * android获取一个用于打开CHM文件的intent
   */
  public static Intent getChmFileIntent(String param) {
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.addCategory("android.intent.category.DEFAULT");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Uri uri = Uri.fromFile(new File(param));
    intent.setDataAndType(uri, "application/x-chm");
    return intent;
  }

  /**
   * android获取一个用于打开Word文件的intent
   */
  public static Intent getWordFileIntent(String param) {
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.addCategory("android.intent.category.DEFAULT");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Uri uri = Uri.fromFile(new File(param));
    intent.setDataAndType(uri, "application/msword");
    return intent;
  }

  /**
   * android获取一个用于打开Excel文件的intent
   */
  public static Intent getExcelFileIntent(String param) {
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.addCategory("android.intent.category.DEFAULT");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Uri uri = Uri.fromFile(new File(param));
    intent.setDataAndType(uri, "application/vnd.ms-excel");
    return intent;
  }

  /**
   * android获取一个用于打开PPT文件的intent
   */
  public static Intent getPptFileIntent(String param) {
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.addCategory("android.intent.category.DEFAULT");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Uri uri = Uri.fromFile(new File(param));
    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
    return intent;
  }

  public static int getStatusBarHeight() {
    int height = 60;
    Resources resources = AppUtil.getApplicationContext().getResources();
    int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
    int dimPix = resources.getDimensionPixelSize(resourceId);
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? (dimPix <= 0 ? height : dimPix)
        : 0;
  }

  public static int getNavigationBarHeight() {

    //boolean var1 = ViewConfiguration.get(var0).hasPermanentMenuKey();
    //int var2;
    //return (var2 = var0.getResources().getIdentifier("navigation_bar_height", "dimen", "android")) > 0 && !var1?var0.getResources().getDimensionPixelSize(var2):0;

    int height = 60;
    Resources resources = AppUtil.getApplicationContext().getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    int dimPix = resources.getDimensionPixelSize(resourceId);
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? (dimPix <= 0 ? height : dimPix)
        : 0;
  }

  private static boolean checkDeviceHasNavigationBar(Context context) {
    boolean hasNavigationBar = false;
    Resources rs = context.getResources();
    int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
    if (id > 0) {
      hasNavigationBar = rs.getBoolean(id);
    }
    try {
      Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
      Method m = systemPropertiesClass.getMethod("get", String.class);
      String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
      if ("1".equals(navBarOverride)) {
        hasNavigationBar = false;
      } else if ("0".equals(navBarOverride)) {
        hasNavigationBar = true;
      }
    } catch (Exception e) {

    }
    return hasNavigationBar;
  }

  public static boolean isNavigationBarShowing(Context context) {
    //判断手机底部是否支持导航栏显示
    boolean haveNavigationBar = checkDeviceHasNavigationBar(context);
    if (haveNavigationBar) {
      if (Build.VERSION.SDK_INT >= 17) {
        String brand = Build.BRAND;
        String mDeviceInfo;
        if (brand.equalsIgnoreCase("HUAWEI")) {
          mDeviceInfo = "navigationbar_is_min";
        } else if (brand.equalsIgnoreCase("XIAOMI")) {
          mDeviceInfo = "force_fsg_nav_bar";
        } else if (brand.equalsIgnoreCase("VIVO")) {
          mDeviceInfo = "navigation_gesture_on";
        } else if (brand.equalsIgnoreCase("OPPO")) {
          mDeviceInfo = "navigation_gesture_on";
        } else {
          mDeviceInfo = "navigationbar_is_min";
        }

        if (Settings.Global.getInt(context.getContentResolver(), mDeviceInfo, 0) == 0) {
          return true;
        }
      }
    }
    return false;
  }

  public static int getNavHeight(Context context) {
    Resources resources = context.getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0) {
      //判断底部导航栏是否为显示状态
      boolean navigationBarShowing = isNavigationBarShowing(context);
      if (navigationBarShowing) {
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
      }
    }
    return 0;
  }

  /**
   * 设置StatusBar沉侵式效果
   * api:21
   */
  public static void setNoStatusBarFullMode(AppCompatActivity activity, boolean isStatusBar,
      int colorPrimaryDark) {
    setNoStatusBarFullMode(activity, isStatusBar, colorPrimaryDark, true);
  }

  /**
   * 设置StatusBar沉侵式效果
   * api:21
   */
  public static void setNoStatusBarFullMode(AppCompatActivity activity, boolean isStatusBar,
      int colorPrimaryDark, boolean isDark) {

    activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //  //透明状态栏
    //  Window window = activity.getWindow();
    //  window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    //  //设置状态栏的颜色
    //  window.setStatusBarColor(colorPrimaryDark > 0 ? colorPrimaryDark : Color.TRANSPARENT);
    //  //设置底部的颜色
    //  // window.setNavigationBarColor(Color.TRANSPARENT);
    //
    //  //修复android7.0半透明问题
    //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    //    try {
    //      Field field = activity.getWindow()
    //          .getDecorView()
    //          .getClass()
    //          .getDeclaredField("mSemiTransparentStatusBarColor");  //获取特定的成员变量
    //      field.setAccessible(true);   //设置对此属性的可访问性
    //      field.setInt(activity.getWindow().getDecorView(), Color.TRANSPARENT);  //修改属性值
    //    } catch (Exception e) {
    //      e.printStackTrace();
    //    }
    //  }
    //}

    //setStatusBarColor(activity, isDark);
    //setNavigationBarColor(activity,colorPrimaryDark);
  }

  public static void setStatusBarColor(Activity activity, boolean dark) {
    View decor = activity.getWindow().getDecorView();
    if (dark) {
      decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    } else {
      decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
  }

  public static void setNavigationBarColor(Activity activity, int color) {
    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //  activity.getWindow().setNavigationBarColor(activity.getResources().getColor(color));
    //}
  }

  /**
   * 获取当前应用所有存活的activity
   */
  public static List<AppCompatActivity> getActivities() {
    List<AppCompatActivity> list = new ArrayList<>();
    try {
      Class<Application> applicationClass = Application.class;
      Field mLoadedApkField = applicationClass.getDeclaredField("mLoadedApk");
      mLoadedApkField.setAccessible(true);
      Object mLoadedApk = mLoadedApkField.get(AppUtil.getApplicationContext());
      Class<?> mLoadedApkClass = mLoadedApk.getClass();
      Field mActivityThreadField = mLoadedApkClass.getDeclaredField("mActivityThread");
      mActivityThreadField.setAccessible(true);
      Object mActivityThread = mActivityThreadField.get(mLoadedApk);
      Class<?> mActivityThreadClass = mActivityThread.getClass();
      Field mActivitiesField = mActivityThreadClass.getDeclaredField("mActivities");
      mActivitiesField.setAccessible(true);
      Object mActivities = mActivitiesField.get(mActivityThread);
      // 注意这里一定写成Map，低版本这里用的是HashMap，高版本用的是ArrayMap
      if (mActivities instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<Object, Object> arrayMap = (Map<Object, Object>) mActivities;
        for (Map.Entry<Object, Object> entry : arrayMap.entrySet()) {
          Object value = entry.getValue();
          Class<?> activityClientRecordClass = value.getClass();
          Field activityField = activityClientRecordClass.getDeclaredField("activity");
          activityField.setAccessible(true);
          Object object = activityField.get(value);
          list.add((AppCompatActivity) object);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      list = null;
    }
    return list;
  }

  public static boolean isLiveActivity(Class _class) {
    List<AppCompatActivity> list = getActivities();
    if (list != null && list.size() > 0) {
      for (AppCompatActivity activity : list) {
        if (activity.getClass().getName().equals(_class.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 安装apk文件
   *
   * @param apkFilePath apk文件路径
   */
  public static void installApkFile(String apkFilePath) {
    Context context = AppUtil.getApplicationContext();
    Intent intentForInstall = new Intent();
    intentForInstall.setAction(Intent.ACTION_VIEW);
    intentForInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    File apkFile = new File(apkFilePath);
    Uri apkUri;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      apkUri =
          FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkFile);
      intentForInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    } else {
      apkUri = Uri.fromFile(apkFile);
    }
    intentForInstall.setDataAndType(apkUri, "application/vnd.android.package-archive");
    context.startActivity(intentForInstall);
  }

  public static NotificationManager notificationManager(Context context) {
    return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  public static NotificationCompat.Builder notificationBuild(Context context, int icon,
      String title, String content) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    builder.setSmallIcon(icon);
    builder.setContentTitle(title).setContentText(content);
    notificationManager(context).notify(notificationId, builder.build());
    return builder;
  }

  public static void scrollTxt(TextView textView, String txt) {
    if (textView == null) return;
    textView.setText(TextUtils.isEmpty(txt) ? "" : txt);
    textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    textView.setSingleLine(true);
    textView.setSelected(true);
    textView.setFocusable(true);
    textView.setFocusableInTouchMode(true);
  }

  public static void showSystemCarema(Activity activity, Uri path) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
    try {
      activity.startActivityForResult(intent, REQUEST_CODE_IMAGE);
    } catch (ActivityNotFoundException e) {
      LogUtil.e(e);
    }
  }

  public static void showSystemPictureSelect(Activity activity, Uri path) {
    List cameraIntents = new ArrayList<Intent>();
    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    PackageManager packageManager = activity.getPackageManager();
    List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
    for (ResolveInfo res : listCam) {
      String packageName = res.activityInfo.packageName;
      Intent intent = new Intent(captureIntent);
      intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
      intent.setPackage(packageName);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
      cameraIntents.add(intent);
    }
    Intent galleryIntent = new Intent();
    galleryIntent.setType("image/*");
    galleryIntent.setAction(Intent.ACTION_PICK);

    Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Picture");
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray());
    try {
      activity.startActivityForResult(chooserIntent, REQUEST_CODE_IMAGE);
    } catch (ActivityNotFoundException e) {
      LogUtil.e(e);
    }
  }

  public static void hideKeyboard(Activity activity) {
    hideKeyboard(activity, null);
  }

  public static void hideKeyboard(Activity activity, View view) {
    if (activity == null) return;
    InputMethodManager inputMethodManager =
        (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (inputMethodManager == null) return;
    if (view == null) {
      inputMethodManager.hideSoftInputFromWindow(
          activity.getWindow().getDecorView().getWindowToken(), 0);
      return;
    }
    if (view.getWindowToken() == null) return;
    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public static void hideKeyboard(Context context, View view) {
    ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE))
        .hideSoftInputFromWindow(view.getWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS);
  }
}