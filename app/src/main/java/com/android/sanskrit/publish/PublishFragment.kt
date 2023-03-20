package com.android.sanskrit.publish

import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.amap.api.services.core.PoiItem
import com.android.event.ZdEvent
import com.android.files.Files
import com.android.files.Files.OnFilesListener
import com.android.files.view.transferee.loader.GlideImageLoader
import com.android.files.view.transferee.transfer.TransferConfig
import com.android.http.oss.OssClient
import com.android.http.oss.OssClient.HttpFileListener
import com.android.img.Img
import com.android.img.listener.IBitmapListener
import com.android.player.dplay.player.VideoViewManager
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.data.DeviceData
import com.android.resource.data.PositionData
import com.android.resource.vm.channel.data.Channel
import com.android.resource.vm.publish.data.Publish
import com.android.resource.vm.user.data.User
import com.android.sanskrit.FLOW_MENUS
import com.android.sanskrit.PUBLISH_UPLOAD
import com.android.sanskrit.R
import com.android.sanskrit.publish.fragment.CHANNEL
import com.android.sanskrit.publish.fragment.ChannelManagerFragment
import com.android.sanskrit.publish.fragment.LOCATION
import com.android.sanskrit.publish.fragment.LocationFragment
import com.android.sanskrit.publish.fragment.PublishBaseFragment
import com.android.sanskrit.publish.fragment.USERS
import com.android.sanskrit.publish.fragment.UsersFragment
import com.android.sanskrit.publish.fragment.VideoPreviewFragment
import com.android.utils.AppUtil
import com.android.utils.DateUtil
import com.android.utils.FileUtil
import com.android.utils.ImgUtil
import com.android.utils.LogUtil
import com.android.utils.ScreenUtil
import com.android.utils.data.FileData
import com.android.utils.data.IMG
import com.android.utils.data.VIDEO
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import com.android.widget.extension.setDrawableLeft
import kotlinx.android.synthetic.main.publish_common.publishAudioL
import kotlinx.android.synthetic.main.publish_common.publishChanneName
import kotlinx.android.synthetic.main.publish_common.publishChanneNameCover
import kotlinx.android.synthetic.main.publish_common.publishChanneNameIcon
import kotlinx.android.synthetic.main.publish_common.publishChannelL
import kotlinx.android.synthetic.main.publish_common.publishLocationL
import kotlinx.android.synthetic.main.publish_common.publishLocationName
import kotlinx.android.synthetic.main.publish_common.publishLocationNameIcon
import kotlinx.android.synthetic.main.publish_common.publishRemindL
import kotlinx.android.synthetic.main.publish_common.publishRemindRecycleView
import kotlinx.android.synthetic.main.publish_common.publishSyncL
import kotlinx.android.synthetic.main.publish_common.publishSyncSwitch
import kotlinx.android.synthetic.main.publish_common.publishSyncSwitchIcon
import kotlinx.android.synthetic.main.publish_common.publishSyncSwitchTips
import kotlinx.android.synthetic.main.publish_fragment.*
import kotlinx.android.synthetic.main.publish_fragment_item.view.publishItemAdd
import kotlinx.android.synthetic.main.publish_fragment_item.view.publishItemF
import kotlinx.android.synthetic.main.publish_fragment_item.view.publishItemIcon

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class PublishFragment(private var data: MutableList<FileData>) : PublishBaseFragment(), OnFilesListener {

  private var adapter: KAdapter<FileData>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.publish_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    LogUtil.e("data1:", data)
    val positionData = PositionData()
    val deviceData = DeviceData(mActivity)
    setLeft("取消").setLeftListener {
      pop()
    }
        .setTitle("")
        .setRight("发布")
        .setRightEnable(false)
        .setRightListener {
          publish.netInfo = positionData.gson
          publish.device = deviceData.gson
          if (publish.format == IMG) {
            publish.files!!.removeAt(publish.files!!.size - 1)
          }
          Resource.publish = publish
          ZdEvent.get()
              .with(PUBLISH_UPLOAD)
              .post(publish)
          pop()
          ZdEvent.get()
              .with(FLOW_MENUS)
              .post(FLOW_MENUS)
        }
    LogUtil.e("data2:", data)
    publishTitle.setListener { s, input ->
      publish.title = input
      validate()
    }
    publishIntro.setListener { s, input ->
      publish.des = input
      validate()
    }
    publish.files = data
    showView(data)
  }

  private fun files() {
    val arr = ArrayList<FileData>()
    adapter!!.datas()
        .forEach {
          if (!TextUtils.isEmpty(it.path)) {
            arr.add(it)
          }
        }
    if (publish.format == IMG) {
      Files().setType(IMG)
          .setMax(9)
          .setFloat(true)
          .setSelectedFile(arr)
          .setMultipleListener(this)
          .open(this)
    } else if (publish.format == VIDEO) {
      Files().setType(VIDEO)
          .setSelectedFile(arr)
          .setMax(1)
          .setFloat(true)
          .setSelectedFile(arr)
          .setMultipleListener(this)
          .open(this)
    }
  }

  private val MIN_CLICK_DELAY_TIME = 500
  private var mLastClickTime: Long = 0
  private fun showView(data: MutableList<FileData>) {
    if (data == null || data.size == 0) return
    publish.format = data[0].format
    if (publish.format == IMG) {
      publishAudioL.visibility = View.GONE
      publishVideoCover.visibility = View.GONE
      publishPlay.visibility = View.GONE
      publishPlayTime.visibility = View.GONE
      publishPlayRetry.visibility = View.GONE
      publishRecycleView.visibility = View.VISIBLE
    } else {
      publishAudioL.visibility = View.VISIBLE
      publishVideoCover.visibility = View.VISIBLE
      publishPlay.visibility = View.VISIBLE
      publishPlayTime.visibility = View.VISIBLE
      publishPlayRetry.visibility = View.VISIBLE
      publishRecycleView.visibility = View.GONE
      val layoutParams = FrameLayout.LayoutParams(
          ScreenUtil.getRtScreenWidth(mActivity), ScreenUtil.getRtScreenHeight(mActivity) / 4
      )
      publishVideoCover.layoutParams = layoutParams
      val path = publish.files!![0].path
      Img.loadImage(path, object : IBitmapListener {
        override fun onSuccess(bitmap: Bitmap?): Boolean {
          publishVideoCover?.setImageBitmap(bitmap)
          val coverData = FileData()
          val cover = "${context?.cacheDir}/${FileUtil.getFileNameNoExtension(path)}.png"
          ImgUtil.save(bitmap, cover)
          coverData.path = cover
          coverData.dir = publish.files!![0].dir
          coverData.format = publish.files!![0].format
          OssClient.instance.setListener(object : HttpFileListener {
            override fun onProgress(
              currentSize: Long,
              totalSize: Long,
              progress: Int
            ) {

            }

            override fun onSuccess(
              position: Int,
              url: String
            ) {
              publish.files!![0].cover = url
            }

            override fun onFailure(
              clientExcepion: Exception,
              serviceException: Exception
            ) {
            }

          })
              .setFileData(coverData)
              .start()
          return true
        }

        override fun onFailure(): Boolean {
          return true
        }
      })
      publishPlayTime.text = DateUtil.formatSeconds(publish.files!![0].duration / 1000)
      publishPlayRetry.setOnClickListener {
        Files().setType(VIDEO)
            .setMax(1)
            .setFloat(true)

            .setSelectedFile(publish.files as java.util.ArrayList<FileData>)
            .setMultipleListener(this)
            .open(this)
      }
      publishVideoCover.setOnClickListener {
        push(
            VideoPreviewFragment().setTitle(publish.files!![0].name)
                .setTopBgIcon(R.color.black), publish.files!![0]
        )
      }

      return
    }

    if (data.size < 9) {
      data.add(FileData())
    }
    validate()
    val layoutManager = GridLayoutManager(mActivity, 3)
    adapter = publishRecycleView.create(data, R.layout.publish_fragment_item, {
      val width = (ScreenUtil.getRtScreenWidth(mActivity) - 100) / 3
      val layoutParams = FrameLayout.LayoutParams(width, width)
      publishItemF.layoutParams = layoutParams
      if (TextUtils.isEmpty(it.path)) {
        publishItemAdd.visibility = View.VISIBLE
      } else {
        publishItemAdd.visibility = View.GONE
      }
      publishItemAdd.setOnClickListener {
        files()
      }
      Img.loadImageRound(it.path, publishItemIcon, 0, R.mipmap.image_placeholder)
      val file = it
      publishItemIcon.setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - mLastClickTime > MIN_CLICK_DELAY_TIME) {
          mLastClickTime = currentTime
          val list = ArrayList<String>()
          var index = 0
          adapter!!.datas()
              .forEachIndexed { position, fileData ->
                if (!TextUtils.isEmpty(fileData.path)) {
                  if (fileData.path == file.path) {
                    index = position
                  }
                  list.add(fileData.path!!)
                }
              }
          transferee?.apply(
              TransferConfig.build()
                  .setImageLoader(GlideImageLoader.with(AppUtil.getApplicationContext()))
                  .setNowThumbnailIndex(index)
                  .setSourceImageList(list)
                  .create()
          )
              ?.show()
        } else {
          if (!TextUtils.isEmpty(file.path)) {
            adapter?.remove(file)
            val size = adapter!!.datas().size
            if (size < 9) {
              var isAdd = true
              adapter!!.datas()
                  .forEach {
                    if (it.bucketName == file.bucketName) {
                      if (file.selectedDirList != null) {
                        it.selectedDirList = file.selectedDirList
                      }
                      if (it.selectedDirList != null && it.selectedDirList!!.size > 0) {
                        it.selectedDirList!![0].selectedDirSize -= 1
                      }
                    }
                    if (TextUtils.isEmpty(it.path)) {
                      isAdd = false
                    }
                  }
              if (isAdd) {
                adapter?.add(FileData())
              }
            }
          }
        }
      }
    }, {
    }, layoutManager)
        .drag(publishRecycleView)
  }

  override fun onFiles(data: MutableList<FileData>) {
    publish.files = data
    showView(data!!)
  }

  override fun onResume() {
    super.onResume()
    showFloatMenu(false)
    VideoViewManager.instance()
      .pause()
  }

  override fun onPause() {
    super.onPause()
    showFloatMenu(true)
    if (Resource.TAB_INDEX == 0) {
      VideoViewManager.instance()
        .resume()
    }
  }
}