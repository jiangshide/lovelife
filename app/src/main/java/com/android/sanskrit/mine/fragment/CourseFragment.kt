package com.android.sanskrit.mine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.resource.vm.user.data.Course
import com.android.sanskrit.R
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.mine_course_fragment.courseRecycleView
import kotlinx.android.synthetic.main.mine_course_fragment_item.view.courseItemDate
import kotlinx.android.synthetic.main.mine_course_fragment_item.view.courseItemName

/**
 * created by jiangshide on 2020/5/3.
 * email:18311271399@163.com
 */
class CourseFragment(private val uid: Long) : MyFragment() {

  private var adapter: KAdapter<Course>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_course_fragment, true, true)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    userVM!!.course.observe(this, Observer {
      hiddle()
      cancelRefresh()
      if (it.error != null) {
        if (adapter == null || adapter!!.count() == 0) {
          noNet("暂无频道!").setTipsRes(R.mipmap.no_data)
        } else if (blogVM!!.isRefresh) {
          adapter?.clear()
          noNet("暂无频道!").setTipsRes(R.mipmap.no_data)
        }
        return@Observer
      }
      showView(it.data)
    })
    userVM?.course(uid)
    showLoading()
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    userVM?.course(uid)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    userVM?.course(uid, false)
  }

  private fun showView(data: MutableList<Course>) {
    enableLoadMore(data.size == userVM?.size)
    if (adapter != null) {
      adapter?.add(data, userVM!!.isRefresh)
      return
    }
    adapter = courseRecycleView.create(data, R.layout.mine_course_fragment_item, {
      courseItemName.text = it.name
      courseItemDate.text = it.date
    }, {})
  }
}