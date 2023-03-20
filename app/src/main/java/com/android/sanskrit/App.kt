package com.android.sanskrit

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.webkit.WebView
import cn.jpush.android.api.JPushInterface
import cn.jpush.im.android.api.JMessageClient
import com.alibaba.sdk.android.httpdns.HttpDns
import com.alibaba.sdk.android.man.MANServiceProvider
import com.android.base.BaseApplication
import com.android.cache.HttpProxyCacheServer
import com.android.http.oss.OssClient
import com.android.utils.FileUtil
import com.android.utils.LogUtil
import com.tencent.bugly.crashreport.CrashReport


/**
 * created by jiangshide on 2020/3/3.
 * email:18311271399@163.com
 */
class App : BaseApplication() {

    private var httpProxyCacheServer: HttpProxyCacheServer? = null

    override fun onCreate() {
        super.onCreate()
//        thread(start = true) {
        OssClient.instance
            .init()
//        }
        initManService()
        initHttpDnsService()
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
        JMessageClient.init(this, true)

        CrashReport.setAppChannel(this,BuildConfig.CHANNEL)
        CrashReport.initCrashReport(applicationContext, "8c5355a1a5", true)


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            val processName =   getProName()
            if("com.android.sanskrit" != processName){
                WebView.setDataDirectorySuffix(processName)
            }
        }
//    Login.getInstance()
//        .regToWx()
//    Push.getInstance()
//        .setChannel("android")
//        .setTags(AppUtil.getAppName(), "android")
//        .setListener {
//          LogUtil.e("pushBean:", it)
//        }
//        .init(applicationContext, true)
//    AudioService.startAudioService()

//    init(this)
    }

    fun getProName():String{
        val activityManager:ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager?.runningAppProcesses?.forEach {
            if(it.pid == Process.myPid()){
                return it.processName
            }
        }
        return ""
    }

    private fun initManService() {
        /**
         * 初始化Mobile Analytics服务
         */
        // 获取MAN服务
        val manService = MANServiceProvider.getService()
        // 打开调试日志
        manService.manAnalytics.turnOnDebug()
        manService.manAnalytics.setAppVersion("3.0")
        // MAN初始化方法之一，通过插件接入后直接在下发json中获取appKey和appSecret初始化
        manService.manAnalytics.init(this, applicationContext)
        // MAN另一初始化方法，手动指定appKey和appSecret
        // String appKey = "******";
        // String appSecret = "******";
        // manService.getMANAnalytics().init(this, getApplicationContext(), appKey, appSecret);
        // 若需要关闭 SDK 的自动异常捕获功能可进行如下操作,详见文档5.4
        //manService.getMANAnalytics().turnOffCrashReporter();
        // 通过此接口关闭页面自动打点功能，详见文档4.2
        manService.manAnalytics.turnOffAutoPageTrack()
        // 设置渠道（用以标记该app的分发渠道名称），如果不关心可以不设置即不调用该接口，渠道设置将影响控制台【渠道分析】栏目的报表展现。如果文档3.3章节更能满足您渠道配置的需求，就不要调用此方法，按照3.3进行配置即可
        manService.manAnalytics.setChannel(BuildConfig.CHANNEL)
        // 若AndroidManifest.xml 中的 android:versionName 不能满足需求，可在此指定；
        // 若既没有设置AndroidManifest.xml 中的 android:versionName，也没有调用setAppVersion，appVersion则为null
        //manService.getMANAnalytics().setAppVersion("2.0");
    }

    fun getAudioUrl(url: String): String? {
        if (url?.contains("http") || url?.contains("https")) {
            if (httpProxyCacheServer == null) {
                httpProxyCacheServer = HttpProxyCacheServer(this)
            }
            return httpProxyCacheServer?.getProxyUrl(url)
        }
        return url
    }

    private fun initHttpDnsService() {
        // 初始化httpdns
        //HttpDnsService httpdns = HttpDns.getService(getApplicationContext(), accountID);
        val httpdns = HttpDns.getService(applicationContext)
        //this.setPreResoveHosts();
        // 允许过期IP以实现懒加载策略
        //httpdns.setExpiredIPEnabled(true);
    }
}