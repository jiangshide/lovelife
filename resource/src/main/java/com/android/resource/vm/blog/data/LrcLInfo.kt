package com.android.resource.vm.blog.data

/**
 * created by jiangshide on 2020/5/29.
 * email:18311271399@163.com
 */
class LrcLInfo {
  var count: Int = 0
  var code: Int = 0
  var result: MutableList<Lrc>? = null

  override fun toString(): String {
    return "LrcLInfo(count=$count, code=$code, result=$result)"
  }

}

class Lrc {
  var aid: Int = 0
  var artist_id: Int = 0
  var lrc: String = ""
  var Sid: Int = 0
  var song: String = ""

  override fun toString(): String {
    return "Lrc(aid=$aid, artist_id=$artist_id, lrc='$lrc', Sid=$Sid, song=$song)"
  }
}