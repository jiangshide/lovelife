package com.android.sanskrit.blog.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.player.audio.AudioView
import com.android.resource.MyFragment
import com.android.resource.audio.AUDIO_CAPTURE
import com.android.resource.audio.AUDIO_PROGRESS
import com.android.resource.audio.AUDIO_STATE
import com.android.resource.audio.AudioPlay
import com.android.resource.audio.AudioService
import com.android.resource.view.MusicPlayerView
import com.android.resource.vm.blog.BlogVM
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.user.audio.AUDIO_REFRESH
import com.android.utils.DateUtil
import com.android.utils.FileUtil
import com.android.utils.LogUtil
import com.android.widget.ZdRecycleView

/**
 * created by jiangshide on 2020/3/31.
 * email:18311271399@163.com
 */
class TypeAudioHolder(
  fragment: MyFragment,
  itemView: View,
  blogVM: BlogVM,
  lifecycleOwner: LifecycleOwner,
  private val adapter: BlogAdapter,
  zdRecycleView: ZdRecycleView,
  from: Int = FROM_FIND,
  private val uid: Long = -1
) : TypeAbstractViewHolder(
    fragment, itemView, blogVM, lifecycleOwner, adapter, zdRecycleView, from, uid
), MusicPlayerView.Callback {

  private var blogAudioItemType: ImageView? = null
  private var blogAudioItemRecord: MusicPlayerView? = null
  private var blogAudioItemF: FrameLayout? = null
  private var blogAudioItemCover: ImageView? = null
  private var blogAudioItemView: AudioView? = null
  private var blogAudioItemPlay: ImageView? = null
  private var blogAudioItemTime: TextView? = null
  private var blogAudioItemSource: ImageView? = null
  private var blogAudioItemProgress: ProgressBar? = null
  private var animDrawable: AnimationDrawable? = null

  @SuppressLint("SetTextI18n")
  override fun bindHolder(
    index: Int,
    blog: Blog
  ) {
    super.bindHolder(index, blog)
    blogAudioItemType = itemView?.findViewById(R.id.blogAudioItemType)
    blogAudioItemRecord = itemView?.findViewById(R.id.blogAudioItemRecord)
    blogAudioItemF = itemView?.findViewById(R.id.blogAudioItemF)
    blogAudioItemCover = itemView?.findViewById(R.id.blogAudioItemCover)
    blogAudioItemView = itemView?.findViewById(R.id.blogAudioItemView)
    blogAudioItemPlay = itemView?.findViewById(R.id.blogAudioItemPlay)
    blogAudioItemSource = itemView?.findViewById(R.id.blogAudioItemSource)
    blogAudioItemSource?.visibility = View.GONE
    if (!TextUtils.isEmpty(blog.wave)) {
      blogAudioItemType?.setImageResource(R.drawable.audio_anim)
      blogAudioItemRecord?.visibility = View.VISIBLE
      blogAudioItemF?.visibility = View.GONE
      animDrawable = blogAudioItemType?.drawable as AnimationDrawable?
//      blogAudioItemRecord?.callback = this
      blogAudioItemRecord?.updateMusic(blog.getMusic())
    } else {
      blogAudioItemType?.setImageResource(R.mipmap.audio)
      blogAudioItemRecord?.visibility = View.GONE
      blogAudioItemF?.visibility = View.VISIBLE
      animDrawable?.stop()
    }
    blogAudioItemType?.setOnClickListener {
      if (TextUtils.isEmpty(blog?.wave)) return@setOnClickListener
      if (blogAudioItemRecord?.visibility == View.VISIBLE) {
        blogAudioItemRecord?.visibility = View.GONE
        blogAudioItemF?.visibility = View.VISIBLE
      } else {
        blogAudioItemF?.visibility = View.GONE
        blogAudioItemRecord?.visibility = View.VISIBLE
      }
    }
    when (blog.source) {
      5 -> {
        blogAudioItemSource?.setImageResource(R.mipmap.sync)
        blogAudioItemSource?.visibility = View.VISIBLE
      }
    }
    val blogAudioItemTitle = itemView.findViewById<TextView>(R.id.blogAudioItemTitle)
    blogAudioItemTitle.text = blog?.name
    blogAudioItemTime = itemView?.findViewById(R.id.blogAudioItemTime)
    blogAudioItemTime?.text = DateUtil.formatSeconds(blog!!.duration / 1000)
    blogAudioItemProgress = itemView?.findViewById(R.id.blogAudioItemProgress)

    itemView?.findViewById<FrameLayout>(R.id.blogAudioItemCard)
        .setOnClickListener {
          play(blog)
        }

    ZdEvent.get()
        .with(AUDIO_PROGRESS, Int::class.java)
        .observes(lifecycleOwner, Observer {
          if (blogAudioItemProgress == null) return@Observer
          if (AudioPlay.getInstance().data.url == blog.url) {
            blogAudioItemProgress?.max = AudioPlay.getInstance()
                .duration()
            blogAudioItemProgress?.progress = it.toInt()

            blogAudioItemTime?.text = "${DateUtil.formatSeconds(
                AudioPlay.getInstance()
                    .duration() / 1000
            )}/${DateUtil.formatSeconds(it / 1000)}"
            blogAudioItemRecord?.moveTo(
                AudioPlay.getInstance()
                    .progress()
            )
            blogAudioItemProgress?.visibility = View.VISIBLE
          } else {
            blogAudioItemProgress?.visibility = View.GONE
          }
        })
    ZdEvent.get()
        .with(AUDIO_CAPTURE, ByteArray::class.java)
        .observes(lifecycleOwner, Observer {
          if (blogAudioItemView == null) return@Observer
          if (AudioPlay.getInstance().data.url == blog.url) {
            blogAudioItemView?.post { blogAudioItemView?.setWaveData(it) }
            blogAudioItemView?.visibility = View.VISIBLE
          } else {
            blogAudioItemView?.visibility = View.GONE
          }
        })

    blogAudioItemPlay?.setImageResource(R.mipmap.play)
    Img.loadImage(blog.cover, blogAudioItemCover, R.drawable.audio)
    blogAudioItemProgress?.max = blog.duration.toInt()
    ZdEvent.get()
        .with(AUDIO_STATE, AudioPlay.PlayState::class.java)
        .observes(lifecycleOwner, Observer {
          if (blogAudioItemView == null) return@Observer
          if (AudioPlay.getInstance().data.url != blog.url) {
            blogAudioItemPlay?.visibility = View.VISIBLE
            blogAudioItemPlay?.setImageResource(R.mipmap.play)
            blogAudioItemView?.visibility = View.GONE
            blogAudioItemRecord?.onPause()
            blogAudioItemRecord?.moveTo(0f)
            blogAudioItemTime?.text = ""
            animDrawable?.stop()
            return@Observer
          }
          when (it) {
            AudioPlay.PlayState.STATE_INITIALIZING -> {
              blogAudioItemPlay?.setImageResource(R.mipmap.play)
            }
            AudioPlay.PlayState.STATE_PLAYING -> {
              blogAudioItemView?.visibility = View.VISIBLE
              blogAudioItemPlay?.visibility = View.GONE
              blogAudioItemRecord?.updateWave(
                  blog?.getMusic()?.waveArray!!, AudioPlay.getInstance()!!
                  .duration()
                  .toLong()
              )
              blogAudioItemRecord?.onStart()
              animDrawable?.start()
            }
            AudioPlay.PlayState.STATE_RESUME -> {
              blogAudioItemPlay?.visibility = View.GONE
              blogAudioItemView?.visibility = View.VISIBLE
              blogAudioItemRecord?.onStart()
              animDrawable?.start()
            }
            AudioPlay.PlayState.STATE_PAUSE -> {
              blogAudioItemPlay?.setImageResource(R.mipmap.pause)
              blogAudioItemPlay?.visibility = View.VISIBLE
              blogAudioItemView?.visibility = View.GONE
              blogAudioItemRecord?.onPause()
              animDrawable?.stop()
            }
            AudioPlay.PlayState.STATE_IDLE -> {
              blogAudioItemPlay?.setImageResource(R.mipmap.play)
              blogAudioItemView?.visibility = View.GONE
              blogAudioItemPlay?.visibility = View.VISIBLE
              blogAudioItemRecord?.onPause()
              animDrawable?.stop()
            }
          }
        })
    val audio = AudioPlay.getInstance()
    if (audio.isPlaying && audio.data.url == blog.url) {
      blogAudioItemPlay?.visibility = View.GONE
      blogAudioItemView?.visibility = View.VISIBLE
    } else {
      blogAudioItemPlay?.visibility = View.VISIBLE
      blogAudioItemView?.visibility = View.GONE
    }
  }

  private fun play(
    blog: Blog
  ) {
    val audio = AudioPlay.getInstance()
    if (audio.mData == null || audio.mData.size == 0) {
      val data = ArrayList<Blog>()
      var i = -1
      var currIndex = 0
      adapter?.data()
          .forEach {
            if (FileUtil.isAudio(it.url)) {
              i++
              data.add(it)
              if (blog.url == it.url) {
                currIndex = i
              }
            }
          }
      AudioService.startAudioCommand(AudioService.CMD_INIT, data = data)
      AudioService.startAudioCommand(AudioService.CMD_PLAY, currIndex)
    } else {
      audio.mData.forEachIndexed { index, it ->
        if (FileUtil.isAudio(it.url) && it.url == blog.url) {
          if (audio.data.url == blog.url) {
            AudioService.startAudioCommand(AudioService.CMD_START)
          } else {
            AudioService.startAudioCommand(AudioService.CMD_PLAY, index)
            ZdEvent.get()
                .with(AUDIO_REFRESH)
                .post(AUDIO_REFRESH)
          }
          return
        }
      }
      AudioPlay.getInstance().data = blog
      AudioService.startAudioCommand(AudioService.CMD_PLAY)
    }
  }

  override fun onPlayClick() {
    LogUtil.e("------onPlayClick")
  }

  override fun onPauseClick() {
    LogUtil.e("------onPauseClick")
  }

  override fun getProgress(): Int {
    return 0
  }
}