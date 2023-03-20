package com.android.resource.vm.user.data

import androidx.annotation.Keep

/**
 * created by jiangshide on 2020/5/3.
 * email:18311271399@163.com
 */
@Keep
data class Follow(
  val id: Long,
  val uid: Long,
  val status: Int,
  val especially: Int,
  val mutual: Int,
  val date: String,
  val name: String,
  val icon: String
)