package com.android.sanskrit.channel.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.resource.MyFragment
import com.android.resource.vm.channel.data.ChannelNature
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.channel_nature_fragment.channelNatureRecycleView
import kotlinx.android.synthetic.main.channel_nature_fragment_item.view.channelTypeItemDes
import kotlinx.android.synthetic.main.channel_nature_fragment_item.view.channelTypeItemName
import kotlinx.android.synthetic.main.channel_nature_fragment_item.view.channelTypeItemNote

/**
 * created by jiangshide on 2020/3/19.
 * email:18311271399@163.com
 */
class ChannelNatureFragment : MyFragment() {

  private var adapter:KAdapter<ChannelNature>?=null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.channel_nature_fragment)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    channelVM?.channelNature()
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    channelVM!!.channelNature.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        if ((adapter == null || adapter?.count() == 0)) {
          noNet("暂无频道支持!").setTipsRes(mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无频道支持!").setTipsRes(mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })
    channelVM?.channelNature()
    showLoading()
  }

  private fun showView(data: MutableList<ChannelNature>) {
    channelNatureRecycleView.create(data, R.layout.channel_nature_fragment_item, {
      channelTypeItemName.text = it.name
      channelTypeItemDes.text = it.des
      channelTypeItemNote.text = it.note
    }, {
      push(CreateChannelFragment().setTitle(name), set("id", id))
    })
  }
}