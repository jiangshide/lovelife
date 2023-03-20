package com.android.files.view.transferee.style.index;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.viewpager.widget.ViewPager;
import com.android.files.view.indicator.NumberIndicator;
import com.android.files.view.transferee.style.IIndexIndicator;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * created by jiangshide on 2020/4/20.
 * email:18311271399@163.com
 */
public class NumberIndexIndicator implements IIndexIndicator {
  private NumberIndicator numberIndicator;

  @Override
  public void attach(FrameLayout parent) {
    FrameLayout.LayoutParams indexLp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    indexLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
    indexLp.topMargin = 30;

    numberIndicator = new NumberIndicator(parent.getContext());
    numberIndicator.setLayoutParams(indexLp);

    parent.addView(numberIndicator);
  }

  @Override
  public void onShow(ViewPager viewPager) {
    if(numberIndicator == null) return;
    numberIndicator.setVisibility(View.VISIBLE);
    numberIndicator.setViewPager(viewPager);
  }

  @Override
  public void onHide() {
    if (numberIndicator == null) return;
    numberIndicator.setVisibility(View.GONE);
  }

  @Override
  public void onRemove() {
    if (numberIndicator == null) return;
    ViewGroup vg = (ViewGroup) numberIndicator.getParent();
    if (vg != null) {
      vg.removeView(numberIndicator);
    }
  }
}
