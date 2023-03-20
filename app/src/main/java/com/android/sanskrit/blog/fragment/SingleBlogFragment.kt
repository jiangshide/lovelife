package com.android.sanskrit.blog.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.files.view.transferee.loader.GlideImageLoader
import com.android.files.view.transferee.transfer.TransferConfig
import com.android.http.exception.HttpException
import com.android.img.Img
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.blog.OnPraiseListener
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.blog.data.Comment
import com.android.resource.vm.channel.data.ChannelBlog
import com.android.resource.vm.user.OnFollowListener
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.sanskrit.mine.MineFragment
import com.android.utils.AppUtil
import com.android.utils.DateUtil
import com.android.widget.ZdToast
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemContent
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemDate
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemIcon
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemName
import kotlinx.android.synthetic.main.blog_fragment_list_foot.blogImgItemChannel
import kotlinx.android.synthetic.main.blog_fragment_list_foot.blogImgItemLocation
import kotlinx.android.synthetic.main.blog_fragment_list_foot.blogItemFootChat
import kotlinx.android.synthetic.main.blog_fragment_list_foot.blogItemFootLike
import kotlinx.android.synthetic.main.blog_fragment_list_foot.blogItemFootShare
import kotlinx.android.synthetic.main.blog_fragment_list_foot.blogItemFootView
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogBack
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogCommentAll
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogCommentRecycleView
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogComments
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogContent
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogDate
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogFollow
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogGuessRecycleView
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogIcon
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogIconL
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogImg
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogMore
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogName
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogSendEdit
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogSex
import kotlinx.android.synthetic.main.blog_fragment_single.singleBlogTitle
import kotlinx.android.synthetic.main.blog_fragment_single.singleViewRoot

/**
 * created by jiangshide on 2019-10-29.
 * email:18311271399@163.com
 */
class SingleBlogFragment(
  private val blogId: Long,
  private val uid: Long = 0
) : MyFragment(), OnFollowListener {

  private var blog: Blog? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setView(R.layout.blog_fragment_single)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    singleBlogBack.setOnClickListener {
      pop()
    }
    singleBlogIconL.setOnClickListener {
      push(MineFragment(id = uid))
    }

    blogItemFootLike.setOnClickListener {
      blogVM?.praiseAdd(blog!!, object :OnPraiseListener{
        override fun onPraise(blog: Blog) {

        }
      })
    }
    blogItemFootView.setOnClickListener {
      push(ViewManagerFragment(), blog)
    }

    blogItemFootChat.setOnClickListener {
      push(CommentFragment(true), blog)
    }

    blogItemFootShare.setOnClickListener {

    }
    if(blog?.uid == Resource.uid){
      singleBlogFollow.visibility = View.GONE
    }else{
      singleBlogFollow.visibility = View.VISIBLE
    }
    singleBlogFollow.setOnClickListener {
      val user = User()
      user.follows = blog!!.follows
      userVM?.followAdd(uid = user.id, status = user.follows, listener = this)
    }
    singleBlogMore.setOnClickListener {

    }

    singleBlogSendEdit.setListener { s, input ->

    }

    singleBlogCommentAll.setOnClickListener {
      push(CommentFragment(true), blog)
    }
    singleBlogImg.setOnClickListener { position, view, urls ->
      val urls = ArrayList<String>()
      blog?.urls?.forEach {
        urls.add(it.url!!)
      }
      transferee?.apply(
          TransferConfig.build()
              .setImageLoader(GlideImageLoader.with(AppUtil.getApplicationContext()))
              .setNowThumbnailIndex(position)
              .setSourceImageList(urls)
              .create()
      )
          ?.show()
      blogVM?.viewAdd(blog!!, position)
    }

    blogVM!!.blogId.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        singleViewRoot.visibility = View.GONE
        return@Observer
      }
      blog = it.data
      initView(blog!!)
      singleViewRoot.visibility = View.VISIBLE
    })

    blogVM?.blogId(blogId)
    showLoading()
  }

  private fun initView(blog: Blog) {
    Img.loadImageCircle(blog.icon, singleBlogIcon)
    singleBlogName.text = blog.nick
    blog.setSex(singleBlogSex)
    if (!TextUtils.isEmpty(blog.channel)) {
      blogImgItemChannel.text = "# ${blog.channel}"
      blogImgItemChannel.visibility = View.VISIBLE
    } else {
      blogImgItemChannel.visibility = View.GONE
    }
    if (!TextUtils.isEmpty(blog.city) && blog.latitude > 0) {
      blogImgItemLocation.visibility = View.VISIBLE
      blogImgItemLocation.text = blog.city
    } else {
      blogImgItemLocation.visibility = View.GONE
    }
    singleBlogImg.setData(blog.urls)
    blogItemFootLike.text = "${blog.praiseNum}"
    blogItemFootLike.isSelected = blog.praises == 1
    blogItemFootView.text = "${blog.viewNum}"
    if (blog.uid == Resource.uid) {
      blogItemFootView.visibility = View.VISIBLE
    } else {
      blogItemFootView.visibility = View.GONE
    }

    blogItemFootChat.text = "${blog.commentNum}"
    blogItemFootShare.text = "${blog.collectionNum}"
    if (blog.follows == 0) {
      singleBlogFollow.visibility = View.VISIBLE
    } else {
      singleBlogFollow.visibility = View.GONE
    }
    singleBlogTitle.text = blog.title
    blog.setDate(singleBlogDate)
    singleBlogContent.setTxt(blog.des)
    singleBlogComments.text = "共计${blog.commentNum}条评论"
    if(blog.comments != null){
      showComments(blog.comments!!)
    }
//    showGussChannel()
  }

  private fun showComments(data: MutableList<Comment>) {
    singleBlogCommentRecycleView.create(data, R.layout.blog_commend_fragment_item, {
      Img.loadImageCircle(it.icon, commentItemIcon, R.mipmap.default_user)
      commentItemName.text = it.nick
      commentItemContent.text = it.content
      commentItemDate.text = DateUtil.showTimeAhead(DateUtil.stringToLong(it.date))
    }, {})
  }

  private fun showGussChannel(data: MutableList<ChannelBlog>) {
    singleBlogGuessRecycleView.create(data, R.layout.default_tab_layout, {}, {})
  }

  override fun follow(
    status: Int,
    e: HttpException?
  ) {
    if (e != null) {
      ZdToast.txt(e.message)
      return
    }
    singleBlogFollow.visibility = View.GONE
  }
}