//package com.android.player.audioeffects;
//
//import android.content.Context;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import com.android.player.audioeffects.decoder.FFmpegMp3Decoder;
//import com.android.player.audioeffects.utils.ByteUtils;
//import com.android.player.audioeffects.utils.FrequencyScanner;
//import com.android.utils.LogUtil;
//import java.io.File;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * created by jiangshide on 2020/4/20.
// * email:18311271399@163.com
// */
//public class FFmpegMp3Player {
//  private static FFmpegMp3Player instance;
//  private PcmChunkPlayer pcmChunkPlayer;
//  private ExecutorService threadPool;
//  private PlayInfoListener infoListener;
//  private FrequencyScanner frequencyScanner = new FrequencyScanner();
//  /**
//   * 缓冲文件，用于放置解码后的PCM文件
//   */
//  private File cacheFile = new File("sdcard/Record/result.pcm");
//
//  private boolean isPreparing = true;
//  private int raw;
//  private File srcFile = new File("sdcard/Record/test.mp3");
//
//  public static FFmpegMp3Player getInstance() {
//    if (instance == null) {
//      synchronized (FFmpegMp3Player.class) {
//        if (instance == null) {
//          instance = new FFmpegMp3Player();
//        }
//      }
//    }
//    return instance;
//  }
//
//  private FFmpegMp3Player() {
//    threadPool = new ThreadPoolExecutor(1, 1, 10000L,
//        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
//        new ThreadFactory() {
//          @Override
//          public Thread newThread(@NonNull Runnable runnable) {
//            return new Thread(runnable, "Mp3Player");
//          }
//        });
//  }
//
//  public static FFmpegMp3Player create(int raw) {
//    FFmpegMp3Player mp3Player = new FFmpegMp3Player();
//    mp3Player.raw = raw;
//    mp3Player.release();
//    mp3Player.pcmChunkPlayer = PcmChunkPlayer.getInstance();
//    return mp3Player;
//  }
//
//  public void setPlayInfoListener(PlayInfoListener playInfoListener) {
//    this.infoListener = playInfoListener;
//  }
//
//  /**
//   * Ffmpage 转换耗时
//   */
//  long time;
//
//  public void prepare(Context context) {
//    time = System.currentTimeMillis();
//    FFmpegMp3Decoder newMp3Decoder = new FFmpegMp3Decoder(context);
//    newMp3Decoder.mp3ToPcm(srcFile, cacheFile, new FFmpegMp3Decoder.ResultCallback() {
//      @Override
//      public void onSuccess() {
//        LogUtil.i( "Ffmpage 转换耗时： %s ms", (System.currentTimeMillis() - time));
//        isPreparing = false;
//        if (infoListener != null) {
//          infoListener.onDecodeFinish(cacheFile);
//        }
//      }
//
//      @Override
//      public void onFailure() {
//
//      }
//    });
//    initPcmChunkPlayer(false, 16000, 16, 1);
//  }
//
//  public void play(Context context, final boolean vad) {
//    if (isPreparing) {
//      LogUtil.w( "正在初始化文件");
//      Toast.makeText(context, "正在初始化文件", Toast.LENGTH_LONG).show();
//      return;
//    }
//    pcmChunkPlayer.putFileDataAsync(cacheFile);
//  }
//
//  private void initPcmChunkPlayer(boolean vad, final int sampleRate, int sampleBit, int channelCount) {
//    pcmChunkPlayer.setFormat(sampleRate, sampleBit, channelCount);
//    pcmChunkPlayer.init(vad, new PcmChunkPlayer.PcmChunkPlayerListener() {
//      @Override
//      public void onFinish() {
//      }
//
//      @Override
//      public void onPlaySize(long size) {
//        infoListener.onPlaySize(size);
//      }
//
//      @Override
//      public void onPlayData(byte[] size) {
//        double maxFrequency = frequencyScanner.getMaxFrequency(ByteUtils.toShorts(size), sampleRate);
//        if (infoListener != null) {
//          infoListener.onVoiceSize((int) maxFrequency);
//        }
//      }
//    });
//  }
//
//  private void mergerDecodeDataAsync(final byte[] pcmChunk) {
//    if (threadPool != null) {
//      threadPool.execute(new Runnable() {
//        @Override
//        public void run() {
//          ByteUtils.byte2File(pcmChunk, cacheFile);
//        }
//      });
//    }
//  }
//
//  public void release() {
//    createCacheFile();
//    if (pcmChunkPlayer != null) {
//      pcmChunkPlayer.release();
//    }
//  }
//
//  private void createCacheFile() {
//    try {
//      if (cacheFile.exists()) {
//        cacheFile.delete();
//      }
//      boolean newFile = cacheFile.createNewFile();
//      if (!newFile) {
//        LogUtil.e( "创建文件失败");
//      }
//    } catch (Exception e) {
//      LogUtil.e( e.getMessage());
//    }
//  }
//
//
//  public interface PlayInfoListener {
//    void onPlayProgress();
//
//    void onDecodeData(byte[] data);
//
//    void onDecodeFinish(File file);
//
//    void onPlaySize(long playsize);
//
//    void onVoiceSize(int playsize);
//  }
//}
