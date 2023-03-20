package com.android.im.rongcloud.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.android.im.rongcloud.R;
import com.android.utils.LogUtil;

/**
 * Created by Lm on 2018/3/27.
 * Email:1002464056@qq.com
 */

public class ConversationListActivity extends FragmentActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.conversationlist);
    LogUtil.e("---------conversationlist");
    Toast.makeText(this,"conversationlist",Toast.LENGTH_LONG).show();
  }
}
