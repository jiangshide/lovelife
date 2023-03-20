package com.android.im.rongcloud.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.im.rongcloud.R;
import com.android.im.rongcloud.fragment.FriendFragment;
import com.android.utils.LogUtil;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lm on 2018/3/29.
 * Email:1002464056@qq.com
 */

public class HomeActivity extends FragmentActivity {

  private ViewPager mViewPager;
  private FragmentPagerAdapter mFragmentPagerAdapter;//将tab页面持久在内存中
  private Fragment mConversationList;
  private Fragment mConversationFragment = null;
  private List<Fragment> mFragment = new ArrayList<>();

  private ImageView iv_back_left;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    // StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.actionbar_bg));
    LogUtil.e("---------activity_home");
    Toast.makeText(this,"activity_home",Toast.LENGTH_LONG).show();
    mConversationList = initConversationList();//获取融云会话列表的对象
    mViewPager = (ViewPager) findViewById(R.id.viewpager);
    iv_back_left = (ImageView) findViewById(R.id.iv_back_left);
    iv_back_left.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });

    mFragment.add(mConversationList);//加入会话列表
    mFragment.add(FriendFragment.getInstance());
    //配置ViewPager的适配器
    mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        return mFragment.get(position);
      }

      @Override
      public int getCount() {
        return mFragment.size();
      }
    };
    mViewPager.setAdapter(mFragmentPagerAdapter);
  }

  private Fragment initConversationList() {
    /**
     * appendQueryParameter对具体的会话列表做展示
     */
    if (mConversationFragment == null) {
      ConversationListFragment listFragment = new ConversationListFragment();
      Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
          .appendPath("conversationList")
          .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(),
              "false")//设置私聊会话是否聚合显示
          .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
          // .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
          //.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//公共服务号
          .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(),
              "false")//设置私聊会话是否聚合显示
          .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(),
              "false")//设置私聊会是否聚合显示
          .build();
      listFragment.setUri(uri);
      return listFragment;
    } else {
      return mConversationFragment;
    }
  }
}


