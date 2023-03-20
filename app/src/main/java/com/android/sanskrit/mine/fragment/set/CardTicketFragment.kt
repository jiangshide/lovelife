package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.resource.MyFragment
import com.android.sanskrit.R

/**
 * created by jiangshide on 2020/5/2.
 * email:18311271399@163.com
 */
class CardTicketFragment :MyFragment(){

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_cartticket_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

  }
}