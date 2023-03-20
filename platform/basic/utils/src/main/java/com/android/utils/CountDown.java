package com.android.utils;

import android.os.Handler;

/**
 * created by jiangshide on 2020/4/21.
 * email:18311271399@163.com
 */
public class CountDown {

  private long mDuration;
  private int mInterval = 1000;
  private OnCountDownListener mOnCountDownListener;

  private Handler handler = new Handler();
  private Runnable runnable = new Runnable() {
    @Override public void run() {
      if (mDuration > 0) {
        mDuration -= mInterval;
        if (mOnCountDownListener != null) {
          mOnCountDownListener.onTick(mDuration);
        }
        handler.postDelayed(this, mInterval);
        return;
      }
      stop();
    }
  };

  public CountDown setInterval(int interval) {
    this.mInterval = interval;
    return this;
  }

  public CountDown setTime() {
    return setTime(Integer.MAX_VALUE, mInterval);
  }

  public CountDown setTime(long duration) {
    return setTime(duration, mInterval);
  }

  public CountDown setTime(long duration, int interval) {
    stop();
    this.mDuration = duration;
    this.mInterval = interval;
    return this;
  }

  public CountDown setListener(OnCountDownListener listener) {
    this.mOnCountDownListener = listener;
    return this;
  }

  public CountDown start() {
    LogUtil.e("-----handler:", handler, " | mOnCountDownListener:", mOnCountDownListener);
    if (handler == null) return this;
    if (mDuration == 0) {
      mDuration = Integer.MAX_VALUE;
    }
    handler.postDelayed(runnable, mInterval);
    return this;
  }

  public CountDown pause() {
    if (handler == null) return this;
    handler.removeCallbacks(runnable);
    if (mOnCountDownListener != null) {
      mOnCountDownListener.onPause(mDuration);
    }
    return this;
  }

  public CountDown stop() {
    if (handler == null) return this;
    handler.removeCallbacks(runnable);
    mDuration = 0;
    if (mOnCountDownListener != null) {
      mOnCountDownListener.onFinish();
    }
    return this;
  }

  public interface OnCountDownListener {
    void onTick(long duration);

    void onPause(long duration);

    void onFinish();
  }
}
