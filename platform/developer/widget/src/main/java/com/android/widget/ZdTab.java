package com.android.widget;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.event.ZdEvent;
import com.android.utils.Constant;
import com.android.utils.LogUtil;
import com.android.utils.SPUtil;
import com.android.utils.SystemUtil;
import com.android.widget.anim.Anim;
import com.android.widget.menu.SectorMenuButton;

import java.util.ArrayList;
import java.util.List;

/**
 * created by jiangshide on 2019-07-24.
 * email:18311271399@163.com
 */
public class ZdTab extends ZdTabHost implements FragmentManager.OnBackStackChangedListener {

    private RelativeLayout tabMain;
    private FragmentManager fragmentManager;
    private ZdTabHost fragmentTabHost;
    public FrameLayout frameLayout;
    //private ZdViewPager zdViewPager;
    private View statusBar, navigationBar;

    public LinearLayout floatL;
    public ImageView floatPlay;
    public SectorMenuButton floatMenus;
    public ImageView floatIcon;
    public ZdButton floatBtn;

    private LinearLayout defaultTabBarL;
    private ProgressBar defaultProgress;
    private Fragment[] mFragments;
    private String[] mTitles;
    private String[] mTags;
    private int[] mStates;
    private int[] mIcons;
    private int mSelectedColor = Color.parseColor("#ffd200");
    private int mUnSelectedColor = Color.parseColor("#000000");
    private OnTabViewListener mOnTabViewListener;
    private OnTabClickListener mOnTabClickListener;
    private OnTabLongClickListener mOnTabLongClickListenerr;
    private final int MIN_CLICK_DELAY_TIME = 200;
    private long mLastClickTime = 0;
    private List<TextView> mRedDotList;
    private int index;
    private List<ImageView> mImageViews;
    private List<TextView> mTextViews;
    public static ZdTab instance;

    private int mEnterAnim;
    private int mExitAnim;
    private boolean mIsAnim;

    private View rootView;

    public final static int rootViewId = 0x1000000;

    private int refresgId;//刷新Id

    public final static int TAB = 0;//被选中
    public final static int NO_TAB = 1;//不被选中

    private ImageView mRefreshImg;

    private String mRefreshEvent = "refreshEvent";
    private ObjectAnimator objectAnimator;

    public boolean isLogin;

    public ZdTab(Context context, FragmentManager fragmentManager) {
        super(context);
        this.fragmentManager = fragmentManager;
        this.fragmentManager.addOnBackStackChangedListener(this);
        init();
    }

    public ZdTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        instance = this;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.default_tab, this, true);
        tabMain = view.findViewById(R.id.tabMain);
        fragmentTabHost = view.findViewById(android.R.id.tabhost);
        frameLayout = view.findViewById(R.id.frameLayout);
        statusBar = view.findViewById(R.id.defaultStatusBar);
        navigationBar = view.findViewById(R.id.defaultNavigationBar);
        statusBar.getLayoutParams().height = SystemUtil.getStatusBarHeight();
        navigationBar.getLayoutParams().height = SystemUtil.getNavigationBarHeight();

        showStatusBar(false);
        showNavigationBar(true);
        floatL = view.findViewById(R.id.floatL);
        floatPlay = view.findViewById(R.id.floatPlay);
        floatMenus = view.findViewById(R.id.floatMenus);
        floatIcon = view.findViewById(R.id.floatIcon);
        floatBtn = view.findViewById(R.id.floatBtn);

        defaultTabBarL = view.findViewById(R.id.defaultTabBarL);
        defaultProgress = view.findViewById(R.id.defaultProgress);
        mEnterAnim = R.anim.slide_right_in;
        mExitAnim = R.anim.slide_right_out;
    }

    public View showStatusBar(boolean isShow) {
        return showStatusBar(isShow, -1);
    }

    public View showStatusBar(boolean isShow, int color) {
        statusBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (color != -1) {
            statusBar.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        } else {
            statusBar.setBackgroundColor(ContextCompat.getColor(getContext(),
                    SPUtil.getInt(Constant.SYSTEM_STATUS_BAR_COLOR, R.color.bg)));
        }
        return this;
    }

    public View showNavigationBar(boolean isShow) {
        return showNavigationBar(isShow, -1);
    }

    public View showNavigationBar(boolean isShow, int color) {
        navigationBar.setVisibility(
                SystemUtil.getNavHeight(getContext()) == 0 ? View.GONE : isShow ? View.VISIBLE : View.GONE);
        if (color != -1) {
            navigationBar.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        } else {
            navigationBar.setBackgroundColor(ContextCompat.getColor(getContext(),
                    SPUtil.getInt(Constant.SYSTEM_STATUS_BAR_COLOR, R.color.bg)));
        }
        return this;
    }

    public View showLoading(Activity activity) {
        return showTips(activity, R.layout.default_loading);
    }

    public View showFail(Activity activity) {
        return showTips(activity, R.layout.default_fail);
    }

    public View showTips(Activity activity, int layout) {
        rootView = LayoutInflater.from(activity).inflate(layout, null);
        rootView.setId(rootViewId);
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        );
        ((FrameLayout.LayoutParams) params).gravity = Gravity.CENTER;
        viewGroup.addView(rootView, params);
        return rootView;
    }

    public void setProgress(int progress) {
        setProgress(100, progress);
    }

    public void setProgress(int max, int progress) {
        if (defaultProgress == null) return;
        defaultProgress.setMax(max);
        defaultProgress.setProgress(progress);
        defaultProgress.setVisibility(progress >= max ? GONE : VISIBLE);
    }

    public void hiddleTips() {
        hiddleTips(true);
    }

    public void hiddleTips(boolean isShow) {
        if (rootView != null && isShow) {
            rootView.setVisibility(View.GONE);
        }
    }

    public void hiddleTips(Activity activity) {
        if (rootView == null || activity == null) return;
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        viewGroup.removeView(rootView);
    }

    public ZdTab setEnterAnim(int enterAnim) {
        this.mEnterAnim = enterAnim;
        return this;
    }

    public ZdTab setExitAnim(int exitAnim) {
        this.mExitAnim = exitAnim;
        return this;
    }

    public ZdTab setAnim(boolean isAnim) {
        this.mIsAnim = isAnim;
        return this;
    }

    public int getCount() {
        return fragmentManager != null ? fragmentManager.getBackStackEntryCount() : 0;
    }

    public ZdTab init(FragmentManager fragmentManager) {
        fragmentTabHost.setup(getContext(), fragmentManager, android.R.id.tabcontent);
        return this;
    }

    public ZdTab setFragments(Fragment... fragments) {
        this.mFragments = fragments;
        return this;
    }

    //public ZdTab setFragments(Fragment[] fragments) {
    //  this.mFragments = fragments;
    //  return this;
    //}

    public ZdTab setTitles(String... titles) {
        this.mTitles = titles;
        return this;
    }

    public ZdTab setIcons(int... icons) {
        this.mIcons = icons;
        return this;
    }

    //public ZdTab setIcons(int[] icons) {
    //  this.mIcons = icons;
    //  if (bubbleTabBar != null) {
    //    bubbleTabBar.setIcons(icons);
    //  }
    //  return this;
    //}

    public ZdTab setTags(String... tags) {
        this.mTags = tags;
        return this;
    }

    //public ZdTab setTags(String[] tags) {
    //  this.mTags = tags;
    //  return this;
    //}

    public ZdTab setState(int... states) {
        this.mStates = states;
        return this;
    }

    public ZdTab setUnSelectedColor(int color) {
        this.mUnSelectedColor = color;
        return this;
    }

    public ZdTab setSelectedColor(int color) {
        this.mSelectedColor = color;
        return this;
    }

    public ZdTab setTabViewListener(OnTabViewListener listener) {
        this.mOnTabViewListener = listener;
        return this;
    }

    public ZdTab setTabOnClickListener(OnTabClickListener listener) {
        this.mOnTabClickListener = listener;
        return this;
    }

    public ZdTab setTabLongClickListener(OnTabLongClickListener listener) {
        this.mOnTabLongClickListenerr = listener;
        return this;
    }

    public ZdTab setRefreshEvent(String refreshEvent) {
        this.mRefreshEvent = refreshEvent;
        return this;
    }

    public ZdTab setTab(int tabId) {
        if (validate(tabId)) return this;
        this.index = tabId;
        if (fragmentTabHost != null) {
            fragmentTabHost.setCurrentTab(index);
            setTextColor(index);
        }
        return this;
    }

    public ZdTab setTabIcon(int icon) {
        if (!mImageViews.isEmpty()) {
            ImageView imageView = mImageViews.get(refresgId);
            imageView.setImageResource(mIcons[refresgId]);
            imageView.clearAnimation();
            refresgId = fragmentTabHost.getCurrentTab();
            mRefreshImg = mImageViews.get(refresgId);
            mRefreshImg.setImageResource(icon);
            objectAnimator = Anim.anim(mRefreshImg, 1000L);
            objectAnimator.start();
            ZdEvent.Companion.get().with(mRefreshEvent).post(mRefreshEvent);
        }
        return this;
    }

    public int getIndex() {
        return index;
    }

    public void clearAnim() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        if (mRefreshImg == null) return;
        mRefreshImg.setImageResource(mIcons[refresgId]);
        mRefreshImg.animate().rotation(0).setDuration(0).start();
    }

    public ZdTab create() {
        mImageViews = new ArrayList<>();
        mTextViews = new ArrayList<>();
        mRedDotList = new ArrayList<>();
        int i = 0;
        for (Fragment fragment : mFragments) {
            String title = i + "";
            if (null != mTitles) {
                title = mTitles[i];
            }
            TabHost.TabSpec tabSpec = fragmentTabHost.newTabSpec(title)
                    .setIndicator(
                            null != mOnTabViewListener ? mOnTabViewListener.view(i) : getDefaultView(i));
            fragmentTabHost.addTab(tabSpec, fragment.getClass(), fragment.getArguments());
            fragmentTabHost.getTabWidget().getChildTabViewAt(i).setId(i);
            if (mTags != null && mTags.length > i) {
                fragmentTabHost.getTabWidget().getChildTabViewAt(i).setTag(mTags[i]);
            }
            fragmentTabHost.getTabWidget()
                    .getChildTabViewAt(i)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long currentTime = System.currentTimeMillis();
                            int tabId = v.getId();
                            if (!isLogin && (tabId == 2 || tabId == 3 || tabId == 4)) {
                                if (mOnTabClickListener != null) {
                                    mOnTabClickListener.unUser();
                                }
                                return;
                            }
                            if (validate(tabId)) return;
                            if (currentTime - mLastClickTime > MIN_CLICK_DELAY_TIME) {
                                mLastClickTime = currentTime;
                                if (!mTags[tabId].contains("no")) {
                                    fragmentTabHost.setCurrentTab(v.getId());
                                }
                                mFragments[index].onPause();
                                index = tabId;
                                setTextColor(tabId);
                                if (mOnTabClickListener != null) {
                                    mOnTabClickListener.onTab(tabId);
                                    mFragments[index].onResume();
                                }
                            } else {
                                mOnTabClickListener.DoubleOnClick(tabId, mTags[tabId]);
                            }
                        }
                    });
            int finalI = i;
            fragmentTabHost.getTabWidget()
                    .getChildTabViewAt(i)
                    .setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (mOnTabLongClickListenerr != null) {
                                mOnTabLongClickListenerr.onTabLongClick(v, finalI);
                            }
                            return true;
                        }
                    });
            i++;
        }
        setTextColor(index);
        return this;
    }

    private boolean validate(int tabId) {
        for (int i = 0; i < mStates.length; i++) {
            if (i == tabId && mStates[i] == NO_TAB) {
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onNoTab(tabId);
                    return true;
                }
            }
        }
        return false;
    }

    private View getDefaultView(int tab) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.default_tab_item, null);
        if (null != mIcons) {
            ImageView imageView = view.findViewById(R.id.tabIcon);
            imageView.setImageResource(mIcons[tab]);
            imageView.setVisibility(VISIBLE);
            mImageViews.add(imageView);
        }
        if (null != mTitles) {
            TextView textView = view.findViewById(R.id.tabTxt);
            textView.setText(mTitles[tab]);
            textView.setVisibility(TextUtils.isEmpty(mTitles[tab]) ? GONE : VISIBLE);
            mTextViews.add(textView);
        }
        mRedDotList.add(view.findViewById(R.id.tabRedDot));
        return view;
    }

    private ZdTab setTextColor(int tab) {
        if (mTextViews == null || mTextViews.size() == 0) return this;
        for (TextView textView : mTextViews) {
            textView.setTextColor(mUnSelectedColor);
        }
        mTextViews.get(tab).setTextColor(mSelectedColor);
        return this;
    }

    public ZdTab push(Fragment fragment) {
        return push(fragment, null);
    }

    public ZdTab push(Fragment fragment, String tag) {
        return push(fragment, null, tag);
    }

    public ZdTab push(Fragment fragment, Bundle bundle, String tag) {
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mIsAnim) {
            fragmentTransaction.setCustomAnimations(mEnterAnim, mExitAnim);
        }
        fragmentTransaction.add(R.id.frameLayout, fragment);
        //fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.DESTROYED);
        fragmentTransaction.addToBackStack(fragment.getClass().getCanonicalName());
        fragmentTransaction.commitAllowingStateLoss();
        showFragment(true);

        return this;
    }

    public ZdTab pop() {

        if (fragmentManager != null) {
            fragmentManager.popBackStackImmediate();
        }
        return this;
    }

    /**
     * tag可以为null或者相对应的tag，flags只有0和1(POP_BACK_STACK_INCLUSIVE)两种情况
     * 如果tag为null，flags为0时，弹出回退栈中最上层的那个fragment。
     * 如果tag为null ，flags为1时，弹出回退栈中所有fragment。
     * 如果tag不为null，那就会找到这个tag所对应的fragment，flags为0时，弹出该
     * fragment以上的Fragment，如果是1，弹出该fragment（包括该fragment）以
     * 上的fragment
     */
    public ZdTab pop(String tag, int flags) {
        if (fragmentManager != null) {
            try {
                fragmentManager.popBackStackImmediate(tag, flags);
            } catch (Exception e) {
                LogUtil.e(e);
            }
        }
        return this;
    }

    public ZdTab setDot() {
        return this.setDot(index);
    }

    public ZdTab setDot(int id) {
        return this.setDot(id, true);
    }

    public ZdTab setDot(int id, boolean isShow) {
        return this.setDot(id, -1, isShow);
    }

    public ZdTab setDot(int id, int count) {
        return this.setDot(id, count, true);
    }

    public ZdTab setDot(int id, int count, boolean isShow) {
        if (count >= 10) {
            mRedDotList.get(id).setPadding(15, 10, 15, 10);
        } else if (count > 0 && count < 10) {
            mRedDotList.get(id).setPadding(15, 5, 15, 5);
        } else {
            mRedDotList.get(id).setPadding(10, 2, 10, 2);
        }

        mRedDotList.get(id).setVisibility(isShow ? VISIBLE : GONE);
        mRedDotList.get(id).setText(count > 99 ? "99+" : count > 0 ? count + "" : "");
        return this;
    }

    @Override
    public void onBackStackChanged() {
        showFragment(fragmentManager != null && fragmentManager.getBackStackEntryCount() > 0);
    }

    public void showFragment(boolean isShow) {
        frameLayout.setVisibility(isShow ? VISIBLE : GONE);
        showTabHost(isShow);
    }

    public void showTabHost(boolean isShow) {
        defaultTabBarL.setVisibility(isShow ? GONE : VISIBLE);
    }

    public Boolean isTabHost() {
        return defaultTabBarL.getVisibility() == VISIBLE;
    }

    public interface OnTabClickListener {
        void onTab(int position);

        void onNoTab(int position);

        void DoubleOnClick(int tabId, String tag);

        void unUser();
    }

    public interface OnViewListener {
        void init(View view);
    }

    public interface OnTabViewListener {
        View view(int position);
    }

    public interface OnTabLongClickListener {
        void onTabLongClick(View view, int position);
    }
}
