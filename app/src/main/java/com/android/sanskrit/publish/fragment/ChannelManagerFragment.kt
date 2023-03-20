package com.android.sanskrit.publish.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.resource.MyFragment
import com.android.resource.vm.channel.data.Channel
import com.android.resource.vm.channel.data.ChannelBlog
import com.android.resource.vm.channel.data.ChannelType
import com.android.sanskrit.R
import com.android.tablayout.indicators.LinePagerIndicator
import kotlinx.android.synthetic.main.publish_channelmanager_fragment.*

/**
 * created by jiangshide on 2020/3/25.
 * email:18311271399@163.com
 */
class ChannelManagerFragment(private val listener:OnChannelListener?=null) : MyFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.publish_channelmanager_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setTopBar(searchRootL)
        publishSearchChannelListEdit.setListener { s, input ->

        }
        publishSearchChannelListCancel.setOnClickListener {
            pop()
        }

        channelVM!!.channelTypes.observe(this, Observer {
            cancelRefresh()
            hiddle()
            if (it.error != null) {
                noData()
                return@Observer
            }
            showView(it.data)
        })
        channelVM?.channelTypes(-1)
        showLoading()
    }

    private fun showView(data: ArrayList<ChannelType>) {
        var list = ArrayList<String>()
        var fragmens = ArrayList<ChannelListFragment>()
        list.add("我的")
        fragmens.add(ChannelListFragment(listener=listener))
        data.forEach {
            list.add(it.name)
            fragmens.add(ChannelListFragment(it.id))
        }
        publishChannelListViewPager.adapter =
            publishChannelListViewPager.create(childFragmentManager)
                .setTitles(
                    list
                )
                .setFragment(
                    fragmens
                )
                .initTabs(activity!!, tabsPublishChannelList, publishChannelListViewPager)
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .setTxtSelecteSize(12)
                .setTxtSelectedSize(15)
                .setLinePagerIndicator(getColor(R.color.blue))
    }
}

interface OnChannelListener{
    fun onChannel(channelBlog: ChannelBlog)
}