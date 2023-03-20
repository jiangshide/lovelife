package com.android.sanskrit.blog.adapter

import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.android.img.Img
import com.android.img.listener.IBitmapListener
import com.android.player.dplay.listener.OnVideoViewStateChangeListener
import com.android.player.dplay.player.AbstractPlayer
import com.android.player.dplay.player.VideoView
import com.android.resource.MyFragment
import com.android.resource.vm.blog.BlogVM
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.App
import com.android.sanskrit.R
import com.android.sanskrit.blog.RotateInFullscreenController
import com.android.utils.ImgUtil
import com.android.widget.ZdRecycleView

/**
 * created by jiangshide on 2020/3/31.
 * email:18311271399@163.com
 */
class TypeVideoHolder(
    fragment: MyFragment,
    itemView: View,
    blogVM: BlogVM,
    lifecycleOwner: LifecycleOwner,
    private val adapter: BlogAdapter,
    private val zdRecycleView: ZdRecycleView,
    from: Int = FROM_FIND,
    private val uid: Long = -1
) : TypeAbstractViewHolder(
    fragment, itemView, blogVM, lifecycleOwner, adapter, zdRecycleView, from, uid
) {

    override fun bindHolder(
        index: Int,
        blog: Blog
    ) {
        super.bindHolder(index, blog)

        val videoPlayer = itemView.findViewById<VideoView<AbstractPlayer>>(R.id.videoPlayer)
        val controller = RotateInFullscreenController(itemView.context)
        var cover = blog.cover
        if (TextUtils.isEmpty(cover)) {
            cover = blog.url
        }
        Img.loadImage(cover, 0, object : IBitmapListener {
            override fun onSuccess(bitmap: Bitmap?): Boolean {
                videoPlayer.setBg(ImgUtil.bitmap2Drawable(bitmap))
                return true
            }

            override fun onFailure(): Boolean {

                return true
            }
        })
        Img.loadImage(cover, controller.thumb)
        val app = fragment.context().applicationContext as App
        videoPlayer.setUrl(app.getAudioUrl(blog.url!!))
        videoPlayer.setLooping(true)
        videoPlayer.setVideoController(controller)
        val widthPixels: Int = itemView.resources
            .displayMetrics.widthPixels
        val heightPixels: Int = itemView.resources
            .displayMetrics.heightPixels
        if (blog.height > blog.width) {
            videoPlayer.layoutParams =
                LayoutParams(widthPixels, heightPixels / 2 + 1)
        } else {
            videoPlayer.layoutParams =
                LayoutParams(widthPixels, widthPixels * 9 / 16 + 1)
        }
        //这段代码用于实现小屏时静音，全屏时有声音
        videoPlayer.setOnVideoViewStateChangeListener(object : OnVideoViewStateChangeListener {
            override fun onPlayStateChanged(playState: Int) {
                //小屏状态下播放出来之后，把声音关闭
                if (playState == VideoView.STATE_PREPARED && !videoPlayer.isFullScreen) {
                    videoPlayer.isMute = true
                }
            }

            override fun onPlayerStateChanged(playerState: Int) {
                if (playerState == VideoView.PLAYER_FULL_SCREEN) {
                    videoPlayer.isMute = false
                    videoPlayer.setBg(ContextCompat.getColor(itemView.context, R.color.bg))
                } else if (playerState == VideoView.PLAYER_NORMAL) {
                    videoPlayer.isMute = true
                    Img.loadImage(cover, 0, object : IBitmapListener {
                        override fun onSuccess(bitmap: Bitmap?): Boolean {
                            videoPlayer.setBg(ImgUtil.bitmap2Drawable(bitmap))
                            return true
                        }

                        override fun onFailure(): Boolean {
                            return true
                        }
                    })
                }
            }
        })
    }
}