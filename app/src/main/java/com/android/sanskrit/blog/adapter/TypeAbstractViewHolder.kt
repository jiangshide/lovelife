package com.android.sanskrit.blog.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.event.ZdEvent
import com.android.resource.*
import com.android.resource.vm.blog.BlogVM
import com.android.resource.vm.blog.OnBlogListener
import com.android.resource.vm.blog.OnPraiseListener
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.blog.data.Comment
import com.android.resource.vm.report.data.Report
import com.android.sanskrit.R
import com.android.sanskrit.blog.fragment.CalculateDistanceFragment
import com.android.sanskrit.blog.fragment.CommentDialogFragment
import com.android.sanskrit.blog.fragment.OnTotalListener
import com.android.sanskrit.blog.fragment.ViewManagerFragment
import com.android.sanskrit.channel.fragment.ChannelManagerBlogFragment
import com.android.sanskrit.home.HOME_REFRESH
import com.android.sanskrit.mine.MineFragment
import com.android.sanskrit.report.ReportFragment
import com.android.sanskrit.user.UserFragment
import com.android.utils.LogUtil
import com.android.widget.*
import com.android.widget.adapter.KAdapter

/**
 * created by jiangshide on 2020/3/31.
 * email:18311271399@163.com
 */
abstract class TypeAbstractViewHolder(
    val fragment: MyFragment,
    itemView: View,
    val blogVM: BlogVM,
    open val lifecycleOwner: LifecycleOwner,
    private val adapter: BlogAdapter,
    private val zdRecycleView: ZdRecycleView,
    private val from: Int = FROM_FIND,
    private val uid: Long = -1
) : ViewHolder(itemView) {

    init {
        blogVM!!.viewAdd.observe(lifecycleOwner, Observer {
            if (adapter == null || adapter.count() == 0) return@Observer
            adapter?.data()[it.data.index].viewNum = it.data.viewNum
            if (zdRecycleView != null && zdRecycleView.isComputingLayout) {
                zdRecycleView.post {
                    adapter.notifyDataSetChanged()
                }
            } else {
                adapter.notifyDataSetChanged()
            }
        })
        blogVM!!.shareAdd.observe(lifecycleOwner, Observer {
            if (adapter == null || adapter.count() == 0) return@Observer
            adapter?.data()[it.data.index].shareNum = it.data.shareNum
//      adapter?.notifyDataSetChanged()
            if (zdRecycleView != null && zdRecycleView.isComputingLayout) {
                zdRecycleView.post {
                    adapter.notifyDataSetChanged()
                }
            } else {
                adapter.notifyDataSetChanged()
            }
        })
        blogVM!!.collection.observe(lifecycleOwner, Observer {
            if (adapter == null || adapter.count() == 0) return@Observer
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }

            adapter?.data()
                .forEachIndexed { index, blog ->
                    if (blog.url == it.data.url) {
                        adapter.data()[index].collections = it.data.collections
                        return@forEachIndexed
                    }
                }
            if (zdRecycleView != null && zdRecycleView.isComputingLayout) {
                zdRecycleView.post {
                    adapter?.notifyDataSetChanged()
                }
            } else {
                adapter?.notifyDataSetChanged()
            }

            ZdEvent.get()
                .with(HOME_REFRESH)
                .post(HOME_REFRESH)
        })
        blogVM!!.followAdd.observe(lifecycleOwner, Observer {
            if (adapter == null || adapter.count() == 0) return@Observer
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
//      if (zdRecycleView != null && zdRecycleView.isComputingLayout) {
//        zdRecycleView.post {
//          adapter?.remove(it.data)
//        }
//      } else {
//        adapter?.remove(it.data)
//      }
            ZdEvent.get()
                .with(HOME_REFRESH)
                .post(HOME_REFRESH)
        })

        blogVM!!.update.observe(lifecycleOwner, Observer {
            if (adapter == null || adapter.count() == 0) return@Observer
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }

            if (it.data.status == -1) {
                if (zdRecycleView != null && zdRecycleView.isComputingLayout) {
                    zdRecycleView.post {
                        adapter?.remove(it.data)
                    }
                } else {
                    adapter?.remove(it.data)
                }
                if (adapter?.count() == 0) {
                    ZdEvent.get()
                        .with(REFRESH_MINE)
                        .post(REFRESH_MINE)
                }
            } else {
                adapter?.update(it.data.index, it.data)
                when (from) {
                    FROM_MEMORY -> {
                        ZdEvent.get()
                            .with(REFRESH_MEMORY)
                            .post(REFRESH_MEMORY)
                        ZdEvent.get()
                            .with(REFRESH_MINE)
                            .post(REFRESH_MINE)
                    }
                    FROM_MINE -> {
                        ZdEvent.get()
                            .with(REFRESH_MINE)
                            .post(REFRESH_MINE)
                    }
                }
            }
        })
        blogVM!!.remove.observe(lifecycleOwner, Observer {
            if (adapter == null || adapter.count() == 0) return@Observer
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
            adapter?.remove(it.data)
        })

        blogVM!!.commentAdd.observe(lifecycleOwner, Observer {
            LogUtil.e("err:", it.error, " | data:", it.data)
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
//      commentAdapter?.add(it.data)

        })
    }

    open fun bindHolder(
        index: Int,
        blog: Blog
    ) {
//    blog.index = index
        head(blog)
        foot(blog)
        comment(blog)
    }

    @SuppressLint("NewApi")
    private fun foot(blog: Blog) {
        val blogImgItemChannel = itemView.findViewById<ZdButton>(R.id.blogImgItemChannel)
        if (from != FROM_CHANNEL && !TextUtils.isEmpty(blog.channel)) {
            blogImgItemChannel.text = "# ${blog.channel}"
            blogImgItemChannel.visibility = View.VISIBLE
        } else {
            blogImgItemChannel.visibility = View.GONE
        }
        blogImgItemChannel.setOnClickListener {
            if (Resource.user == null) {
                fragment?.goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            fragment?.push(ChannelManagerBlogFragment(blog.uid, blog.channelId, blogId = blog.id))
        }

        val blogImgItemLocation = itemView.findViewById<TextView>(R.id.blogImgItemLocation)
        if (TextUtils.isEmpty(blog.city)) {
            blogImgItemLocation.visibility = View.GONE
        } else {
            blogImgItemLocation.visibility = View.VISIBLE
        }
        blogImgItemLocation.text = blog.city
        blogImgItemLocation.setOnClickListener {
            if (Resource.user == null) {
                fragment?.goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            fragment?.push(CalculateDistanceFragment(), blog)
//      fragment.go(DistrictActivity::class.java)
        }

        val blogItemFootLike = itemView.findViewById<TextView>(R.id.blogItemFootLike)
        blogItemFootLike.isSelected = blog.praises == 1
        blogItemFootLike.text = "${blog.praiseNum}"
        blogItemFootLike.setOnClickListener {
            if (Resource.user == null) {
                fragment?.goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            blog.praises = 1 - blog.praises
            if (blog.praises == FOLLOW) {
                blog.praiseNum += 1
            } else {
                blog.praiseNum -= 1
            }
            adapter?.notifyDataSetChanged()
            blogVM?.praiseAdd(blog, object : OnPraiseListener {
                override fun onPraise(it: Blog) {
                    blog.praiseNum = it.praiseNum
                    blog.praises = it.praises
                    adapter?.notifyDataSetChanged()
                }
            })
        }
        val blogItemFootView = itemView.findViewById<TextView>(R.id.blogItemFootView)
        blogItemFootView.text = "${blog.viewNum}"
        blogItemFootView.setOnClickListener {
            if (Resource.user == null) {
                fragment?.goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            fragment?.push(ViewManagerFragment().setTitle(blog.name), blog)
        }
        val blogItemFootChat = itemView.findViewById<TextView>(R.id.blogItemFootChat)
        blog.showNum(blogItemFootChat, blog.commentNum)
        blogItemFootChat.setOnClickListener {
            if (Resource.user == null) {
                fragment?.goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
//      fragment?.push(CommentFragment(true),blog)
            val commentDialogFragment =
                CommentDialogFragment(fragment, blog, object : OnTotalListener {
                    override fun total(total: Long?) {
                        blog?.showNum(blogItemFootChat, total?.toInt()!!)
                    }
                })
            commentDialogFragment.show(fragment.childFragmentManager, blog.toString())
        }
        val blogItemFootShare = itemView.findViewById<TextView>(R.id.blogItemFootShare)
        blogItemFootShare.text = "${blog.collectionNum}"
        blogItemFootShare.isSelected = blog.collections > 0
        blogItemFootShare.setOnClickListener {
            if (Resource.user == null) {
                fragment?.goFragment(UserFragment::class.java)
                return@setOnClickListener
            }

            blog.collections = FOLLOW - blog.collections
            blogItemFootShare.isSelected = blog.collections > 0
            blogVM?.collectionAdd(index(blog), object : OnBlogListener {
                override fun onBlog(blog: Blog?, e: Exception?) {
                    if (e != null) {
                        blog?.collections = FOLLOW - blog?.collections!!
                        blogItemFootShare.isSelected = blog.collections > 0
                    }
                }
            })

//      ZdDialog.createList(itemView.context, listOf("站内好友", "微信朋友圈", "微信好友"))
//          .setOnItemListener { parent, view, position, id ->
//            blogVM?.shareAdd(blog, arrayListOf(2, 3, 4))
//          }
//          .show()
        }

        when (from) {
            FROM_FOLLOW, FROM_RECOMMEND -> {
                blogItemFootLike.visibility = View.VISIBLE
                blogItemFootView.visibility = View.GONE
//                blogItemFootChat.visibility = View.VISIBLE
                blogItemFootShare.visibility = View.VISIBLE
            }
            FROM_COLLECTION -> {
                blogItemFootLike.visibility = View.VISIBLE
                blogItemFootView.visibility = View.GONE
//                blogItemFootChat.visibility =
//                    if (blog.uid == Resource.uid) View.GONE else View.VISIBLE
                blogItemFootShare.visibility = View.VISIBLE
            }
            FROM_MINE, FROM_MEMORY -> {
                blogItemFootLike.visibility = View.VISIBLE
                blogItemFootView.visibility = View.VISIBLE
//                blogItemFootChat.visibility = View.GONE
                blogItemFootShare.visibility = View.VISIBLE
                if (from == FROM_MEMORY) {
                    blogItemFootLike.compoundDrawableTintList =
                        ColorStateList.valueOf(itemView.context.getColor(R.color.txtGray))
                    blogItemFootLike.isEnabled = false
                    blogItemFootView.compoundDrawableTintList =
                        ColorStateList.valueOf(itemView.context.getColor(R.color.txtGray))
                    blogItemFootView.isEnabled = false
                    blogItemFootShare.compoundDrawableTintList =
                        ColorStateList.valueOf(itemView.context.getColor(R.color.txtGray))
                    blogItemFootShare.isEnabled = false
                }
            }
        }
    }

    private fun head(blog: Blog) {
        blog.setDate(itemView.findViewById<TextView>(R.id.blogItemHeadTime))

        val blogItemHeadIcon = itemView.findViewById<ZdImageView>(R.id.blogItemHeadIcon)
        blogItemHeadIcon.loadCircle(blog.icon)
        blogItemHeadIcon.setOnClickListener {
            if (blog.uid == Resource.uid || from == FROM_MINE) return@setOnClickListener
            fragment?.push(MineFragment(from = FROM_MINE, id = blog.uid, isBottomBar = false))
        }
        val blogItemHeadName = itemView.findViewById<TextView>(R.id.blogItemHeadName)
        if (blog.uid == Resource.uid) {
            blogItemHeadName.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue))
        } else {
            blogItemHeadName.setTextColor(ContextCompat.getColor(itemView.context, R.color.font))
        }
        blogItemHeadName.text = blog.nick
        val blogItemHeadDes = itemView.findViewById<TextView>(R.id.blogItemHeadDes)
        blog.setSex(blogItemHeadDes)
        itemView.findViewById<ImageView>(R.id.blogItemHeadMore)
            .setOnClickListener {
                var collectione = if (blog.collections == ESPECIALLY) "取消珍藏" else "珍藏"
                var collections = if (blog.collections == FOLLOW) "取消收藏" else "收藏"

                var reposts = if (!TextUtils.isEmpty(blog.reportr)) "已举报" else "举报"

                var tope = if (blog.tops == ESPECIALLY) "取消超级置顶" else "超级置顶"
                var tops = if (blog.tops == FOLLOW) "取消置顶" else "置顶"

                var followe = if (blog.follows == ESPECIALLY) "取消特别关注" else "特别关注"
                var follows = if (blog.follows == FOLLOW) "取消关注" else "关注"

                if (blog.follows == ESPECIALLY) {
                    followe = "取消特别关注"
                }

                when (from) {
                    FROM_FOLLOW, FROM_RECOMMEND, FROM_CHANNEL -> {
                        if (blog.uid == Resource.uid) {
                            showDialog(
                                blog, arrayListOf(collectione, collections, "移到回忆箱"),
                                arrayListOf(R.color.blue, R.color.font, R.color.redLight)
                            )
                        } else {
                            if (blog.follows == FOLLOW) {
                                if (blog.collections == FOLLOW) {
                                    showDialog(
                                        blog,
                                        arrayListOf(
                                            followe,
                                            follows,
                                            collectione,
                                            collections,
                                            reposts
                                        ),
                                        arrayListOf(
                                            R.color.blue, R.color.font, R.color.blue, R.color.font,
                                            R.color.redLight
                                        )
                                    )
                                } else {
                                    showDialog(
                                        blog, arrayListOf(followe, follows, collections, reposts),
                                        arrayListOf(
                                            R.color.blue,
                                            R.color.font,
                                            R.color.font,
                                            R.color.redLight
                                        )
                                    )
                                }
                            } else {
                                showDialog(
                                    blog, arrayListOf(followe, follows, collections, reposts),
                                    arrayListOf(
                                        R.color.blue,
                                        R.color.font,
                                        R.color.font,
                                        R.color.redLight
                                    )
                                )
                            }
                        }
                        if (from == FROM_CHANNEL && uid == blog.uid) {
                            showDialog(
                                blog, arrayListOf(collectione, collections, reposts),
                                arrayListOf(R.color.font, R.color.font, R.color.redLight)
                            )
                        }
                    }
                    FROM_COLLECTION -> {
                        if (blog.uid == Resource.uid) {
                            showDialog(
                                blog, arrayListOf(collectione, collections, "移到回忆箱"),
                                arrayListOf(R.color.blue, R.color.font, R.color.redLight)
                            )
                        } else {
                            showDialog(
                                blog, arrayListOf("珍藏", collections, reposts),
                                arrayListOf<Int>(R.color.blue, R.color.font, R.color.redLight)
                            )
                        }
                    }
                    FROM_MINE -> {
                        showDialog(
                            blog, arrayListOf(tope, tops, collectione, collections, "移到回忆箱"),
                            arrayListOf<Int>(
                                R.color.blue,
                                R.color.font,
                                R.color.blue,
                                R.color.font,
                                R.color.redLight
                            )
                        )
                    }
                    FROM_MEMORY -> {
                        showDialog(
                            blog,
                            arrayListOf("还原", "删除"),
                            arrayListOf<Int>(R.color.blue, R.color.redLight)
                        )
                    }
                }
            }
    }

    private fun showDialog(
        blog: Blog,
        data: ArrayList<String>,
        dataColors: ArrayList<Int>
    ) {
        if (Resource.user == null) {
            fragment?.goFragment(UserFragment::class.java)
            return
        }
        ZdDialog.createList(itemView.context, data, dataColors)
            .setOnItemListener { parent, view, position, id ->
                when (data[position]) {
                    "特别关注", "取消特别关注" -> {
                        if (blog.follows == ESPECIALLY) {
                            blog.follows = FOLLOW
                        } else {
                            blog.follows = ESPECIALLY
                        }
                        blogVM?.followAdd(index(blog))
                    }
                    "关注", "取消关注" -> {
                        blog.follows = FOLLOW - blog.follows
                        blogVM?.followAdd(index(blog))
                    }
                    "珍藏", "取消珍藏" -> {
                        if (blog.collections == ESPECIALLY) {
                            blog.collections = FOLLOW
                        } else {
                            blog.collections = ESPECIALLY
                        }
                        blogVM?.collectionAdd(index(blog))
                    }
                    "收藏", "取消收藏" -> {
                        blog.collections = FOLLOW - blog.collections
                        blogVM?.collectionAdd(index(blog))
                    }
                    "超级置顶", "取消超级置顶" -> {
                        if (blog.tops == ESPECIALLY) {
                            blog.tops = FOLLOW
                        } else {
                            blog.tops = ESPECIALLY
                        }
                        blogVM?.topAdd(index(blog))
                    }
                    "置顶", "取消置顶" -> {
                        blog.tops = FOLLOW - blog.tops
                        blogVM?.topAdd(index(blog))
                    }
                    "还原" -> {
                        blog.status = ESPECIALLY
                        blogVM?.update(index(blog))
                    }
                    "删除" -> {
                        blogVM?.remove(index(blog))
                    }
                    "移到回忆箱" -> {
                        blog.status = -1
                        blogVM?.update(index(blog))
                    }
                    "举报" -> {
                        val report = Report(
                            contentId = blog.id,
                            source = CONTENT_FROM_BLOG,
                            name = blog.name
                        )
                        fragment.push(ReportFragment(), report)
                    }
                }
            }
            .show()
    }

    private fun index(blog: Blog): Blog {
        adapter.data()
            .forEachIndexed { index, it ->
                if (blog.id == it.id) {
                    blog.index = index
                    return blog
                }
            }
        return blog
    }

    val coment = Comment()
    var commentAdapter: KAdapter<Comment>? = null
    private fun comment(blog: Blog) {
        val blogImgItemTitle = itemView.findViewById<TextView>(R.id.blogImgItemTitle)
        if (!TextUtils.isEmpty(blog.title)) {
            blogImgItemTitle.text = blog.title
            blogImgItemTitle.visibility = View.VISIBLE
        } else {
            blogImgItemTitle.visibility = View.GONE
        }

        val blogImgItemContent = itemView.findViewById<ZdTextView>(R.id.blogImgItemContent)
        val des = blog.des
        if (!TextUtils.isEmpty(blog.des)) {
            blogImgItemContent.setTxt(blog.des)
            blogImgItemContent.visibility = View.VISIBLE
        } else {
            blogImgItemContent.visibility = View.GONE
        }

        val spannableStringBuilder =
            SpannableStringBuilder()
        spannableStringBuilder.append(des)
        blog?.ats?.forEach {
            if (it.uid != Resource?.uid) {
                val at = "  @${it.nick}"
                spannableStringBuilder.append(at)
                val aTclick: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        LogUtil.e("at:", at)
                        fragment?.push(MineFragment(from, it.uid))
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = false
                    }
                }
                val length = spannableStringBuilder?.length
                spannableStringBuilder.setSpan(
                    aTclick, length - at.length, length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                val color = blogImgItemContent.randomColor()
                val userColor =
                    ForegroundColorSpan(color)
                spannableStringBuilder.setSpan(
                    userColor, length - at.length, length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
//      spannableStringBuilder.setSpan(
//          AbsoluteSizeSpan(16, true), length-at.length, length,
//          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
//      )

            }
        }
        blogImgItemContent.movementMethod = LinkMovementMethod.getInstance()
        blogImgItemContent.text = spannableStringBuilder

//    itemView.findViewById<TextView>(R.id.blogImgItemComments).text =
//      if (blog.commentNum > 0) "共${blog.commentNum}条评论" else ""
//
//    val blogImgItemCommentRecycleView =
//      itemView.findViewById<ZdRecycleView>(R.id.blogImgItemCommentRecycleView)
//    val blogImgItemComment = itemView.findViewById<ZdButton>(R.id.blogImgItemComment)
//    blogImgItemComment.setOnClickListener {
//      val commentDialogFragment = CommentDialogFragment()
//      commentDialogFragment.show(fragment!!.childFragmentManager,"Dialog")
//    }
//
//    commentAdapter =blogImgItemCommentRecycleView.create(
//        arrayListOf(), R.layout.blog_fragment_list_comment_item,
//        {
//          blogImgItemCommentItemName.text = it.content
//        }, {
////      fragment?.push(CommentFragment(true),blog)
//      val commentDialogFragment = CommentDialogFragment()
//      commentDialogFragment.show(fragment!!.childFragmentManager,"Dialog")
//    })
//    if(blog.comments != null){
//      blog.comments!!.forEachIndexed { index, comment ->
//        commentAdapter?.add(comment)
//        if(index > 1)return
//      }
//    }
    }
}