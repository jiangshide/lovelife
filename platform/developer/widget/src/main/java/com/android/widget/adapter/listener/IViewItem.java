package com.android.widget.adapter.listener;

import com.android.widget.adapter.holder.ViewHolder;

/**
 * created by jiangshide on 2019-10-18.
 * email:18311271399@163.com
 */
public interface IViewItem<T> {

  //布局
  int getItemLayout();

  //是否开启点击
  boolean openClick();

  //是否为当前所需要的布局
  boolean isItemView(T entity, int position);

  //将item控件与数据绑定
  void convert(ViewHolder holder, T entity, int position);
}
