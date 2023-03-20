package com.android.resource.view

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.resource.R
import com.android.resource.data.Music
import com.android.utils.LogUtil
import kotlinx.android.synthetic.main.music_player_view.view.music_coordinate
import kotlinx.android.synthetic.main.music_player_view.view.music_play
import kotlinx.android.synthetic.main.music_player_view.view.music_time
import kotlinx.android.synthetic.main.music_player_view.view.music_wave
import java.lang.ref.WeakReference

/**
 * created by jiangshide on 2019-10-25.
 * email:18311271399@163.com
 */
class MusicPlayerView(
  context: Context?,
  attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {
//  class MusicPlayerHandler(musicPlayerView: MusicPlayerView) : Handler() {
//    val musicPlayerViewRef: WeakReference<MusicPlayerView> = WeakReference(musicPlayerView)
//
//    override fun handleMessage(msg: Message?) {
//      super.handleMessage(msg)
//      val view = musicPlayerViewRef.get() ?: return
//      if (view.callback == null) return
//
//      val curr = view.callback!!.getProgress()
//      val progress = curr / 1000f
//      val intProgress = progress.toInt()
//      view.music_time.text =
//        "%02d:%02d/%02d:%02d".format(intProgress / 60,intProgress % 60,view.duration / 60, view.duration % 60
//        )
//      view.moveTo(progress / view.duration)
//      view.sendMessage()
//    }
//  }

  interface Callback {
    fun onPlayClick()

    fun onPauseClick()

    fun getProgress(): Int

  }

  var callback: MusicPlayerView.Callback? = null
  private var music: Music? = null
//  private var handler: MusicPlayerHandler = MusicPlayerHandler(this)
  private var duration: Int = 0

  init {
    View.inflate(context, R.layout.music_player_view, this)
//        setBackgroundResource(R.drawable.bg_corner_yellow_5)
    music_play.setOnClickListener {
      if (music_play.isSelected) {
        callback?.onPauseClick()
      } else {
        callback?.onPlayClick()
      }
    }
  }

  override fun onLayout(
    changed: Boolean,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
  ) {
    super.onLayout(changed, left, top, right, bottom)
    music_wave?.waveStart = ((music_coordinate.left + music_coordinate.right) / 2 - music_wave.left)
  }

  fun updateMusic(music: Music) {
    this.music = music
    music_wave?.updateMusic(music)
    this.duration = music.duration / 1000
    music_wave?.moveTo(0f)
    music_time.text = "%02d:%02d/%02d:%02d".format(0, 0, duration / 60, duration % 60)
  }

  fun updateWave(
    waveArray: List<Int>,
    duration: Long
  ) {
    music_wave.updateWaveArray(waveArray)
    this.duration = (duration / 1000).toInt()
    music_time?.text =
      "%02d:%02d/%02d:%02d".format(0, 0, this.duration / 60, this.duration % 60)
    moveTo(1f)
  }

  fun moveTo(percent: Float) {
    music_wave?.moveTo(percent)
  }

  fun onStart() {
    music_play?.isSelected = true
    handler?.removeMessages(0)
    sendMessage()
  }

  fun onPause() {
    music_play?.isSelected = false
    handler?.removeMessages(0)
  }

  fun enableClick(clickable: Boolean) {
    music_play?.isClickable = clickable
    if (music_play.isClickable) {
      music_play?.alpha = 1f
    } else {
      music_play?.alpha = .5f
    }
  }

  private fun sendMessage() {
    handler?.sendEmptyMessageDelayed(0, 50)
  }
}