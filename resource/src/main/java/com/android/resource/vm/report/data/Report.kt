package com.android.resource.vm.report.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.annotation.Keep

/**
 * created by jiangshide on 2020/4/5.
 * email:18311271399@163.com
 */
@Keep
data class Report(
  var id: Long=0,
  var uid: Long=0,
  var contentId: Long=0,
  var type: Int=0,
  var source: Int=0,
  var status: Int=0,
  var reason: String?="",
    var name:String?=""
):Parcelable {
  constructor(parcel: Parcel) : this(
      parcel.readLong(),
      parcel.readLong(),
      parcel.readLong(),
      parcel.readInt(),
      parcel.readInt(),
      parcel.readInt(),
      parcel.readString(),
      parcel.readString()
  ) {
  }

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(id)
    parcel.writeLong(uid)
    parcel.writeLong(contentId)
    parcel.writeInt(type)
    parcel.writeInt(source)
    parcel.writeInt(status)
    parcel.writeString(reason)
    parcel.writeString(name)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Creator<Report> {
    override fun createFromParcel(parcel: Parcel): Report {
      return Report(parcel)
    }

    override fun newArray(size: Int): Array<Report?> {
      return arrayOfNulls(size)
    }
  }
}

@Keep
data class Type(
  val id: Int,
  val name: String
)