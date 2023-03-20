package com.android.resource.vm.blog.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * created by jiangshide on 2020/7/8.
 * email:18311271399@163.com
 */
data class At(
  val id: Long=0,
  val uid: Long=0,
  val nick: String?=""
):Parcelable {
  constructor(parcel: Parcel) : this(
      parcel.readLong(),
      parcel.readLong(),
      parcel.readString()
  ) {
  }

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(id)
    parcel.writeLong(uid)
    parcel.writeString(nick)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Creator<At> {
    override fun createFromParcel(parcel: Parcel): At {
      return At(parcel)
    }

    override fun newArray(size: Int): Array<At?> {
      return arrayOfNulls(size)
    }
  }
}