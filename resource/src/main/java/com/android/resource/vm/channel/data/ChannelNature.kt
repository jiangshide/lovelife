package com.android.resource.vm.channel.data

import androidx.annotation.Keep

/**
 * created by jiangshide on 2020/3/24.
 * email:18311271399@163.com
 */
@Keep
data class ChannelNature(
  val id: Int,
  val name: String,
  val des: String,
  val num: Int,
  val time: Int,
  val note: String
)