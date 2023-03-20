package com.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * created by jiangshide on 2019-10-18.
 * email:18311271399@163.com
 */
public class ZdExceptionTips extends FrameLayout {

  private View loadingView;
  private View failView;

  public ZdExceptionTips(@NonNull Context context) {
    super(context);
    defaultView();
  }

  public ZdExceptionTips(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    defaultView();
  }

  public ZdExceptionTips(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    defaultView();
  }

  private void defaultView() {
    loadingView = getView(R.layout.default_loading);
    loadingView.setVisibility(View.GONE);
    failView = getView(R.layout.default_fail);
    failView.setVisibility(View.GONE);
    this.addView(loadingView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    this.addView(failView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  private View getView(int layout) {
    return LayoutInflater.from(getContext()).inflate(layout, null);
  }

  public void setLoadingView(int layout) {
    this.removeView(loadingView);
    loadingView = getView(layout);
    this.addView(loadingView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  public void setFailView(int layout) {
    this.removeView(failView);
    failView = getView(layout);
    this.addView(failView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  public void loading() {
    showLoading(true);
    showFail(false);
  }

  public ZdExceptionTips fail() {
    showLoading(false);
    showFail(true);
    return this;
  }

  public ZdExceptionTips setListener(OnClickListener listener) {
    if (failView != null) {
      failView.setOnClickListener(listener);
    }
    return this;
  }

  public void finish() {
    showLoading(false);
    showFail(false);
  }

  public void showLoading(boolean isLoading) {
    if (loadingView == null) return;
    loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
  }

  public void showFail(boolean isFail) {
    if (failView == null) return;
    failView.setVisibility(isFail ? View.VISIBLE : View.GONE);
  }
}
