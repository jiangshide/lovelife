package com.android.resource.vm.message.data

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class Follow {
  var name:String?=null
  var url:String?="https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2777277375,4094746218&fm=11&gp=0.jpg"
  var mark:String?=null
  var isFollow:Boolean?=false

  private val names = arrayListOf<String>("jankey","just","test","jiusdsd","sddsadsasddsadsasddsadsasddsadsasddsadsasddsadsasddsadsasddsadsasddsadsasddsadsasddsadsasddsadsasddsadsasddsadsa","cdsdaa","zhangshan","李天","手机电视手机电视手机电视手机电视手机电视手机电视手机电视手机电视手机电视手机电视手机电视手机电视手机电视手机电视")
  fun arr():MutableList<Follow>{
    val list = arrayListOf<Follow>()
    names.forEach {
      val follow = Follow()
      follow.name = it
      list.add(follow)
    }
    return list
  }
}