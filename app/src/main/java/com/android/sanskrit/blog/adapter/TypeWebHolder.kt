package com.android.sanskrit.blog.adapter

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.android.resource.MyFragment
import com.android.resource.vm.blog.BlogVM
import com.android.resource.vm.blog.data.Blog
import com.android.widget.ZdRecycleView

/**
 * created by jiangshide on 2020/3/31.
 * email:18311271399@163.com
 */
class TypeWebHolder(
  fragment: MyFragment,
  itemView: View,
  blogVM: BlogVM,
  lifecycleOwner: LifecycleOwner,
  adapter: BlogAdapter,
  zdRecycleView: ZdRecycleView,from: Int = FROM_FIND,private val uid:Long=-1
) : TypeAbstractViewHolder(fragment, itemView, blogVM, lifecycleOwner, adapter, zdRecycleView,from,uid) {
  override fun bindHolder(index:Int,blog: Blog) {
    super.bindHolder(index,blog)
  }
}