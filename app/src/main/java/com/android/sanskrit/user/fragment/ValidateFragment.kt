package com.android.sanskrit.user.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.resource.FINISH_USERCODE
import com.android.resource.FINISH_VALIDATE
import com.android.resource.MyFragment
import com.android.resource.NAME_SIZE
import com.android.resource.code.CodeFragment
import com.android.resource.code.Country
import com.android.sanskrit.R
import com.android.sanskrit.R.color
import com.android.utils.LogUtil
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.user_validate_fragment.userCode
import kotlinx.android.synthetic.main.user_validate_fragment.userNameEdit
import kotlinx.android.synthetic.main.user_validate_fragment.userSubmit

/**
 * created by jiangshide on 2020/3/18.
 * email:18311271399@163.com
 */
class ValidateFragment : MyFragment() {

  private var isReg = true
  private var name = ""
  private var title = ""
  private var countryCode = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTopView(false).setTitleView(R.layout.user_validate_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    this.title = arguments?.getString("title")!!
    isReg = arguments?.getString("param")
        .equals("reg")
    this.countryCode = 86
    userCode.text = "+${this.countryCode}"
    if (!isReg) {
      userCode.visibility = View.GONE
      userNameEdit.setPadding(10, 10, 10, 40)
    }

    userCode.setOnClickListener {
      goFragment("国家代码", CodeFragment::class.java)
      LogUtil.e("it:", it)
    }

    ZdEvent.get()
        .with(FINISH_USERCODE, Country::class.java)
        .observes(this, Observer {
          this.countryCode = it.code
          userCode.text = "+${this.countryCode}"
        })

    userNameEdit.setListener { s, input ->
      this.name = input
      val disable = TextUtils.isEmpty(this.name) || this.name.length < NAME_SIZE
      userSubmit.isEnabled = !disable
      userSubmit.normalColor = if (disable) color.disable else color.blackLightMiddle
    }
    userSubmit.setOnClickListener {
      userVM?.userExit(this.name)
      showLoading()
    }
    userVM!!.userExit.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      if (isReg) {
        if (it.data) {
          ZdToast.txt("$name is exits!")
          return@Observer
        }
      } else {
        if (!it.data) {
          ZdToast.txt("$name is not exits!")
          return@Observer
        }
      }
      val bundle = Bundle()
      bundle.putString("name", this.name)
      bundle.putBoolean("isReg", isReg)
      bundle.putString("country", "${this.countryCode}")
      goFragment(this.title, RegFragment::class.java, bundle)
    })

    ZdEvent.get()
        .with(FINISH_VALIDATE)
        .observes(this, Observer {
          finish()
        })
  }
}