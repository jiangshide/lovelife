package com.android.sanskrit.home.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.refresh.api.RefreshLayout
import com.android.resource.view.LoadingView
import com.android.resource.view.textbanner.TextBanner
import com.android.sanskrit.R
import com.android.sanskrit.blog.BlogFragment
import com.android.sanskrit.blog.adapter.FROM_RECOMMEND
import com.android.sanskrit.home.HOME_REFRESH
import com.android.utils.SystemUtil
import kotlinx.android.synthetic.main.blog_fragment.*

/**
 * created by jiangshide on 2020/4/1.
 * email:18311271399@163.com
 */
class RecommendFragment(
    private val textBannerBg: TextBanner? = null,
    private val homeLoading: LoadingView? = null
) :
    BlogFragment(isBottomBar = true, from = FROM_RECOMMEND, isRefresh = false) {

    override fun onTips(view: View?) {
        super.onTips(view)
        request()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        blogRecycleView.setPadding(
            0,
            SystemUtil.getStatusBarHeight() + getDim(R.dimen.small_navigator_height),
            0,
            0
        )
        ZdEvent.get()
            .with(HOME_RECOMMEND_REFRESH)
            .observes(this, Observer {
                blogVM?.recommendBlog()
            })
        blogVM!!.recommendBlog.observe(this, Observer {
            cancelRefresh()
            textBannerBg?.visibility = View.VISIBLE
            homeLoading?.visibility = View.GONE
            homeLoading?.stop()
            hiddle()
            clearAnimTab()
            if (it.error != null) {
                if (adapter == null || adapter!!.count() == 0) {
                    noNet("暂无推荐!").setTipsRes(R.mipmap.no_data)
                } else if (blogVM!!.isRefresh) {
                    adapter?.clear()
                    noNet("暂无推荐!").setTipsRes(R.mipmap.no_data)
                    enableLoadMore(false)
                }
                return@Observer
            }
            showView(it.data)
        })

        ZdEvent.get()
            .with(HOME_REFRESH)
            .observes(this, Observer {
                blogVM?.recommendBlog()
            })

        request()
    }

    private fun request() {
        blogVM?.recommendBlog()
        showLoading()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        cancelRefresh(0)
        textBannerBg?.visibility = View.GONE
        homeLoading?.visibility = View.VISIBLE
        homeLoading?.start()
        blogVM?.recommendBlog()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        blogVM?.recommendBlog(isRefresh = false)
    }

}

const val HOME_RECOMMEND_REFRESH = "homeRecommendRefresh"