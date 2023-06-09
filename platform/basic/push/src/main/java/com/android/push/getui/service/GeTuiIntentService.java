package com.android.push.getui.service;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.android.event.ZdEvent;
import com.android.network.NetWork;
import com.android.push.Push;
import com.android.push.R;
import com.android.push.data.PushBean;
import com.android.push.listener.OnPushListener;
import com.android.push.receiver.PushReceiver;
import com.android.utils.AppUtil;
import com.android.utils.JsonUtil;
import com.android.utils.LogUtil;
import com.android.utils.SPUtil;
import com.android.widget.ZdNotification;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

/**
 * created by jiangshide on 2020/4/24.
 * email:18311271399@163.com
 */
public class GeTuiIntentService extends GTIntentService {

  private static OnPushListener mOnPushListener;

  public GeTuiIntentService() {
  }

  public void setListener(OnPushListener listener) {
    mOnPushListener = listener;
  }

  @Override
  public void onReceiveServicePid(Context context, int pid) {
  }

  /**
   * 处理透传消息
   */
  @Override
  public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
    String data = new String(msg.getPayload());
    if (TextUtils.isEmpty(data)) {
      return;
    }
    PushBean pushBean = parseData(context, data);
    LogUtil.e("push~data:", data);
    if (pushBean == null || TextUtils.isEmpty(pushBean.push_content)) return;
    pushBean.isInner = AppUtil.isAppForeground();
    if (pushBean.isInner && pushBean.is_float) {
      ZdEvent.Companion.get().with(Push.PUSH).post(pushBean);
      if (mOnPushListener != null) {
        mOnPushListener.push(pushBean);
      }
      return;
    }
    Bundle bundle = new Bundle();
    bundle.putSerializable(Push.PUSH, pushBean);
    ZdNotification.getInstance().create()
        .setTitle(pushBean.title)
        .setContent(pushBean.push_content)
        .setIsClear(pushBean.is_clear)
        .setIsRing(pushBean.is_ring)
        .setLights(pushBean.show_type)
        .setIcon(R.drawable.push)
        .setVibrate(pushBean.is_vibrate)
        .setClass(PushReceiver.PUSH, bundle, PushReceiver.class)
        .build();
    if (pushBean.show_type) {
      AppUtil.wakeUpAndUnlock();
    }
  }

  public static PushBean parseData(Context context, String data) {
    PushBean pushBean = JsonUtil.fromJson(data, PushBean.class);
    if (pushBean == null || pushBean.action == -1) return pushBean;
    if (TextUtils.isEmpty(pushBean.title)) {
      pushBean.title = pushBean.push_title;
    }
    if (pushBean.is_wifi && !NetWork.Companion.getInstance().isNetworkAvailable()) {
      LogUtil.e("push~no net:",NetWork.Companion.getInstance().isNetworkAvailable());
      return pushBean;
    }
    //文章
    if (pushBean.action == 0 && !TextUtils.isEmpty(pushBean.content) && pushBean.content.contains(
        "$")) {
      pushBean.content = pushBean.content.substring(1, pushBean.content.length());
    }
    return pushBean;
  }

  /**
   * 接收 cid
   */
  @Override
  public void onReceiveClientId(Context context, String clientid) {
    LogUtil.e("push~clientId:", clientid);
    SPUtil.putString(Push.CLIENT_ID, clientid);
  }

  /**
   * cid 离线上线通知
   */
  @Override
  public void onReceiveOnlineState(Context context, boolean online) {
    LogUtil.e("push~online:", online);
  }

  /**
   * 各种事件处理回执
   * 1.如果没有任何Action与透传直接打开 => 打开appp
   * 2.
   */
  @Override
  public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
  }

  /**
   * 通知到达，只有个推通道下发的通知会回调此方法
   */
  @Override
  public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
    PushBean pushBean = new PushBean().setTaskId(msg.getTaskId())
        .setMsgId(msg.getMessageId())
        .setTitle(msg.getTitle())
        .setContent(msg.getContent());
    LogUtil.e("push~notification:", msg.getContent());
    if (mOnPushListener != null && AppUtil.isAppForeground()) {
      mOnPushListener.push(pushBean);
    }
    if (pushBean.show_type) {
      AppUtil.wakeUpAndUnlock();
    }
  }

  /**
   * 通知点击，只有个推通道下发的通知会回调此方法
   */
  @Override
  public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
    if (msg == null || TextUtils.isEmpty(msg.getContent())) return;
    LogUtil.e("push~notification~click:", msg.getContent());
    PushBean pushBean = JsonUtil.fromJson(msg.getContent(), PushBean.class);
    if (pushBean == null || pushBean.action == -1 || TextUtils.isEmpty(pushBean.push_content)) return;
    pushBean.setTaskId(msg.getTaskId()).setTitle(msg.getTitle()).setContent(msg.getContent());

    Push.getInstance().setPushBean(pushBean);
    if (pushBean.is_wifi && !NetWork.Companion.getInstance().isNetworkAvailable()) {
      return;
    }
    pushBean.isInner = AppUtil.isAppForeground();
    if (!pushBean.isInner) {
      AppUtil.goActivity(Push.MAIN);
    }
    if (mOnPushListener != null) {
      mOnPushListener.push(pushBean);
    }
  }
}
