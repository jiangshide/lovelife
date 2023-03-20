package com.android.sanskrit.blog.adapter

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.android.files.view.transferee.loader.GlideImageLoader
import com.android.files.view.transferee.transfer.TransferConfig
import com.android.resource.MyFragment
import com.android.resource.view.ImgsView
import com.android.resource.vm.blog.BlogVM
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.utils.AppUtil
import com.android.utils.LogUtil
import com.android.widget.ZdRecycleView

/**
 * created by jiangshide on 2020/3/31.
 * email:18311271399@163.com
 */
class TypeImgHolder(
  fragment: MyFragment,
  itemView: View,
  blogVM: BlogVM,
  lifecycleOwner: LifecycleOwner,
  adapter: BlogAdapter,
  zdRecycleView: ZdRecycleView,
  from: Int = FROM_FIND,
  private val uid: Long
) : TypeAbstractViewHolder(
    fragment, itemView, blogVM, lifecycleOwner, adapter, zdRecycleView, from, uid
) {

  override fun bindHolder(
    index: Int,
    blog: Blog
  ) {
    super.bindHolder(index, blog)
    val blogImgItemImg = itemView.findViewById<ImgsView>(R.id.blogImgItemImg)
    blogImgItemImg?.setData(blog.getFiles())
    blogImgItemImg?.setOnClickListener { position, view, urls ->
      fragment?.transferee?.apply(
          TransferConfig.build()
              .setImageLoader(GlideImageLoader.with(AppUtil.getApplicationContext()))
              .setNowThumbnailIndex(position)
              .setSourceImageList(urls)
              .create()
      )
          ?.show()
      blog.index = index
      blogVM.viewAdd(blog, index = position)
//      fragment?.push(SingleBlogFragment(), blog)
    }
  }

}