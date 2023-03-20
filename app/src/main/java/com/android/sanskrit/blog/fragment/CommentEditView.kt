package com.android.sanskrit.blog.fragment

import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import com.android.network.NetWork
import com.android.resource.MyFragment
import com.android.resource.vm.blog.data.Comment
import com.android.sanskrit.R
import com.android.utils.LogUtil
import com.android.widget.ZdDialog
import com.android.widget.ZdDialog.DialogViewListener
import com.android.widget.ZdEditText
import com.android.widget.ZdEditText.OnKeyboardListener
import com.android.widget.ZdToast

/**
 * created by jiangshide on 2019-10-31.
 * email:18311271399@163.com
 */
class CommentEditView(
  private val fragment: MyFragment
) : DialogViewListener, OnKeyboardListener {

  var commentEdit: ZdEditText? = null

  private val comment = Comment()

  private var zdDialog: ZdDialog? = null

  private var mListener: OnCommentListener? = null

  init {
    zdDialog = ZdDialog(
        fragment?.mActivity, R.style.DialogTheme, R.layout.blog_fragment_comment_edit, this
    ).setGravity(Gravity.BOTTOM)
        .setAnim(R.style.bottomAnim)
        .setCancelListener {
          cance()
        }
  }

  fun setListener(listener: OnCommentListener): CommentEditView {
    this.mListener = listener
    return this
  }

  override fun onView(view: View) {
    commentEdit = view.findViewById(R.id.commentEdit)
    if (!TextUtils.isEmpty(comment.nick)) {
      commentEdit?.hint = "回复:${comment.nick}"
    }
    val commentEditSend = view.findViewById<ImageView>(R.id.commentEditSend)
    commentEditSend.setOnClickListener {
      if (!NetWork.instance.isNetworkAvailable()) {
        ZdToast.txt("网络不可用!")
        return@setOnClickListener
      }
      mListener?.onResult(comment)
      cance()
    }
    commentEdit?.setListener { s, input ->
      comment.content = input
      val disable = !TextUtils.isEmpty(comment.content) || comment!!.content!!.isEmpty()
      commentEditSend.isSelected = disable
      commentEditSend.isEnabled = disable
    }
        ?.setKeyBoardListener(fragment?.mActivity, this)
  }

  fun showView(showType: Int) {
    zdDialog?.show()
    when (showType) {
      SHOW_SOFTKEY -> {
        Handler().postDelayed({
          commentEdit?.show(fragment?.mActivity)
        }, 50)
      }
      SHOW_AT -> {
        hideSoft()
      }
      SHOW_EMOIL -> {
        hideSoft()
      }
    }
  }

  fun setComment(it: Comment): CommentEditView {
    comment.id = it.id
    comment.fromUid = it.uid
    comment.nick = it.nick
    if (commentEdit != null) {
      commentEdit?.hint = "回复 ${comment.nick} :"
    }
    return this
  }

  fun cance() {
    try {
      hideSoft()
      zdDialog?.cancel()
    } catch (e: Exception) {
      LogUtil.e(e)
    }
  }

  private fun hideSoft() {
    if (commentEdit != null) {
      commentEdit?.setText("")
      commentEdit?.clearFocus()
      val view = zdDialog?.window?.peekDecorView()
      commentEdit?.hide(fragment.mActivity, view)
    }
  }

  override fun hide(height: Int) {
    LogUtil.e("------hide~height:", height)
  }

  override fun show(height: Int) {
    LogUtil.e("-------show:", height)
  }

  interface OnCommentListener {
    fun onResult(comment: Comment)
  }
}

const val SHOW_SOFTKEY = 0
const val SHOW_AT = 1
const val SHOW_EMOIL = 2