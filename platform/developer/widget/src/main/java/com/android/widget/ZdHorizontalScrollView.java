package com.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.android.utils.BubbleUtil;
import com.android.utils.LogUtil;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * created by jiangshide on 2019-12-05.
 * email:18311271399@163.com
 */
public class ZdHorizontalScrollView<T> extends HorizontalScrollView {

  private static final int MAX_X_OVERSCROLL_DISTANCE = 200;
  private Context mContext;
  private int mMaxXOverscrollDistance;
  private OnHScrollListener mListener;
  private List<View> views;
  private int mSelectedIcon;
  private int mUnSelectedIcon;
  private int selectedIndex;

  public ZdHorizontalScrollView(Context context) {
    super(context);
    mContext = context;
    initBounceDistance();
  }

  public ZdHorizontalScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    initBounceDistance();
  }

  public ZdHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mContext = context;
    initBounceDistance();
  }

  private void initBounceDistance() {
    final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
    mMaxXOverscrollDistance = (int) (metrics.density * MAX_X_OVERSCROLL_DISTANCE);
    setOverScrollMode(OVER_SCROLL_NEVER);
  }

  @Override
  protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
      int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
    return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
        mMaxXOverscrollDistance, maxOverScrollY, isTouchEvent);
  }

  public ZdHorizontalScrollView setListener(OnHScrollListener listeener) {
    this.mListener = listeener;
    LogUtil.e("-------mListener:",mListener);
    return this;
  }

  public ZdHorizontalScrollView setSelectedBg(int selectedIcon) {
    this.mSelectedIcon = selectedIcon;
    return this;
  }

  public ZdHorizontalScrollView setUnSelectedBg(int unSelectedIcon) {
    this.mUnSelectedIcon = unSelectedIcon;
    return this;
  }

  public ZdHorizontalScrollView setView(int layout, List<T> datas) {
    if (datas == null) return this;
    int size = datas.size();
    if (views == null) {
      views = new ArrayList<>();
    }
    views.clear();
    LinearLayout linearLayout = new LinearLayout(getContext());
    for (int i = 0; i < size; i++) {
      View view = LayoutInflater.from(getContext()).inflate(layout, null);
      view.setId(i);
      linearLayout.addView(view);
      view.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          if (mListener != null) {
            int index = v.getId();
            //setIndex(v.getId());
            for (int j = 0; j < size; j++) {
              mListener.onSelected(views.get(j), datas.get(j), j, index == j);
            }
          }
        }
      });
      //view.setBackgroundResource(selectedIndex == i ? mSelectedIcon : mUnSelectedIcon);
      views.add(view);
      if (mListener != null) {
        mListener.onSelected(view, datas.get(i), i, selectedIndex == i);
      }
    }
    removeAllViews();
    addView(linearLayout);
    return this;
  }

  public void setIndex(int index) {
    if (views == null) return;
    int size = views.size();
    for (int i = 0; i < size; i++) {
      views.get(i).setBackgroundResource(i == index ? mSelectedIcon : mUnSelectedIcon);
    }
    this.selectedIndex = index;
  }

  public void setBubble() {
    BubbleUtil.elastic(this);
  }

  public void setBubble(int padding) {
    BubbleUtil.elastic(this, padding);
  }

  public interface OnHScrollListener<T> {
    public void onSelected(View view, T t, int index, boolean isSelected);
  }
}
