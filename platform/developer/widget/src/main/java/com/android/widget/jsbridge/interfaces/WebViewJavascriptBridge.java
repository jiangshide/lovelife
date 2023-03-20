package com.android.widget.jsbridge.interfaces;


public interface WebViewJavascriptBridge {
    void send(String data);

    void send(String data, CallBackFunction responseCallback);
}
