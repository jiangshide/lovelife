package com.android.resource.vm.publish.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.android.utils.data.FileData
import com.android.utils.data.IMG
import com.google.gson.Gson

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class Publish() : Parcelable {
  var uid: Long = 0
  var channelId: Long = 0
  var channelCover: String? = null
  var content: String? = ""
  var title: String? = ""
  var des: String? = null
  var format: Int = IMG
  var city: String? = null
  var position: String? = null
  var address: String? = null
  var netInfo: String? = null
  var device: String? = null
  var weiXin: Int = 0

  var atsJson: String? = null
  var filesJson: String? = null

  var uploadFiles: ArrayList<File>? = ArrayList()

  var files: MutableList<FileData>? = null

  var styleJson: String? = null

  constructor(parcel: Parcel) : this() {
    uid = parcel.readLong()
    channelId = parcel.readLong()
    channelCover = parcel.readString()
    content = parcel.readString()
    title = parcel.readString()
    des = parcel.readString()
    format = parcel.readInt()
    city = parcel.readString()
    position = parcel.readString()
    address = parcel.readString()
    netInfo = parcel.readString()
    device = parcel.readString()
    weiXin = parcel.readInt()
    atsJson = parcel.readString()
    filesJson = parcel.readString()
    styleJson = parcel.readString()
  }

  fun toGson() {
    this.filesJson = Gson().toJson(uploadFiles)
  }

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(uid)
    parcel.writeLong(channelId)
    parcel.writeString(channelCover)
    parcel.writeString(content)
    parcel.writeString(title)
    parcel.writeString(des)
    parcel.writeInt(format)
    parcel.writeString(city)
    parcel.writeString(position)
    parcel.writeString(address)
    parcel.writeString(netInfo)
    parcel.writeString(device)
    parcel.writeInt(weiXin)
    parcel.writeString(atsJson)
    parcel.writeString(filesJson)
    parcel.writeString(styleJson)
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun toString(): String {
    return "Publish(uid=$uid, channelId=$channelId, channelCover=$channelCover, content=$content, title=$title, des=$des, format=$format, city=$city, position=$position, address=$address, netInfo=$netInfo, device=$device, weiXin=$weiXin, atsJson=$atsJson, filesJson=$filesJson, uploadFiles=$uploadFiles, files=$files, styleJson=$styleJson)"
  }

  companion object CREATOR : Creator<Publish> {
    override fun createFromParcel(parcel: Parcel): Publish {
      return Publish(parcel)
    }

    override fun newArray(size: Int): Array<Publish?> {
      return arrayOfNulls(size)
    }
  }
}