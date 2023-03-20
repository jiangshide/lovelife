package com.android.sanskrit.blog;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.utils.ScreenUtil;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * created by jiangshide on 2020/6/24.
 * email:18311271399@163.com
 */
public class AutoPlayTool {
  private AutoPlayItem mHolder;
  private int visiblePercent = 60;
  public static int MODE_PLAY_FIRST = 0;
  public static int MODE_PLAY_CENTER = 1;
  private int mode = MODE_PLAY_FIRST;

  public AutoPlayTool() {

  }

  public AutoPlayTool(int visiblePercent) {
    this.visiblePercent = visiblePercent;
  }

  public AutoPlayTool(int visiblePercent, int mode) {
    this.visiblePercent = visiblePercent;
    this.mode = mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  /**
   * 当滑动停止的时候，开始视频播放
   */
  public int onActiveWhenNoScrolling(RecyclerView recyclerView) {
    LinearLayoutManager layoutManager = null;
    if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
      layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }
    if (layoutManager != null) {
      int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
      int lastItemPosition = layoutManager.findLastVisibleItemPosition();
      LinkedHashMap<Integer, AutoPlayItem> items = new LinkedHashMap();
      while (firstItemPosition <= lastItemPosition) {
        RecyclerView.ViewHolder holder =
            recyclerView.findViewHolderForLayoutPosition(firstItemPosition);

        if (holder instanceof AutoPlayItem) {
          View view = (((AutoPlayItem) holder)).getAutoplayView();
          if (view != null && getVisible(view, visiblePercent)) {
            if (mode == MODE_PLAY_FIRST) {//优先播放第一个的情况
              ((AutoPlayItem) holder).setActive();
              mHolder = ((AutoPlayItem) holder);
              return firstItemPosition;
            }
            items.put(firstItemPosition, ((AutoPlayItem) holder));
          }
        }
        firstItemPosition++;
      }
      //下面的逻辑是播放靠中间的视频
      int d = Integer.MAX_VALUE;
      AutoPlayItem findHolder = null;
      int position = -1;
      //找出距离中间最近的一个
      for (Map.Entry<Integer, AutoPlayItem> entry : items.entrySet()) {
        int d2 = getDistanceFromCenter(entry.getValue().getAutoplayView());
        if (d2 < d) {
          findHolder = entry.getValue();
          d = d2;
          position = entry.getKey();
        }
      }
      if (mHolder != findHolder) {
        if (mHolder != null) {
          mHolder.deactivate();
        }
        mHolder = findHolder;
      }
      if (mHolder != null) {
        mHolder.setActive();
        return position;
      }
    }
    return -1;
  }

  //当视频画出屏幕时停止播放
  public void onScrolledAndDeactivate(RecyclerView recyclerView) {
    if (mHolder != null && mHolder.getAutoplayView() != null && !getVisible(
        mHolder.getAutoplayView(), visiblePercent)) {
      mHolder.deactivate();
    }
  }

  /**
   * 用于停止滑出去的视频
   */
  public void onScrolledAndDeactivate() {
    if (mHolder != null && mHolder.getAutoplayView() != null && !getVisible(
        mHolder.getAutoplayView(), visiblePercent)) {
      mHolder.deactivate();
    }
  }

  public void setVisiblePercent(int visiblePercent) {
    this.visiblePercent = visiblePercent;
  }

  private int getVisiblePercent(View v) {
    Rect r = new Rect();
    boolean visible = v.getLocalVisibleRect(r);
    if (visible && v.getMeasuredHeight() > 0) {
      int percent = 100 * r.height() / v.getMeasuredHeight();
      return percent;
    }
    return -1;
  }

  private boolean getVisible(View v, int value) {
    Rect r = new Rect();
    boolean visible = v.getLocalVisibleRect(r);
    if (visible && v.getVisibility() == View.VISIBLE) {
      if (getVisiblePercent(v) >= value) {
        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  private int getDistanceFromCenter(View view) {
    int centerHeight = (int) (ScreenUtil.getRtScreenHeight(view.getContext()) / 2.3);//中间线靠上一点，
    //项目代码原因，可以写getResources().getDisplayMetrics().heightPixels;
    int[] viewLocation = new int[2];
    view.getLocationOnScreen(viewLocation);
    return Math.abs(viewLocation[1] + view.getHeight() / 2 - centerHeight);
  }
}