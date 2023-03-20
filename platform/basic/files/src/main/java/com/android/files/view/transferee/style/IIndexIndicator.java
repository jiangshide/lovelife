package com.android.files.view.transferee.style;

import android.widget.FrameLayout;
import androidx.viewpager.widget.ViewPager;

/**
 * created by jiangshide on 2020/4/20.
 * email:18311271399@163.com
 */
public interface IIndexIndicator {
  /**
   * 在父容器上添加一个图片索引指示器 UI 组件
   *
   * @param parent TransferImage
   */
  void attach(FrameLayout parent);

  /**
   * 显示图片索引指示器 UI 组件
   *
   * @param viewPager TransferImage
   */
  void onShow(ViewPager viewPager);

  /**
   * 隐藏图片索引指示器 UI 组件
   */
  void onHide();

  /**
   * 移除图片索引指示器 UI 组件
   */
  void onRemove();
}
