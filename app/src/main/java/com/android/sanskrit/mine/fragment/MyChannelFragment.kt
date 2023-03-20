package com.android.sanskrit.mine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.CHANNEL_COMMON
import com.android.resource.CHANNEL_PRIVATE
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.channel.data.ChannelBlog
import com.android.sanskrit.R
import com.android.sanskrit.channel.fragment.ChannelManagerBlogFragment
import com.android.sanskrit.user.UserFragment
import com.android.utils.LogUtil
import com.android.utils.ScreenUtil
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.mine_channel_fragment.*
import kotlinx.android.synthetic.main.mine_channel_fragment_item.view.*

/**
 * created by jiangshide on 2020/3/25.
 * email:18311271399@163.com
 */
class MyChannelFragment(private val status: Int = 2, private val id: Long? = Resource.uid) :
    MyFragment() {

    private var adapter: KAdapter<ChannelBlog>? = null

    private var uid = id

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.mine_channel_fragment, true)
    }

    fun updateData(id: Long) {
        uid = id
        channelVM?.channelUser(uid = uid, status = status)
    }

    override fun onTips(view: View?) {
        super.onTips(view)
        channelVM?.channelUser(uid = uid, status = status)
        showLoading()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        ZdEvent.get().with(MINE_MY_CHANNEL_REFRESH).observes(this, Observer {
            autoRefresh()
        })
        channelVM!!.channelUser.observe(this, Observer {
            cancelRefresh()
            hiddle()
            clearAnimTab()
            if (it.error != null) {
                val h = ScreenUtil.getRtScreenHeight(context) / 3 - 100
                zdTipsView.root.setPadding(0, 0, 0, h)
                if (adapter == null || adapter?.count() == 0) {
                    noNet("暂无创建的频道").setTipsRes(R.mipmap.no_data)
                } else if (channelVM!!.isRefresh) {
                    adapter?.clear()
                    noNet("暂无创建的频道").setTipsRes(R.mipmap.no_data)
                }
                return@Observer
            }
            showView(it.data)
        })
        channelVM?.channelUser(uid = uid, status = status)
        showLoading()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        LogUtil.e("uid:", Resource.uid, " | user:", Resource.user)
        channelVM?.channelUser(uid = uid, status = status)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        channelVM?.channelUser(uid = uid, status = status, isRefresh = false)
    }

    private fun showView(data: MutableList<ChannelBlog>) {
        enableLoadMore(data.size == channelVM?.size)
        if (adapter != null && !channelVM!!.isRefresh) {
            adapter?.add(data, channelVM!!.isRefresh)
            return
        }
        adapter =
            mineChannelRecycleView.create(data, R.layout.mine_channel_fragment_item, {
                Img.loadImageRound(it.cover, myChannelItemIcon, 5, R.mipmap.default_user)
                myChannelItemName.text = it.name
                when (it.natureId) {
                    CHANNEL_PRIVATE -> {
                        myChannelItemType.text = "私有"
                    }
                    CHANNEL_COMMON -> {
                        myChannelItemType.text = "公共"
                    }
                }
                if (it.uid == Resource.uid) {
                    myChannelItemType.visibility = View.VISIBLE
                } else {
                    myChannelItemType.visibility = View.GONE
                }
                myChannelItemDes.text = it.des
                myChannelItemDate.text = it.date
                myChannelItemStatus.text = "已产生(${it.blogNum})条"
            },
                {
                    if (Resource.user == null) {
                        goFragment(UserFragment::class.java)
                        return@create
                    }
                    if (blogNum == 0) {
                        ZdToast.txt("暂无动态！赶快去发布动态吧!")
                        return@create
                    }
                    push(ChannelManagerBlogFragment(uid, id, id))
                })
    }
}

const val MINE_MY_CHANNEL_REFRESH = "mineMyChannelRefresh"