package com.android.sanskrit.mine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.tablayout.indicators.LinePagerIndicator
import kotlinx.android.synthetic.main.mine_visitor_fragment.tabsVisitor
import kotlinx.android.synthetic.main.mine_visitor_fragment.visitorViewPager

/**
 * created by jiangshide on 2020/3/19.
 * email:18311271399@163.com
 */
class VisitorFragment : MyFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_visitor_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    visitorViewPager.adapter = visitorViewPager.create(childFragmentManager)
        .setTitles(
            "谁看过我", "我看过谁"
        )
        .setFragment(
//            BlogFragment(),
//            BlogFragment()
        )
        .initTabs(activity!!, tabsVisitor, visitorViewPager, true)
        .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
        .setTxtSelectedColor(R.color.black)
        .setTxtSelecteColor(R.color.gray)
        .setTxtSelecteSize(12)
        .setTxtSelectedSize(15)
        .setLinePagerIndicator(getColor(R.color.blue))
  }
}