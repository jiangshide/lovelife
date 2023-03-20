package com.android.resource.vm.user;

import com.android.resource.BuildConfig;
import com.android.utils.AppUtil;
import com.android.utils.LogUtil;
import com.android.widget.ZdToast;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * created by jiangshide on 2020/3/19.
 * email:18311271399@163.com
 */
public class Login {
  private IWXAPI mWxApi;

  private static class LoginHoler {
    private static Login INSTANCE = new Login();
  }

  public static Login getInstance() {
    return LoginHoler.INSTANCE;
  }

  public IWXAPI getWxApi() {
    if (mWxApi == null) {
      regToWx();
    }
    return mWxApi;
  }

  /**
   * 微信注册
   */
  public IWXAPI regToWx() {
    if (mWxApi == null) {
      mWxApi =
          WXAPIFactory.createWXAPI(AppUtil.getApplicationContext(), BuildConfig.WECHAT_APPID, true);
    }
    mWxApi.registerApp(BuildConfig.WECHAT_APPID);
    LogUtil.e("isWXAppInstalled:", mWxApi.isWXAppInstalled());
    return mWxApi;
  }

  public void loginWX(){
    if(!getWxApi().isWXAppInstalled()){
      //LogUtil.e("-------open",getWxApi().openWXApp());
      ZdToast.txt("请先安装微信!");
      return;
    }
    SendAuth.Req req = new SendAuth.Req();
    req.scope = "snsapi_userinfo";
    req.state = "Sanskrit";
    getWxApi().sendReq(req);
    LogUtil.e("req:",req);
  }

  public void share(String text){
    //初始化一个 WXTextObject 对象，填写分享的文本内容
    WXTextObject textObj = new WXTextObject();
    textObj.text = text;

    //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
    WXMediaMessage msg = new WXMediaMessage();
    msg.mediaObject = textObj;
    msg.description = text;

    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = String.valueOf(System.currentTimeMillis());  //transaction字段用与唯一标示一个请求
    req.message = msg;
    //req.scene = mTargetScene;

    //调用api接口，发送数据到微信
    getWxApi().sendReq(req);
  }
}
