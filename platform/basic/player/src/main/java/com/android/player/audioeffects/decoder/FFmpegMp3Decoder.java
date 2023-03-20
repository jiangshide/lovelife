//package com.android.player.audioeffects.decoder;
//
//import android.content.Context;
//import com.android.utils.LogUtil;
//import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
//import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
//import java.io.File;
//import java.io.IOException;
//
///**
// * created by jiangshide on 2020/4/20.
// * email:18311271399@163.com
// */
//public class FFmpegMp3Decoder {
//  private FFmpeg ffmpeg;
//  private ResultCallback callback;
//
//  public FFmpegMp3Decoder(Context context) {
//    ffmpeg = FFmpeg.getInstance(context);
//    loadFFMpegBinary();
//  }
//
//  private void loadFFMpegBinary() {
//    try {
//      ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//        @Override
//        public void onFailure() {
//          LogUtil.e("loadFFMpegBinary 初始化失败");
//        }
//      });
//    } catch (FFmpegNotSupportedException e) {
//      LogUtil.e("loadFFMpegBinary 初始化失败");
//    }
//  }
//
//  public void mp3ToPcm(File srcFile, File resultFile, ResultCallback callback) {
//    this.callback = callback;
//    try {
//      if (!resultFile.exists()) {
//        resultFile.createNewFile();
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    execFFmpegBinary(
//        new String[] { "-y", "-i", srcFile.getPath(), "-f", "s16le", resultFile.getPath() });
//  }
//
//  public void execFFmpegBinary(final String[] cmd) {
//    try {
//      ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
//
//        @Override
//        public void onStart() {
//          LogUtil.i("onStart");
//        }
//
//        @Override
//        public void onProgress(String message) {
//          LogUtil.i("onProgress-message: %s", message);
//        }
//
//        @Override
//        public void onFailure(String message) {
//          LogUtil.e("onFailure-message: %s", message);
//          if (callback != null) {
//            callback.onFailure();
//          }
//        }
//
//        @Override
//        public void onSuccess(String message) {
//          LogUtil.i("onSuccess-message: %s", message);
//          if (callback != null) {
//            callback.onSuccess();
//          }
//        }
//
//        @Override
//        public void onFinish() {
//          LogUtil.i("onFinish");
//        }
//      });
//    } catch (Exception e) {
//      LogUtil.e("execFFmpegBinary 失败");
//    }
//  }
//
//  public interface ResultCallback {
//
//    void onSuccess();
//
//    void onFailure();
//  }
//}
