package com.android.jpush.receiver

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import cn.jmessage.support.google.gson.Gson
import cn.jpush.android.api.CmdMessage
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.api.JPushMessage
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver
import com.android.event.ZdEvent
import com.android.jpush.PUSH
import com.android.jpush.data.PushBean
import com.android.utils.LogUtil

/**
 * created by jiangshide on 2020/5/21.
 * email:18311271399@163.com
 */
class JReceiver : JPushMessageReceiver() {

  override fun onMessage(
    p0: Context?,
    p1: CustomMessage?
  ) {
    super.onMessage(p0, p1)

    LogUtil.e("-----push:", p1)
  }

  override fun onNotifyMessageOpened(
    p0: Context?,
    p1: NotificationMessage
  ) {
    super.onNotifyMessageOpened(p0, p1)
    LogUtil.e("-----push:", p1)
    if (p1 != null && !TextUtils.isEmpty(p1.notificationExtras)) {
      val pushBean = Gson().fromJson<PushBean>(p1.notificationExtras, PushBean::class.java)
      pushBean.onClick = true
      ZdEvent.get()
          .with(PUSH)
          .post(pushBean)
    }
  }

  override fun onMultiActionClicked(
    p0: Context?,
    p1: Intent?
  ) {
//    super.onMultiActionClicked(p0, p1)
    LogUtil.e("-----push:", p1)
  }

  override fun onNotifyMessageArrived(
    p0: Context?,
    p1: NotificationMessage?
  ) {
    super.onNotifyMessageArrived(p0, p1)
    LogUtil.e("-----push:", p1)
    if (p1 != null && !TextUtils.isEmpty(p1.notificationExtras)) {
      val pushBean = Gson().fromJson<PushBean>(p1.notificationExtras, PushBean::class.java)
      ZdEvent.get()
          .with(PUSH)
          .post(pushBean)
    }
  }

  override fun onNotifyMessageDismiss(
    p0: Context?,
    p1: NotificationMessage?
  ) {
    super.onNotifyMessageDismiss(p0, p1)
    LogUtil.e("-----push:", p1)
  }

  override fun onRegister(
    p0: Context?,
    p1: String?
  ) {
    super.onRegister(p0, p1)
    LogUtil.e("-----push:", p1)
  }

  override fun onConnected(
    p0: Context?,
    p1: Boolean
  ) {
    super.onConnected(p0, p1)
    LogUtil.e("-----push:", p1)
  }

  override fun onCommandResult(
    p0: Context?,
    p1: CmdMessage?
  ) {
    super.onCommandResult(p0, p1)
    LogUtil.e("-----push:", p1)
  }

  override fun onAliasOperatorResult(
    p0: Context?,
    p1: JPushMessage?
  ) {
    super.onAliasOperatorResult(p0, p1)
    LogUtil.e("-----push:", p1)
  }

  override fun onNotificationSettingsCheck(
    p0: Context?,
    p1: Boolean,
    p2: Int
  ) {
    super.onNotificationSettingsCheck(p0, p1, p2)
  }
}