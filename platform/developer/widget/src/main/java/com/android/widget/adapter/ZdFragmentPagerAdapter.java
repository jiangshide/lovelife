package com.android.widget.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.tablayout.ZdTabLayout;
import com.android.tablayout.adapter.CommonNavigatorAdapter;
import com.android.tablayout.help.ViewPagerHelper;
import com.android.tablayout.indicators.LinePagerIndicator;
import com.android.tablayout.interfaces.IPagerIndicator;
import com.android.tablayout.interfaces.IPagerTitleView;
import com.android.tablayout.titles.ColorTransitionPagerTitleView;
import com.android.tablayout.titles.SimplePagerTitleView;
import com.android.tablayout.titles.badge.BadgeAnchor;
import com.android.tablayout.titles.badge.BadgePagerTitleView;
import com.android.tablayout.titles.badge.BadgeRule;
import com.android.utils.Constant;
import com.android.utils.DimenUtil;
import com.android.utils.LogUtil;
import com.android.utils.SPUtil;
import com.android.widget.R;
import com.android.widget.ZdNavigator;
import com.android.widget.ZdViewPager;
import java.util.HashMap;
import java.util.List;

/**
 * created by jiangshide on 2019/2/13.
 * email:18311271399@163.com
 */
//public class CusFragmentPagerAdapter extends FragmentStatePagerAdapter {
public class ZdFragmentPagerAdapter extends FragmentPagerAdapter
    implements ViewPager.OnPageChangeListener {
  private Fragment[] mFragments;
  private String[] mTitles;
  private Bundle mBundle;
  private CommonNavigatorAdapter commonNavigatorAdapter;
  private int mLinePagerIndicator = 0xFFbb2323;
  private ZdViewPager mZdViewPager;
  private int mSelected = -1;
  private int mTxtSelecteColor;
  private int mTxtSelectedColor;
  private int mTxtSelecteSize = 12;
  private int mTxtSelectedSize = 16;
  private boolean mDivider = false;
  private int mMode = LinePagerIndicator.MODE_MATCH_EDGE;
  private boolean mPersistent = true;
  private ZdNavigator zdNavigator = null;
  private HashMap<Integer, Integer> dot = new HashMap();
  private ViewPager.OnPageChangeListener mListener;

  //public ZdFragmentPagerAdapter(FragmentManager fm) {
  //  super(fm);
  //}

  public ZdFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
    super(fm, behavior);
  }

  public ZdFragmentPagerAdapter setFragment(Fragment... fragments) {
    if (fragments == null || fragments.length == 0) return this;
    this.mFragments = null;
    this.mFragments = fragments;
    notifyDataSetChanged();
    return this;
  }

  public ZdFragmentPagerAdapter setFragment(List<? extends Fragment> fragments) {
    if (fragments == null || fragments.size() == 0) return this;
    mFragments = null;
    int size = fragments.size();
    mFragments = new Fragment[size];
    for (int i = 0; i < size; i++) {
      mFragments[i] = fragments.get(i);
    }
    notifyDataSetChanged();
    return this;
  }

  public ZdFragmentPagerAdapter setTitles(String... titles) {
    if (titles == null || titles.length == 0) return this;
    this.mTitles = null;
    this.mTitles = titles;
    notifyDataSetChanged();
    updateTabs();
    return this;
  }

  public ZdFragmentPagerAdapter setTitles(List<String> titles) {
    if (titles == null || titles.size() == 0) return this;
    int size = titles.size();
    mTitles = null;
    mTitles = new String[size];
    for (int i = 0; i < size; i++) {
      mTitles[i] = titles.get(i);
    }
    notifyDataSetChanged();
    updateTabs();
    return this;
  }

  public ZdFragmentPagerAdapter setBundle(Bundle bundle) {
    this.mBundle = bundle;
    return this;
  }

  public ZdFragmentPagerAdapter setDot(int index, int num) {
    dot.put(index, num);
    if (commonNavigatorAdapter != null) {
      commonNavigatorAdapter.notifyDataSetChanged();
    }
    return this;
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return (null != mTitles && mTitles.length >= 0 && position <= mTitles.length)
        ? mTitles[position] : null;
  }

  @Override
  public float getPageWidth(int position) {
    return super.getPageWidth(position);
  }

  @Override
  public Fragment getItem(int position) {
    Fragment fragment = mFragments[position];
    fragment.setArguments(mBundle);
    return fragment;
  }

  @Override
  public int getCount() {
    return mFragments == null ? 0 : mFragments.length;
  }

  public ZdFragmentPagerAdapter initTabs(Context context, ZdTabLayout cusTabLayout,
      final ZdViewPager viewPager, boolean mode) {
    return initTabs(context, cusTabLayout, viewPager, mode, mListener);
  }

  public ZdFragmentPagerAdapter initTabs(Context context, ZdTabLayout cusTabLayout,
      final ZdViewPager viewPager) {
    return initTabs(context, cusTabLayout, viewPager, false, mListener);
  }

  public ZdFragmentPagerAdapter initTabs(Context context, ZdTabLayout cusTabLayout,
      final ZdViewPager viewPager, ViewPager.OnPageChangeListener listener) {
    return initTabs(context, cusTabLayout, viewPager, false, listener);
  }

  public ZdFragmentPagerAdapter updateTabs() {
    if (commonNavigatorAdapter != null) {
      commonNavigatorAdapter.notifyDataSetChanged();
    }
    return this;
  }

  public ZdFragmentPagerAdapter setTxtSelecteColor(int color) {
    this.mTxtSelecteColor = color;
    return this;
  }

  public ZdFragmentPagerAdapter setTxtSelectedColor(int color) {
    this.mTxtSelectedColor = color;
    return this;
  }

  public ZdFragmentPagerAdapter setTxtSelecteSize(int size) {
    this.mTxtSelecteSize = size;
    return this;
  }

  public ZdFragmentPagerAdapter setTxtSelectedSize(int size) {
    this.mTxtSelectedSize = size;
    return this;
  }

  public ZdFragmentPagerAdapter setMode(int mode) {
    this.mMode = mode;
    return this;
  }

  public ZdFragmentPagerAdapter setPersistent(boolean persistent) {
    this.mPersistent = persistent;
    return this;
  }

  public ZdFragmentPagerAdapter setDivider(boolean divider) {
    this.mDivider = divider;
    return this;
  }

  public ZdFragmentPagerAdapter setListener(ViewPager.OnPageChangeListener listener) {
    this.mListener = listener;
    //if (mZdViewPager != null) {
    //  mZdViewPager.addOnPageChangeListener(listener);
    //}
    return this;
  }

  public ZdFragmentPagerAdapter initTabs(Context context, ZdTabLayout zdTabLayout,
      final ZdViewPager viewPager, boolean mode, ViewPager.OnPageChangeListener listener) {
    this.mListener = listener;
    this.mZdViewPager = viewPager;
    if (mPersistent) {
      viewPager.setOffscreenPageLimit(mTitles.length);
    }
    viewPager.addOnPageChangeListener(this);
    zdNavigator = new ZdNavigator(context);
    zdNavigator.invalidate();
    zdNavigator.setAdjustMode(mode);
    zdNavigator.setAdapter(commonNavigatorAdapter = new CommonNavigatorAdapter() {
      @Override
      public int getCount() {
        return (null != mTitles) ? mTitles.length : 0;
      }

      @Override
      public IPagerTitleView getTitleView(Context context, final int index) {
        BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        simplePagerTitleView.setNormalColor(
            mTxtSelecteColor != 0 ? ContextCompat.getColor(context, mTxtSelecteColor)
                : ContextCompat.getColor(context, SPUtil.getInt(
                    Constant.APP_COLOR_FONT_LIGHT, R.color.fontLight)))
            .setSelectedColor(
                mTxtSelectedColor != 0 ? ContextCompat.getColor(context, mTxtSelectedColor)
                    : ContextCompat.getColor(context,
                        SPUtil.getInt(Constant.APP_COLOR_FONT, R.color.font)))
            .setNormalSize(mTxtSelecteSize).setSelectedSize(mTxtSelectedSize);
        if (null != mTitles && mTitles.length >= index) {
          simplePagerTitleView.setText(mTitles[index]);
          simplePagerTitleView.getPaint().setFakeBoldText(true);
        }
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            viewPager.setCurrentItem(index);
            badgePagerTitleView.setBadgeView(null);
          }
        });
        badgePagerTitleView.setInnerPagerTitleView(simplePagerTitleView);

        if (dot.containsKey(index)) {
          int num = dot.get(index);
          if (num > 0) {
            TextView
                textView = (TextView) LayoutInflater.from(context)
                .inflate(R.layout.simple_count_badge_layout, null);
            textView.setText("" + num);
            badgePagerTitleView.setBadgeView(textView);
          }

          badgePagerTitleView.setXBadgeRule(
              new BadgeRule(BadgeAnchor.CONTENT_LEFT, -DimenUtil.dip2px(6.0f)));
          badgePagerTitleView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, 0));

          badgePagerTitleView.setAutoCancelBadge(false);
        }
        return badgePagerTitleView;
      }

      @Override
      public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(mMode);
        linePagerIndicator.setColors(mLinePagerIndicator);
        linePagerIndicator.setRoundRadius(5);
        return linePagerIndicator;
      }
    });

    zdTabLayout.setNavigator(zdNavigator);
    if (mDivider) {
      LinearLayout titleContainer = zdNavigator.getTitleContainer();
      titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
      titleContainer.setDividerPadding(DimenUtil.dip2px(15.0f));
      titleContainer.setDividerDrawable(
          context.getResources().getDrawable(R.drawable.simple_splitter));
    }
    ViewPagerHelper.bind(zdTabLayout, viewPager);
    return this;
  }

  public ZdFragmentPagerAdapter setLinePagerIndicator(int color) {
    this.mLinePagerIndicator = color;
    if (null != commonNavigatorAdapter) commonNavigatorAdapter.notifyDataSetChanged();
    return this;
  }

  public void onRefresh() {
    mFragments[mZdViewPager.getCurrentItem()].onResume();
  }

  public void onResume() {
    if (mFragments != null && mFragments.length >= mSelected) {
      mFragments[mSelected].onResume();
    }
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    if (mListener != null) {
      mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }
    if (mSelected == -1 && mFragments != null) {
      mFragments[position].onResume();
    }
  }

  @Override
  public void onPageSelected(int position) {
    mSelected = position;
    if (mListener != null) {
      mListener.onPageSelected(position);
    }
    if (mFragments != null) {
      mFragments[position].onResume();
    }
  }

  @Override
  public void onPageScrollStateChanged(int state) {
    if (mListener != null) {
      mListener.onPageScrollStateChanged(state);
    }
  }
}
