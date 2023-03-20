package com.android.sanskrit.channel.fragment.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuffColorFilter
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.resource.view.tag.TagsAdapter
import com.android.sanskrit.R

/**
 * created by jiangshide on 2020/4/3.
 * email:18311271399@163.com
 */
abstract class TagAdapter<T>(
  context: Context,
  layoutId: Int
) : TagsAdapter() {

  private var mContext: Context? = null
  private var mLayoutId = 0
  private val mData: ArrayList<T> = ArrayList()

  init {
    this.mContext = context
    this.mLayoutId = layoutId
  }

  fun add(data: List<T>) {
    mData.addAll(data)
  }

  override fun getPopularity(position: Int): Int {
    return position % 5
  }

  override fun onThemeColorChanged(
    view: View?,
    themeColor: Int,
    alpha: Float
  ) {
//    view?.setBackgroundColor(themeColor);
//    val porterDuffColorFilter = PorterDuffColorFilter(
//        themeColor,
//        SRC_ATOP
//    )
    onThemeColorChanged(view,themeColor,alpha,null)
  }

  override fun getView(
    context: Context?,
    position: Int,
    parent: ViewGroup?
  ): View {
    var convertView = LayoutInflater.from(mContext)
        .inflate(mLayoutId, null)
    val t = getItem(position)
    convertView?.let { convertView(position, it, t) }
    return convertView
  }

  override fun getItem(position: Int): T {
    return mData[position]
  }

  override fun getCount(): Int {
    return mData.size
  }

  protected abstract fun convertView(
    position: Int,
    item: View?,
    t: T?
  )

  protected fun onThemeColorChanged(
    view: View?,
    themeColor: Int,
    alpha: Float,
    t: T?
  ){
    //图片处理1
//    val imageView =
//          view!!.findViewById(R.id.searchTagItemIcon) as ImageView
//        val porterDuffColorFilter =
//          PorterDuffColorFilter(
//              themeColor,
//              SRC_ATOP
//          )
//        if (imageView == null) {
//          return
//        }
//        imageView.drawable.colorFilter = porterDuffColorFilter

    //图片处理2
//    val color: Int = Color.argb(((1 - alpha) * 255).toInt(), 255, 255, 255)
//    (view!!.findViewById(R.id.searchTagItemIcon) as ImageView).setColorFilter(color)
  }

  open operator fun <T : View?> get(
    view: View,
    id: Int
  ): T? {
    var viewHolder =
      view.tag as SparseArray<View?>
    if (viewHolder == null) {
      viewHolder = SparseArray()
      view.tag = viewHolder
    }
    var childView = viewHolder[id]
    if (childView == null) {
      childView = view.findViewById(id)
      viewHolder.put(id, childView)
    }
    return childView as T?
  }
}