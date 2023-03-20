package com.android.widget.adapter.listener;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import com.android.widget.adapter.ZdAdapter;

/**
 * created by jiangshide on 2019-10-18.
 * email:18311271399@163.com
 */
public interface IViewCreate<T> {

  /**
   *
   */
  Context context();

  /**
   * 创建RecycleView
   */
  RecyclerView createRecyclerView();

  /**
   * 创建RecycleView.Adapter
   */
  ZdAdapter<T> createRecycleViewAdapter();

}
