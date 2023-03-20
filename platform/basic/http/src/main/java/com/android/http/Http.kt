package com.android.http

import android.app.Application
import com.android.http.codec.BooleanTypeAdapter
import com.android.http.download.Download
import com.android.http.interceptor.AuthInterceptor
import com.android.http.interceptor.OfflineCacheInterceptor
import com.android.network.NetWork
import com.android.utils.FileUtil
import com.android.utils.LogUtil
import com.android.utils.SPUtil
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * created by jiangshide on 2019-12-05.
 * email:18311271399@163.com
 */
object Http {
    @Volatile
    private var instance: Http? = null

    private var application: Application? = null

    private var retrofit: Retrofit? = null

    private var download: Download? = null

    fun getInstance(): Http? {
        if (null == instance) {
            synchronized(Http::class.java) {
                if (null == instance) {
                    instance = Http
                }
            }
        }
        return instance
    }

    public const val TOKEN = "token"

    open var token: String?
        get() = SPUtil.getString(TOKEN)
        set(token) {
            SPUtil.putString(TOKEN, token!!)
        }

    fun init(application: Application) {
        this.application = application
        this.download = Download()
        NetWork.instance.init(application)//初始化网络监听模块

        val loggingInterceptor = HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { message ->
                LogUtil.i("message:", message)
            }
        )
        val cache = Cache(FileUtil.getHttpDir(application), 1024 * 1024 * 1024)
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor())
            .addInterceptor(OfflineCacheInterceptor())
            .cache(cache)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder().registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
            .create()
        val host = BuildConfig.API_HOST
        retrofit = Retrofit.Builder()
            .baseUrl(host)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    fun <T> createService(clazz: Class<T>): T {
        return retrofit!!.create(clazz)
    }

    fun dowload(): Download {
        return this!!.download!!
    }

    fun download(
        url: String,
        destFileDir: String,
        destFileName: String
    ) {
        download?.download(url, destFileDir, destFileName)
    }

    fun download(
        url: String,
        destFileDir: String,
        destFileName: String,
        listener: Download.OnDownloadListener
    ) {
        download?.download(url, destFileDir, destFileName, listener)
    }
}
