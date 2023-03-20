package com.android.resource.vm.channel

import androidx.lifecycle.MutableLiveData
import com.android.base.vm.BaseVM
import com.android.http.Http
import com.android.http.exception.HttpException
import com.android.http.observer.BaseObserver
import com.android.http.transformer.CommonTransformer
import com.android.http.vm.LiveResult
import com.android.http.vm.data.RespData
import com.android.resource.Resource
import com.android.resource.vm.blog.OnWordListener
import com.android.resource.vm.channel.data.*
import com.android.resource.vm.channel.remote.ChannelRemote
import com.android.utils.ScreenUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import kotlin.random.Random

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class ChannelVM : BaseVM() {
    private val iChannel: ChannelRemote = Http.createService(ChannelRemote::class.java)

    var followAdd: MutableLiveData<LiveResult<Channel>> = MutableLiveData()
    var channelFollow: MutableLiveData<LiveResult<MutableList<Channel>>> = MutableLiveData()
    var channelRecommend: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    var channelTypes: MutableLiveData<LiveResult<ArrayList<ChannelType>>> = MutableLiveData()
    var channelNature: MutableLiveData<LiveResult<MutableList<ChannelNature>>> = MutableLiveData()
    var createChannel: MutableLiveData<LiveResult<Long>> = MutableLiveData()
    var channel: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    val channelUser: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    val channelOfficial: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    val channelType: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    var search: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    var wordAdd: MutableLiveData<LiveResult<Int>> = MutableLiveData()
    var channelId: MutableLiveData<LiveResult<Channel>> = MutableLiveData()
    var follow: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()

    fun follow(
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iChannel.follow(page = page, pageSize = this.size)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    follow.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    follow.postValue(LiveResult.error(e))
                }
            })
    }

    fun channelId(
        id: Long?,
        uid: Long?,
        blogId: Long
    ) {
        iChannel.channelId(id, uid, blogId)
            .compose(
                CommonTransformer<Response<RespData<Channel>>, Channel>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Channel>() {
                override fun onNext(t: Channel) {
                    super.onNext(t)
                    channelId.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    channelId.postValue(LiveResult.error(e))
                }
            })
    }

    fun word(
        uid: Long? = Resource.uid,
        source: Int = 0,
        listener: OnWordListener
    ) {
        iChannel.word(uid, source)
            .compose(
                CommonTransformer<Response<RespData<MutableList<Word>>>, MutableList<Word>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Word>>() {
                override fun onNext(t: MutableList<Word>) {
                    super.onNext(t)
                    listener?.onWords(data = t)
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    listener?.onWords(err = e)
                }
            })
    }

    fun search(
        word: String,
        uid: Long? = Resource.uid,
        source: Int? = 2
    ) {
        iChannel.search(word, uid, source, page = page, pageSize = this.size)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    search.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    search.postValue(LiveResult.error(e))
                }
            })
    }

    fun channel(
        status: Int = 2,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iChannel.channel(status, page = page, pageSize = this.size)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    channel.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    channel.postValue(LiveResult.error(e))
                }
            })
    }

    fun channelOfficial(
        official: Int? = 1,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iChannel.channelOfficial(official, page = page, pageSize = this.size)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    channelOfficial.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    channelOfficial.postValue(LiveResult.error(e))
                }
            })
    }

//  fun channelId(
//    status: Int = 2,
//    id: Long? = 1,
//    isRefresh: Boolean = true
//  ) {
//    refresh(isRefresh)
//    iChannel.channelId(status, id, page = page, pageSize = this.size)
//        .compose(
//            CommonTransformer<Response<RespData<MutableList<Channel>>>, MutableList<Channel>>()
//        )
//        .subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(object : BaseObserver<MutableList<Channel>>() {
//          override fun onNext(t: MutableList<Channel>) {
//            super.onNext(t)
//            if (!isRefresh && (t == null || t == null || t.size == 0)) {
//              page--
//            }
//            channelId.postValue(LiveResult.success(t))
//          }
//
//          override fun onFail(e: HttpException) {
//            super.onFail(e)
//            if (!isRefresh && page > 1) {
//              page--
//            }
//            channelId.postValue(LiveResult.error(e))
//          }
//        })
//  }

    fun channelType(
        status: Int = 2,
        id: Int? = 1,
        name: String = "",
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iChannel.channelType(status, id, name = name, page = page, pageSize = this.size)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    t?.forEach {
                        if (it.blog != null) {
                            it.blog!!.urls?.forEach {
                                val height = ScreenUtil.getScreenHeight(context) / 3
                                it.randomH = height + Random.nextInt(100)
                                it.height = height
                            }
                        }
                    }
                    channelType.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    channelType.postValue(LiveResult.error(e))
                }
            })
    }

    fun channelUser(
        status: Int = 2,
        uid: Long? = Resource.uid,
        fromUid:Long?=Resource.uid,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iChannel.channelUser(status, uid, fromUid,page = page, pageSize = this.size)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    channelUser.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    channelUser.postValue(LiveResult.error(e))
                }
            })
    }

    fun createChannel(
        id: Long? = 0,
        uid: Long? = Resource.uid,
        channelNatureId: Int,
        name: String,
        cover: String,
        des: String
    ) {
        iChannel.createChannel(id, uid, channelNatureId, name, cover, des)
            .compose(CommonTransformer<Response<RespData<Long>>, Long>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Long>() {
                override fun onNext(t: Long) {
                    super.onNext(t)
                    createChannel.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    createChannel.postValue(LiveResult.error(e))
                }
            })
    }

    fun channelRecommend(
        status: Int = 1,
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true
    ) {
        refresh(isRefresh)
        iChannel.channelRecommend(status, uid, page, size)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    if (!isRefresh && (t == null || t == null || t.size == 0)) {
                        page--
                    }
                    channelRecommend.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    if (!isRefresh && page > 1) {
                        page--
                    }
                    channelRecommend.postValue(LiveResult.error(e))
                }
            })
    }

    fun channelTypes(uid: Long) {
        iChannel.channelType(uid)
            .compose(
                CommonTransformer<Response<RespData<ArrayList<ChannelType>>>, ArrayList<ChannelType>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<ArrayList<ChannelType>>() {
                override fun onNext(t: ArrayList<ChannelType>) {
                    super.onNext(t)
                    channelTypes.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    channelTypes.postValue(LiveResult.error(e))
                }
            })
    }

    fun channelNature() {
        iChannel.channelNature(Resource.uid)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelNature>>>, MutableList<ChannelNature>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelNature>>() {
                override fun onNext(t: MutableList<ChannelNature>) {
                    super.onNext(t)
                    channelNature.postValue(LiveResult.success(t))
                }

                override fun onFail(e: HttpException) {
                    super.onFail(e)
                    channelNature.postValue(LiveResult.error(e))
                }
            })
    }
}