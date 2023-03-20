package com.android.sanskrit.user.audio

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.audio.AUDIO_PROGRESS
import com.android.resource.audio.AUDIO_STATE
import com.android.resource.audio.AudioPlay
import com.android.resource.audio.AudioService
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.mine.fragment.set.FeedbackFragment
import com.android.sanskrit.user.UserFragment
import com.android.utils.DateUtil
import com.android.utils.SPUtil
import com.android.widget.ZdButton
import com.android.widget.ZdDialog
import com.android.widget.ZdDialog.DialogViewListener
import kotlinx.android.synthetic.main.user_audio_lrc_fragment.lrcBtn
import kotlinx.android.synthetic.main.user_audio_lrc_fragment.lrcPlay
import kotlinx.android.synthetic.main.user_audio_lrc_fragment.lrcSeekBar
import kotlinx.android.synthetic.main.user_audio_lrc_fragment.lrcSeekTime
import kotlinx.android.synthetic.main.user_audio_lrc_fragment.lrcSeekTimeStart
import kotlinx.android.synthetic.main.user_audio_lrc_fragment.lrcView
import java.io.IOException
import java.io.InputStream

/**
 * created by jiangshide on 2020/4/21.
 * email:18311271399@163.com
 */
class AudioLrcFragment : MyFragment(), DialogViewListener {

  private var progress: Long = 0
  private var mBlog: Blog? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    isBackground = false
    return setView(R.layout.user_audio_lrc_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    lrcBtn.setOnClickListener {
      if(Resource.user == null){
        goFragment(UserFragment::class.java)
        return@setOnClickListener
      }
      ZdDialog.createView(mActivity, R.layout.user_audio_lrc_fragment_dialog, this)
          .setAnim(R.anim.bottom_enter)
          .setGravity(Gravity.BOTTOM)
          .show()
    }

    ZdEvent.get()
        .with(LRC_REFRESH, Blog::class.java)
        .observes(this, Observer {
          loadLrc(it, true)
        })

    ZdEvent.get()
        .with(AUDIO_STATE, AudioPlay.PlayState::class.java)
        .observes(this, Observer {
          if (lrcView == null) return@Observer
          when (it) {
            AudioPlay.PlayState.STATE_INITIALIZING -> {
              loadLrc(AudioPlay.getInstance().data, true)
              lrcSeekTime.text = DateUtil.formatSeconds(
                  AudioPlay.getInstance()
                      .duration() / 1000
              )
              lrcSeekBar.max = AudioPlay.getInstance()
                  .duration()
              lrcSeekBar.progress = 0
            }
            AudioPlay.PlayState.STATE_PLAYING -> {
              lrcPlay.isSelected = true
            }
            AudioPlay.PlayState.STATE_RESUME -> {
              lrcPlay.isSelected = true
            }
            AudioPlay.PlayState.STATE_PAUSE -> {
              lrcPlay.isSelected = false
            }
            AudioPlay.PlayState.STATE_IDLE -> {
              lrcPlay.isSelected = false
              lrcSeekBar.progress = 0
              lrcView.updateTime(0 + progress)
            }
          }
        })

    ZdEvent.get()
        .with(AUDIO_PROGRESS, Int::class.java)
        .observes(this, Observer {
          if (lrcSeekBar == null) return@Observer
          showProgress(it)
        })

    lrcPlay.setOnClickListener {
      AudioService.startAudioCommand(AudioService.CMD_START)
    }

    initView()
  }

  private fun showProgress(progress: Int) {
    lrcView.updateTime(progress.toLong())
    lrcPlay.isSelected = true
    lrcSeekBar.progress = progress
    lrcSeekTimeStart.text = DateUtil.formatSeconds(progress / 1000)
    lrcSeekTime.text = DateUtil.formatSeconds(AudioPlay.getInstance().duration()/1000)
  }

  private fun initView() {

    val audipPlay = AudioPlay.getInstance()
    if (audipPlay.data != null && !TextUtils.isEmpty(audipPlay.data.lrcZh)) {
      loadLrc(audipPlay!!.data, true)
    }
    lrcView.setDraggable(true) { time ->
      val currTime: Int = time.toInt()
      AudioPlay.getInstance()
          .seekTo(currTime)
      true
    }

    lrcSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
      override fun onProgressChanged(
        seekBar: SeekBar,
        progress: Int,
        fromUser: Boolean
      ) {
      }

      override fun onStartTrackingTouch(seekBar: SeekBar) {}
      override fun onStopTrackingTouch(seekBar: SeekBar) {
//        mediaPlayer.seekTo(seekBar.pro\
        AudioPlay.getInstance()
            .seekTo(seekBar.progress)
        lrcView.updateTime(seekBar.progress.toLong() + progress)
      }
    })
    if (audipPlay.isPlaying) {
      val duration = audipPlay.duration()
      lrcSeekBar.max = duration
      lrcSeekTime.text = DateUtil.formatSeconds(duration / 1000)
      showProgress(audipPlay.currentPosition())
    }
    val size = SPUtil.getFloat(FONT_SIZE)
    lrcView.setCurrentTextSize(size)
    lrcView.setNormalTextSize(size - 5)
    val fontLightColor = SPUtil.getInt(FONT_LIGHT_COLOR)
    if (fontLightColor != -1) {
      lrcView.setCurrentColor(fontLightColor)
    }
    val fontColor = SPUtil.getInt(FONT_COLOR)
    if (fontColor != -1) {
      lrcView.setNormalColor(fontColor)
    }
    blogVM!!.searchLrc.observe(this, Observer {
      if (it.error != null || it?.data?.result == null || it?.data?.result?.size == 0) {
        return@Observer
      }
      mBlog?.lrcZh = it?.data.result!![0].lrc
      AudioPlay.getInstance()
          .update(mBlog)
      loadLrc(mBlog, true)
    })
  }

  private fun loadLrc(
    blog: Blog?,
    online: Boolean
  ) {
    if (blog == null) return
    mBlog = blog
    if (TextUtils.isEmpty(blog.lrcZh) && TextUtils.isEmpty(blog.lrcEn)) {
      blogVM?.searchLrc(blog.uid, name = blog.name!!)
      return
    }
    if (online) {
//      lrcView.loadLrcByUrl(blog.lrcZh,"gb2312")
      if (!TextUtils.isEmpty(blog.lrcZh)) {
        lrcView.loadLrcByUrl(blog.lrcZh)
      } else {
        lrcView.loadLrcByUrl(blog.lrcEn)
      }
    } else {
      // 加载歌词文件
      // File mainLrcFile = new File("/sdcard/Download/send_it_cn.lrc");
      // File secondLrcFile = new File("/sdcard/Download/send_it_en.lrc");
      // lrcView.loadLrc(mainLrcFile, secondLrcFile);
    }
  }

  private fun getLrcText(fileName: String): String? {
    var lrcText: String? = null
    try {
      val `is`: InputStream = mActivity.assets.open(fileName)
      val size = `is`.available()
      val buffer = ByteArray(size)
      `is`.read(buffer)
      `is`.close()
      lrcText = String(buffer)
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return lrcText
  }

  override fun onView(view: View) {
    val lrcSearch = view.findViewById<ImageButton>(R.id.lrcSearch)
    lrcSearch.setOnClickListener {
      val blog = AudioPlay.getInstance().data
      if (blog != null) {
        push(LrcSearchFragment().setTitle("歌词搜索-"+blog.title), blog)
      }
      ZdDialog.cancelDialog()
    }
    val audioDetail = view.findViewById<ImageButton>(R.id.audioDetail)
    audioDetail.setOnClickListener {

    }
    val makeLrc = view.findViewById<ImageButton>(R.id.makeLrc)
    makeLrc.setOnClickListener {
      val blog = AudioPlay.getInstance().data
      if (blog != null) {
        push(LrcAddFragment(blog).setTitle("添加歌词-"+blog.title))
      }
      ZdDialog.cancelDialog()
    }
    val lrcFeedback = view.findViewById<ImageButton>(R.id.lrcFeedback)
    lrcFeedback.setOnClickListener {
      val blog = AudioPlay.getInstance().data
      if (blog != null) {
        push(LrcFeedbackFragment(blog).setTitle("歌词反馈-"+blog.name), blog)
      }

      ZdDialog.cancelDialog()
    }

    val progressAdd = view.findViewById<ZdButton>(R.id.progressAdd)
    progressAdd.setOnClickListener {
      progress += 500
      lrcView.updateTime(
          AudioPlay.getInstance()
              .duration() + progress
      )
    }
    val progressReset = view.findViewById<ZdButton>(R.id.progressReset)
    progressReset.setOnClickListener {
      progress = 0
      lrcView.updateTime(
          AudioPlay.getInstance()
              .duration() + progress
      )
    }
    val progressReduce = view.findViewById<ZdButton>(R.id.progressReduce)
    progressReduce.setOnClickListener {
      progress -= 500
      lrcView.updateTime(
          AudioPlay.getInstance()
              .duration() + progress
      )
    }

    val fontSizeSeekBar = view.findViewById<SeekBar>(R.id.fontSizeSeekBar)
    fontSizeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
      override fun onProgressChanged(
        seekBar: SeekBar?,
        progress: Int,
        fromUser: Boolean
      ) {
        val size = (FONT_SIZE_DEFAULT + progress).toFloat()
        SPUtil.putFloat(FONT_SIZE, size)
        lrcView.setCurrentTextSize(size)
        lrcView.setNormalTextSize(size - 5)
      }

      override fun onStartTrackingTouch(seekBar: SeekBar?) {
      }

      override fun onStopTrackingTouch(seekBar: SeekBar?) {
      }

    })
    val fontReduce = view.findViewById<ZdButton>(R.id.fontReduce)
    fontReduce.setOnClickListener {
      fontSizeSeekBar.progress -= 5
      if (fontSizeSeekBar.progress <= 0) {
        fontSizeSeekBar.progress = 0
      }

    }
    val fontAdd = view.findViewById<ZdButton>(R.id.fontAdd)
    fontAdd.setOnClickListener {
      fontSizeSeekBar.progress += 5
      if (fontSizeSeekBar.progress >= 50) {
        fontSizeSeekBar.progress = 50
      }
    }

    val fontLightColorBlue = view.findViewById<ImageButton>(R.id.fontLightColorBlue)
    fontLightColorBlue.setOnClickListener {
      val color = getColor(R.color.blue)
      lrcView.setNormalColor(color)
      SPUtil.putInt(FONT_LIGHT_COLOR, color)
    }
    val fontLightColorFans = view.findViewById<ImageButton>(R.id.fontLightColorFans)
    fontLightColorFans.setOnClickListener {
      val color = getColor(R.color.fans)
      lrcView.setNormalColor(color)
      SPUtil.putInt(FONT_LIGHT_COLOR, color)
    }
    val fontLightColorYellow = view.findViewById<ImageButton>(R.id.fontLightColorYellow)
    fontLightColorYellow.setOnClickListener {
      val color = getColor(R.color.yellowLight)
      lrcView.setNormalColor(color)
      SPUtil.putInt(FONT_LIGHT_COLOR, color)
    }
    val fontLightColorGreen = view.findViewById<ImageButton>(R.id.fontLightColorGreen)
    fontLightColorGreen.setOnClickListener {
      val color = getColor(R.color.green)
      lrcView.setNormalColor(color)
      SPUtil.putInt(FONT_LIGHT_COLOR, color)
    }
    val fontLightColorRandom = view.findViewById<ZdButton>(R.id.fontLightColorRandom)
    fontLightColorRandom.setOnClickListener {
      val color = fontLightColorRandom.setRandomColor()
      lrcView.setNormalColor(color)
      SPUtil.putInt(FONT_LIGHT_COLOR, color)
    }

    val fontColorBlue = view.findViewById<ImageButton>(R.id.fontColorBlue)
    fontColorBlue.setOnClickListener {
      val color = getColor(R.color.blue)
      lrcView.setCurrentColor(color)
      SPUtil.putInt(FONT_COLOR, color)
    }
    val fontColorFans = view.findViewById<ImageButton>(R.id.fontColorFans)
    fontColorFans.setOnClickListener {
      val color = getColor(R.color.fans)
      lrcView.setCurrentColor(color)
      SPUtil.putInt(FONT_COLOR, color)
    }
    val fontColorYellow = view.findViewById<ImageButton>(R.id.fontColorYellow)
    fontColorYellow.setOnClickListener {
      val color = getColor(R.color.yellowLight)
      lrcView.setCurrentColor(color)
      SPUtil.putInt(FONT_COLOR, color)
    }
    val fontColorGreen = view.findViewById<ImageButton>(R.id.fontColorGreen)
    fontColorGreen.setOnClickListener {
      val color = getColor(R.color.green)
      lrcView.setCurrentColor(color)
      SPUtil.putInt(FONT_COLOR, color)
    }
    val fontColorRandom = view.findViewById<ZdButton>(R.id.fontColorRandom)
    fontColorRandom.setOnClickListener {
      val color = fontColorRandom.setRandomColor()
      lrcView.setCurrentColor(color)
      SPUtil.putInt(FONT_COLOR, color)
    }

    val lrcDialogCancel = view.findViewById<ZdButton>(R.id.lrcDialogCancel)
    lrcDialogCancel.setOnClickListener {
      ZdDialog.cancelDialog()
    }
  }
}

const val FONT_LIGHT_COLOR = "fontLightColor"
const val FONT_COLOR = "fontColor"
const val FONT_SIZE = "fontSize"
const val FONT_SIZE_DEFAULT = 10

const val LRC_REFRESH = "lrcRefresh"
