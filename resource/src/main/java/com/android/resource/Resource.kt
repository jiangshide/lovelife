package com.android.resource

import com.android.http.Http
import com.android.jpush.JPush
import com.android.resource.vm.blog.data.Format
import com.android.resource.vm.publish.data.Publish
import com.android.resource.vm.user.data.User
import com.android.utils.AppUtil
import com.android.utils.LogUtil
import com.android.utils.SPUtil

/**
 * created by jiangshide on 2019-12-05.
 * email:18311271399@163.com
 */
object Resource {
  private val IS_FIRST = "isFirst"

  var UID = "uid"
  var USER = "user"
  var USERS = "users"
  var NAME = "name"
  var ICON = "icon"
  var PUBLISH = "publish"
  var FORMAT = "format"

  var TAB_INDEX = 0

  var format: MutableList<Format>?
    get() = SPUtil.getList(FORMAT, Format::class.java)
    set(format) {
      SPUtil.put(FORMAT, format)
    }

  var uid: Long?
    get() = SPUtil.getLong(UID)
    set(uid) {
      SPUtil.putLong(UID, uid!!)
    }

  var user: User?
    get() = SPUtil.get(USER, User::class.java)
    set(user) {
      uid = user!!.id
      icon = user!!.icon!!
      SPUtil.put(USER, user)
//      JPush().setAlias(AppUtil.getApplicationContext(),uid!!.toInt(),user!!.unionId!!)
      JPush().setAlias(AppUtil.getApplicationContext(),uid!!.toInt(),"$uid")
    }

  var users: ArrayList<User>?
    get() = SPUtil.getList(USERS,User::class.java) as ArrayList<User>?
    set(users) {
      SPUtil.put(USERS,users)
    }

  var name: String
    get() = SPUtil.getString(NAME)
    set(name) {
      LogUtil.e("name:", name)
      SPUtil.putString(NAME, name)
    }

  var icon: String
    get() = SPUtil.getString(ICON)
    set(icon) {
      LogUtil.e("icon", icon)
      SPUtil.putString(ICON, icon)
    }

  var first: Boolean
    get() = SPUtil.getBoolean(IS_FIRST,true)
    set(first) {
      SPUtil.putBoolean(IS_FIRST, first)
    }

  var publish: Publish?
    get() = SPUtil.get(PUBLISH, Publish::class.java)
    set(publish) {
      SPUtil.put(PUBLISH, publish)
    }

  fun clearPublish() {
    SPUtil.clear(PUBLISH)
  }

  fun clearUser() {
    SPUtil.clear(UID)
    SPUtil.clear(USER)
    SPUtil.clear(Http.TOKEN)
    clearPublish()
  }

}

const val MEN = 1
const val WOMEN = 2

const val NAME_SIZE = 8

const val BLOG_FOLLOW = 1//关注
const val BLOG_RECOMMEND = 2//推荐
const val BLOG_HOT = 3//热门
const val BLOG_NEW = 4//最新
const val BLOG_CIRCLE = 5//圈子
const val BLOG_CHANNEL = 6//频道
const val BLOG_MY = 7//我的
const val BLOG_RECYCLER = 8//记忆箱

const val REFRESH_MIN="refreshMine"
const val REFRESH_PROFILE = "refreshProfile"
const val REFRESH_MEMORY = "refreshMemory"
const val REFRESH_MINE = "refreshMine"

const val FINISH_VALIDATE = "finishValidate"

const val FINISH_USERCODE = "finishUserCode"

//审核状态:0~未审核,1~审核中,2~审核通过,3移到回忆箱,-1~审核拒绝,-2~禁言,-3~关闭/折叠
const val AUDIT = 0
const val AUDIT_UNDER = 1
const val AUDIT_PASS = 2
const val RECYCLE = 3
const val AUDIT_REJECTED = -1
const val FORBIDDEN_WORDS = -2
const val FOLD = -3

const val CHANNEL_PRIVATE = 1//私有频道
const val CHANNEL_COMMON = 2//共有频道

const val CHANNEL_FOLLOW_CANCEL = -1
const val CHANNEL_FOLLOW_RECOMMEND = 0//推荐关注
const val CHANNEL_FOLLOW = 1 //关注

const val CONTENT_FROM_USER = 1//来自用户
const val CONTENT_FROM_CHANNEL = 2//来自频道
const val CONTENT_FROM_BLOG = 3//来自动态
const val CONTENT_FROM_COMMENT = 4//来自评论

const val BLOG_FOLLOW_FROM_FIND = 1//首页动态发现~关注

const val UNFOLLOW=0
const val FOLLOW=1
const val ESPECIALLY=2

const val ON=0
const val OFF=-1

const val VIDEO_COVER = "?x-oss-process=video/snapshot,t_10000,m_fast/auto-orient,1"//
const val IMG = "?x-oss-process=image/resize,m_fill,h_100,w_200"//缩略图
const val ROTATION = "?x-oss-process=image/resize,w_100/auto-orient,0"//旋转










