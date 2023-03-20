package com.android.sanskrit.mine.fragment.set

import android.Manifest.permission
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.files.Files
import com.android.files.Files.OnFileListener
import com.android.http.oss.OssClient
import com.android.http.oss.OssClient.HttpFileListener
import com.android.img.Img
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.utils.data.FileData
import com.android.utils.data.IMG
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.mine_set_feedback_fragment.feedbackEdit
import kotlinx.android.synthetic.main.mine_set_feedback_fragment.feedbackImg
import kotlinx.android.synthetic.main.mine_set_feedback_fragment.feedbackMethod

/**
 * created by jiangshide on 2020/6/9.
 * email:18311271399@163.com
 */
class FeedbackFragment(
  private val contentId: Long? = 0,
  private val source: Int? = 0
) : MyFragment(), HttpFileListener, OnFileListener {

  private var content: String = ""
  private var mUrl: String = ""
  private var contact: String = ""

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_feedback_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    feedbackEdit.setListener { s, input ->
      this.content = input
      validate()
    }
    feedbackImg.setOnClickListener {
      openFile()
    }
    feedbackMethod.setListener { s, input ->
      contact = input
      validate()
    }
    reportVM!!.feedback.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      ZdToast.txt(getString(R.string.success)) {
        pop()
      }
    })
  }

  private fun openFile() {
    Files().setType(IMG)
        .setMax(1)
        .setFloat(true)
        .setSingleListener(this)
        .open(this)
  }

  private fun validate() {
    val disable = TextUtils.isEmpty(content)
    setRight("发送").setRightColor(R.color.redLight)
        .setRightEnable(!disable)
        .setRightListener {
          reportVM?.feedback(
              contentId = contentId, content = content, url = mUrl, contact = contact,
              source = source
          )
          showLoading()
        }
  }

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
    mUrl = url
  }

  override fun onFailure(
    clientExcepion: Exception,
    serviceException: Exception
  ) {
    ZdToast.txt(serviceException?.message)
  }

  override fun onFile(fileData: FileData) {
    if (feedbackImg == null) return
    mUrl = fileData!!.path!!
    Img.loadImage(mUrl, feedbackImg, R.mipmap.image_placeholder)
    OssClient.instance.setListener(this)
        .setFileData(fileData!!)
        .start()
    validate()
  }
}