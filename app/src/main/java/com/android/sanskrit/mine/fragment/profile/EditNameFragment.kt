package com.android.sanskrit.mine.fragment.profile

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.resource.REFRESH_PROFILE
import com.android.resource.Resource
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.mine_profile_name_fragment.editProfileName

/**
 * created by jiangshide on 2020/3/22.
 * email:18311271399@163.com
 */
class EditNameFragment : MyFragment() {

  private var nice = ""

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_profile_name_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    setTitle("修改昵称").setRight("完成")
        .setRightEnable(false)
        .setRightListener {
          val user = Resource.user
          user?.nick = this.nice
          userVM!!.profile(user,2)
          showLoading()
        }
    val user = Resource.user
    if (user != null && !TextUtils.isEmpty(user.nick)) {
      editProfileName.setText(user.nick)
    }
    editProfileName.setListener { s, input ->
      this.nice = input
      setRightEnable(!TextUtils.isEmpty(this.nice))
    }

    userVM!!.profile.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        ZdToast.txt(it.error.msg)
        return@Observer
      }
      ZdEvent.get()
          .with(REFRESH_PROFILE)
          .post(REFRESH_PROFILE)
      pop()
    })
  }
}