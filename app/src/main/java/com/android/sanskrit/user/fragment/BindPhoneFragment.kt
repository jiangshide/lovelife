package com.android.sanskrit.user.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.resource.REFRESH_MINE
import com.android.resource.REFRESH_PROFILE
import com.android.resource.Resource
import com.android.resource.data.DeviceData
import com.android.resource.data.PositionData
import com.android.sanskrit.MainActivity
import com.android.sanskrit.R
import com.android.sanskrit.R.color
import com.android.utils.LogUtil
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.user_bindphone_fragment.userBind
import kotlinx.android.synthetic.main.user_bindphone_fragment.userBindName
import kotlinx.android.synthetic.main.user_bindphone_fragment.userBindPsw
import kotlinx.android.synthetic.main.user_bindphone_fragment.userBindSkip

/**
 * created by jiangshide on 2020/4/16.
 * email:18311271399@163.com
 */
class BindPhoneFragment(private val isApp:Boolean=false) : MyFragment() {

  private var name = ""
  private var psw = ""

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return  setTopView(false).setTitleView(R.layout.user_bindphone_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    val positionData = PositionData()
    val deviceData = DeviceData(mActivity)

    userBindSkip.setOnClickListener {
      val user = Resource.user
      user?.ignoreBind = 1
      Resource.user = user
      startActivity(Intent(mActivity, MainActivity::class.java))
      finish()
    }
    userBindName.setListener { s, input ->
      this.name = input
      validate()
    }
    userBindPsw.setListener { s, input ->
      this.psw = input
      validate()
    }
    userBind.setOnClickListener {
      userVM?.bind(
          name = name, password = psw, netInfo = positionData.gson, device = deviceData.gson
      )
    }

    userVM!!.bind.observe(this, Observer {
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      LogUtil.e("err:",it.error," | data:",it.data)
      Resource.user = it.data
      if(isApp){
        ZdEvent.get().with(REFRESH_PROFILE).post(REFRESH_PROFILE)
        ZdEvent.get()
            .with(REFRESH_MINE)
            .post(REFRESH_MINE)
        pop()
      }else{
        startActivity(Intent(mActivity, MainActivity::class.java))
      }
      finish()
    })

    val user = Resource.user
    if(user != null && user.ignoreBind == 1){
      userBindSkip.visibility = View.GONE
    }else{
      userBindSkip.visibility = View.VISIBLE
    }
  }

  private fun validate() {
    val disable = TextUtils.isEmpty(this.name) || TextUtils.isEmpty(
        this.psw
    ) || this.name.length < 3 || this.psw.length < 6
    userBind.isEnabled = !disable
    userBind.normalColor = if (!disable) color.black else color.blackLightMiddle
  }
}