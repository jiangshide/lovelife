package com.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import com.android.utils.Constant;
import com.android.utils.SPUtil;
import com.android.utils.ScreenUtil;
import com.android.widget.spedit.emoji.EmojiManager;
import java.util.Random;

/**
 * created by jiangshide on 2019-10-18.
 * email:18311271399@163.com
 */
public class ZdTextView extends AppCompatTextView implements View.OnClickListener {
  private int mMaxSize = 80;
  private String mMoreStr = "more";
  private String mCloseStr = "close";
  private boolean mLight = false;
  private int mColor;
  private int mTextColor;
  private boolean isClose;
  private SpannableString mSpan;
  private SpannableString mSpanALL;
  private TextView mTextView;
  private View.OnClickListener mOnClickListener;
  boolean isOnClick = false;

  public ZdTextView(Context context) {
    super(context, null);
  }

  public ZdTextView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    mColor = getResources().getColor(R.color.fontLight);
    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CusTextView, 0, 0);
    if (array != null) {
      mMoreStr = array.getString(R.styleable.CusTextView_more);
      mCloseStr = array.getString(R.styleable.CusTextView_close);
      mMaxSize = array.getInt(R.styleable.CusTextView_maxSize, mMaxSize);
      mColor = array.getColor(R.styleable.CusTextView_tips_color, mColor);
      mTextColor = array.getColor(R.styleable.CusTextView_text_color, mColor);
      mLight = array.getBoolean(R.styleable.CusTextView_is_light, false);
      if (mLight) {
        mTextColor = ContextCompat.getColor(getContext(),
            SPUtil.getInt(Constant.APP_COLOR_FONT_LIGHT, R.color.fontLight));
      } else {
        mTextColor = SPUtil.getInt(Constant.APP_COLOR_BG, mTextColor);
      }
      setTextColor(mTextColor);
      array.recycle();
    }
    setOnClickListener(this);
  }

  public void setTxt(String str) {
    setTxt(str, null);
  }

  public void setTxt(String str, View.OnClickListener listener) {
    this.setText(str);
    showNormalStr(this.getText().toString(), mMaxSize, this, listener);
  }

  public void setTxt(CharSequence charSequence) {
    setTxt(charSequence, null);
  }

  public void setTxt(CharSequence charSequence, View.OnClickListener listener) {
    this.setText(charSequence);
    showNormalStr(this.getText().toString(), mMaxSize, this, listener);
  }

  private int getShowNornalIndex(TextView textView, String str, int width, int maxLine) {
    TextPaint textPaint = textView.getPaint();
    StaticLayout
        staticLayout =
        new StaticLayout(str, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
    if (staticLayout.getLineCount() > maxLine) {
      return staticLayout.getLineStart(maxLine) - 1;
    } else {
      return -1;
    }
  }

  private void showNormalStr(String str, int showNormalMax, final TextView textView) {
    showNormalStr(str, showNormalMax, textView, null);
  }

  private void showNormalStr(String textString, int showNormalMax, final TextView textView,
      final View.OnClickListener clickListener) {
    this.mTextView = textView;
    this.mOnClickListener = clickListener;
    if (textView == null) return;
    textView.setOnClickListener(this);
    int width = textView.getWidth();
    if (width == 0) width = ScreenUtil.getScreenWidth(getContext());
    int lastCharIndex = getShowNornalIndex(textView, textString, width, 10);
    if (lastCharIndex < 0 && textString.length() <= showNormalMax) {
      textView.setText(textString);
      isOnClick = false;
      return;
    }
    isOnClick = true;
    textView.setMovementMethod(LinkMovementMethod.getInstance());
    if (lastCharIndex > showNormalMax || lastCharIndex < 0) {
      lastCharIndex = showNormalMax;
    }
    String explicitText = null;
    String explicitTextAll;
    if (textString.charAt(lastCharIndex) == '\n') {
      explicitText = textString.substring(0, lastCharIndex);
    } else if (lastCharIndex > 12) {
      explicitText = textString.substring(0, lastCharIndex - 12);
    }
    int sourceLength = explicitText.length();
    explicitText = explicitText + "..." + mMoreStr;
    mSpan = new SpannableString(explicitText);

    explicitTextAll = textString + "..." + mCloseStr;
    mSpanALL = new SpannableString(explicitTextAll);
    mSpanALL.setSpan(new ClickableSpan() {
      @Override
      public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mColor);
        ds.setAntiAlias(true);
        ds.setUnderlineText(false);
      }

      @Override
      public void onClick(View widget) {
        open();
      }
    }, textString.length(), explicitTextAll.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    mSpan.setSpan(new ClickableSpan() {
      @Override
      public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mColor);
        ds.setAntiAlias(true);
        ds.setUnderlineText(false);
      }

      @Override
      public void onClick(View widget) {
        close();
      }
    }, sourceLength, explicitText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    textView.setText(mSpan);
  }

  private void open() {
    mTextView.setText(mSpan);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (mOnClickListener != null) {
          mTextView.setOnClickListener(mOnClickListener);
        }
      }
    }, 20);
  }

  private void close() {
    mTextView.setText(mSpanALL);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (mOnClickListener != null) {
          mTextView.setOnClickListener(mOnClickListener);
        }
      }
    }, 20);
  }

  @Override
  public void onClick(View v) {
    if (!isOnClick) {
      return;
    }
    if (isClose) {
      isClose = false;
      open();
    } else {
      isClose = true;
      close();
    }
  }

  /**
   * 设置随机颜色
   */
  public int randomColor() {
    Random random = new Random();
    int ranColor = 0xff000000 | random.nextInt(0x00ffffff);
    return ranColor;
  }

  public Spannable getText(String text) {
    return EmojiManager.INSTANCE.getSpannableByPattern(getContext(), text);
  }
}