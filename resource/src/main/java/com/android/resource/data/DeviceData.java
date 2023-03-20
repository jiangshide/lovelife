package com.android.resource.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import com.android.network.NetWork;
import com.android.utils.AppUtil;
import com.android.utils.DateUtil;
import com.android.utils.LogUtil;
import com.android.utils.ScreenUtil;
import com.android.utils.SystemUtil;
import com.google.gson.Gson;

/**
 * created by jiangshide on 2020/3/23.
 * email:18311271399@163.com
 */
public class DeviceData {
  public Long uid;
  public Long positionId;
  public String uuid;
  public String netOperator;//获取运营商
  public String netName;//获取联网方式
  public String netSpeed;//网络速度
  public String meid;//获取meid ?
  public String imei;//获取imei1 ?
  public String imei2;//获取imei2 ?
  public String inumeric;//获取双卡手机的imei ?
  public long totalMem;//总共内存
  public long threshold;//内存阀值
  public long availMem;//可用内存
  public String mac;//
  public String board;//主板
  public String brand;//系统定制商
  public String device;//设备参数
  public String display;//显示屏参数
  public String fingerprint;//唯一编号
  public String serial;//硬件序列号
  public String manufacturer;//硬件制造商
  public String model;//机型
  public String hardware;//硬件名
  public String product;//手机产品名
  public String type;// Builder类型
  public String host;// HOST值
  public String user; // User名
  public long time;// 编译时间
  public String osVersion;//os版本号
  public String osName;//os名称
  public String osArch;//os架构
  public int sdkVersion;//当前sdk版本
  public String appName;//应用名称
  public String pkg;//应用包名
  public int versionCode;//应用code
  public String versionName;//应用版本
  public String os;//来自os平台
  public String resolution;//分辨率
  public String timeZone;//时区
  public int battery;//电量
  public long elapsedRealtime;//开机时间
  public String language;//语言

  public DeviceData(Activity activity) {
    uuid = SystemUtil.getDeviceUUID();
    netOperator = getNetOperator(activity);
    netName = NetWork.Companion.getInstance().getNetWorkTypeName(activity);
    netSpeed = NetWork.Companion.getInstance().getNetSpeed(activity);
    inumeric = "";
    imei = SystemUtil.getPhoneIMEI();
    totalMem = SystemUtil.getDeviceMemory(3);
    threshold = SystemUtil.getDeviceMemory(2);
    availMem = SystemUtil.getDeviceMemory(1);
    mac = getLocalMacAddress();
    osVersion = System.getProperty("os.version");
    osName = System.getProperty("os.name");
    osArch = System.getProperty("os.arch");
    sdkVersion = SystemUtil.getSDKVersion();
    board = Build.BOARD;
    brand = Build.BRAND;
    device = Build.DEVICE;
    display = Build.DISPLAY;
    fingerprint = Build.FINGERPRINT;
    serial = Build.SERIAL;
    manufacturer = Build.MANUFACTURER;
    model = Build.MODEL;
    hardware = Build.HARDWARE;
    product = Build.PRODUCT;
    host = Build.HOST;
    user = Build.USER;
    type = Build.TYPE;
    time = Build.TIME;
    appName = AppUtil.getAppName();
    pkg = AppUtil.getPackageName();
    versionCode = AppUtil.getAppVersionCode();
    versionName = AppUtil.getAppVersionName();
    os = getOS();
    resolution =
        ScreenUtil.getRtScreenWidth(activity) + "x" + ScreenUtil.getRtScreenHeight(activity);
    timeZone = DateUtil.getGmtTimeZone();
    battery = getSystemBattery();
    elapsedRealtime = SystemClock.elapsedRealtime();
    language = SystemUtil.getSystemLanguage();
  }

  public String getGson() {
    return new Gson().toJson(this);
  }

  @Override public String toString() {
    return "DeviceData{" +
        "uid=" + uid +
        ", positionId=" + positionId +
        ", uuid='" + uuid + '\'' +
        ", netOperator='" + netOperator + '\'' +
        ", netName='" + netName + '\'' +
        ", netSpeed='" + netSpeed + '\'' +
        ", meid='" + meid + '\'' +
        ", imei='" + imei + '\'' +
        ", imei2='" + imei2 + '\'' +
        ", inumeric='" + inumeric + '\'' +
        ", totalMem=" + totalMem +
        ", threshold=" + threshold +
        ", availMem=" + availMem +
        ", mac='" + mac + '\'' +
        ", board='" + board + '\'' +
        ", brand='" + brand + '\'' +
        ", device='" + device + '\'' +
        ", display='" + display + '\'' +
        ", fingerprint='" + fingerprint + '\'' +
        ", serial='" + serial + '\'' +
        ", manufacturer='" + manufacturer + '\'' +
        ", model='" + model + '\'' +
        ", hardware='" + hardware + '\'' +
        ", product='" + product + '\'' +
        ", type='" + type + '\'' +
        ", host='" + host + '\'' +
        ", user='" + user + '\'' +
        ", time=" + time +
        ", osVersion='" + osVersion + '\'' +
        ", osName='" + osName + '\'' +
        ", osArch='" + osArch + '\'' +
        ", sdkVersion=" + sdkVersion +
        ", appName='" + appName + '\'' +
        ", pkg='" + pkg + '\'' +
        ", versionCode=" + versionCode +
        ", versionName='" + versionName + '\'' +
        ", os='" + os + '\'' +
        ", resolution='" + resolution + '\'' +
        ", timeZone='" + timeZone + '\'' +
        ", battery=" + battery +
        ", elapsedRealtime=" + elapsedRealtime +
        ", language='" + language + '\'' +
        '}';
  }

  /**
   * 实时获取电量
   */
  public static int getSystemBattery() {
    int level = 0;
    Intent batteryInfoIntent =
        AppUtil.getApplicationContext().getApplicationContext().registerReceiver(null,
            new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    level = batteryInfoIntent.getIntExtra("level", 0);
    int batterySum = batteryInfoIntent.getIntExtra("scale", 100);
    int percentBattery = 100 * level / batterySum;
    LogUtil.e("level = " + level);
    LogUtil.e("batterySum = " + batterySum);
    LogUtil.e("percent is " + percentBattery + "%");
    return percentBattery;
  }

  public String getLocalMacAddress() {
    try {
      WifiManager wifi =
          (WifiManager) AppUtil.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
      WifiInfo info = wifi.getConnectionInfo();
      return info.getMacAddress();
    } catch (Exception e) {
      LogUtil.e(e);
    }
    return "";
  }

  /**
   * 获取运营商
   */
  public String getNetOperator(Context context) {
    TelephonyManager manager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    String iNumeric = manager.getSimOperator();
    String netOperator = "";
    if (iNumeric.length() > 0) {
      if (iNumeric.equals("46000") || iNumeric.equals("46002")) {
        // 中国移动
        netOperator = "中国移动";
      } else if (iNumeric.equals("46003")) {
        // 中国电信
        netOperator = "中国电信";
      } else if (iNumeric.equals("46001")) {
        // 中国联通
        netOperator = "中国联通";
      } else {
        //未知
        netOperator = "未知";
      }
    }
    return netOperator;
  }

  /**
   * 获取操作系统
   */
  public String getOS() {
    return "Android" + Build.VERSION.RELEASE;
  }
}
