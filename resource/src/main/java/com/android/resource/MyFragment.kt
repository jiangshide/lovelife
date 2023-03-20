package com.android.resource

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.android.base.BaseFragment
import com.android.base.WebActivity
import com.android.files.view.transferee.loader.GlideImageLoader
import com.android.files.view.transferee.transfer.TransferConfig
import com.android.files.view.transferee.transfer.Transferee
import com.android.resource.vm.blog.BlogVM
import com.android.resource.vm.channel.ChannelVM
import com.android.resource.vm.home.HomeVM
import com.android.resource.vm.message.MessageVM
import com.android.resource.vm.publish.PublishVM
import com.android.resource.vm.report.ReportVM
import com.android.resource.vm.user.UserVM
import com.android.utils.AppUtil
import com.android.utils.SystemUtil

//import com.android.web.Web

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
open class MyFragment : BaseFragment() {

  public var transferee: Transferee? = null
  public var config: TransferConfig? = null

  var homeVM: HomeVM? = null
  var channelVM: ChannelVM? = null
  var publishVM: PublishVM? = null
  var rankVM: MessageVM? = null
  var userVM: UserVM? = null
  var blogVM: BlogVM? = null

  var reportVM: ReportVM? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    homeVM = ViewModelProviders.of(this)
        .get(HomeVM::class.java)
    channelVM = ViewModelProviders.of(this)
        .get(ChannelVM::class.java)
    publishVM = ViewModelProviders.of(this)
        .get(PublishVM::class.java)
    rankVM = ViewModelProviders.of(this)
        .get(MessageVM::class.java)
    userVM = ViewModelProviders.of(this)
        .get(UserVM::class.java)
    blogVM = ViewModelProviders.of(this)
        .get(BlogVM::class.java)
    reportVM = ViewModelProviders.of(this)
        .get(ReportVM::class.java)
    transferee = Transferee.getDefault(mActivity)
  }

//  open fun setStatusBar(isShow: Boolean) {
//    if (mActivity != null && mActivity.mZdTab != null) {
//      mActivity.mZdTab.showStatusBar(isShow)
//    }
//  }
//
//  open fun setNavigationBar(isShow: Boolean) {
//    if (mActivity != null && mActivity.mZdTab != null) {
//      mActivity.mZdTab.showNavigationBar(isShow)
//    }
//  }

  override fun noData(): MyFragment {
    setTipsRes(R.mipmap.no_data)
    super.noData()
    return this
  }

  override fun noData(tips: String?): BaseFragment {
    setTipsRes(R.mipmap.no_data)
    return super.noData(tips)
  }

  override fun noData(res: Int): BaseFragment {
    setTipsRes(R.mipmap.no_data)
    return super.noData(res)
  }

  override fun noNet(): MyFragment {
    setTipsRes(R.mipmap.no_net)
    super.noNet()
    return this
  }

  fun openUrl(url: String) {
    openUrl(url, "web")
  }

  fun openUrl(
    url: String,
    title: String
  ) {
//    Web().setUrl(url)
//        .open(any)

    val bundle = Bundle()
    bundle.putString("title",title)
    bundle.putString("url",url)
    AppUtil.goActivity(WebActivity::class.java,bundle)
  }

  fun openUrl(
    url: String,
    isRemote: Boolean
  ) {
    openUrl(url, isRemote, this)
  }

  fun openUrl(
    url: String,
    isRemote: Boolean,
    any: Any
  ) {
//    Web().setUrl(url, isRemote)
//        .open(any)
  }

  fun viewImg(url: String) {
    transferee?.apply(
        TransferConfig.build()
            .setImageLoader(GlideImageLoader.with(AppUtil.getApplicationContext()))
            .setNowThumbnailIndex(0)
            .setSourceImage(url)
            .create()
    )
        ?.show()
  }
}