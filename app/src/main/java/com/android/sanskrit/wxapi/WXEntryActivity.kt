package com.android.sanskrit.wxapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.event.ZdEvent
import com.android.sanskrit.user.LOGIN_WECHAT
import com.android.utils.LogUtil
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXEntryActivity : AppCompatActivity(), IWXAPIEventHandler {

    override fun onReq(p0: BaseReq?) {
        LogUtil.e("wx onReq", p0)
    }

    override fun onResp(resp: BaseResp?) {
        LogUtil.e("wx onResp", resp, " | errCode:", resp?.errCode)
        when (resp?.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                if (resp is SendAuth.Resp) {
                    val code = resp.code
                    ZdEvent.get()
                        .with(LOGIN_WECHAT)
                        .post(code)
                    finish()
                }
            }
            else -> {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WXApiManager.handlerIntent(intent, this)
    }
}
