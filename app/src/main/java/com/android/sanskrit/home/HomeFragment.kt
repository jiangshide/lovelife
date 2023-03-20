package com.android.sanskrit.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.event.ZdEvent
import com.android.player.dplay.player.VideoViewManager
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.view.textbanner.SimpleTextBannerAdapter
import com.android.resource.vm.blog.OnWordListener
import com.android.resource.vm.channel.data.Word
import com.android.sanskrit.R
import com.android.sanskrit.R.layout
import com.android.sanskrit.home.fragment.FollowFragment
import com.android.sanskrit.home.fragment.RecommendFragment
import com.android.sanskrit.home.fragment.VideoFragment
import com.android.sanskrit.search.ScanFragment
import com.android.sanskrit.search.SearchFragment
import com.android.sanskrit.user.UserFragment
import com.android.tablayout.indicators.LinePagerIndicator
import com.android.utils.LogUtil
import com.android.utils.SPUtil
import com.android.widget.adapter.ZdFragmentPagerAdapter
import kotlinx.android.synthetic.main.home_fragment.*

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class HomeFragment : MyFragment(), OnPageChangeListener, OnWordListener {

    private var zdFragmentPagerAdapter: ZdFragmentPagerAdapter? = null
    private var bannerAdapter: SimpleTextBannerAdapter? = null

    private var followFragment: FollowFragment? = null
    private var recommendFragment: RecommendFragment? = null
    private var videoFragment: VideoFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.home_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setTopBar(honeTopL)
        followFragment = FollowFragment(textBanner, homeLoading)
        recommendFragment = RecommendFragment(textBanner, homeLoading)
        videoFragment = VideoFragment(textBanner, homeLoading)
        zdFragmentPagerAdapter = homeViewPager.create(childFragmentManager)
            .setTitles(
                "关注", "推荐", "发现"
            )
            .setFragment(
                followFragment,
                recommendFragment,
//            FindFragment(),
//            AudioRecommendFragment(true),
                videoFragment
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setLinePagerIndicator(getColor(R.color.alpha))
            .setPersistent(true)
            .setListener(this)
//            .setDot(0, 23)
//            .setDot(1, 3)
            .initTabs(activity!!, tabsHome, homeViewPager)
        homeViewPager.adapter = zdFragmentPagerAdapter
//    onPageSelected(1)
        val index = SPUtil.getInt(HOME_INDEX, 1)
        homeViewPager.setCurrentItem(index, true)
        if (index == 2) {
            ZdEvent.get().with(HOME_PLAY).post(true)
        }
//    ZdEvent.get()
//        .with(HOME_PLAY, Boolean::class.java)
//        .observes(this, Observer {
////          zdFragmentPagerAdapter?.onRefresh()
//          if (homeViewPager == null) return@Observer
//        })
//    zdFragmentPagerAdapter?.setDot(3,100)
//    ZdEvent.get().with(PUSH, PushBean::class.java).observes(this, Observer {
//      LogUtil.e("--------jsd~push:",it)
//    })

        bannerAdapter = SimpleTextBannerAdapter(
            mActivity,
            layout.item_text_banner, listOf()
        )
        textBanner.setAdapter(bannerAdapter)
        bannerAdapter?.setItemClickListener { data, position ->
            LogUtil.e("----------title:", data, " | position:", position)
            if (Resource.user == null) {
                goFragment(UserFragment::class.java)
            } else {
                push(SearchFragment(data))
            }
        }
        textBannerBg.setOnClickListener {
            push(SearchFragment())
        }
        textBannerScan.setOnClickListener {
            if(Resource.user == null){
                goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            push(ScanFragment())
        }
        blogVM?.banner(listener = this)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {

    }

    override fun onPageSelected(position: Int) {
        VideoViewManager.instance()!!
            .release()
        if (position == 2) {
            enableLoadMore(false)
            enableRefresh(false)
            textBannerBg.normalColor = R.color.translucent3
            ZdEvent.get().with(HOME_PLAY).post(true)
        } else {
            enableLoadMore(true)
            enableRefresh(true)
            textBannerBg.normalColor = R.color.blackLightMiddle
        }
        blogVM?.banner(listener = this)
    }

    override fun onResume() {
        super.onResume()
        textBanner?.startAutoPlay()
        VideoViewManager.instance()
            ?.resume()
    }

    override fun onPause() {
        super.onPause()
        VideoViewManager.instance()
            ?.pause()
    }

    override fun onStop() {
        super.onStop()
        textBanner?.stopAutoPlay()
    }

    override fun onWords(
        data: MutableList<Word>?,
        err: Exception?
    ) {
        if (data != null) {
            bannerAdapter?.setData(data)
        }
    }
}

const val HOME_REFRESH = "homeRefresh"
const val HOME_PLAY = "homePlay"
const val HOME_INDEX = "homeIndex"
