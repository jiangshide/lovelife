package com.android.sanskrit.user.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.resource.audio.AUDIO_PROGRESS
import com.android.resource.audio.AudioPlay
import com.android.resource.vm.blog.data.Lrc
import com.android.sanskrit.R
import com.android.utils.LogUtil
import kotlinx.android.synthetic.main.lrc_fragment.lrcSelected
import kotlinx.android.synthetic.main.lrc_fragment.lrcView

/**
 * created by jiangshide on 2020/6/8.
 * email:18311271399@163.com
 */
class LrcFragment(private val lrc: Lrc) : MyFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setView(R.layout.lrc_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    lrcView?.setOnLrcListener {
      if (it == null || it.size == 0) {
        lrcSelected.visibility = View.GONE
      } else {
        lrcSelected.visibility = View.VISIBLE
      }
    }
        ?.loadLrcByUrl(lrc.lrc)
    lrcView.setDraggable(true) { time ->
      val currTime: Int = time.toInt()
      AudioPlay.getInstance()
          .seekTo(currTime)
      true
    }
    lrcSelected.setOnClickListener {
      ZdEvent.get()
          .with(LRC_SELECTED)
          .post(lrc)
    }
    ZdEvent.get()
        .with(AUDIO_PROGRESS, Int::class.java)
        .observes(this, Observer {
          if (lrcView == null) return@Observer
          lrcView?.updateTime(it.toLong())
        })
    ZdEvent.get()
        .with(LRC_SELECTED, Lrc::class.java)
        .observes(this, Observer {
          lrcSelected.isSelected = it == lrc
        })
  }
}