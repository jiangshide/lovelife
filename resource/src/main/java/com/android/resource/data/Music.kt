package com.android.resource.data

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

/**
 * created by jiangshide on 2019-10-25.
 * email:18311271399@163.com
 */
@Keep
data class Music(
  var duration: Int = 0,
  var audioWave: String? = "",
  var name: String? = "",
  var id: Int = 0,
  var url: String? = ""
) : Parcelable {

  var waveArray: List<Int>? = null

  constructor(parcel: Parcel) : this(
      parcel.readInt(),
      parcel.readString(),
      parcel.readString(),
      parcel.readInt(),
      parcel.readString()
  ) {
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(duration)
    parcel.writeString(audioWave)
    parcel.writeString(name)
    parcel.writeInt(id)
    parcel.writeString(url)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Music> {
    override fun createFromParcel(parcel: Parcel): Music {
      return Music(parcel)
    }

    override fun newArray(size: Int): Array<Music?> {
      return arrayOfNulls(size)
    }
  }
}