package com.android.files.view.transferee.style.index;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.viewpager.widget.ViewPager;
import com.android.files.view.indicator.CircleIndicator;
import com.android.files.view.transferee.style.IIndexIndicator;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * created by jiangshide on 2020/4/20.
 * email:18311271399@163.com
 */
public class CircleIndexIndicator implements IIndexIndicator {
  private CircleIndicator circleIndicator;

  @Override
  public void attach(FrameLayout parent) {
    FrameLayout.LayoutParams indexLp = new FrameLayout.LayoutParams(WRAP_CONTENT, 48);
    indexLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
    indexLp.bottomMargin = 10;

    circleIndicator = new CircleIndicator(parent.getContext());
    circleIndicator.setGravity(Gravity.CENTER_VERTICAL);
    circleIndicator.setLayoutParams(indexLp);

    parent.addView(circleIndicator);
  }

  @Override
  public void onShow(ViewPager viewPager) {
    if (circleIndicator == null) return;
    circleIndicator.setVisibility(View.VISIBLE);
    circleIndicator.setViewPager(viewPager);
  }

  @Override
  public void onHide() {
    if (circleIndicator == null) return;
    circleIndicator.setVisibility(View.GONE);
  }

  @Override
  public void onRemove() {
    if (circleIndicator == null) return;
    ViewGroup vg = (ViewGroup) circleIndicator.getParent();
    if (vg != null) {
      vg.removeView(circleIndicator);
    }
  }
}
