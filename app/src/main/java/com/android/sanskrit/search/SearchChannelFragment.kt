package com.android.sanskrit.search

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.event.ZdEvent
import com.android.refresh.api.RefreshLayout
import com.android.resource.FOLLOW
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.blog.OnPraiseListener
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.channel.data.ChannelBlog
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.channel.CHANNEL_OFFICIAL_REFRESH
import com.android.sanskrit.channel.fragment.ChannelManagerBlogFragment
import com.android.sanskrit.mine.MineFragment
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.channel_type_fragment.channelTypeRecycleView
import kotlinx.android.synthetic.main.channel_type_fragment_item.view.channelItem
import kotlinx.android.synthetic.main.channel_type_fragment_item.view.channelItemFormat
import kotlinx.android.synthetic.main.channel_type_fragment_item.view.channelItemIcon
import kotlinx.android.synthetic.main.channel_type_fragment_item.view.channelItemImg
import kotlinx.android.synthetic.main.channel_type_fragment_item.view.channelItemLike
import kotlinx.android.synthetic.main.channel_type_fragment_item.view.channelItemName
import kotlinx.android.synthetic.main.channel_type_fragment_item.view.channelItemUserL

/**
 * created by jiangshide on 2020/7/19.
 * email:18311271399@163.com
 */
class SearchChannelFragment : MyFragment() {

  private var mTitle: String = ""

  private var adapter: KAdapter<ChannelBlog>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setView(R.layout.channel_type_fragment, true, true)
  }

  fun updateData(title: String) {
    this.mTitle = title
    channelVM?.channelType(name = mTitle)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    channelVM?.channelType(name = mTitle)
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    channelVM!!.channelType.observe(this, Observer {
      cancelRefresh()
      hiddle()
      if (it.error != null) {
        if (adapter == null || adapter?.count() == 0) {
          noNet("暂无频道!").setTipsRes(mipmap.no_data)
        } else if (channelVM!!.isRefresh) {
          noNet("暂无频道!").setTipsRes(mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })

    channelVM?.channelType(name = mTitle)
    showLoading()
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    channelVM?.channelType(name = mTitle)
    ZdEvent.get()
        .with(CHANNEL_OFFICIAL_REFRESH)
        .post(CHANNEL_OFFICIAL_REFRESH)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    channelVM?.channelType(name = mTitle, isRefresh = false)
  }

  private fun showView(data: MutableList<ChannelBlog>) {
    var isRandom = if (adapter?.datas() == null || adapter!!.datas().size == 0) {
      data.size > 10
    } else {
      adapter?.datas()!!.size > 10
    }
    enableLoadMore(data.size == channelVM?.size)
    val layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
    adapter = channelTypeRecycleView.create(data, R.layout.channel_type_fragment_item, {
      val channel = it
      if (it.blog != null) {
        channelItemLike.isSelected = it.blog!!.praises == 1
        channelItemLike.setOnClickListener {
          channel?.blog?.praises = 1 - channel?.blog?.praises!!
          if (channel?.blog?.praises == FOLLOW) {
            channel?.blog?.praiseNum = channel?.blog?.praiseNum!! + 1
          } else {
            channel?.blog?.praiseNum = channel?.blog?.praiseNum!! - 1
          }
          adapter?.notifyDataSetChanged()
          blogVM?.praiseAdd(channel?.blog!!, object : OnPraiseListener {
            override fun onPraise(blog: Blog) {
              hiddle()
              channel?.blog?.praiseNum = blog.praiseNum
              channel?.blog?.praises = blog.praises
              adapter?.notifyDataSetChanged()
            }
          })
          showLoading()
        }
        it?.blog?.showNum(channelItemLike, it.blog!!.praiseNum)
        it?.blog?.setImg(
            activity = mActivity, formatImg = channelItemFormat, coverImg = channelItemImg,
            isRandom = isRandom
        )
        it?.blog?.setUserInfo(channelItemIcon, channelItemName)
        channelItemUserL.setOnClickListener {
          if (channel?.blog?.uid == Resource.uid) return@setOnClickListener
          push(MineFragment(id = channel?.blog?.uid))
        }
      }
      if (!TextUtils.isEmpty(it.name)) {
        channelItem.text = "# ${it.name}"
        if (it.blogNum > 0) {
          channelItem.text = "# ${it.name}(${it.blogNum})"
        }
      }
    }, {
      push(ChannelManagerBlogFragment(uid, id, blogId = blog!!.id))
    }, layoutManager)
  }
}