package com.android.tablayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.android.tablayout.interfaces.IPagerNavigator;
import com.android.utils.Constant;
import com.android.utils.LogUtil;
import com.android.utils.SPUtil;

/**
 * created by jiangshide on 2018-08-05.
 * email:18311271399@163.com
 */
public class ZdTabLayout extends FrameLayout {

    private IPagerNavigator mNavigator;

    public ZdTabLayout(Context context) {
        super(context);
    }

    public ZdTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mNavigator != null) {
            mNavigator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageSelected(int position) {
        if (mNavigator != null) {
            mNavigator.onPageSelected(position);
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (mNavigator != null) {
            mNavigator.onPageScrollStateChanged(state);
        }
    }

    public IPagerNavigator getNavigator() {
        return mNavigator;
    }

    public void setNavigator(IPagerNavigator navigator) {
        if (mNavigator == navigator) {
            return;
        }
        if (mNavigator != null) {
            mNavigator.onDetachFromMagicIndicator();
        }
        mNavigator = navigator;
        removeAllViews();
        //if (mNavigator instanceof View) {
        //    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView((View) mNavigator);
        //    ((View) mNavigator).setPadding(15,5,15,5);
            mNavigator.onAttachToMagicIndicator();
        //}
    }
}
