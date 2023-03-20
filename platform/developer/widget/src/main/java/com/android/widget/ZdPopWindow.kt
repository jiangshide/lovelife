package com.android.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION_CODES
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import android.view.View.OnKeyListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import android.widget.PopupWindow.OnDismissListener
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.android.widget.R.layout
import com.android.widget.adapter.AbstractAdapter.OnItemListener
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.default_item_txt.view.defaultItemTxt
import java.util.ArrayList

/**
 * created by jiangshide on 2019-12-27.
 * email:18311271399@163.com
 */
class ZdPopWindow(private val mContext: Context) : OnDismissListener {
  var width = 0
    private set
  var height = 0
    private set
  private var mIsFocusable = true
  private var mIsOutside = true
  private var mView: View? = null
  var popupWindow: PopupWindow? = null
    private set
  private var mAnimationStyle = -1
  private var mClippEnable = true
  private var mIgnoreCheekPress = false
  private var mInputMode = -1
  private var mOnDismissListener: OnDismissListener? = null
  private var mSoftInputMode = -1
  private var mTouchable = true
  private var mOnTouchListener: OnTouchListener? = null
  private var mWindow: Window? = null
  private var mIsBackgroundDark = false
  private var mBgAlpha = 0f
  private var enableOutsideTouchDisMiss = true

  var recycleView: ZdRecycleView? = null
  var adapter: KAdapter<String>? = null

  open fun showAsDropDown(
    view: View?,
    xOff: Int,
    yOff: Int
  ): ZdPopWindow {
    if (popupWindow != null) {
      popupWindow!!.showAsDropDown(view, xOff, yOff)
    }
    return this
  }

  open fun showAsDropDown(view: View?): ZdPopWindow {
    if (popupWindow != null) {
      popupWindow!!.showAsDropDown(view)
    }
    return this
  }

  @RequiresApi(api = VERSION_CODES.KITKAT) fun showAsDropDown(
    view: View?,
    xOff: Int,
    yOff: Int,
    gravity: Int
  ): ZdPopWindow {
    if (popupWindow != null) {
      popupWindow!!.showAsDropDown(view, xOff, yOff, gravity)
    }
    return this
  }

  fun showAtLocation(
    view: View?,
    gravity: Int,
    x: Int,
    y: Int
  ): ZdPopWindow {
    if (popupWindow != null) {
      popupWindow!!.showAtLocation(view, gravity, x, y)
    }
    return this
  }

  private fun apply(popupWindow: PopupWindow) {
    popupWindow.isClippingEnabled = mClippEnable
    if (mIgnoreCheekPress) {
      popupWindow.setIgnoreCheekPress()
    }
    if (mInputMode != -1) {
      popupWindow.inputMethodMode = mInputMode
    }
    if (mSoftInputMode != -1) {
      popupWindow.softInputMode = mSoftInputMode
    }
    if (mOnDismissListener != null) {
      popupWindow.setOnDismissListener(mOnDismissListener)
    }
    if (mOnTouchListener != null) {
      popupWindow.setTouchInterceptor(mOnTouchListener)
    }
    popupWindow.isTouchable = mTouchable
  }

  fun build(): ZdPopWindow {
    val activity = mView!!.context as Activity
    if (activity != null && mIsBackgroundDark) {
      val alpha =
        if (mBgAlpha > 0 && mBgAlpha < 1) mBgAlpha else 0.7f
      mWindow = activity.window
      val params = mWindow!!.attributes
      params.alpha = alpha
      mWindow!!.addFlags(LayoutParams.FLAG_DIM_BEHIND)
      mWindow!!.attributes = params
    }
    if (width != 0 && height != 0) {
      popupWindow = PopupWindow(mView, width, height)
    } else {
      popupWindow = PopupWindow(
          mView, ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
      )
    }
    if (mAnimationStyle != -1) {
      popupWindow!!.animationStyle = mAnimationStyle
    }
    apply(popupWindow!!)
    if (width == 0 || height == 0) {
      popupWindow!!.contentView
          .measure(
              MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED
          )
      width = -popupWindow!!.contentView.measuredWidth
      height = -popupWindow!!.contentView.measuredHeight
    }
    popupWindow!!.setOnDismissListener(this)
    if (!enableOutsideTouchDisMiss) {
      popupWindow!!.isFocusable = true
      popupWindow!!.isOutsideTouchable = false
      popupWindow!!.setBackgroundDrawable(null)
      popupWindow!!.contentView.isFocusable = true
      popupWindow!!.contentView.isFocusableInTouchMode = true
      popupWindow!!.contentView
          .setOnKeyListener(OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
              popupWindow!!.dismiss()
              return@OnKeyListener true
            }
            false
          })
      popupWindow!!.setTouchInterceptor(object : OnTouchListener {
        override fun onTouch(
          v: View,
          event: MotionEvent
        ): Boolean {
          val x = event.x
              .toInt()
          val y = event.y
              .toInt()
          if (event.action == MotionEvent.ACTION_DOWN
              && (x < 0 || x >= width || y < 0 || y >= height)
          ) {
            return true
          } else if (event.action == MotionEvent.ACTION_OUTSIDE) {
            return true
          }
          return false
        }
      })
    } else {
      popupWindow!!.isFocusable = mIsFocusable
      popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      popupWindow!!.isOutsideTouchable = mIsOutside
    }
    popupWindow!!.update()
    return this
  }

  override fun onDismiss() {
    dissmiss()
  }

  fun dissmiss() {
    if (mOnDismissListener != null) {
      mOnDismissListener!!.onDismiss()
    }
    if (mWindow != null) {
      val params = mWindow!!.attributes
      params.alpha = 1.0f
      mWindow!!.attributes = params
    }
    if (popupWindow != null && popupWindow!!.isShowing) {
      popupWindow!!.dismiss()
    }
  }

  fun size(
    width: Int,
    height: Int
  ): ZdPopWindow {
    this.width = width
    this.height = height
    return this
  }

  fun setFocusable(focusable: Boolean): ZdPopWindow {
    mIsFocusable = focusable
    return this
  }

  fun setData(vararg strs: String): ZdPopWindow {
    val data: MutableList<String> = ArrayList()
    for (str in strs) {
      data.add(str)
    }
    return this.setData(data)
  }

  fun setData(data: MutableList<String>): ZdPopWindow {
    this.setView(layout.default_recycleview)
    recycleView = mView?.findViewById<ZdRecycleView>(R.id.recycleView)
    recycleView?.setBackgroundResource(R.drawable.bg_dialog_radius)
    recycleView?.setPadding(20, 20, 20, 20)
    adapter = recycleView!!.create(data, R.layout.default_item_txt, {
      defaultItemTxt.text = it
    }, {})
    return this
  }

  fun setBgIcon(icon: Int): ZdPopWindow {
    recycleView?.setBackgroundResource(icon)
    return this
  }

  fun setBgColor(color: Int): ZdPopWindow {
    recycleView?.setBackgroundColor(color)
    return this
  }

  fun setLayoutManager(manager: RecyclerView.LayoutManager): ZdPopWindow {
    recycleView?.layoutManager = manager
    return this
  }

  fun setItemDecoration(itemDecoration: ItemDecoration): ZdPopWindow {
    recycleView?.addItemDecoration(itemDecoration)
    return this
  }

  fun setView(layout: Int): ZdPopWindow {
    return this.setView(LayoutInflater.from(mContext).inflate(layout, null))
  }

  fun setView(view: View?): ZdPopWindow {
    mView = view
    return this
  }

  fun setOnItemListener(listener: OnItemListener<String>): ZdPopWindow {
    adapter?.setItemListener(listener)
    return this
  }

  fun setOutsideTouchable(outsideTouchable: Boolean): ZdPopWindow {
    mIsOutside = outsideTouchable
    return this
  }

  fun setAnimationStyle(animationStyle: Int): ZdPopWindow {
    mAnimationStyle = animationStyle
    return this
  }

  fun setClippingEnable(enable: Boolean): ZdPopWindow {
    mClippEnable = enable
    return this
  }

  fun setIgnoreCheekPress(ignoreCheekPress: Boolean): ZdPopWindow {
    mIgnoreCheekPress = ignoreCheekPress
    return this
  }

  fun setInputMethodMode(mode: Int): ZdPopWindow {
    mInputMode = mode
    return this
  }

  fun setOnDissmissListener(
    onDissmissListener: OnDismissListener?
  ): ZdPopWindow {
    mOnDismissListener = onDissmissListener
    return this
  }

  fun setSoftInputMode(softInputMode: Int): ZdPopWindow {
    mSoftInputMode = softInputMode
    return this
  }

  fun setTouchable(touchable: Boolean): ZdPopWindow {
    mTouchable = touchable
    return this
  }

  fun setTouchIntercepter(touchIntercepter: OnTouchListener?): ZdPopWindow {
    mOnTouchListener = touchIntercepter
    return this
  }

  fun enableBackgroundDark(isDark: Boolean): ZdPopWindow {
    mIsBackgroundDark = isDark
    return this
  }

  fun setBgAlpha(darkValue: Float): ZdPopWindow {
    mBgAlpha = darkValue
    return this
  }

  fun enableOutsideTouchableDissmiss(disMiss: Boolean): ZdPopWindow {
    enableOutsideTouchDisMiss = disMiss
    return this
  }

  fun createView(): ZdRecycleView {
    return recycleView!!
  }
}