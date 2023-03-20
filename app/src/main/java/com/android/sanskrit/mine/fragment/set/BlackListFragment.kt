package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.vm.user.data.Friend
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.home.HOME_REFRESH
import com.android.utils.LogUtil
import com.android.widget.ZdDialog
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.mine_set_blacklist_fragment.blackListRecycleView
import kotlinx.android.synthetic.main.mine_set_blacklist_fragment_item.view.blackListItemChannel
import kotlinx.android.synthetic.main.mine_set_blacklist_fragment_item.view.blackListItemDate
import kotlinx.android.synthetic.main.mine_set_blacklist_fragment_item.view.blackListItemIcon
import kotlinx.android.synthetic.main.mine_set_blacklist_fragment_item.view.blackListItemName
import kotlinx.android.synthetic.main.mine_set_blacklist_fragment_item.view.blackListItemReason

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class BlackListFragment : MyFragment() {

  private var adapter: KAdapter<Friend>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_blacklist_fragment, true, true)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    userVM?.friend(status = -1)
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    userVM!!.friendAdd.observe(this, Observer {
      if(it.error != null){
        ZdToast.txt(it.error.message)
        return@Observer
      }
      ZdEvent.get().with(HOME_REFRESH).post(HOME_REFRESH)
      autoRefresh()
    })

    userVM!!.friend.observe(this, Observer {
      cancelRefresh()
      hiddle()
      if (it.error != null) {
        if ((adapter == null || adapter?.count() == 0)) {
          noNet("暂无黑名单!").setTipsRes(mipmap.no_data)
        } else if (userVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无黑名单!").setTipsRes(mipmap.no_data)
        }else{
        }
        return@Observer
      }
      showView(it.data)
    })
    userVM?.friend(status = -1)
    showLoading()
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    userVM?.friend(status = -1)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    userVM?.friend(status = -1, isRefresh = false)
  }

  private fun showView(data: MutableList<Friend>) {
    enableLoadMore(data.size == userVM?.size)
    if (adapter != null) {
      adapter?.add(data, userVM!!.isRefresh)
      return
    }
    adapter = blackListRecycleView.create(data, R.layout.mine_set_blacklist_fragment_item, {
      Img.loadImageCircle(it.icon, blackListItemIcon)
      blackListItemName.text = it.nick
      blackListItemChannel.text = "已产生内容(频道:${it.channelNum},动态:${it.blogNum})"
      it.setDate(blackListItemDate)
      blackListItemReason.setTxt(it.reason)
    }, {
      removeBlack(this)
    })
  }

  private fun removeBlack(friend: Friend){
    val list = arrayListOf("移除黑名单","添加为好友")
    ZdDialog.createList(mActivity, list, arrayListOf(R.color.black,R.color.redLight)).setOnItemListener { parent, view, position, id ->
      when(list[position]){
        "移除黑名单"->{
          userVM?.friendAdd(uid=friend.uid,status = 0)
        }
        "添加为好友"->{
          userVM?.friendAdd(uid=friend.uid,status = 1)
        }
      }
    }.show()
  }
}