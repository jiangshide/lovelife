package com.android.sanskrit.search

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.mine.MineFragment
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.publish_users.usesEdit
import kotlinx.android.synthetic.main.publish_users.usesRecycleView
import kotlinx.android.synthetic.main.publish_users_item.view.usersDes
import kotlinx.android.synthetic.main.publish_users_item.view.usersIcon
import kotlinx.android.synthetic.main.publish_users_item.view.usersNick

/**
 * created by jiangshide on 2020/7/19.
 * email:18311271399@163.com
 */
class SearchUserFragment : MyFragment() {

  private var adapter: KAdapter<User>? = null

  private var mTitle: String = ""

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.publish_users, true, true)
  }

  fun updateData(title: String) {
    this.mTitle = title
    userVM?.users(name = mTitle)
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
          noNet("暂无发现!").setTipsRes(mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无发现!").setTipsRes(mipmap.no_data)
          enableLoadMore(false)
        }
        return@Observer
      }
      showView(it.data)
    })

    userVM?.users(name = mTitle)
    showLoading()
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    userVM?.users(name = mTitle)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    userVM?.users(name = mTitle, isRefresh = false)
  }

  private fun showView(data: MutableList<User>) {
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
    }, {
      push(MineFragment(id = id))
    })
  }
}