package com.android.sanskrit.publish.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.CHANNEL_PRIVATE
import com.android.resource.MyFragment
import com.android.resource.vm.channel.data.ChannelBlog
import com.android.sanskrit.R
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.publish_channelmanager_list_fragment.*
import kotlinx.android.synthetic.main.publish_channelmanager_list_fragment_item.view.*

/**
 * created by jiangshide on 2020/3/27.
 * email:18311271399@163.com
 */
class ChannelListFragment(
    private val fromId: Int = 0,
    private val listener: OnChannelListener? = null
) : MyFragment() {

    private var adapter: KAdapter<ChannelBlog>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.publish_channelmanager_list_fragment, true, true)
    }

    override fun onTips(view: View?) {
        super.onTips(view)
        request(isLoading = true, isRefresh = true)
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
                    noNet("暂无频道!").setTipsRes(R.mipmap.no_data)
                } else if (channelVM!!.isRefresh) {
                    noNet("暂无频道!").setTipsRes(R.mipmap.no_data)
                }
                return@Observer
            }
            showView(it.data)
        })

        channelVM!!.channelUser.observe(this, Observer {
            cancelRefresh()
            hiddle()
            if (it.error != null) {
                if (adapter == null || adapter?.count() == 0) {
                    noNet("暂无频道!").setTipsRes(R.mipmap.no_data)
                } else if (channelVM!!.isRefresh) {
                    adapter?.clear()
                    noNet("暂无频道!").setTipsRes(R.mipmap.no_data)
                }
                return@Observer
            }
            showView(it.data)
        })

        request(isLoading = true, isRefresh = true)
    }

    private fun request(
        isLoading: Boolean,
        isRefresh: Boolean = true
    ) {
        if (fromId > 0) {
            channelVM?.channelType(id = fromId, isRefresh = isRefresh)
        } else {
            channelVM?.channelUser()
        }
        if (isLoading) {
            showLoading()
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        request(isLoading = false, isRefresh = true)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        request(isLoading = false, isRefresh = false)
    }

    private fun showView(data: MutableList<ChannelBlog>) {
        adapter =
            publishChannelListRecycleView.create(data,
                R.layout.publish_channelmanager_list_fragment_item,
                {
                    Img.loadImageRound(it.cover, channelListItemIcon, 5)
                    channelListItemName.text = it.name
                    channelListItemType.text =
                        if (it.natureId == CHANNEL_PRIVATE) "私有频道" else "公共频道"
                    channelListItemDes.text = it.des
                },
                {
                    if (listener != null) {
                        listener?.onChannel(this)
                    } else {
                        ZdEvent.get()
                            .with(CHANNEL)
                            .post(this)
                    }
                    pop()
                })
    }
}

const val CHANNEL = "channel"