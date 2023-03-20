package com.android.http.transformer;

import androidx.annotation.Nullable;
import com.android.http.BuildConfig;
import com.android.http.exception.HttpException;
import com.android.http.vm.data.RespData;
import com.android.utils.LogUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import retrofit2.Response;

/**
 * created by jiangshide on 2019-07-31.
 * email:18311271399@163.com
 */
public class ErrorCheckerTransformer<T extends Response<R>, R extends RespData>
    implements ObservableTransformer<T, R> {
  private String errorMessage;
  public static int code;
  public static String msg;

  public ErrorCheckerTransformer(@Nullable String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public ObservableSource<R> apply(Observable<T> upstream) {
    return upstream.map(new Function<T, R>() {
      @Override
      public R apply(T t) {
        String resultMsg = null;
        int resultCode = 0;
        RespData body = t.body();
        if(body == null){
          code = t.code();
          msg = t.message();
        }else{
          code = body.getCode();
          msg = body.getMsg();
        }
        if (!t.isSuccessful() || body == null) {
          resultMsg = t.message();
          resultCode = t.code();
        } else {
          //if (body.getCode() != BuildConfig.RESP_CODE_VALUE || body.getRes() == null || body.getRes().toString().length() <= 2) {
          if (body.getCode() != BuildConfig.RESP_CODE_VALUE){
            resultMsg = body.getMsg();
            resultCode = body.getCode();
            if (resultMsg == null) {
              resultMsg = errorMessage;
            }
          }
        }
        if (resultCode != 0) {
          throw new HttpException(resultMsg, resultCode);
        }
        return t.body();
      }
    });
  }
}