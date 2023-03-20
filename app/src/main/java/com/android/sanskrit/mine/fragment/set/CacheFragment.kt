package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.utils.FileUtil
import com.android.widget.ZdDialog
import com.android.widget.extension.setDrawableRight
import kotlinx.android.synthetic.main.mine_set_cache_fragment.*

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class CacheFragment : MyFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.mine_set_cache_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val apiSize = FileUtil.getDirSize(FileUtil.getHttpDir())
        if (TextUtils.isEmpty(apiSize) || apiSize == "0.000B") {
            cacheApiTxt.text = "0.0B"
            cacheApiTxt.setDrawableRight(R.drawable.alpha)
        } else {
            cacheApiTxt.text = apiSize
            cacheApiTxt.setDrawableRight(R.mipmap.arrow)
            cacheApiTxt.setOnClickListener {
                ZdDialog.create(mActivity).setContent("确定删除!")
                    .setListener { isCancel, editMessage ->
                        if (isCancel) return@setListener
                        val isDeleted = FileUtil.deleteAllInDir(FileUtil.getVideoDir())
                        if (isDeleted) {
                            cacheApiTxt.text = "0.0B"
                            cacheApiTxt.setDrawableRight(R.drawable.alpha)
                        }
                        ZdDialog.cancelDialog()
                    }.show()
            }
        }

        val videoSize = FileUtil.getDirSize(FileUtil.getVideoDir())
        if (TextUtils.isEmpty(videoSize) || videoSize == "0.000B") {
            cacheVideoTxt.text = "0.0B"
            cacheVideoTxt.setDrawableRight(R.drawable.alpha)
        } else {
            cacheVideoTxt.text = videoSize
            cacheVideoTxt.setDrawableRight(R.mipmap.arrow)
            cacheVideoTxt.setOnClickListener {
                ZdDialog.create(mActivity).setContent("确定删除!")
                    .setListener { isCancel, editMessage ->
                        if (isCancel) return@setListener
                        val isDeleted = FileUtil.deleteAllInDir(FileUtil.getVideoDir())
                        if (isDeleted) {
                            cacheVideoTxt.text = "0.0B"
                            cacheVideoTxt.setDrawableRight(R.drawable.alpha)
                        }
                        ZdDialog.cancelDialog()
                    }.show()
            }
        }

        val audioSize = FileUtil.getDirSize(FileUtil.getAudioDir())
        if (TextUtils.isEmpty(audioSize) || audioSize == "0.000B") {
            cacheAudioTxt.text = "0.0B"
            cacheAudioTxt.setDrawableRight(R.drawable.alpha)
        } else {
            cacheAudioTxt.text = audioSize
            cacheAudioTxt.setDrawableRight(R.mipmap.arrow)
            cacheAudioTxt.setOnClickListener {
                ZdDialog.create(mActivity).setContent("确定删除!")
                    .setListener { isCancel, editMessage ->
                        if (isCancel) return@setListener
                        val isDeleted = FileUtil.deleteAllInDir(FileUtil.getAudioDir())
                        if (isDeleted) {
                            cacheAudioTxt.text = "0.0B"
                            cacheAudioTxt.setDrawableRight(R.drawable.alpha)
                        }
                        ZdDialog.cancelDialog()
                    }.show()
            }
        }

        val imgSize = FileUtil.getDirSize(FileUtil.getImagesDir())
        if (TextUtils.isEmpty(imgSize) || imgSize == "0.000B") {
            cacheImgTxt.text = "0.0B"
            cacheImgTxt.setDrawableRight(R.drawable.alpha)
        } else {
            cacheImgTxt.text = imgSize
            cacheImgTxt.setDrawableRight(R.mipmap.arrow)
            cacheImgTxt.setOnClickListener {
                ZdDialog.create(mActivity).setContent("确定删除!")
                    .setListener { isCancel, editMessage ->
                        if (isCancel) return@setListener
                        val isDeleted = FileUtil.deleteAllInDir(FileUtil.getImagesDir())
                        if (isDeleted) {
                            cacheImgTxt.text = "0.0B"
                            cacheImgTxt.setDrawableRight(R.drawable.alpha)
                        }
                        ZdDialog.cancelDialog()
                    }.show()
            }
        }

        val webViewSize = FileUtil.getDirSize(FileUtil.getWebViewDir())
        if (TextUtils.isEmpty(webViewSize) || webViewSize == "0.000B") {
            cacheWebViewTxt.text = "0.0B"
            cacheWebViewTxt.setDrawableRight(R.drawable.alpha)
        } else {
            cacheWebViewTxt.text = webViewSize
            cacheWebViewTxt.setDrawableRight(R.mipmap.arrow)
            cacheWebViewTxt.setOnClickListener {
                ZdDialog.create(mActivity).setContent("确定删除!")
                    .setListener { isCancel, editMessage ->
                        if (isCancel) return@setListener
                        val isDeleted = FileUtil.deleteAllInDir(FileUtil.getWebViewDir())
                        if (isDeleted) {
                            cacheWebViewTxt.text = "0.0B"
                            cacheWebViewTxt.setDrawableRight(R.drawable.alpha)
                        }
                        ZdDialog.cancelDialog()
                    }.show()
            }
        }

        val downloadSize = FileUtil.getDirSize(FileUtil.getDownload())
        if (TextUtils.isEmpty(downloadSize) || downloadSize == "0.000B") {
            cacheDownloadTxt.text = "0.0B"
            cacheDownloadTxt.setDrawableRight(R.drawable.alpha)
        } else {
            cacheDownloadTxt.text = downloadSize
            cacheDownloadTxt.setDrawableRight(R.mipmap.arrow)
            cacheDownloadTxt.setOnClickListener {
                ZdDialog.create(mActivity).setContent("确定删除!")
                    .setListener { isCancel, editMessage ->
                        if (isCancel) return@setListener
                        val isDeleted = FileUtil.deleteAllInDir(FileUtil.getDownload())
                        if (isDeleted) {
                            cacheDownloadTxt.text = "0.0B"
                            cacheDownloadTxt.setDrawableRight(R.drawable.alpha)
                        }
                        ZdDialog.cancelDialog()
                    }.show()
            }
        }
    }
}