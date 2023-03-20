package com.android.sanskrit.channel.fragment

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
import com.android.img.Img
import com.android.resource.MyFragment
import com.android.resource.vm.channel.data.Channel
import com.android.sanskrit.R
import com.android.sanskrit.mine.fragment.channelmanager.MYCHANNEL_REFRESH
import com.android.utils.LogUtil
import com.android.utils.data.FileData
import com.android.utils.data.IMG
import com.android.utils.data.OnCompressListener
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.channel_create_fragment.*

/**
 * created by jiangshide on 2020/3/19.
 * email:18311271399@163.com
 */
class CreateChannelFragment : MyFragment(), HttpFileListener {

    private var id: Long = 0
    private var channelNatureId: Int = 0
    private var name: String = ""
    private var cover = ""
    private var des = ""

    private var channel: Channel? = null

    private var mFileData: FileData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.channel_create_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setTopBar(createChannelL)
        setRight("创建").setRightEnable(false)
            .setRightListener {
                if (mFileData != null) {
                    mFileData?.format = IMG
                    mFileData?.compress(object : OnCompressListener {
                        override fun onResult(dest: String?) {
                            result(dest)
                        }

                        override fun onProgress(percent: Float) {
                        }
                    })
                } else {
                    request()
                }
            }

        channelNatureId = arguments?.getInt("id")!!
        channel = arguments?.getParcelable("data")

        if (channel != null) {
            setRight("更新")
            id = channel!!.id
            channelNatureId = channel!!.natureId
            name = channel!!.name!!
            cover = channel!!.cover!!
            des = channel!!.des!!
            channelNameEdit.hint = channel?.name
            channelNameEdit.isEnabled = false
            channelIntroEdit.setText(channel?.des)
            if (!TextUtils.isEmpty(channel?.cover)) {
                Img.loadImage(channel?.cover, channelCoverImg)
            }
            validate()
        } else {
            channelNameEdit.isEnabled = true
        }

        channelNameEdit.setListener { s, input ->
            this.name = input
            validate()
        }
        channelIntroEdit.setListener { s, input ->
            this.des = input
            validate()
        }

        channelType.setOnClickListener {
//      val formatNames = arrayListOf<String>()
//      formats?.forEach {
//        formatNames.add(it.name)
//      }
//      ZdDialog.createList(activity, formatNames)
//          .setOnItemListener { parent, view, position, id ->
//            channelType.text = formatNames[position]
////            this.format = formats!![position].id
//            validate()
//          }
//          .show()
        }
        channelCoverR.setOnClickListener {
            Files().setType(IMG)
                .setMax(1)
                .setSingleListener(object : OnFileListener {
                    override fun onFile(fileData: FileData) {
                        uploadFile(fileData!!)
                    }
                })
                .open(this)
        }

        channelVM!!.createChannel.observe(this, Observer {
            hiddle()
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
            if (channel == null) {
                pop()
            } else {
                ZdEvent.get()
                    .with(MYCHANNEL_REFRESH)
                    .post(MYCHANNEL_REFRESH)
            }
            pop()
        })

//    blogVM!!.format.observe(this, Observer {
//      hiddle()
//      if (it.error != null) {
//        ZdToast.txt(it.error.message)
//        formats = Resource.format
//        return@Observer
//      }
//      Resource.format = it.data
//      formats = it.data
//    })
//    blogVM?.format()
//    showLoading()
    }

    private fun result(dest: String?) {
        LogUtil.e("dest:", dest)
        OssClient.instance
            .setListener(this).setFileData(mFileData!!).start()
    }

    private fun request() {
        channelVM?.createChannel(
            id = id, channelNatureId = channelNatureId, name = name, cover = cover, des = des
        )
        showLoading()
    }

    private fun uploadFile(fileData: FileData) {
        mFileData = fileData
        LogUtil.e("fileData:", fileData)
        Img.loadImage(fileData?.path, channelCoverImg)
        channelCoverL.visibility = View.VISIBLE

//    OssClient.instance
//        .setListener(this).setFileData(fileData).start()
    }

    private fun validate() {
        val disable = TextUtils.isEmpty(this.name) || TextUtils.isEmpty(this.des)
        LogUtil.e("disable:", disable)
        setRightEnable(!disable)
    }

    override fun onSuccess(
        position: Int,
        url: String
    ) {
        this.cover = url!!
//        validate()
        request()
    }

    override fun onFailure(
        clientExcepion: Exception,
        serviceException: Exception
    ) {
        LogUtil.e("serviceException:", serviceException)
    }

    override fun onProgress(
        currentSize: Long,
        totalSize: Long,
        progress: Int
    ) {

    }
}