package com.android.resource.vm.channel.data

import androidx.annotation.Keep

/**
 * created by jiangshide on 2020/4/3.
 * email:18311271399@163.com
 */
@Keep
data class Word(
  val id: Long?=0,
  val name: String?="",
  val source: Int?=0,
  val from: Int?=0
)