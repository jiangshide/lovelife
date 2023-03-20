package com.android.sanskrit.home.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.refresh.api.RefreshLayout
import com.android.resource.Resource
import com.android.resource.view.LoadingView
import com.android.resource.view.textbanner.TextBanner
import com.android.sanskrit.R
import com.android.sanskrit.blog.BlogFragment
import com.android.sanskrit.blog.adapter.FROM_FOLLOW
import com.android.sanskrit.home.HOME_REFRESH
import com.android.sanskrit.user.UserFragment
import com.android.utils.SystemUtil
import kotlinx.android.synthetic.main.blog_fragment.*
import kotlinx.android.synthetic.main.recycleview.*

/**
 * created by jiangshide on 2020/4/1.
 * email:18311271399@163.com
 */
class FollowFragment(
    private val textBannerBg: TextBanner? = null,
    private val homeLoading: LoadingView? = null,
    private val isTopBar: Boolean = true,
    private val isBottomBar: Boolean = true
) :
    BlogFragment(isBottomBar = isBottomBar, from = FROM_FOLLOW, isRefresh = false) {

    override fun onTips(view: View?) {
        super.onTips(view)
        request()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        if (isTopBar) {
            blogRecycleView.setPadding(
                0,
                SystemUtil.getStatusBarHeight() + getDim(R.dimen.small_navigator_height),
                0,
                0
            )
        }
        ZdEvent.get()
            .with(HOME_FOLLOW_REFRESH)
            .observes(this, Observer {
                blogVM?.followBlog()
                zdRecycleView?.scrollToPosition(0)
            })
        blogVM!!.followBlog.observe(this, Observer {
            cancelRefresh()
            textBannerBg?.visibility = View.VISIBLE
            homeLoading?.visibility = View.GONE
            homeLoading?.stop()
            hiddle()
            clearAnimTab()
            if (Resource.user == null) {
                noNet("还未登录!","注册/登录") {
                    goFragment(UserFragment::class.java)
                }.setTipsRes(R.mipmap.no_login)
                return@Observer
            }
            if (it.error != null) {
                if (adapter == null || adapter!!.count() == 0) {
                    noNet("暂无关注!").setTipsRes(R.mipmap.no_data)
                } else if (blogVM!!.isRefresh) {
                    adapter?.clear()
                    noNet("暂无关注!").setTipsRes(R.mipmap.no_data)
                    enableLoadMore(false)
                }
                return@Observer
            }
            showView(it.data)
        })

        ZdEvent.get()
            .with(HOME_REFRESH)
            .observes(this, Observer {
                blogVM?.followBlog()
                zdRecycleView?.scrollToPosition(0)
            })
        request()
    }

    private fun request() {
        blogVM?.followBlog()
        showLoading()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        cancelRefresh(0)
        textBannerBg?.visibility = View.GONE
        homeLoading?.visibility = View.VISIBLE
        homeLoading?.start()
        blogVM?.followBlog()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        blogVM?.followBlog(isRefresh = false)
    }
}

const val HOME_FOLLOW_REFRESH = "homeFollowRefresh"