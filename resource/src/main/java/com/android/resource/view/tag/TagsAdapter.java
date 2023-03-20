package com.android.resource.view.tag;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * created by jiangshide on 2020/4/3.
 * email:18311271399@163.com
 */
public abstract class TagsAdapter {
  private OnDataSetChangeListener onDataSetChangeListener;

  public abstract int getCount();
  public abstract View getView(Context context, int position, ViewGroup parent);
  public abstract Object getItem(int position);
  public abstract int getPopularity(int position);
  public abstract void onThemeColorChanged(View view, int themeColor, float alpha);

  public final void notifyDataSetChanged() {
    onDataSetChangeListener.onChange();
  }

  protected interface OnDataSetChangeListener{
    void onChange();
  }

  protected void setOnDataSetChangeListener(OnDataSetChangeListener listener) {
    onDataSetChangeListener = listener;
  }
}
