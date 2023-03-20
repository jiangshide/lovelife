package com.android.web;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.base.BaseActivity;
import com.android.base.BaseFragment;
import com.android.web.fragment.WebFragment;

/**
 * created by jiangshide on 2020-01-23.
 * email:18311271399@163.com
 */
public class Web implements Parcelable {

  public String mUrl;
  public boolean mIsRemote;
  public boolean fromFragment = true;

  public String mTitle = "Web";
  public int mTopColor;
  public int mTitleColor;
  public int mSmallTitleColor;

  public String mRightTxt = "";
  public int mRightTxtColor;

  public Web(){}

  protected Web(Parcel in) {
    mUrl = in.readString();
    mIsRemote = in.readByte() != 0;
    fromFragment = in.readByte() != 0;
    mTitle = in.readString();
    mTopColor = in.readInt();
    mTitleColor = in.readInt();
    mSmallTitleColor = in.readInt();
    mRightTxt = in.readString();
    mRightTxtColor = in.readInt();
  }

  public static final Creator<Web> CREATOR = new Creator<Web>() {
    @Override
    public Web createFromParcel(Parcel in) {
      return new Web(in);
    }

    @Override
    public Web[] newArray(int size) {
      return new Web[size];
    }
  };

  public Web setUrl(String url) {
    return setUrl(url, false);
  }

  public Web setUrl(String url, boolean isRemote) {
    this.mUrl = url;
    this.mIsRemote = isRemote;
    return this;
  }

  public Web setTitle(String title) {
    this.mTitle = title;
    return this;
  }

  public Web setTopColor(int color) {
    this.mTopColor = color;
    return this;
  }

  public Web setTitleColor(int color) {
    this.mTitleColor = color;
    return this;
  }

  public Web setSmallTitleColor(int smallTitleColor) {
    this.mSmallTitleColor = smallTitleColor;
    return this;
  }

  public Web setRightTxt(String txt) {
    this.mRightTxt = txt;
    return this;
  }

  public Web setRightColor(int color) {
    this.mRightTxtColor = color;
    return this;
  }

  public void open(Object object) {
    mTopColor = mTopColor == 0 ? R.color.black : mTopColor;
    mTitleColor = mTitleColor == 0 ? R.color.white : mTitleColor;
    mSmallTitleColor = mSmallTitleColor == 0 ? R.color.white : mSmallTitleColor;
    mRightTxtColor = mRightTxtColor == 0 ? R.color.red : mRightTxtColor;
    Bundle bundle = new Bundle();
    bundle.putParcelable("web", this);
    if (object instanceof BaseFragment) {
      fromFragment = true;
      ((BaseFragment) object).push(new WebFragment(), bundle);
    } else {
      fromFragment = false;
      ((BaseActivity) object).goFragment(WebFragment.class, bundle);
    }
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(mUrl);
    dest.writeByte((byte) (mIsRemote ? 1 : 0));
    dest.writeByte((byte) (fromFragment ? 1 : 0));
    dest.writeString(mTitle);
    dest.writeInt(mTopColor);
    dest.writeInt(mTitleColor);
    dest.writeInt(mSmallTitleColor);
    dest.writeString(mRightTxt);
    dest.writeInt(mRightTxtColor);
  }
}
