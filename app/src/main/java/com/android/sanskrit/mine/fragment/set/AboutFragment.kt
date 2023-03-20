package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.sanskrit.BuildConfig
import com.android.sanskrit.R
import com.android.resource.MyFragment
import com.android.utils.AppUtil
import com.android.utils.SystemUtil
import com.android.widget.ZdToast
import com.android.widget.adapter.create
import com.android.widget.extension.setDrawableRight
import com.android.widget.recycleview.divider.CommonItemDecoration
import kotlinx.android.synthetic.main.mine_set_about_fragment.*
import kotlinx.android.synthetic.main.mine_set_about_fragment_item.view.aboutItem
import kotlinx.android.synthetic.main.mine_set_about_fragment_item.view.aboutItemDes

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class AboutFragment : MyFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_about_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    aboutVersion.text = "Version ${AppUtil.getAppVersionName()}"

    aboutRecycleView.create(
        arrayListOf("功能介绍", "帮助与反馈", "检查版本","商务合作"), R.layout.mine_set_about_fragment_item, {
      aboutItem.text = it
      if(it == "商务合作"){
        aboutItemDes.text = "18311271399"
        aboutItemDes.setDrawableRight(R.drawable.alpha)
      }else{
        aboutItemDes.text = ""
        aboutItemDes.setDrawableRight(R.mipmap.arrow)
      }
    }, {
      when (this) {
        "功能介绍" -> {
          openUrl(BuildConfig.FUNCTION_INTRODUCE,"功能介绍")
        }
        "帮助与反馈" -> {
         push(FeedbackFragment().setTitle("帮助与反馈"))
        }
        "检查版本" -> {
          ZdToast.txt("暂无更新")
        }
        "商务合作"->{
          SystemUtil.call(mActivity,"18311271399")
        }
      }
    })

    aboutEmail.setOnClickListener {
      SystemUtil.sendEmail(context,"18311271399@163.com")
    }

    aboutSoftProtocol.setOnClickListener {
      openUrl(BuildConfig.PRIVACY_AGREEMENT,"软件许可及服务协议")
    }
    aboutPrivacyProtocol.setOnClickListener {
      openUrl(BuildConfig.USE_AGREEMENT,"隐私保护指引")
    }
  }
}