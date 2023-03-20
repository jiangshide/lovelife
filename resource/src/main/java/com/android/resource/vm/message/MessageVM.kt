package com.android.resource.vm.message

import com.android.base.vm.BaseVM
import com.android.http.Http
import com.android.resource.vm.message.remote.MessageRemote

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class MessageVM :BaseVM(){
  private val iRank:MessageRemote = Http.createService(MessageRemote::class.java)
}