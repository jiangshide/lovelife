package com.android.sanskrit.mine.fragment.profile

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
import com.android.sanskrit.R
import com.android.utils.LogUtil
import com.android.widget.ZdButton
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.mine_profile_hobby_fragment.userProfileHobbyAdd
import kotlinx.android.synthetic.main.mine_profile_hobby_fragment.userProfileHobbyEdit
import kotlinx.android.synthetic.main.mine_profile_hobby_fragment.userProfileHobbyTag
import kotlinx.android.synthetic.main.mine_profile_hobby_fragment.userProfileHobbyTips

/**
 * created by jiangshide on 2020/5/1.
 * email:18311271399@163.com
 */
class EditHobbyFragment : MyFragment() {

  private var selectedData = ArrayList<String>()
  private var data = ArrayList<String>()
  private var name = ""
  private val MAX = 19

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_profile_hobby_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    setTitle("爱好").setRight("完成")
        .setRightEnable(false)
        .setRightListener {
          val user = Resource.user
          var hobby = ""
          selectedData?.forEachIndexed { index, s ->
            if (index == selectedData.size - 1) {
              hobby += "$s"
            } else {
              hobby += "$s,"
            }
          }
          user?.hobby = hobby
          LogUtil.e("user:", user)
          userVM!!.profile(user)
          showLoading()
        }

    val user = Resource.user
    data.addAll(resources.getStringArray(R.array.hobby))
    if (!TextUtils.isEmpty(user?.hobby)) {
      val selected = user?.hobby!!.split(",")
      selectedData.addAll(selected)
      selected.forEach {
        if (!data.contains(it)) {
          data.add(it)
        }
      }
    }
    showView()
    userProfileHobbyEdit.setListener { s, input ->
      this.name = input
    }
    userProfileHobbyAdd.setOnClickListener {
      if (!TextUtils.isEmpty(name) && !data.contains(name)) {
        if (data.size > MAX) {
          data.removeAt(0)
        }
        data.add(name)
        val last = MAX - data.size
        userProfileHobbyTips.text = "可自由添加后进行选择,限6/${last}项!"
        showView()
      }
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
      REFRESH_MINE
      pop()
    })
  }

  private fun showView() {
    val views = ArrayList<ZdButton>()
    userProfileHobbyTag.removeAllViews()
    data.forEachIndexed { index, type ->
      val view = getView(R.layout.report_fragment_item)
      val zdButton = view.findViewById<ZdButton>(R.id.reportItemName)
      zdButton.text = type
      view.id = index
      userProfileHobbyTag.addView(view)
      views.add(zdButton)
      if (selectedData.contains(type) && views.size - 1 <= index) {
        views[index].normalColor = R.color.black
        views[index].setTextColor(getColor(R.color.white))
      }
      views[index].setOnClickListener {
        if (selectedData.contains(type)) {
          selectedData.remove(type)
          views[index].normalColor = R.color.grayLight
          views[index].setTextColor(getColor(R.color.txtGray))
        } else {
          if (selectedData.size > 5) return@setOnClickListener
          selectedData.add(type)
          views[index].normalColor = R.color.black
          views[index].setTextColor(getColor(R.color.white))
        }
        setRightEnable(selectedData.size > 0)
      }
      views[index].setOnLongClickListener {
        selectedData.removeAt(index)
        showView()
        return@setOnLongClickListener true
      }
    }
  }
}