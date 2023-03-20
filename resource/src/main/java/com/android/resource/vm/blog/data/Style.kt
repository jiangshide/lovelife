package com.android.resource.vm.blog.data

import com.google.gson.Gson

/**
 * created by jiangshide on 2020/7/3.
 * email:18311271399@163.com
 */
data class Style(
  var id: Long = 0,
  var blogId:Long=0,
  var position: Int = 0,//位置:左上-51,上中-49,上右-53,右中-21,右下-85,下中-81,下左-83,左中-19
  var size: Int = 20,//文字大小
  var txtColor: String? = "#FFFFFF",//文字颜色
  var bgColor: String? = "#33353f",//背景颜色
  var scroll: Int = 0//是否滚动:1~滚动,0~不滚动
) {

  fun toGson(): String {
    return Gson().toJson(this)
  }

  override fun toString(): String {
    return "Style(id=$id, blogId=$blogId, position=$position, size=$size, txtColor=$txtColor, bgColor=$bgColor, scroll=$scroll)"
  }
}