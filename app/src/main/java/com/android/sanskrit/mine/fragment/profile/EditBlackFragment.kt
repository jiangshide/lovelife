package com.android.sanskrit.mine.fragment.profile

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.resource.REFRESH_MIN
import com.android.sanskrit.R
import com.android.sanskrit.home.HOME_REFRESH
import com.android.utils.LogUtil
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.profile_remarks_fragment.editProfileRemarks
import kotlinx.android.synthetic.main.profile_remarks_fragment.editProfileRemarksTips

/**
 * created by jiangshide on 2020/5/19.
 * email:18311271399@163.com
 */
class EditBlackFragment :MyFragment(){
  private var reason:String=""

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.profile_black_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    val id = arguments?.getLong("id")
    LogUtil.e("-----id:",id)
    setTitle("添加黑名单").setRight("完成")
        .setRightEnable(false)
        .setRightListener {
          userVM?.friendAdd(uid = id!!,status = -1,reason = reason)
          showLoading()
        }
    editProfileRemarks.setListener { s, input ->
      reason = input
      setRightEnable(!TextUtils.isEmpty(reason))
      showTips()
    }

    userVM!!.friendAdd.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        ZdToast.txt(it.error.msg)
        return@Observer
      }
      ZdEvent.get()
          .with(REFRESH_MIN)
          .post(REFRESH_MIN)
      ZdEvent.get().with(HOME_REFRESH).post(HOME_REFRESH)
      pop()
    })
  }

  private fun showTips() {
    editProfileRemarksTips.text = "可输入${100 - reason.length}字符"
  }
}