package com.android.utils.compress.img.listener;

/**
 * 单张图片压缩是的监听
 *
 * created by jiangshide on 2020/6/23.
 * email:18311271399@163.com
 */
public interface CompressResultListener {
  //成功
  void onCompressSuccess(String imgPath);

  //失败
  void onCompressFailed(String imgPath, String error);
}