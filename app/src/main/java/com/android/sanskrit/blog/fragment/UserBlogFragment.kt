package com.android.sanskrit.blog.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.refresh.api.RefreshLayout
import com.android.resource.REFRESH_MINE
import com.android.resource.Resource
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.blog.BlogFragment
import com.android.sanskrit.blog.adapter.FROM_MINE
import com.android.utils.ScreenUtil

/**
 * created by jiangshide on 2020/4/1.
 * email:18311271399@163.com
 */
class UserBlogFragment(
  private val from: Int = FROM_MINE,
  private val id: Long? = Resource.uid
) : BlogFragment(from = from) {

  private var uid = id

  override fun onTips(view: View?) {
    super.onTips(view)
    blogVM?.userBlog(uid = uid)
    showLoading()
  }

  fun updateData(id: Long) {
    uid = id
    blogVM?.userBlog(uid = uid)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    ZdEvent.get()
        .with(MINE_USER_BLOG_REFRESH)
        .observes(this, Observer {
          autoRefresh()
        })

    blogVM!!.userBlog.observe(this, Observer {
      cancelRefresh()
      hiddle()
      clearAnimTab()
      if (it.error != null) {
        val h = ScreenUtil.getRtScreenHeight(context) / 3-100
        zdTipsView.root.setPadding(0, 0, 0, h)
        if ((adapter == null || adapter?.count() == 0)) {
          noNet("暂无动态!").setTipsRes(mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无动态!").setTipsRes(mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })

    ZdEvent.get()
        .with(REFRESH_MINE)
        .observes(this, Observer {
          autoRefresh()
        })

    blogVM?.userBlog(uid = uid)
    showLoading()
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    blogVM?.userBlog(uid = uid)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    blogVM?.userBlog(uid = uid, isRefresh = false)
  }
}

const val MINE_USER_BLOG_REFRESH = "mineUserBlogRefresh"