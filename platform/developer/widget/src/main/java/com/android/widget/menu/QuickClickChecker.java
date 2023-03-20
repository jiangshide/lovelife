package com.android.widget.menu;

/**
 * created by jiangshide on 2020/4/10.
 * email:18311271399@163.com
 */
public class QuickClickChecker {
  private int threshold;
  private long lastClickTime = 0;

  public QuickClickChecker(int threshold) {
    this.threshold = threshold;
  }

  public boolean isQuick() {
    boolean isQuick = System.currentTimeMillis() - lastClickTime <= threshold;
    lastClickTime = System.currentTimeMillis();
    return isQuick;
  }
}
