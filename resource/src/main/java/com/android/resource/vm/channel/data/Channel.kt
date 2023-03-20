package com.android.resource.vm.channel.data

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.text.TextUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.img.Img
import com.android.resource.MEN
import com.android.resource.R
import com.android.resource.WOMEN
import com.android.utils.AppUtil
import com.android.utils.ScreenUtil
import com.android.utils.StringUtil
import com.android.widget.extension.setDrawableLeft

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
open class Channel() : Parcelable {
  var id: Long = 0
  var uid: Long = 0//用户ID
  var typeId: Int = 0//频道类型ID
  var natureId: Int = 0//频道所属ID
  var cover: String? = ""//封面
  var name: String? = ""//频道名称
  var des: String? = ""//频道描述
  var status: Int = 0//状态:0~未审核,1~审核中,2~审核通过,-1~移到回忆箱,-2~审核拒绝,-3～禁言，-4~关闭/折叠,-5~被投诉
  var official: Int = 0//官方推荐:-1~取消推荐,0~未推荐,1~推荐,2~特别推荐
  var reason: String? = ""//原由
  var date: String? = ""//创建时间

  val icon: String? = ""//用户头像
  var nick: String? = ""//用户昵称
  val sex: Int = 0//用户性别
  val age: Int = 0//用户年龄
  val city: String? = ""//用户出生城市
  var latitude: Double = 0.0//用户所在经度
  var longitude: Double = 0.0//用户所在纬度
  var locationType: String? = ""//定位类型

  var blogNum: Int = 0//动态条数

  var url: String? = ""//动态url

  var remark: String? = ""//备注名

  var tops: Int = 0//置顶状态

  var follows: Int = 0//关注状态

  var reportr: String? = ""//被举报原因

  var index: Int = 0
  var refresh: String? = ""

  fun setSex(text: TextView) {
    val context = AppUtil.getApplicationContext().applicationContext
    when (sex) {
      MEN -> {
        text.setDrawableLeft(R.mipmap.sex_man)
      }
      WOMEN -> {
        text.setDrawableLeft(R.mipmap.sex_women)
      }
    }
    var str = ""
    if (age > 0) {
      str = context.getString(R.string.dot) + " $age"
    }
    if (!TextUtils.isEmpty(city)) {
      str += context.getString(R.string.dot) + " $city"
    }
    text.text = str
    StringUtil.setDot(text, context.getString(R.string.dot))
  }

  fun setIcon(img:ImageView){
    setIcon(img,false)
  }

  fun setIcon(
    img: ImageView,
    isCircle: Boolean
  ) {
    var url = cover
    if (TextUtils.isEmpty(url)) {
      url = icon
    }
    if (isCircle) {
      Img.loadImageCircle(url, img)
    } else {
      Img.loadImageRound(url, img, 5)
    }
  }

  fun setCover(activity: Activity, img:ImageView,isRandom: Boolean){
    var height = ScreenUtil.getRtScreenHeight(activity) / 3
    val layoutParams =
      RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.MATCH_PARENT,
        height
      )
    img?.layoutParams = layoutParams
    Img.loadImageRound(cover, img, 10)
  }

  constructor(parcel: Parcel) : this() {
    id = parcel.readLong()
    uid = parcel.readLong()
    typeId = parcel.readInt()
    natureId = parcel.readInt()
    cover = parcel.readString()
    name = parcel.readString()
    des = parcel.readString()
    status = parcel.readInt()
    official = parcel.readInt()
    reason = parcel.readString()
    date = parcel.readString()
    nick = parcel.readString()
    latitude = parcel.readDouble()
    longitude = parcel.readDouble()
    locationType = parcel.readString()
    blogNum = parcel.readInt()
    url = parcel.readString()
    remark = parcel.readString()
    tops = parcel.readInt()
    follows = parcel.readInt()
    reportr = parcel.readString()
    index = parcel.readInt()
    refresh = parcel.readString()
  }

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(id)
    parcel.writeLong(uid)
    parcel.writeInt(typeId)
    parcel.writeInt(natureId)
    parcel.writeString(cover)
    parcel.writeString(name)
    parcel.writeString(des)
    parcel.writeInt(status)
    parcel.writeInt(official)
    parcel.writeString(reason)
    parcel.writeString(date)
    parcel.writeString(nick)
    parcel.writeDouble(latitude)
    parcel.writeDouble(longitude)
    parcel.writeString(locationType)
    parcel.writeInt(blogNum)
    parcel.writeString(url)
    parcel.writeString(remark)
    parcel.writeInt(tops)
    parcel.writeInt(follows)
    parcel.writeString(reportr)
    parcel.writeInt(index)
    parcel.writeString(refresh)
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun toString(): String {
    return "Channel(id=$id, uid=$uid, typeId=$typeId, natureId=$natureId, cover=$cover, name=$name, des=$des, status=$status, official=$official, reason=$reason, date=$date, icon=$icon, nick=$nick, latitude=$latitude, longitude=$longitude, locationType=$locationType, blogNum=$blogNum, remark=$remark, tops=$tops, follows=$follows, reportr=$reportr, index=$index, refresh=$refresh)"
  }

  companion object CREATOR : Creator<Channel> {
    override fun createFromParcel(parcel: Parcel): Channel {
      return Channel(parcel)
    }

    override fun newArray(size: Int): Array<Channel?> {
      return arrayOfNulls(size)
    }
  }

}