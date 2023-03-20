package com.android.sanskrit.user.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.blog.adapter.BlogAdapter
import com.android.sanskrit.blog.adapter.FROM_AUDIO_RECOMMEND
import com.android.sanskrit.blog.adapter.FROM_FOLLOW
import com.android.sanskrit.blog.adapter.FROM_RECOMMEND
import com.android.utils.data.AUDIO
import kotlinx.android.synthetic.main.user_audio_recommend_fragment.audioRecommendRecycleView

/**
 * created by jiangshide on 2020/4/29.
 * email:18311271399@163.com
 */
class AudioRecommendFragment(private val isStatusBar: Boolean = false) : MyFragment() {

  private var adapter: BlogAdapter? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    isBackground = false
    return setView(R.layout.user_audio_recommend_fragment, true, true)
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
    if (isStatusBar) {
      audioRecommendRecycleView.setPadding(0, getDim(R.dimen.topBar), 0, 0)
    }
    blogVM!!.blogFormat.observe(this, Observer {
      cancelRefresh()
      hiddle()
      if (it.error != null) {
        if (adapter == null || adapter!!.count() == 0) {
          noNet("暂无推荐!").setTipsRes(R.mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无推荐!").setTipsRes(R.mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })

//    adapter = BlogAdapter(this, blogVM!!, this, audioRecommendRecycleView, FROM_AUDIO_RECOMMEND)
    adapter = BlogAdapter(this, blogVM!!, this, audioRecommendRecycleView, FROM_RECOMMEND)
    audioRecommendRecycleView.adapter = adapter
    request()
  }

  private fun request() {
    blogVM?.blogFormat(format = AUDIO)
    showLoading()
  }

  private fun showView(data: MutableList<Blog>) {
    adapter?.add(data as ArrayList<Blog>, blogVM!!.isRefresh)
    enableLoadMore(data.size == blogVM?.size)
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    blogVM?.blogFormat(format = AUDIO)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    blogVM?.blogFormat(format = AUDIO, isRefresh = false)
  }
}
