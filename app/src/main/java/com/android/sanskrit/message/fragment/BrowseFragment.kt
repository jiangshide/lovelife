package com.android.sanskrit.message.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.BLOG_FOLLOW_FROM_FIND
import com.android.resource.FOLLOW
import com.android.resource.MEN
import com.android.resource.MyFragment
import com.android.resource.WOMEN
import com.android.resource.vm.blog.OnPraiseListener
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.blog.adapter.FROM_FIND
import com.android.sanskrit.channel.fragment.ChannelManagerBlogFragment
import com.android.sanskrit.mine.MineFragment
import com.android.utils.StringUtil
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import com.android.widget.extension.setDrawableLeft
import com.android.widget.recycleview.divider.CommonItemDecoration
import kotlinx.android.synthetic.main.home_find_fragment_item.view.findItemChannel
import kotlinx.android.synthetic.main.home_find_fragment_item.view.findItemFollow
import kotlinx.android.synthetic.main.home_find_fragment_item.view.findItemFormat
import kotlinx.android.synthetic.main.home_find_fragment_item.view.findItemIcon
import kotlinx.android.synthetic.main.home_find_fragment_item.view.findItemImg
import kotlinx.android.synthetic.main.home_find_fragment_item.view.findItemLike
import kotlinx.android.synthetic.main.home_find_fragment_item.view.findItemName
import kotlinx.android.synthetic.main.home_find_fragment_item.view.findItemUserL
import kotlinx.android.synthetic.main.message_browse_fragment.browseRecycleView

/**
 * created by jiangshide on 2020/5/21.
 * email:18311271399@163.com
 */
class BrowseFragment : MyFragment() {

  private var adapter: KAdapter<Blog>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.message_browse_fragment)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    request()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    blogVM!!.browseBlog.observe(this, Observer {
      hiddle()
      cancelRefresh()
      clearAnimTab()
      if (it.error != null) {
        if (adapter == null || adapter!!.count() == 0) {
          noNet("暂无点赞!").setTipsRes(mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无点赞!").setTipsRes(mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })

    blogVM!!.findBlogFollow.observe(this, Observer {
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      adapter!!.datas()[it.data.index].follows = it.data.follows
      adapter?.notifyDataSetChanged()
    })

    request()
  }

  private fun request() {
    blogVM?.browseBlog(context = mActivity)
    showLoading()
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    blogVM?.browseBlog(context = mActivity)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    blogVM?.browseBlog(isRefresh = false, context = mActivity)
  }

  private fun showView(data: MutableList<Blog>) {
    var isRandom = if (adapter?.datas() == null || adapter!!.datas().size == 0) {
      data.size > 10
    } else {
      adapter?.datas()!!.size > 10
    }
    enableLoadMore(data.size == blogVM?.size)
    if (!blogVM!!.isRefresh) {
      adapter?.add(data, blogVM!!.isRefresh)
      return
    }
    val layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
    adapter = browseRecycleView.create(data, R.layout.home_find_fragment_item, {
      val blog = it
      it.setImg(
          activity = mActivity, formatImg = findItemFormat, coverImg = findItemImg,
          isRandom = isRandom
      )
      findItemUserL.setOnClickListener {
        push(MineFragment(FROM_FIND, blog.uid))
      }
      Img.loadImageCircle(it.icon, findItemIcon, R.mipmap.default_user)
      findItemName.text = it.nick
      var city = it.ucity
      if (!TextUtils.isEmpty(city)) {
        city = it.city
      }
      findItemName.text = getString(R.string.dot) + city
      StringUtil.setDot(findItemName, getString(R.string.dot))
      when (it.sex) {
        WOMEN -> {
          findItemName.setDrawableLeft(R.mipmap.sex_women)
        }
        MEN -> {
          findItemName.setDrawableLeft(R.mipmap.sex_man)
        }
      }

      findItemChannel.text = "# ${it.channel}"
      findItemLike.isSelected = blog.praises == 1
      findItemLike.text = "${blog.praiseNum}"
      findItemLike.setOnClickListener {
        blog.praises = FOLLOW -blog.praises
        if(blog.praises == FOLLOW){
          blog.praiseNum +=1
        }else{
          blog.praiseNum -=1
        }
        adapter?.notifyDataSetChanged()
        blogVM?.praiseAdd(blog,object :OnPraiseListener{
          override fun onPraise(blog: Blog) {
            blog?.praiseNum = blog.praiseNum
            blog?.praises = blog.praises
            adapter?.notifyDataSetChanged()
          }
        })
      }
      findItemFollow.text = if (blog.follows == 1) "取消关注" else "关注"
      findItemFollow.setOnClickListener {
        adapter!!.datas()
            .forEachIndexed { index, it ->
              if (it.id == blog.id) {
                blog.index = index
                return@forEachIndexed
              }
            }
        blog.follows = 1 - blog.follows
        blogVM?.followAdd(blog, BLOG_FOLLOW_FROM_FIND)
      }
    }, {
      push(ChannelManagerBlogFragment(uid,channelId,id))
    }, layoutManager)
  }
}