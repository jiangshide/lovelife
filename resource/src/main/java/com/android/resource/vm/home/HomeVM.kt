package com.android.resource.vm.home

import com.android.base.vm.BaseVM
import com.android.http.Http
import com.android.resource.vm.home.remote.HomeRemote

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class HomeVM :BaseVM(){
  private val iHome:HomeRemote = Http.createService(HomeRemote::class.java)
}