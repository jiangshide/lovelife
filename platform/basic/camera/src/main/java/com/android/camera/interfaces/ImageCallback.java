package com.android.camera.interfaces;

public interface ImageCallback {

    //给surfaceview回调的原生数据
    void onData(byte[] file);


}
