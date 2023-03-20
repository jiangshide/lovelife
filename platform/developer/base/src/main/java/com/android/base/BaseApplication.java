package com.android.base;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Keep;
import androidx.annotation.RequiresApi;
import com.android.http.Http;
import com.android.utils.LogUtil;
import com.android.widget.spedit.emoji.EmojiManager;
import com.android.zdrouter.ZdRouter;

/**
 * created by jiangshide on 2019-07-23.
 * email:18311271399@163.com
 */
public class BaseApplication extends Application {

  public static BaseApplication instance;

  @Override
  protected void attachBaseContext(Context base) {
    //MultiDex.install(base);
    super.attachBaseContext(base);
    //        loop();
    //ZdRouter.getInstance().init(this);//路由初始化
  }

  @Override public void onCreate() {
    super.onCreate();
    instance = this;
    Http.INSTANCE.init(instance);//网络初始化
    //Snake.init(this);
    //Push.getInstance().init(this,this).setChannel("jsd");
    //loop();
    //Intent service = new Intent(getApplicationContext(), BaseService.class);
    //getApplicationContext().startService(service);
    //initAccessTokenWithAkSk();
    /*
     * 配置 融云 IM 消息推送
     * 根据需求配置各个平台的推送
     * 配置推送需要在初始化 融云 SDK 之前
     */
    //PushConfig config = new PushConfig
    //    .Builder()
    //    .enableHWPush(true)        // 在 AndroidManifest.xml 中搜索 com.huawei.hms.client.appid 进行设置
    //    //.enableMiPush("替换为您的小米推送 AppId", "替换为您的小米推送 AppKey")
    //    //.enableMeiZuPush("替换为您的魅族推送 AppId", "替换为您的魅族推送 AppKey")
    //    //.enableVivoPush(true)     // 在 AndroidManifest.xml 中搜索 com.vivo.push.api_key 和 com.vivo.push.app_id 进行设置
    //    .enableFCM(true)          // 在 google-services.json 文件中进行配置
    //    .build();
    //RongPushClient.setPushConfig(config);
    //
    //RongIMClient.setServerInfo(BuildConfig.NAVI_SERVER, BuildConfig.FILE_SERVER);
    //RongIM.init(this, BuildConfig.IM_APP_KEY);//the im init
    //RongIM.registerMessageType(GroupMessage.class);
    //RongIM.registerMessageType(DocumentMessage.class);
    //RongIM.registerMessageType(SysMessage.class);
  }

  private void loop() {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @RequiresApi(api = Build.VERSION_CODES.M)
      @Override
      public void run() {
        while (true) {
          try {
            Looper.loop();
          } catch (Exception e) {
            LogUtil.e(e);
          }
        }
      }
    });
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
  }
}
