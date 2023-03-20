package com.android.im.rongcloud.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.android.im.rongcloud.R;
import com.android.utils.LogUtil;

/**
 * Created by Lm on 2018/3/28.
 * Email:1002464056@qq.com
 */

public class ConversationActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.conversation);
    LogUtil.e("---------conversation");
    Toast.makeText(this,"conversation",Toast.LENGTH_LONG).show();
  }
}
