package com.android.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Keep;

/**
 * created by jiangshide on 2019-10-01.
 * email:18311271399@163.com
 */
public class BaseReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {
    Intent serviceIntent = new Intent(context, BaseService.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(serviceIntent);
    } else {
      context.startService(serviceIntent);
    }
  }
}
