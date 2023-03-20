package com.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.drawable.DrawableCompat;
import com.android.utils.Constant;
import com.android.utils.SPUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * created by jiangshide on 2019-07-24.
 * email:18311271399@163.com
 */
public class ZdButton extends AppCompatButton {

  private static final String DELIMITERS = "\n";
  private static final int DRAWABLE_LEFT_POSITION = 0;
  private static final int DRAWABLE_TOP_POSITION = 1;
  private static final int DRAWABLE_RIGHT_POSITION = 2;
  private static final int DRAWABLE_BOTTOM_POSITION = 3;
  private static final int DRAWABLES_LENGTH = 4;

  private int mTxtColor;

  //按下颜色
  private int mPressedColor;
  //当前颜色
  private int mNormalColor;
  //当前圆角
  private float mCurrCorner;
  private float mLeftCorner, mTopCorner, mRightCorner, mBottomCorner;
  // 四边宽度
  private float mStrokeWidth;
  // 颜色
  private int mStrokeColor;

  GradientDrawable mGradientDrawable;

  //按钮类型
  private int mType;

  //位置
  private int mPosition;
  private int CENTER = 0;
  private int LEft = 1;
  private int TOP = 2;
  private int RIGHT = 3;
  private int BOTTOM = 4;

  boolean mIsTouchPass = true;

  public boolean selected;

  private Rect textBoundsRect;
  @ColorInt
  private int mTintColor = Color.TRANSPARENT;
  private int mLeftPadding;
  private int mRightPadding;
  private int mDrawableSize;

  public ZdButton(Context context) {
    this(context, null);
  }

  public ZdButton(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ZdButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttrs(attrs);
    init();
  }

  private void init() {
    setGravity(mPosition == LEft ? Gravity.LEFT : mPosition == TOP ? Gravity.TOP
        : mPosition == RIGHT ? Gravity.RIGHT
            : mPosition == BOTTOM ? Gravity.BOTTOM : Gravity.CENTER);
    mGradientDrawable = new GradientDrawable();
    //说明配置了快速按钮选项
    if (mType != 0) {
      setTextColor(Color.WHITE);
      switch (mType) {
        case 1:
          mNormalColor = Color.parseColor("#5CB85C");
          mPressedColor = Color.parseColor("#449D44");
          break;
        case 2:
          mNormalColor = Color.parseColor("#5BC0DE");
          mPressedColor = Color.parseColor("#31B0D5");
          break;
        case 3:
          mNormalColor = Color.parseColor("#F0AD4E");
          mPressedColor = Color.parseColor("#EC971F");
          break;
        case 4:
          mNormalColor = Color.parseColor("#D9534F");
          mPressedColor = Color.parseColor("#C9302C");
          break;
      }
    }

    mGradientDrawable.setColor(mNormalColor);
    mGradientDrawable.setStroke((int) mStrokeWidth, mStrokeColor);
    if (mLeftCorner > 0 || mTopCorner > 0 || mRightCorner > 0 || mBottomCorner > 0) {
      mGradientDrawable.setCornerRadii(new float[] {
          mLeftCorner, mLeftCorner, mTopCorner, mTopCorner, mRightCorner, mRightCorner,
          mBottomCorner, mBottomCorner
      });
    } else {
      mGradientDrawable.setCornerRadius(mCurrCorner);
    }
    setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View arg0, MotionEvent event) {
        setBackgroundDrawable(mGradientDrawable);
        return setColor(event.getAction());
      }
    });
    setBackgroundDrawable(mGradientDrawable);
  }

  private void initAttrs(AttributeSet attrs) {
    if (attrs != null) {
      TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ButtonStyle);
      int color = getResources().getColor(R.color.bg);
      mTxtColor = typedArray.getColor(R.styleable.ButtonStyle_txt_color,color);
      mNormalColor = typedArray.getColor(R.styleable.ButtonStyle_normal_color, color);
      mStrokeWidth = typedArray.getDimension(R.styleable.ButtonStyle_stroke, 0);
      mStrokeColor = typedArray.getColor(R.styleable.ButtonStyle_stroke_color, Color.TRANSPARENT);
      mPressedColor = typedArray.getColor(R.styleable.ButtonStyle_press_color,
          getResources().getColor(R.color.gray));
      mCurrCorner = typedArray.getDimension(R.styleable.ButtonStyle_corner,
          getResources().getDimension(R.dimen.default_corner));
      mLeftCorner = typedArray.getDimension(R.styleable.ButtonStyle_leftCorner, 0);
      mTopCorner = typedArray.getDimension(R.styleable.ButtonStyle_topCorner, 0);
      mRightCorner = typedArray.getDimension(R.styleable.ButtonStyle_rightCorner, 0);
      mBottomCorner = typedArray.getDimension(R.styleable.ButtonStyle_bottomCorner, 0);
      mType = typedArray.getInt(R.styleable.ButtonStyle_type, 0);
      mPosition = typedArray.getInt(R.styleable.ButtonStyle_position, CENTER);
      mTintColor = typedArray.getColor(R.styleable.ButtonStyle_drawableTint, Color.TRANSPARENT);
      mDrawableSize = typedArray.getDimensionPixelSize(R.styleable.ButtonStyle_drawableSize, -1);

      float defaultDrawablePadding = getResources().getDimension(R.dimen.default_drawable_padding);
      int drawablePadding =
          (int) typedArray.getDimension(R.styleable.ButtonStyle_android_drawablePadding,
              defaultDrawablePadding);
      setCompoundDrawablePadding(drawablePadding);

      updateDrawables();
      typedArray.recycle();
      //mTxtColor = getResources().getColor(SPUtil.getInt(Constant.APP_COLOR_FONT,R.color.font));
      //setTextColor(mTxtColor);
    }
    mLeftPadding = getPaddingLeft();
    mRightPadding = getPaddingRight();
  }

  @Override
  public void setOnClickListener(OnClickListener l) {
    super.setOnClickListener(l);
    mIsTouchPass = false;
  }

  public boolean setColor(int action) {
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mGradientDrawable.setColor(mPressedColor);
        break;
      case MotionEvent.ACTION_UP:
        mGradientDrawable.setColor(mNormalColor);
        break;
      case MotionEvent.ACTION_CANCEL:
        mGradientDrawable.setColor(mNormalColor);
        break;
    }

    return mIsTouchPass;
  }

  /**
   * @return 获取按下颜色
   */
  public int getPressedColor() {
    return mPressedColor;
  }

  /**
   * @param pressedColor 按下颜色设置
   */
  public ZdButton setPressedColor(int pressedColor) {
    if (pressedColor == 0) return this;
    this.mPressedColor = getResources().getColor(pressedColor);
    return this;
  }

  /**
   * @param pressedColor 设置按下颜色 例如：#ffffff
   */
  public ZdButton setPressedColor(String pressedColor) {
    if (TextUtils.isEmpty(pressedColor)) return this;
    this.mPressedColor = Color.parseColor(pressedColor);
    return this;
  }

  /**
   * @return 获取默认颜色
   */
  public int getNormalColor() {
    return mNormalColor;
  }

  /**
   * @param normalColor 设置默认颜色
   */
  public ZdButton setNormalColor(int normalColor) {
    if (normalColor == 0) return this;
    this.mNormalColor = getResources().getColor(normalColor);
    if (mGradientDrawable != null) {
      mGradientDrawable.setColor(this.mNormalColor);
    }
    return this;
  }

  /**
   * 设置随机颜色
   */
  public int setRandomColor() {
    Random random = new Random();
    int ranColor = 0xff000000 | random.nextInt(0x00ffffff);
    this.mNormalColor = ranColor;
    if (mGradientDrawable != null) {
      mGradientDrawable.setColor(this.mNormalColor);
    }
    return ranColor;
  }

  /**
   * @param normalColor 设置默认颜色 例如：#ffffff
   */
  public ZdButton setNormalColor(String normalColor) {
    if (TextUtils.isEmpty(normalColor)) return this;
    this.mNormalColor = Color.parseColor(normalColor);
    if (mGradientDrawable != null) {
      mGradientDrawable.setColor(this.mNormalColor);
    }
    return this;
  }

  /**
   * @return 返回当前圆角大小
   */
  public float getCurrCorner() {
    return mCurrCorner;
  }

  /**
   * @param currCorner 设置当前圆角
   */
  public ZdButton setCurrCorner(float currCorner) {
    if (currCorner == 0) return this;
    this.mCurrCorner = currCorner;
    if (mGradientDrawable != null) {
      mGradientDrawable.setCornerRadius(currCorner);
    }
    return this;
  }

  /**
   * @param currCorner 圆角设置,必须为四个参数:左上,右上,左下,右下
   */
  public ZdButton setCurrCorner(float... currCorner) {
    if (currCorner == null || currCorner.length == 0) return this;
    if (currCorner != null && currCorner.length == 4) {
      this.mLeftCorner = currCorner[0];
      this.mTopCorner = currCorner[1];
      this.mRightCorner = currCorner[2];
      this.mBottomCorner = currCorner[3];
      init();
    }
    return this;
  }

  /**
   * @return 返回边框大小
   */
  public float getStrokeWidth() {
    return mStrokeWidth;
  }

  /**
   * @param strokeWidth 设置边框大小
   */
  public ZdButton setStrokeWidth(float strokeWidth) {
    if (strokeWidth == 0) return this;
    this.mStrokeWidth = strokeWidth;
    if (mGradientDrawable != null) {
      mGradientDrawable.setStroke((int) strokeWidth, this.mStrokeColor);
    }
    return this;
  }

  /**
   * @return 返回边框颜色
   */
  public int getStrokeColor() {
    return mStrokeColor;
  }

  /**
   * @param strokeColor 设置边框颜色
   */
  public ZdButton setStrokeColor(int strokeColor) {
    if (strokeColor == 0) return this;
    this.mStrokeColor = getResources().getColor(strokeColor);
    if (mGradientDrawable != null) {
      mGradientDrawable.setStroke((int) mStrokeWidth, this.mStrokeColor);
    }
    return this;
  }

  /**
   * @param strokeColor 设置边框颜色 例如：#ffffff
   */
  public ZdButton setStrokeColor(String strokeColor) {
    if (TextUtils.isEmpty(strokeColor)) return this;
    this.mStrokeColor = Color.parseColor(strokeColor);
    if (mGradientDrawable != null) {
      mGradientDrawable.setStroke((int) mStrokeWidth, this.mStrokeColor);
    }
    return this;
  }

  public ZdButton setWeight(int weight) {
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, weight);
    setLayoutParams(params);
    return this;
  }

  private Drawable getDrawable(int icon) {
    Drawable drawable = getResources().getDrawable(icon);
    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    return drawable;
  }

  public void drawableLeft(int icon) {
    setCompoundDrawables(getDrawable(icon), null, null, null);
  }

  public void drawableTop(int icon) {
    setCompoundDrawables(null, getDrawable(icon), null, null);
  }

  public void drawableRight(int icon) {
    setCompoundDrawables(null, null, getDrawable(icon), null);
  }

  public void drawableBottom(int icon) {
    setCompoundDrawables(null, null, null, getDrawable(icon));
  }

  private void updateDrawables() {
    if (mTintColor != Color.TRANSPARENT || mDrawableSize != -1) {
      Drawable[] drawables = getCompoundDrawables();
      if (drawables.length != DRAWABLES_LENGTH) return;

      Drawable[] wrappedDrawables = new Drawable[DRAWABLES_LENGTH];
      for (int i = 0; i < DRAWABLES_LENGTH; i++) {
        Drawable drawable = drawables[i];
        if (drawable != null) {
          Drawable wrappedDrawable = drawable;
          if (mTintColor != Color.TRANSPARENT) {
            wrappedDrawable = getTintedDrawable(wrappedDrawable);
          }
          if (mDrawableSize > 0) {
            wrappedDrawable = updateDrawableBounds(wrappedDrawable);
          }
          wrappedDrawables[i] = wrappedDrawable;
        }
      }
      if (mDrawableSize > 0) {
        setCompoundDrawables(wrappedDrawables[DRAWABLE_LEFT_POSITION],
            wrappedDrawables[DRAWABLE_TOP_POSITION],
            wrappedDrawables[DRAWABLE_RIGHT_POSITION],
            wrappedDrawables[DRAWABLE_BOTTOM_POSITION]);
      } else {
        setCompoundDrawablesWithIntrinsicBounds(wrappedDrawables[DRAWABLE_LEFT_POSITION],
            wrappedDrawables[DRAWABLE_TOP_POSITION],
            wrappedDrawables[DRAWABLE_RIGHT_POSITION],
            wrappedDrawables[DRAWABLE_BOTTOM_POSITION]);
      }
    }
  }

  @NonNull
  private Drawable getTintedDrawable(@NonNull Drawable drawable) {
    Drawable mutate = DrawableCompat.wrap(drawable).mutate();
    DrawableCompat.setTint(mutate, mTintColor);
    return mutate;
  }

  @NonNull
  private Drawable updateDrawableBounds(@NonNull Drawable drawable) {
    drawable.getBounds().set(0, 0, mDrawableSize, mDrawableSize);
    return drawable;
  }

  @Override
  public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top,
      @Nullable Drawable right, @Nullable Drawable bottom) {
    super.setCompoundDrawables(left, top, right, bottom);
    updatePadding();
  }

  @Override
  public void setCompoundDrawablesWithIntrinsicBounds(@Nullable Drawable left,
      @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
    super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    updatePadding();
  }

  @Override
  public void setText(CharSequence text, BufferType type) {
    super.setText(text, type);
    updatePadding();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    updatePadding(w);
  }

  private void updatePadding() {
    updatePadding(getMeasuredWidth());
  }

  @Override
  public void setPadding(int left, int top, int right, int bottom) {
    super.setPadding(left, top, right, bottom);
    mLeftPadding = left;
    mRightPadding = right;
    updatePadding();
  }

  private void updatePadding(int width) {
    if (width == 0) return;

    Drawable[] compoundDrawables = getCompoundDrawables();
    if (compoundDrawables.length != DRAWABLES_LENGTH) return;

    Drawable leftDrawable = compoundDrawables[DRAWABLE_LEFT_POSITION];
    Drawable rightDrawable = compoundDrawables[DRAWABLE_RIGHT_POSITION];
    if (leftDrawable == null && rightDrawable == null) return;

    int textWidth = getTextWidth();
    int iconPadding = Math.max(getCompoundDrawablePadding(), 1);
    int paddingSize;

    int leftWidth = leftDrawable == null ? 0 : leftDrawable.getBounds().width();
    int rightWidth = rightDrawable == null ? 0 : rightDrawable.getBounds().width();

    if (leftDrawable != null && rightDrawable != null) {
      paddingSize = (width - leftWidth - rightWidth - textWidth - iconPadding * 4) / 2;
    } else if (leftDrawable != null) {
      paddingSize = mPosition == LEft ? 0 : (width - leftWidth - iconPadding * 2 - textWidth) / 2;
    } else {
      paddingSize = (width - rightWidth - iconPadding * 2 - textWidth) / 2;
    }

    super.setPadding(Math.max(mLeftPadding, paddingSize), getPaddingTop(),
        Math.max(paddingSize, mRightPadding), getPaddingBottom());
  }

  private int getTextWidth() {
    if (textBoundsRect == null) {
      textBoundsRect = new Rect();
    }
    Paint paint = getPaint();
    String text = divideText();
    paint.getTextBounds(text, 0, text.length(), textBoundsRect);
    return textBoundsRect.width();
  }

  private String divideText() {
    String text = getText().toString();
    if (text.isEmpty()) {
      return "";
    }
    List<String> list = new ArrayList<>();
    StringTokenizer tokenizer = new StringTokenizer(text, DELIMITERS, false);
    while (tokenizer.hasMoreTokens()) {
      list.add(tokenizer.nextToken());
    }
    if (list.size() == 1) {
      return isAllCaps() ? list.get(0).toUpperCase() : list.get(0);
    }
    String longPart = list.get(0);
    for (int i = 0; i < list.size() - 1; i++) {
      if (list.get(i + 1).length() > list.get(i).length()) {
        longPart = list.get(i + 1);
      }
    }

    return isAllCaps() ? longPart.toUpperCase() : longPart;
  }

  public boolean isAllCaps() {
    TransformationMethod method = getTransformationMethod();
    if (method == null) return false;

    return method.getClass().getSimpleName().equals("AllCapsTransformationMethod");
  }
}
