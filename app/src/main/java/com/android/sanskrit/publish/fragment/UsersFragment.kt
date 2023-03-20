package com.android.sanskrit.publish.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.utils.LogUtil
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.publish_users.usesEdit
import kotlinx.android.synthetic.main.publish_users.usesRecycleView
import kotlinx.android.synthetic.main.publish_users.usesSelectedRecycleView
import kotlinx.android.synthetic.main.publish_users_item.view.userCheck
import kotlinx.android.synthetic.main.publish_users_item.view.usersDes
import kotlinx.android.synthetic.main.publish_users_item.view.usersIcon
import kotlinx.android.synthetic.main.publish_users_item.view.usersNick
import kotlinx.android.synthetic.main.publish_users_item.view.usersR
import kotlinx.android.synthetic.main.publish_users_item_icon.view.usersSelectedIcon

/**
 * created by jiangshide on 2020/7/8.
 * email:18311271399@163.com
 */
class UsersFragment(private val users: MutableList<User>? = null) : MyFragment() {

  private var adapter: KAdapter<User>? = null
  private var selectedAdapter: KAdapter<User>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.publish_users, true, true)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    usesEdit.setListener { s, input ->
      if (TextUtils.isEmpty(input)) {
        userVM?.users()
      } else {
        userVM?.users(name = input)
      }
    }

    userVM!!.users.observe(this, Observer {
      hiddle()
      cancelRefresh()
      clearAnimTab()
      if (it.error != null) {
        if (adapter == null || adapter!!.count() == 0) {
          noNet("暂无发现!").setTipsRes(R.mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无发现!").setTipsRes(R.mipmap.no_data)
          enableLoadMore(false)
        }
        return@Observer
      }
      showView(it.data)
    })

    showSelectedView()
    userVM?.users()
    showLoading()
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    userVM?.users()
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    userVM?.users(isRefresh = false)
  }

  private fun showView(data: MutableList<User>) {
    data?.forEach { newUser ->
      selectedAdapter?.datas()
          ?.forEach {
            if (newUser.nick == it.nick) {
              newUser.selected = true
            }
          }
    }
    enableLoadMore(data.size == userVM?.size)
    if (adapter != null) {
      adapter?.add(data, userVM!!.isRefresh)
      return
    }
    adapter = usesRecycleView.create(data, R.layout.publish_users_item, {
      val user = it
      Img.loadImageCircle(it.icon, usersIcon)
      usersNick.text = it.nick
      it.setSex(usersDes)
      userCheck.isSelected = it.selected
      if (it.id == Resource?.uid){
        userCheck.visibility = View.GONE
      }else{
        userCheck.visibility = View.VISIBLE
      }
    }, {
      if (id == Resource?.uid) return@create
      selected = !selected
      if (selected) {
        if (selectedAdapter!!.count() >= 10) {
          return@create
        }
        var isAdd = true
        selectedAdapter?.datas()
            ?.forEach {
              if (nick == it.nick) {
                isAdd = false
                return@forEach
              }
            }
        if (isAdd) {
          selectedAdapter?.add(this)
        }
      } else {
        selectedAdapter?.remove(this)
      }
      adapter?.notifyDataSetChanged()
      setStatus()
      LogUtil.e(
          "-------this:", this, " | adapter:", selectedAdapter?.count(), " | selected:", selected
      )
    })
  }

  private fun setStatus() {
    setRight("确定(${selectedAdapter?.count()}/10)").setRightListener {
      ZdEvent.get()
          .with(USERS)
          .post(selectedAdapter!!.datas())
      pop()
    }
  }

  private fun showSelectedView() {
    val layoutManager = LinearLayoutManager(mActivity)
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
    selectedAdapter =
      usesSelectedRecycleView.create(arrayListOf(), R.layout.publish_users_item_icon, {
        Img.loadImageCircle(it.icon, usersSelectedIcon)
      }, {
        selectedAdapter?.remove(this)
        setStatus()
        adapter?.notifyDataSetChanged()
      }, layoutManager)
    if (users != null) {
      selectedAdapter?.add(users)
    }
  }
}

const val USERS = "USERS"