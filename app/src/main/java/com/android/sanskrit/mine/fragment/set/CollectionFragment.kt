package com.android.sanskrit.mine.fragment.set

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
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.blog.adapter.BlogAdapter
import com.android.sanskrit.blog.adapter.FROM_COLLECTION
import com.android.sanskrit.home.HOME_REFRESH
import kotlinx.android.synthetic.main.mine_set_collection_fragment.mineBlogCollectionRecycleView

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class CollectionFragment : MyFragment() {

  private var adapter: BlogAdapter? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_collection_fragment, true)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    blogVM?.collectionBlog()
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    blogVM!!.collectionBlog.observe(this, Observer {
      cancelRefresh()
      hiddle()
      if (it.error != null) {
        if ((adapter == null || adapter?.count() == 0)) {
          noNet("暂无收藏!").setTipsRes(mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无收藏!").setTipsRes(mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })

    ZdEvent.get()
        .with(HOME_REFRESH)
        .observes(this, Observer {
          autoRefresh()
        })

    blogVM?.collectionBlog()
    showLoading()
  }

  private fun showView(data: MutableList<Blog>) {
    adapter = BlogAdapter(this, blogVM!!, this, mineBlogCollectionRecycleView, FROM_COLLECTION)
    mineBlogCollectionRecycleView.adapter = adapter
    adapter?.add(data as ArrayList<Blog>, blogVM!!.isRefresh)
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    blogVM?.collectionBlog()
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    blogVM?.collectionBlog(isRefresh = false)
  }
}