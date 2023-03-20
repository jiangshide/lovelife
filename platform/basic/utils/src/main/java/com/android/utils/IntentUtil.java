package com.android.utils;

import android.content.Intent;

/**
 * created by jiangshide on 2019-11-28.
 * email:18311271399@163.com
 */
public class IntentUtil extends Intent {

  public IntentUtil setClass(Class _class) {
    this.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    this.setClass(AppUtil.getApplicationContext(), _class);
    return this;
  }

  public void start() {
    AppUtil.getApplicationContext().startActivity(this);
  }
}
