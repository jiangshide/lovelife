package com.android.sanskrit.home.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.android.event.ZdEvent
import com.android.player.dplay.player.AbstractPlayer
import com.android.player.dplay.player.VideoView
import com.android.player.dplay.player.VideoViewManager
import com.android.refresh.api.RefreshLayout
import com.android.refresh.footer.BallPulseFooter
import com.android.refresh.header.BezierRadarHeader
import com.android.resource.MyFragment
import com.android.resource.cache.PreloadManager
import com.android.resource.view.LoadingView
import com.android.resource.view.VerticalViewPager
import com.android.resource.view.textbanner.TextBanner
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.SHOW_MENUS
import com.android.sanskrit.blog.adapter.VIDEO_SOUND
import com.android.sanskrit.blog.adapter.VideoAdapter
import com.android.sanskrit.home.HOME_PLAY
import com.android.utils.LogUtil
import com.android.utils.SPUtil
import kotlinx.android.synthetic.main.recycleview_video.*

/**
 * created by jiangshide on 2020/7/11.
 * email:18311271399@163.com
 */
class VideoFragment(
    private val textBannerBg: TextBanner? = null,
    private val homeLoading: LoadingView? = null
) : MyFragment() {

    private var mCurrentPosition = 0
    private var mPlayingPosition = 0
    private var mPreloadManager: PreloadManager? = null
    private var adapter: VideoAdapter? = null
    private var playIndex = 0

    /**
     * VerticalViewPager是否反向滑动
     */
    private var mIsReverseScroll = false

    private var videoItemIconL: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.recycleview_video, true, true)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        mRefresh?.setRefreshHeader(BezierRadarHeader(mActivity))
        mRefresh?.setRefreshFooter(BallPulseFooter(mActivity))
        mPreloadManager = PreloadManager.getInstance(mActivity)
        videoPager.offscreenPageLimit = 4
        adapter = VideoAdapter(this, userVM!!, blogVM!!, arrayListOf())
        videoPager.adapter = adapter
        videoPager.overScrollMode = View.OVER_SCROLL_NEVER
        videoPager.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (position > mPlayingPosition) {
                    mIsReverseScroll = false
                } else if (position < mPlayingPosition) {
                    mIsReverseScroll = true
                }
                if (position == 0) {
                    startPlay(mCurrentPosition)
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mCurrentPosition = position
                if (position == mPlayingPosition) return
                startPlay(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (mCurrentPosition == mPlayingPosition) return
                if (state == VerticalViewPager.SCROLL_STATE_IDLE) {
                    mPreloadManager?.resumePreload(mCurrentPosition, mIsReverseScroll)
                } else {
                    mPreloadManager?.pausePreload(mCurrentPosition, mIsReverseScroll)
                }
            }
        })

        blogVM!!.blogFormat.observe(this, Observer {
            hiddle()
            cancelRefresh()
            textBannerBg?.visibility = View.VISIBLE
            homeLoading?.visibility = View.GONE
            homeLoading?.stop()
            clearAnimTab()
            if (it.error != null) {
                if (adapter == null || adapter?.size == 0) {
                    noNet("暂无视频!").setTipsRes(mipmap.no_data)
                } else if (blogVM!!.isRefresh) {
                    adapter?.clear()
                    noNet("暂无视频!").setTipsRes(mipmap.no_data)
                    enableLoadMore(false)
                }
                return@Observer
            }
            adapter?.add(it.data, blogVM!!.isRefresh)
            adapter?.notifyDataSetChanged()
            enableLoadMore(false)
        })

        ZdEvent.get()
            .with(SHOW_MENUS, Boolean::class.java)
            .observes(this, Observer {
                videoItemIconL?.visibility = if (it) View.INVISIBLE else View.VISIBLE
            })
        request()
        ZdEvent.get()
            .with(HOME_PLAY, Boolean::class.java)
            .observes(this, Observer {
                if (it) {
                    startPlay(playIndex)
                }
            })
    }

    private fun startPlay(position: Int) {
        playIndex = position
        val count: Int = videoPager.childCount
        for (i in 0 until count) {
            val itemView: View = videoPager.getChildAt(i)
            val viewHolder: VideoAdapter.ViewHolder = itemView.tag as VideoAdapter.ViewHolder
            if (viewHolder.mPosition === position) {
                val videoView =
                    itemView.findViewById<VideoView<AbstractPlayer>>(R.id.videoItemVideo)
                videoView.setPlay(true)
                val sound = SPUtil.getBoolean(VIDEO_SOUND, true)
                viewHolder.mVideoView.isMute = sound
                videoItemIconL = itemView.findViewById(R.id.videoItemIconL)
                val blog: Blog = adapter?.datas!![position]
                ZdEvent.get()
                    .with(PLAYING_ID)
                    .post(blog.uid)
                videoView.setUrl(mPreloadManager!!.getPlayUrl(blog.url))
                videoView.start()
                mPlayingPosition = position
                break
            }
        }
        if (adapter?.count == 0 || position == 0) {
            enableRefresh(true)
            enableLoadMore(false)
        } else if ((adapter?.count!! - 1) == position) {
            enableLoadMore(true)
            enableRefresh(false)
        } else {
            enableRefresh(false)
            enableLoadMore(false)
        }
    }

    override fun onTips(view: View?) {
        super.onTips(view)
        request()
    }

    private fun request() {
        blogVM?.blogFormat(format = 2)
        showLoading()
    }

    override fun onPause() {
        super.onPause()
        VideoViewManager.instance()
            .pause()
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            LogUtil.e("------------onResume2:")
            VideoViewManager.instance().resume()
        }, 500)
        LogUtil.e("------------onResume1:")
    }

    override fun onDestroy() {
        super.onDestroy()
        VideoViewManager.instance()
            .release()
        mPreloadManager!!.removeAllPreloadTask()

        //清除缓存，实际使用可以不需要清除，这里为了方便测试
//    ProxyVideoCacheManager.clearAllCache(mActivity)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        cancelRefresh(0)
        textBannerBg?.visibility = View.GONE
        homeLoading?.visibility = View.VISIBLE
        homeLoading?.start()
        blogVM?.blogFormat(format = 2)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        blogVM?.blogFormat(format = 2, isRefresh = false)
    }
}

const val PLAYING_ID = "playingId"