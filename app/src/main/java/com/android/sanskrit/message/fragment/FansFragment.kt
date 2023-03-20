package com.android.sanskrit.message.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.http.exception.HttpException
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.user.OnFollowListener
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.mine.MineFragment
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.blog_praise_fragment.praiseRecycleView
import kotlinx.android.synthetic.main.blog_view_fragment_item.view.viewItemAge
import kotlinx.android.synthetic.main.blog_view_fragment_item.view.viewItemIcon
import kotlinx.android.synthetic.main.blog_view_fragment_item.view.viewItemName
import kotlinx.android.synthetic.main.blog_view_fragment_item.view.viewItemSubmit

/**
 * created by jiangshide on 2020/5/21.
 * email:18311271399@163.com
 */
class FansFragment :MyFragment(){

  private var adapter: KAdapter<User>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.blog_praise_fragment)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    userVM?.fans()
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    userVM!!.fans.observe(this, Observer {
      hiddle()
      cancelRefresh()
      clearAnimTab()
      if (it.error != null) {
        if (adapter == null || adapter!!.count() == 0) {
          noNet("暂无粉丝!").setTipsRes(mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无粉丝!").setTipsRes(mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })

    userVM?.fans()
    showLoading()
  }

  private fun showView(data: MutableList<User>) {
    enableLoadMore(data.size == blogVM?.size)
    val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
    adapter = praiseRecycleView.create(data, R.layout.blog_view_fragment_item, {
      val user = it
      Img.loadImageCircle(it.icon, viewItemIcon)
      viewItemName.text = it.nick
      it.setSex(viewItemAge)
      if (user.follows == 1 || user.id == Resource.uid) {
        viewItemSubmit.visibility = View.GONE
      } else {
        viewItemSubmit.visibility = View.VISIBLE
      }
      viewItemSubmit.setOnClickListener {
        user.follows = 1 - user.follows
        userVM?.followAdd(uid = user.id,status = user.follows,listener = object :OnFollowListener{
          override fun follow(
            status: Int,
            e: HttpException?
          ) {
            if(e != null){
              ZdToast.txt(e.message)
              return
            }
            user.follows = status
            adapter?.notifyDataSetChanged()
          }
        })
      }
    }, {
      if(id == Resource.uid)return@create
      push(MineFragment(id = id))
    }, layoutManager)
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    userVM?.fans()
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    userVM?.fans( isRefresh = false)
  }
}