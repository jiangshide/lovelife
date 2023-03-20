package com.android.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.event.ZdEvent;
import com.android.http.Http;
import com.android.http.download.Download;
import com.android.refresh.Refresh;
import com.android.refresh.api.RefreshLayout;
import com.android.refresh.footer.ClassicsFooter;
import com.android.refresh.header.MaterialHeader;
import com.android.refresh.listener.OnLoadMoreListener;
import com.android.refresh.listener.OnRefreshListener;
import com.android.tablayout.ZdTabLayout;
import com.android.utils.Constant;
import com.android.utils.DateUtil;
import com.android.utils.FileUtil;
import com.android.utils.LogUtil;
import com.android.utils.SPUtil;
import com.android.utils.SystemUtil;
import com.android.utils.listener.ZdOnClickListener;
import com.android.widget.ZdButton;
import com.android.widget.ZdRecycleView;
import com.android.widget.ZdTab;
import com.android.widget.ZdTipsView;
import com.android.widget.ZdToast;
import com.android.widget.ZdTopView;
import com.android.widget.ZdViewPager;
import com.android.widget.adapter.ZdAdapter;
import com.android.widget.adapter.ZdFragmentPagerAdapter;
import com.android.widget.adapter.helper.ViewHelper;
import com.android.widget.adapter.listener.IViewCreate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * created by jiangshide on 2019-07-23.
 * email:18311271399@163.com
 */
public class BaseFragment extends Fragment
        implements ZdOnClickListener, AdapterView.OnItemClickListener, OnRefreshListener,
        OnLoadMoreListener, Refresh.OnTouchEventListener, IViewCreate, ZdTipsView.OnTipsListener,
        Download.OnDownloadListener, View.OnTouchListener {

    private boolean isPush = false;

    protected Refresh mRefresh;

    public Http http;

    public BaseActivity mActivity;

    public ZdRecycleView recyclerView;

    protected ViewHelper viewHelper;

    public ZdTopView zdTopView;
    public ZdTipsView zdTipsView;
    protected HashMap<String, Object> hashMap;

    public int page = 0;
    public boolean isRefesh;

    public Download download;
    public String mSufix;

    public boolean isBackground = true;

    private boolean mShowTop = true;

    public BaseFragment setTopView(boolean isShow) {
        this.mShowTop = isShow;
        return this;
    }

    public BaseFragment setSufix(String sufix) {
        this.mSufix = sufix;
        return this;
    }

    public void download(String url) {
        if (mActivity == null) return;
        download(url, mActivity.getCacheDir().getAbsolutePath());
    }

    public void download(String url, Download.OnDownloadListener listener) {
        download(url, FileUtil.getDownload(), FileUtil.getFileName(url), listener);
    }

    public void download(String url, String path) {
        String fileName = FileUtil.getFileName(url);
        if (TextUtils.isEmpty(fileName)) {
            fileName = System.currentTimeMillis() + (TextUtils.isEmpty(mSufix) ? ".pdf" : mSufix);
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

    public BaseFragment() {
        hashMap = new HashMap<>();
    }

    public void setTopBar(View view) {
        if (view == null) return;
        view.setPadding(0, SystemUtil.getStatusBarHeight(), 0, 0);
    }

    public DateUtil countDown(long second) {
        return mActivity.countDown(second);
    }

    public DateUtil countDown(long millisInFuture, long countDownInterval) {
        return mActivity.countDown(millisInFuture, countDownInterval);
    }

    public void cancelTime() {
        mActivity.cancelTime();
    }

    public void showTabHost(boolean isShow) {
        if (mActivity == null || mActivity.mZdTab == null) return;
        mActivity.mZdTab.showTabHost(isShow);
    }

    public void showFloat(boolean isShow) {
        if (mActivity == null) return;
        mActivity.mZdTab.floatL.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public int getPosition() {
        if (mActivity == null && mActivity.mZdTab == null) return 0;
        return mActivity.mZdTab.getIndex();
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (null == http) http = Http.INSTANCE;
        mActivity = (BaseActivity) getActivity();
        download = new Download();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewHelper = new ViewHelper.Builder(this).build();
        super.onViewCreated(view, savedInstanceState);
    }

    public void setRefreshEvent(String refreshEvent) {
        (mActivity).mZdTab.setRefreshEvent(refreshEvent);
    }

    public void clearAnimTab() {
        (mActivity).mZdTab.clearAnim();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public void setTab(int tab) {
        if (mActivity == null) return;
        mActivity.setTab(tab);
    }

    protected View getView(int layout) {
        return LayoutInflater.from(mActivity).inflate(layout, null);
    }

    protected View setView(@LayoutRes int layout) {
        return setView(layout, false, false, getTag());
    }

    protected View setView(@LayoutRes int layout, String tag) {
        return setView(layout, true, false, tag);
    }

    protected View setView(@LayoutRes int layout, boolean isRefresh) {
        return setView(layout, isRefresh, false, getTag());
    }

    protected View setView(@LayoutRes int layout, boolean isRefresh, boolean isMore) {
        return setView(layout, isRefresh, isMore, getTag());
    }

    public View setView(@LayoutRes int layout, boolean isRefresh, boolean isMore, String tag) {
        return setView(LayoutInflater.from(getContext()).inflate(layout, null), isRefresh, isMore,
                tag);
    }

    protected View setView(View view, boolean isRefresh, boolean isMore, String tag) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setOnTouchListener(this);
        if (isBackground) {
            frameLayout.setBackgroundColor(Constant.getColor(R.color.bg));
        }
        zdTipsView = new ZdTipsView(getContext());
        zdTipsView.hidden();
        recyclerView = view.findViewById(R.id.recycleView);
        if (!isRefresh) {
            frameLayout.addView(view);
            frameLayout.addView(zdTipsView);
            return frameLayout;
        }
        mRefresh = new Refresh(getContext());
        mRefresh.setOnRefreshListener(this)
                .setOnLoadMoreListener(this)
                .setEnableRefresh(isRefresh)
                .setEnableLoadMore(isMore)
                .setRefreshHeader(new MaterialHeader(getActivity()))
                .setRefreshFooter(new ClassicsFooter(getActivity()));
        //mRefresh.setOnTouchListener(this)
        mRefresh.addView(view);
        if (TextUtils.isEmpty(tag)) {
            mRefresh.setTag(tag);
        }
        frameLayout.addView(mRefresh);
        frameLayout.addView(zdTipsView);
        return frameLayout;
    }

    public View setTitleView(int layout) {
        return setTitleView(layout, "", false, this.getClass().getName());
    }

    public View setTitleView(int layout, String title) {
        return setTitleView(layout, title, false, this.getClass().getName());
    }

    public View setTitleView(int layout, boolean isRefresh) {
        return setTitleView(layout, "", isRefresh, this.getClass().getName());
    }

    public View setTitleView(int layout, boolean isRefresh, boolean isMore) {
        return setTitleView(layout, "", isRefresh, isMore, this.getClass().getName());
    }

    public View setTitleView(int layout, String title, boolean isRefresh) {
        return setTitleView(layout, title, isRefresh, this.getClass().getName());
    }

    public View setTitleView(int layout, String title, boolean isRefresh, String tag) {
        return setTitleView(layout, title, isRefresh, false, tag);
    }

    public View setTitleView(int layout, String title, boolean isRefresh, boolean isMore,
                             String tag) {
        LinearLayout root = new LinearLayout(getContext());
        root.setClickable(true);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        root.setLayoutParams(params);
        root.setBackgroundColor(getColor(SPUtil.getInt(Constant.APP_COLOR_BG, R.color.bg)));
        root.setOrientation(LinearLayout.VERTICAL);
        if (zdTopView == null) {
            zdTopView = new ZdTopView(getContext());
        }
        if (!TextUtils.isEmpty(title)) {
            zdTopView.setTitle(title);
        } else if (hashMap.containsKey("title")) {
            zdTopView.setTitle(hashMap.get("title"));
        }
        if (hashMap.containsKey("titleColor")) {
            zdTopView.setTitleColor((Integer) hashMap.get("titleColor"));
        }
        if (hashMap.containsKey("topBgIcon")) {
            zdTopView.setBg((Integer) hashMap.get("topBgIcon"));
        }
        if (hashMap.containsKey("titleGravity")) {
            zdTopView.setTitleGravity((Integer) hashMap.get("titleGravity"));
        }
        if (hashMap.containsKey("smallTitle")) {
            zdTopView.setSmallTitle(hashMap.get("smallTitle"));
        }
        if (hashMap.containsKey("smallTitleColor")) {
            zdTopView.setSmallTitleColor((Integer) hashMap.get("smallTitleColor"));
        }
        if (hashMap.containsKey("left")) {
            zdTopView.setLefts(hashMap.get("left"));
        }
        if (hashMap.containsKey("leftColor")) {
            zdTopView.setLeftColor((Integer) hashMap.get("leftColor"));
        }
        if (hashMap.containsKey("right")) {
            zdTopView.setRights(hashMap.get("right"));
        }
        if (hashMap.containsKey("rightColor")) {
            zdTopView.setRightColor((Integer) hashMap.get("rightColor"));
        }
        zdTopView.setOnLeftClick(
                hashMap.containsKey("leftClick") ? (View.OnClickListener) hashMap.get("leftClick")
                        : this::onClick);
        if (hashMap.containsKey("rightClick")) {
            zdTopView.setOnRightClick((View.OnClickListener) hashMap.get("rightClick"));
        }
        if (hashMap.containsKey("datas")) {
            zdTopView.setDataList((List<String>) hashMap.get("datas"));
        }
        if (hashMap.containsKey("itemClick")) {
            zdTopView.setOnItemListener((AdapterView.OnItemClickListener) hashMap.get("itemClick"));
        }
        root.removeView(zdTopView);
        root.addView(zdTopView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        root.addView(setView(layout, isRefresh, isMore, tag), LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        if (mShowTop) {
            setTopBar(root);
        }
        return root;
    }

    public BaseFragment setTopBgIcon(int topBgIcon) {
        if (zdTopView != null) {
            zdTopView.setBg(topBgIcon);
        }
        hashMap.put("topBgIcon", topBgIcon);
        return this;
    }

    public BaseFragment setTitleGravity(int gravity) {
        if (zdTopView != null) {
            zdTopView.setTitleGravity(gravity);
        }
        hashMap.put("titleGravity", gravity);
        return this;
    }

    public BaseFragment setTitle(Object title) {
        if (zdTopView != null) {
            zdTopView.setTitle(title);
        }
        hashMap.put("title", title);
        return this;
    }

    public BaseFragment setTitleColor(int color) {
        if (zdTopView != null) {
            zdTopView.setTitleColor(color);
        }
        hashMap.put("titleColor", color);
        return this;
    }

    public BaseFragment setSmallTitle(Object smallTitle) {
        if (zdTopView != null) {
            zdTopView.setSmallTitle(smallTitle);
        }
        hashMap.put("smallTitle", smallTitle);
        return this;
    }

    public BaseFragment setSmallTitleColor(int smallTitleColor) {
        if (zdTopView != null) {
            zdTopView.setSmallTitleColor(smallTitleColor);
        }
        hashMap.put("smallTitleColor", smallTitleColor);
        return this;
    }

    public BaseFragment setLeft(Object object) {
        if (zdTopView != null) {
            zdTopView.setLefts(object);
        }
        hashMap.put("left", object);
        return this;
    }

    public BaseFragment setLeftColor(int color) {
        if (zdTopView != null) {
            zdTopView.setLeftColor(color);
        }
        hashMap.put("leftColor", color);
        return this;
    }

    public BaseFragment setLeftEnable(boolean isEnable) {
        return setLeftEnable(0.5F, isEnable);
    }

    public BaseFragment setLeftEnable(float alpha, boolean isEnable) {
        if (zdTopView == null) return this;
        zdTopView.topLeftBtn.setAlpha(alpha);
        zdTopView.topLeftBtn.setEnabled(isEnable);
        return this;
    }

    public BaseFragment setRightEnable(boolean isEnable) {
        return setRightEnable(isEnable ? 1.0F : 0.5F, isEnable);
    }

    public BaseFragment setRightEnable(float alpha, boolean isEnable) {
        if (zdTopView == null) return this;
        zdTopView.topRightBtn.setAlpha(alpha);
        zdTopView.topRightBtn.setEnabled(isEnable);
        return this;
    }

    public BaseFragment setRight(Object object) {
        if (zdTopView != null) {
            zdTopView.setRights(object);
        } else {
            hashMap.put("right", object);
        }
        return this;
    }

    public BaseFragment setRightColor(int color) {
        if (zdTopView != null) {
            zdTopView.setRightColor(color);
        }
        hashMap.put("rightColor", color);
        return this;
    }

    public BaseFragment setDatas(List<String> list) {
        if (zdTopView != null) {
            zdTopView.setDataList(list);
        } else {
            hashMap.put("datas", list);
        }
        return this;
    }

    public void setLeftListener() {
        pop();
    }

    public BaseFragment setLeftListener(View.OnClickListener listener) {
        if (zdTopView != null) {
            zdTopView.setOnLeftClick(listener);
        } else {
            hashMap.put("leftClick", listener);
        }
        return this;
    }

    public BaseFragment setRightListener(View.OnClickListener listener) {
        if (zdTopView != null) {
            zdTopView.setOnRightClick(listener);
        } else {
            hashMap.put("rightClick", listener);
        }
        return this;
    }

    public BaseFragment setItemClickListener(AdapterView.OnItemClickListener listener) {
        if (zdTopView != null) {
            zdTopView.setOnItemListener(listener);
        } else {
            hashMap.put("itemClick", listener);
        }
        return this;
    }

    public BaseFragment setTipsRes(int res) {
        if (zdTipsView == null) return this;
        zdTipsView.setTipsImg(res);
        return this;
    }

    public BaseFragment showLoading() {
        ZdToast.cancelToast();
        if (zdTipsView == null) return this;
        zdTipsView.loading();
        SystemUtil.hideKeyboard(mActivity);
        //mActivity.mZdTab.showLoading(mActivity).setOnClickListener(this);
        return this;
    }

    public void showFloatMenu(boolean isShow) {
        ZdTab ZdTab = ((BaseActivity) mActivity).mZdTab;
        if (ZdTab != null) {
            ZdTab.floatL.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    public void setStatusBar(boolean isShow) {
        if (mActivity != null && mActivity.mZdTab != null) {
            mActivity.mZdTab.showStatusBar(isShow);
        }
    }

    public void setNavigationBar(boolean isShow) {
        if (mActivity != null && mActivity.mZdTab != null) {
            mActivity.mZdTab.showNavigationBar(isShow);
        }
    }

    public BaseFragment noNet() {
        return noNet("Network error!");
    }

    public BaseFragment noNet(int res) {
        return noNet(res);
    }

    public BaseFragment noNet(String tips) {
        return noNet(tips,"", this);
    }

    public BaseFragment noNet(String tips,String btnTips, ZdTipsView.OnTipsListener listener) {
        if (zdTipsView == null) return this;
        zdTipsView.setTipsDes(tips).setBtnTips(btnTips).noNet().setListener(listener);
        return this;
    }

    public BaseFragment noData() {
        return noData("No content");
    }

    public BaseFragment noData(int res) {
        return noData(res);
    }

    public BaseFragment noData(String tips) {
        if (zdTipsView == null) return this;
        zdTipsView.setTipsDes(tips).noData();
        //mActivity.mZdTab.showFail(mActivity).setOnClickListener(this);
        return this;
    }

    public void hiddle() {
        if (zdTipsView == null) return;
        zdTipsView.hidden();
        //mActivity.mZdTab.hiddleTips(mActivity);
    }

    @Override
    public void onDown() {
    }

    @Override
    public void onMove(float scrollY) {
        SystemUtil.hideKeyboard(mActivity, mRefresh);
    }

    @Override
    public void onUp(float scrollY) {
    }

    @Override
    public void onPause() {
        SystemUtil.hideKeyboard(mActivity);
        ZdToast.cancelToast();
        super.onPause();
        //cancelTime();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return setView(R.layout.default_tab_layout);
    }

    protected ZdViewPager createTabLayout(View view, boolean isTabBtn, List<String> titles,
                                          List<Fragment> fragments, Bundle savedInstanceState) {
        ZdTabLayout tabTitle = view.findViewById(R.id.tabTitle);
        ImageView dotImg = view.findViewById(R.id.dotImg);
        ZdViewPager tabView = view.findViewById(R.id.tabView);
        ZdButton tabBtn = view.findViewById(R.id.tabBtn);
        tabBtn.setVisibility(isTabBtn ? View.VISIBLE : View.GONE);
        ZdFragmentPagerAdapter zdFragmentPagerAdapter = tabView.create(getChildFragmentManager());
        tabView.setAdapter(zdFragmentPagerAdapter
                .setTitles(
                        titles
                )
                .setFragment(
                        fragments
                )
                .initTabs(getActivity(), tabTitle, tabView)
                .setLinePagerIndicator(getColor(R.color.yellow)));
        onViewCreated(view, tabTitle, dotImg, tabView, tabBtn, savedInstanceState);
        return tabView;
    }

    protected void onViewCreated(View view, ZdTabLayout tabTitle, ImageView dotImg,
                                 ZdViewPager tabView, ZdButton tabBtn, Bundle savedInstanceState) {
    }

    public BaseFragment getFragment(String _class) {
        try {
            return (BaseFragment) Class.forName(_class).newInstance();
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return null;
    }

    public Bundle set(String key, int value) {
        Bundle bundle = new Bundle();
        bundle.putInt(key, value);
        return bundle;
    }

    public Bundle set(String key, long value) {
        Bundle bundle = new Bundle();
        bundle.putLong(key, value);
        return bundle;
    }

    public Bundle set(String key, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        return bundle;
    }

    public Bundle setArr(ArrayList<String> datas) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("datas", datas);
        return bundle;
    }

    public void push(Fragment fragment) {
        push(fragment, this.getTag());
    }

    public void push(Fragment fragment, Parcelable parcelable) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", parcelable);
        push(fragment, bundle);
    }

    public void push(Fragment fragment, ArrayList<? extends Parcelable> datas) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("datas", datas);
        push(fragment, bundle);
    }

    public void push(String _class) {
        this.push(_class, null);
    }

    public void push(String _class, Bundle bundle) {
        try {
            BaseFragment baseFragment =
                    (BaseFragment) Class.forName(_class).newInstance();
            push(baseFragment, bundle, this.getTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void push(Fragment fragment, Bundle bundle) {
        push(fragment, bundle, this.getTag());
    }

    public void push(Fragment fragment, String tag) {
        push(fragment, null, tag);
    }

    public void push(Fragment fragment, Bundle bundle, String tag) {
        isPush = true;
        setLeftListener(this);
        ZdTab.instance.push(fragment, bundle, tag);
    }

    public ZdTab pop() {
        return ZdTab.instance.pop();
    }

    public void pop(int time) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ZdEvent.Companion.get().with("pop").post("pop");
            }
        }, time);
    }

    public ZdTab pop(String tag, int flags) {
        return ZdTab.instance.pop(tag, flags);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(Class _class) {
        return this.goFragment(null, _class, new Bundle(), false);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String _class) {
        return this.goFragment(null, _class, new Bundle(), false);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(Class _class, boolean isFinissh) {
        return this.goFragment(null, _class, new Bundle(), isFinissh);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String _class, boolean isFinissh) {
        return this.goFragment(null, _class, new Bundle(), isFinissh);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String title, Class _class) {
        return this.goFragment(title, _class, new Bundle(), false);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(Class _class, String param) {
        return this.goFragment(_class, param, false);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String title, Class _class, String param) {
        return this.goFragment(title, _class, param, false);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(Class _class, String param, boolean isFinissh) {
        return this.goFragment(null, _class, param, isFinissh);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String title, Class _class, String param, boolean isFinissh) {
        Bundle bundle = new Bundle();
        bundle.putString("param", param);
        return this.goFragment(title, _class, bundle, isFinissh);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String title, String _class) {
        return this.goFragment(title, _class, new Bundle(), false);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(Class _class, Bundle bundle) {
        return this.goFragment(null, _class, bundle, false);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String title, Class _class, Bundle bundle) {
        return this.goFragment(title, _class, bundle, false);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String title, String _class, Bundle bundle) {
        return this.goFragment(title, _class, bundle, false);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String title, Class _class, boolean isFinish) {
        return this.goFragment(title, _class, new Bundle(), isFinish);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String title, String _class, boolean isFinish) {
        return this.goFragment(title, _class, new Bundle(), isFinish);
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String title, String _class, Bundle bundle, boolean isFinish) {
        ((BaseActivity) getActivity()).goFragment(title, _class, bundle, isFinish);
        return this;
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(Class _class, Bundle bundle, boolean isFinish) {
        ((BaseActivity) getActivity()).goFragment(null, _class, bundle, isFinish);
        return this;
    }

    /**
     * 依赖组件间跳转
     */
    public BaseFragment goFragment(String title, Class _class, Bundle bundle, boolean isFinish) {
        ((BaseActivity) getActivity()).goFragment(title, _class, bundle, isFinish);
        return this;
    }

    public void go(Class _class) {
        this.go(_class, true);
    }

    public void go(Class _class, Boolean isFinish) {
        startActivity(new Intent(mActivity, _class));
        if (mActivity != null && isFinish) {
            mActivity.finish();
        }
    }

    public void startService(Class clazz) {
        if (mActivity == null) return;
        mActivity.startService(new Intent(mActivity, clazz));
    }

    protected int getColor(int resColor) {
        return getResources().getColor(resColor);
    }

    protected int getDim(int resDim) {
        return getResources().getDimensionPixelSize(resDim);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        isRefesh = false;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isRefesh = true;
        page = 0;
    }

    public void autoRefresh() {
        if (mRefresh != null) {
            mRefresh.autoRefresh();
        }
    }

    public void enableRefresh(boolean isRefresh) {
        if (mRefresh != null) {
            mRefresh.setEnableRefresh(isRefresh);
        }
    }

    public void enableLoadMore(boolean isLoadMore) {
        if (mRefresh != null) {
            mRefresh.setEnableLoadMore(isLoadMore);
        }
    }

    public void cancelRefresh() {
        this.cancelRefresh(mRefresh, 1);
    }

    public void cancelRefresh(int delayed) {
        this.cancelRefresh(mRefresh, delayed);
    }

    public void cancelRefresh(Refresh refresh, int delayed) {
        if (refresh != null) {
            refresh.finishRefresh(delayed);
            refresh.finishLoadMore();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mActivity.mZdTab.rootViewId) {
        } else if (v.getId() == R.id.topLeftBtn) {
            setLeftListener();
        }
    }

    @Override
    public void onClick(View v, Bundle bundle) {
    }

    public void finish() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }

    @Override
    public Context context() {
        return mActivity;
    }

    @Override
    public RecyclerView createRecyclerView() {
        return null;
    }

    @Override
    public ZdAdapter createRecycleViewAdapter() {
        return null;
    }

    public void notifyAdapterDataSetChanged(List<?> datas) {
        if (viewHelper == null) return;
        viewHelper.notifyAdapterDataSetChanged(datas);
    }

    public LinearLayoutManager getLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        return linearLayoutManager;
    }

    @Override
    public void onTips(View view) {
    }

    @Override
    public void onDownloadSuccess(File file) {
        LogUtil.e("file:", file);
        hiddle();
    }

    @Override
    public void onDownloading(int progress) {
        LogUtil.e("progress:", progress);
        showLoading();
    }

    @Override
    public void onDownloadFailed(Exception e) {
        LogUtil.e("e:", e);
        hiddle();
        ZdToast.txt(e.getMessage());
    }

    public OnFragmentListener mOnFragmentListener;

    public void setFragmentListener(OnFragmentListener listener) {
        this.mOnFragmentListener = listener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    public interface OnFragmentListener {
        public void onMsg();
    }
}
