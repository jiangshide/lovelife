package com.android.resource.vm.user.data

/**
 * created by jiangshide on 2020/5/25.
 * email:18311271399@163.com
 */
class ContactsInfo {
  var id:Int = 0
  var name:String=""
  var number:String=""
  var photo:String=""
  var started:Int=0
  override fun toString(): String {
    return "ContactsInfo(id=$id, name='$name', number='$number', photo='$photo', started=$started)"
  }

}