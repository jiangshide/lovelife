package com.android.resource

import android.os.Bundle
import androidx.annotation.Keep
import androidx.lifecycle.ViewModelProviders
import com.android.base.BaseActivity
import com.android.base.WebActivity
import com.android.resource.vm.publish.PublishVM
import com.android.resource.vm.user.UserVM
import com.android.utils.AppUtil
import com.android.utils.Constant
import com.android.utils.SPUtil
import com.android.utils.SystemUtil
//import com.android.web.Web

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
open class MyActivity : BaseActivity() {

//  var userVM: UserVM? = null
//  var publishVM: PublishVM? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    userVM = ViewModelProviders.of(this)
//        .get(UserVM::class.java)
//    publishVM = ViewModelProviders.of(this)
//        .get(PublishVM::class.java)
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
//    if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//      val decorView: View = window.decorView
//      decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//          or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//          or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//          or View.SYSTEM_UI_FLAG_FULLSCREEN
//          or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//    }
  }
}