package com.android.resource.vm.blog

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.base.vm.BaseVM
import com.android.http.exception.HttpException
import com.android.http.observer.BaseObserver
import com.android.http.oss.OssClient
import com.android.http.transformer.CommonTransformer
import com.android.http.vm.LiveResult
import com.android.http.vm.data.RespData
import com.android.resource.BLOG_FOLLOW_FROM_FIND
import com.android.resource.Resource
import com.android.resource.vm.blog.data.*
import com.android.resource.vm.blog.remote.BlogRemote
import com.android.resource.vm.channel.data.Word
import com.android.resource.vm.user.data.User
import com.android.utils.LogUtil
import com.android.utils.ScreenUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import kotlin.random.Random

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class BlogVM : BaseVM() {
    private val iBlog: BlogRemote = http.createService(BlogRemote::class.java)
    var collection: MutableLiveData<LiveResult<Blog>> = MutableLiveData()
    var top: MutableLiveData<LiveResult<Blog>> = MutableLiveData()
    var followAdd: MutableLiveData<LiveResult<Blog>> = MutableLiveData()
    var findBlogFollow: MutableLiveData<LiveResult<Blog>> = MutableLiveData()
    var shareAdd: MutableLiveData<LiveResult<Blog>> = MutableLiveData()
    var share: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
    var viewAdd: MutableLiveData<LiveResult<Blog>> = MutableLiveData()
    var format: MutableLiveData<LiveResult<MutableList<Format>>> = MutableLiveData()
    var collectionBlog: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()
    var userBlog: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()
    var cityBlog: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()
    var followBlog: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()
    var recommendBlog: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()
    var findBlog: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()
    var praiseBlog: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()
    var browseBlog: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()
    var channelBlog: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()
    var update: MutableLiveData<LiveResult<Blog>> = MutableLiveData()
    var remove: MutableLiveData<LiveResult<Blog>> = MutableLiveData()
    var commentAdd: MutableLiveData<LiveResult<Total>> = MutableLiveData()
    var comment: MutableLiveData<LiveResult<MutableList<Comment>>> = MutableLiveData()
    var praiseCommentAdd: MutableLiveData<LiveResult<Comment>> = MutableLiveData()
    var blogFormat: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()
    var blogId: MutableLiveData<LiveResult<Blog>> = MutableLiveData()
    var searchLrc: MutableLiveData<LiveResult<LrcLInfo>> = MutableLiveData()
    var lrcUpdate: MutableLiveData<LiveResult<Long>> = MutableLiveData()
    var historyAdd: MutableLiveData<LiveResult<Long>> = MutableLiveData()
    var historyList: MutableLiveData<LiveResult<MutableList<Blog>>> = MutableLiveData()

    fun historyList(
        uid: Long? = Resource.uid,
        source: Int? = 0,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.historyList(uid = uid, source = source, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    historyList.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    historyList.postValue(LiveResult.error(e))
                }
            })
    }

    fun historyAdd(
        contentId: Long = 0,
        num: Int? = 0,
        source: Int? = 0
    ) {
        iBlog.historyAdd(contentId = contentId, num = num, source = source)
            .compose(CommonTransformer<Response<RespData<Long>>, Long>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Long>() {
                override fun onNext(t: Long) {
                    super.onNext(t)
                    historyAdd.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    historyAdd.postValue(LiveResult.error(e))
                }
            })
    }

    fun lrcUpdate(
        id: Long = 0,
        name: String? = "",
        lrcZh: String? = "",
        lrcEs: String? = ""
    ) {
        iBlog.lrcUpdate(id, name, lrcZh = lrcZh, lrcEs = lrcEs)
            .compose(CommonTransformer<Response<RespData<Long>>, Long>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Long>() {
                override fun onNext(t: Long) {
                    super.onNext(t)
                    lrcUpdate.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    lrcUpdate.postValue(LiveResult.error(e))
                }
            })
    }

    fun searchLrc(
        uid: Long = 0,
        name: String,
        author: String? = "",
        language: Int = 0
    ) {
        iBlog.searchLrc(uid, name = name, author = author, language = language)
            .compose(CommonTransformer<Response<RespData<LrcLInfo>>, LrcLInfo>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<LrcLInfo>() {
                override fun onNext(t: LrcLInfo) {
                    super.onNext(t)
                    searchLrc.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    searchLrc.postValue(LiveResult.error(e))
                }
            })
    }

    fun blogId(id: Long) {
        iBlog.blogId(id)
            .compose(CommonTransformer<Response<RespData<Blog>>, Blog>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Blog>() {
                override fun onNext(t: Blog) {
                    super.onNext(t)
                    blogId.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    blogId.postValue(LiveResult.error(e))
                }
            })
    }

    fun blogFormat(
        format: Int = 0,
        title: String = "",
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.blogFormat(format = format, title = title, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    blogFormat.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    blogFormat.postValue(LiveResult.error(e))
                }
            })
    }

    fun commentAdd(
        comment: Comment
    ) {
        iBlog.commentAdd(
            contentId = comment.contentId,
            content = comment.content,
            urls = comment.urls
        )
            .compose(CommonTransformer<Response<RespData<Total>>, Total>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Total>() {
                override fun onNext(t: Total) {
                    super.onNext(t)
                    commentAdd.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    commentAdd.postValue(LiveResult.error(e))
                }
            })
    }

    fun comment(
        blogId: Long,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.comment(contentId = blogId, page = page, pageSize = this.size)
            .compose(
                CommonTransformer<Response<RespData<MutableList<Comment>>>, MutableList<Comment>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Comment>>() {
                override fun onNext(t: MutableList<Comment>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    comment.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    comment.postValue(LiveResult.error(e))
                }
            })
    }

    fun praiseCommentAdd(
        comment: Comment,
        listener: OnCommentPraiseListener
    ) {
        if (comment.praises == 1) {
            comment.praises = 0
            comment.praiseNum -= 1
        } else {
            comment.praises = 1
            comment.praiseNum += 1
        }
        iBlog.praiseCommentAdd(id = comment.id, commentId = comment.id, status = comment.praises)
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
//            praiseCommentAdd.postValue(LiveResult.success(comment))
                    listener?.onPraise(comment)
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (comment.praises == 1) {
                        comment.praises = 0
                        comment.praiseNum -= 1
                    } else {
                        comment.praises = 1
                        comment.praiseNum += 1
                    }
//            praiseCommentAdd.postValue(LiveResult.success(comment))
                    listener?.onPraise(comment)
                }
            })
    }

    fun commentUidAdd(
        comment: Comment,
        listener: OnCommentUidAddListener
    ) {
        iBlog.commentUidAdd(
            commentId = comment.id, fromUid = comment.fromUid, contentId = comment.contentId,
            content = comment.content,
            urls = comment.urls, reply = comment.reply
        )
            .compose(CommonTransformer<Response<RespData<Total>>, Total>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Total>() {
                override fun onNext(t: Total) {
                    super.onNext(t)
//            commentUidAdd.postValue(LiveResult.success(comment))
                    listener?.onCommentAdd(total = t)
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
//            commentUidAdd.postValue(LiveResult.error(e))
                    listener?.onCommentAdd(err = e)
                }
            })
    }

    fun commentUid(
        commentId: Long,
        fromUid: Long,
        contentId: Long,
        listener: OnCommentUidListener,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.commentUid(
            commentId = commentId, fromUid = fromUid, contentId = contentId, page = page,
            pageSize = this.size
        )
            .compose(
                CommonTransformer<Response<RespData<MutableList<Comment>>>, MutableList<Comment>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Comment>>() {
                override fun onNext(t: MutableList<Comment>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
//            commentUid.postValue(LiveResult.success(t))
                    listener?.onComments(data = t)
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
//            commentUid.postValue(LiveResult.error(e))
                    listener?.onComments(err = e)
                }
            })
    }

    fun praiseCommentUidAdd(
        comment: Comment,
        listener: OnCommentPraiseListener
    ) {
        if (comment.praises == 1) {
            comment.praises = 0
            comment.praiseNum -= 1
        } else {
            comment.praises = 1
            comment.praiseNum += 1
        }
        iBlog.praiseCommentUidAdd(
            id = comment.id,
            commentUidId = comment.id,
            status = comment.praises
        )
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
//            praiseCommentUidAdd.postValue(LiveResult.success(comment))
                    listener?.onPraise(comment)
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (comment.praises == 1) {
                        comment.praises = 0
                        comment.praiseNum -= 1
                    } else {
                        comment.praises = 1
                        comment.praiseNum += 1
                    }
//            praiseCommentUidAdd.postValue(LiveResult.success(comment))
                    listener?.onPraise(comment)
                }
            })
    }

    fun topAdd(
        blog: Blog
    ) {
        iBlog.topAdd(blog.id, blog.tops)
            .compose(CommonTransformer<Response<RespData<Any>>, Any>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Any>() {
                override fun onNext(t: Any) {
                    super.onNext(t)
                    top.postValue(LiveResult.success(blog))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    top.postValue(LiveResult.error(e))
                }
            })
    }

    fun collectionAdd(
        blog: Blog,
        onBlogListener: OnBlogListener? = null
    ) {
        iBlog.collectionAdd(blog.id, blog.collections)
            .compose(CommonTransformer<Response<RespData<Any>>, Any>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Any>() {
                override fun onNext(t: Any) {
                    super.onNext(t)
                    if (onBlogListener != null) {
                        onBlogListener?.onBlog(blog)
                    } else {
                        collection.postValue(LiveResult.success(blog))
                    }
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (onBlogListener != null) {
                        onBlogListener?.onBlog(e = e)
                    } else {
                        collection.postValue(LiveResult.error(e))
                    }
                }
            })
    }

    fun followAdd(
        blog: Blog,
        from: Int = 0
    ) {
        iBlog.followAdd(uid = blog.uid, status = blog.follows)
            .compose(CommonTransformer<Response<RespData<Any>>, Any>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Any>() {
                override fun onNext(t: Any) {
                    super.onNext(t)
                    when (from) {
                        BLOG_FOLLOW_FROM_FIND -> {
                            findBlogFollow.postValue(LiveResult.success(blog))
                        }
                        else -> {
                            followAdd.postValue(LiveResult.success(blog))
                        }
                    }
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    when (from) {
                        BLOG_FOLLOW_FROM_FIND -> {
                            findBlogFollow.postValue(LiveResult.error(e))
                        }
                        else -> {
                            followAdd.postValue(LiveResult.error(e))
                        }
                    }
                }
            })
    }

    fun share(
        blogId: Long,
        status: Int? = 1,
        station: Int? = 0,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.share(blogId = blogId, status = status, station = station)
            .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<User>>() {
                override fun onNext(t: MutableList<User>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    share.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    share.postValue(LiveResult.error(e))
                }
            })
    }

    fun shareAdd(
        blog: Blog,
        fromIds: ArrayList<Long>? = null
    ) {
        blog.shareNum += 1
        LogUtil.e("blog:", blog, " | fromIds:", fromIds)
        iBlog.shareAdd(blogId = blog.id, status = 1, fromIds = fromIds.toString())
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    shareAdd.postValue(LiveResult.success(blog))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    blog.shareNum -= 1
                    shareAdd.postValue(LiveResult.success(blog))
                }
            })
    }

    fun viewAdd(
        blog: Blog,
        index: Int
    ) {
        blog.viewNum += 1
        LogUtil.e("blog:", blog)
        iBlog.viewAdd(fromId = blog.uid, blogId = blog.id, num = index)
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    viewAdd.postValue(LiveResult.success(blog))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    blog.viewNum -= 1
                    viewAdd.postValue(LiveResult.success(blog))
                }
            })
    }

    fun praiseAdd(
        blog: Blog,
        listener: OnPraiseListener? = null
    ) {
        iBlog.praiseAdd(fromId = blog.uid, blogId = blog.id, status = blog.praises)
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    blog.praiseNum = t
                    listener?.onPraise(blog)
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (blog.praises == 1) {
                        blog.praises = 0
                        blog.praiseNum -= 1
                    } else {
                        blog.praises = 1
                        blog.praiseNum += 1
                    }
                    listener?.onPraise(blog)
                }
            })
    }

    fun format() {
        iBlog.format()
            .compose(CommonTransformer<Response<RespData<MutableList<Format>>>, MutableList<Format>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Format>>() {
                override fun onNext(t: MutableList<Format>) {
                    super.onNext(t)
                    format.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    format.postValue(LiveResult.error(e))
                }
            })
    }

    fun banner(
        name: String = "",
        listener: OnWordListener
    ) {
        iBlog.banner(name)
            .compose(CommonTransformer<Response<RespData<MutableList<Word>>>, MutableList<Word>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Word>>() {
                override fun onNext(t: MutableList<Word>) {
                    super.onNext(t)
//            banner.postValue(LiveResult.success(t))
                    listener?.onWords(data = t)
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
//            banner.postValue(LiveResult.error(e))
                    listener?.onWords(err = e)
                }
            })
    }

    fun collectionBlog(
        status: Int = 1,
        uid: Long? = Resource.uid,
        blogStatus: Int = 2,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.collectionBlog(status, uid, blogStatus, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    collectionBlog.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    collectionBlog.postValue(LiveResult.error(e))
                }
            })
    }

    fun userBlog(
        status: Int = 2,
        uid: Long? = Resource.uid,
        fromUid: Long? = Resource.uid,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.userBlog(status, uid, fromUid = fromUid, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    if (context != null) {
                        t?.forEach {
                            it.urls?.forEach {
                                val height = ScreenUtil.getScreenHeight(context) / 3
                                it.randomH = height + Random.nextInt(100)
                                it.height = height
                            }
                        }
                    }
                    userBlog.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    userBlog.postValue(LiveResult.error(e))
                }
            })
    }

    fun cityBlog(
        status: Int = 2,
        mode: Int = 0,
        city: String? = Resource.user?.city,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.cityBlog(status, mode, city, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    cityBlog.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    cityBlog.postValue(LiveResult.error(e))
                }
            })
    }

    fun followBlog(
        status: Int = 1,
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.followBlog(status, uid, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    followBlog.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    followBlog.postValue(LiveResult.error(e))
                }
            })
    }

    fun recommendBlog(
        format: Int? = -1,
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.recommendBlog(format, uid, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    recommendBlog.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    recommendBlog.postValue(LiveResult.error(e))
                }
            })
    }

    fun findBlog(
        status: Int = 2,
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        context: Context? = null
    ) {
        refresh(isRefresh)
        iBlog.findBlog(status, uid, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    if (context != null) {
                        t?.forEach {
                            it.urls?.forEach {
                                val height = ScreenUtil.getScreenHeight(context) / 3
                                it.randomH = height + Random.nextInt(100)
                                it.height = height
                            }
                        }
                    }
                    findBlog.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    findBlog.postValue(LiveResult.error(e))
                }
            })
    }

    fun praiseBlog(
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        context: Context? = null
    ) {
        refresh(isRefresh)
        iBlog.praiseBlog(uid, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    if (context != null) {
                        t?.forEach {
                            it.urls?.forEach {
                                val height = ScreenUtil.getScreenHeight(context) / 3
                                it.randomH = height + Random.nextInt(100)
                                it.height = height
                            }
                        }
                    }
                    praiseBlog.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    praiseBlog.postValue(LiveResult.error(e))
                }
            })
    }

    fun browseBlog(
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        context: Context? = null
    ) {
        refresh(isRefresh)
        iBlog.browseBlog(uid, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    if (context != null) {
                        t?.forEach {
                            it.urls?.forEach {
                                val height = ScreenUtil.getScreenHeight(context) / 3
                                it.randomH = height + Random.nextInt(100)
                                it.height = height
                            }
                        }
                    }
                    browseBlog.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    browseBlog.postValue(LiveResult.error(e))
                }
            })
    }

    fun channelBlog(
        channelId: Long,
        sort: Int? = 1,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iBlog.channelBlog(channelId, sort, page = page, pageSize = this.size)
            .compose(CommonTransformer<Response<RespData<MutableList<Blog>>>, MutableList<Blog>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Blog>>() {
                override fun onNext(t: MutableList<Blog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    channelBlog.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    channelBlog.postValue(LiveResult.error(e))
                }
            })
    }

    fun update(
        blog: Blog
    ) {
        iBlog.update(blog.id, blog.status, blog.reason)
            .compose(
                CommonTransformer<Response<RespData<Int>>, Int>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    update.postValue(LiveResult.success(blog))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    update.postValue(LiveResult.error(e))
                }
            })
    }

    fun remove(blog: Blog) {
        iBlog.remove(blog.id)
            .compose(
                CommonTransformer<Response<RespData<Int>>, Int>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    OssClient.instance.delete(blog.url)
                    remove.postValue(LiveResult.success(blog))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    remove.postValue(LiveResult.error(e))
                }
            })
    }
}

interface OnBlogListener {
    fun onBlog(blog: Blog? = null, e: Exception? = null)
}

interface OnPraiseListener {
    fun onPraise(blog: Blog)
}

interface OnCommentPraiseListener {
    fun onPraise(comment: Comment)
}

interface OnWordListener {
    fun onWords(
        data: MutableList<Word>? = null,
        err: Exception? = null
    )
}

interface OnCommentUidListener {
    fun onComments(
        data: MutableList<Comment>? = null,
        err: Exception? = null
    )
}

interface OnCommentUidAddListener {
    fun onCommentAdd(
        total: Total? = null,
        err: Exception? = null
    )
}