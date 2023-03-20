//package com.android.resource.view.paneyout;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.slidingpanelayout.widget.SlidingPaneLayout;
//import com.android.resource.R;
//
///**
// * created by jiangshide on 2020/4/13.
// * email:18311271399@163.com
// */
//public class FoldingPaneLayout extends SlidingPaneLayout {
//  protected BaseFoldingLayout foldingNavigationLayout = null;
//
//
//  int mNumberOfFolds;
//
//  public FoldingPaneLayout(Context context) {
//    this(context, null);
//    foldingNavigationLayout = new BaseFoldingLayout(getContext());
//  }
//
//  public FoldingPaneLayout(Context context, AttributeSet attrs) {
//    this(context, attrs, 0);
//
//    initView(context, attrs);
//  }
//
//  public FoldingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
//    super(context, attrs, defStyle);
//
//    initView(context, attrs);
//
//  }
//
//  private void initView(Context context, AttributeSet attrs) {
//    TypedArray ta = context.obtainStyledAttributes(attrs,
//        R.styleable.FoldingMenu);
//    int mFoldNumber = ta.getInt(R.styleable.FoldingMenu_foldNumber,
//        mNumberOfFolds);
//    if (mFoldNumber > 0 && mFoldNumber < 7) {
//      mNumberOfFolds = mFoldNumber;
//    } else {
//      mNumberOfFolds = 2;
//    }
//    ta.recycle();
//
//    foldingNavigationLayout = new BaseFoldingLayout(getContext());
//    foldingNavigationLayout.setNumberOfFolds(mNumberOfFolds);
//    foldingNavigationLayout.setAnchorFactor(0);
//
//  }
//
//  public BaseFoldingLayout getFoldingLayout() {
//    return foldingNavigationLayout;
//  }
//
//  @Override
//  protected void onAttachedToWindow() {
//    super.onAttachedToWindow();
//
//    View child = getChildAt(0);
//    if (child != null) {
//      System.out.println(child);
//
//      removeView(child);
//      foldingNavigationLayout.addView(child);
//      ViewGroup.LayoutParams layPar = child.getLayoutParams();
//      addView(foldingNavigationLayout, 0, layPar);
//
//    }
//
//    setPanelSlideListener(new PanelSlideListener() {
//
//      @Override
//      public void onPanelSlide(View arg0, float mSlideOffset) {
//        if (foldingNavigationLayout != null) {
//          foldingNavigationLayout.setFoldFactor(1-mSlideOffset);
//        }
//
//      }
//
//      @Override
//      public void onPanelOpened(View arg0) {
//
//      }
//
//      @Override
//      public void onPanelClosed(View arg0) {
//
//      }
//    });
//
//  }
//}
