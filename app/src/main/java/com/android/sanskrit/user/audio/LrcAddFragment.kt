package com.android.sanskrit.user.audio

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.files.Files
import com.android.files.Files.OnFileListener
import com.android.http.oss.OssClient
import com.android.http.oss.OssClient.HttpFileListener
import com.android.resource.MyFragment
import com.android.resource.audio.AudioPlay
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.utils.LogUtil
import com.android.utils.data.DOC
import com.android.utils.data.FileData
import com.android.widget.ZdEditText
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.user_audio_lrc_add_fragment.*

/**
 * created by jiangshide on 2020/6/8.
 * email:18311271399@163.com
 */
class LrcAddFragment(private val blog: Blog) : MyFragment() {

    private var data: FileData? = null
    private var lrcZh: String = ""
    private var lrcEs: String = ""
    private var name: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.user_audio_lrc_add_fragment)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        showFloatMenu(false)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        showFloatMenu(false)
    }

    override fun onPause() {
        super.onPause()
        showFloatMenu(false)
    }

    override fun onResume() {
        super.onResume()
        showFloatMenu(false)
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setTopBar(lrcAddL)
        name = blog.name!!
        songNameEdit.setText(name)
        songNameEdit.setListener { s, input ->
            this.name = input
            lrcModifyView.loadLrcByUrl(input)
            validate()
        }

        songUrlZhEdit.setListener { s, input ->
            this.lrcZh = input
            lrcModifyView.loadLrcByUrl(input)
            validate()
        }

        songUrlZhFileBtn.setOnClickListener {
            openLrc(songUrlZhEdit, true)
        }

        songUrlEsEdit.setListener { s, input ->
            this.lrcEs = input
            validate()
        }
        songUrlEsFileBtn.setOnClickListener {
            openLrc(songUrlEsEdit, false)
        }
        lrcModifyView.setDraggable(true) { time ->
            val currTime: Int = time.toInt()
            AudioPlay.getInstance()
                .seekTo(currTime)
            true
        }

        blogVM!!.lrcUpdate.observe(this, Observer {
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
            blog.lrcZh = lrcZh
            blog.lrcEn = lrcEs
            AudioPlay.getInstance()
                .update(blog)
            ZdEvent.get()
                .with(LRC_REFRESH)
                .post(blog)
        })
    }

    private fun openLrc(
        editText: ZdEditText,
        isZh: Boolean
    ) {
        Files().setType(DOC)
            .setSelectedFile(data)
            .setMax(1)
            .setSingleListener(object : OnFileListener {
                override fun onFile(fileData: FileData) {
                    data = fileData
                    LogUtil.e("---------fileData:", fileData)
                    OssClient.instance
                        .setListener(object : HttpFileListener {
                            override fun onProgress(
                                currentSize: Long,
                                totalSize: Long,
                                progress: Int
                            ) {
                                LogUtil.e(
                                    "----------currentSize:",
                                    currentSize,
                                    " | totalSize:",
                                    totalSize,
                                    " | progress:",
                                    progress
                                )
                            }

                            override fun onSuccess(
                                position: Int,
                                url: String
                            ) {
                                LogUtil.e("-------position:", position, " | url:", url)
                                if (isZh) {
                                    lrcZh = url
                                } else {
                                    lrcEs = url
                                }
                                if (editText == null) return
                                editText.setText(url)
                                validate()
                                lrcModifyView.loadLrcByUrl(url)
                            }

                            override fun onFailure(
                                clientExcepion: Exception,
                                serviceException: Exception
                            ) {
                                LogUtil.e(
                                    "------clientExcepion:", clientExcepion, " | serviceException:",
                                    serviceException
                                )
                                if (serviceException != null && !TextUtils.isEmpty(serviceException.message)) {
                                    ZdToast.txt(serviceException.message)
                                }
                            }

                        })
                        .setFileData(fileData!!)
                        .start()
                }
            })
            .open(this)
    }

    private fun validate() {
        var disable =
            TextUtils.isEmpty(name) || (TextUtils.isEmpty(lrcZh) && TextUtils.isEmpty(lrcEs))
        setRight("完成").setRightEnable(!disable)
            .setRightListener {
                blogVM?.lrcUpdate(blog.id, name, lrcZh, lrcEs)
            }
    }
}