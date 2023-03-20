package com.android.sanskrit.blog.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.android.img.Img
import com.android.resource.MyFragment
import com.android.resource.vm.blog.BlogVM
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.utils.LogUtil
import com.android.utils.ScreenUtil
import com.android.widget.ZdAutoScroll
import com.android.widget.ZdRecycleView

/**
 * created by jiangshide on 2020/3/31.
 * email:18311271399@163.com
 */
class TypeTextHolder(
  fragment: MyFragment,
  itemView: View,
  blogVM: BlogVM,
  lifecycleOwner: LifecycleOwner,
  adapter: BlogAdapter,
  zdRecycleView: ZdRecycleView,
  from: Int = FROM_FIND,
  private val uid: Long = -1
) : TypeAbstractViewHolder(
    fragment, itemView, blogVM, lifecycleOwner, adapter, zdRecycleView, from, uid
) {

  override fun bindHolder(
    index: Int,
    blog: Blog
  ) {
    super.bindHolder(index, blog)

    val blogDocF = itemView?.findViewById<FrameLayout>(R.id.blogDocF)
    val layoutParams = blogDocF.layoutParams
    layoutParams.width = ScreenUtil.getRtScreenWidth(fragment?.activity)
    if(blog?.content?.length!! > 300){
      layoutParams.height = ScreenUtil.getRtScreenHeight(fragment?.activity)/4*3
    }else{
      layoutParams.height = ScreenUtil.getRtScreenHeight(fragment?.activity)/2
    }
    val zdAutoScroll = itemView.findViewById<ZdAutoScroll>(R.id.zdAutoScroll)
    val blogDocBg = itemView.findViewById<ImageView>(R.id.blogDocBg)
    val blogDocTxt = itemView.findViewById<TextView>(R.id.blogDocTxt)
    val zdScrollCheckBox = itemView.findViewById<CheckBox>(R.id.zdScrollCheckBox)
    blogDocTxt.text = blog?.content

    if (!TextUtils.isEmpty(blog.cover)) {
      Img.loadImage(blog.cover, blogDocBg)
    } else if (blog.style != null && !TextUtils.isEmpty(blog?.style?.bgColor)) {
      blogDocBg.setBackgroundColor(Color.parseColor(blog?.style?.bgColor))
    }
    if (!TextUtils.isEmpty(blog?.style?.txtColor)) {
      val color = blog?.style?.txtColor
      blogDocTxt?.setTextColor(Color.parseColor(color))
    }
    if (blog?.style?.size!! > 9) {
      blogDocTxt?.textSize = blog?.style?.size!!.toFloat()
    }
    if (blog?.style?.position!! > 0) {
      blogDocTxt?.gravity = blog?.style?.position!!
    }
    val isScroll = blog?.style?.scroll == 1
    zdScrollCheckBox.isChecked = isScroll
    zdAutoScroll?.setAutoToScroll(isScroll)
    zdAutoScroll?.setFistTimeScroll(2000)
    zdAutoScroll?.setScrollRate(50)
    zdAutoScroll?.setScrollLoop(false)

    zdScrollCheckBox.setOnCheckedChangeListener { compoundButton, b ->
      zdAutoScroll?.setAutoToScroll(b)
      if(b){
        zdAutoScroll?.setFistTimeScroll(2000)
        zdAutoScroll?.setScrollRate(50)
        zdAutoScroll?.setScrollLoop(false)
      }
    }

//    val width = ScreenUtil.getRtScreenWidth(fragment?.mActivity)
//    val height = ScreenUtil.getRtScreenHeight(fragment?.mActivity)
//    val rect = Rect(0, 0, width, height)
//    val location = IntArray(2)
//    itemView.getLocationInWindow(location)
//
//    LogUtil.e("-----------isShow:", itemView.getLocalVisibleRect(rect), " | index:", index)
  }
}