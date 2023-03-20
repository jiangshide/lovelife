package com.android.sanskrit.publish.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.files.Files
import com.android.http.download.Download
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.utils.FileUtil
import com.android.utils.data.AUDIO
import com.android.utils.data.FileData
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import com.android.widget.extension.setDrawableLeft
import kotlinx.android.synthetic.main.user_audio_location_fragment_item.view.*
import kotlinx.android.synthetic.main.user_audio_play_list_fragment.*
import java.io.File

/**
 * created by jiangshide on 2020/7/21.
 * email:18311271399@163.com
 */
class EditAudioFragment(private val listener: OnAudioFinishListener? = null) : MyFragment() {

    private var adapter: KAdapter<Blog>? = null

    private var mTitle: String = ""
    private var files: MutableList<FileData>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        load()
        return setTitleView(R.layout.user_audio_play_list_fragment, true, true)
    }

    fun search(title: String) {
        this.mTitle = title
        blogVM?.blogFormat(format = AUDIO, title = mTitle)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
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
        request()
    }

    private fun request() {
        blogVM?.blogFormat(format = AUDIO, title = mTitle)
        showLoading()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        blogVM?.blogFormat(format = AUDIO, title = mTitle)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        blogVM?.blogFormat(format = AUDIO, title = mTitle, isRefresh = false)
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
        } else {
            adapter =
                audioPlayRecycleView.create(data, R.layout.user_audio_location_fragment_item, {
                    val blog = it
                    it.setCover(audioLocationItemIcon, audioLocationItemIconBtn)
                    audioLocationItemTitle.setTextColor(getColor(R.color.font))
                    audioLocationItemDes.setTextColor(getColor(R.color.font))
                    audioLocationItemTitle.text = it.name
                    audioLocationItemDes.text = it.sufix
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
                }, {
                    if (TextUtils.isEmpty(path)) {
                        ZdToast.txt("请先下载本文件!")
                    } else {
                        listener?.onPath(path)
                        pop()
                    }
                })
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