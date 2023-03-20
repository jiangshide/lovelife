package com.android.resource.vm.user.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.android.location.ZdLocation
import com.android.utils.LogUtil
import com.google.gson.Gson

/**
 * created by jiangshide on 2020/4/30.
 * email:18311271399@163.com
 */
open class Profile() : Parcelable {
  var id: Long = 0
  var unionId: String? = ""//唯一凭证:针对第三登录
  var icon: String? = "" //用户头像
  var level: Int = 0 //级别
  var score: Int = 0 //积分
  var vip: Int = 0 //会员及级别
  var certification: Long = 0//实名认证ID
  var nick: String? = "" //昵称
  var fullName: String? = "" //姓名
  var phone: String? = "" //电话
  var intro: String? = "" //座右铭
  var birthday: String? = "" //生日
  var sex: Int = 0 //性别:0:保密,1:男,2:女
  var age: Int = 0//年龄
  var zodiac: String? = ""//生肖
  var constellation: String? = "" //星座
  var hobby: String? = "" //爱好
  var email: String? = "" //邮箱
  var weixin: String? = "" //微信
  var qq: String? = "" //QQ
  var weibo: String? = "" //微博
  var alipay: String? = ""//支付宝
  var latitude: Double = 0.0//精度
  var longitude: Double = 0.0//纬度
  var locationType: String? = ""//定位类型
  var country: String? = "" //国家代码
  var province: String? = "" //省
  var city: String? = "" //城市
  var adCode: String? = ZdLocation.getInstance().adCode//区域码

  var praiseNotice: Int = 0//点赞通知:0~打开,-1~关闭
  var followNotice: Int = 0//关注通知:0~打开,-1~关闭
  var broweHomeNotice: Int = 0//看主页通知:0~打开,-1~关闭
  var broweChannelNotice: Int = 0//看频道通知:0~打开,-1~关闭
  var broweBlogNotice: Int = 0//看动态通知:0~打开,-1~关闭
  var createChannelNotice: Int = 0//创建频道通知:0~打开,-1~关闭
  var createBlogNotice: Int = 0//发布动态通知:0~打开,-1~关闭
  var noticeType: String? =
    ""//通知类型:可选范围为 -1～7 ，对应 Notification.DEFAULT_ALL = -1 或者 Notification.DEFAULT_SOUND = 1, Notification.DEFAULT_VIBRATE = 2, Notification.DEFAULT_LIGHTS = 4 的任意 “or” 组合。默认按照 -1 处理'

  var date: String? = "" //加入时间

  constructor(parcel: Parcel) : this() {
    id = parcel.readLong()
    unionId = parcel.readString()
    icon = parcel.readString()
    level = parcel.readInt()
    score = parcel.readInt()
    vip = parcel.readInt()
    certification = parcel.readLong()
    nick = parcel.readString()
    fullName = parcel.readString()
    phone = parcel.readString()
    intro = parcel.readString()
    birthday = parcel.readString()
    sex = parcel.readInt()
    age = parcel.readInt()
    zodiac = parcel.readString()
    constellation = parcel.readString()
    hobby = parcel.readString()
    email = parcel.readString()
    weixin = parcel.readString()
    qq = parcel.readString()
    weibo = parcel.readString()
    alipay = parcel.readString()
    latitude = parcel.readDouble()
    longitude = parcel.readDouble()
    locationType = parcel.readString()
    country = parcel.readString()
    province = parcel.readString()
    city = parcel.readString()
    adCode = parcel.readString()

    praiseNotice = parcel.readInt()
    followNotice = parcel.readInt()
    broweHomeNotice = parcel.readInt()
    broweChannelNotice = parcel.readInt()
    broweBlogNotice = parcel.readInt()
    createChannelNotice = parcel.readInt()
    createBlogNotice = parcel.readInt()
    noticeType = parcel.readString()

    date = parcel.readString()
  }

  fun set(profile: Profile) {
    id = profile.id
    unionId = profile.unionId
    icon = profile.icon
    level = profile.level
    score = profile.score
    vip = profile.vip
    certification = profile.certification
    nick = profile.nick
    fullName = profile.fullName
    phone = profile.phone
    intro = profile.intro
    birthday = profile.birthday
    sex = profile.sex
    age = profile.age
    zodiac = profile.zodiac
    constellation = profile.constellation
    hobby = profile.hobby
    email = profile.email
    weixin = profile.weixin
    qq = profile.qq
    weibo = profile.weibo
    alipay = profile.alipay
    latitude = profile.longitude
    longitude = profile.longitude
    locationType = profile.locationType
    country = profile.country
    province = profile.province
    city = profile.city
    adCode = profile.adCode

    praiseNotice = profile.praiseNotice
    followNotice = profile.followNotice
    broweHomeNotice = profile.broweHomeNotice
    broweChannelNotice = profile.broweChannelNotice
    broweBlogNotice = profile.broweBlogNotice
    createChannelNotice = profile.createChannelNotice
    createBlogNotice = profile.createBlogNotice
    noticeType = profile.noticeType

    date = profile.date
  }

  open fun getGson(): String? {
    val profile = Profile()
    profile.id = this.id
    profile.unionId = this.unionId
    profile.icon = this.icon
    profile.level = this.level
    profile.score = this.score
    profile.vip = this.vip
    profile.certification = this.certification
    profile.nick = this.nick
    profile.fullName = this.fullName
    profile.phone = this.phone
    profile.intro = this.intro
    profile.birthday = this.birthday
    profile.sex = this.sex
    profile.age = this.age
    profile.zodiac = this.zodiac
    profile.constellation = this.constellation
    profile.hobby = this.hobby
    profile.email = this.email
    profile.weixin = this.weixin
    profile.qq = this.qq
    profile.weibo = this.weibo
    profile.alipay = this.alipay
    profile.latitude = this.latitude
    profile.longitude = this.longitude
    profile.locationType = this.locationType
    profile.country = this.country
    profile.province = this.province
    profile.city = this.city
    profile.adCode = this.adCode
    profile.praiseNotice = this.praiseNotice
    profile.followNotice = this.followNotice
    profile.broweHomeNotice = this.broweHomeNotice
    profile.broweChannelNotice = this.broweChannelNotice
    profile.broweBlogNotice = this.broweBlogNotice
    profile.createChannelNotice = this.createChannelNotice
    profile.createBlogNotice = this.createBlogNotice
    profile.noticeType = this.noticeType

    val json = Gson().toJson(profile)
    LogUtil.e("json:", json)
    return json
  }

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(id)
    parcel.writeString(unionId)
    parcel.writeString(icon)
    parcel.writeInt(level)
    parcel.writeInt(score)
    parcel.writeInt(vip)
    parcel.writeLong(certification)
    parcel.writeString(nick)
    parcel.writeString(fullName)
    parcel.writeString(phone)
    parcel.writeString(intro)
    parcel.writeString(birthday)
    parcel.writeInt(sex)
    parcel.writeInt(age)
    parcel.writeString(zodiac)
    parcel.writeString(constellation)
    parcel.writeString(hobby)
    parcel.writeString(email)
    parcel.writeString(weixin)
    parcel.writeString(qq)
    parcel.writeString(weibo)
    parcel.writeString(alipay)
    parcel.writeDouble(latitude)
    parcel.writeDouble(longitude)
    parcel.writeString(locationType)
    parcel.writeString(country)
    parcel.writeString(province)
    parcel.writeString(city)
    parcel.writeString(adCode)

    parcel.writeInt(praiseNotice)
    parcel.writeInt(followNotice)
    parcel.writeInt(broweHomeNotice)
    parcel.writeInt(broweChannelNotice)
    parcel.writeInt(broweBlogNotice)
    parcel.writeInt(createChannelNotice)
    parcel.writeInt(createBlogNotice)
    parcel.writeString(noticeType)

    parcel.writeString(date)
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun toString(): String {
    return "Profile(id=$id, unionId=$unionId, icon=$icon, level=$level, score=$score, vip=$vip, certification=$certification, nick=$nick, fullName=$fullName, phone=$phone, intro=$intro, birthday=$birthday, sex=$sex, age=$age, zodiac=$zodiac, constellation=$constellation, hobby=$hobby, email=$email, weixin=$weixin, qq=$qq, weibo=$weibo, alipay=$alipay, latitude=$latitude, longitude=$longitude, locationType=$locationType, country=$country, province=$province, city=$city, adCode=$adCode, praiseNotice=$praiseNotice, followNotice=$followNotice, broweHomeNotice=$broweHomeNotice, broweChannelNotice=$broweChannelNotice, broweBlogNotice=$broweBlogNotice, createChannelNotice=$createChannelNotice, createBlogNotice=$createBlogNotice, noticeType=$noticeType, date=$date)"
  }

  companion object CREATOR : Creator<Profile> {
    override fun createFromParcel(parcel: Parcel): Profile {
      return Profile(parcel)
    }

    override fun newArray(size: Int): Array<Profile?> {
      return arrayOfNulls(size)
    }
  }

}
