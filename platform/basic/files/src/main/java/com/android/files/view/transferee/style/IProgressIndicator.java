package com.android.files.view.transferee.style;

import android.widget.FrameLayout;

/**
 * created by jiangshide on 2020/4/20.
 * email:18311271399@163.com
 */
public interface IProgressIndicator {
  /**
   * 在父容器上附加一个图片加载进度 UI 控件
   *
   * @param position 当前图片的索引
   * @param parent   父容器
   */
  void attach(int position, FrameLayout parent);

  /**
   * 图片加载进度 UI 控件初始化
   *
   * @param position 索引下标
   */
  void onStart(int position);

  /**
   * 图片加载进度 UI 控件显示对应的进度
   *
   * @param position 索引下标
   * @param progress 进度值(0 - 100)
   */
  void onProgress(int position, int progress);

  /**
   * 隐藏 position 索引位置的图片加载进度 UI 控件
   *
   * @param position 索引下标
   */
  void hideView(int position);

  /**
   * 图片加载完成, 移除图片加载进度 UI 控件
   *
   * @param position 索引下标
   */
  void onFinish(int position);
}
