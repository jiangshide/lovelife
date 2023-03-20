package com.android.widget.adapter.listener;

import android.view.View;

/**
 * created by jiangshide on 2019-10-18.
 * email:18311271399@163.com
 */
public interface ItemListener<T> {
  void onItemClick(View view, T entity, int position);
  boolean onItemLongClick(View view, T entity, int position);
}
