package com.android.sanskrit.search

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.android.refresh.api.RefreshLayout
import com.android.sanskrit.R
import com.android.sanskrit.blog.BlogFragment

/**
 * created by jiangshide on 2020/7/19.
 * email:18311271399@163.com
 */
class SearchBlogFragment : BlogFragment() {

  private var mTitle: String = ""

  override fun onTips(view: View?) {
    super.onTips(view)
    request()
  }

  fun updateData(title: String) {
    this.mTitle = title
    blogVM?.blogFormat(title = mTitle)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    blogVM!!.blogFormat.observe(this, Observer {
      cancelRefresh()
      hiddle()
      clearAnimTab()
      if (it.error != null) {
        if (adapter == null || adapter!!.count() == 0) {
          noNet("暂无关注!").setTipsRes(R.mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无关注!").setTipsRes(R.mipmap.no_data)
          enableLoadMore(false)
        }
        return@Observer
      }
      showView(it.data)
    })
    request()
  }

  private fun request() {
    blogVM?.blogFormat(title = mTitle)
    showLoading()
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    blogVM?.blogFormat(title = mTitle)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    blogVM?.blogFormat(title = mTitle, isRefresh = false)
  }
}