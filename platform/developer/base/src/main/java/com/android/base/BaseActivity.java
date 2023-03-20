package com.android.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.android.http.Http;
import com.android.http.download.Download;
import com.android.network.NetWork;
import com.android.network.annotation.INetType;
import com.android.network.annotation.NetType;
import com.android.permission.annotation.PermissionResult;
import com.android.permission.model.GrantModel;
import com.android.refresh.Refresh;
import com.android.refresh.api.RefreshLayout;
import com.android.refresh.header.MaterialHeader;
import com.android.refresh.listener.OnLoadMoreListener;
import com.android.refresh.listener.OnRefreshListener;
import com.android.utils.AppUtil;
import com.android.utils.Constant;
import com.android.utils.DateUtil;
import com.android.utils.FileUtil;
import com.android.utils.LogUtil;
import com.android.utils.SPUtil;
import com.android.utils.SystemUtil;
import com.android.utils.ViewUtils;
import com.android.utils.listener.ZdOnClickListener;
import com.android.widget.ZdTab;
import com.android.widget.ZdToast;
import java.io.File;

/**
 * created by jiangshide on 2019-07-23.
 * email:18311271399@163.com
 */
public class BaseActivity extends AppCompatActivity
    implements ZdOnClickListener, View.OnLongClickListener, OnRefreshListener, OnLoadMoreListener,
    Download.OnDownloadListener {

  protected BaseActivity mContext;

  public ZdTab mZdTab;

  protected Refresh mRefresh;

  protected Http http;

  public int page = 0;
  public boolean isRefesh = true;
  public int count;
  private DateUtil mDateUtil;
  public Download download;

  public void download(String url) {
    download(url, getCacheDir().getAbsolutePath());
  }

  public void download(String url, String path) {
    String fileName = FileUtil.getFileName(url);
    if (TextUtils.isEmpty(fileName)) {
      fileName = System.currentTimeMillis() + ".pdf";
    }
    download(url, path, fileName);
  }

  public void download(String url, String path, String fileName) {
    download(url, path, fileName, this);
  }

  public void download(String url, String path, String fileName,
      Download.OnDownloadListener listener) {
    if (download == null) return;
    download.download(url, path, fileName, listener);
  }

  public DateUtil countDown(long second) {
    return countDown(second * 1000, 1000);
  }

  public DateUtil countDown(long millisInFuture, long countDownInterval) {
    cancelTime();
    return mDateUtil = new DateUtil(millisInFuture, countDownInterval);
  }

  public void cancelTime() {
    if (mDateUtil != null) {
      mDateUtil.cancel();
      mDateUtil = null;
    }
  }

  public void setTopBar(View view) {
    if (view == null) return;
    view.setPadding(0, SystemUtil.getStatusBarHeight(), 0, 0);
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(newBase);
    if (null == http) http = Http.INSTANCE;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    //        Skin.getInstance().setFactory(this);//view监听
    super.onCreate(savedInstanceState);
    mContext = this;

    SystemUtil.setNoStatusBarFullMode(
        this, false, SPUtil.getInt(Constant.SYSTEM_STATUS_BAR_COLOR, R.color.alpha), false
    );
    SystemUtil.setNavigationBarColor(
        this, SPUtil.getInt(Constant.SYSTEM_NAVIGATION_COLOR, R.color.bg)
    );
    NetWork.Companion.getInstance().registerObserver(this);//网络监听
    download = new Download();
  }

  @Override protected void onPause() {
    ZdToast.cancelToast();
    super.onPause();
  }

  public void setTab(int tab) {
    mZdTab.setTab(tab);
  }

  @PermissionResult
  public void requestPermissionResult(GrantModel grantModel) {
    LogUtil.e("----------grantModel:", grantModel.isCancel);
  }

  public ZdTab push(Fragment fragment) {
    return push(fragment, "");
  }

  public ZdTab push(Fragment fragment, Bundle bundle) {
    return push(fragment, bundle, null);
  }

  public ZdTab push(Fragment fragment, String tag) {
    return push(fragment, null, tag);
  }

  public ZdTab push(Fragment fragment, Bundle bundle, String tag) {
    return mZdTab.push(fragment, bundle, tag);
  }

  public ZdTab pop() {
    return mZdTab.pop();
  }

  public ZdTab pop(String tag, int flags) {
    return mZdTab.pop(tag, flags);
  }

  public void openUrl(String url) {
    openUrl(url, "web");
  }

  public void  openUrl(
          String url,
          String title
  ) {
    Bundle bundle = new Bundle();
    bundle.putString("title",title);
    bundle.putString("url",url);
    AppUtil.goActivity(WebActivity.class,bundle);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(Class<?> _class) {
    this.goFragment(null, _class, new Bundle(), false);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String _class) {
    this.goFragment(null, _class, new Bundle(), false);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(Class<?> _class, boolean isFinissh) {
    this.goFragment(null, _class, new Bundle(), isFinissh);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String _class, boolean isFinissh) {
    this.goFragment(null, _class, new Bundle(), isFinissh);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String title, Class<?> _class) {
    this.goFragment(title, _class, new Bundle(), false);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String title, String _class) {
    this.goFragment(title, _class, new Bundle(), false);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String _class, Bundle bundle) {
    this.goFragment(null, _class, bundle, false);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String title, String _class, Bundle bundle) {
    this.goFragment(title, _class, bundle, false);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(Class<?> _class, Bundle bundle) {
    this.goFragment(null, _class, bundle, false);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String title, Class<?> _class, Bundle bundle) {
    this.goFragment(title, _class, bundle, false);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String title, Class<?> _class, boolean isFinish) {
    this.goFragment(title, _class, new Bundle(), isFinish);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String title, String _class, boolean isFinish) {
    this.goFragment(title, _class, new Bundle(), isFinish);
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String title, String _class, Bundle bundle, boolean isFinish) {
    try {
      Class clazz = Class.forName(_class);
      clazz.newInstance();
      this.goFragment(title, clazz, bundle, isFinish);
    } catch (Exception e) {
      LogUtil.e(e);
    }
  }

  /**
   * 依赖组件间跳转
   */
  public void goFragment(String title, Class _class, Bundle bundle, boolean isFinish) {
    Intent intent = new Intent(this, CommActivity.class);
    if (!TextUtils.isEmpty(title)) {
      bundle.putString("title", title);
    }
    bundle.putSerializable("_class", _class);
    intent.putExtras(bundle);
    startActivity(intent);
    if (isFinish) finish();
  }

  /**
   * 组件间对象传递
   */
  //protected <T> getIntent(Class<?> _class) {
  //  String json = getIntent().getStringExtra(OBJECT);
  //  if (TextUtils.isEmpty(json)) return null;
  //  try {
  //    return new Gson().fromJson(json, _class);
  //  } catch (Exception e) {
  //    LogUtil.e(e);
  //  }
  //  return null;
  //}

  /**
   * 网络状态监控
   * int NONE = -1;
   * int AVAILABLE = 1;
   * int AUTO = 2;
   * int CELLULAR = 3;//cmwap
   * int WIFI = 4;
   * int BLUETOOTH = 5;
   * int ETHERNET = 6;
   * int VPN = 7;
   * int WIFI_AWARE = 8;
   * int LOWPAN = 9;
   */
  @NetType(netType = INetType.AUTO)
  public void netState(@INetType int netType) {
    //ZdSnackbar.make(findViewById(android.R.id.content), netType == -1 ? "网络已断开" : "网络已连接",
    //    ZdSnackbar.LENGTH_LONG, ZdSnackbar.STYLE_SHOW_BOTTOM).show();
  }

  protected View getView(int layout) {
    return LayoutInflater.from(this).inflate(layout, null);
  }

  protected View setView(@LayoutRes int layout) {
    return setView(layout, false, this.getClass().getName());
  }

  protected View setView(@LayoutRes int layout, String tag) {
    return setView(layout, true, tag);
  }

  protected View setView(@LayoutRes int layout, boolean isRefresh) {
    return setView(layout, isRefresh, this.getClass().getName());
  }

  protected View setView(@LayoutRes int layout, boolean isRefresh, String tag) {
    if (isRefresh) {
      mRefresh = new Refresh(this);
      mRefresh.setOnRefreshListener(this);
      mRefresh.setOnLoadMoreListener(this);
      mRefresh.setEnableLoadMore(false);
      mRefresh.addView(LayoutInflater.from(this).inflate(layout, null));
      mRefresh.setRefreshHeader(new MaterialHeader(this));
      return mRefresh;
    } else {
      return getView(layout);
    }
  }

  public void showLoading() {
    ZdToast.cancelToast();
    SystemUtil.hideKeyboard(this);
    mZdTab.showLoading(this).setOnClickListener(this);
  }

  public void showFail() {
    mZdTab.showFail(this).setOnClickListener(this);
  }

  public void hiddle() {
    mZdTab.hiddleTips(this);
  }

  @Override
  public void onRefresh(@NonNull RefreshLayout refreshLayout) {
    isRefesh = true;
    page = 0;
  }

  @Override
  public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
    isRefesh = false;
  }

  /**
   * 取消刷新操作
   */
  public void cancelRefresh() {
    if (mRefresh != null) {
      mRefresh.finishRefresh();
      mRefresh.finishLoadMore();
    }
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == mZdTab.rootViewId) {
    }
  }

  @Override
  public void onClick(View v, Bundle bundle) {

  }

  @Override public boolean onLongClick(View view) {
    return false;
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    if (this.getClass().getSimpleName().equals("MainActivity")) {
    }
  }

  @Override public void onDownloadSuccess(File file) {
    LogUtil.e("file:", file);
  }

  @Override public void onDownloading(int progress) {
    LogUtil.e("progress:", progress);
  }

  @Override public void onDownloadFailed(Exception e) {
    LogUtil.e("e:", e);
  }
}
