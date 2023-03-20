package com.android.jpush.data

/**
 * created by jiangshide on 2020/4/24.
 * email:18311271399@163.com
 */
class PushBean {
  var id: Long? = 0
  var uid: Long? = 0
  var action: Int? = 0//2:点赞通知,3:关注通知,4:浏览动态通知,5:浏览主页通知,6:浏览频道通知,7:创建了一个频道通知,8:创建了一个动态通知
  var name: String? = ""
  var content: String? = ""

  var onClick:Boolean = false
  override fun toString(): String {
    return "PushBean(id=$id, uid=$uid, action=$action, name=$name, content=$content, onClick=$onClick)"
  }

}