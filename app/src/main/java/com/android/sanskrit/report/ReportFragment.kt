package com.android.sanskrit.report

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.resource.vm.report.data.Report
import com.android.resource.vm.report.data.Type
import com.android.sanskrit.R
import com.android.sanskrit.R.color
import com.android.utils.LogUtil
import com.android.widget.ZdButton
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.report_fragment.reportDesEdit
import kotlinx.android.synthetic.main.report_fragment.reportDesTips
import kotlinx.android.synthetic.main.report_fragment.reportSubmit
import kotlinx.android.synthetic.main.report_fragment.reportTag

/**
 * created by jiangshide on 2020/4/4.
 * email:18311271399@163.com
 */
class ReportFragment : MyFragment() {

  private var mType = -1
  private var report: Report? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.report_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    report = arguments?.getParcelable("data")!!
    LogUtil.e("report:", report)
    if (report == null) {
      ZdToast.txt("data is null!")
      pop()
      return
    }
    setTitle("举报-${report?.name}").setTitleColor(R.color.redLight)

    reportDesEdit.setListener { s, input ->
      report?.reason = input
      reportDesTips.text = "可输入${200 - input.length}字符"
    }

    reportVM!!.reportAdd.observe(this, Observer {
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      ZdToast.txt("举报成功!")
      ZdEvent.get()
          .with(REPORT_REFRESH)
          .post(report!!)
      pop()
    })

    reportSubmit.setOnClickListener {
      report?.status = 1
      report?.type = mType
      reportVM?.reportAdd(report!!)
      LogUtil.e("report:", report)
    }

    reportVM!!.types.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        noNet("暂无数据!").setTipsRes(R.mipmap.no_data)
        return@Observer
      }
      showView(report!!.type, it.data)
    })
    reportVM?.types()
    showLoading()
  }

  private fun showView(
    id: Int,
    data: MutableList<Type>
  ) {
    val views = ArrayList<ZdButton>()
    reportTag.removeAllViews()
    data.forEachIndexed { index, type ->
      val view = getView(R.layout.report_fragment_item)
      val zdButton = view.findViewById<ZdButton>(R.id.reportItemName)
      zdButton.text = type.name
      view.id = type.id
      if (mType == type.id || type.id == id) {
        zdButton.normalColor = R.color.blackLightMiddle
        zdButton.setTextColor(getColor(R.color.white))
      }
      reportTag.addView(view)
      views.add(zdButton)
      views[index].setOnClickListener {
        data.forEachIndexed { index, type ->
          if (type.id == it.id) {
            mType = index
            views[index].normalColor = R.color.blackLightMiddle
            views[index].setTextColor(getColor(R.color.white))
            validate()
          } else {
            views[index].normalColor = R.color.grayLight
            views[index].setTextColor(getColor(R.color.txtGray))
          }
        }
      }
    }
  }

  private fun validate() {
    val disable = this.mType == -1
    reportSubmit.isEnabled = !disable
    reportSubmit.normalColor = if (!disable) color.blackLightMiddle else color.disable
  }
}

const val REPORT_REFRESH = "reportRefresh"