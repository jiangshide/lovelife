package com.android.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * created by jiangshide on 2020-01-22.
 * email:18311271399@163.com
 */
public class ZdTipsView extends LinearLayout {

    private LinearLayout tipsL;
    private FrameLayout tipsLoadingL;
    private ProgressBar tipsLoading;
    private ImageView tipsImg;
    private TextView tipsDes;
    private ZdButton tipsRetry;
    private OnTipsListener mOnTipsListener;

    public ZdTipsView(Context context) {
        super(context);
        init();
    }

    public ZdTipsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZdTipsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = getView(R.layout.default_tips);
        tipsL = view.findViewById(R.id.tipsL);
        tipsLoadingL = view.findViewById(R.id.tipsLoadingL);
        tipsLoading = view.findViewById(R.id.tipsLoading);
        tipsImg = view.findViewById(R.id.tipsImg);
        tipsDes = view.findViewById(R.id.tipsDes);
        tipsRetry = view.findViewById(R.id.tipsRetry);
        tipsRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnTipsListener != null) {
                    mOnTipsListener.onTips(v);
                }
            }
        });
        addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public LinearLayout getRoot() {
        return tipsL;
    }

    public ZdTipsView setTipsImg(int res) {
        if (tipsImg != null) {
            tipsImg.setImageResource(res);
        }
        return this;
    }

    public ZdTipsView setTipsDes(int res) {
        return setTipsDes(getContext().getResources().getString(res));
    }

    public ZdTipsView setTipsDes(String tips) {
        if (tipsDes != null && !TextUtils.isEmpty(tips)) {
            tipsDes.setText(tips);
        }
        return this;
    }

    public ZdTipsView setTipsSize(int size) {
        if (tipsDes != null) {
            tipsDes.setTextSize(size);
        }
        return this;
    }

    public ZdTipsView setTipsColor(int color) {
        if (tipsDes != null) {
            tipsDes.setTextColor(getContext().getResources().getColor(color));
        }
        return this;
    }

    public ZdTipsView setBtnTips(String tips) {
        if (tipsRetry != null) {
            if(!TextUtils.isEmpty(tips)){
                tipsRetry.setText(tips);
            }
        }
        return this;
    }

    public ZdTipsView setListener(OnTipsListener listener) {
        this.mOnTipsListener = listener;
        return this;
    }

    public ZdTipsView hidden() {
        return setStatus(false, false, false, false, false);
    }

    public ZdTipsView loading() {
        return setStatus(true, true, false, false, false);
    }

    public ZdTipsView noData() {
        return setStatus(true, false, true, true, false);
    }

    public ZdTipsView noNet() {
        return setStatus(true, false, true, true, true);
    }

    public ZdTipsView setStatus(boolean isTipsL, boolean isTipsLoadingL, boolean isTipsImg,
                                boolean isTipsDes,
                                boolean isTipsRetry) {
        tipsL.setVisibility(isTipsL ? View.VISIBLE : View.GONE);
        tipsLoadingL.setVisibility(isTipsLoadingL ? View.VISIBLE : View.GONE);
        tipsImg.setVisibility(isTipsImg ? View.VISIBLE : View.GONE);
        tipsDes.setVisibility(isTipsDes ? View.VISIBLE : View.GONE);
        tipsRetry.setVisibility(isTipsRetry ? View.VISIBLE : View.GONE);
        return this;
    }

    private View getView(int layout) {
        return LayoutInflater.from(getContext()).inflate(layout, null);
    }

    public interface OnTipsListener {
        void onTips(View view);
    }
}
