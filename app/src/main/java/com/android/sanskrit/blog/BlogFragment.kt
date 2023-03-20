package com.android.sanskrit.blog

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.android.player.dplay.player.AbstractPlayer
import com.android.player.dplay.player.VideoView
import com.android.refresh.footer.BallPulseFooter
import com.android.refresh.header.BezierRadarHeader
import com.android.resource.MyFragment
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.blog.adapter.BlogAdapter
import com.android.sanskrit.blog.adapter.FROM_MINE
import com.android.utils.LogUtil
import kotlinx.android.synthetic.main.blog_fragment.*

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
open class BlogFragment(
    private val isBottomBar: Boolean = false,
    private val from: Int = FROM_MINE,
    private var isRefresh: Boolean = true
) : MyFragment() {

    var adapter: BlogAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.blog_fragment, true, true)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        if(!isRefresh){
            mRefresh?.setRefreshHeader(BezierRadarHeader(mActivity))
            mRefresh?.setRefreshFooter(BallPulseFooter(mActivity))
        }
        if (isBottomBar) {
            bottomBar.visibility = View.VISIBLE
        } else {
            bottomBar.visibility = View.GONE
        }
        val layoutManager = LinearLayoutManager(mActivity)
        blogRecycleView.layoutManager = layoutManager
        adapter = BlogAdapter(this, blogVM!!, this, blogRecycleView, from)
        blogRecycleView.adapter = adapter
        blogRecycleView.addOnChildAttachStateChangeListener(object :
            OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                var videoView: VideoView<AbstractPlayer>? = null
                try {
                    videoView = view.findViewById(R.id.videoPlayer)
                } catch (e: Exception) {
                    LogUtil.e(e)
                }
                if (videoView != null && !videoView.isFullScreen) {
                    videoView.release()
                }
            }
            override fun onChildViewAttachedToWindow(view: View) {
            }
        })
        blogRecycleView.addOnScrollListener(object : OnScrollListener() {
            var firstVisibleItem = 0
            var lastVisibleItem: Int = 0
            var visibleCount: Int = 0
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    SCROLL_STATE_IDLE -> autoPlayVideo(recyclerView)
                }
            }

            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                visibleCount = lastVisibleItem - firstVisibleItem //记录可视区域item个数
            }

            private fun autoPlayVideo(view: RecyclerView?) {
                //循环遍历可视区域videoview,如果完全可见就开始播放
                for (i in 0 until visibleCount) {
                    if (view?.getChildAt(i) == null) continue
                    var videoView: VideoView<AbstractPlayer>? = null
                    try {
                        videoView = view.getChildAt(i)
                            .findViewById(R.id.videoPlayer)
                    } catch (e: Exception) {
                        LogUtil.e(e)
                    }
                    if (videoView != null) {
                        val rect = Rect()
                        videoView?.getLocalVisibleRect(rect)
                        val videoHeight: Int = videoView!!.height
                        if (rect.top == 0 && rect.bottom == videoHeight) {
                            videoView?.start()
                            return
                        }
                    }
                }
            }
        })

        blogRecycleView.post {
            val view = blogRecycleView.getChildAt(0)
            try {
                val videoView: VideoView<AbstractPlayer> = view.findViewById(R.id.videoPlayer)
                videoView?.start()
            } catch (e: Exception) {
                LogUtil.e("e:", e)
            }
        }
    }

    fun showView(data: MutableList<Blog>) {
        adapter?.add(data as ArrayList<Blog>, blogVM!!.isRefresh)
//        enableLoadMore(data.size == 20)
    }
}