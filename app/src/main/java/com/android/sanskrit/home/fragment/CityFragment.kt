package com.android.sanskrit.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.blog.adapter.BlogAdapter
import com.android.sanskrit.blog.adapter.FROM_CITY
import com.android.sanskrit.home.HOME_REFRESH
import kotlinx.android.synthetic.main.recycleview.zdRecycleView

/**
 * created by jiangshide on 2020/4/1.
 * email:18311271399@163.com
 */
class CityFragment : MyFragment() {

  private var adapter: BlogAdapter? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setView(R.layout.recycleview, true, true)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    request()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    zdRecycleView.setPadding(0, getDim(R.dimen.topBar), 0, 0)
    blogVM!!.cityBlog.observe(this, Observer {
      cancelRefresh()
      hiddle()
      if (it.error != null) {
        if ((adapter == null || adapter?.count() == 0) || blogVM!!.isRefresh) {
          noNet("暂无动态!").setTipsRes(R.mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })

    ZdEvent.get()
        .with(HOME_REFRESH)
        .observes(this, Observer {
          blogVM?.cityBlog()
        })
    request()
    adapter = BlogAdapter(this, blogVM!!, this, zdRecycleView, FROM_CITY)
    zdRecycleView.adapter = adapter
  }

  private fun request(){
    blogVM?.cityBlog()
    showLoading()
  }

  private fun showView(data: MutableList<Blog>) {
    adapter?.add(data as ArrayList<Blog>, blogVM!!.isRefresh)
    enableLoadMore(data.size ==blogVM?.size)
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    blogVM?.cityBlog()
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    blogVM?.cityBlog(isRefresh = false)
  }
}