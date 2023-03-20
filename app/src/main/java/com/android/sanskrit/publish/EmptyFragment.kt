package com.android.sanskrit.publish

import android.os.Bundle
import com.android.resource.MyFragment

/**
 * created by jiangshide on 2020/3/25.
 * email:18311271399@163.com
 */
class EmptyFragment:MyFragment(){
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    showFloatMenu(false)
  }
}