package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.resource.MyFragment
import com.android.resource.OFF
import com.android.resource.ON
import com.android.resource.Resource
import com.android.resource.vm.user.data.Profile
import com.android.sanskrit.R
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchBrowseBlog
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchBrowseChannel
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchBrowseHome
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchCreatedChannel
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchDefault
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchFollowed
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchLight
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchPraise
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchPublishBlog
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchShock
import kotlinx.android.synthetic.main.mine_set_switch_fragment.switchSound

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class SwitchFragment : MyFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_switch_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    showFloatMenu(false)

    userVM?.profile?.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        showStatus()
        return@Observer
      }
      Resource.user?.set(it.data)
    })

    switchPraise?.setOnCheckedChangeListener { view, isChecked ->
      val user = Resource.user
      if (isChecked) {
        user?.praiseNotice = ON
      } else {
        user?.praiseNotice = OFF
      }
      userVM?.profile(user)
      showLoading()
    }

    switchFollowed?.setOnCheckedChangeListener { view, isChecked ->
      val user = Resource.user
      if (isChecked) {
        user?.followNotice = ON
      } else {
        user?.followNotice = OFF
      }
      userVM?.profile(user)
      showLoading()
    }

    switchBrowseHome?.setOnCheckedChangeListener { view, isChecked ->
      val user = Resource.user
      if (isChecked) {
        user?.broweHomeNotice = ON
      } else {
        user?.broweHomeNotice = OFF
      }
      userVM?.profile(user)
      showLoading()
    }

    switchBrowseChannel?.setOnCheckedChangeListener { view, isChecked ->
      val user = Resource.user
      if (isChecked) {
        user?.broweChannelNotice = ON
      } else {
        user?.broweChannelNotice = OFF
      }
      userVM?.profile(user)
      showLoading()
    }

    switchBrowseBlog?.setOnCheckedChangeListener { view, isChecked ->
      val user = Resource.user
      if (isChecked) {
        user?.broweBlogNotice = ON
      } else {
        user?.broweBlogNotice = OFF
      }
      userVM?.profile(user)
      showLoading()
    }

    switchCreatedChannel?.setOnCheckedChangeListener { view, isChecked ->
      val user = Resource.user
      if (isChecked) {
        user?.createChannelNotice = ON
      } else {
        user?.createChannelNotice = OFF
      }
      userVM?.profile(user)
      showLoading()
    }

    switchPublishBlog?.setOnCheckedChangeListener { view, isChecked ->
      val user = Resource.user
      if (isChecked) {
        user?.createBlogNotice = ON
      } else {
        user?.createBlogNotice = OFF
      }
      userVM?.profile(user)
      showLoading()
    }

    switchDefault?.setOnCheckedChangeListener { view, isChecked ->
      val user = Resource.user
      if (isChecked) {
        user?.noticeType = "-1"
        userVM?.profile(user)
        showLoading()
      } else {
        setNotice()
      }
    }

    switchSound?.setOnCheckedChangeListener { view, isChecked ->
      setNotice()
      switchDefault?.isChecked = false
    }

    switchShock?.setOnCheckedChangeListener { view, isChecked ->
      setNotice()
      switchDefault?.isChecked = false
    }

    switchLight?.setOnCheckedChangeListener { view, isChecked ->
      setNotice()
      switchDefault?.isChecked = false
    }

    showStatus()
  }

  private fun setNotice() {
    var noticeType = ""
    val sound = switchSound.isChecked
    val shock = switchShock.isChecked
    val light = switchLight.isChecked
    if (sound && shock && light) {
      noticeType = "1,2,3"
    } else if (sound && shock && !light) {
      noticeType = "1,2"
    } else if (sound && !shock && light) {
      noticeType = "1,3"
    } else if (!sound && sound && light) {
      noticeType = "2,3"
    } else if (sound && !shock && !light) {
      noticeType = "1"
    } else if (!sound && shock && !light) {
      noticeType = "2"
    } else if (!sound && !shock && light) {
      noticeType = "3"
    } else {
      noticeType = "-2"
    }
    val user = Resource.user
    user?.noticeType = noticeType
    userVM?.profile(user)
    showLoading()
  }

  private fun showStatus(profile: Profile? = Resource.user) {
    switchPraise.isChecked = profile?.praiseNotice == ON
    switchFollowed.isChecked = profile?.followNotice == ON
    switchBrowseHome.isChecked = profile?.broweHomeNotice == ON
    switchBrowseChannel.isChecked = profile?.broweChannelNotice == ON
    switchBrowseBlog.isChecked = profile?.broweBlogNotice == ON
    switchCreatedChannel.isChecked = profile?.createChannelNotice == ON
    switchPublishBlog.isChecked = profile?.createBlogNotice == ON
    switchDefault.isChecked = profile?.noticeType == "-1"
    switchSound?.isChecked = profile?.noticeType!!.contains("1")
    switchShock?.isChecked = profile?.noticeType!!.contains("2")
    switchLight?.isChecked = profile?.noticeType!!.contains("3")
  }
}