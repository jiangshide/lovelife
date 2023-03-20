package com.android.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.android.event.ZdEvent;
import com.android.utils.Constant;
import com.android.utils.SPUtil;
import com.android.utils.ScreenUtil;
import java.util.List;

/**
 * created by jiangshide on 2019-07-24.
 * email:18311271399@163.com
 */
public class ZdTopView extends LinearLayout implements View.OnClickListener {

  private int mBgIcon;

  private String mLeftName;
  private int mLeftTextColor;
  private int mLeftTextSize;

  private String mTitleName;
  private int mTitleTextColor;
  private int mTitleTextSize;

  private String mTitleSmallName;
  private int mTitleSmallTextColor;
  private int mTitleSmallTextSize;

  private String mRightName;
  private int mRightTextColor;
  private int mRightTextSize;

  private List<String> mDataList;

  private OnClickListener mOnLeftClickListener, mOnRightClickListener;

  private AdapterView.OnItemClickListener mOnItemClickListener;

  public ZdTopView(Context context) {
    this(context, null);
  }

  public ZdTopView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ZdTopView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ZdTopView, 0, 0);
    if (array != null) {
      mBgIcon = array.getColor(R.styleable.ZdTopView_bgColor, -1);

      mLeftName = array.getString(R.styleable.ZdTopView_leftName);
      mLeftTextColor = array.getColor(R.styleable.ZdTopView_leftTextColor,
          getResources().getColor(R.color.font));
      mLeftTextSize = array.getInteger(R.styleable.ZdTopView_leftTextSize, 13);

      mTitleName = array.getString(R.styleable.ZdTopView_titleName);
      mTitleTextColor = array.getInteger(R.styleable.ZdTopView_titleTextColor,
          getResources().getColor(R.color.font));
      mTitleTextSize = array.getInteger(R.styleable.ZdTopView_titleTextSize, 15);

      mTitleSmallName = array.getString(R.styleable.ZdTopView_titleName);
      mTitleSmallTextColor = array.getInteger(R.styleable.ZdTopView_titleTextColor,
          getResources().getColor(R.color.fontLight));
      mTitleSmallTextSize = array.getInteger(R.styleable.ZdTopView_titleTextSize, 10);

      mRightName = array.getString(R.styleable.ZdTopView_rightName);
      mRightTextColor = array.getColor(R.styleable.ZdTopView_rightTextColor,
          getResources().getColor(R.color.font));
      mRightTextSize = array.getInteger(R.styleable.ZdTopView_rightTextSize, 13);
      array.recycle();
    }
    init();
  }

  private RelativeLayout topL;
  public ZdButton topLeftBtn;
  private LinearLayout topTitleL;
  public TextView topTitle;
  public TextView topTitleSmall;
  public ZdButton topRightBtn;

  private void init() {
    //addView(LayoutInflater.from(getContext()).inflate(R.layout.default_top, null),
    //    new RelativeLayout.LayoutParams(
    //        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
    addView(LayoutInflater.from(getContext()).inflate(R.layout.default_top, null), ScreenUtil.getRtScreenWidth(getContext()),
        (int) getContext().getResources().getDimension(R.dimen.topBar));
    topL = findViewById(R.id.topL);
    topL.setBackgroundColor(ContextCompat.getColor(getContext(),SPUtil.getInt(Constant.APP_COLOR_BG,R.color.bg)));
    topLeftBtn = findViewById(R.id.topLeftBtn);
    topTitleL = findViewById(R.id.topTitleL);
    topTitle = findViewById(R.id.topTitle);
    topTitleSmall = findViewById(R.id.topTitleSmall);
    topRightBtn = findViewById(R.id.topRightBtn);

    //topL.setBackgroundResource(mBgIcon != -1 ? mBgIcon : R.drawable.default_view_bottom_shade);
    topLeftBtn.setTextColor(mLeftTextColor);
    topLeftBtn.setTextSize(mLeftTextSize);
    topLeftBtn.setOnClickListener(this);
    if (!TextUtils.isEmpty(mLeftName)) {
      setLefts(mLeftName);
    }

    if(!TextUtils.isEmpty(mTitleName)){
      setTitle(mTitleName);
    }
    topTitle.setTextColor(mTitleTextColor);
    topTitle.setTextSize(mTitleTextSize);

    topTitleSmall.setText(TextUtils.isEmpty(mTitleSmallName) ? "" : mTitleSmallName);
    topTitleSmall.setTextColor(mTitleSmallTextColor);
    topTitleSmall.setTextSize(mTitleSmallTextSize);

    topRightBtn.setTextColor(mRightTextColor);
    topRightBtn.setTextSize(mRightTextSize);
    topRightBtn.setOnClickListener(this);
    if (!TextUtils.isEmpty(mRightName)) {
      setRights(mRightName);
    }
    setTitleGravity(Gravity.CENTER);
  }

  public ZdTopView showView(boolean isShow) {
    topL.setVisibility(isShow ? View.VISIBLE : View.GONE);
    return this;
  }

  public ZdTopView setBg(int resIcon) {
    mBgIcon = resIcon;
    //invalidate();
    topL.setBackgroundResource(resIcon);
    return this;
  }

  public ZdTopView setLeftTint(int color) {
    return this;
  }

  public ZdTopView setTitleGravity(int gravity) {
    topTitleL.setGravity(gravity);
    return this;
  }

  public ZdTopView setTitle(Object object) {
    if (object == null) return showView(false);
    String title = "";
    if (object instanceof String) {
      title = (String) object;
    } else if (object instanceof Integer) {
      title = getResources().getString((Integer) object);
    }
    if (TextUtils.isEmpty(title)) return showView(false);
    topTitle.setText(title);
    topTitle.setVisibility(VISIBLE);
    return showView(true);
  }

  public ZdTopView setTitleColor(int color) {
    topTitle.setTextColor(getResources().getColor(color));
    return this;
  }

  public ZdTopView setSmallTitle(Object object) {
    if (object == null) return showView(false);
    String title = "";
    if (object instanceof String) {
      title = (String) object;
    } else if (object instanceof Integer) {
      title = getResources().getString((Integer) object);
    }
    if (TextUtils.isEmpty(title)) return showView(false);
    topTitleSmall.setText(title);
    topTitleSmall.setVisibility(VISIBLE);
    return showView(true);
  }

  public ZdTopView setSmallTitleColor(int color) {
    topTitleSmall.setTextColor(getResources().getColor(color));
    return this;
  }

  public ZdTopView setLefts(Object object) {
    if (object == null) return showView(false);
    if (object instanceof String) {
      topLeftBtn.setText((String) object);
      topLeftBtn.drawableLeft(R.drawable.alpha);
      //RelativeLayout.LayoutParams params =
      //    new RelativeLayout.LayoutParams(topLeftBtn.getLayoutParams());
      //params.setMargins(25, 0, 25, 0);
      //topLeftBtn.setLayoutParams(params);
      topLeftBtn.setVisibility(VISIBLE);
      return this.showView(true);
    } else if (object instanceof Integer) {
      topLeftBtn.drawableLeft((Integer) object);
      topLeftBtn.setText("");
      return this.showView(true);
    } else if (object instanceof Boolean) {
      boolean isShow = (Boolean) object;
      topLeftBtn.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
      if (isShow) {
        this.showView(true);
      }
    }
    return this;
  }

  public ZdTopView setLeftColor(int color) {
    topLeftBtn.setTextColor(color);
    return this;
  }

  public ZdTopView setRights(Object object) {
    if (object == null) return showView(false);
    if (object instanceof String) {
      topRightBtn.setText((String) object);
      topRightBtn.drawableRight(R.drawable.alpha);
      topRightBtn.setVisibility(VISIBLE);
      return showView(true);
    } else if (object instanceof Integer) {
      topRightBtn.drawableLeft((Integer) object);
      topRightBtn.setText("");
      topRightBtn.setVisibility(VISIBLE);
      return showView(true);
    } else if (object instanceof Boolean) {
      boolean isShow = (Boolean) object;
      topRightBtn.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
      if (isShow) {
        this.showView(true);
      }
    }
    return this;
  }

  public ZdTopView setRightColor(int color) {
    topRightBtn.setTextColor(getResources().getColor(color));
    return this;
  }

  public ZdTopView setDataList(List<String> dataList) {
    this.mDataList = dataList;
    return this;
  }

  public ZdTopView setOnLeftClick(OnClickListener listener) {
    this.mOnLeftClickListener = listener;
    topLeftBtn.setVisibility(listener != null ? VISIBLE : INVISIBLE);
    return this;
  }

  public ZdTopView setOnRightClick(OnClickListener listener) {
    this.mOnRightClickListener = listener;
    topRightBtn.setVisibility(listener != null ? VISIBLE : INVISIBLE);
    return this;
  }

  public ZdTopView setOnItemListener(AdapterView.OnItemClickListener listener) {
    this.mOnItemClickListener = listener;
    return this;
  }

  @Override
  public void onClick(View v) {
    ZdEvent.Companion.get()
        .with("flowMenus")
        .post(true);
    int id = v.getId();
    if (id == R.id.topLeftBtn) {
      if (mOnLeftClickListener != null) {
        mOnLeftClickListener.onClick(v);
      } else {
        ((Activity) getContext()).finish();
      }
    } else if (id == R.id.topRightBtn) {
      if (mDataList != null && mDataList.size() > 0) {
        ZdDialog zdDialog = ZdDialog.createList(getContext(), mDataList);
        if (mOnItemClickListener != null) {
          zdDialog.setOnItemListener(mOnItemClickListener);
        }
        zdDialog.show();
      } else if (mOnRightClickListener != null) {
        mOnRightClickListener.onClick(v);
      }
    }
  }
}
