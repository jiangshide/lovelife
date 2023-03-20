package com.android.sanskrit.blog.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.resource.MyFragment
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.tablayout.indicators.LinePagerIndicator
import kotlinx.android.synthetic.main.blog_view_manager_fragment.tabsView
import kotlinx.android.synthetic.main.blog_view_manager_fragment.viewViewPager

/**
 * created by jiangshide on 2020/5/10.
 * email:18311271399@163.com
 */
class ViewManagerFragment : MyFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.blog_view_manager_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    val blog: Blog = arguments?.getParcelable("data")!!
    viewViewPager.adapter = viewViewPager.create(childFragmentManager)
        .setTitles(
            "点赞", "预览", "分享"
        )
        .setFragment(
            PraiseFragment(blog.id),
            ViewFragment(blog.id),
            ShareFragment(blog.id)
        )
        .initTabs(activity!!, tabsView, viewViewPager, true)
        .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
        .setTxtSelecteSize(12)
        .setTxtSelectedSize(15)
        .setLinePagerIndicator(getColor(R.color.blue))
  }
}