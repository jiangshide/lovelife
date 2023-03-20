package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.sanskrit.R
import com.android.resource.MyFragment

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class TypefaceFragment :MyFragment(){

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_typeface_fragment)
  }
}