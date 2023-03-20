package com.android.resource.vm.user

import androidx.lifecycle.MutableLiveData
import com.android.base.vm.BaseVM
import com.android.http.Http
import com.android.http.exception.HttpException
import com.android.http.observer.BaseObserver
import com.android.http.transformer.CommonTransformer
import com.android.http.vm.LiveResult
import com.android.http.vm.data.RespData
import com.android.location.ZdLocation
import com.android.resource.Resource
import com.android.resource.vm.user.data.App
import com.android.resource.vm.user.data.Certification
import com.android.resource.vm.user.data.Course
import com.android.resource.vm.user.data.Follow
import com.android.resource.vm.user.data.Friend
import com.android.resource.vm.user.data.Order
import com.android.resource.vm.user.data.Profile
import com.android.resource.vm.user.data.User
import com.android.resource.vm.user.remote.UserRemote
import com.android.utils.AppUtil
import com.android.utils.LogUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class UserVM : BaseVM() {

  private val iUser: UserRemote = Http.createService(UserRemote::class.java)
  var forgetPsw: MutableLiveData<LiveResult<Boolean>> = MutableLiveData()
  var reg: MutableLiveData<LiveResult<User>> = MutableLiveData()
  var login: MutableLiveData<LiveResult<User>> = MutableLiveData()
  var bind: MutableLiveData<LiveResult<User>> = MutableLiveData()
  var validate: MutableLiveData<LiveResult<String>> = MutableLiveData()
  var userExit: MutableLiveData<LiveResult<Boolean>> = MutableLiveData()
  var profile: MutableLiveData<LiveResult<Profile>> = MutableLiveData()
  var remarks: MutableLiveData<LiveResult<Long>> = MutableLiveData()
  var profileUid: MutableLiveData<LiveResult<User>> = MutableLiveData()
  var onLine: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
  var users: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
  var appUpdate: MutableLiveData<LiveResult<App>> = MutableLiveData()
  var praise: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
  var course: MutableLiveData<LiveResult<MutableList<Course>>> = MutableLiveData()
  var friend: MutableLiveData<LiveResult<MutableList<Friend>>> = MutableLiveData()
  var friendAdd: MutableLiveData<LiveResult<Long>> = MutableLiveData()
  var follow: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
  var fans: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
  var view: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
  var certification: MutableLiveData<LiveResult<Certification>> = MutableLiveData()
  var certificationUpdate: MutableLiveData<LiveResult<Certification>> = MutableLiveData()
  var certificationList: MutableLiveData<LiveResult<MutableList<Certification>>> = MutableLiveData()
  var pay: MutableLiveData<LiveResult<Order>> = MutableLiveData()

  fun pay(
    source: Int? = 1,
    uid: Long? = Resource.uid,
    body: String? = AppUtil.getPackageName() + "-账号充值",
    fee: Int? = 500,
    receipt: String? = ""
  ) {
    iUser.pay(source, uid, body, fee, receipt)
        .compose(
            CommonTransformer<Response<RespData<Order>>, Order>()
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<Order>() {
          override fun onNext(t: Order) {
            super.onNext(t)
            pay.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            pay.postValue(LiveResult.error(e))
          }
        })
  }

  fun certification() {
    iUser.certification()
        .compose(
            CommonTransformer<Response<RespData<Certification>>, Certification>()
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<Certification>() {
          override fun onNext(t: Certification) {
            super.onNext(t)
            certification.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            certification.postValue(LiveResult.error(e))
          }
        })
  }

  fun certificationList(
    status: Int? = 1,
    isRefresh: Boolean = true
  ) {
    refresh(isRefresh)
    iUser.certificationList(status, page, size)
        .compose(
            CommonTransformer<Response<RespData<MutableList<Certification>>>, MutableList<Certification>>()
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<MutableList<Certification>>() {
          override fun onNext(t: MutableList<Certification>) {
            super.onNext(t)
            if (!isRefresh && (t == null || t == null || t.size == 0)) {
              page--
            }
            certificationList.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            if (!isRefresh && page > 1) {
              page--
            }
            certificationList.postValue(LiveResult.error(e))
          }
        })
  }

  fun certificationUpdate(cert: Certification) {
    iUser.certificationUpdate(cert.toJson()!!)
        .compose(CommonTransformer<Response<RespData<Int>>, Int>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<Int>() {
          override fun onNext(t: Int) {
            super.onNext(t)
            certificationUpdate.postValue(LiveResult.success(cert))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            certificationUpdate.postValue(LiveResult.error(e))
          }
        })
  }

  fun followAdd(
    uid:Long,status: Int,listener:OnFollowListener
  ) {
    iUser.followAdd( uid=uid, status = status)
        .compose(
            CommonTransformer<Response<RespData<Int>>, Int>()
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<Int>() {
          override fun onNext(id: Int) {
            super.onNext(status)
              listener?.follow(status,null)
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            listener?.follow(-1,e)
          }
        })
  }

  fun follow(
    action:Int=0,
    isRefresh: Boolean = true
  ) {
    refresh(isRefresh)
    iUser.follow(action,page = page, pageSize = size)
        .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<MutableList<User>>() {
          override fun onNext(t: MutableList<User>) {
            super.onNext(t)
            if (!isRefresh && (t == null || t == null || t.size == 0)) {
              page--
            }
            follow.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            if (!isRefresh && page > 1) {
              page--
            }
            follow.postValue(LiveResult.error(e))
          }
        })
  }

  fun fans(
    isRefresh: Boolean = true
  ) {
    refresh(isRefresh)
    iUser.fans(page = page, pageSize = size)
        .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<MutableList<User>>() {
          override fun onNext(t: MutableList<User>) {
            super.onNext(t)
            if (!isRefresh && (t == null || t == null || t.size == 0)) {
              page--
            }
            fans.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            if (!isRefresh && page > 1) {
              page--
            }
            fans.postValue(LiveResult.error(e))
          }
        })
  }

  fun view(
    blogId: Long,
    isRefresh: Boolean = true
  ) {
    refresh(isRefresh)
    iUser.view(blogId = blogId)
        .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<MutableList<User>>() {
          override fun onNext(t: MutableList<User>) {
            super.onNext(t)
            if (!isRefresh && (t == null || t == null || t.size == 0)) {
              page--
            }
            view.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            if (!isRefresh && page > 1) {
              page--
            }
            view.postValue(LiveResult.error(e))
          }
        })
  }

  fun friendAdd(
    uid: Long,
    fromId: Long? = Resource.uid,
    status: Int = 1,
    reason: String? = "",
    url: String? = ""
  ) {
    iUser.friendAdd(uid=uid, fromId = fromId, status = status, reason = reason, url = url)
        .compose(CommonTransformer<Response<RespData<Long>>, Long>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<Long>() {
          override fun onNext(t: Long) {
            super.onNext(t)
            friendAdd.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            friendAdd.postValue(LiveResult.error(e))
          }
        })
  }

  fun friend(
    fromId: Long? = Resource.uid,
    status: Int? = 1,
    isRefresh: Boolean = true
  ) {
    refresh(isRefresh)
    iUser.friend(fromId, status, page, size)
        .compose(CommonTransformer<Response<RespData<MutableList<Friend>>>, MutableList<Friend>>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<MutableList<Friend>>() {
          override fun onNext(t: MutableList<Friend>) {
            super.onNext(t)
            if (!isRefresh && (t == null || t == null || t.size == 0)) {
              page--
            }
            friend.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            if (!isRefresh && page > 1) {
              page--
            }
            friend.postValue(LiveResult.error(e))
            LogUtil.e("---------isRefresh:",isRefresh)
          }
        })
  }

  fun praise(
    blogId: Long,
    status: Int? = 1,
    isRefresh: Boolean = true
  ) {
    refresh(isRefresh)
    iUser.praise(blogId = blogId, status = status)
        .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<MutableList<User>>() {
          override fun onNext(t: MutableList<User>) {
            super.onNext(t)
            if (!isRefresh && (t == null || t == null || t.size == 0)) {
              page--
            }
            praise.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            if (!isRefresh && page > 1) {
              page--
            }
            praise.postValue(LiveResult.error(e))
          }
        })
  }

  fun course(
    uid: Long,
    isRefresh: Boolean = true
  ) {
    refresh(isRefresh)
    iUser.course(uid, page, size)
        .compose(CommonTransformer<Response<RespData<MutableList<Course>>>, MutableList<Course>>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<MutableList<Course>>() {
          override fun onNext(t: MutableList<Course>) {
            super.onNext(t)
            if (!isRefresh && (t == null || t == null || t.size == 0)) {
              page--
            }
            course.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            if (!isRefresh && page > 1) {
              page--
            }
            course.postValue(LiveResult.error(e))
          }
        })
  }

  fun appUpdate(
    name: String? = null,
    platform: Int?,
    version: String?,
    code: Int?
  ) {
    iUser.appUpdate(name, platform, version, code)
        .compose(CommonTransformer<Response<RespData<App>>, App>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<App>() {
          override fun onNext(t: App) {
            super.onNext(t)
            appUpdate.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            appUpdate.postValue(LiveResult.error(e))
          }
        })
  }

  fun users(
    status: Int = 2,
    name:String?="",
    isRefresh: Boolean = true
  ) {
    refresh(isRefresh)
    iUser.users(status=status,name = name, page=page, pageSize=size)
        .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<MutableList<User>>() {
          override fun onNext(t: MutableList<User>) {
            super.onNext(t)
            if (!isRefresh && (t == null || t == null || t.size == 0)) {
              page--
            }
            users.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            if (!isRefresh && page > 1) {
              page--
            }
            users.postValue(LiveResult.error(e))
          }
        })
  }

  fun onLine(
    status: Int = 2,
    online: Int = 1,
    isRefresh: Boolean = true
  ) {
    refresh(isRefresh)
    iUser.oneLine(status, online, page, size)
        .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<MutableList<User>>() {
          override fun onNext(t: MutableList<User>) {
            super.onNext(t)
            if (!isRefresh && (t == null || t == null || t.size == 0)) {
              page--
            }
            onLine.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            if (!isRefresh && page > 1) {
              page--
            }
            onLine.postValue(LiveResult.error(e))
          }
        })
  }

  fun profile(
      fromId: Long? = Resource.uid,
      uid: Long? = Resource.uid
  ) {
    iUser.profiles(uid, fromId)
        .compose(CommonTransformer<Response<RespData<User>>, User>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<User>() {
          override fun onNext(t: User) {
            super.onNext(t)
            profileUid.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            profileUid.postValue(LiveResult.error(e))
          }
        })
  }

  fun profile(
    p: Profile?,
    type: Int = 0
  ) {
    iUser.profile(p?.getGson()!!, type)
        .compose(CommonTransformer<Response<RespData<Profile>>, Profile>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<Profile>() {
          override fun onNext(t: Profile) {
            super.onNext(t)
            val user = Resource.user
            user?.set(t)
            Resource.user = user
            profile.postValue(LiveResult.success(user))
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            profile.postValue(LiveResult.error(e))
          }
        })
  }

  fun remarks(
    uid: Long,
    name: String,
    url: String? = ""
  ) {
    iUser.remarks(uid = uid, name = name, url = url)
        .compose(CommonTransformer<Response<RespData<Long>>, Long>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<Long>() {
          override fun onNext(t: Long) {
            super.onNext(t)
            remarks.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            remarks.postValue(LiveResult.error(e))
          }
        })
  }

  fun userExit(userName: String) {
    iUser.userExit(userName)
        .compose(CommonTransformer<Response<RespData<Boolean>>, Boolean>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<Boolean>() {
          override fun onNext(t: Boolean) {
            super.onNext(t)
            LogUtil.e("t:", t)
            userExit.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            LogUtil.e("e", e)
            userExit.postValue(LiveResult.error(e))
          }
        })
  }

  fun forgetPsw(
    userName: String,
    password: String,
    validateCode: String
  ) {
    iUser.forgetPsw(userName, md5(password), validateCode)
        .compose(CommonTransformer<Response<RespData<Boolean>>, Boolean>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<Boolean>() {
          override fun onNext(t: Boolean) {
            super.onNext(t)
            forgetPsw.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            forgetPsw.postValue(LiveResult.error(e))
          }
        })
  }

  fun reg(
    userName: String,
    password: String,
    validateCode: String,
    country: String,
    icon: String,
    sex: Int,
    netInfo: String,
    device: String
  ) {
    iUser.reg(userName, md5(password), validateCode, country, icon, sex, netInfo, device)
        .compose(CommonTransformer<Response<RespData<User>>, User>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<User>() {
          override fun onNext(t: User) {
            super.onNext(t)
            reg.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            reg.postValue(LiveResult.error(e))
          }
        })
  }

  fun login(
    userName: String,
    password: String,
    netInfo: String,
    device: String
  ) {
    iUser.login(userName, md5(password), netInfo, device)
        .compose(CommonTransformer<Response<RespData<User>>, User>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<User>() {
          override fun onNext(t: User) {
            super.onNext(t)
            login.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            login.postValue(LiveResult.error(e))
          }
        })
  }

  fun bind(
    id: Long? = Resource.uid,
    name: String,
    password: String,
    netInfo: String,
    device: String
  ) {
    iUser.bind(id, name, md5(password), netInfo, device)
        .compose(CommonTransformer<Response<RespData<User>>, User>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<User>() {
          override fun onNext(t: User) {
            super.onNext(t)
            bind.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            bind.postValue(LiveResult.error(e))
          }
        })
  }

  fun weChat(
    code: String,
    netInfo: String,
    device: String
  ) {
    iUser!!.weChat(code, netInfo, device)
        .compose(CommonTransformer<Response<RespData<User>>, User>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<User>() {
          override fun onNext(t: User) {
            login.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException) {
            super.onFail(e)
            login.postValue(LiveResult.error(e))
          }
        })
  }

//  fun qqLogin(
//    context: Context,
//    accessToken: String,
//    openid: String
//  ) {
//    iUser!!.qqLogin(BuildConfig.QQ_ID, accessToken, openid)
//        .compose(CommonTransformer<Response<RespData<User>>, User>())
//        .subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(object : BaseObserver<User>(context) {
//          override fun onNext(t: User) {
//            login.postValue(LiveResult.success(t))
//          }
//
//          override fun onFail(e: HttpException) {
//            super.onFail(e)
//            login.postValue(LiveResult.error(e))
//          }
//        })
//  }

  fun validate(userName: String) {
    iUser.validate(userName)
        .compose(CommonTransformer<Response<RespData<String>>, String>())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BaseObserver<String>() {
          override fun onNext(t: String) {
            super.onNext(t)
            validate.postValue(LiveResult.success(t))
          }

          override fun onFail(e: HttpException?) {
            super.onFail(e)
            validate.postValue(LiveResult.error(e))
          }
        })
  }

}

interface OnFollowListener{
  fun follow(status: Int,e: HttpException?)
}