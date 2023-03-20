package com.android.resource.vm.user.data

import android.text.TextUtils
import android.widget.TextView
import com.amap.api.maps.model.LatLng
import com.android.resource.MEN
import com.android.resource.R
import com.android.resource.WOMEN
import com.android.utils.AppUtil
import com.android.utils.StringUtil
import com.android.widget.extension.setDrawableLeft

/**
 * latitude=39.761187#longitude=116.210377#province=北京市#coordType=GCJ02#city=北京市#district=房山区#cityCode=010#adCode=110111#address=北京市房山区怡和北路5号院靠近北京市第四中学(房山校区)#country=中国#road=怡和北路#poiName=北京市第四中学(房山校区)#street=怡和北路#streetNum=5号院#aoiName=首开熙悦湾#poiid=#floor=#errorCode=0#errorInfo=success#locationDetail=#id:YYWhvcWhxY2xrOWNjYThmOGtoY21hYjA4ODQwNjkyLFhPUXI2Unh1ZTRVREFFQ3JFWllGNWhxbA==#csid:849efa837c294849835fea9de567005c#description=在北京市第四中学(房山校区)附近#locationType=5#conScenario=96
 * 2020-03
 * created by jiangshide on 2019-10-08.
 * email:18311271399@163.com
 */
class User : Profile() {

  var name: String? = ""//用户名
  var status: Int = 0 //状态:0~审核中,1~审核通过,-1~审核拒绝,-2~移到回忆箱,-3～禁言,-4~关闭/折叠,-5~被举报
  var reason: String? = "" //审核未通过原因
  var online: Int = 0 //在线状态:1~在线:-1~离线

  var fansNum: Int = 0//粉丝数
  var followsNum: Int = 0 //关注数
  var channelsNum: Int = 0 //频道条数
  var blogsNum: Int = 0 //动态条数
  var activeNum: Int = 0 //活跃度

  var remark: String = ""//备注名

  var friends: Int = 0//好友状态

  var follows: Int = 0//关注状态

  var reportr: String? = ""//已被举报原因

  var ignoreBind: Int = 0//绑定状态:0~未绑定手机号,1~忽略绑定手机号或已绑定手机号

  var selected:Boolean = false

  fun setSex(text: TextView) {
    val context = AppUtil.getApplicationContext().applicationContext
    when (sex) {
      MEN -> {
        text.setDrawableLeft(R.mipmap.sex_man)
      }
      WOMEN -> {
        text.setDrawableLeft(R.mipmap.sex_women)
      }
      else->{
        text.setDrawableLeft(R.mipmap.secrecy)
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

  fun getLatLng(): LatLng {
    return LatLng(latitude, longitude)
  }

  override fun toString(): String {
    return "User(name=$name, status=$status, reason=$reason, online=$online, fansNum=$fansNum, followsNum=$followsNum, channelsNum=$channelsNum, blogsNum=$blogsNum, activeNum=$activeNum, remark='$remark', friends=$friends, follows=$follows, reportr=$reportr, ignoreBind=$ignoreBind)"
  }

}