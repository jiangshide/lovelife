package com.android.sanskrit.mine.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.event.ZdEvent
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.REFRESH_MINE
import com.android.resource.Resource
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.channel.fragment.ChannelManagerBlogFragment
import com.android.sanskrit.user.UserFragment
import com.android.utils.ScreenUtil
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import com.android.widget.recycleview.divider.CommonItemDecoration
import kotlinx.android.synthetic.main.mine_album_fragment.mineAlbumRecycleView
import kotlinx.android.synthetic.main.mine_album_fragment_item.view.albumItemChannel
import kotlinx.android.synthetic.main.mine_album_fragment_item.view.albumItemDate
import kotlinx.android.synthetic.main.mine_album_fragment_item.view.albumItemFormat
import kotlinx.android.synthetic.main.mine_album_fragment_item.view.albumItemImg
import kotlinx.android.synthetic.main.mine_album_fragment_item.view.albumItemSize

/**
 * created by jiangshide on 2020/4/4.
 * email:18311271399@163.com
 */
class AlbumFragment(private val id:Long?= Resource.uid) : MyFragment() {

  private var adapter: KAdapter<Blog>? = null

  private var uid = id

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setView(R.layout.mine_album_fragment, true, true)
  }

  fun updateData(id: Long) {
    uid = id
    blogVM?.userBlog(uid = uid)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    blogVM?.userBlog(uid = uid)
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    ZdEvent.get()
        .with(MINE_ALBUM_REFRESH)
        .observes(this, Observer {
          autoRefresh()
        })
    blogVM!!.userBlog.observe(this, Observer {
      hiddle()
      cancelRefresh()
      clearAnimTab()
      if (it.error != null) {
        val h = ScreenUtil.getRtScreenHeight(context) / 3-100
        zdTipsView.root.setPadding(0, 0, 0, h)
        if ((adapter == null || adapter?.count() == 0)) {
          noNet("暂无相册!").setTipsRes(mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无相册!").setTipsRes(mipmap.no_data)
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
    blogVM?.userBlog(uid = uid,isRefresh = false)
  }

  private fun showView(data: MutableList<Blog>) {
    var isRandom = true
    isRandom = if (adapter?.datas() == null || adapter!!.datas().size == 0) {
      data.size > 10
    } else {
      adapter?.datas()!!.size > 10
    }

    enableLoadMore(data.size == blogVM?.size)
    if (!blogVM!!.isRefresh) {
      adapter?.add(data, blogVM!!.isRefresh)
      return
    }
    val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    adapter = mineAlbumRecycleView.create(data, R.layout.mine_album_fragment_item, {
      it.setImg(mActivity, albumItemFormat, albumItemSize, albumItemImg,it.cover, isRandom)
      albumItemDate.text = it.date
      if(!TextUtils.isEmpty(it.channel)){
        albumItemChannel.text = "# ${it.channel}"
        albumItemChannel.visibility = View.VISIBLE
      }else{
        albumItemChannel.visibility = View.GONE
      }
    }, {
      if(Resource.user == null){
        goFragment(UserFragment::class.java)
        return@create
      }
      push(ChannelManagerBlogFragment(uid,channelId,id))
    }, layoutManager)
  }
}

const val MINE_ALBUM_REFRESH = "mineAlbumRefresh"