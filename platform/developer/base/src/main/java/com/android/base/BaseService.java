package com.android.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import com.android.widget.ZdNotification;

/**
 * created by jiangshide on 2019-10-01.
 * email:18311271399@163.com
 */
public class BaseService extends Service {

  @Override public void onCreate() {
    super.onCreate();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    ZdNotification.getInstance()
        .create()
        .setService(this)
        .setTitle("")
        .setContent("")
        .setIsClear(true)
        .setIsRing(true)
        .setLights(true)
        .setIcon(R.drawable.alpha)
        .setVibrate(true)
        .build();
    return START_STICKY;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}
