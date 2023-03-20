package com.android.player.audioeffects;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import com.android.player.audioeffects.utils.ByteUtils;
import com.android.player.audioeffects.utils.FrequencyScanner;
import com.android.utils.LogUtil;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * created by jiangshide on 2020/4/20.
 * email:18311271399@163.com
 */
public class PcmChunkPlayer {
  private static final String MARK_PCM = ".pcm";
  private static final int DEFAULT_SAMPLE_RATE = 16000;
  private static final int DEFAULT_CHANNEL_COUNT = 1;

  private static final int VAD_THRESHOLD = 8;

  private static PcmChunkPlayer instance;

  private AudioTrack player;
  private PcmChunkPlayerThread pcmChunkPlayerThread;

  private int sampleRate = DEFAULT_SAMPLE_RATE;
  /**
   * 单声道
   */
  private int sampleBitConfig = AudioFormat.CHANNEL_OUT_MONO;
  private int channelCountConfig = AudioFormat.ENCODING_PCM_16BIT;

  /**
   * 是否开启静音跳过功能
   */
  private volatile boolean isVad = false;

  public static PcmChunkPlayer getInstance() {
    if (instance == null) {
      synchronized (PcmChunkPlayer.class) {
        if (instance == null) {
          instance = new PcmChunkPlayer();
        }
      }
    }
    return instance;
  }

  private PcmChunkPlayer() {

  }

  public synchronized void setVad(boolean vad) {
    isVad = vad;
  }

  /**
   * 设置Pcm的音频格式
   */
  public void setFormat(int sampleRate, int sampleBit, int channelCount) {
    this.sampleRate = sampleRate;

    if (sampleBit == 16) {
      this.sampleBitConfig = AudioFormat.ENCODING_PCM_16BIT;
    } else if (sampleBit == 8) {
      this.sampleBitConfig = AudioFormat.ENCODING_PCM_8BIT;
    } else {
      this.sampleBitConfig = AudioFormat.ENCODING_PCM_FLOAT;
    }

    if (channelCount == 1) {
      this.channelCountConfig = AudioFormat.CHANNEL_OUT_MONO;
    } else if (channelCount == 2) {
      this.channelCountConfig = AudioFormat.CHANNEL_OUT_STEREO;
    } else {
      LogUtil.e( "不支持该声道数：%s", channelCount);
    }
  }

  public void init(boolean vad, PcmChunkPlayerListener pcmChunkPlayerListener) {
    release();
    init();
    setVad(vad);
    setPcmChunkPlayerListener(pcmChunkPlayerListener);
  }

  private void init() {
    LogUtil.d( "音频：sampleRate: %s ，sampleBitConfig：%s,channelCountConfig：%s ", sampleRate, sampleBitConfig, channelCountConfig);
    int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRate,
        channelCountConfig, sampleBitConfig);
    player = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
        channelCountConfig, sampleBitConfig,
        bufferSizeInBytes, AudioTrack.MODE_STREAM);

    player.play();
    pcmChunkPlayerThread = new PcmChunkPlayerThread();
    pcmChunkPlayerThread.start();
  }

  /**
   * 播放PCM
   *
   * @param pcmFile PCM文件
   */
  public void putFileDataAsync(final File pcmFile) {
    new Thread() {
      @Override
      public void run() {
        if (pcmFile == null || !pcmFile.exists()) {
          LogUtil.e( "pcmFile is null");
          return;
        }

        if (!pcmFile.getName().endsWith(MARK_PCM)) {
          LogUtil.e( "这不是一个PCM文件");
          return;
        }

        int bufferSize = 2048;
        try (FileInputStream dis = new FileInputStream(pcmFile)) {
          int len = -1;
          byte[] data = new byte[bufferSize];
          while ((len = dis.read(data)) != -1) {
            putPcmData(data, len);
          }
          over();
        } catch (Exception e) {
          LogUtil.e( e.getMessage());
        }
      }
    }.start();
  }

  public void putPcmData(byte[] chunk, int size) {
    if (pcmChunkPlayerThread == null) {
      LogUtil.w( "pcmChunkPlayerThread is null");
      return;
    }
    pcmChunkPlayerThread.addChangeBuffer(new ChangeBuffer(chunk, size, sampleRate));
  }

  public void over() {
    if (pcmChunkPlayerThread == null) {
      LogUtil.w( "pcmChunkPlayerThread is null");
      return;
    }
    pcmChunkPlayerThread.stopSafe();
  }

  private PcmChunkPlayerListener pcmChunkPlayerListener;

  public void setPcmChunkPlayerListener(PcmChunkPlayerListener pcmChunkPlayerListener) {
    this.pcmChunkPlayerListener = pcmChunkPlayerListener;
  }

  public void release() {
    if (pcmChunkPlayerThread != null) {
      pcmChunkPlayerThread.stopNow();
      pcmChunkPlayerThread = null;
    }

    if (player != null) {
      player.release();
      player = null;
    }
  }

  /**
   * PCM数据调度器
   */
  public class PcmChunkPlayerThread extends Thread {

    /**
     * PCM 数据缓冲队列
     */
    private List<ChangeBuffer>
        cacheBufferList = Collections.synchronizedList(new LinkedList<ChangeBuffer>());

    /**
     * 是否已停止
     */
    private volatile boolean isOver = false;

    /**
     * 是否继续轮询数据队列
     */
    private volatile boolean start = true;

    public void addChangeBuffer(ChangeBuffer changeBuffer) {
      if (changeBuffer != null) {
        cacheBufferList.add(changeBuffer);
        synchronized (this) {
          notify();
        }
      }
    }

    public void stopSafe() {
      isOver = true;
      synchronized (this) {
        notify();
      }
    }

    public void stopNow() {
      isOver = true;
      start = false;
      synchronized (this) {
        notify();
      }
    }

    private ChangeBuffer next() {
      for (; ; ) {
        if (cacheBufferList == null || cacheBufferList.size() == 0) {
          try {
            if (isOver) {
              finish();
              return null;
            }
            synchronized (this) {
              wait();
            }
          } catch (Exception e) {
            LogUtil.e( e.getMessage());
          }
        } else {
          return cacheBufferList.remove(0);
        }
      }
    }

    private void finish() {
      start = false;
      if (pcmChunkPlayerListener != null) {
        pcmChunkPlayerListener.onFinish();
      }
    }

    @Override
    public void run() {
      super.run();

      while (start) {
        play(next());
      }
    }

    long readSize = 0L;

    private void play(ChangeBuffer chunk) {
      if (chunk == null) {
        return;
      }
      readSize += chunk.getSize();

      if (!isVad || chunk.getVoiceSize() > VAD_THRESHOLD) {
        if (pcmChunkPlayerListener != null) {
          pcmChunkPlayerListener.onPlayData(chunk.getRawData());
          pcmChunkPlayerListener.onPlaySize(readSize);
        }
        if (player != null && start) {
          player.write(chunk.getRawData(), 0, chunk.getSize());
        }
      }
    }
  }

  private static FrequencyScanner fftScanner = new FrequencyScanner();

  public static class ChangeBuffer {

    private byte[] rawData;

    private int size;
    private int voiceSize;

    public ChangeBuffer(byte[] rawData, int size, int sampleRate) {
      this.rawData = rawData.clone();
      this.size = size;
      this.voiceSize = (int) fftScanner.getMaxFrequency(ByteUtils.toShorts(rawData), sampleRate);
    }

    public byte[] getRawData() {
      return rawData;
    }

    public int getSize() {
      return size;
    }

    public int getVoiceSize() {
      return voiceSize;
    }
  }


  public interface PcmChunkPlayerListener {
    /**
     * 播放完成
     */
    void onFinish();

    /**
     * 已播放的数据量
     */
    void onPlaySize(long size);

    /**
     * 播放的数据
     */
    void onPlayData(byte[] size);
  }

}
