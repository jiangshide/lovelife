package com.android.http.vm.data

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import com.android.http.BuildConfig
import com.google.gson.annotations.SerializedName

/**
 * created by jiangshide on 2019-12-06.
 * email:18311271399@163.com
 */
@SuppressLint("ParcelCreator")
@Keep
data class RespData<T>(
  val code: Int = 0,
  val date: Long,     
  @SerializedName(BuildConfig.RESP_RES) var res: T? = null,
  @SerializedName(BuildConfig.RESP_MSG) val msg: String? = null,
  @SerializedName(BuildConfig.RESP_VERSION) val version: String? = null
) : Parcelable {
  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeInt(code)
    parcel.writeLong(date)
    parcel.writeString(msg)
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun toString(): String {
    return "RespData(code=$code, date=$date, res=$res, msg=$msg)"
  }
}