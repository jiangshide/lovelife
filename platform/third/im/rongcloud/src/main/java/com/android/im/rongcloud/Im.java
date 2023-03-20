package com.android.im.rongcloud;

import android.app.Activity;
import androidx.fragment.app.FragmentManager;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * created by jiangshide on 2020-01-14.
 * email:18311271399@163.com
 */
public class Im {

  public static String IM = "im";

  /**
   * 启动会话列表界面。
   */
  public static void conversationList(Activity activity) {
    //RongIM.getInstance().startConversationList(activity, supportedConversation);
  }

  /**
   * test:test11000
   * 启动会话界面。
   */
  public static void conversation(Activity activity, String targetId, String title) {
    RongIM.getInstance()
        .startConversation(activity, Conversation.ConversationType.PRIVATE, targetId, title);
  }

  /**
   * test:test11000
   * 启动单聊
   */
  public static void chat(Activity activity, String targetUserId, String title) {
    RongIM.getInstance().startPrivateChat(activity, targetUserId, title);
  }

  /**
   * test:test11000
   * 启动群聊
   * 客户端的所有群组操作都需要请求 App Server， App Server 可以根据自己的逻辑进行管理和控制，然后通过 Server API 接口进行群组操作，并将结果返回给客户端
   */
  public static void groupChat(Activity activity, String targetGroupId, String title) {
    RongIM.getInstance().startGroupChat(activity, targetGroupId, title);
  }

  /**
   * test:test11000
   * 启动系统会话聊天界面
   * 备注:系统会话消息由应由服务端发送
   */
  public static void conversationSystem(Activity activity,String targetId,String title) {
    RongIM.getInstance()
        .startConversation(activity, Conversation.ConversationType.SYSTEM, targetId, title);
  }

  /**
   * 会话列表
   */
  public void conversationList(Activity activity, FragmentManager fragmentManager) {
    //ConversationListFragment mConversationListFragment = new ConversationListFragment();
    //Uri uri = Uri.parse("rong://" + activity.getApplicationInfo().packageName).buildUpon()
    //    .appendPath("conversationlist")
    //    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")
    //    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
    //    .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")
    //    .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")
    //    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")
    //    .build();
    //mConversationListFragment.setUri(uri);
    //FragmentTransaction transaction = fragmentManager.beginTransaction();
    //transaction.replace(R.id.relativeLayout_main, mConversationListFragment);
    //transaction.commit();
  }

  /**
   * 会话
   */
  public void conversation(Activity activity, String mTargetId, FragmentManager fragmentManager) {
    //ConversationFragment mConversationFragment = new ConversationFragment();
    //Uri uri = Uri.parse("rong://" + activity.getApplicationInfo().packageName).buildUpon()
    //    .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
    //    .appendQueryParameter("targetId", mTargetId).build();
    //mConversationFragment.setUri(uri);
    //FragmentTransaction transaction = fragmentManager.beginTransaction();
    //transaction.replace(R.id.relativeLayout_main, mConversationFragment);
    //transaction.commit(); // 提交创建Fragment请求
  }
}
