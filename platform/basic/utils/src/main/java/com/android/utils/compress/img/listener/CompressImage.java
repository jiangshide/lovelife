package com.android.utils.compress.img.listener;

/**
 * 图片集合的压缩返回监听
 *
 * created by jiangshide on 2020/6/23.
 * email:18311271399@163.com
 */

import com.android.utils.data.FileData;
import java.util.ArrayList;

public interface CompressImage {
  //开始压缩
  void compress();

  //图片集合的压缩结果返回
  interface CompressListener {
    //成功
    void onCompressSuccess(ArrayList<FileData> images);

    //失败
    void onCompressFailed(ArrayList<FileData> images, String error);
  }
}
