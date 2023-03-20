package com.android.sanskrit.message.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.http.exception.HttpException
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.FOLLOW
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.user.OnFollowListener
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.message.MESSAGE_FOLLOWED_REFRESH
import com.android.sanskrit.mine.MineFragment
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.message_follow_fragment.followRecycleView
import kotlinx.android.synthetic.main.message_follow_fragment_item.view.followItemDes
import kotlinx.android.synthetic.main.message_follow_fragment_item.view.followItemIcon
import kotlinx.android.synthetic.main.message_follow_fragment_item.view.followItemName
import kotlinx.android.synthetic.main.message_follow_fragment_item.view.followItemStatus

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class FollowFragment(
  private val action: Int = 0,
  private val refresh: String
) : MyFragment() {

  private var adapter: KAdapter<User>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setView(R.layout.message_follow_fragment, true, true)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    userVM?.follow(action = action)
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    userVM!!.follow.observe(this, Observer {
      cancelRefresh()
      hiddle()
      if (it.error != null) {
        if ((adapter == null || adapter?.count() == 0)) {
          if (action == FOLLOW) {
            noNet("暂无关注!").setTipsRes(mipmap.no_data)
          } else {
            noNet("暂无推荐关注!").setTipsRes(mipmap.no_data)
          }
        } else if (userVM!!.isRefresh) {
          adapter?.clear()
          if (action == FOLLOW) {
            noNet("暂无关注!").setTipsRes(mipmap.no_data)
          } else {
            noNet("暂无推荐关注!").setTipsRes(mipmap.no_data)
          }
        }
        return@Observer
      }
      showView(it.data)
    })

    ZdEvent.get()
        .with(refresh)
        .observes(this, Observer {
          autoRefresh()
        })

    userVM?.follow(action = action)
    showLoading()
  }

  private fun showView(data: MutableList<User>) {
    enableLoadMore(data.size == userVM?.size)
    adapter = followRecycleView.create(data, R.layout.message_follow_fragment_item, {
      val user = it
      Img.loadImageCircle(it.icon, followItemIcon,R.mipmap.default_user)
      followItemName.text = it.nick
      it.setSex(followItemDes)
      if (action == FOLLOW) {
        followItemStatus.text = "私信TA"
        followItemStatus.setOnClickListener {
          push(ChatFragment().setTitle(user.nick), set("userName", user.name))
        }
      } else {
        followItemStatus.text = "关注TA"
        if(user.id == Resource.uid){
          followItemStatus.visibility = View.GONE
        }else{
          followItemStatus.visibility = View.VISIBLE
        }
        followItemStatus.setOnClickListener {
          user.follows = FOLLOW
          userVM?.followAdd(
              uid = user.id, status = user.follows, listener = object : OnFollowListener {
            override fun follow(
              status: Int,
              e: HttpException?
            ) {
              if (e != null) {
                ZdToast.txt(e.message)
                return
              }
              adapter?.remove(user)
              ZdEvent.get()
                  .with(MESSAGE_FOLLOWED_REFRESH)
                  .post(MESSAGE_FOLLOWED_REFRESH)
            }
          })
        }
      }
    }, {
      push(MineFragment(id = id))
    })
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    userVM?.follow(action = action)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    userVM?.follow(action = action, isRefresh = false)
  }
}