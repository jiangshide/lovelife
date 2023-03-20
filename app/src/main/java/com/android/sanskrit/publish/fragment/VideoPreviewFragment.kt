package com.android.sanskrit.publish.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.android.img.Img
import com.android.img.listener.IBitmapListener
import com.android.player.dplay.listener.OnVideoViewStateChangeListener
import com.android.player.dplay.player.VideoView
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.sanskrit.blog.RotateInFullscreenController
import com.android.utils.ImgUtil
import com.android.utils.LogUtil
import com.android.utils.ScreenUtil
import com.android.utils.data.FileData
import kotlinx.android.synthetic.main.video_preview_fragment.*

/**
 * created by jiangshide on 2020/4/24.
 * email:18311271399@163.com
 */
class VideoPreviewFragment : MyFragment() {

    private var path: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.video_preview_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
//    ZdStatusBar.setColorForSwipeBack(mActivity, getColor(R.color.black))
        val fileData: FileData = arguments!!.getParcelable("data")!!
        if (fileData == null) pop()
        val controller = RotateInFullscreenController(mActivity)
        val layoutParams = FrameLayout.LayoutParams(
            ScreenUtil.getRtScreenWidth(mActivity), ScreenUtil.getRtScreenHeight(mActivity)
        )
        layoutParams.gravity = Gravity.CENTER
        videoPreview.layoutParams = layoutParams
        videoPreview.setLooping(true)
        if (!TextUtils.isEmpty(fileData.outPath)) {
            path = fileData.outPath!!
        } else {
            path = fileData.path!!
        }
        Img.loadImage(path, controller.thumb)
        videoPreview.setUrl(path)
        videoPreview.setLooping(true)
        videoPreview.setVideoController(controller)
        //这段代码用于实现小屏时静音，全屏时有声音
        videoPreview.setOnVideoViewStateChangeListener(object : OnVideoViewStateChangeListener {
            override fun onPlayStateChanged(playState: Int) {
                //小屏状态下播放出来之后，把声音关闭
                if (playState == VideoView.STATE_PREPARED && !videoPreview.isFullScreen) {
                    videoPreview.isMute = false
                }
            }

            override fun onPlayerStateChanged(playerState: Int) {
                if (playerState == VideoView.PLAYER_FULL_SCREEN) {
                    videoPreview.isMute = false
                    videoPreview.setBg(ContextCompat.getColor(mActivity, R.color.bg))
                } else if (playerState == VideoView.PLAYER_NORMAL) {
                    videoPreview.isMute = false
                    Img.loadImage(path, object : IBitmapListener {
                        override fun onSuccess(bitmap: Bitmap?): Boolean {
                            videoPreview.setBg(ContextCompat.getColor(mActivity, R.color.bg))
//                            videoPreview.setBg(ImgUtil.bitmap2Drawable(bitmap))
                            return true
                        }

                        override fun onFailure(): Boolean {
                            return true
                        }
                    })
                }
            }
        })
        videoPreview.start()
    }

    override fun onPause() {
        super.onPause()
        videoPreview?.release()
    }

    override fun onStop() {
        super.onStop()
        videoPreview?.release()
    }
}