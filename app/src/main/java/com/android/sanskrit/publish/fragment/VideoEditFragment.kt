package com.android.sanskrit.publish.fragment

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.img.Img
import com.android.player.dplay.listener.OnVideoViewStateChangeListener
import com.android.player.dplay.player.VideoView.STATE_PLAYING
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.sanskrit.blog.RotateInFullscreenController
import com.android.utils.FileUtil
import com.android.utils.LogUtil
import com.android.widget.seekbar.OnRangeChangedListener
import com.android.widget.seekbar.RangeSeekBar
import com.hw.videoprocessor.MixVideo
import com.hw.videoprocessor.VideoEffects
import com.hw.videoprocessor.VideoProcessor
import kotlinx.android.synthetic.main.video_edit.*

/**
 * created by jiangshide on 2020/7/21.
 * email:18311271399@163.com
 */
class VideoEditFragment(
    private val url: String,
    private val duration: Float,
    private val listener: OnAudioFinishListener
) : MyFragment(), OnRangeChangedListener, OnVideoViewStateChangeListener, OnAudioFinishListener {

    private var runnable: Runnable? = null
    private var start = 0
    private var end = 0
    private var speed = 0f

    private var path = url
    private var mixOut: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.video_edit)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val controller = RotateInFullscreenController(mActivity)
        end = duration.toInt()
        rangeSeekBar.setRange(start.toFloat(), duration)
        Img.loadImage(path, controller.thumb)
        mixVideoView.setVideoController(controller)
        mixVideoView.setOnVideoViewStateChangeListener(this)
        mixVideoView.setUrl(path)
        mixVideoView.setLooping(true)
        mixVideoView.start()
        removeAudio.setOnClickListener {
            removeAudio?.isSelected = !removeAudio?.isSelected!!
            var sourcePath = path
            if (removeAudio?.isSelected!!) {
                try {
                    sourcePath = MixVideo.muxerMedia(path)
                    mixOut = "消除原音"
                    finishPath(sourcePath)
                } catch (e: Exception) {
                    LogUtil.e(e)
                }
            }
            mixVideoView?.release()
            mixVideoView?.setUrl(sourcePath)
            mixVideoView?.setLooping(true)
            mixVideoView?.start()
        }

        mixVideoCut.setOnClickListener {
            cutVideo()
        }

        mixVideoScale.setOnClickListener {
            scaleVideo()
        }

        mixVideoAudio.setOnClickListener {
            push(EditAudioManagerFragment(this))
//      mixAudio()
        }

        mixVideoInCreate.setOnClickListener {
            inOrDeCreate(2f)
        }

        mixVideoDeCreate.setOnClickListener {
            inOrDeCreate(0.5f)
        }

        kichikuVideoDeCreate.setOnClickListener {
            kichiku()
        }

        mixVideoReverse.setOnClickListener {
            reverse()
        }

        rangeSeekBar.setOnRangeChangedListener(this)
    }

    private var handler = Handler {
        hiddle()
        when (it.what) {
            1 -> {
                val sourcePath = it.obj.toString()
                mixVideoView?.release()
                mixVideoView?.setUrl(sourcePath)
                mixVideoView?.setLooping(true)
                mixVideoView?.start()
                finishPath(sourcePath)
            }
            -1 ->{
                hiddle()
            }
        }
        return@Handler true
    }

    private fun finishPath(sourcePath: String) {
        setTitle(FileUtil.getFileNameNoExtension(sourcePath)).setRight("完成").setRightListener {
            listener?.onPath(sourcePath, mixOut)
            pop()
        }
    }

    private fun cutVideo() {
        showLoading()
        val thread = Thread {
            try {
                val outPath = VideoProcessor.cutVideo(path, start, end)
                mixOut = "裁剪"
                val msg = Message()
                msg.what = 1
                msg.obj = outPath
                handler.sendMessage(msg)
            } catch (e: Exception) {
                LogUtil.e(e)
                val msg = Message()
                msg.what = -1
                handler.sendMessage(msg)
            }
        }
        thread.start()
    }

    private fun scaleVideo() {
        showLoading()
        val thread = Thread {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(mActivity, Uri.parse(path))
                val originWidth =
                    (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))!!.toInt()
                val originHeight =
                    (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))!!
                        .toInt()
                val bitrate =
                    (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE))!!
                        .toInt()

                val outWidth = originWidth / 2
                val outHeight = originHeight / 2
                val outPath = VideoProcessor.processor()
                    .input(path)
                    .outWidth(outWidth)
                    .outHeight(outHeight)
                    .startTimeMs(start)
                    .endTimeMs(end)
                    .bitrate(bitrate / 2)
                    .process()
                val msg = Message()
                mixOut = "魔放"
                msg.what = 1
                msg.obj = outPath
                handler.sendMessage(msg)
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(e)
                val msg = Message()
                msg.what = -1
                handler.sendMessage(msg)
            }
        }
        thread.start()
    }

    /**
     * 不需要改变音频速率的情况下，直接读写就可
     * 只支持16bit音频
     */
    private fun mixAudio(audioPath: String?) {//"/storage/emulated/0/KuwoMusic/music/兄弟报抱一下-庞龙-3534986.mp3"
        showLoading()
        var sourcePath = path
        if (removeAudio.isSelected) {
            try {
                sourcePath = MixVideo.muxerMedia(path)
                mixOut = FileUtil.getFileName(audioPath)
            } catch (e: Exception) {
                LogUtil.e(e)
            }
        }
        LogUtil.e("-------audioPath~path:", sourcePath, " | audioPath:", audioPath)
        mixVideoView?.release()
        mixVideoView?.setUrl(sourcePath)
        mixVideoView?.start()
        val thread = Thread {
            try {
                val outPath = VideoProcessor.mixAudioTrack(
                    sourcePath, audioPath, start, end
                )
                val msg = Message()
                msg.what = 1
                msg.obj = outPath
                handler.sendMessage(msg)
            } catch (e: Exception) {
                LogUtil.e(e)
                val msg = Message()
                msg.what = -1
                handler.sendMessage(msg)
            }
        }
        thread.start()
    }

    private fun inOrDeCreate(speed: Float) {//increate:2f,decreate:0.5f
        showLoading()
        val thread = Thread {
            try {
                val outPath = VideoProcessor.processor()
                    .input(path)
                    .startTimeMs(start)
                    .endTimeMs(end)
                    .speed(speed)
                    .changeAudioSpeed(true)
                    .process()
                if(speed == 2f){
                    mixOut = "魔快"
                }else{
                    mixOut = "魔慢"
                }
                val msg = Message()
                msg.what = 1
                msg.obj = outPath
                handler.sendMessage(msg)
            } catch (e: Exception) {
                LogUtil.e(e)
                val msg = Message()
                msg.what = -1
                handler.sendMessage(msg)
            }
        }
        thread.start()
    }

    /**
     * 对视频先检查，如果不是全关键帧，先处理成所有帧都是关键帧，再逆序
     */
    private fun reverse() {
        showLoading()
        val thread = Thread {
            try {
                val outPath = VideoProcessor.reverseVideo(path)
                val msg = Message()
                mixOut = "逆放"
                msg.what = 1
                msg.obj = outPath
                handler.sendMessage(msg)
            } catch (e: Exception) {
                LogUtil.e(e)
                val msg = Message()
                msg.what = -1
                handler.sendMessage(msg)
            }
        }
        thread.start()
    }

    /**
     * 鬼畜效果，先按speed倍率对视频进行加速，然后按splitTimeMs分割视频，并对每一个片段做正放+倒放
     */
    private fun kichiku() {
        showLoading()
        val thread = Thread {
            try {
                val outPath = VideoEffects.doKichiku(path)
                val msg = Message()
                msg.what = 1
                msg.obj = outPath
                handler.sendMessage(msg)
            } catch (e: Exception) {
                LogUtil.e(e)
                val msg = Message()
                msg.what = -1
                handler.sendMessage(msg)
            }
        }
        thread.start()
    }

    override fun onPause() {
        super.onPause()
        mixVideoView?.release()
    }

    override fun onStop() {
        super.onStop()
        mixVideoView?.release()
    }

    override fun onStartTrackingTouch(
        view: RangeSeekBar?,
        isLeft: Boolean
    ) {
    }

    override fun onRangeChanged(
        view: RangeSeekBar?,
        leftValue: Float,
        rightValue: Float,
        isFromUser: Boolean
    ) {
        start = leftValue.toInt()
        end = rightValue.toInt()
        mixVideoView?.seekTo(leftValue.toLong())
    }

    override fun onStopTrackingTouch(
        view: RangeSeekBar?,
        isLeft: Boolean
    ) {
    }

    override fun onPlayStateChanged(playState: Int) {
        if (mixVideoView == null) return
        when (playState) {
            STATE_PLAYING -> {
                val handler = Handler()
                handler.postDelayed(Runnable {
                    if (mixVideoView == null) return@Runnable
                    if (mixVideoView?.currentPosition!! >= end
                    ) {
                        mixVideoView?.seekTo(start.toLong())
                    }
                    handler.postDelayed(runnable!!, 1000)
                }
                    .also { runnable = it }, 1000)
            }
        }
    }

    override fun onPlayerStateChanged(playerState: Int) {
    }

    override fun onPath(audioPath: String?, mixOut: String?) {
        LogUtil.e("---------audioPath:", audioPath)
        mixAudio(audioPath)
    }
}