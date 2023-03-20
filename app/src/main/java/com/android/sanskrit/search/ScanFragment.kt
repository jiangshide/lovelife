package com.android.sanskrit.search

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.files.Files
import com.android.http.exception.HttpException
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.user.OnFollowListener
import com.android.sanskrit.R
import com.android.utils.SystemUtil
import com.android.utils.data.FileData
import com.android.utils.data.IMG
import com.android.widget.ZdToast
import com.king.zxing.CaptureHelper
import com.king.zxing.OnCaptureCallback
import com.king.zxing.util.CodeUtils
import kotlinx.android.synthetic.main.scan_qr.*

/**
 * created by jiangshide on 2020/7/28.
 * email:18311271399@163.com
 */
class ScanFragment : MyFragment(), Files.OnFileListener,
    OnCaptureCallback, OnFollowListener {

    private var mCaptureHelper: CaptureHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.scan_qr)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCaptureHelper = CaptureHelper(this, surfaceView, viewfinderView, ivTorch)
        mCaptureHelper?.setOnCaptureCallback(this)
        mCaptureHelper?.onCreate()
        mCaptureHelper //                .decodeFormats(DecodeFormatManager.QR_CODE_FORMATS)//设置只识别二维码会提升速度
            ?.playBeep(true)
            ?.vibrate(true)
        topBarL.setPadding(0, SystemUtil.getStatusBarHeight(), 0, 0)
        topLeftBtn.setOnClickListener {
            pop()
        }
        scanAlbumImg.setOnClickListener {
            Files().setType(IMG)
                .setMax(1)
                .setFloat(true)
                .setSingleListener(this)
                .open(this)
        }
    }

    override fun onResume() {
        super.onResume()
        mCaptureHelper?.onResume()
        showFloatMenu(false)
    }

    override fun onPause() {
        super.onPause()
        mCaptureHelper?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCaptureHelper?.onDestroy()
        showFloatMenu(true)
    }

    override fun onResultCallback(result: String): Boolean {
        follow(result)
        return true
    }

    override fun onFile(fileData: FileData) {
        val result = CodeUtils.parseCode(fileData.path)
        follow(result)
    }

    private fun follow(result: String) {
        if (TextUtils.isEmpty(result)) return
        if (TextUtils.isDigitsOnly(result)) {
            val uid = result.toLong()
            if (uid == Resource.uid) {
                ZdToast.txt("不能关注自己")
                return
            }
            showLoading()
            userVM?.followAdd(uid, -1, this)
        } else {
            openUrl(result)
            pop()
        }
    }

    override fun follow(status: Int, e: HttpException?) {
        hiddle()
        if (e != null) {
            ZdToast.txt(e.message)
        } else {
            ZdToast.txt("关注成功!") {
                pop()
            }
        }
    }
}