package com.android.http.vm;

import com.android.http.exception.HttpException;
import com.android.http.transformer.ErrorCheckerTransformer;

/**
 * created by jiangshide on 2019-12-06.
 * email:18311271399@163.com
 */
public class LiveResult<T> {
  private final HttpException error;
  private final T data;
  private final int code;
  private final String msg;

  public LiveResult(int code, String msg, HttpException error, T data) {
    this.code = code;
    this.msg = msg;
    this.error = error;
    this.data = data;
  }

  public int getCode(){
    return code;
  }

  public String getMsg(){
    return msg;
  }

  public T getData() {
    return data;
  }

  public HttpException getError() {
    return error;
  }

  public static <T> LiveResult<T> success(T data) {
    return new LiveResult<>(ErrorCheckerTransformer.code, ErrorCheckerTransformer.msg, null, data);
  }

  public static <T> LiveResult<T> error(HttpException throwable) {
    return new LiveResult<>(ErrorCheckerTransformer.code, ErrorCheckerTransformer.msg, throwable,
        null);
  }
}
