package com.android.sanskrit.channel.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.http.exception.HttpException
import com.android.img.Img
import com.android.resource.*
import com.android.resource.vm.channel.data.Channel
import com.android.resource.vm.report.data.Report
import com.android.resource.vm.user.OnFollowListener
import com.android.sanskrit.R
import com.android.sanskrit.blog.fragment.SingleBlogFragment
import com.android.sanskrit.home.HOME_REFRESH
import com.android.sanskrit.mine.MineFragment
import com.android.sanskrit.report.ReportFragment
import com.android.tablayout.indicators.LinePagerIndicator
import com.android.utils.FileUtil
import com.android.widget.ZdDialog
import com.android.widget.ZdToast
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlinx.android.synthetic.main.channel_blog_head_fragment.*
import kotlinx.android.synthetic.main.channel_manager_blog_fragment.*

/**
 * created by jiangshide on 2020/3/25.F
 * email:18311271399@163.com
 */
class ChannelManagerBlogFragment(
    private val uid: Long = 0,
    private val channelId: Long = 0,
    private val blogId: Long = 0
) : MyFragment(),
    OnOffsetChangedListener,
    OnFollowListener {

    private var channel: Channel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.channel_manager_blog_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setTopBar(blogChannelL)
        blogChannelTopBack.setOnClickListener {
            pop()
        }
        blogChannelTopMore.setOnClickListener {
            if (channel?.follows == ESPECIALLY) {
                showDialog(
                    arrayListOf("取消特别关注", "取消关注", "举报"),
                    arrayListOf<Int>(R.color.font, R.color.font, R.color.redLight)
                )
            } else if (channel?.follows == FOLLOW) {
                showDialog(
                    arrayListOf("特别关注", "取消关注", "举报"),
                    arrayListOf<Int>(R.color.blue, R.color.font, R.color.redLight)
                )
            } else {
                showDialog(
                    arrayListOf("特别关注", "关注", "举报"),
                    arrayListOf<Int>(R.color.blue, R.color.font, R.color.redLight)
                )
            }
        }
        showChannel()
        blogChannelTopIconR.setOnClickListener {
            push(MineFragment(id = channel?.uid))
        }

        channelBlogViewPager.adapter = channelBlogViewPager.create(childFragmentManager)
            .setTitles(
                "最新", "最热 "
            )
            .setFragment(
                ChannelBlogFragment(uid, channelId, CHANNEL_BLOG_NEW),
                ChannelBlogFragment(uid, channelId, CHANNEL_BLOG_HOT)
            )
            .initTabs(activity!!, tabsChannelBlog, channelBlogViewPager, true)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelecteSize(12)
            .setTxtSelectedSize(15)
            .setLinePagerIndicator(getColor(R.color.blue))
        channelBlogAppBarLayout.addOnOffsetChangedListener(this)

        channelVM!!.channelId.observe(this, Observer {
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
            channel = it.data
            showChannel()
        })

        channelVM?.channelId(channelId, uid, blogId)
    }

    private fun showChannel() {
        if (channel == null) return
        showFollow(channel!!.status)
        if(channel?.uid == Resource.uid){
            blogChannelTopFollow.visibility = View.GONE
        }else{
            blogChannelTopFollow.visibility = View.VISIBLE
        }
        blogChannelTopFollow.setOnClickListener {
            follow()
        }
        channelHeadFollow.setOnClickListener {
            follow()
        }
        Img.loadImageCircle(channel?.icon, blogChannelTopIcon)
        blogChannelTopName.text = channel?.nick
        channel?.setSex(blogChannelTopDes)

        var url = channel?.cover
        if (TextUtils.isEmpty(url) && !TextUtils.isEmpty(channel?.url) && FileUtil.isImg(
                channel?.url
            )
        ) {
            url = channel?.url
        } else {
            url = channel?.icon
        }
        Img.loadImageCircle(url, channelBlogIcon)
        channelBlogName.text = channel?.name
        channelBlogCount.text = "已产生${channel?.blogNum}条"
        channelBlogDes.setTxt(channel?.des)
        channelBlogIcon.setOnClickListener {
            if (uid == Resource.uid) return@setOnClickListener
//            push(SingleBlogFragment(blogId))
            push(MineFragment(id=uid))
        }
    }

    private fun showFollow(status: Int) {
        if (status == FOLLOW) {
            blogChannelTopFollow.visibility = View.GONE
            channelHeadFollow.visibility = View.GONE
        } else {
            blogChannelTopFollow.visibility = View.VISIBLE
            channelHeadFollow.visibility = View.VISIBLE
        }
    }

    private fun showDialog(
        data: ArrayList<String>,
        dataColors: ArrayList<Int>
    ) {
        ZdDialog.createList(context, data, dataColors)
            .setOnItemListener { parent, view, position, id ->
                when (data[position]) {
                    "特别关注", "取消特别关注" -> {
                        if (channel?.follows == ESPECIALLY) {
                            channel?.follows = FOLLOW
                        } else {
                            channel?.follows = ESPECIALLY
                        }
                        follow()
                    }
                    "关注", "取消关注" -> {
                        channel?.follows = FOLLOW - channel!!.follows
                        follow()
                    }
                    "举报" -> {
                        val report =
                            Report(
                                uid = Resource.uid!!,
                                source = CONTENT_FROM_CHANNEL,
                                name = channel?.name
                            )
                        push(ReportFragment(), report)
                    }
                }
            }
            .show()
    }

    override fun onOffsetChanged(
        appBarLayout: AppBarLayout?,
        verticalOffset: Int
    ) {
        val size = verticalOffset * -1 / 308f
        blogChannelTopIconR.alpha = size
        blogChannelTopFollow.alpha = size
        if (size == 0.0f) {
            blogChannelTopIconR.visibility = View.GONE
            blogChannelTopFollow.visibility = View.GONE
        } else {
            blogChannelTopIconR.visibility = View.VISIBLE
            blogChannelTopFollow.visibility = View.VISIBLE
        }
    }

    private fun follow() {
        if(channel?.uid == Resource.uid){
            ZdToast.txt("不能关注自己!")
            return
        }
        userVM?.followAdd(uid = channel!!.uid, status = channel!!.follows - 1, listener = this)
    }

    override fun follow(
        status: Int,
        e: HttpException?
    ) {
        if (e != null) {
            ZdToast.txt(e.message)
            return
        }
        showFollow(status)
        channel?.follows = status
        ZdEvent.get()
            .with(CHANNEL_BLOG_REFRESH)
            .post(CHANNEL_BLOG_REFRESH)
        ZdEvent.get()
            .with(HOME_REFRESH)
            .post(HOME_REFRESH)
    }
}

const val CHANNEL_BLOG_NEW = 1
const val CHANNEL_BLOG_HOT = 2

const val CHANNEL_BLOG_REFRESH = "channelBlogRefresh"