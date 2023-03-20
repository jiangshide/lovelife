package com.android.utils.compress.img;

import android.content.Context;
import android.text.TextUtils;
import com.android.utils.compress.img.config.CompressConfig;
import com.android.utils.compress.img.core.CompressImageUtil;
import com.android.utils.compress.img.listener.CompressImage;
import com.android.utils.compress.img.listener.CompressResultListener;
import com.android.utils.data.FileData;
import java.io.File;
import java.util.ArrayList;

/**
 * created by jiangshide on 2020/6/23.
 * email:18311271399@163.com
 */
public class ImgCompress implements CompressImage {
  private CompressImageUtil compressImageUtil;//压缩工具类（提炼）
  private ArrayList<FileData> images;//需要压缩的图片集合
  private CompressListener listener;//压缩的监听
  private CompressConfig config;//压缩的配置

  private ImgCompress(Context context, CompressConfig compressConfig,
      ArrayList<FileData> images, CompressListener listener) {
    this.compressImageUtil = new CompressImageUtil(context, compressConfig);
    this.images = images;
    this.listener = listener;
    this.config = compressConfig;
  }

  public static CompressImage build(Context context, CompressListener listener, FileData... datas) {
    ArrayList<FileData> arrayList = new ArrayList<>();
    for (FileData data : datas) {
      arrayList.add(data);
    }
    return build(context, listener, arrayList);
  }

  public static CompressImage build(Context context, CompressListener listener,
      ArrayList<FileData> images) {
    CompressConfig compressConfig = CompressConfig.builder()
        .setUnCompressMinPixel(1000)//最小像素不压缩，默认值：1000
        .setUnCompressNormalPixel(2000)//标准像素不压缩，默认值：2000
        .setMaxPixel(1000) //长或宽不超过的最大像素（单位px），默认值：1200
        .setMaxSize(100 * 1024)//压缩到的最大大小（单位B）,默认值：200*1024 = 200KB
        .enablePixelCompress(true)//是否启用像素压缩，默认值：true
        .enableQualityCompress(true)//是否启用质量压缩，默认值：true
        .enableReserveRaw(true)//是否保留源文件，默认值：true
        .setCacheDir("")//压缩后缓存图片路径，默认值：Constants.COMPRESS_CACHE
        .setShowCompressDialog(true)//是否显示压缩进度条，默认值：false
        .create();
    return new ImgCompress(context, compressConfig, images, listener);
  }

  public static CompressImage build(Context context, CompressConfig compressConfig,
      CompressListener listener, ArrayList<FileData> images) {
    return new ImgCompress(context, compressConfig, images, listener);
  }

  @Override
  public void compress() {
    if (images == null || images.isEmpty()) {
      listener.onCompressFailed(images, "有空情况。。。");
      return;
    }
    for (FileData image : images) {
      if (image == null) {
        listener.onCompressFailed(images, "某张图片有情况。。。");
        return;
      }
    }
    //都没有问题了，开始从第一张开始压缩
    compress(images.get(0));
  }

  //index = 0
  private void compress(final FileData image) {
    if (TextUtils.isEmpty(image.getPath())) {
      continueCompress(image, false);
      return;
    }
    File file = new File(image.getPath());
    if (!file.exists() || !file.isFile()) {
      continueCompress(image, false);
      return;
    }

    if (file.length() < config.getMaxSize()) {
      continueCompress(image, true);
      return;
    }
    //确实要压缩了
    compressImageUtil.compress(image.getPath(), new CompressResultListener() {
      @Override
      public void onCompressSuccess(String imgPath) {
        //压缩成功的图片路径设置到对象的属性中
        image.setDest(imgPath);
        continueCompress(image, true);
        File file = new File(imgPath);
      }

      @Override
      public void onCompressFailed(String imgPath, String error) {
        continueCompress(image, false, error);
        File file = new File(imgPath);
      }
    });
  }

  private void continueCompress(FileData image, boolean bool, String... error) {
    image.setCompress(bool);
    //获取当前的索引
    int index = images.indexOf(image);
    //判断是否为压缩的图片最后一张
    if (index == images.size() - 1) {
      //不需要压缩了，告知activity界面
      callback(error);
    } else {
      //递归
      compress(images.get(index + 1));
    }
  }

  private void callback(String... error) {
    if (error.length > 0) {
      listener.onCompressFailed(images, error[0]);
      return;
    }
    for (FileData image : images) {
      if (!image.getCompress()) {
        listener.onCompressFailed(images, "............");
        return;
      }
    }
    listener.onCompressSuccess(images);
  }
}
