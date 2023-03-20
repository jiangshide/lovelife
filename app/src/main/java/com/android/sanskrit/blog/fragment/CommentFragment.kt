package com.android.sanskrit.blog.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.FOLLOW
import com.android.resource.MyFragment
import com.android.resource.vm.blog.OnCommentPraiseListener
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.blog.data.Comment
import com.android.sanskrit.R
import com.android.sanskrit.blog.fragment.CommentEditView.OnCommentListener
import com.android.utils.DateUtil
import com.android.utils.LogUtil
import com.android.widget.ZdDialog
import com.android.widget.ZdEditText.OnKeyboardListener
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.blog_commend_fragment.commentRecycleView
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.blogItemFootLike
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemContent
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemDate
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemIcon
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemName
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemRecycleView
import kotlinx.android.synthetic.main.blog_fragment_comment.commentFeel
import kotlinx.android.synthetic.main.blog_fragment_comment.commentGoEdit
import kotlinx.android.synthetic.main.blog_fragment_comment.commentUser
import kotlinx.android.synthetic.main.blog_fragment_comment_item.view.commentChildDate
import kotlinx.android.synthetic.main.blog_fragment_comment_item.view.commentChildDes
import kotlinx.android.synthetic.main.blog_fragment_comment_item.view.commentChildIcon
import kotlinx.android.synthetic.main.blog_fragment_comment_item.view.commentChildName

/**
 * created by jiangshide on 2020/4/1.
 * email:18311271399@163.com
 */
class CommentFragment(private val showTitle: Boolean) : MyFragment(), OnKeyboardListener,
    OnCommentListener {

  private var blogId: Long = 0

  private var adapter: KAdapter<Comment>? = null

  private val comment = Comment()
  private var commentEditView:CommentEditView?=null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    commentEditView = CommentEditView(this)
    return if (showTitle) {
      setTitleView(R.layout.blog_commend_fragment, true, true)
    } else {
      setView(R.layout.blog_commend_fragment, true, true)
    }
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    blogVM?.comment(blogId = blogId)
    showLoading()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    val blog: Blog = arguments?.getParcelable("data")!!
    blogId = blog.id
    comment.contentId = blogId
    showFloatMenu(false)
    setTitle(blog.name)

    commentGoEdit.setOnClickListener {
//      commentEditView.setFromId(blogId)
//          .setListener(this)
//          .show(true)
    }
    commentUser.setOnClickListener {
//      commentEditView.setFromId(blogId)
//          .setListener(this)
//          .show(true)
    }
    commentFeel.setOnClickListener {
//      commentEditView.setFromId(blogId)
//          .setListener(this)
//          .show()
    }

    blogVM!!.commentAdd.observe(this, Observer {
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      if (commentEditView != null) {
        ZdDialog.cancelDialog()
      }
      autoRefresh()
    })

    blogVM!!.praiseCommentAdd.observe(this, Observer {
      if (adapter == null || adapter?.datas() == null) return@Observer
      adapter!!.datas()
          .forEachIndexed { index, comment ->
            if (comment.id == it.data.id) {
              adapter!!.datas()[index].praises = it.data.praises
              adapter!!.datas()[index].praiseNum = it.data.praiseNum
              return@forEachIndexed
            }
          }
      adapter?.notifyDataSetChanged()
    })

    blogVM!!.comment.observe(this, Observer {
      cancelRefresh()
      hiddle()
      if (it.error != null) {
        if (adapter == null || adapter?.count() == 0) {
          noNet("暂无评论!").setTipsRes(R.mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          noNet("暂无评论!").setTipsRes(R.mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })

    blogVM?.comment(blogId = blogId)
    showLoading()
  }

  private fun showView(data: MutableList<Comment>) {
    enableLoadMore(data.size == blogVM?.size)
    adapter = commentRecycleView.create(data, R.layout.blog_commend_fragment_item, {
      val comment = it
      Img.loadImageCircle(it!!.icon, commentItemIcon)
      commentItemName.text = it.nick
      commentItemContent.setTxt(it.content)
      it.setDate(commentItemDate)
      blogItemFootLike.isSelected = !blogItemFootLike.isSelected
      blogItemFootLike.text = "${it.praiseNum}"
      blogItemFootLike.setOnClickListener {
        blogVM?.praiseCommentAdd(comment,object : OnCommentPraiseListener{
          override fun onPraise(it: Comment) {
            comment.praises = it.praises
            blogItemFootLike.isSelected = comment.praises == FOLLOW
            comment.praiseNum = it.praiseNum
            blogItemFootLike.text = "${comment.praiseNum}"
          }
        })
      }
      if (it.comments != null) {
        commentItemRecycleView.create(it.comments!!, R.layout.blog_fragment_comment_item, {
          Img.loadImageCircle(it!!.icon, commentChildIcon)
          commentChildName.text = it.nick
          commentChildDes.setTxt(it.content)
          it.setDate(commentChildDate)
        }, {showEdit(this)})
      }
    }, {
      showEdit(this)
    })
  }

  private fun showEdit(comment: Comment) {
//    commentEditView.setNick(comment.nick!!)
//        .setFromId(comment.id)
//        .setListener(this)
//        .show(true)
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    blogVM?.comment(blogId = blogId)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    blogVM?.comment(blogId = blogId, isRefresh = false)
  }

  override fun hide(height: Int) {
  }

  override fun show(height: Int) {
  }

  override fun onResult(comment: Comment) {
    blogVM?.commentAdd(comment)
  }
}