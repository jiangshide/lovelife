package com.android.resource.vm.user.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.android.resource.Resource
import com.google.gson.Gson

/**
 * created by jiangshide on 2020/5/4.
 * email:18311271399@163.com
 */
class Certification() :Parcelable{
  var id: Long = 0
  var uid: Long = Resource.uid!!
  var name: String? = ""
  var idCard: String? = ""
  var idCardPicFront: String? = ""
  var idCardPicBehind: String? = ""
  var url: String? = ""
  var status:Int=0
  var reason:String?=""
  var date:String?=""
  var refresh:String?=""

  constructor(parcel: Parcel) : this() {
    id = parcel.readLong()
    uid = parcel.readLong()
    name = parcel.readString()
    idCard = parcel.readString()
    idCardPicFront = parcel.readString()
    idCardPicBehind = parcel.readString()
    url = parcel.readString()
    status = parcel.readInt()
    reason = parcel.readString()
    date = parcel.readString()
    refresh = parcel.readString()
  }

  fun toJson(): String? {
    return Gson().toJson(this)
  }

  override fun toString(): String {
    return "Certification(id=$id, uid=$uid, name=$name, idCard=$idCard, idCardPicFront=$idCardPicFront, idCardPicBehind=$idCardPicBehind, url=$url, status=$status, reason='$reason', date='$date', refresh='$refresh')"
  }

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(id)
    parcel.writeLong(uid)
    parcel.writeString(name)
    parcel.writeString(idCard)
    parcel.writeString(idCardPicFront)
    parcel.writeString(idCardPicBehind)
    parcel.writeString(url)
    parcel.writeInt(status)
    parcel.writeString(reason)
    parcel.writeString(date)
    parcel.writeString(refresh)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Creator<Certification> {
    override fun createFromParcel(parcel: Parcel): Certification {
      return Certification(parcel)
    }

    override fun newArray(size: Int): Array<Certification?> {
      return arrayOfNulls(size)
    }
  }

}