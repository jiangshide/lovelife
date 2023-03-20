package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.REFRESH_MEMORY
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.blog.adapter.BlogAdapter
import com.android.sanskrit.blog.adapter.FROM_MEMORY
import kotlinx.android.synthetic.main.mine_set_memory_fragment.memoryRecycleView

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class MemoryFragment : MyFragment() {

  private var adapter: BlogAdapter? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_memory_fragment, true)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    blogVM?.userBlog(status = -1)
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    blogVM!!.userBlog.observe(this, Observer {
      cancelRefresh()
      hiddle()
      if (it.error != null) {
        if ((adapter == null || adapter?.count() == 0)) {
          noNet("暂无数据!").setTipsRes(mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无数据!").setTipsRes(mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })
    ZdEvent.get()
        .with(REFRESH_MEMORY)
        .observes(this, Observer {
          autoRefresh()
        })

    adapter = BlogAdapter(this, blogVM!!, this, memoryRecycleView, FROM_MEMORY)
    memoryRecycleView.adapter = adapter
    blogVM?.userBlog(status = -1)
    showLoading()
  }

  private fun showView(data: MutableList<Blog>? = null) {
    adapter?.add(data, blogVM!!.isRefresh)
    enableLoadMore((data != null && data?.size == 20))
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    blogVM?.userBlog(status = -1)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    blogVM?.userBlog(status = -1, isRefresh = false)
  }
}