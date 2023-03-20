package com.android.resource.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.android.resource.R;
import com.android.utils.LogUtil;
import com.android.utils.ScreenUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * created by jiangshide on 2019-11-01.
 * email:18311271399@163.com
 */
public class ZdDialogFragment extends BottomSheetDialogFragment {
  /**
   * 顶部向下偏移量
   */
  private int topOffset = 0;
  private BottomSheetBehavior<FrameLayout> behavior;

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    if (getContext() == null) {
      return super.onCreateDialog(savedInstanceState);
    }
    return new BottomSheetDialog(getContext(), R.style.TransparentBottomSheetStyle);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog().getWindow()
        .getAttributes().windowAnimations = R.style.bottomAnim;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
    init(0.5f,getHeight());

  }

  public void init(float alpha,int height){
    setAlpha(alpha);
    // 设置软键盘不自动弹出
    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
    FrameLayout bottomSheet = dialog.getDelegate().findViewById(R.id.design_bottom_sheet);
    if (bottomSheet != null) {
      CoordinatorLayout.LayoutParams layoutParams =
          (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
      layoutParams.height = height;
      behavior = BottomSheetBehavior.from(bottomSheet);
      // 初始为展开状态
      behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      behavior.addBottomSheetCallback(bottomSheetCallback);
    }
  }

  public void setHeight(int height){
    final View view = getView();
    view.post(new Runnable() {
      @Override
      public void run() {
        View parent = (View) view.getParent();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        behavior = (BottomSheetBehavior) behavior;
        ((BottomSheetBehavior) behavior).addBottomSheetCallback(bottomSheetCallback);

        ((BottomSheetBehavior) behavior).setPeekHeight(height);
        parent.setBackgroundColor(Color.TRANSPARENT);
      }
    });
  }

  private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
    @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {
      LogUtil.e("------newState:",newState);
      if(newState == BottomSheetBehavior.STATE_HIDDEN){
        hidden();
      }
    }

    @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
          LogUtil.e("--------slideOffset:",slideOffset);
    }
  };

  public ZdDialogFragment setAlpha(float alpha) {
    Window window = getDialog().getWindow();
    WindowManager.LayoutParams windowLayoutParams = window.getAttributes();
    windowLayoutParams.dimAmount = alpha;
    window.setAttributes(windowLayoutParams);
    return this;
  }

  public void hidden(){

  }

  /**
   * 获取屏幕高度
   *
   * @return height
   */
  private int getHeight() {
    int height = ScreenUtil.getRtScreenHeight(getContext());
    if (getContext() != null) {
      WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
      Point point = new Point();
      if (wm != null) {
        wm.getDefaultDisplay().getSize(point);
        height = point.y - getTopOffset();
      }
    }
    return height;
  }

  public int getTopOffset() {
    return topOffset;
  }

  public void setTopOffset(int topOffset) {
    this.topOffset = topOffset;
  }

  public BottomSheetBehavior<FrameLayout> getBehavior() {
    return behavior;
  }

  protected View getView(int layout) {
    return LayoutInflater.from(getContext()).inflate(layout, null);
  }
}
