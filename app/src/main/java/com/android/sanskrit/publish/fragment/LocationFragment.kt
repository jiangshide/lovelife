package com.android.sanskrit.publish.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.android.event.ZdEvent
import com.android.location.ZdLocation
import com.android.location.ZdLocation.Errors
import com.android.location.listener.IPoiSearchListener
import com.android.refresh.api.RefreshLayout
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.utils.LogUtil
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.publish_location_fragment.locationCancel
import kotlinx.android.synthetic.main.publish_location_fragment.locationEdit
import kotlinx.android.synthetic.main.publish_location_fragment.locationRecycleView
import kotlinx.android.synthetic.main.publish_location_fragment_item.view.locationItemDes
import kotlinx.android.synthetic.main.publish_location_fragment_item.view.locationItemName
import java.util.ArrayList

/**
 * created by jiangshide on 2020/3/26.
 * email:18311271399@163.com
 */
class LocationFragment : MyFragment(), IPoiSearchListener {

  private var adapter: KAdapter<PoiItem>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.publish_location_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    locationEdit.setListener { s, input ->
      ZdLocation.getInstance()
          .searchPOI(activity?.applicationContext, input, "", this)
    }
    locationCancel.setOnClickListener {
      pop()
    }
    ZdLocation.getInstance()
        .searchNearPOI(mActivity, page, 100, this)
    showLoading()
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    page = 0
    ZdLocation.getInstance()
        .searchNearPOI(mActivity, page, 20, this)
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    super.onLoadMore(refreshLayout)
    page++
    ZdLocation.getInstance()
        .searchNearPOI(mActivity, page, 20, this)
  }

  private fun showView(data: ArrayList<PoiItem>) {
    if (adapter != null) {
      adapter?.add(data)
      return
    }
    adapter = locationRecycleView.create(data, R.layout.publish_location_fragment_item, {
      locationItemName.text = it.title
      locationItemDes.text = it.snippet
    }, {
      ZdEvent.get()
          .with(LOCATION)
          .post(this)
      pop()
    })

  }

  override fun onPoiResult(poiResult: PoiResult?) {
    cancelRefresh()
    hiddle()
    showView(poiResult!!.pois)
    LogUtil.e("size:", poiResult.pois.size)
  }

  override fun onError(errors: Errors?) {
    cancelRefresh()
    hiddle()
    LogUtil.e("errors:", errors)
    if (!isRefesh) {
      page--
    }
    if (adapter == null || adapter?.datas() == null || adapter!!.datas().size == 0) {
      noData()
    }
  }
}

const val LOCATION = "location"