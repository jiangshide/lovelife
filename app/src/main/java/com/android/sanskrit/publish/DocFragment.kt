package com.android.sanskrit.publish

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.event.ZdEvent
import com.android.files.Files
import com.android.files.Files.OnFileListener
import com.android.img.Img
import com.android.player.dplay.player.VideoViewManager
import com.android.resource.Resource
import com.android.resource.vm.blog.data.Style
import com.android.sanskrit.FLOW_MENUS
import com.android.sanskrit.PUBLISH_UPLOAD
import com.android.sanskrit.R
import com.android.sanskrit.R.drawable
import com.android.sanskrit.R.layout
import com.android.sanskrit.publish.fragment.PublishBaseFragment
import com.android.sanskrit.user.audio.FONT_SIZE_DEFAULT
import com.android.utils.ImgUtil
import com.android.utils.LogUtil
import com.android.utils.data.DOC
import com.android.utils.data.FileData
import com.android.utils.data.IMG
import com.android.widget.ZdButton
import com.android.widget.ZdDialog
import com.android.widget.pickercolor.AlphaTileView
import com.android.widget.pickercolor.ColorEnvelope
import com.android.widget.pickercolor.ColorPickerView
import com.android.widget.pickercolor.flag.FlagMode
import com.android.widget.pickercolor.listeners.ColorEnvelopeListener
import com.android.widget.pickercolor.sliders.AlphaSlideBar
import com.android.widget.pickercolor.sliders.BrightnessSlideBar
import kotlinx.android.synthetic.main.publish_doc_fragment.*

/**
 * created by jiangshide on 2020/4/23.
 * email:18311271399@163.com
 */
class DocFragment : PublishBaseFragment() {

  private var data: FileData? = null

  private var isBg = false
  private var isImg = false

  private var pickerPositionLeft: ZdButton? = null
  private var pickerPositionLeftTop: ZdButton? = null
  private var pickerPositionTopCenter: ZdButton? = null
  private var pickerPositionRightTop: ZdButton? = null
  private var pickerPositionRightCenter: ZdButton? = null
  private var pickerPositionRightBottom: ZdButton? = null
  private var pickerPositionBottomCenter: ZdButton? = null
  private var pickerPositionLeftBottom: ZdButton? = null
  private var pickerPositionCenter: ZdButton? = null

  private val style = Style()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.publish_doc_fragment)
  }

  private fun setPosition(
    left: Int,
    leftTop: Int,
    topCenter: Int,
    rightTop: Int,
    rightCenter: Int,
    rightBottom: Int,
    bottomCenter: Int,
    leftBottom: Int,
    center: Int,
    position: Int
  ) {
    pickerPositionLeft?.normalColor = left
    pickerPositionLeftTop?.normalColor = leftTop
    pickerPositionTopCenter?.normalColor = topCenter
    pickerPositionRightTop?.normalColor = rightTop
    pickerPositionRightCenter?.normalColor = rightCenter
    pickerPositionRightBottom?.normalColor = rightBottom
    pickerPositionBottomCenter?.normalColor = bottomCenter
    pickerPositionLeftBottom?.normalColor = leftBottom
    pickerPositionCenter?.normalColor = center
    publishDocEdit?.gravity = position
    style.position = position
    LogUtil.e("---------style:", style)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    setLeft("取消").setLeftListener {
      pop()
    }
        .setRight("发布")
        .setRightEnable(false)
        .setRightListener {
          if (publish == null) return@setRightListener
          if (data != null) {
            val list = ArrayList<FileData>()
            list.add(data!!)
            publish.files = list
          }
          publish.styleJson = style.toGson()
          Resource.publish = publish
          ZdEvent.get()
              .with(PUBLISH_UPLOAD)
              .post(publish)
          pop()
          ZdEvent.get()
              .with(FLOW_MENUS)
              .post(FLOW_MENUS)
        }

    publish.format = DOC
    publishDocEdit.setListener { s, input ->
      publish.content = input
    }
    publishDocScroll.setOnClickListener {
      publishDocScroll.isSelected = !publishDocScroll.isSelected
      if(publishDocScroll.isSelected){
        style.scroll = 1
      }else{
        style.scroll = 0
      }
    }
    publishDocPositionOrSize.setOnClickListener {
      ZdDialog.createView(context, R.layout.dialog_picker_position) {
        val pickerPositionTxt = it.findViewById<TextView>(R.id.pickerPositionTxt)
        pickerPositionLeft = it.findViewById<ZdButton>(R.id.pickerPositionLeft)
        pickerPositionLeftTop = it.findViewById<ZdButton>(R.id.pickerPositionLeftTop)
        pickerPositionTopCenter = it.findViewById<ZdButton>(R.id.pickerPositionTopCenter)
        pickerPositionRightTop = it.findViewById<ZdButton>(R.id.pickerPositionRightTop)
        pickerPositionRightCenter = it.findViewById<ZdButton>(R.id.pickerPositionRightCenter)
        pickerPositionRightBottom = it.findViewById<ZdButton>(R.id.pickerPositionRightBottom)
        pickerPositionBottomCenter = it.findViewById<ZdButton>(R.id.pickerPositionBottomCenter)
        pickerPositionLeftBottom = it.findViewById<ZdButton>(R.id.pickerPositionLeftBottom)
        pickerPositionCenter = it.findViewById<ZdButton>(R.id.pickerPositionCenter)

        pickerPositionLeft?.setOnClickListener {
          setPosition(
              R.color.blueLight, R.color.gray, R.color.gray, R.color.gray, R.color.gray,
              R.color.gray, R.color.gray, R.color.gray, R.color.gray,
              Gravity.LEFT or Gravity.CENTER_VERTICAL
          )
        }
        pickerPositionLeftTop?.setOnClickListener {
          setPosition(
              R.color.gray, R.color.blueLight, R.color.gray, R.color.gray, R.color.gray,
              R.color.gray, R.color.gray, R.color.gray, R.color.gray, Gravity.LEFT or Gravity.TOP
          )
        }
        pickerPositionTopCenter?.setOnClickListener {
          setPosition(
              R.color.gray, R.color.gray, R.color.blueLight, R.color.gray, R.color.gray,
              R.color.gray, R.color.gray, R.color.gray, R.color.gray, Gravity.TOP or Gravity.CENTER
          )
        }
        pickerPositionRightTop?.setOnClickListener {
          setPosition(
              R.color.gray, R.color.gray, R.color.gray, R.color.blueLight, R.color.gray,
              R.color.gray, R.color.gray, R.color.gray, R.color.gray, Gravity.RIGHT or Gravity.TOP
          )
        }
        pickerPositionRightCenter?.setOnClickListener {
          setPosition(
              R.color.gray, R.color.gray, R.color.gray, R.color.gray, R.color.blueLight,
              R.color.gray, R.color.gray, R.color.gray, R.color.gray,
              Gravity.RIGHT or Gravity.CENTER
          )
        }
        pickerPositionRightBottom?.setOnClickListener {
          setPosition(
              R.color.gray, R.color.gray, R.color.gray, R.color.gray, R.color.gray,
              R.color.blueLight, R.color.gray, R.color.gray, R.color.gray,
              Gravity.RIGHT or Gravity.BOTTOM
          )
        }
        pickerPositionBottomCenter?.setOnClickListener {
          setPosition(
              R.color.gray, R.color.gray, R.color.gray, R.color.gray, R.color.gray,
              R.color.gray, R.color.blueLight, R.color.gray, R.color.gray,
              Gravity.BOTTOM or Gravity.CENTER
          )
        }
        pickerPositionLeftBottom?.setOnClickListener {
          setPosition(
              R.color.gray, R.color.gray, R.color.gray, R.color.gray, R.color.gray,
              R.color.gray, R.color.gray, R.color.blueLight, R.color.gray,
              Gravity.LEFT or Gravity.BOTTOM
          )
        }
        pickerPositionCenter?.setOnClickListener {
          setPosition(
              R.color.gray, R.color.gray, R.color.gray, R.color.gray, R.color.gray,
              R.color.gray, R.color.gray, R.color.gray, R.color.blueLight, Gravity.CENTER
          )
        }

        val pickerFontSize = it.findViewById<TextView>(R.id.pickerFontSize)
        val fontReduce = it.findViewById<ZdButton>(R.id.fontReduce)
        val fontSizeSeekBar = it.findViewById<SeekBar>(R.id.fontSizeSeekBar)
        val fontAdd = it.findViewById<ZdButton>(R.id.fontAdd)
        fontSizeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
          override fun onProgressChanged(
            seekBar: SeekBar?,
            progress: Int,
            fromUser: Boolean
          ) {
            val size = (FONT_SIZE_DEFAULT + progress).toFloat()
            publishDocEdit?.textSize = size
            pickerFontSize?.text = "字体大小: $size"
            style.size = size.toInt()
          }

          override fun onStartTrackingTouch(seekBar: SeekBar?) {
          }

          override fun onStopTrackingTouch(seekBar: SeekBar?) {
          }

        })
        fontReduce.setOnClickListener {
          fontSizeSeekBar.progress -= 5
          if (fontSizeSeekBar.progress <= 0) {
            fontSizeSeekBar.progress = 0
          }
        }
        fontAdd.setOnClickListener {
          fontSizeSeekBar.progress += 5
          if (fontSizeSeekBar.progress >= 25) {
            fontSizeSeekBar.progress = 25
          }
        }
        it.findViewById<ZdButton>(R.id.pickerPositionSure)
            .setOnClickListener {
              ZdDialog.cancelDialog()
            }
      }
          .setGravity(Gravity.BOTTOM)
          .show()
    }

    publishDocColorAdd.setOnClickListener {
      ZdDialog.createView(context, R.layout.dialog_picker_color) {
        val colorPickerView = it.findViewById<ColorPickerView>(R.id.colorPickerView)
        val alphaSlideBar = it.findViewById<AlphaSlideBar>(R.id.alphaSlideBar)
        val brightnessSlide = it.findViewById<BrightnessSlideBar>(R.id.brightnessSlide)
        val colorTxt = it.findViewById<TextView>(R.id.colorTxt)
        val alphaTileView = it.findViewById<AlphaTileView>(R.id.alphaTileView)
        val bubbleFlag = BubbleFlag(context, layout.dialog_picker_color_bubble)
        val pickImg = it.findViewById<ImageView>(R.id.pickImg)
        it.findViewById<ZdButton>(R.id.pickerColorSure)
            .setOnClickListener {
              ZdDialog.cancelDialog()
            }
        val colorPickerType: RadioGroup = it.findViewById<RadioGroup>(R.id.colorPickerType)
        colorPickerType.setOnCheckedChangeListener { radioGroup, checkedId ->
          when (checkedId) {
            R.id.colorPickerTypeTxt -> {
              isBg = false
            }
            R.id.colorPickerTypeBg -> {
              isBg = true
            }
          }
        }
        if (isBg) {
          colorPickerType.check(R.id.colorPickerTypeBg)
        } else {
          colorPickerType.check(R.id.colorPickerTypeTxt)
        }
        if (data != null && !TextUtils.isEmpty(data?.path)) {
          pickImg.visibility = View.VISIBLE
          pickImg.isSelected = isImg
          if (isImg) {
            colorPickerView.setPaletteDrawable(ImgUtil.path2Drawable(data?.path))
          } else {
            colorPickerView.setPaletteDrawable(
                ContextCompat.getDrawable(context!!, drawable.palette)!!
            )
          }
        } else {
          pickImg.visibility = View.GONE
        }
        pickImg.setOnClickListener {
          isImg = !isImg
          pickImg.isSelected = isImg
          if (isImg) {
            colorPickerView.setPaletteDrawable(ImgUtil.path2Drawable(data?.path))
          } else {
            colorPickerView.setPaletteDrawable(
                ContextCompat.getDrawable(context!!, drawable.palette)!!
            )
          }
        }
        bubbleFlag.flagMode = FlagMode.FADE
        colorPickerView.flagView = bubbleFlag
        colorPickerView.attachAlphaSlider(alphaSlideBar)
        colorPickerView.attachBrightnessSlider(brightnessSlide)
        colorPickerView.setLifecycleOwner(this)
        colorPickerView.setColorListener(object : ColorEnvelopeListener {
          override fun onColorSelected(
            envelope: ColorEnvelope?,
            fromUser: Boolean
          ) {
            val color = "#${envelope?.hexCode}"
            colorTxt?.text = color
            colorTxt?.setHintTextColor(envelope?.color!!)
            alphaTileView?.setPaintColor(envelope?.color!!)
            if (isBg) {
              style.bgColor = color
              publishDocCoverImg.setBackgroundColor(Color.parseColor(color))
            } else {
              style.txtColor = color
              publishDocEdit?.setTextColor(Color.parseColor(color))
              publishDocEdit?.setHintTextColor(Color.parseColor(color))
            }
          }
        })
      }
          .setGravity(Gravity.BOTTOM)
          .show()
    }
    publishDocCoverAdd.setOnClickListener {
      openImg()
    }

    publishDocName.setListener { s, input ->
      publish.title = input
      validate()
    }

    publishDocDes.setListener { s, input ->
      publish.des = input
      validate()
    }
  }

  private fun openImg() {
    Files().setType(IMG)
        .setMax(1)
        .setSingleListener(object : OnFileListener {
          override fun onFile(fileData: FileData) {
            if (fileData == null) return
            data = fileData
            Img.loadImage(fileData.path, publishDocCoverImg)
          }
        })
        .open(this)
  }

  override fun validate() {
    val disable =
      TextUtils.isEmpty(publish.title) || TextUtils.isEmpty(
          publish.content
      )
    setRightEnable(!disable)
//    val disable =
//      TextUtils.isEmpty(publish.title)
//    setRightEnable(!disable)
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