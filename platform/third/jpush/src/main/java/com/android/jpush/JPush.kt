package com.android.jpush

import android.content.Context
import cn.jpush.android.api.JPushInterface
import com.android.utils.LogUtil

/**
 * created by jiangshide on 2020/5/21.
 * email:18311271399@163.com
 */
class JPush {

  fun setAlias(context: Context,id:Int,alias:String){
    JPushInterface.setAlias(context,id,alias)
    LogUtil.e("--------id:",id," | alias:",alias)
  }

  fun setTag(context: Context,id:Int,set:HashSet<String>){
    LogUtil.e("----------set:",set)
    JPushInterface.addTags(context,id,set)
  }
}

const val PUSH="push"