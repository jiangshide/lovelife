package com.android.resource

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.android.http.oss.OssClient
import com.android.http.oss.OssClient.HttpFileListener
import com.android.resource.vm.publish.data.File
import com.android.resource.vm.publish.data.Publish
import com.android.utils.LogUtil
import com.android.utils.data.FileData
import java.lang.Exception

/**
 * created by jiangshide on 2020/3/29.
 * email:18311271399@163.com
 */
class PublishService : Service(), HttpFileListener {

  private var publish: Publish? = null
  private var data: MutableList<FileData>? = null

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int
  ): Int {
    publish = Resource.publish
    if (publish != null) {
      publish(publish)
    }
    return super.onStartCommand(intent, flags, startId)
  }

  private fun publish(publish: Publish?) {
    LogUtil.e("publish:", publish)
    if (publish?.files != null) {
      data = publish.files
      publishFile(0, null)
    }
  }

  private fun publishFile(
    position: Int,
    url: String?
  ) {
    if (!TextUtils.isEmpty(url)) {
      data!![position].url = url
      val file = File()
      file.name = data!![position].name
      file.sufix = data!![position].sufix
      file.width = data!![position].width
      file.height = data!![position].height
      file.size = data!![position].size
      file.duration = data!![position].duration
      file.format = data!![position].format
      file.rotate = data!![position].rotate
      file.url = data!![position].url
      file.lrcEs = data!![position].lrcEs
      file.lrcZh = data!![position].lrcZh
      publish?.uploadFiles?.add(file)
    }
    var isUpload = true
    data?.forEachIndexed { index, fileData ->
      if (!TextUtils.isEmpty(fileData.path) && TextUtils.isEmpty(fileData.url)) {
        fileData.position = index
//        ZdEvent.get()
//            .with(PUBLISH_START)
//            .post(fileData)
        fileData.position = index
        OssClient.instance
            .setListener(this).setFileData(fileData).start()
        isUpload = false
      }
    }
    if (isUpload) {
      publish?.toGson()
//      ZdEvent.get()
//          .with(PUBLISH_SUCCESS)
//          .post(publish!!)

    }
  }

  override fun onDestroy() {
    super.onDestroy()
  }

  override fun onSuccess(
    position: Int,
    url: String
  ) {
    publishFile(position,url)
  }

  override fun onFailure(
    clientExcepion: Exception,
    serviceException: Exception
  ) {
//    ZdEvent.get()
//        .with(PUBLISH_FAILE)
//        .post(serviceException)
  }

  override fun onProgress(
    currentSize: Long,
    totalSize: Long,
      progress:Int
  ) {
    var curr = (currentSize / totalSize.toDouble()) * 100
//    ZdEvent.get()
//        .with(PUBLISH_PROGRESS)
//        .post(curr.toInt())
  }
}

const val PUBLISH_START = "publishStart"
const val PUBLISH_PROGRESS = "publishProgress"
const val PUBLISH_SUCCESS="publishSuccess"
const val PUBLISH_FAILE = "publishFaile"