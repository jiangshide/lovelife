package com.android.resource.data

import androidx.annotation.Keep

/**
 * created by jiangshide on 2020/3/22.
 * email:18311271399@163.com
 */
@Keep
data class CodeData(
  val code: Int,
  val tw: String,
  val en: String,
  val locale: String,
  val zh: String
)