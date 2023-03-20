package com.android.web.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.web.bridge.BridgeWebView;

/**
 * created by jiangshide on 2020-01-23.
 * email:18311271399@163.com
 */
public class ZdWebView extends FrameLayout {

  public ZdWebView(@NonNull Context context) {
    super(context);
    init();
  }

  public ZdWebView(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ZdWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private final static String PDFJS_PREFIX = "file:///android_asset/pdf_js/web/viewer.html?file=";
  public BridgeWebView bridgeWebView;

  @SuppressLint("SetJavaScriptEnabled")
  private void init() {
    bridgeWebView = new BridgeWebView(getContext());
    addView(bridgeWebView);
    WebSettings settings = bridgeWebView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setAllowUniversalAccessFromFileURLs(true);
  }

  public void loadLocalPDF(String path) {
    bridgeWebView.loadUrl(PDFJS_PREFIX + "file://" + path);
  }

  public void loadOnlinePDF(String url) {
    bridgeWebView.loadUrl(PDFJS_PREFIX + url);
  }

  public void loadUrl(String url) {
    bridgeWebView.loadUrl(url);
  }

  public void reload() {
    bridgeWebView.reload();
  }
}
