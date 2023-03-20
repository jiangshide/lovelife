package com.android.resource.vm.user.data

import android.widget.TextView
import androidx.annotation.Keep
import com.android.utils.DateUtil

/**
 * created by jiangshide on 2020/5/3.
 * email:18311271399@163.com
 */
@Keep
data class Friend(
  val id: Long,
  val uid: Long,
  val status: Int,
  val reason: String,
  val url: String,
  val date: String,
  val nick: String,
  val icon: String,
  val channelNum:Int,
  val blogNum:Int
){
  fun setDate(text: TextView) {
    text.text = DateUtil.showTimeAhead(DateUtil.stringToLong(date))
  }
}