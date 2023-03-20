package com.android.sanskrit.publish

import android.media.audiofx.Visualizer
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.event.ZdEvent
import com.android.files.Files
import com.android.files.Files.OnFileListener
import com.android.http.oss.OssClient
import com.android.http.oss.OssClient.HttpFileListener
import com.android.img.Img
import com.android.player.audio.AudioRecorder
import com.android.player.audio.AudioRecorder.OnAudioStatusUpdateListener
import com.android.player.dplay.player.VideoViewManager
import com.android.resource.Resource
import com.android.resource.audio.AudioPlay
import com.android.resource.data.DeviceData
import com.android.resource.data.PositionData
import com.android.resource.view.MusicPlayerView
import com.android.sanskrit.FLOW_MENUS
import com.android.sanskrit.PUBLISH_UPLOAD
import com.android.sanskrit.R
import com.android.sanskrit.publish.fragment.PublishBaseFragment
import com.android.utils.CountDown
import com.android.utils.CountDown.OnCountDownListener
import com.android.utils.DateUtil
import com.android.utils.FileUtil
import com.android.utils.LogUtil
import com.android.utils.data.AUDIO
import com.android.utils.data.DOC
import com.android.utils.data.FileData
import com.android.utils.data.IMG
import com.android.widget.ZdButton
import com.android.widget.ZdDialog
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.publish_record_fragment.*

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class RecordFragment(private var data: FileData? = null) : PublishBaseFragment(),
    OnAudioStatusUpdateListener,
    OnFileListener,
    Visualizer.OnDataCaptureListener,
    MusicPlayerView.Callback,
    OnCountDownListener,
    HttpFileListener {

  private val recorder: AudioRecorder by lazy { AudioRecorder() }

//  private var duration: Int = 0

  private var waveArray: MutableList<Int>? = null

  //  private var audioPath: String = ""
//  private var cover: String = ""
  private var isPlayerPaused: Boolean = false

  private val countDown = CountDown()

  private var positionData: PositionData? = null
  private var deviceData: DeviceData? = null
  private var lrc: String = ""

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.publish_record_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    positionData = PositionData()
    deviceData = DeviceData(mActivity)
    setLeft("取消").setLeftListener {
      pop()
    }
        .setRight("发布")
        .setRightEnable(false)
        .setRightListener {
          publish.netInfo = positionData?.gson
          publish.device = deviceData?.gson
          val list = ArrayList<FileData>()
          list.add(data!!)
          publish.files = list
          if (!TextUtils.isEmpty(publish.files!![0].cover)) {
            showLoading()
            OssClient.instance.setFileData(publish.files!![0].cover!!)
                .setListener(this)
                .start()
          } else {
            request()
          }
        }
    LogUtil.e("data:", data)
    publish.format = AUDIO
    init()
    initRecord()
  }

  private fun init() {
    if (data != null) {
      publishAudioViewF.visibility = View.VISIBLE
      publishAudioLrcZh.visibility = View.VISIBLE
      publishAudioLrcEs.visibility = View.VISIBLE
      publishAudioRecordL.visibility = View.GONE
      publishRecordView.visibility = View.GONE

      publishAudioLrcZh.setOnClickListener {
//        openLrc()
      }

      publishAudioLrcEs.setOnClickListener {

      }

      publishAudioRetry.setOnClickListener {
        release()
        openAudio()
      }
      publishAudioView.setOnClickListener {
        AudioPlay.getInstance()
            .start()
      }
      publishAudioPlay.setOnClickListener {
        initAudio()
        publishAudioProgress.max = data?.duration!!.toInt()
        publishAudioView.visibility = View.VISIBLE
        AudioPlay.getInstance()
            .setPlayStateListener {
              if (publishAudioPlay == null) return@setPlayStateListener
              if (it == AudioPlay.PlayState.STATE_PLAYING) {
                publishAudioPlay.visibility = View.GONE
                publishAudioView.visibility = View.VISIBLE
                countDown.start()
              } else if (it == AudioPlay.PlayState.STATE_PAUSE) {
                publishAudioPlay.visibility = View.VISIBLE
                publishAudioView.visibility = View.GONE
                countDown.pause()
              } else if (it == AudioPlay.PlayState.STATE_IDLE) {
                publishAudioPlay.visibility = View.VISIBLE
                publishAudioProgress.progress = 0
                publishAudioView.visibility = View.GONE
                countDown.stop()
              }
            }
            .setDataCaptureListener(this)
            .play(data?.path)
      }
    } else {
      data = FileData()
      publishRecordView.visibility = View.VISIBLE
      publishAudioRecordL.visibility = View.VISIBLE
      publishAudioViewF.visibility = View.GONE
      publishAudioLrcZh.visibility = View.GONE
      publishAudioLrcEs.visibility = View.GONE

    }
    publishAudioCoverAdd.setOnClickListener {
      openImg()
    }
    publishAudioName.setListener { s, input ->
      publish.title = input
      validate()
    }
    publishAudioDes.setListener { s, input ->
      publish.des = input
      validate()
    }
    Img.loadImageRound(data?.cover, publishAudioCover, 15, R.drawable.audio)
  }

  private fun initRecord() {
    publishAudioRecordReset.setOnClickListener {
      data = null
      data = FileData()
      publishRecordView.updateWave(listOf(), 0)
      recorder.cancelRecord()
      record()
      validate()
    }
    publishAudioRecord.setOnClickListener {
      AudioPlay.getInstance()
          .stop()

      if (recorder.isRecordPauseing) {
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
          recorder.resumeRecord()
          recording(false)
        }
        return@setOnClickListener
      }
      publishRecordView.onPause()
      if (recorder.isAudioRecording) {
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
          recorder.pauseRecord()
          recording(true)
        }
        return@setOnClickListener
      }
      waveArray = mutableListOf()
      try {
        recorder.start(publish.title)
      } catch (e: Exception) {
        LogUtil.e(e)
        ZdDialog.create(context!!)
            .setContentOnly(R.string.no_audio)
            .setOnlySure()
            .show()
        return@setOnClickListener
      }
      recording(false)
    }
    publishAudioRecordFinish.setOnClickListener {
      recorded()
      recorder.stopRecord()
      validate()
    }
    publishRecordView.callback = this
    recorder.setOnAudioStatusUpdateListener(this)
    record()
    initAudio()
  }

  private fun record() {
    publishAudioRecordReset.isSelected = false
    publishAudioRecordReset.isEnabled = false
    publishAudioRecordReset.setTextColor(getColor(R.color.txtGray))
    publishAudioRecord.setImageResource(R.mipmap.record)
    publishAudioRecord.isEnabled = true
    publishAudioRecordFinish.isSelected = false
    publishAudioRecordFinish.isEnabled = false
    publishAudioRecordFinish.setTextColor(getColor(R.color.txtGray))
    publishRecordView.enableClick(false)
  }

  private fun recording(isPause: Boolean) {
    publishAudioRecordReset.isSelected = false
    publishAudioRecordReset.isEnabled = false
    publishAudioRecordReset.setTextColor(getColor(R.color.txtGray))
    if (isPause) {
      publishAudioRecord.setImageResource(R.mipmap.record_pause)
    } else {
      publishAudioRecord.setImageResource(R.mipmap.recording)
    }
    publishAudioRecord.isEnabled = true
    publishAudioRecordFinish.isSelected = true
    publishAudioRecordFinish.isEnabled = true
    publishAudioRecordFinish.setTextColor(getColor(R.color.blue))
    publishRecordView.enableClick(false)
  }

  private fun recorded() {
    publishAudioRecordReset.isSelected = true
    publishAudioRecordReset.isEnabled = true
    publishAudioRecordReset.setTextColor(getColor(R.color.blue))
    publishAudioRecord.setImageResource(R.mipmap.recorded)
    publishAudioRecord.isEnabled = false
    publishAudioRecordFinish.isSelected = false
    publishAudioRecordFinish.isEnabled = false
    publishAudioRecordFinish.setTextColor(getColor(R.color.txtGray))
    publishRecordView.enableClick(true)
  }

  override fun onRecording(
    db: Double,
    wave: Int,
    time: Long
  ) {
    if (publishRecordView == null) return
    waveArray?.add(wave)
    publishRecordView.updateWave(waveArray!!, time)
    publishRecordView.enableClick(false)
  }

  override fun onRecordStop(
    duration: Long,
    filePath: String
  ) {
    if (publishRecordView == null) return
    data!!.duration = duration
    data!!.path = filePath
    data!!.format = AUDIO
    data!!.dir = "audio"
    data!!.size = FileUtil.getFileLength(filePath)
    data!!.setWave(waveArray!!)
    recorded()
  }

  override fun onDestroy() {
    super.onDestroy()
    recorder.stopRecord()
    release()
  }

  private fun release() {
    AudioPlay.getInstance()
        .release()
    countDown.stop()
  }

  private fun openLrc(
    zdButton: ZdButton,
    mode: Int
  ) {
    Files().setType(DOC)
        .setSelectedFile(data!!)
        .setMax(1)
        .setSingleListener(object : OnFileListener {
          override fun onFile(fileData: FileData) {
            OssClient.instance
                .setListener(object : HttpFileListener {
                  override fun onProgress(
                    currentSize: Long,
                    totalSize: Long,
                    progress: Int
                  ) {

                  }

                  override fun onSuccess(
                    position: Int,
                    url: String
                  ) {
                    lrc = url
                    if (zdButton == null) return
                    zdButton.text = "重新上传:$${FileUtil.getFileName(lrc)}"
                    LogUtil.e("lrc:", lrc)
                  }

                  override fun onFailure(
                    clientExcepion: Exception,
                    serviceException: Exception
                  ) {
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

  private fun openAudio() {
    Files().setType(AUDIO)
        .setSelectedFile(data!!)
        .setMax(1)
        .setSingleListener(this)
        .open(this)
  }

  private fun openImg() {
    Files().setType(IMG)
        .setMax(1)
        .setSingleListener(object : OnFileListener {
          override fun onFile(fileData: FileData) {
            data?.cover = fileData.path
            initAudio()
          }
        })
        .open(this)
  }

  override fun onFile(fileData: FileData) {
    data = fileData
    publishAudioPlay.visibility = View.VISIBLE
    publishAudioView.visibility = View.GONE
    initAudio()
  }

  private fun initAudio() {
    Img.loadImageRound(data?.cover, publishAudioCover, 15, R.drawable.audio)
    publishAudioTitle.text = data?.name
    countDown.setTime(data?.duration!!)
        .setListener(this)
    publishAudioTime.text = DateUtil.formatSeconds(data!!.duration / 1000)
  }

  override fun onFftDataCapture(
    visualizer: Visualizer?,
    fft: ByteArray?,
    samplingRate: Int
  ) {

  }

  override fun onWaveFormDataCapture(
    visualizer: Visualizer?,
    waveform: ByteArray?,
    samplingRate: Int
  ) {
    if (publishAudioView == null || waveform == null) return
    publishAudioView?.post { publishAudioView?.setWaveData(waveform) }
  }

  override fun onFinish() {
    if (publishAudioProgress == null) return
    publishAudioTime.text = DateUtil.formatSeconds(data!!.duration / 1000)
  }

  override fun onPause(duration: Long) {
  }

  override fun onTick(millisUntilFinished: Long) {
    if (publishAudioTime == null || publishAudioProgress == null) return
    publishAudioTime.text =
      "${DateUtil.formatSeconds(data!!.duration / 1000)}/${DateUtil.formatSeconds(
          millisUntilFinished / 1000
      )}"
    publishAudioProgress.progress = AudioPlay.getInstance()
        .currentPosition()
  }

  private fun request() {
    if (publish == null) return
    release()
    Resource.publish = publish
    ZdEvent.get()
        .with(PUBLISH_UPLOAD)
        .post(publish)
    pop()
    ZdEvent.get()
        .with(FLOW_MENUS)
        .post(FLOW_MENUS)
  }

  override fun onProgress(
    currentSize: Long,
    totalSize: Long,
    progress: Int
  ) {
  }

  override fun onSuccess(
    position: Int,
    url: String
  ) {
    if (publish != null || publish.files != null) {
      hiddle()
      publish.files!![0].cover = url
    }
    request()
  }

  override fun onFailure(
    clientExcepion: Exception,
    serviceException: Exception
  ) {
    LogUtil.e("serviceException:", serviceException)
  }

  override fun onPlayClick() {
    if (waveArray == null) return
    if (isPlayerPaused) {
      AudioPlay.getInstance()
          .start()
      publishRecordView.onStart()
    } else {
      AudioPlay.getInstance()
          .setPlayStateListener {
            when (it) {
              AudioPlay.PlayState.STATE_PLAYING -> {
                publishRecordView?.updateWave(
                    waveArray!!, AudioPlay.getInstance()!!
                    .duration()
                    .toLong()
                )
                publishRecordView?.onStart()
              }
              AudioPlay.PlayState.STATE_IDLE -> {
                publishRecordView?.onPause()
                isPlayerPaused = false
              }
            }
          }
          .play(data!!.path)
    }
    isPlayerPaused = false
  }

  override fun onPauseClick() {
    if (waveArray == null) return
    isPlayerPaused = true
    AudioPlay.getInstance()
        .pause()
    publishRecordView.onPause()
  }

  override fun getProgress(): Int {
    return AudioPlay.getInstance()
        .currentPosition()
  }

  override fun validate() {
//    val disable =
//      TextUtils.isEmpty(publish.name) || publish.channelId == 0L
    val disable =
      TextUtils.isEmpty(publish.title) || TextUtils.isEmpty(data?.path)
    setRightEnable(!disable)
  }

  override fun onResume() {
    super.onResume()
    showFloatMenu(false)
    VideoViewManager.instance()
      .pause()
  }

  override fun onPause() {
    super.onPause()
    showFloatMenu(true)
    if (Resource.TAB_INDEX == 0) {
      VideoViewManager.instance()
        .resume()
    }
  }

}