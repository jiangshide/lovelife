package com.android.im.rongcloud.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * created by jiangshide on 2020-02-19.
 * email:18311271399@163.com
 */
public class Extra implements Parcelable {
  public String title;
  public String content;
  public int corporate_id;
  public int announcement_id;
  public String sent_at;
  public int event_id;
  public String event_name;
  public String time;
  public String location;
  public String initiator;
  public String sectors;
  public List<String> meeting_ids;
  public int app_user_id;
  public int meeting_id;

  protected Extra(Parcel in) {
    title = in.readString();
    content = in.readString();
    corporate_id = in.readInt();
    announcement_id = in.readInt();
    sent_at = in.readString();
    event_id = in.readInt();
    event_name = in.readString();
    time = in.readString();
    location = in.readString();
    initiator = in.readString();
    sectors = in.readString();
    meeting_ids = in.createStringArrayList();
    app_user_id = in.readInt();
    meeting_id = in.readInt();
  }

  public static final Creator<Extra> CREATOR = new Creator<Extra>() {
    @Override
    public Extra createFromParcel(Parcel in) {
      return new Extra(in);
    }

    @Override
    public Extra[] newArray(int size) {
      return new Extra[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(title);
    dest.writeString(content);
    dest.writeInt(corporate_id);
    dest.writeInt(announcement_id);
    dest.writeString(sent_at);
    dest.writeInt(event_id);
    dest.writeString(event_name);
    dest.writeString(time);
    dest.writeString(location);
    dest.writeString(initiator);
    dest.writeString(sectors);
    dest.writeStringList(meeting_ids);
    dest.writeInt(app_user_id);
    dest.writeInt(meeting_id);
  }

  @Override public String toString() {
    return "Extra{" +
        "title='" + title + '\'' +
        ", content='" + content + '\'' +
        ", corporate_id=" + corporate_id +
        ", announcement_id=" + announcement_id +
        ", sent_at='" + sent_at + '\'' +
        ", event_id=" + event_id +
        ", event_name='" + event_name + '\'' +
        ", time='" + time + '\'' +
        ", location='" + location + '\'' +
        ", initiator='" + initiator + '\'' +
        ", sectors='" + sectors + '\'' +
        ", meeting_ids=" + meeting_ids +
        ", app_user_id=" + app_user_id +
        ", meeting_id=" + meeting_id +
        '}';
  }
}
