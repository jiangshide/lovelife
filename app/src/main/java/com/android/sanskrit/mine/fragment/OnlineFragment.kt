package com.android.sanskrit.mine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.MEN
import com.android.resource.WOMEN
import com.android.resource.vm.channel.data.Channel
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.sanskrit.mine.MineFragment
import com.android.utils.StringUtil
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import com.android.widget.extension.setDrawableLeft
import kotlinx.android.synthetic.main.mine_online_fragment.mineOnlineRecycleView
import kotlinx.android.synthetic.main.mine_online_fragment_item.view.mineOnlineItemArrow
import kotlinx.android.synthetic.main.mine_online_fragment_item.view.mineOnlineItemIcon
import kotlinx.android.synthetic.main.mine_online_fragment_item.view.mineOnlineItemName
import kotlinx.android.synthetic.main.mine_online_fragment_item.view.mineOnlineItemSexAdder

/**
 * created by jiangshide on 2020/3/25.
 * email:18311271399@163.com
 */
class OnlineFragment : MyFragment() {

  private var adapter: KAdapter<User>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_online_fragment,true)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    val channel = arguments?.getParcelable<Channel>("data")

    userVM!!.onLine.observe(this, Observer {
      cancelRefresh()
      hiddle()
      if (it.error != null) {
        if (adapter == null || adapter?.datas() == null || adapter!!.datas().size == 0) {
          noData()
        }
        return@Observer
      }
      setTitle(channel?.name).setSmallTitle("${it.data.size}在线")
      showView(it.data)
    })
    userVM?.onLine()
    showLoading()
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    userVM?.onLine()
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    userVM?.onLine(isRefresh = false)
  }

  private fun showView(data: MutableList<User>) {
    adapter = mineOnlineRecycleView.create(data, R.layout.mine_online_fragment_item, {
      Img.loadImageCircle(it.icon, mineOnlineItemIcon, R.mipmap.default_user)
      mineOnlineItemName.text = it.nick
      mineOnlineItemSexAdder.text = getString(R.string.dot) + " ${it.city}"
      StringUtil.setDot(mineOnlineItemSexAdder, getString(R.string.dot))
      when (it.sex) {
        MEN -> {
          mineOnlineItemSexAdder.setDrawableLeft(R.mipmap.sex_man)
        }
        WOMEN -> {
          mineOnlineItemSexAdder.setDrawableLeft(R.mipmap.sex_women)
        }
      }
      mineOnlineItemArrow.text = it.intro
    }, {
      push(MineFragment())
    })
  }
}