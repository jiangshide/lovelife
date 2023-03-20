package com.android.push;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.android.event.ZdEvent;
import com.android.push.data.PushBean;
import com.android.push.getui.GeTui;
import com.android.push.listener.OnPushListener;
import com.android.push.receiver.PushReceiver;
import com.android.utils.AppUtil;
import com.android.utils.LogUtil;
import com.android.utils.SPUtil;
import com.android.widget.ZdNotification;

/**
 * created by jiangshide on 2020-01-23.
 * email:18311271399@163.com
 */
public class Push {

  /**
   * 使用event时当作key注册来接收PushBean对象消息
   */
  public static final String PUSH = "push";
  public static final String ACTION = "action";
  public static final String CLIENT_ID = "clientId";
  public static final String MAIN = "com.weishang.wxrd.activity.SplashActivity";

  public String userId;

  private PushBean pushBean;

  public PushBean getPushBean() {
    return pushBean;
  }

  public void setPushBean(PushBean pushBean) {
    this.pushBean = pushBean;
  }

  private static class PushHolder {
    private static Push instance = new Push();
  }

  public static Push getInstance() {
    return PushHolder.instance;
  }

  public Push init(Context context) {
    return init(context, null);
  }

  public Push init(Context context, OnPushListener listener) {
    return init(context, listener, false);
  }

  public Push init(Context context, boolean isSync) {
    return init(context, null, isSync);
  }

  public Push init(final Context context, final OnPushListener listener, boolean isSync) {
    if (isSync) {
      new Handler().post(new Runnable() {
        @Override public void run() {
          GeTui.getInstance().init(context, listener);
        }
      });
    } else {
      GeTui.getInstance().init(context, listener);
    }
    return this;
  }

  public void bindAlias(String... alias) {
    GeTui.getInstance().bindAlias(alias);
  }

  public void unBindAlias(String... alias) {
    GeTui.getInstance().unBindAlias(alias);
  }

  public Push setTags(String... tags) {
    GeTui.getInstance().setTag(tags);
    return this;
  }

  public Push setChannel(String channel) {
    GeTui.getInstance().setChannel(channel);
    return this;
  }

  public Push setListener(OnPushListener listener) {
    GeTui.getInstance().setListener(listener);
    return this;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return this.userId;
  }

  public String getClientId() {
    return SPUtil.getString(CLIENT_ID);
  }
}
