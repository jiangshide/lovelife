//package com.android.player.view
//
//import android.content.Context
//import android.util.AttributeSet
//import com.android.player.utils.Config
//import com.android.utils.LogUtil
//import com.pili.pldroid.player.AVOptions
//import com.pili.pldroid.player.widget.PLVideoTextureView
//
///**
// * created by jiangshide on 2020/4/24.
// * email:18311271399@163.com
// */
//class ZdVideoView : PLVideoTextureView {
//  constructor(context: Context?) : super(context) {}
//  constructor(
//    context: Context?,
//    attrs: AttributeSet?
//  ) : super(context, attrs) {
//  }
//
//  constructor(
//    context: Context?,
//    attrs: AttributeSet?,
//    defStyleAttr: Int
//  ) : super(context, attrs, defStyleAttr) {
//  }
//
//  constructor(
//    context: Context?,
//    attrs: AttributeSet?,
//    defStyleAttr: Int,
//    defStyleRes: Int
//  ) : super(context, attrs, defStyleAttr, defStyleRes) {
//  }
//
//  override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
//    super.onWindowFocusChanged(hasWindowFocus)
//    LogUtil.e("--------jsd~hasWindowFocus:",hasWindowFocus)
//  }
//
//  override fun onWindowVisibilityChanged(visibility: Int) {
//    super.onWindowVisibilityChanged(visibility)
//    LogUtil.e("--------jsd~visibility:",visibility)
//  }
//
//  init {
//    setAVOptions(getOption(false))
//  }
//
//  private fun getOption(isLiveStreaming: Boolean): AVOptions {
//    val codec: Int = AVOptions.MEDIA_CODEC_SW_DECODE
//    val options = AVOptions()
//    options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000)
//    options.setInteger(AVOptions.KEY_LIVE_STREAMING, if (isLiveStreaming) 1 else 0)
//    options.setInteger(AVOptions.KEY_MEDIACODEC, codec)
//    val disableLog: Boolean = false
//    options.setInteger(AVOptions.KEY_LOG_LEVEL, if (disableLog) 5 else 0)
//    val cache: Boolean = false
//    if (!isLiveStreaming && cache) {
//      options.setString(AVOptions.KEY_CACHE_DIR, Config.DEFAULT_CACHE_DIR)
//    }
//    if (!isLiveStreaming) {
//      val startPos: Int = 0
//      options.setInteger(AVOptions.KEY_START_POSITION, startPos * 1000)
//    }
//    return options
//  }
//
//}