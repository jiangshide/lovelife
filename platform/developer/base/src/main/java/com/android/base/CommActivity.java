package com.android.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.android.utils.LogUtil;
import com.android.utils.SystemUtil;
import com.android.widget.ZdTopView;

/**
 * created by jiangshide on 2019-08-05.
 * email:18311271399@163.com
 */
public class CommActivity extends BaseActivity {

  private boolean isAnim = true;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    setContentView(R.layout.comm_activity);

    ZdTopView zdTabTop = findViewById(R.id.commTabTop);
    View defaultStatusBar = findViewById(R.id.defaultStatusBar);
    defaultStatusBar.setBackgroundColor(ContextCompat.getColor(this, R.color.bg));
    defaultStatusBar.getLayoutParams().height = SystemUtil.getStatusBarHeight();
    defaultStatusBar.setVisibility(View.VISIBLE);
    View defaultNavigationBar = findViewById(R.id.defaultNavigationBar);
    defaultNavigationBar.setBackgroundColor(ContextCompat.getColor(this, R.color.bg));
    defaultNavigationBar.getLayoutParams().height = SystemUtil.getNavigationBarHeight();
    defaultNavigationBar.setVisibility(
        SystemUtil.getNavHeight(this) ==0 ? View.GONE : View.VISIBLE);
    if (intent.hasExtra("title")) {
      String title = intent.getStringExtra("title");
      zdTabTop.setTitle(title);
      zdTabTop.setVisibility(View.VISIBLE);
    } else {
      zdTabTop.setVisibility(View.GONE);
    }
    Fragment _clazz = null;
    try {
      _clazz = ((Class<Fragment>) intent.getSerializableExtra("_class")).newInstance();
      _clazz.setArguments(intent.getExtras());
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
    if (isAnim) {
      getSupportFragmentManager().beginTransaction()
          .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out, R.anim.slide_right_in,
              R.anim.slide_right_out)
          .replace(R.id.commonActivity, _clazz)
          .commit();
    } else {
      getSupportFragmentManager().beginTransaction().replace(R.id.commonActivity, _clazz).commit();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    LogUtil.e("----------requestCode:", requestCode, " | requestCode:", requestCode, " | data:",
        data);
  }
}
