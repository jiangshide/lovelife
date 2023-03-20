package com.android.sanskrit.user

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.location.ZdLocation
import com.android.resource.MyFragment
import com.android.resource.REFRESH_PROFILE
import com.android.resource.Resource
import com.android.resource.data.DeviceData
import com.android.resource.data.PositionData
import com.android.sanskrit.MainActivity
import com.android.sanskrit.R
import com.android.sanskrit.R.color
import com.android.sanskrit.user.fragment.BindPhoneFragment
import com.android.sanskrit.user.fragment.ValidateFragment
import com.android.sanskrit.wxapi.WXApiManager
import com.android.utils.LogUtil
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.user_fragment.skipTxt
import kotlinx.android.synthetic.main.user_fragment.userForgetPsw
import kotlinx.android.synthetic.main.user_fragment.userGoReg
import kotlinx.android.synthetic.main.user_fragment.userIcon
import kotlinx.android.synthetic.main.user_fragment.userLogin
import kotlinx.android.synthetic.main.user_fragment.userNameEdit
import kotlinx.android.synthetic.main.user_fragment.userPswEdit
import kotlinx.android.synthetic.main.user_fragment.userWeixinLoginL

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class UserFragment : MyFragment() {

  private var name = ""
  private var psw = ""

  private var positionData: PositionData? = null
  private var deviceData: DeviceData? = null

  private val handler = Handler {
    LogUtil.e("it:", it)
    when (it.what) {
      1 -> {
        userVM?.weChat(it.obj.toString(), positionData!!.gson, deviceData!!.gson)
        showLoading()
      }
    }
    return@Handler false
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return  setTopView(false).setTitleView(R.layout.user_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    WXApiManager.regToWX(mActivity)
    skipTxt.setOnClickListener {
      startActivity(Intent(mActivity, MainActivity::class.java))
      finish()
    }
    positionData = PositionData()
    deviceData = DeviceData(mActivity)
    userNameEdit.setListener { s, input ->
      this.name = input
      validate()
    }
    userPswEdit.setListener { s, input ->
      this.psw = input
      validate()
    }

    userLogin.setOnClickListener {
      userVM?.login(this.name, this.psw, positionData!!.gson, deviceData!!.gson)
      showLoading()
    }
    userForgetPsw.setOnClickListener {
      goFragment("忘记密码", ValidateFragment::class.java, "")
    }
    userGoReg.setOnClickListener {
      goFragment("注册", ValidateFragment::class.java, "reg")
    }

    userVM!!.login.observe(this, Observer {
      LogUtil.e("err:", it.error, " | data:", it.data)
      hiddle()
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      Resource.user = it.data
      if (TextUtils.isEmpty(it.data.name) && it.data.ignoreBind == 0) {
        goFragment("绑定手机号", BindPhoneFragment::class.java)
      } else {
        Resource.name = this.name
        startActivity(Intent(mActivity, MainActivity::class.java))
//        finish()
      }
    })
    userWeixinLoginL.setOnClickListener {
      if (!WXApiManager.isWxInstall()) {
        ZdToast.txt(R.string.wechat_not_install)
        return@setOnClickListener
      }
      WXApiManager.sendLoginRequest()
    }

    ZdEvent.get()
        .with(LOGIN_WECHAT, String::class.java)
        .observes(this, Observer {
          LogUtil.e("-----it:", it)
          val msg = Message()
          msg.what = 1
          msg.obj = it
          handler.sendMessage(msg)
        })

    ZdEvent.get()
        .with(REFRESH_PROFILE)
        .observes(this, Observer {
          initView()
        })
    initView()
  }

  private fun initView() {
    this.name = Resource.name
    if (!TextUtils.isEmpty(this.name)) {
      userNameEdit.setText(this.name)
      Img.loadImageCircle(Resource.icon, userIcon, R.mipmap.ic_launcher)
    }
    validate()

  }

  private fun validate() {
    val disable = TextUtils.isEmpty(this.name) || TextUtils.isEmpty(
        this.psw
    ) || this.name.length < 3 || this.psw.length < 6
    userLogin.isEnabled = !disable
    userLogin.normalColor = if (!disable) color.blackLightMiddle else color.disable
  }
}

const val LOGIN_WECHAT = "loginWeChat"