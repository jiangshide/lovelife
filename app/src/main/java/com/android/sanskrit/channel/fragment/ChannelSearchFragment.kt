package com.android.sanskrit.channel.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.img.Img
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.blog.OnWordListener
import com.android.resource.vm.channel.data.ChannelBlog
import com.android.resource.vm.channel.data.Word
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.channel.fragment.adapter.TagAdapter
import com.android.sanskrit.user.UserFragment
import com.android.widget.ZdButton
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import com.android.widget.recycleview.divider.CommonItemDecoration
import kotlinx.android.synthetic.main.channel_search_fragment.*
import kotlinx.android.synthetic.main.channel_search_fragment_item.view.channelSearchDes
import kotlinx.android.synthetic.main.channel_search_fragment_item.view.channelSearchIcon
import kotlinx.android.synthetic.main.channel_search_fragment_item.view.channelSearchMarket
import kotlinx.android.synthetic.main.channel_search_fragment_item.view.channelSearchName
import kotlinx.android.synthetic.main.channel_search_word_fragment_item.view.searchWordItemName

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class ChannelSearchFragment : MyFragment(),OnWordListener {

  private var adapter: KAdapter<ChannelBlog>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setView(R.layout.channel_search_fragment)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    channelVM?.word(source = 2,listener = this)
    channelVM?.channel()
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    setTopBar(searchRootL)
    searchEdit.setListener { s, input ->
      if (!TextUtils.isEmpty(input)) {
        channelVM!!.search(input, source = 2)
      } else {
        channelSearchRecycleView.visibility = View.GONE
      }
    }
    searchCancel.setOnClickListener {
      pop()
    }

    channelVM!!.search.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        channelSearchRecycleView.visibility = View.GONE
        return@Observer
      }
      showView(it.data)
      channelSearchRecycleView.visibility = View.VISIBLE
    })
    channelVM!!.channel.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        if ((adapter == null || adapter?.count() == 0)) {
          noNet("暂无频道!").setTipsRes(mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无频道!").setTipsRes(mipmap.no_data)
        }
        return@Observer
      }
      showTag(it.data)
    })
    channelVM?.word(source = 2,listener = this)
    channelVM?.channel()
    showLoading()
  }

  private fun showTag(data: MutableList<ChannelBlog>) {
    val tagAdapter = object : TagAdapter<ChannelBlog>(mActivity, R.layout.search_tag_item) {
      override fun convertView(
        position: Int,
        item: View?,
        t: ChannelBlog?
      ) {
        val searchTagItemIcon = item?.findViewById<ImageView>(R.id.searchTagItemIcon)
        t?.setIcon(searchTagItemIcon!!,true)
//        Anim.anim(searchTagItemIcon)
        val searchTagItemName = item?.findViewById<ZdButton>(R.id.searchTagItemName)
        searchTagItemName?.setRandomColor()
        searchTagItemName?.text = t?.name
        item?.setOnClickListener {
          if(Resource.user == null){
            goFragment(UserFragment::class.java)
            return@setOnClickListener
          }
          push(ChannelManagerBlogFragment(t!!.uid,t.id))
        }
      }

      override fun onThemeColorChanged(
        view: View?,
        themeColor: Int,
        alpha: Float
      ) {
        super.onThemeColorChanged(view, themeColor, alpha)
        val color: Int = Color.argb(((1 - (1 - alpha)) * 255).toInt(), 255, 255, 255)
        val searchTagItemName = view?.findViewById<ZdButton>(R.id.searchTagItemName)
        searchTagItemName?.setTextColor(color)
        searchTagItemName?.alpha = alpha
        (view!!.findViewById(R.id.searchTagItemIcon) as ImageView).setColorFilter(
            Color.argb(((1 - alpha) * 255).toInt(), 255, 255, 255)
        )
      }
    }
    tagAdapter.add(data)
    searchTag.setAdapter(tagAdapter)
  }

  private fun showWord(data: MutableList<Word>) {
    if (data == null) return
    val layoutManager = LinearLayoutManager(mActivity)
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
    searchWordRecycleView.create(data, R.layout.channel_search_word_fragment_item, {
      searchWordItemName.text = it.name
      searchWordItemName.setRandomColor()
    }, {
      searchEdit.setText(name)
      channelVM!!.search(name!!, source = source)
    }, layoutManager, CommonItemDecoration(30, 10))
  }

  private fun showView(data: MutableList<ChannelBlog>) {
    adapter = channelSearchRecycleView.create(data, R.layout.channel_search_fragment_item, {
      it.setIcon(channelSearchIcon)
      channelSearchName.text = it.name
      when {
        it.official == 2 -> {
          channelSearchMarket.text = "官方力推"
          channelSearchMarket.visibility = View.VISIBLE
        }
        it.official == 1 -> {
          channelSearchMarket.text = "官方推荐"
          channelSearchMarket.visibility = View.VISIBLE
        }
        else -> {
          channelSearchMarket.visibility = View.GONE
        }
      }
      channelSearchDes.text = if (it.blogNum > 0) "已产生了${it.blogNum}内容" else it.des
    }, {
      if(Resource.user == null){
        goFragment(UserFragment::class.java)
        return@create
      }
      push(ChannelManagerBlogFragment(uid,id))
    })
  }

  override fun onWords(
    data: MutableList<Word>?,
    err: Exception?
  ) {
    if (data != null) {
      showWord(data)
    }
  }
}