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
 * @B.Yyyy' 是否可以简化下为:name(文件名称),url(文件地址),type(文件类型):2020年02月21日星期五
 */
@MessageTag(value = "CA3:Document", flag = MessageTag.ISPERSISTED | MessageTag.ISCOUNTED)
public class DocumentMessage extends MessageContent {
  /**
   * 戳一下内容
   */
  public String name;
  public String url;
  public String type;

  private DocumentMessage() {
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT) public DocumentMessage(byte[] data) {
    String jsonStr = null;
    try {
      jsonStr = new String(data, StandardCharsets.UTF_8);
      LogUtil.e("GroupMsg:", jsonStr);
      Gson gson = new Gson();
      DocumentMessage groupMessage = gson.fromJson(jsonStr, DocumentMessage.class);
      name = groupMessage.name;
      url = groupMessage.url;
      type = groupMessage.type;
    } catch (Exception e) {
      LogUtil.e("GroupMessage parse error:" + e.toString());
    }
  }

  public DocumentMessage(Parcel in) {
    name = ParcelUtils.readFromParcel(in);
    url = ParcelUtils.readFromParcel(in);
    type = ParcelUtils.readFromParcel(in);
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getType(){
    return type;
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT) @Override
  public byte[] encode() {
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.put("name", this.name);
      jsonObj.put("url", this.url);
      jsonObj.put("type",this.type);
      return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
    } catch (JSONException e) {
      e.printStackTrace();
      LogUtil.e("GroupMessage encode error:" + e.toString());
    }

    return null;
  }

  public static DocumentMessage obtain(String name,String url){
    return obtain(name,url,"pdf");
  }

  public static DocumentMessage obtain(String name, String url, String type) {
    DocumentMessage documentMessage = new DocumentMessage();
    documentMessage.name = name;
    documentMessage.url = url;
    documentMessage.type = type;
    return documentMessage;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    ParcelUtils.writeToParcel(dest, this.name);
    ParcelUtils.writeToParcel(dest, this.url);
    ParcelUtils.writeToParcel(dest, this.type);
  }

  public static final Creator<DocumentMessage> CREATOR = new Creator<DocumentMessage>() {
    public DocumentMessage createFromParcel(Parcel source) {
      return new DocumentMessage(source);
    }

    public DocumentMessage[] newArray(int size) {
      return new DocumentMessage[size];
    }
  };

  @Override public String toString() {
    return "GroupMessage{" +
        "name ='" + name + '\'' +
        ", url=" + url +
        ", type=" + type +
        '}';
  }
}
