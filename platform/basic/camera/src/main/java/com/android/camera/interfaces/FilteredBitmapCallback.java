package com.android.camera.interfaces;

import android.graphics.Bitmap;

public interface FilteredBitmapCallback {

    //经过滤镜处理的bitmap数据
    void onData(Bitmap bitmap);

}
