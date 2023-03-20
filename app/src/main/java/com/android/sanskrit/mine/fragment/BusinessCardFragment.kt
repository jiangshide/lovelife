package com.android.sanskrit.mine.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.img.Img
import com.android.img.listener.IBitmapListener
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.sanskrit.R
import com.android.sanskrit.search.ScanFragment
import com.android.utils.FileUtil
import com.android.utils.ImgUtil
import com.android.utils.LogUtil
import com.android.utils.ScreenUtil
import com.android.widget.ZdToast
import com.king.zxing.util.CodeUtils
import kotlinx.android.synthetic.main.mine_fragment_business_card.*
import java.util.*


/**
 * created by jiangshide on 2020/6/30.
 * email:18311271399@163.com
 */
class BusinessCardFragment : MyFragment() {

    companion object {
        fun newInstance(): BusinessCardFragment {
            return BusinessCardFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.mine_fragment_business_card)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
//跳转的默认扫码界面
        //跳转的默认扫码界面
//        startActivityForResult(Intent(context, CaptureActivity::class.java), 1)

        //生成二维码

        //生成二维码
//        CodeUtils.createQRCode("content", 600, logo)
        //生成条形码
//        //生成条形码
//        CodeUtils.createBarCode("content", BarcodeFormat.CODE_128, 800, 200)
//        //解析条形码/二维码
//        //解析条形码/二维码
//        CodeUtils.parseCode(bitmapPath)
//        //解析二维码
//        //解析二维码
//        CodeUtils.parseQRCode(bitmapPath)
        val height = ScreenUtil.getRtScreenHeight(mActivity) / 3
        val layoutParams = qrCode.layoutParams
        layoutParams.height = height
        qrCode.layoutParams = layoutParams
        qrCode.setOnClickListener {
            generalQrCode(height)
        }
        qrCode.setOnLongClickListener {
            val qr = FileUtil.getImagePath("qr.png")
            LogUtil.e("--------qr:", qr)
            val isSave = ImgUtil.save(ImgUtil.coverViewToBitmap(mineCardQrBgF), qr)
            if (isSave) {
                ZdToast.txt("保存为:${qr}")
            }
            return@setOnLongClickListener true
        }
        Img.loadImageCircle(Resource.icon, mineCardIcon)
        generalQrCode(height)
        mineCardScan.setOnClickListener {
            push(ScanFragment())
        }
    }

    private fun generalQrCode(height: Int) {
        Img.loadImage(Resource.icon, object : IBitmapListener {
            override fun onSuccess(bitmap: Bitmap?): Boolean {
                val roundBitmap = ImgUtil.toRound(bitmap)
                qrCode.setImageBitmap(
                    CodeUtils.createQRCode(
                        "${Resource.uid}", height
                        ,
                        roundBitmap,
                        randomColor()
                    )
                )
//                val blurBitmap = ImgUtil.fastBlur(bitmap,1f,10f)
//                qrCodeBg.setImageBitmap(blurBitmap)
                return true
            }

            override fun onFailure(): Boolean {
                return true
            }
        })
        mineCardQrBgF.setBackgroundColor(randomColor())
    }


    private fun randomColor(): Int {
        val random = Random()
        return -0x1000000 or random.nextInt(0x00ffffff)
    }
}

const val SCAN_REQUEST = 1