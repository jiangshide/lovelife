package com.android.sanskrit.mine.fragment.channelmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.*
import com.android.resource.vm.channel.data.ChannelBlog
import com.android.sanskrit.R
import com.android.sanskrit.channel.fragment.ChannelManagerBlogFragment
import com.android.sanskrit.channel.fragment.CreateChannelFragment
import com.android.utils.LogUtil
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.mine_set_channelmanager_create_fragment.*
import kotlinx.android.synthetic.main.mine_set_channelmanager_create_fragment_item.view.*

/**
 * created by jiangshide on 2020/4/9.
 * email:18311271399@163.com
 */
class MyCreateFragment : MyFragment() {

    private var adapter: KAdapter<ChannelBlog>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.mine_set_channelmanager_create_fragment, true, true)
    }

    override fun onTips(view: View?) {
        super.onTips(view)
        channelVM?.channelUser(status = -1)
        showLoading()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        channelVM!!.channelUser.observe(this, Observer {
            hiddle()
            cancelRefresh()
            LogUtil.e("-err:", it.error, " | data:", it.data)
            if (it.error != null) {
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

        ZdEvent.get()
            .with(MYCHANNEL_REFRESH)
            .observes(this, Observer {
                autoRefresh()
            })

        channelVM?.channelUser(status = -1)
        showLoading()
    }

    private fun showView(data: MutableList<ChannelBlog>) {
        enableLoadMore(data.size == channelVM?.size)
        if (adapter != null) {
            adapter?.add(data, channelVM!!.isRefresh)
            return
        }
        adapter =
            createChannelRecycleView.create(data,
                R.layout.mine_set_channelmanager_create_fragment_item,
                {
                    val channel = it
                    swipeMenuLayout.isSwipeEnable = true
                    channelStatus.setOnClickListener {
                        ZdToast.txt("只针对高级会员享开放!")
                    }
                    channelTop.setOnClickListener {
                        ZdToast.txt("只针对高级会员享开放!")
                    }
                    channelRefresh.setOnClickListener {
                        ZdToast.txt("只针对高级会员享开放!")
                    }
                    channelDel.setOnClickListener {
                        ZdToast.txt("只针对高级会员享开放!")
                    }
                    createChannelItemIcon.setOnClickListener {
                        push(ChannelManagerBlogFragment(channel?.uid,channel?.id))
                    }
                    content.setOnClickListener {
                        push(CreateChannelFragment(), channel)
                    }
                    Img.loadImageRound(it.cover, createChannelItemIcon, 5, R.mipmap.logo)
                    createChannelItemName.text = it.name
                    createChannelItemType.visibility = View.VISIBLE
                    when (it.natureId) {
                        CHANNEL_PRIVATE -> {
                            createChannelItemType.text = "私有"
                            channelStatus.text = "私有"
                        }
                        CHANNEL_COMMON -> {
                            createChannelItemType.text = "公共"
                            channelStatus.text = "公开"
                        }
                    }
                    createChannelItemDes.text = it.des
                    createChannelItemDate.text = it.date
                    when (it.status) {
                        AUDIT_UNDER -> {
                            createChannelItemStatus.text = getString(R.string.audit_under)
                        }
                        AUDIT_PASS -> {
                            if (channel.blogNum > 0) {
                                createChannelItemStatus.text = "已产生${channel.blogNum}条"
                            } else {
                                createChannelItemStatus.text = getString(R.string.audit_pass)
                            }
                        }
                        RECYCLE -> {
                            createChannelItemStatus.text = getString(R.string.recycle)
                        }
                        AUDIT_REJECTED -> {
                            createChannelItemStatus.text = getString(R.string.audit_rejected)
                        }
                        FORBIDDEN_WORDS -> {
                            createChannelItemStatus.text = getString(R.string.forbidden_words)
                        }
                    }
                    createChannelItemIcon.setOnClickListener {
                        if (channel.blog != null) {
                            push(
                                ChannelManagerBlogFragment(
                                    channel?.uid,
                                    channel?.id,
                                    channel?.blog!!.id
                                )
                            )
                        }
                    }
                })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        channelVM?.channelUser(status = -1)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        channelVM?.channelUser(status = -1, isRefresh = false)
    }
}

const val MYCHANNEL_REFRESH = "myChannelRefresh"