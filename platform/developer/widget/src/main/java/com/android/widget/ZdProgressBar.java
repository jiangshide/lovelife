package com.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import com.android.utils.Constant;
import com.android.utils.SPUtil;

/**
 * created by jiangshide on 2020/6/4.
 * email:18311271399@163.com
 */
public class ZdProgressBar extends ProgressBar {

  private int progressDrawable;

  public ZdProgressBar(Context context) {
    super(context);
    init();
  }

  public ZdProgressBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ZdProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    progressDrawable = R.drawable.seek_progress;
    progressDrawable = SPUtil.getInt(Constant.APP_PROGRESS_DRAWABLE, progressDrawable);
    setProgressDrawable(getContext().getResources().getDrawable(progressDrawable));
  }
}
