package com.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.android.utils.Constant;
import com.android.utils.SPUtil;
import com.google.android.material.appbar.AppBarLayout;

/**
 * created by jiangshide on 2020/6/4.
 * email:18311271399@163.com
 */
public class ZdAppBarLayout extends AppBarLayout {
  public ZdAppBarLayout(Context context) {
    super(context);
    init();
  }

  public ZdAppBarLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init(){
    //setBackgroundColor(getResources().getColor(SPUtil.getInt(Constant.APP_COLOR_BG,R.color.bg)));
  }
}
