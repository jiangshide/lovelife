package com.android.sanskrit.user.audio

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.files.Files
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.audio.AudioPlay
import com.android.resource.audio.AudioService
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.blog.data.OnSyncListener
import com.android.resource.vm.blog.data.OnSyncStatusListener
import com.android.resource.vm.channel.data.ChannelBlog
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.publish.fragment.ChannelManagerFragment
import com.android.sanskrit.publish.fragment.OnAudioFinishListener
import com.android.sanskrit.publish.fragment.OnChannelListener
import com.android.sanskrit.user.UserFragment
import com.android.utils.data.AUDIO
import com.android.utils.data.FileData
import com.android.widget.adapter.AbstractAdapter.OnItemListener
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.user_audio_location_fragment_item.view.*
import kotlinx.android.synthetic.main.user_audio_play_list_fragment.*

/**
 * created by jiangshide on 2020/5/28.
 * email:18311271399@163.com
 */
class LocalMusicFragment(private val listener: OnAudioFinishListener? = null) : MyFragment(),
    OnItemListener<Blog> {

    private var url: String? = ""

    private var adapter: KAdapter<Blog>? = null
    private var datas: MutableList<Blog>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isBackground = false
        return setView(R.layout.user_audio_play_list_fragment, true, false)
    }

    fun search(word: String) {
        if (TextUtils.isEmpty(word)) {
            load()
            return
        }
        val list = ArrayList<Blog>()
        datas?.forEach {
            if (it?.name?.contains(word)!!) {
                list.add(it)
            }
        }
        adapter?.add(list, true)
    }

    override fun onTips(view: View?) {
        super.onTips(view)
        load()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        load()

        ZdEvent.get()
            .with(AUDIO_REFRESH)
            .observes(this, Observer {
                url = AudioPlay.getInstance()?.data?.url
                adapter?.notifyDataSetChanged()
            })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        load()
    }

    private fun showView(
        data: MutableList<Blog>
    ) {
        datas = data
        hiddle()
        cancelRefresh()
        if (adapter != null) {
            adapter?.add(data, true)
        } else {
            adapter =
                audioPlayRecycleView.create(data, R.layout.user_audio_location_fragment_item, {
                    val blog = it
                    it.setCover(audioLocationItemIcon, audioLocationItemIconBtn)
                    if (url == it.url) {
                        audioLocationItemTitle.setTextColor(getColor(R.color.blueLight))
                        audioLocationItemDes.setTextColor(getColor(R.color.blue))
                    } else {
                        audioLocationItemTitle.setTextColor(getColor(R.color.font))
                        audioLocationItemDes.setTextColor(getColor(R.color.font))
                    }
                    audioLocationItemTitle.text = it.name
                    audioLocationItemDes.text = it.sufix
                    if (it.isSync || listener != null) {
                        audioLocationItemClose.visibility = View.GONE
                        if (!TextUtils.isEmpty(it.channel)) {
                            audioLocationItemChannel.visibility = View.VISIBLE
                            audioLocationItemChannel.text = it.channel
                        } else {
                            audioLocationItemChannel.visibility = View.GONE
                        }
                    } else {
                        audioLocationItemChannel.visibility = View.GONE
                        audioLocationItemClose.visibility = View.VISIBLE
                        audioLocationItemClose.text = "发布"
                        it.position
                        audioLocationItemClose.drawableLeft(R.drawable.alpha)
                        audioLocationItemClose.normalColor = R.color.blue
                        it.syncStatus(publishVM!!, object : OnSyncStatusListener {
                            override fun syncResult(
                                status: Int,
                                t: Blog?
                            ) {
                                when (status) {
                                    0 -> {

                                    }
                                    1 -> {
                                        blog?.id = t!!.id
                                        blog?.uid = t!!.uid
                                        blog?.channelId = t!!.channelId
                                        blog?.positionId = t!!.positionId
                                        blog?.title = t!!.title
                                        blog?.des = t!!.des
                                        blog?.latitude = t!!.latitude
                                        blog?.longitude = t!!.longitude
                                        blog?.locationType = t!!.locationType
                                        blog?.country = t!!.country
                                        blog?.city = t!!.city
                                        blog?.position = t!!.position
                                        blog?.address = t!!.address
                                        blog?.cityCode = t!!.cityCode
                                        blog?.adCode = t!!.adCode
                                        blog?.timeCone = t!!.timeCone
                                        blog?.tag = t!!.tag
                                        blog?.status = t!!.status
                                        blog?.reason = t!!.reason
                                        blog?.official = t!!.official
                                        blog?.url = t!!.url
                                        blog?.cover = t!!.cover
                                        blog?.name = t!!.name
                                        blog?.sufix = t!!.sufix
                                        blog?.format = t!!.format
                                        blog?.duration = t!!.duration
                                        blog?.width = t!!.width
                                        blog?.height = t!!.height
                                        blog?.size = t!!.size
                                        blog?.rotate = t!!.rotate
                                        blog?.bitrate = t!!.bitrate
                                        blog?.sampleRate = t!!.sampleRate
                                        blog?.level = t!!.level
                                        blog?.mode = t!!.mode
                                        blog?.wave = t!!.wave
                                        blog?.lrcZh = t!!.lrcZh
                                        blog?.lrcEn = t!!.lrcEn
                                        blog?.source = t!!.source
                                        blog?.date = t!!.date
                                        blog?.icon = t!!.icon
                                        blog?.sex = t!!.sex
                                        blog?.age = t!!.age
                                        blog?.zodiac = t!!.zodiac
                                        blog?.ucity = t!!.ucity
                                        blog?.remark = t!!.remark
                                        blog?.channel = t!!.channel
                                        blog?.praiseNum = t!!.praiseNum
                                        blog?.viewNum = t!!.viewNum
                                        blog?.shareNum = t!!.shareNum
                                        blog?.commentNum = t!!.commentNum
                                        blog?.praises = t!!.praises
                                        blog?.reportr = t!!.reportr
                                        blog?.follows = t!!.follows
                                        blog?.collections = t!!.collections
                                        blog?.tops = t!!.tops
                                        blog?.recommends = t!!.recommends
                                        blog?.praises = t!!.praises
                                        blog?.urls = t!!.urls
                                        blog?.comments = t!!.comments
                                        blog?.praises = t!!.praises

                                        blog?.isSync = true
                                        adapter?.notifyDataSetChanged()
                                        audioLocationItemClose?.visibility = View.GONE
                                    }
                                }
                            }
                        })
                    }

                    audioLocationItemClose.setOnClickListener {
                        if (Resource.user == null) {
                            goFragment(UserFragment::class.java)
                            return@setOnClickListener
                        }
                        push(ChannelManagerFragment(listener = object : OnChannelListener {
                            override fun onChannel(channelBlog: ChannelBlog) {
                                audioLocationItemChannel?.text = channelBlog?.name
                                audioLocationItemChannel.visibility = View.VISIBLE
                                blog.syncBlog(
                                    channelBlog.id,
                                    syncProgress,
                                    object : OnSyncListener {
                                        override fun syncResult(
                                            status: Int,
                                            url: String
                                        ) {
                                            when (status) {
                                                0 -> {
                                                    syncProgress?.visibility = View.VISIBLE
                                                    audioLocationItemClose?.text = "发布中..."
                                                    audioLocationItemClose?.normalColor =
                                                        R.color.fontLight
                                                }
                                                1 -> {
                                                    syncProgress?.visibility = View.GONE
                                                    blog.isSync = true
                                                    adapter?.notifyDataSetChanged()
                                                }
                                                -1 -> {
                                                    syncProgress?.visibility = View.GONE
                                                    audioLocationItemClose?.text = "发布"
                                                    audioLocationItemClose?.normalColor =
                                                        R.color.blue
                                                }
                                            }
                                        }
                                    },
                                    publishVM!!
                                )
                            }
                        }))
                    }
                }, {
                })
                    .setItemListener(this)
        }
        if (adapter?.count() == 0) {
            noNet("暂无数据!").setTipsRes(mipmap.no_data)
        }
    }

    private fun load() {
        showLoading()
        Thread {
            kotlin.run {
                val list = Files().setType(AUDIO)
                    .fileList()
                val msg = Message()
                msg.what = 1
                msg.obj = list
                handler.sendMessage(msg)
            }
        }.start()
    }

    private val handler = Handler { it ->
        if (it.what == 1) {
            val data = ArrayList<Blog>()
            val list = it.obj as MutableList<MutableList<FileData>>
            list.forEach { it ->
                it.forEach {
                    data.add(
                        Blog(
                            name = it.name,
                            sufix = it.sufix,
                            format = it.format,
                            width = it.width,
                            height = it.height,
                            duration = it.duration,
                            bitrate = it.bitrate,
                            size = it.size,
                            rotate = it.rotate,
                            url = it.path,
                            path = it.path,
                            cover = it.cover,
                            isSync = false
                        )
                    )
                }
            }
            showView(data)
        }
        return@Handler false
    }

    override fun onItem(
        position: Int,
        item: Blog
    ) {
        if (listener != null) {
            listener?.onPath(item.path)
            pop()
            return
        }
        val audio = AudioPlay.getInstance()
        if (audio.mData == null || audio.mData.size == 0) {
            AudioService.startAudioCommand(
                AudioService.CMD_INIT, data = adapter?.datas() as ArrayList<Blog>?,
                position = position
            )
            AudioService.startAudioCommand(AudioService.CMD_PLAY, position)
        } else {
            audio.mData.forEach {
                if (it.url == item.url) {
                    AudioService.startAudioCommand(AudioService.CMD_PLAY, position)
                    ZdEvent.get()
                        .with(AUDIO_REFRESH)
                        .post(AUDIO_REFRESH)
                    return
                }
            }
            AudioPlay.getInstance().data = item
            AudioService.startAudioCommand(AudioService.CMD_PLAY)
        }
        ZdEvent.get()
            .with(AUDIO_REFRESH)
            .post(AUDIO_REFRESH)
    }
}