package com.android.resource.vm.blog.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.widget.TextView
import com.android.utils.DateUtil

/**
 * created by jiangshide on 2020/4/1.
 * email:18311271399@163.com
 */
class Comment() : Parcelable {
  var id: Long = 0
  var uid: Long = 0//用户ID
  var fromUid: Long = 0//来自用户ID
  var contentId: Long? = 0//动态ID
  var content: String? = ""//内容
  var urls: String? = ""//内容Url/音频波文件数组地址
  var reply:Int = 0//回复:1~是,0~不是
  var replyNick:String?=""//回复的昵称
  var status: Int = 1//状态:1~正常评论,2~优秀评论,-1~异常评论,-4~关闭/折叠,-5~被投诉
  var reason: String? = ""//原由
  var date: String? = ""//创建日期

  var nick: String? = ""//昵称
  var icon: String? = ""//头像
  var remark: String? = ""//备注名称

  var num: Int = 0//评论条数

  var praiseNum: Int = 0//点赞次数

  var praises: Int = 0//点赞状态

  var reportr: String? = ""//举报原因

  var comments: MutableList<Comment>? = null

  constructor(parcel: Parcel) : this() {
    uid = parcel.readLong()
    fromUid = parcel.readLong()
    contentId = parcel.readValue(Long::class.java.classLoader) as? Long
    content = parcel.readString()
    urls = parcel.readString()
    status = parcel.readInt()
    reason = parcel.readString()
    date = parcel.readString()
    nick = parcel.readString()
    icon = parcel.readString()
    remark = parcel.readString()
    num = parcel.readInt()
    praiseNum = parcel.readInt()
    praises = parcel.readInt()
    reportr = parcel.readString()
  }

  fun setDate(text: TextView) {
    text.text = DateUtil.showTimeAhead(DateUtil.stringToLong(date))
  }

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(uid)
    parcel.writeLong(fromUid)
    parcel.writeValue(contentId)
    parcel.writeString(content)
    parcel.writeString(urls)
    parcel.writeInt(status)
    parcel.writeString(reason)
    parcel.writeString(date)
    parcel.writeString(nick)
    parcel.writeString(icon)
    parcel.writeString(remark)
    parcel.writeInt(num)
    parcel.writeInt(praiseNum)
    parcel.writeInt(praises)
    parcel.writeString(reportr)
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun toString(): String {
    return "Comment(id=$id, uid=$uid, fromUid=$fromUid, contentId=$contentId, content=$content, urls=$urls, status=$status, reason=$reason, date=$date, nick=$nick, icon=$icon, remark=$remark, num=$num, praiseNum=$praiseNum, praises=$praises, reportr=$reportr, comments=$comments)"
  }

  companion object CREATOR : Creator<Comment> {
    override fun createFromParcel(parcel: Parcel): Comment {
      return Comment(parcel)
    }

    override fun newArray(size: Int): Array<Comment?> {
      return arrayOfNulls(size)
    }
  }

}

data class Total(
  val size: Long,
  val count: Long
)