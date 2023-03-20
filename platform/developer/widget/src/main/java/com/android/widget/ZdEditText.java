package com.android.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.MetricAffectingSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import com.android.utils.AppUtil;
import com.android.utils.Constant;
import com.android.utils.LogUtil;
import com.android.utils.SPUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.HashMap;

/**
 * created by jiangshide on 2019-07-24.
 * email:18311271399@163.com
 */
public class ZdEditText extends AppCompatEditText
    implements View.OnFocusChangeListener, TextWatcher, ViewTreeObserver.OnGlobalLayoutListener {
  private Drawable mClearDrawable;
  private boolean mHasFoucs;
  private int mRequestType;
  private int mCacheType;
  private String mAction;
  private HashMap<String, String> mParams;
  private int mLength;
  public final int MOBILE = 1, ID_CARD = 2, BRAND_CARD = 3;
  private int mStart, mEnd;
  private int mFormat;
  private boolean mIsDelete;
  private CusEditListener mCusEditListener;
  private KeyboardListener mKeyboardListener;
  private OnFocusListener mOnFocusListener;

  //当前圆角
  private float mCurrCorner;

  //当前颜色
  private int mNormalColor;

  // 四边宽度
  private float mStrokeWidth;
  // 颜色
  private int mStrokeColor;

  GradientDrawable mGradientDrawable;

  private HashMap<String, MyTextSpan> hashMap = new HashMap<>();

  private StringBuilder builder;

  public ZdEditText(Context context) {
    this(context, null);
  }

  public ZdEditText(Context context, AttributeSet attrs) {
    this(context, attrs, android.R.attr.editTextStyle);
  }

  public ZdEditText(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CusEditText, 0, 0);
    if (array != null) {
      int color = getResources().getColor(R.color.blackLightMiddle);
      mFormat = array.getInteger(R.styleable.CusEditText_format, 0);
      mStart = array.getInteger(R.styleable.CusEditText_formatStart, 0);
      mEnd = array.getInteger(R.styleable.CusEditText_formatEnd, 0);
      mIsDelete = array.getBoolean(R.styleable.CusEditText_isDelete, true);
      mCurrCorner = array.getDimension(R.styleable.CusEditText_corners, 0);
      mNormalColor = array.getColor(R.styleable.CusEditText_normal_colors, color);
      mStrokeWidth = array.getDimension(R.styleable.CusEditText_strokes, 0);
      mStrokeColor = array.getColor(R.styleable.CusEditText_stroke_colors, Color.TRANSPARENT);
      setHintTextColor(ContextCompat.getColor(getContext(),R.color.fontLight));
      //setTextColor(ContextCompat.getColor(getContext(),R.color.font));
      array.recycle();
    }
    if (mFormat > 0) {
      format(mFormat);
    } else {
      format(mStart, mEnd);
    }
    init();
    //setTransformationMethod(new AsteriskPasswordTransformationMethod());
    getViewTreeObserver().addOnGlobalLayoutListener(this);
  }

  public void format(int format) {
    format(format, 0, 0);
  }

  public void format(int start, int end) {
    format(0, start, end);
  }

  private void format(int format, int start, int end) {
    switch (format) {
      case MOBILE:
        mStart = 2;
        mEnd = 7;
        break;
      case ID_CARD:
        mStart = 3;
        mEnd = 11;
        break;
      case BRAND_CARD:
        mStart = 2;
        mEnd = 14;
        break;
      default:
        mStart = start;
        mEnd = end;
        break;
    }
  }

  public ZdEditText setRequestSize(int length) {
    this.mLength = length;
    return this;
  }

  public ZdEditText setListener(CusEditListener listener) {
    this.mCusEditListener = listener;
    return this;
  }

  public ZdEditText setKetListener(KeyboardListener listener) {
    this.mKeyboardListener = listener;
    return this;
  }

  int rootViewVisibleHeight = 0;//纪录根视图的显示高度

  public ZdEditText setKeyBoardListener(Activity activity, OnKeyboardListener listener) {
    View rootView = activity.getWindow().getDecorView();
    rootView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            //获取当前根视图在屏幕上显示的大小
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);

            int visibleHeight = r.height();
            System.out.println("" + visibleHeight);
            if (rootViewVisibleHeight == 0) {
              rootViewVisibleHeight = visibleHeight;
              return;
            }

            //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
            if (rootViewVisibleHeight == visibleHeight) {
              return;
            }

            //根视图显示高度变小超过200，可以看作软键盘显示了
            if (rootViewVisibleHeight - visibleHeight > 200) {
              if (listener != null) {
                listener.show(rootViewVisibleHeight - visibleHeight);
              }
              rootViewVisibleHeight = visibleHeight;
              return;
            }

            //根视图显示高度变大超过200，可以看作软键盘隐藏了
            if (visibleHeight - rootViewVisibleHeight > 200) {
              if (listener != null) {
                listener.hide(visibleHeight - rootViewVisibleHeight);
              }
              rootViewVisibleHeight = visibleHeight;
              return;
            }
          }
        });
    return this;
  }

  public ZdEditText setFocusListener(OnFocusListener listener) {
    this.mOnFocusListener = listener;
    return this;
  }

  private void init() {
    // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片,获取图片的顺序是左上右下（0,1,2,3,）
    mClearDrawable = getCompoundDrawables()[2];
    if (mClearDrawable == null) {
      mClearDrawable = getResources().getDrawable(
          R.drawable.input_delete);
    }

    mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
        mClearDrawable.getIntrinsicHeight());

    // 默认设置隐藏图标
    setClearIconVisible(false);
    // 设置焦点改变的监听
    setOnFocusChangeListener(this);
    // 设置输入框里面内容发生改变的监听
    addTextChangedListener(this);
    mGradientDrawable = new GradientDrawable();

    mGradientDrawable.setColor(mNormalColor);
    mGradientDrawable.setCornerRadius(mCurrCorner);
    if (mCurrCorner > 0) {
      setBackgroundDrawable(mGradientDrawable);
    }
    if (mStrokeWidth > 0) {
      mGradientDrawable.setStroke((int) mStrokeWidth, mStrokeColor);
    }
  }

  /* @说明：isInnerWidth, isInnerHeight为ture，触摸点在删除图标之内，则视为点击了删除图标
   * event.getX() 获取相对应自身左上角的X坐标
   * event.getY() 获取相对应自身左上角的Y坐标
   * getWidth() 获取控件的宽度
   * getHeight() 获取控件的高度
   * getTotalPaddingRight() 获取删除图标左边缘到控件右边缘的距离
   * getPaddingRight() 获取删除图标右边缘到控件右边缘的距离
   * isInnerWidth:
   * getWidth() - getTotalPaddingRight() 计算删除图标左边缘到控件左边缘的距离
   * getWidth() - getPaddingRight() 计算删除图标右边缘到控件左边缘的距离
   * isInnerHeight:
   * distance 删除图标顶部边缘到控件顶部边缘的距离
   * distance + height 删除图标底部边缘到控件顶部边缘的距离
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_UP) {
      if (getCompoundDrawables()[2] != null) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Rect rect = getCompoundDrawables()[2].getBounds();
        int height = rect.height();
        int distance = (getHeight() - height) / 2;
        boolean isInnerWidth =
            x > (getWidth() - getTotalPaddingRight()) && x < (getWidth() - getPaddingRight());
        boolean isInnerHeight = y > distance && y < (distance + height);
        if (isInnerWidth && isInnerHeight) {
          this.setText("");
        }
      }
    }
    return super.onTouchEvent(event);
  }

  /**
   * 当ClearEditText焦点发生变化的时候，
   * 输入长度为零，隐藏删除图标，否则，显示删除图标
   */
  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    this.mHasFoucs = hasFocus;
    if (mOnFocusListener != null) {
      mOnFocusListener.onFocus(hasFocus);
    }
    if (hasFocus) {
      setClearIconVisible(getText().length() > 0);
    } else {
      setClearIconVisible(false);
    }
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
  public ZdEditText setNormalColor(int normalColor) {
    if (normalColor == 0) return this;
    this.mNormalColor = getResources().getColor(normalColor);
    if (mGradientDrawable != null) {
      mGradientDrawable.setColor(this.mNormalColor);
    }
    return this;
  }

  /**
   * @param normalColor 设置默认颜色 例如：#ffffff
   */
  public ZdEditText setNormalColor(String normalColor) {
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
  public ZdEditText setCurrCorner(float currCorner) {
    if (currCorner == 0) return this;
    this.mCurrCorner = currCorner;
    if (mGradientDrawable != null) {
      mGradientDrawable.setCornerRadius(currCorner);
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
  public ZdEditText setStrokeWidth(float strokeWidth) {
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
  public ZdEditText setStrokeColor(int strokeColor) {
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
  public ZdEditText setStrokeColor(String strokeColor) {
    if (TextUtils.isEmpty(strokeColor)) return this;
    this.mStrokeColor = Color.parseColor(strokeColor);
    if (mGradientDrawable != null) {
      mGradientDrawable.setStroke((int) mStrokeWidth, this.mStrokeColor);
    }
    return this;
  }

  public void setClearIconVisible(boolean visible) {
    Drawable right = visible && mIsDelete ? mClearDrawable : null;
    setCompoundDrawables(getCompoundDrawables()[0],
        getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
  }

  public void setClearVisible() {
    setCompoundDrawables(getCompoundDrawables()[0],
        getCompoundDrawables()[1], null, getCompoundDrawables()[3]);
  }

  public void addAtSpan(String txt, String userId) {
    this.addAtSpan(txt, "", userId);
  }

  /**
   * 添加一个块,在文字的后面添加
   *
   * @param showText 显示到界面的内容
   * @param userId 附加属性，比如用户id,邮件id之类的，如果不需要可以去除掉
   */
  @SuppressLint("ResourceAsColor")
  public void addAtSpan(String maskText, String showText, String userId) {
    LogUtil.e("---------maskText:",maskText," | showText:",showText," | userId:",userId);
    if (hashMap.containsKey(userId)) return;
    builder = new StringBuilder();
    if (!TextUtils.isEmpty(maskText)) {
      //已经添加了@
      builder.append(maskText).append(showText).append(" ");
      //builder.append(showText).append(" ");
    } else {
      builder.append(showText).append(" ");
    }
    getText().insert(getSelectionStart(), builder.toString());
    SpannableString sps = new SpannableString(getText());

    int start =
        getSelectionEnd() - builder.toString().length() - (TextUtils.isEmpty(maskText) ? 1 : 0);
    int end = getSelectionEnd();
    LogUtil.e("--------start:",start," | end:",end);
    makeSpan(sps, new UnSpanText(start, end, builder.toString()), userId);
    setText(sps);
    setSelection(end);
  }

  /**
   * 获取用户Id列表
   * @return
   */
  public String getUserIdString() {
    MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
    StringBuilder builder = new StringBuilder();
    for (MyTextSpan myTextSpan : spans) {
      String realText = getText().toString()
          .substring(getText().getSpanStart(myTextSpan), getText().getSpanEnd(myTextSpan));
      String showText = myTextSpan.getShowText();
      if (realText.equals(showText)) {
        builder.append(myTextSpan.getUserId()).append(",");
      }
    }
    if (!TextUtils.isEmpty(builder.toString())) {
      builder.deleteCharAt(builder.length() - 1);
    }
    return builder.toString();
  }

  /**
   * 生成一个需要整体删除的Span
   * @param sps
   * @param unSpanText
   * @param userId
   */
  private void makeSpan(Spannable sps, UnSpanText unSpanText, String userId) {
    MyTextSpan what = new MyTextSpan(unSpanText.returnText, userId);
    int start = unSpanText.start;
    int end = unSpanText.end;
    sps.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    hashMap.put(userId, what);
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int count, int after) {
    if (mHasFoucs) {
      setClearIconVisible(s.length() > 0);
    }
    //向前删除一个字符，@后的内容必须大于一个字符，可以在后面加一个空格
    if (count == 1 && after == 0) {
      MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
      for (MyTextSpan myImageSpan : spans) {
        if (getText().getSpanEnd(myImageSpan) == start && !s.toString()
            .endsWith(myImageSpan.getShowText())) {
          getText().delete(getText().getSpanStart(myImageSpan), getText().getSpanEnd(myImageSpan));
          hashMap.remove(myImageSpan.userId);
          LogUtil.e("----------hashMap:", hashMap);
          break;
        }
      }
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count,
      int after) {
  }

  @Override
  public void afterTextChanged(Editable s) {
    if (s.length() > mLength) {
      //todo:net for callss
    }
    if (null != mCusEditListener) {
      mCusEditListener.afterTextChanged(s, s.toString());
    }
  }

  public void show(){
    show(getContext(),this);
  }

  public void show(Context context){
    show(context,this);
  }

  public void show(Context context,View view) {
    //setFocusable(true);
    InputMethodManager inputMethodManager =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (inputMethodManager != null) {
      requestFocus();
      boolean isShow = inputMethodManager.showSoftInput(view, 0);
    }
  }

  public void hide(){
    hide(getContext(),this);
  }

  public void hide(Context context,View view) {
    InputMethodManager inputMethodManager =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (inputMethodManager != null) {
      boolean isHide = inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  public void hide(Activity activity){
    InputMethodManager imm = (InputMethodManager)
        activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
  }

  public void KeyBoardCancle(BottomSheetDialogFragment dialog) {
    //View view =dialog.getWindow().peekDecorView();//注意：这里要根据EditText位置来获取
    View view = dialog.getDialog().getWindow().getDecorView();
    if (view != null) {
      InputMethodManager inputmanger = (InputMethodManager) AppUtil.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
      boolean isHidde = inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  @Override
  public void onGlobalLayout() {
    Rect rect = new Rect();
    getWindowVisibleDisplayFrame(rect);
    if (getContext() instanceof Activity) {
      int screenHeight =
          ((Activity) getContext()).getWindow().getDecorView().getRootView().getHeight();
      int heightDifference = screenHeight - rect.bottom;
      if (mKeyboardListener != null) {
        mKeyboardListener.onHeight(heightDifference);
      }
      if (heightDifference == 0) {
        //losePoint();
      }
    }
  }

  public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
      return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
      private CharSequence mSource;

      public PasswordCharSequence(CharSequence source) {
        this.mSource = source;
      }

      @Override
      public int length() {
        return mSource.length();
      }

      @Override
      public char charAt(int index) {
        return (index > mStart && index < mEnd) ? '*' : mSource.charAt(index);
      }

      @Override
      public CharSequence subSequence(int start, int end) {
        return mSource.subSequence(start, end);
      }
    }
  }

  public void searchPoint() {
    InputMethodManager mInputMethodManager =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    setFocusable(true);
    setCursorVisible(true);
    setFocusableInTouchMode(true);
    requestFocus();
    findFocus();
    mInputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED);
  }

  public void losePoint() {
    InputMethodManager mInputMethodManager =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    //        setFocusable(false);
    setCursorVisible(false);
    if (mInputMethodManager.isActive()) {
      mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
    }
  }

  public interface CusEditListener {
    public void afterTextChanged(Editable s, String input);
  }

  public interface KeyboardListener {
    public void onHeight(int height);
  }

  public interface OnFocusListener {
    public void onFocus(boolean focus);
  }

  public interface OnKeyboardListener {
    void show(int height);

    void hide(int height);
  }

  private class MyTextSpan extends MetricAffectingSpan {
    private String showText;
    private String userId;

    public MyTextSpan(String showText, String userId) {
      this.showText = showText;
      this.userId = userId;
    }

    public String getShowText() {
      return showText;
    }

    public String getUserId() {
      return userId;
    }

    @Override
    public void updateMeasureState(TextPaint p) {

    }

    @Override
    public void updateDrawState(TextPaint tp) {

    }
  }

  private class UnSpanText {
    int start;
    int end;
    String returnText;

    UnSpanText(int start, int end, String returnText) {
      this.start = start;
      this.end = end;
      this.returnText = returnText;
    }
  }
}
