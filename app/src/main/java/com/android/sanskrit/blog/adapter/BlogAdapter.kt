package com.android.sanskrit.blog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.resource.MyFragment
import com.android.resource.vm.blog.BlogVM
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R.layout
import com.android.utils.LogUtil
import com.android.utils.ScreenUtil
import com.android.utils.data.AUDIO
import com.android.utils.data.DOC
import com.android.utils.data.IMG
import com.android.utils.data.VIDEO
import com.android.utils.data.VR
import com.android.utils.data.WEB
import com.android.widget.ZdRecycleView

/**
 * created by jiangshide on 2020/3/31.
 * email:18311271399@163.com
 */
class BlogAdapter(
  private val fragment: MyFragment,
  val blogVM: BlogVM,
  private val lifecycleOwner: LifecycleOwner,
  private val zdRecycleView: ZdRecycleView,
  private val from: Int = FROM_FIND,
  private val uid: Long = -1
) : Adapter<ViewHolder>() {

  private val mLayoutInflater: LayoutInflater = LayoutInflater.from(fragment.context)
  var mData: ArrayList<Blog>? = ArrayList()

  fun add(data: MutableList<Blog>?) {
    add(data, false)
  }

  fun add(
    data: MutableList<Blog>?,
    isRefresh: Boolean
  ) {

    if (isRefresh) {
      mData!!.clear()
    }
    if (data != null) {
      mData?.addAll(data)
    }
    this.notifyDataSetChanged()
  }

  fun addFirst(data: MutableList<Blog>) {
    mData?.addAll(0, data)
    notifyDataSetChanged()
  }

  fun add(blog: Blog) {
    mData?.add(blog)
    notifyDataSetChanged()
  }

  fun addFirst(blog: Blog) {
    mData?.add(0, blog)
    notifyDataSetChanged()
  }

  fun count(): Int {
    if (mData == null || mData!!.size == 0) return 0
    return mData!!.size
  }

  fun data(): MutableList<Blog> {
    return mData!!
  }

  fun update(
    index: Int,
    blog: Blog
  ) {
    if (mData == null) return
    mData?.removeAt(index)
    mData?.add(index, blog)
    notifyDataSetChanged()
  }

  fun remove(blog: Blog) {
    mData?.remove(blog)
    notifyDataSetChanged()
  }

  fun clear() {
    mData?.clear()
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    LogUtil.e("viewType:", viewType)
    when (viewType) {
      IMG -> return TypeImgHolder(
          fragment,
          mLayoutInflater.inflate(
              layout.blog_img_fragment_item, parent, false
          )
          , blogVM, lifecycleOwner, BlogAdapter@ this, zdRecycleView, from, uid
      )
      AUDIO -> return TypeAudioHolder(
          fragment,
          mLayoutInflater.inflate(
              layout.blog_audio_fragment_item, parent, false
          )
          , blogVM, lifecycleOwner, BlogAdapter@ this, zdRecycleView, from, uid
      )
      VIDEO -> return TypeVideoHolder(
          fragment,
          mLayoutInflater.inflate(
              layout.blog_video_fragment_item, parent, false
          )
          , blogVM, lifecycleOwner, BlogAdapter@ this, zdRecycleView, from, uid
      )
      DOC -> return TypeTextHolder(
          fragment,
          mLayoutInflater.inflate(
              layout.blog_text_fragment_item, parent, false
          )
          , blogVM, lifecycleOwner, BlogAdapter@ this, zdRecycleView, from, uid
      )
      WEB -> return TypeWebHolder(
          fragment,
          mLayoutInflater.inflate(
              layout.blog_web_fragment_item, parent, false
          )
          , blogVM, lifecycleOwner, BlogAdapter@ this, zdRecycleView, from, uid
      )
      VR -> return TypeVRHolder(
          fragment,
          mLayoutInflater.inflate(
              layout.blog_vr_fragment_item, parent, false
          )
          , blogVM, lifecycleOwner, BlogAdapter@ this, zdRecycleView, from, uid
      )
    }
    return TypeVRHolder(
        fragment,
        mLayoutInflater.inflate(layout.blog_vr_fragment_item, parent, false)
        , blogVM, lifecycleOwner, BlogAdapter@ this, zdRecycleView, from, uid
    )
  }

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int
  ) {
    if (mData != null) {
      (holder as TypeAbstractViewHolder).bindHolder(position, mData!![position])
    }
  }

  override fun getItemViewType(position: Int): Int {
    return mData!![position].format
  }

  override fun getItemCount(): Int {
    return if (mData != null) mData!!.size else 0
  }
}

const val FROM_FIND = 0
const val FROM_FOLLOW = 1
const val FROM_RECOMMEND = 2
const val FROM_CITY = 3
const val FROM_COLLECTION = 4
const val FROM_MINE = 5
const val FROM_MEMORY = 6
const val FROM_AUDIO_RECOMMEND = 7
const val FROM_CHANNEL = 8