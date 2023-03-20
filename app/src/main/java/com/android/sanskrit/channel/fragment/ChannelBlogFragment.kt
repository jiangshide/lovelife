package com.android.sanskrit.channel.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.player.dplay.player.VideoViewManager
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.blog.adapter.BlogAdapter
import com.android.sanskrit.blog.adapter.FROM_CHANNEL
import com.android.utils.LogUtil
import kotlinx.android.synthetic.main.channel_blog_fragment.channelBlogRecycleView

/**
 * created by jiangshide on 2020/5/14.
 * email:18311271399@163.com
 */
class ChannelBlogFragment(
  private val uid:Long,
  private val channelId: Long,
  private val sort: Int = CHANNEL_BLOG_NEW
) : MyFragment() {

  private var adapter: BlogAdapter? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setView(R.layout.channel_blog_fragment, true, true)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    blogVM?.channelBlog(channelId = channelId, sort = sort)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    blogVM!!.channelBlog.observe(this, Observer {
      cancelRefresh()
      hiddle()
      if (it.error != null) {
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
        .with(CHANNEL_BLOG_REFRESH)
        .observes(this, Observer {
          autoRefresh()
        })

    request()
    adapter =
      BlogAdapter(this, blogVM!!, this, channelBlogRecycleView, FROM_CHANNEL,uid)
    channelBlogRecycleView.adapter = adapter
  }

  private fun request() {
    blogVM?.channelBlog(channelId = channelId, sort = sort)
    showLoading()
  }

  private fun showView(data: MutableList<Blog>) {
    adapter?.add(data as ArrayList<Blog>, blogVM!!.isRefresh)
    enableLoadMore(data.size == 20)
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    blogVM?.channelBlog(channelId = channelId, sort = sort)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    blogVM?.channelBlog(channelId = channelId, sort = sort, isRefresh = false)
  }

  override fun onResume() {
    super.onResume()
    VideoViewManager.instance()
      .pause()
  }

  override fun onPause() {
    super.onPause()
    if (Resource.TAB_INDEX == 0) {
      VideoViewManager.instance()
        .resume()
    }
  }
}