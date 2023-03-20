package com.android.resource.vm.user.data

import androidx.annotation.Keep

/**
 * created by jiangshide on 2020/4/18.
 * email:18311271399@163.com
 */
@Keep
data class App(
  val id: Long,
  val uid: Long,
  val name: String,
  val platform: String,
  val channel: String,
  val version: String,
  val code: Int,
  val duration: Int,
  val times: Int,
  val des: String,
  val url: String,
  var status: Int,
  val count: Int,
  val date: String,
  var index: Int
)