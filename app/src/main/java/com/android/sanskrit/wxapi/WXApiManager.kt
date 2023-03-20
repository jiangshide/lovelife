package com.android.sanskrit.wxapi

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.android.resource.BuildConfig
import com.android.resource.vm.user.data.Order
import com.android.utils.ImgUtil
import com.android.utils.LogUtil
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXTextObject
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class WXApiManager {

  companion object {
    private var api: IWXAPI? = null

    private const val mTargetScene = Req.WXSceneTimeline
    private const val THUMB_SIZE = 150

    /**
     * 注册到微信
     */
    fun regToWX(context: Context) {
      if (api == null) {
        api = WXAPIFactory.createWXAPI(context, BuildConfig.WECHAT_APPID, true)
      }
      api!!.registerApp(BuildConfig.WECHAT_APPID)
    }

    /**
     * 发送登陆请求
     */
    fun sendLoginRequest() {
      // send oauth request
      var req = SendAuth.Req()
      req.scope = "snsapi_userinfo"
      req.state = "sanskrit_login"
      val reqRet = api!!.sendReq(req)
      LogUtil.i(String.format("send wx login req result %s", reqRet))
    }

    /**
     * 微信是否安装
     */
    fun isWxInstall(): Boolean {
      return api!!.isWXAppInstalled()
    }

    fun handlerIntent(
      intent: Intent,
      iwxapiEventHandler: IWXAPIEventHandler
    ) {
      if (api != null) {
        api?.handleIntent(intent, iwxapiEventHandler)
      }
    }

    fun pay(order: Order) {
      val payReq = PayReq()
      payReq.appId = order.appid
      payReq.partnerId = order.mchid
      payReq.prepayId = order.prepayId
      payReq.packageValue = order.packageValue
      payReq.nonceStr = order.nonceStr
      payReq.timeStamp = "${order.date}"
      payReq.sign = order.sign
      val result = api?.sendReq(payReq)
      LogUtil.e("api:",api," | order:",order," | result:",result)
    }

    fun shareTxt(str: String?) {
      LogUtil.e("------share~str:", str)
      val wxTextObject = WXTextObject()
      wxTextObject.text = str
      val msg = WXMediaMessage()
      msg.mediaObject = wxTextObject
      msg.description = str
      msg.mediaTagName = "the jankey!"
      val req = Req()
      req.transaction = buildTransaction("text")
      req.message = msg
      req.scene = mTargetScene
      api?.sendReq(req)
      LogUtil.e("--------share~str:", str)
    }

    fun shareImg(path: String) {
      val bmp = ImgUtil.getBitmap(path)
      val imgObj = WXImageObject(bmp)
      val msg = WXMediaMessage()
      msg.mediaObject = imgObj
      val thumbBmp = Bitmap.createScaledBitmap(
          bmp, THUMB_SIZE, THUMB_SIZE, true
      )
      bmp.recycle()
      msg.thumbData = ImgUtil.bmpToByteArray(thumbBmp, true)

      val req = Req()
      req.transaction = buildTransaction("img")
      req.message = msg
      req.scene = mTargetScene
      api!!.sendReq(req)
    }

    private fun buildTransaction(type: String?): String? {
      return if (type == null) System.currentTimeMillis()
          .toString() else type + System.currentTimeMillis()
    }
  }

}