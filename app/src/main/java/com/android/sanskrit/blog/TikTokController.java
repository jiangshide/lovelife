package com.android.sanskrit.blog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.player.dplay.controller.BaseVideoController;
import com.android.player.dplay.controller.MediaPlayerControl;
import com.android.player.dplay.player.VideoView;
import com.android.sanskrit.R;
import com.android.utils.CountDown;
import com.android.utils.LogUtil;
import com.android.widget.ZdSeekBar;

/**
 * 抖音
 * Created by xinyu on 2018/1/6.
 */

public class TikTokController extends BaseVideoController<MediaPlayerControl> implements
    CountDown.OnCountDownListener, SeekBar.OnSeekBarChangeListener {

  private ImageView thumb;
  private ImageView mPlayBtn;
  private ZdSeekBar mVideoSeekBar;
  private ProgressBar mVideoProgressBar;
  private CountDown countDown = new CountDown();
  ;

  public TikTokController(@NonNull Context context) {
    super(context);
  }

  public TikTokController(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TikTokController(@NonNull Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected int getLayoutId() {
    return R.layout.layout_tiktok_controller;
  }

  @Override
  protected void initView() {
    super.initView();
    thumb = mControllerView.findViewById(R.id.videoThumb);
    mPlayBtn = mControllerView.findViewById(R.id.videoPlay);
    mVideoSeekBar = mControllerView.findViewById(R.id.videoSeekBar);
    mVideoProgressBar = mControllerView.findViewById(R.id.videoProgressBar);
    mVideoSeekBar.setOnSeekBarChangeListener(this);
    setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        doPauseResume();
      }
    });
  }

  @Override
  public void setPlayState(int playState) {
    super.setPlayState(playState);
    if (countDown != null) {
      countDown.setInterval(50)
          .setListener(this);
    }
    switch (playState) {
      case VideoView.STATE_IDLE:
        LogUtil.e("STATE_IDLE " + hashCode());
        thumb.setVisibility(VISIBLE);
        if (countDown != null) {
          countDown.stop();
        }
        if (mVideoProgressBar != null) {
          mVideoProgressBar.setProgress(0);
          mVideoSeekBar.setVisibility(View.GONE);
        }
        break;
      case VideoView.STATE_PLAYING:
        LogUtil.e("STATE_PLAYING " + hashCode());
        thumb.setVisibility(GONE);
        mPlayBtn.setVisibility(GONE);
        if (countDown != null) {
          countDown.start();
        }
        if (mVideoProgressBar != null) {
          mVideoProgressBar.setVisibility(View.VISIBLE);
          mVideoSeekBar.setVisibility(View.GONE);
        }
        break;
      case VideoView.STATE_PAUSED:
        LogUtil.e("STATE_PAUSED " + hashCode());
        thumb.setVisibility(GONE);
        mPlayBtn.setVisibility(VISIBLE);
        if (countDown != null) {
          countDown.pause();
        }
        if (mVideoProgressBar != null) {
          mVideoProgressBar.setVisibility(View.GONE);
          mVideoSeekBar.setVisibility(View.VISIBLE);
        }
        break;
      case VideoView.STATE_PREPARED:
        LogUtil.e("STATE_PREPARED " + hashCode());
        if (mVideoSeekBar != null) {
          mVideoSeekBar.setVisibility(View.GONE);
        }
        break;
      case VideoView.STATE_ERROR:
        if (countDown != null) {
          countDown.stop();
        }
        if (mVideoProgressBar != null) {
          mVideoProgressBar.setProgress(0);
          mVideoSeekBar.setVisibility(View.GONE);
        }
        LogUtil.e("STATE_ERROR " + hashCode());
        Toast.makeText(getContext(), R.string.dkplayer_error_message, Toast.LENGTH_SHORT).show();
        break;
    }
  }

  @Override
  public boolean showNetWarning() {
    //不显示移动网络播放警告
    return false;
  }

  @Override public void onTick(long duration) {
    if (mVideoSeekBar == null || mMediaPlayer == null) return;
    int position = (int) mMediaPlayer.getCurrentPosition();
    int total = (int) mMediaPlayer.getDuration();
    mVideoSeekBar.setMax(total);
    mVideoSeekBar.setProgress(position);
    mVideoProgressBar.setMax(total);
    mVideoProgressBar.setProgress(position);
  }

  @Override public void onPause(long duration) {
  }

  @Override public void onFinish() {
    if (mVideoSeekBar == null) return;
    mVideoSeekBar.setMax(0);
    mVideoSeekBar.setProgress(0);
    mVideoProgressBar.setMax(0);
    mVideoProgressBar.setProgress(0);
  }

  @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
  }

  @Override public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override public void onStopTrackingTouch(SeekBar seekBar) {
    if (mMediaPlayer == null) return;
    mMediaPlayer.seekTo(seekBar.getProgress());
  }
}
