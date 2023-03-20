package com.android.resource.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.android.resource.R
import com.android.resource.data.Music
import com.android.utils.EncryptUtils

/**
 * created by jiangshide on 2019-10-25.
 * email:18311271399@163.com
 */
class MusicWaveView(context: Context, attrs: AttributeSet?) : View(context, attrs){
  var waveArray: List<Int>? = null

  private var columnMaxHeight: Int
  private var columnMinHeight: Int
  private var columnWidth: Int
  private var columnGap: Int
  private var columnWidthWithGap: Int

  private var colorUp: Int
  private var colorUpMask: Int
  private var colorDown: Int
  private var colorDownMask: Int

  private var rect: Rect = Rect()

  private var paint: Paint

  private var halfHeight: Int = 0
  var waveStart: Int = 0
  var waveOffset: Int = 0

  companion object {
    val GOLD_SPLIT = 0.618f
  }

  init {
    columnMaxHeight = context.resources.getDimensionPixelSize(R.dimen.music_wave_column_height)
    columnMinHeight = context.resources.getDimensionPixelSize(R.dimen.music_wave_column_min_height)
    columnWidth = context.resources.getDimensionPixelSize(R.dimen.music_wave_column_width)
    columnGap = context.resources.getDimensionPixelSize(R.dimen.music_wave_column_gap)
    columnWidthWithGap = columnWidth + columnGap

    colorUp = ContextCompat.getColor(context, R.color.blueLight)
    colorUpMask = ContextCompat.getColor(context, R.color.blueLight)
    colorDown = ContextCompat.getColor(context, R.color.blue)
    colorDownMask = ContextCompat.getColor(context, R.color.bg)
    paint = Paint()
//        updateMusic(music)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    halfHeight = measuredHeight / 2
  }

  fun updateMusic(music: Music) {
    if (music.waveArray == null) {
      val byteArray = EncryptUtils.base64Decode(music.audioWave)
      music.waveArray = List(byteArray.size) { index ->
        val n = byteArray[index].toInt()
        if (n < 0) {
          return@List n + 256
        }
        return@List n
      }
//            music.waveArray = transformWave(byteArray, 2)
    }
    waveArray = music.waveArray
  }

  fun updateWaveArray(waveArray: List<Int>) {
    this.waveArray = waveArray
  }

  fun moveTo(percent: Float) {
    if (waveArray == null) return
    var tempPercent: Float = percent
    if (percent > 1) {
      tempPercent = 1f
    }
    waveOffset = (waveArray?.size!! * tempPercent * columnWidthWithGap).toInt()
    invalidate()
  }

//    /**
//     * :arg rate:倍率
//     */
//    private fun transformWave(byteArray: ByteArray?, rate: Int): IntArray? {
//        if (byteArray == null) return null
//
//        val newIntArray: IntArray = IntArray(byteArray.size / 2)
//        for (i in 0..byteArray.size - 1 step 2) {
//            if (i >= byteArray.size - 1) break
//            val wave = abs((byteArray[i] + byteArray[i + 1]) / 2)
//            newIntArray[i / 2] = wave
//        }
//        return newIntArray
//    }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    if (waveArray == null) return

    for (i in 0 until waveArray!!.size) {

      var left = i * columnWidthWithGap + waveStart - waveOffset
      var right = left + columnWidth

      if (left > measuredWidth) {
        return
      }
      if (left < 0) {
        continue
      }

      var upColumnHeight = columnMinHeight
      if (waveArray!![i] > 0) {
        upColumnHeight = (waveArray!![i] / 256f * columnMaxHeight).toInt()
      }
      var downColumnHeight = (upColumnHeight * GOLD_SPLIT).toInt()
      if (left < waveStart) {
        paint.color = colorUp
        paint.alpha = 255
      } else {
        paint.color = colorUpMask
        paint.alpha = 128
      }
      rect.set(left, halfHeight - upColumnHeight, right, halfHeight)
      canvas?.drawRect(rect, paint)
      if (left < waveStart) {
        paint.color = colorDown
        paint.alpha = 255
      } else {
        paint.color = colorDownMask
        paint.alpha = 128
      }
      rect.set(left, halfHeight, right, halfHeight + downColumnHeight)
      canvas?.drawRect(rect, paint)

    }
  }
}