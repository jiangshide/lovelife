package com.android.im.rongcloud.push;

import android.content.Context;
import com.android.event.ZdEvent;
import com.android.utils.LogUtil;
import io.rong.push.PushType;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

import static com.android.im.rongcloud.Im.IM;

/**
 * created by jiangshide on 2020-01-21.
 * email:18311271399@163.com
 *
 * String pushId;    //对应推送消息的唯一Id,如果是消息转push，则为消息的uid
 * RongPushClient.ConversationType conversationType;  //会话类型
 * String objectName;  // 消息类型：RC:Txt RC:Img ...
 * String senderId;  // 发送者ID
 * String senderName; // 发送者名称
 * Uri senderPortrait;  //发送者头像地址
 * String targetId;        // 目标Id。ex: 群里的某人发了一条消息，则targetId为群Id，senderId为群里的这个用户的Id。
 * String targetUserName;  //目标名字。
 * String toId; //该推送的目标用户。
 * String pushTitle;  //推送消息的标题
 * String pushContent; //推送消息内容
 * String pushData;    // 客户端发送push消息时的附加字段
 * String isFromPush;  //是push消息时为true, 后台消息时为false
 * PushSourceType sourceType; //推送来源。
 *
 *
 * 1: 跟隨者收到跟随的公司发公告通知，
 *
 * 2: 每日17点參會人收到会议邀请通知，
 *
 * 3: 每日10点发出参会人出席明日会议提醒通知，
 *
 * 4: 每日10点发给创建人明日参会同意提醒通知，
 *
 * 5: 创建人收到新报名通知，
 *
 * 6: 参会人收到报名通过通知，
 *
 * 7: 参会人收到报名被拒通知，
 *
 * 8: 公司员工收到新跟随者通知，
 *
 * 9: 参会人收到活动时间更改通知，
 *
 * 10: 创建人收到参会人取消报名通知，
 *
 * 11: 每日17点发送会邀已送出通知给创建人
 *
 * 12: 用户注册审核通过通知
 *
 * 13: 用户注册审核被拒通知
 *
 * 14: 每日17点发送参会人今日需填写调查问卷通知
 */
public class ImPushReceiver extends PushMessageReceiver {

  @Override public boolean onNotificationMessageArrived(Context context, PushType pushType,
      PushNotificationMessage message) {
    ZdEvent.Companion.get().with(IM).post(message);
    LogUtil.e("push~pushType:", pushType, " | message:", message);
    return false;
  }

  @Override public boolean onNotificationMessageClicked(Context context, PushType pushType,
      PushNotificationMessage message) {
    LogUtil.e("push~message:", message, " | pushType:", pushType);
    ZdEvent.Companion.get().with(IM).post(message);
    //if (!message.getSourceType().equals(PushNotificationMessage.PushSourceType.FROM_ADMIN)) {
    //  String targetId = message.getTargetId();
    //  //10000 为 Demo Server 加好友的 id，若 targetId 为 10000，则为加好友消息，默认跳转到 NewFriendListActivity
    //  if (targetId != null && targetId.equals("10000")) {
    //    Intent intentMain = new Intent(context, NewFriendListActivity.class);
    //    intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
    //    Intent intentNewFriend = new Intent(context, MainActivity.class);
    //    intentNewFriend.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
    //    Intent[] intents = new Intent[]{};
    //    intents[0] = intentMain;
    //    intents[1] = intentNewFriend;
    //    context.startActivities(intents);
    //    return true;
    //  } else {
    //    Intent intentMain = new Intent(context, MainActivity.class);
    //    intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
    //    context.startActivity(intentMain);
    //  }
    //}
    return false;
  }

  @Override public void onThirdPartyPushState(PushType pushType, String action, long resultCode) {
    super.onThirdPartyPushState(pushType, action, resultCode);
    LogUtil.e("push~pushType:", pushType, " | action:", action, " | resultCode:", resultCode);
  }

}
