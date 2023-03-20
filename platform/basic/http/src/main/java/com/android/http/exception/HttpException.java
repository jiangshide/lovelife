package com.android.http.exception;

/**
 * created by jiangshide on 2019-10-08.
 * email:18311271399@163.com
 */
public class HttpException extends RuntimeException {

  public String msg;
  public int code = -1;
  public Throwable throwable;

  public HttpException(Throwable throwable) {
    super();
    this.throwable = throwable;
    if(throwable != null){
      this.msg = throwable.getMessage();
    }
  }

  public HttpException(String msg, int code) {
    super();
    this.msg = msg;
    this.code = code;
  }

  @Override
  public String getMessage() {
    return msg;
  }

  public int getCode() {
    return code;
  }

  @Override public String toString() {
    return "HttpException{" +
        "msg='" + msg + '\'' +
        ", code=" + code +
        ", throwable=" + throwable +
        '}';
  }
}
