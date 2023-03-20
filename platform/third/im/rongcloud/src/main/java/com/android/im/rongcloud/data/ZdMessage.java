package com.android.im.rongcloud.data;

import android.os.Parcel;
import android.os.Parcelable;
import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * created by jiangshide on 2020-02-19.
 * email:18311271399@163.com
 */
//@MessageTag(value = "CA3:GroupMsg",flag = MessageTag.ISPERSISTED | MessageTag.ISCOUNTED)
public class ZdMessage extends MessageContent implements Parcelable {

  public String content;

  public String extra;

  protected ZdMessage(Parcel in) {
    ParcelUtils.readFromParcel(in);
    content = in.readString();
    extra = in.readString();
  }

  public ZdMessage(byte[] data){

  }

  public static final Creator<ZdMessage> CREATOR = new Creator<ZdMessage>() {
    @Override
    public ZdMessage createFromParcel(Parcel in) {
      return new ZdMessage(in);
    }

    @Override
    public ZdMessage[] newArray(int size) {
      return new ZdMessage[size];
    }
  };

  @Override public byte[] encode() {
    return new byte[0];
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(content);
    dest.writeString(extra);
  }
}
