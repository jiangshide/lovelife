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
import com.android.http.download.Download
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.audio.AudioPlay
import com.android.resource.audio.AudioService
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.publish.fragment.OnAudioFinishListener
import com.android.utils.FileUtil
import com.android.utils.ScreenUtil
import com.android.utils.data.AUDIO
import com.android.utils.data.FileData
import com.android.widget.ZdToast
import com.android.widget.adapter.AbstractAdapter.OnItemListener
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import com.android.widget.extension.setDrawableLeft
import kotlinx.android.synthetic.main.user_audio_location_fragment_item.view.*
import kotlinx.android.synthetic.main.user_audio_play_list_fragment.*
import java.io.File

/**
 * created by jiangshide on 2020/5/29.
 * email:18311271399@163.com
 */
class HistoryAudioFragment(
    private val listener: OnAudioFinishListener? = null,
    private val id: Long? = Resource.uid
) : MyFragment() {

    private var adapter: KAdapter<Blog>? = null
    private var files: MutableList<FileData>? = null

    private var uid = id

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isBackground = false
        load()
        return setView(R.layout.user_audio_play_list_fragment, true, true)
    }

    fun search(word: String) {
        if (TextUtils.isEmpty(word)) {
            blogVM?.historyList(uid = uid, source = 4)
            return
        } else {
            blogVM?.blogFormat(format = AUDIO, title = word)
        }

    }

    fun updateData(id: Long) {
        uid = id
        blogVM?.historyList(uid = uid, source = 4)
    }

    override fun onTips(view: View?) {
        super.onTips(view)
        blogVM?.historyList(uid = uid, source = 4)
        showLoading()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        blogVM!!.historyList!!.observe(this, Observer {
            hiddle()
            cancelRefresh()
            if (it.error != null) {
                val h = ScreenUtil.getRtScreenHeight(context) / 3-100
                zdTipsView.root.setPadding(0, 0, 0, h)
                if (adapter == null || adapter!!.count() == 0) {
                    noNet("暂无发现!").setTipsRes(R.mipmap.no_data)
                } else if (blogVM!!.isRefresh) {
                    adapter?.clear()
                    noNet("暂无发现!").setTipsRes(R.mipmap.no_data)
                    enableLoadMore(false)
                }
                return@Observer
            }
            showView(it.data)
        })
        blogVM!!.blogFormat.observe(this, Observer {
            cancelRefresh()
            hiddle()
            clearAnimTab()
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
        blogVM?.historyList(uid = uid, source = 4)
        showLoading()
    }

    private fun showView(
        data: MutableList<Blog>
    ) {
        data?.forEach { blog ->
            files?.forEach { file ->
                if (FileUtil.getFileSufix(file.path) == FileUtil.getFileSufix(blog.url)) {
                    blog.path = file.path
                    return@forEach
                }
            }
            val list = FileUtil.listFilesInDir(FileUtil.getDownload())
            list.forEach {
                if (FileUtil.getFileSufix(blog.url) == FileUtil.getFileSufix(it.absolutePath)) {
                    blog.path = it.absolutePath
                    return@forEach
                }
            }
        }
        if (adapter != null) {
            adapter?.add(data, true)
            return
        }
        adapter = audioPlayRecycleView.create(data, R.layout.user_audio_location_fragment_item, {
            val blog = it
            it.setCover(audioLocationItemIcon, audioLocationItemIconBtn)
            val data = AudioPlay.getInstance().data
            if (data != null && it.url == data.url) {
                audioLocationItemTitle.setTextColor(getColor(R.color.blueLight))
                audioLocationItemDes.setTextColor(getColor(R.color.blue))
            } else {
                audioLocationItemTitle.setTextColor(getColor(R.color.font))
                audioLocationItemDes.setTextColor(getColor(R.color.font))
            }
            audioLocationItemTitle.text = it.name
            var des = it.des
            if (TextUtils.isEmpty(des)) {
                des = it.sufix
            }
            audioLocationItemDes.text = des
            if (listener != null) {
                if (TextUtils.isEmpty(it.path)) {
                    audioLocationItemClose.setDrawableLeft(R.mipmap.download)
                    audioLocationItemClose.setOnClickListener {
                        download(blog.url, object : Download.OnDownloadListener {
                            override fun onDownloading(progress: Int) {
                                syncProgress?.visibility = View.VISIBLE
                                syncProgress?.progress = progress
                            }

                            override fun onDownloadFailed(e: Exception?) {
                                syncProgress?.visibility = View.GONE
                            }

                            override fun onDownloadSuccess(file: File?) {
                                syncProgress?.visibility = View.GONE
                                blog?.path = file?.absolutePath
                                adapter?.notifyDataSetChanged()
                            }

                        })
                    }
                } else {
                    audioLocationItemClose.setDrawableLeft(R.drawable.alpha)
                }
            } else {
                if (uid == Resource.uid) {
                    audioLocationItemClose.visibility = View.VISIBLE
                } else {
                    audioLocationItemClose.visibility = View.GONE
                }
                audioLocationItemClose.setOnClickListener {
                    adapter?.remove(blog)
                }
            }

        }, {
        })
            .setItemListener(object : OnItemListener<Blog> {
                override fun onItem(
                    position: Int,
                    item: Blog
                ) {
                    if (listener != null) {
                        if (TextUtils.isEmpty(item.path)) {
                            ZdToast.txt("请先下载本文件!")
                        } else {
                            listener?.onPath(item.path)
                            pop()
                        }
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
            })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        blogVM?.historyList(uid = uid, source = 4)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        blogVM?.historyList(uid = uid, source = 4, isRefresh = false)
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
            val data = ArrayList<FileData>()
            val list = it.obj as MutableList<MutableList<FileData>>
            list.forEach { it ->
                it.forEach {
                    data.add(it)
                }
            }
            files = data
        }
        return@Handler false
    }
}