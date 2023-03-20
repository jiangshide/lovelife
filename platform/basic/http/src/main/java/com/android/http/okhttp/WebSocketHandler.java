package com.android.http.okhttp;

import com.android.utils.LogUtil;
import io.reactivex.annotations.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * created by jiangshide on 2020/5/6.
 * email:18311271399@163.com
 */
public class WebSocketHandler extends WebSocketListener {

  private String mUrl;

  private WebSocket webSocket;

  private ConnectStatus connectStatus;

  private OkHttpClient client = new OkHttpClient.Builder().build();

  private static class WebSocketHandlerHelp {
    private static WebSocketHandler instance = new WebSocketHandler();
  }

  public static WebSocketHandler getInstance() {
    return WebSocketHandlerHelp.instance;
  }

  public WebSocketHandler setUrl(String url) {
    this.mUrl = url;
    return this;
  }

  public ConnectStatus getConnectStatus() {
    return connectStatus;
  }

  public WebSocketHandler connect() {
    Request request = new Request.Builder().url(mUrl).build();
    webSocket = client.newWebSocket(request, this);
    connectStatus = ConnectStatus.Connecting;
    LogUtil.e("-------webSocket:",webSocket," | state:",connectStatus," | url:",mUrl);
    return this;
  }

  public WebSocketHandler reConnect() {
    if (webSocket != null) {
      webSocket = client.newWebSocket(webSocket.request(), this);
    }
    return this;
  }

  public WebSocketHandler send(String str) {
    if (webSocket != null) {
      webSocket.send(str);
    }
    return this;
  }

  public void cancel() {
    if (webSocket != null) {
      webSocket.cancel();
    }
  }

  public void close() {
    if (webSocket != null) {
      webSocket.close(1000, null);
    }
  }

  @Override
  public void onOpen(WebSocket webSocket, Response response) {
    super.onOpen(webSocket, response);
    LogUtil.e("onOpen");
    this.connectStatus = ConnectStatus.Open;
    if (mSocketIOCallBack != null) {
      mSocketIOCallBack.onOpen();
    }
  }

  @Override
  public void onMessage(WebSocket webSocket, String text) {
    super.onMessage(webSocket, text);
    LogUtil.e("onMessage: " + text);
    if (mSocketIOCallBack != null) {
      mSocketIOCallBack.onMessage(text);
    }
  }

  @Override
  public void onMessage(WebSocket webSocket, ByteString bytes) {
    super.onMessage(webSocket, bytes);
  }

  @Override
  public void onClosing(WebSocket webSocket, int code, String reason) {
    super.onClosing(webSocket, code, reason);
    this.connectStatus = ConnectStatus.Closing;
    LogUtil.e("onClosing");
  }

  @Override
  public void onClosed(WebSocket webSocket, int code, String reason) {
    super.onClosed(webSocket, code, reason);
    LogUtil.e("onClosed");
    this.connectStatus = ConnectStatus.Closed;
    if (mSocketIOCallBack != null) {
      mSocketIOCallBack.onClose();
    }
  }

  @Override
  public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
    super.onFailure(webSocket, t, response);
    LogUtil.e("onFailure: " + t.toString());
    t.printStackTrace();
    this.connectStatus = ConnectStatus.Canceled;
    if (mSocketIOCallBack != null) {
      mSocketIOCallBack.onConnectError(t);
    }
  }

  private WebSocketCallBack mSocketIOCallBack;

  public void setSocketIOCallBack(WebSocketCallBack callBack) {
    mSocketIOCallBack = callBack;
  }

  public void removeSocketIOCallBack() {
    mSocketIOCallBack = null;
  }

  public enum ConnectStatus {
    Connecting, // the initial state of each web socket.
    Open, // the web socket has been accepted by the remote peer
    Closing, // one of the peers on the web socket has initiated a graceful shutdown
    Closed, //  the web socket has transmitted all of its messages and has received all messages from the peer
    Canceled // the web socket connection failed
  }

  public interface WebSocketCallBack {
    void onOpen();

    void onMessage(String str);

    void onClose();

    void onConnectError(Throwable t);
  }
}
