package com.android.im.rongcloud.data;

import android.os.Build;
import android.os.Parcel;
import android.util.Log;
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
@MessageTag(value = "CA3:SystemMsg", flag = MessageTag.ISPERSISTED | MessageTag.ISCOUNTED)
public class SysMessage extends MessageContent {
    /**
     * 戳一下内容
     */
    public String content;
    public int message_type;

    public Extra extra;

    private SysMessage() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT) public SysMessage(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = new String(data, StandardCharsets.UTF_8);
            Log.e("GroupMsg:",jsonStr);
            Gson gson = new Gson();
            SysMessage groupMessage = gson.fromJson(jsonStr, SysMessage.class);
            content = groupMessage.content;
            message_type = groupMessage.message_type;
            extra = groupMessage.extra;
            //JSONObject jsonObj = new JSONObject(jsonStr);
            //content = jsonObj.optString("content");
            //message_type = jsonObj.optInt("message_type");
        } catch (Exception e) {
            LogUtil.e("GroupMessage parse error:" + e.toString());
        }
    }

    public SysMessage(Parcel in) {
        content = ParcelUtils.readFromParcel(in);
        message_type = ParcelUtils.readIntFromParcel(in);
        extra = ParcelUtils.readFromParcel(in,Extra.class);
    }


    public String getContent() {
        return content;
    }

    public int getMessage_type(){
        return message_type;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT) @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("content", this.content);
            jsonObj.put("message_type",this.message_type);
            return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.e( "GroupMessage encode error:" + e.toString());
        }

        return null;
    }

    public static SysMessage obtain(String content) {
        SysMessage pokeMessage = new SysMessage();
        pokeMessage.content = content;
        return pokeMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, this.content);
        ParcelUtils.writeToParcel(dest,this.message_type);
        ParcelUtils.writeToParcel(dest,this.extra);
    }

    public static final Creator<SysMessage> CREATOR = new Creator<SysMessage>() {
        public SysMessage createFromParcel(Parcel source) {
            return new SysMessage(source);
        }

        public SysMessage[] newArray(int size) {
            return new SysMessage[size];
        }
    };

    @Override public String toString() {
        return "GroupMessage{" +
            "content='" + content + '\'' +
            ", message_type=" + message_type +
            ", extra=" + extra +
            '}';
    }
}
