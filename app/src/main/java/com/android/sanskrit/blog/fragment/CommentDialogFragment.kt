package com.android.sanskrit.blog.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.img.Img
import com.android.resource.FOLLOW
import com.android.resource.MyFragment
import com.android.resource.view.LoadingView
import com.android.resource.view.ZdDialogFragment
import com.android.resource.vm.blog.OnCommentPraiseListener
import com.android.resource.vm.blog.OnCommentUidAddListener
import com.android.resource.vm.blog.OnCommentUidListener
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.blog.data.Comment
import com.android.resource.vm.blog.data.Total
import com.android.sanskrit.R
import com.android.sanskrit.blog.fragment.CommentEditView.OnCommentListener
import com.android.utils.LogUtil
import com.android.utils.ScreenUtil
import com.android.widget.ZdButton
import com.android.widget.ZdRecycleView
import com.android.widget.ZdTipsView.OnTipsListener
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.blog_commend_fragment_child_item.view.blogChildItemFootLike
import kotlinx.android.synthetic.main.blog_commend_fragment_child_item.view.commentChildItemContent
import kotlinx.android.synthetic.main.blog_commend_fragment_child_item.view.commentChildItemDate
import kotlinx.android.synthetic.main.blog_commend_fragment_child_item.view.commentChildItemIcon
import kotlinx.android.synthetic.main.blog_commend_fragment_child_item.view.commentChildItemName
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.blogItemFootLike
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemContent
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemDate
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemIcon
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemName
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.commentItemRecycleView
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.itemLoadMore
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.itemLoadMoreL
import kotlinx.android.synthetic.main.blog_commend_fragment_item.view.itemLoading
import kotlinx.android.synthetic.main.blog_fragment_comment.commentClose
import kotlinx.android.synthetic.main.blog_fragment_comment.commentErrTips
import kotlinx.android.synthetic.main.blog_fragment_comment.commentFeel
import kotlinx.android.synthetic.main.blog_fragment_comment.commentGoEdit
import kotlinx.android.synthetic.main.blog_fragment_comment.commentRecycleView
import kotlinx.android.synthetic.main.blog_fragment_comment.commentTips
import kotlinx.android.synthetic.main.blog_fragment_comment.commentUser
import kotlinx.android.synthetic.main.blog_fragment_comment.loadMore
import kotlinx.android.synthetic.main.blog_fragment_comment.loadMoreL
import kotlinx.android.synthetic.main.blog_fragment_comment.loading

/**
 * created by jiangshide on 2020/5/11.
 * email:18311271399@163.com
 */
class CommentDialogFragment(
  private val fragment: MyFragment,
  private val blog: Blog,
  private val listener: OnTotalListener
) : ZdDialogFragment(),
    OnTipsListener,
    OnCommentListener {
  private var adapter: KAdapter<Comment>? = null
  private var commentEditView: CommentEditView? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return LayoutInflater.from(context)
        .inflate(R.layout.blog_fragment_comment, container, false)
  }

  override fun onTips(view: View?) {
    request(isLoad = true, isShow = true)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    commentEditView = CommentEditView(fragment)
    topOffset = ScreenUtil.getRtScreenHeight(context) / 4
    commentClose.setOnClickListener {
      commentEditView?.cance()
      dismiss()
    }
    commentGoEdit.setOnClickListener {
      commentEditView?.setListener(this)
          ?.showView(SHOW_SOFTKEY)
    }
    commentUser.setOnClickListener {
      commentEditView?.setListener(this)
          ?.showView(SHOW_AT)
    }
    commentFeel.setOnClickListener {
      commentEditView?.setListener(this)
          ?.showView(SHOW_EMOIL)
    }
    loadMore.setOnClickListener {
      request(isLoad = true, isShow = true, isRefresh = false)
    }

    fragment.blogVM!!.commentAdd.observe(this, Observer {
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      if (commentEditView != null) {
        commentEditView?.cance()
      }
      commentTips?.text = "${it?.data?.count} 条评论"
      listener?.total(it?.data?.count)
      request(true, isShow = true)
    })

    fragment.blogVM!!.comment.observe(this, Observer {
      commentErrTips.hidden()
      if (it.error != null) {
        adapter?.clear()
        load(isLoad = false, isShow = true)
        if (adapter == null || adapter?.count() == 0) {
          commentErrTips.setTipsDes("暂无评论")
              .noNet()
              .setTipsImg(R.mipmap.no_data)
              .setListener(this)
        } else if (fragment.blogVM!!.isRefresh) {
          commentErrTips.setTipsDes("暂无评论")
              .noNet()
              .setTipsImg(R.mipmap.no_data)
              .setListener(this)
        }
        return@Observer
      }

      showView(it.data)
    })
    commentTips.text = "${blog?.commentNum} 条评论"
    request(true, isShow = true)
  }

  private fun load(
    isLoad: Boolean,
    isShow: Boolean
  ) {
    if (isShow) {
      loadMoreL.visibility = View.VISIBLE
    } else {
      loadMoreL.visibility = View.GONE
    }
    if (isLoad) {
      loading.start()
      loading.visibility = View.VISIBLE
      loadMore.visibility = View.GONE
    } else {
      loading.visibility = View.GONE
      loadMore.visibility = View.VISIBLE
    }
  }

  private fun showView(data: MutableList<Comment>) {
    val size = data.size
    if (size == fragment.blogVM!!.size) {
      load(isLoad = false, isShow = true)
    } else {
      load(isLoad = false, isShow = false)
    }
    if (adapter != null) {
      adapter?.add(data, fragment.blogVM!!.isRefresh)
      commentRecycleView?.scrollToPosition(0)
      val layoutManager: LinearLayoutManager? =
        commentRecycleView.layoutManager as LinearLayoutManager?
      layoutManager?.scrollToPositionWithOffset(0,0)
      return
    }
    adapter = commentRecycleView.create(data, R.layout.blog_commend_fragment_item, {
      val comment = it
      Img.loadImageCircle(it!!.icon, commentItemIcon)
      if (it.num > 0) {
        commentItemName.text = "${it.nick} (${it.num})"
      } else {
        commentItemName.text = it.nick
      }
      commentItemContent.setTxt(it.content)
      it.setDate(commentItemDate)
      blogItemFootLike.isSelected = comment.praises == FOLLOW
      blogItemFootLike.text = "${it.praiseNum}"
      blogItemFootLike.setOnClickListener {
        fragment.blogVM?.praiseCommentAdd(comment, object : OnCommentPraiseListener {
          override fun onPraise(it: Comment) {
            comment.praises = it.praises
            blogItemFootLike.isSelected = comment.praises == FOLLOW
            comment.praiseNum = it.praiseNum
            blogItemFootLike.text = "${comment.praiseNum}"
          }
        })
      }
      var moreAdapter: KAdapter<Comment>? =null

      moreAdapter =  commentItemRecycleView.create(
            arrayListOf(), R.layout.blog_commend_fragment_child_item, { child ->
          Img.loadImageCircle(child!!.icon, commentChildItemIcon)
          commentChildItemName.text = child.nick
          if(child.reply == 1 && !TextUtils.isEmpty(child.replyNick)){
            commentChildItemContent.setTxt("回复 ${child.replyNick}: ${child.content}")
          }else{
            commentChildItemContent.setTxt(child.content)
          }
          it.setDate(commentChildItemDate)
          blogChildItemFootLike.isSelected = child.praises == FOLLOW
          blogChildItemFootLike.text = "${child.praiseNum}"
          blogChildItemFootLike.setOnClickListener {
            fragment.blogVM?.praiseCommentUidAdd(child, object : OnCommentPraiseListener {
              override fun onPraise(it: Comment) {
                child.praises = it.praises
                blogItemFootLike?.isSelected = child.praises == FOLLOW
                child.praiseNum = it.praiseNum
                blogItemFootLike?.text = "${child.praiseNum}"
              }
            })
          }
        }, {
          commentEditView?.setComment(comment)
              ?.setListener(object : OnCommentListener {
                override fun onResult(comment: Comment) {
                  comment?.contentId = blog?.id
                  comment?.reply = 1
                  fragment?.blogVM?.commentUidAdd(comment, object : OnCommentUidAddListener {
                    override fun onCommentAdd(
                      total: Total?,
                      err: Exception?
                    ) {
                      if (err != null) return
                      commentTips?.text = "${total?.count} 条评论"
                      listener?.total(total?.count)
                      requestCommentUid(
                          moreAdapter!!,
                          commentItemRecycleView, itemLoadMoreL, itemLoadMore, itemLoading,
                          comment,
                          true
                      )
                    }
                  })
                }
              })
              ?.showView(SHOW_SOFTKEY)
        })
      setOnClickListener {
        commentEditView?.setComment(comment)
            ?.setListener(object : OnCommentListener {
              override fun onResult(comment: Comment) {
                comment?.contentId = blog?.id
                fragment?.blogVM?.commentUidAdd(comment, object : OnCommentUidAddListener {
                  override fun onCommentAdd(
                    total: Total?,
                    err: Exception?
                  ) {
                    if (err != null) return
                    commentTips?.text = "${total?.count} 条评论"
                    listener?.total(total?.count)
                    requestCommentUid(
                        moreAdapter,
                        commentItemRecycleView, itemLoadMoreL, itemLoadMore, itemLoading,
                        comment,
                        true
                    )
                  }
                })
              }
            })
            ?.showView(SHOW_SOFTKEY)
      }

      itemLoadMore.setOnClickListener {
        requestCommentUid(
            moreAdapter,
            commentItemRecycleView, itemLoadMoreL, itemLoadMore, itemLoading, comment, false
        )
      }
      requestCommentUid(
          moreAdapter,
          commentItemRecycleView, itemLoadMoreL, itemLoadMore, itemLoading, comment, true
      )
    }, {
    })
  }

  private fun requestCommentUid(
    moreAdapter: KAdapter<Comment>,
    recycleView: ZdRecycleView,
    itemLoadMoreL: FrameLayout,
    itemLoadMore: ZdButton,
    itemLoading: LoadingView,
    comment: Comment,
    isRefresh: Boolean
  ) {

    itemLoadMoreL.visibility = View.VISIBLE
    itemLoadMore.visibility = View.GONE
    itemLoading.visibility = View.VISIBLE
    fragment?.blogVM?.commentUid(
        commentId = comment.id, fromUid = comment.fromUid, contentId = comment.contentId!!,
        listener = object :
            OnCommentUidListener {
          override fun onComments(
            data: MutableList<Comment>?,
            err: Exception?
          ) {
            itemLoading?.visibility = View.GONE
            if (err != null) return
            if (data?.size == fragment?.blogVM?.size) {
              itemLoadMoreL?.visibility = View.VISIBLE
              itemLoadMore?.visibility = View.VISIBLE
            } else {
              itemLoadMoreL?.visibility = View.GONE
            }
            moreAdapter?.add(data!!, isRefresh)

            if (isRefresh) {
              recycleView?.scrollToPosition(0)
            }
            itemLoadMore?.text = "------->还剩${comment?.num - moreAdapter?.datas().size}条可以加载"
          }
        }, isRefresh = isRefresh
    )
  }

  private fun request(
    isLoad: Boolean,
    isShow: Boolean,
    isRefresh: Boolean = true
  ) {
    fragment.blogVM?.comment(blogId = blog.id, isRefresh = isRefresh)
    commentErrTips.loading()
    load(isLoad, isShow)
  }

  override fun onResult(comment: Comment) {
    comment?.contentId = blog?.id
    fragment?.blogVM?.commentAdd(comment)
  }
}

interface OnTotalListener {
  fun total(total: Long? = 0)
}