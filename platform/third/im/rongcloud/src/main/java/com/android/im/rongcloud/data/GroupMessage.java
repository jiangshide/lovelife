package com.android.im.rongcloud.data;

import android.os.Build;
import android.os.Parcel;
import androidx.annotation.RequiresApi;
import com.android.utils.LogUtil;
import com.google.gson.Gson;
import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 戳一下消息
 */
@MessageTag(value = "CA3:GroupMsg", flag = MessageTag.ISPERSISTED | MessageTag.ISCOUNTED)
public class GroupMessage extends MessageContent {
  /**
   * 戳一下内容
   */
  public String message;
  public int message_type;

  public Extra extra;

  private GroupMessage() {
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT) public GroupMessage(byte[] data) {
    String jsonStr = null;
    try {
      jsonStr = new String(data, StandardCharsets.UTF_8);
      LogUtil.e("GroupMsg:", jsonStr);
      Gson gson = new Gson();
      GroupMessage groupMessage = gson.fromJson(jsonStr, GroupMessage.class);
      message = groupMessage.message;
      message_type = groupMessage.message_type;
      extra = groupMessage.extra;
    } catch (Exception e) {
      LogUtil.e("GroupMessage parse error:" + e.toString());
    }
  }

  public GroupMessage(Parcel in) {
    message = ParcelUtils.readFromParcel(in);
    message_type = ParcelUtils.readIntFromParcel(in);
    extra = ParcelUtils.readFromParcel(in, Extra.class);
  }

  public String getContent() {
    return message;
  }

  public int getMessage_type() {
    return message_type;
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT) @Override
  public byte[] encode() {
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.put("content", this.message);
      jsonObj.put("message_type", this.message_type);
      return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
    } catch (JSONException e) {
      e.printStackTrace();
      LogUtil.e("GroupMessage encode error:" + e.toString());
    }

    return null;
  }

  public static GroupMessage obtain(String content) {
    GroupMessage pokeMessage = new GroupMessage();
    pokeMessage.message = content;
    return pokeMessage;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    ParcelUtils.writeToParcel(dest, this.message);
    ParcelUtils.writeToParcel(dest, this.message_type);
    ParcelUtils.writeToParcel(dest, this.extra);
  }

  public static final Creator<GroupMessage> CREATOR = new Creator<GroupMessage>() {
    public GroupMessage createFromParcel(Parcel source) {
      return new GroupMessage(source);
    }

    public GroupMessage[] newArray(int size) {
      return new GroupMessage[size];
    }
  };

  @Override public String toString() {
    return "GroupMessage{" +
        "message='" + message + '\'' +
        ", message_type=" + message_type +
        ", extra=" + extra +
        '}';
  }
}
