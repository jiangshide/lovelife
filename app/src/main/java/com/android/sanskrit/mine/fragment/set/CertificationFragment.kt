package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.files.Files
import com.android.files.Files.OnFileListener
import com.android.img.Img
import com.android.resource.AUDIT_PASS
import com.android.resource.AUDIT_REJECTED
import com.android.resource.AUDIT_UNDER
import com.android.resource.MyFragment
import com.android.http.oss.OssClient
import com.android.http.oss.OssClient.HttpFileListener
import com.android.resource.vm.user.data.Certification
import com.android.sanskrit.R
import com.android.utils.data.FileData
import com.android.utils.data.IMG
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationBackEg
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationBackF
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationBackImg
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationBackL
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationBackProgress
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationBackTry
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationCardNum
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationName
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationPositiveEg
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationPositiveF
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationPositiveImg
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationPositiveL
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationPositiveProgress
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationPositiveTry
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationReason
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationReasonL
import kotlinx.android.synthetic.main.mine_set_certification_fragment.certificationStatus

/**
 * created by jiangshide on 2020/5/4.
 * email:18311271399@163.com
 */
class CertificationFragment : MyFragment() {

  private var certification = Certification()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_certification_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    setRightEnable(false).setRight("提交审核")
        .setRightListener {
          certification.reason = getString(R.string.audit_under)
          userVM?.certificationUpdate(certification)
        }

    certification.status = 1
    certificationName.setListener { s, input ->
      certification.name = input
      validate()
    }

    certificationCardNum.setListener { s, input ->
      certification.idCard = input
      validate()
    }

    certificationPositiveEg.setOnClickListener {
      viewImg("http://zd112.oss-cn-beijing.aliyuncs.com/img/Sanskrit1587946790890.jpg")
    }
    certificationPositiveF.setOnClickListener {
      var currfileData: FileData? = null
      certificationPositiveTry.setOnClickListener {
        uploadPositive(currfileData!!)
        certificationPositiveTry.visibility = View.GONE
      }
      Files().setType(IMG)
          .setMax(1)
          .setFloat(true)
          .setSingleListener(object : OnFileListener {
            override fun onFile(fileData: FileData) {
              currfileData = fileData
              certificationPositiveL.visibility = View.VISIBLE
              Img.loadImageRound(fileData.path, certificationPositiveImg, 5)
              uploadPositive(currfileData!!)
            }
          })
          .open(this)
    }

    certificationBackEg.setOnClickListener {
      viewImg("http://zd112.oss-cn-beijing.aliyuncs.com/img/Sanskrit1587946790890.jpg")
    }

    certificationBackF.setOnClickListener {
      var currfileData: FileData? = null
      certificationBackTry.setOnClickListener {
        uploadBack(currfileData!!)
        certificationBackTry.visibility = View.GONE
      }
      Files().setType(IMG)
          .setMax(1)
          .setFloat(true)
          .setSingleListener(object : OnFileListener {
            override fun onFile(fileData: FileData) {
              currfileData = fileData
              certificationBackL.visibility = View.VISIBLE
              Img.loadImageRound(fileData.path, certificationBackImg, 5)
              uploadBack(currfileData!!)
            }
          })
          .open(this)
    }

    userVM!!.certificationUpdate.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        return@Observer
      }
      ZdToast.txt("提交成功") {
        pop()
      }
    })

    userVM!!.certification.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      certification = it.data
      showView()
    })

    userVM!!.certification()
    showLoading()
  }

  private fun showView() {
    when (certification.status) {
      AUDIT_UNDER -> {
        certificationStatus.text =
          getString(R.string.audit_status) + getString(R.string.audit_under)
      }
      AUDIT_PASS -> {
        certificationStatus.text = getString(R.string.audit_status) + getString(R.string.audit_pass)
      }
      AUDIT_REJECTED -> {
        certificationStatus.text =
          getString(R.string.audit_status) + getString(R.string.audit_rejected)
      }
    }
    if (!TextUtils.isEmpty(certification.reason)) {
      certificationReasonL.visibility = View.VISIBLE
      certificationReason.setTxt(certification.reason)
    } else {
      certificationReasonL.visibility = View.GONE
    }
    certificationName.setText(certification.name)
    certificationCardNum.setText(certification.idCard)
    Img.loadImageRound(certification.idCardPicFront, certificationPositiveImg, 5)
    Img.loadImageRound(certification.idCardPicBehind, certificationBackImg, 5)
  }

  private fun uploadPositive(fileData: FileData) {
    OssClient.instance.setListener(object : HttpFileListener {
      override fun onProgress(
        currentSize: Long,
        totalSize: Long,
        progress: Int
      ) {
        if (certificationPositiveTry == null) return
        certificationPositiveTry.visibility = View.GONE
        certificationPositiveProgress.visibility = View.VISIBLE
        certificationPositiveProgress.progress = progress
      }

      override fun onSuccess(
        position: Int,
        url: String
      ) {
        if (certificationPositiveProgress == null) return
        certificationPositiveProgress.progress = 0
        certificationPositiveProgress.visibility = View.GONE
        certification.idCardPicFront = url
        validate()
      }

      override fun onFailure(
        clientExcepion: Exception,
        serviceException: Exception
      ) {
        if (certificationPositiveTry == null) return
        certificationPositiveTry.visibility = View.VISIBLE
        certificationPositiveProgress.visibility = View.GONE
      }

    })
        .setFileData(fileData!!)
        .start()
  }

  private fun uploadBack(fileData: FileData) {
    OssClient.instance.setListener(object : HttpFileListener {
      override fun onProgress(
        currentSize: Long,
        totalSize: Long,
        progress: Int
      ) {
        if (certificationBackTry == null) return
        certificationBackTry.visibility = View.GONE
        certificationBackProgress.visibility = View.VISIBLE
        certificationBackProgress.progress = progress
      }

      override fun onSuccess(
        position: Int,
        url: String
      ) {
        if (certificationBackProgress == null) return
        certificationBackProgress.progress = 0
        certificationBackProgress.visibility = View.GONE
        certification.idCardPicBehind = url
        validate()
      }

      override fun onFailure(
        clientExcepion: Exception,
        serviceException: Exception
      ) {
        if (certificationBackTry == null) return
        certificationBackTry.visibility = View.VISIBLE
        certificationBackProgress.visibility = View.GONE
      }

    })
        .setFileData(fileData!!)
        .start()
  }

  private fun validate() {
    val disable = TextUtils.isEmpty(certification.name) || TextUtils.isEmpty(
        certification.idCard
    ) || TextUtils.isEmpty(certification.idCardPicFront) || TextUtils.isEmpty(
        certification.idCardPicBehind
    )
    setRightEnable(!disable)
  }
}