package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.sanskrit.mine.fragment.channelmanager.MyCreateFragment
import com.android.tablayout.indicators.LinePagerIndicator
import kotlinx.android.synthetic.main.mine_set_channelmanager_fragment.*

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class MyChannelManagerFragment : MyFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.mine_set_channelmanager_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        channelManagerViewPager.adapter = channelManagerViewPager.create(childFragmentManager)
            .setTitles(
                "我关注的", "我创建的"
            )
            .setFragment(
                FollowChannelFragment(),
                MyCreateFragment()
            )
            .initTabs(activity!!, tabsChannelManager, channelManagerViewPager, true)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelecteSize(12)
            .setTxtSelectedSize(15)
            .setLinePagerIndicator(getColor(R.color.blue))
    }
}