package com.android.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

/**
 * created by jiangshide on 2016-09-02.
 * email:18311271399@163.com
 */
public class BubbleUtil {

  public static void elastic(final ScrollView scrollView, final int padding) {
    View child = scrollView.getChildAt(0);
    final int oldpt = child.getPaddingTop();
    final int oldpb = child.getPaddingBottom();
    child.setPadding(child.getPaddingLeft(), padding + oldpt, child.getPaddingRight(),
        padding + oldpb);

    scrollView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          private boolean inTouch = false;

          @SuppressLint("NewApi")
          private void disableOverScroll() {
            scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
          }

          private void scrollToTop() {
            scrollView.smoothScrollTo(scrollView.getScrollX(), padding - oldpt);
          }

          private void scrollToBottom() {
            scrollView.smoothScrollTo(scrollView.getScrollX(),
                scrollView.getChildAt(0).getBottom() - scrollView.getMeasuredHeight() - padding
                    + oldpb);
          }

          private final Runnable checkStopped = new Runnable() {
            @Override
            public void run() {
              int y = scrollView.getScrollY();
              int bottom =
                  scrollView.getChildAt(0).getBottom() - y - scrollView.getMeasuredHeight();
              if (y <= padding && !inTouch) {
                scrollToTop();
              } else if (bottom <= padding && !inTouch) {
                scrollToBottom();
              }
            }
          };

          @SuppressWarnings("deprecation")
          @Override
          public void onGlobalLayout() {
            scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
              disableOverScroll();
            }

            scrollView.setOnTouchListener(new View.OnTouchListener() {
              @Override
              public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
                  inTouch = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                  inTouch = false;
                  scrollView.post(checkStopped);
                }
                return false;
              }
            });

            scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                  @Override
                  public void onScrollChanged() {
                    if (!inTouch
                        && scrollView != null
                        && scrollView.getHandler() != null) {//如果持续滚动,移除checkStopped,停止滚动以后只执行一次检测任务
                      scrollView.getHandler().removeCallbacks(checkStopped);
                      scrollView.postDelayed(checkStopped, 100);
                    }
                  }
                });

            scrollView.postDelayed(checkStopped, 300);
          }
        });
  }

  public static void elastic(HorizontalScrollView scrollView) {
    elastic(scrollView, 200);
  }

  public static void elastic(final HorizontalScrollView scrollView, final int padding) {
    View child = scrollView.getChildAt(0);

    final int oldpt = child.getPaddingTop();
    final int oldpb = child.getPaddingBottom();
    child.setPadding(padding + oldpt, child.getPaddingTop(), padding + oldpb,
        child.getPaddingBottom());

    scrollView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          private boolean inTouch = false;

          @SuppressLint("NewApi")
          private void disableOverScroll() {
            scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
          }

          private void scrollToLeft() {
            scrollView.smoothScrollTo(padding - oldpt, scrollView.getScrollY());
          }

          private void scrollToRight() {
            scrollView.smoothScrollTo(
                scrollView.getChildAt(0).getRight() - scrollView.getMeasuredWidth() - padding
                    + oldpb, scrollView.getScrollY());
          }

          private final Runnable checkStopped = new Runnable() {
            @Override
            public void run() {
              int x = scrollView.getScrollX();
              int bottom = scrollView.getChildAt(0).getRight() - x - scrollView.getMeasuredWidth();
              if (x <= padding && !inTouch) {
                scrollToLeft();
              } else if (bottom <= padding && !inTouch) {
                scrollToRight();
              }
            }
          };

          @SuppressWarnings("deprecation")
          @Override
          public void onGlobalLayout() {
            scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
              disableOverScroll();
            }

            scrollView.setOnTouchListener(new View.OnTouchListener() {
              @Override
              public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
                  inTouch = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                  inTouch = false;
                  scrollView.post(checkStopped);
                }
                return false;
              }
            });

            scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                  @Override
                  public void onScrollChanged() {
                    if (!inTouch && scrollView != null && scrollView.getHandler() != null) {
                      scrollView.getHandler().removeCallbacks(checkStopped);
                      scrollView.postDelayed(checkStopped, 100);
                    }
                  }
                });

            scrollView.postDelayed(checkStopped, 300);
          }
        });
  }
}
