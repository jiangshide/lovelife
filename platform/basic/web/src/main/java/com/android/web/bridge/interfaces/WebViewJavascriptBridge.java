package com.android.web.bridge.interfaces;

/**
 * created by jiangshide on 2014-09-21.
 * email:18311271399@163.com
 */
public interface WebViewJavascriptBridge {
    public void send(String data);
    public void send(String data, CallBackFunction responseCallback);
}
