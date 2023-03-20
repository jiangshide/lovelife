package com.android.sanskrit.blog.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.OnMarkerDragListener
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.UiSettings
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.particle.ParticleOverlay
import com.amap.api.maps.model.particle.ParticleOverlayOptions
import com.amap.api.maps.model.particle.ParticleOverlayOptionsFactory
import com.amap.api.services.weather.LocalWeatherForecastResult
import com.amap.api.services.weather.LocalWeatherLiveResult
import com.amap.api.services.weather.WeatherSearch
import com.amap.api.services.weather.WeatherSearch.OnWeatherSearchListener
import com.amap.api.services.weather.WeatherSearchQuery
import com.android.img.Img
import com.android.img.listener.IBitmapListener
import com.android.resource.FOLLOW
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.sanskrit.mine.MineFragment
import com.android.utils.ImgUtil
import com.android.utils.LogUtil
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.blog_calculate_distance_fragment.*
import java.util.ArrayList
import java.util.Hashtable

/**
 * created by jiangshide on 2020/5/16.
 * email:18311271399@163.com
 */
class CalculateDistanceFragment : MyFragment(), OnMarkerDragListener, OnWeatherSearchListener {

  private var amap: AMap? = null

  private var makerA: Marker? = null
  private var makerB: Marker? = null
  private var distance = 0f

  private var uiSettings: UiSettings? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setView(R.layout.blog_calculate_distance_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    setTopBar(mapL)
    showFloat(false)
    amap = distanceMap.map
    uiSettings = amap?.uiSettings
    uiSettings?.isMyLocationButtonEnabled = true
    uiSettings?.isCompassEnabled = true
    uiSettings?.isScaleControlsEnabled = true
    distanceMap.onCreate(savedInstanceState)
    amap?.setOnMarkerDragListener(this)
    mapTopBack.setOnClickListener {
      pop()
    }
    val blog: Blog = arguments?.getParcelable("data")!!

    blogVM!!.followBlog.observe(this, Observer {
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      if (mapTopFollow != null) {
        mapTopFollow.visibility = View.GONE
      }
    })
    initData(blog)
  }

  private fun initData(blog: Blog) {
    mapTopIconR.setOnClickListener {
      push(MineFragment(id = blog.uid))
    }
    mapTopName.text = blog.nick
    if (blog.follows == FOLLOW) {
      mapTopFollow.visibility = View.GONE
    } else {
      mapTopFollow.visibility = View.VISIBLE
    }
    mapTopFollow.setOnClickListener {
      blogVM?.followAdd(blog)
    }
    mapTopMore.setOnClickListener {

    }

    addMark(Resource.icon, Resource.user!!.getLatLng())

    Img.loadImage(blog.icon, -2, object : IBitmapListener {
      override fun onSuccess(bitmap: Bitmap?): Boolean {
        mapTopIcon.setImageBitmap(bitmap)
        makerB = amap?.addMarker(
            MarkerOptions().position(blog.getLatLng())
                .draggable(true)
                .icon(
                    BitmapDescriptorFactory
                        .fromBitmap(ImgUtil.coverViewToBitmap(mapTopIcon))
                )
        )
        distance()
        return false
      }

      override fun onFailure(): Boolean {
        return false
      }

    })

    amap?.moveCamera(
        CameraUpdateFactory.newLatLngZoom(
            Resource.user!!.getLatLng(), 15f
        )
    )

    searchliveweather()
  }

  private fun distance(){
    LogUtil.e("------------makerA:",makerA," | position:",makerA?.position," | makerB:",makerB," | position:",makerB?.position)
    distance = AMapUtils.calculateLineDistance(makerA?.position, makerB?.position)
    mapTopDes.text = "相距：$distance m"
  }

  private fun addMark(
    url: String,
    latlng: LatLng
  ) {
    Img.loadImage(url, -2, object : IBitmapListener {
      override fun onSuccess(bitmap: Bitmap?): Boolean {
        mapTopIcon.setImageBitmap(bitmap)
        makerA = amap?.addMarker(
            MarkerOptions().position(latlng)
                .draggable(true)
                .icon(
                    BitmapDescriptorFactory
                        .fromBitmap(ImgUtil.coverViewToBitmap(mapTopIcon))
                )
        )
        distance()
        return false
      }

      override fun onFailure(): Boolean {
        return false
      }

    })
  }

  override fun onMarkerDragEnd(p0: Marker?) {
  }

  override fun onMarkerDragStart(p0: Marker?) {
  }

  override fun onMarkerDrag(p0: Marker?) {
//    distance = AMapUtils.calculateLineDistance(makerA!!.position, makerB!!.position)
//    mapTopDes.text = "相距：$distance m"
    distance()
  }

  /**
   * 所有粒子系统集合
   */
  private val particleMaps =
    Hashtable<String, List<ParticleOverlay>>()

  private fun weathEffect(key: String? = "") {
    var particleOverlayList: List<ParticleOverlay?>? = null

    var optionsList: List<ParticleOverlayOptions?>? = null
    particleOverlayList = particleMaps.get(key)
    if (particleOverlayList != null) {
      return
    }

    particleOverlayList = ArrayList<ParticleOverlay>()

    optionsList =
      ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_RAIN)
    for (options in optionsList) {
      val particleOverlay: ParticleOverlay = amap!!.addParticleOverlay(options)
      // 可以根据屏幕大小修改一下宽高
      particleOverlayList.add(particleOverlay)
    }
  }

  private val cityname = "北京市"
  private var mquery: WeatherSearchQuery? = null
  private var mweathersearch: WeatherSearch? = null

  /**s
   * 实时天气查询
   */
  private fun searchliveweather() {
    mquery = WeatherSearchQuery(
        cityname, WeatherSearchQuery.WEATHER_TYPE_LIVE
    ) //检索参数为城市和天气类型，实时天气为1、天气预报为2
    mweathersearch = WeatherSearch(mActivity)
    mweathersearch?.setOnWeatherSearchListener(this)
    mweathersearch?.setQuery(mquery)
    mweathersearch?.searchWeatherAsyn() //异步搜索
  }

  override fun onResume() {
    super.onResume()
    if (distanceMap != null) {
      distanceMap.onResume()
    }
    setMenuVisibility(false)
  }

  override fun onPause() {
    super.onPause()
    if (distanceMap != null) {
      distanceMap.onPause()
    }
    setMenuVisibility(true)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    if (distanceMap != null) {
      distanceMap.onSaveInstanceState(outState)
    }
  }

  override fun onDestroy() {
    if (distanceMap != null) {
      distanceMap.onDestroy()
    }
    super.onDestroy()
  }

  override fun onWeatherLiveSearched(
    weatherLiveResult: LocalWeatherLiveResult?,
    rCode: Int
  ) {
    LogUtil.e("-------weatherLiveResult:", weatherLiveResult)
    if (weatherLiveResult == null || weatherLiveResult.liveResult == null) return
    val weathLive = weatherLiveResult?.liveResult
    weathEffect(weathLive.weather)
    LogUtil.e(
        "reportTime:", weathLive.reportTime, " | weath:", weathLive.weather, " | temp:",
        weathLive.temperature, " | wind:", weathLive.windDirection, " | level:",
        weathLive.windPower, " | humid:", weathLive.humidity
    )

  }

  override fun onWeatherForecastSearched(
    weatherForecastResult: LocalWeatherForecastResult?,
    rCode: Int
  ) {

  }
}