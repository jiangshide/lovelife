package com.android.player.audio;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import androidx.annotation.RequiresApi;
import com.android.utils.AppUtil;
import com.android.utils.FileUtil;
import com.android.utils.LogUtil;
import com.android.utils.SPUtil;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * created by jiangshide on 2019-10-25.
 * email:18311271399@163.com
 */
public class AudioRecorder {
  private static final int BASE = 2;
  private static final int SPACE = 50;// 间隔取样时间
  //public static final int MAX_RECORD_LENGTH = 1000 * 6;
  public static final int MAX_RECORD_LENGTH = 1000 * 60 * 10;
  public static final String RECODER_AUDIO_DURATION="recoderAudioDuration";

  private String mSoundFilePath;
  private MediaRecorder mRecorder;
  private OnAudioStatusUpdateListener audioStatusUpdateListener;

  private long startTime;
  private long duration;
  public boolean isAudioRecording;
  public boolean isRecordPauseing;

  private double max = 0;
  private double min = 0;

  private final Handler mHandler = new Handler();
  private Runnable mUpdateMicRunnable = () -> updateMicStatus();

  public void start() {
    start(FileUtil.getAudioPath(AppUtil.getAppName()), SPUtil.getInt(RECODER_AUDIO_DURATION,MAX_RECORD_LENGTH));
  }

  public void start(String name) {
    start(TextUtils.isEmpty(name)?FileUtil.getAudioPath():FileUtil.getAudioPath(name), SPUtil.getInt(RECODER_AUDIO_DURATION,MAX_RECORD_LENGTH));
  }

  /**
   * 开始录制
   *
   * @param maxDuration 毫秒
   */
  public void start(String path, int maxDuration) {
    if (isAudioRecording) return;
      if (mRecorder == null) mRecorder = new MediaRecorder();

    try {
      mSoundFilePath = path;
      mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
      mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

      //mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
      //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

      //mRecorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
      mRecorder.setAudioEncodingBitRate(44100);
      //mRecorder.setAudioEncodingBitRate(16);

      mRecorder.setAudioSamplingRate(16000);
      //mRecorder.setAudioSamplingRate(44100);

      mRecorder.setOutputFile(mSoundFilePath);
      mRecorder.setMaxDuration(maxDuration);
      mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
        @Override public void onInfo(MediaRecorder mr, int what, int extra) {
          if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            stopRecord();
          }
        }
      });

      mRecorder.prepare();
      mRecorder.start();
      updateMicStatus();

      startTime = System.currentTimeMillis();
      duration = 0;
      isAudioRecording = true;
    } catch (Exception e) {
      e.printStackTrace();
      mRecorder.reset();
      mRecorder.release();
      mRecorder = null;
      LogUtil.e(e);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.N) public void pauseRecord() {
    if (mRecorder == null) return;

    try {
      isRecordPauseing = true;
      mRecorder.pause();
      duration = duration + System.currentTimeMillis() - startTime;
      mHandler.removeCallbacks(mUpdateMicRunnable);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.N) public void resumeRecord() {
    if (mRecorder == null) return;

    try {
      isRecordPauseing = false;
      mRecorder.resume();
      startTime = System.currentTimeMillis();
      updateMicStatus();
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
  }

  public long stopRecord() {
    if (!isRecordPauseing) {
      duration = duration + System.currentTimeMillis() - startTime;
    }

    isAudioRecording = false;
    isRecordPauseing = false;
    if (mRecorder == null) return 0L;
    try {
      mRecorder.stop();
      mRecorder.reset();
      mRecorder.release();
      mRecorder = null;

      if (null != audioStatusUpdateListener) {
        audioStatusUpdateListener.onRecordStop(duration, mSoundFilePath);
      }
      mSoundFilePath = "";
    } catch (RuntimeException e) {
      e.printStackTrace();
      mRecorder.reset();
      mRecorder.release();

      File file = new File(mSoundFilePath);
      if (file.exists()) file.delete();
      duration = 0;
    } finally {
      mSoundFilePath = "";
      mRecorder = null;
    }

    return duration;
  }

  public void cancelRecord() {
    isAudioRecording = false;
    isRecordPauseing = false;
    if (mRecorder == null) return;
    try {
      mRecorder.stop();
      mRecorder.reset();
      mRecorder.release();
      mRecorder = null;
    } catch (RuntimeException e) {
      mRecorder.reset();
      mRecorder.release();
      mRecorder = null;
    }

    File file = new File(mSoundFilePath);
    if (file.exists()) file.delete();
    mSoundFilePath = "";
  }

  public boolean isRecordPauseing() {
    return isRecordPauseing;
  }

  public byte[] getDate() {
    if (TextUtils.isEmpty(mSoundFilePath)) return null;
    try {
      return readFile(new File(mSoundFilePath));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static byte[] readFile(File file) throws IOException {
    RandomAccessFile f = new RandomAccessFile(file, "r");
    try {
      long longlength = f.length();
      int length = (int) longlength;
      if (length != longlength) {
        throw new IOException("File size >= 2 GB");
      }
      // Read file and return data
      byte[] data = new byte[length];
      f.readFully(data);
      return data;
    } finally {
      f.close();
    }
  }

  private void updateMicStatus() {
    if (mRecorder != null) {
      int maxAmplitude = mRecorder.getMaxAmplitude();
      double ratio = (double) maxAmplitude / BASE;
      double db;// 分贝
      if (ratio > 1) {
        db = 20 * Math.log10(ratio);
        if (null != audioStatusUpdateListener) {
          audioStatusUpdateListener.onRecording(db, wave(db),
              duration + System.currentTimeMillis() - startTime);
        }
      } else {
        if (null != audioStatusUpdateListener) {
          audioStatusUpdateListener.onRecording(0, wave(0d),
              duration + System.currentTimeMillis() - startTime);
        }
      }
      mHandler.postDelayed(mUpdateMicRunnable, SPACE);
    }
  }

  public void setOnAudioStatusUpdateListener(
      OnAudioStatusUpdateListener audioStatusUpdateListener) {
    this.audioStatusUpdateListener = audioStatusUpdateListener;
  }

  public int wave(Double db) {
    if (max == 0 || min == 0) {
      max = db;
      min = db;
    }
    if (db < min) {
      min = db;
    }
    if (db > max) {
      max = db;
    }
    if (max == min) {
      return 0;
    } else {
      double d = (db - min) / (max - min);
      return (int) (255 * d);
    }
  }

  public interface OnAudioStatusUpdateListener {
    /**
     * 录音中...
     *
     * @param db 当前声音分贝
     * @param time 录音时长
     */
    void onRecording(double db, int d, long time);

    /**
     * 停止录音
     *
     * @param filePath 保存路径
     */
    void onRecordStop(long duration, String filePath);
  }
}
