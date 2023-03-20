package com.android.sanskrit.channel

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.player.dplay.player.VideoViewManager
import com.android.resource.FOLLOW
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.blog.OnPraiseListener
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.channel.data.ChannelBlog
import com.android.resource.vm.channel.data.ChannelType
import com.android.sanskrit.R
import com.android.sanskrit.channel.fragment.ChannelManagerBlogFragment
import com.android.sanskrit.channel.fragment.ChannelNatureFragment
import com.android.sanskrit.channel.fragment.ChannelSearchFragment
import com.android.sanskrit.channel.fragment.ChannelTypeFragment
import com.android.sanskrit.mine.MineFragment
import com.android.sanskrit.user.UserFragment
import com.android.tablayout.indicators.LinePagerIndicator
import com.android.utils.LogUtil
import com.android.utils.SystemUtil
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import com.android.widget.recycleview.divider.CommonItemDecoration
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlinx.android.synthetic.main.channel_fragment.*
import kotlinx.android.synthetic.main.channel_fragment_head.*
import kotlinx.android.synthetic.main.channel_official_fragment_item.view.*

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class ChannelFragment : MyFragment(), OnOffsetChangedListener {

    private var adapter: KAdapter<ChannelBlog>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.channel_fragment)
    }

    override fun onTips(view: View?) {
        super.onTips(view)
        channelVM?.channelTypes(-1)
        showLoading()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        channelL.setPadding(
            0,
            SystemUtil.getStatusBarHeight(),
            0,
            getDim(R.dimen.common_navigator_height)
        )
        setRefreshEvent(CHANNEL_OFFICIAL_REFRESH)
        channelAppBarLayout.addOnOffsetChangedListener(this)
        channelCreate.setOnClickListener {
            if (Resource.user == null) {
                goFragment(UserFragment::class.java)
            } else {
                push(ChannelNatureFragment().setTitle("申请创建频道"))
            }
        }
        channelSearch.setOnClickListener {
            push(ChannelSearchFragment())
        }

        channelVM!!.channelTypes.observe(this, Observer {
            cancelRefresh()
            hiddle()
            if (it.error != null) {
                if (adapter == null || adapter!!.count() == 0) {
                    noNet("暂无频道!").setTipsRes(R.mipmap.no_data)
                } else if (blogVM!!.isRefresh) {
                    adapter?.clear()
                    noNet("暂无频道!").setTipsRes(R.mipmap.no_data)
                    enableLoadMore(false)
                }
                return@Observer
            }
            showView(it.data)
        })
        channelVM?.channelTypes(-1)
        showLoading()
    }

    private fun showView(data: ArrayList<ChannelType>) {
        var list = ArrayList<String>()
        var fragmens = ArrayList<ChannelTypeFragment>()
        data.forEach {
            list.add(it.name)
            fragmens.add(ChannelTypeFragment(it.id))
        }
        channelViewPager.adapter = channelViewPager.create(childFragmentManager)
            .setTitles(
                list
            )
            .setFragment(
                fragmens
            )
            .initTabs(activity!!, tabsChannel, channelViewPager)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setLinePagerIndicator(getColor(R.color.blue))

        channelVM!!.channelOfficial.observe(this, Observer {
            cancelRefresh()
            hiddle()
            clearAnimTab()
            if (it.error != null) {
                LogUtil.e(it.error)
                return@Observer
            }
            showChannelOfficial(it.data)
        })

        ZdEvent.get()
            .with(CHANNEL_OFFICIAL_REFRESH)
            .observes(this, Observer {
                channelVM?.channelOfficial()
            })

        channelVM?.channelOfficial()
        showLoading()
    }

    private fun showChannelOfficial(data: MutableList<ChannelBlog>) {
        if (data.size > 0) {
            channelOfficialTips.visibility = View.VISIBLE
            channelOfficial.visibility = View.VISIBLE
        } else {
            channelOfficialTips.visibility = View.GONE
            channelOfficial.visibility = View.GONE
        }
        val layoutManager = LinearLayoutManager(mActivity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        adapter = channelOfficial.create(data, R.layout.channel_official_fragment_item, {
            val channel = it
            officialName.text = "# ${it.name}"
            officialCount.text = "(已产生${it.blogNum}条)"
            Img.loadImageCircle(it.icon, officialItemIcon, R.mipmap.default_user)
            var url = channel.cover
            if (it.blog != null) {
                url = it?.blog?.cover
                it?.blog?.setUserInfo(officialItemIcon, officialItemName)
                officialItemUserL.setOnClickListener {
                    if(Resource.user == null){
                        goFragment(UserFragment::class.java)
                        return@setOnClickListener
                    }
                    push(MineFragment(id = channel?.blog?.uid))
                }
                if (TextUtils.isEmpty(url)) {
                    url = it?.blog?.url
                }
                it?.blog?.showNum(officialLike, it.blog!!.praiseNum)
                officialLike.setOnClickListener {
                    if (Resource.user == null) {
                        goFragment(UserFragment::class.java)
                        return@setOnClickListener
                    }
                    channel?.blog?.praises = 1 - channel?.blog?.praises!!
                    if (channel?.blog?.praises == FOLLOW) {
                        channel?.blog?.praiseNum = channel?.blog?.praiseNum!! + 1
                    } else {
                        channel?.blog?.praiseNum = channel?.blog?.praiseNum!! - 1
                    }
                    adapter?.notifyDataSetChanged()
                    blogVM?.praiseAdd(channel.blog!!, object : OnPraiseListener {
                        override fun onPraise(it: Blog) {
                            channel?.blog?.praiseNum = it.praiseNum
                            channel?.blog?.praises = it.praises
                            adapter?.notifyDataSetChanged()
                        }
                    })
                }
                officialLike.isSelected = it.blog!!.praises == 1
                Img.loadImageRound(url, officialCover, 5)
                it?.blog?.setImg(mActivity, officialItemFormat, isRandom = false)
            }
        }, {
            if (Resource.user == null) {
                goFragment(UserFragment::class.java)
                return@create
            }
            push(ChannelManagerBlogFragment(uid, id, blogId = blog!!.id))
        }, layoutManager, CommonItemDecoration(-20, 0))
    }

    override fun onResume() {
        super.onResume()
        VideoViewManager.instance()
            .pause()
    }

    override fun onOffsetChanged(
        appBarLayout: AppBarLayout?,
        verticalOffset: Int
    ) {
        val size = verticalOffset * -1 / 308f
//    zdTopView.topRightBtn.alpha = size
//    channelTopView.alpha = size
    }
}

const val CHANNEL_OFFICIAL_REFRESH = "channelOfficialRefresh"