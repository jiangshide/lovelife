package com.android.base.vm;

import androidx.annotation.Keep;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import com.android.base.BuildConfig;
import com.android.http.Http;
import com.android.utils.EncryptUtils;

/**
 * created by jiangshide on 2019-08-14.
 * email:18311271399@163.com
 */
@Keep
public class BaseVM extends ViewModel {
  protected int page = 0;
  public int size = 20;
  public boolean isRefresh;
  public String action;
  public int type;

  public Http http;

  public BaseVM() {
    if (null == http) http = Http.INSTANCE;
    page++;
  }

  public static <T extends ViewModel> T of(FragmentActivity activity, Class<T> modelClass) {
    return ViewModelProviders.of(activity).get(modelClass);
  }

  public void refresh(boolean isRefresh) {
    if (isRefresh) {
      page = 0;
    } else {
      page += 1;
    }
    this.isRefresh = isRefresh;
  }

  public String md5(String psw) {
    return BuildConfig.IS_MD5 ? EncryptUtils.encryptMd5(psw) : psw;
  }
}
