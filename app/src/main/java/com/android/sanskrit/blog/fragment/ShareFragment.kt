package com.android.sanskrit.blog.fragment

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
import kotlinx.android.synthetic.main.blog_share_fragment.shareRecycleView
import kotlinx.android.synthetic.main.blog_view_fragment_item.view.viewItemAge
import kotlinx.android.synthetic.main.blog_view_fragment_item.view.viewItemIcon
import kotlinx.android.synthetic.main.blog_view_fragment_item.view.viewItemName
import kotlinx.android.synthetic.main.blog_view_fragment_item.view.viewItemSubmit

/**
 * created by jiangshide on 2020/5/10.
 * email:18311271399@163.com
 */
class ShareFragment(private val blogId: Long = 0) : MyFragment() {

  private var adapter: KAdapter<User>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setView(R.layout.blog_share_fragment, true, true)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    blogVM?.share(blogId)
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    blogVM!!.share.observe(this, Observer {
      hiddle()
      cancelRefresh()
      if (it.error != null) {
        if (adapter == null || adapter?.count() == 0 || blogVM!!.isRefresh) {
          noNet("暂无点赞!").setTipsRes(mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })

    blogVM?.share(blogId)
    showLoading()
  }

  private fun showView(data: MutableList<User>) {
    enableLoadMore(data.size == blogVM?.size)
    val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
    adapter = shareRecycleView.create(data, R.layout.blog_view_fragment_item, {
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
    blogVM?.share(blogId)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    blogVM?.share(blogId, isRefresh = false)
  }
}