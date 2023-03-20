package com.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.android.utils.Constant;
import com.android.utils.SPUtil;

/**
 * created by jiangshide on 2020/6/4.
 * email:18311271399@163.com
 */
public class ZdSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

  private int progressDrawable;

  public ZdSeekBar(Context context) {
    super(context);
  }

  public ZdSeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ZdSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init(){
    progressDrawable = R.drawable.seek_progress;
    progressDrawable = SPUtil.getInt(Constant.APP_PROGRESS_DRAWABLE, progressDrawable);
    setProgressDrawable(getContext().getResources().getDrawable(progressDrawable));
  }

  public void setThumb(int thumb){
    setThumb(getContext().getResources().getDrawable(thumb));
  }
}
