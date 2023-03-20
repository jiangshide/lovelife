package com.android.sanskrit.user.audio

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.resource.audio.AudioPlay
import com.android.resource.audio.AudioService
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.widget.adapter.AbstractAdapter.OnItemListener
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.user_audio_location_fragment_item.view.audioLocationItemClose
import kotlinx.android.synthetic.main.user_audio_location_fragment_item.view.audioLocationItemDes
import kotlinx.android.synthetic.main.user_audio_location_fragment_item.view.audioLocationItemIcon
import kotlinx.android.synthetic.main.user_audio_location_fragment_item.view.audioLocationItemIconBtn
import kotlinx.android.synthetic.main.user_audio_location_fragment_item.view.audioLocationItemTitle
import kotlinx.android.synthetic.main.user_audio_play_list_fragment.audioPlayRecycleView

/**
 * created by jiangshide on 2020/5/14.
 * email:18311271399@163.com
 */
class PlayListFragment : MyFragment() {

  private var adapter: KAdapter<Blog>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    isBackground = false
    return setView(R.layout.user_audio_play_list_fragment)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    checkPlayList()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    ZdEvent.get()
        .with(AUDIO_REFRESH)
        .observes(this, Observer {
          checkPlayList()
        })

    checkPlayList()
  }

  private fun checkPlayList() {
    hiddle()
    val data = AudioPlay.getInstance().mData
    if (data != null && data.size > 0) {
      showView(data)
    } else {
      noNet("暂无播放!").setTipsRes(mipmap.no_data)
    }
  }

  private fun showView(
    data: MutableList<Blog>
  ) {
    adapter = audioPlayRecycleView.create(data, R.layout.user_audio_location_fragment_item, {
      val fileData = it
      it.setCover(audioLocationItemIcon, audioLocationItemIconBtn)
      val data = AudioPlay.getInstance().data
      if (it.url == data.url) {
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
      audioLocationItemClose.visibility = View.VISIBLE
      audioLocationItemClose.setOnClickListener {
        adapter?.remove(fileData)
      }
    }, {
    })
        .setItemListener(object : OnItemListener<Blog> {
          override fun onItem(
            position: Int,
            item: Blog
          ) {
            AudioService.startAudioCommand(AudioService.CMD_PLAY, position)
          }
        })
  }
}
