package com.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jiangshide
 * @Created by Ender on 2018/9/11.
 * @Emal:18311271399@163.com
 */
public class TagView extends ViewGroup {
  private List<Line> mLines = new LinkedList<Line>();    // 用来记录布局中有多少个行
  private Line mCurrentLine;                                   //当前行
  private int mHorizontalSpace = 60;                              //控件之间的间隙
  private int mVerticalSpace = 60;                                   //行之间的间隙

  //按下颜色
  private int mPressedColor;
  //当前颜色
  private int mNormalColor;
  //当前圆角
  private float mCurrCorner = 90;
  private float mLeftCorner, mTopCorner, mRightCorner, mBottomCorner;
  // 四边宽度
  private float mStrokeWidth;
  // 颜色
  private int mStrokeColor;
  //文字正常颜色
  private int mTxtNormalColor;
  //文字选中时颜色
  private int mTxtSelectedColor;
  //随机颜色
  private boolean mRandomColor;

  private List<ZdButton> btns;
  private List<Boolean> bools;
  private TagOnListener mTagOnListener;

  public TagView(Context context) {
    this(context, null);
  }

  public TagView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    mLines.clear();
    mCurrentLine = null;
    int width = MeasureSpec.getSize(widthMeasureSpec);

    int maxWidth = width - getPaddingLeft() - getPaddingRight();

    for (int i = 0; i < getChildCount(); i++) {

      View child = getChildAt(i);
      if (child.getVisibility() == View.GONE) {
        continue;
      }
      measureChild(child, widthMeasureSpec, heightMeasureSpec);
      if (mCurrentLine == null) {
        mCurrentLine = new Line(maxWidth, mHorizontalSpace);
        mCurrentLine.addView(child);
        mLines.add(mCurrentLine);
      } else {
        if (mCurrentLine.canAdd(child)) {
          mCurrentLine.addView(child);
        } else {
          mCurrentLine = new Line(maxWidth, mHorizontalSpace);
          mLines.add(mCurrentLine);
          mCurrentLine.addView(child);
        }
      }
    }
    int measuredHeight = getPaddingTop() + getPaddingBottom();

    for (int i = 0; i < mLines.size(); i++) {
      measuredHeight += mLines.get(i).height;
    }
    measuredHeight = measuredHeight + (mLines.size() - 1) * mVerticalSpace;
    setMeasuredDimension(width, measuredHeight);
  }

  public TagView setHorizontalSpace(int horizontalSpace) {
    this.mHorizontalSpace = horizontalSpace;
    invalidate();
    return this;
  }

  public TagView setVerticalSpace(int verticalSpace) {
    this.mVerticalSpace = verticalSpace;
    invalidate();
    return this;
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int marginTop = getPaddingTop();
    int marginLeft = getPaddingLeft();
    for (int i = 0; i < mLines.size(); i++) {
      Line line = mLines.get(i);
      line.layout(marginLeft, marginTop);
      marginTop += mVerticalSpace + line.height;
    }
  }

  /**
   * add the single buttons
   */
  public TagView setDatas(String... datas) {
    for (int i = 0; i < datas.length; i++) {
      setData(i, datas[i]);
    }
    return this;
  }

  public TagView setDatas(ArrayList<String> datas) {
    int size = datas.size();
    for (int i = 0; i < size; i++) {
      setData(i, datas.get(i));
    }
    return this;
  }

  public ZdButton setData(String str) {
    return this.setData(0, str);
  }

  /**
   * add the single button
   */
  public ZdButton setData(int id, String str) {
    ZdButton zdButton = new ZdButton(getContext());
    zdButton.setTextColor(
        getResources().getColor(mTxtNormalColor == 0 ? R.color.white : mTxtNormalColor));
    zdButton.setText(str);
    zdButton.setPressedColor(mPressedColor);
    if (mRandomColor) {
      zdButton.setRandomColor();
    } else {
      zdButton.setNormalColor(mNormalColor);
    }
    zdButton.setPadding(20,10,20,10);
    zdButton.setStrokeWidth(mStrokeWidth);
    zdButton.setStrokeColor(mStrokeColor);
    if (mCurrCorner > 0) {
      zdButton.setCurrCorner(mCurrCorner);
    } else {
      zdButton.setCurrCorner(this.mLeftCorner, this.mTopCorner, this.mRightCorner,
          this.mBottomCorner);
    }
    if (id != -1) {
      zdButton.setId(id);
    }
    zdButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        if (mTagOnListener != null) {
          mTagOnListener.onTag(zdButton,id, str);
        }
      }
    });
    addView(zdButton);
    if (btns == null) {
      bools = null;
      bools = new ArrayList<>();
      btns = new ArrayList<>();
      btns.add(zdButton);
      bools.add(false);
    }
    return zdButton;
  }

  public TagView setTxtNormalColor(int color) {
    this.mTxtNormalColor = color;
    return this;
  }

  public TagView setTxtSelectedColor(int color) {
    this.mTxtSelectedColor = color;
    return this;
  }

  /**
   * @param pressedColor 按下颜色设置
   */
  public TagView setPressedColor(int pressedColor) {
    this.mPressedColor = pressedColor;
    return this;
  }

  /**
   * @param normalColor 设置默认颜色
   */
  public TagView setNormalColor(int normalColor) {
    this.mNormalColor = normalColor;
    return this;
  }

  /**
   * @param currCorner 设置当前圆角
   */
  public TagView setCurrCorner(float currCorner) {
    this.mCurrCorner = currCorner;
    return this;
  }

  /**
   * @param currCorner 圆角设置,必须为四个参数:左上,右上,左下,右下
   */
  public TagView setCurrCorner(float... currCorner) {
    if (currCorner != null && currCorner.length == 4) {
      this.mLeftCorner = currCorner[0];
      this.mTopCorner = currCorner[1];
      this.mRightCorner = currCorner[2];
      this.mBottomCorner = currCorner[3];
    }
    return this;
  }

  /**
   * @param strokeWidth 设置边框大小
   */
  public TagView setStrokeWidth(float strokeWidth) {
    this.mStrokeWidth = strokeWidth;
    return this;
  }

  /**
   * @param strokeColor 设置边框颜色
   */
  public TagView setStrokeColor(int strokeColor) {
    this.mStrokeColor = strokeColor;
    return this;
  }

  public TagView setRandomColor(boolean randomColor) {
    this.mRandomColor = randomColor;
    return this;
  }

  public TagView setListener(TagOnListener listener) {
    this.mTagOnListener = listener;
    return this;
  }

  /**
   * 用来记录每行控件的摆放
   */
  private class Line {
    private List<View> mViews = new ArrayList<View>();
    private int maxWidth;
    private int space;
    private int usedWidth;
    public int height;

    public Line(int maxWidth, int horizontalSpace) {
      this.maxWidth = maxWidth;
      this.space = horizontalSpace;
    }

    /**
     * 添加子控件
     */
    public void addView(View child) {
      int childWidth = child.getMeasuredWidth();
      int childHeight = child.getMeasuredHeight();
      if (mViews.size() == 0) {
        if (childWidth > maxWidth) {
          usedWidth = maxWidth;
          height = childHeight;
        } else {
          usedWidth = childWidth;
          height = childHeight;
        }
      } else {
        usedWidth = usedWidth + space + childWidth;
        height = (childHeight > height) ? childHeight : height;
      }
      mViews.add(child);
    }

    /**
     * 判断是否可以添加子控件
     */
    public boolean canAdd(View child) {
      int width = child.getMeasuredWidth();
      if (mViews.size() == 0) {
        return true;
      }
      if (usedWidth + width + space > maxWidth) {
        return false;
      } else {
        return true;
      }
    }

    /**
     * 摆放行
     */
    public void layout(int marginLeft, int marginTop) {
      int extraWidth = maxWidth - usedWidth;
      int avgWidth = (int) (extraWidth * 1f / mViews.size() + 0.5f);
      for (int i = 0; i < mViews.size(); i++) {

        View view = mViews.get(i);

        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();

        if (avgWidth > 0) {
          int specWidth = MeasureSpec.makeMeasureSpec(viewWidth + avgWidth, MeasureSpec.EXACTLY);
          int specHeight = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
          view.measure(specWidth, specHeight);
          viewWidth = view.getMeasuredWidth();
          viewHeight = view.getMeasuredHeight();
        }

        int extraTop = (int) ((height - viewHeight) / 2f + 0.5);

        int left = marginLeft;
        int top = marginTop + extraTop;
        int right = left + viewWidth;
        int bottom = top + viewHeight;
        view.layout(left, top, right, bottom);
        marginLeft += viewWidth + space;
      }
    }
  }

  public interface TagOnListener {
    void onTag(ZdButton zdButton,int position, String data);
  }
}
