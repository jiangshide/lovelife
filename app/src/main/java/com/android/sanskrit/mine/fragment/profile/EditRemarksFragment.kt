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
import com.android.resource.REFRESH_PROFILE
import com.android.resource.Resource
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.utils.LogUtil
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.profile_remarks_fragment.editProfileRemarks
import kotlinx.android.synthetic.main.profile_remarks_fragment.editProfileRemarksTips

/**
 * created by jiangshide on 2020/4/1.
 * email:18311271399@163.com
 */
class EditRemarksFragment : MyFragment() {


  private var name:String=""

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.profile_remarks_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    val user:User = arguments?.getParcelable("data")!!
    name = user.remark
    LogUtil.e("-----id:",user.id)
    setTitle("修改备注名").setRight("完成")
        .setRightEnable(false)
        .setRightListener {
          userVM!!.remarks(user!!.id, name)
          showLoading()
        }
    if (!TextUtils.isEmpty(name)) {
      editProfileRemarks.setText(name)
      showTips()
    }
    editProfileRemarks.setListener { s, input ->
      name = input
      setRightEnable(!TextUtils.isEmpty(name))
      showTips()
      LogUtil.e("user:",user)
    }

    userVM!!.remarks.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        ZdToast.txt(it.error.msg)
        return@Observer
      }
      ZdEvent.get()
          .with(REFRESH_MIN)
          .post(REFRESH_MIN)
      pop()
    })
  }

  private fun showTips() {
    editProfileRemarksTips.text = "可输入${12 - name.length}字符"
  }
}