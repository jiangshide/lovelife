package com.android.sanskrit.blog;

import android.view.View;

/**
 * created by jiangshide on 2020/6/24.
 * email:18311271399@163.com
 */
public interface AutoPlayItem {
  void setActive();
  void deactivate();
  View getAutoplayView();
}
