//package com.android.resource.oss
//
//import android.os.Handler
//import android.os.Message
//import com.alibaba.sdk.android.oss.ClientConfiguration
//import com.alibaba.sdk.android.oss.ClientException
//import com.alibaba.sdk.android.oss.OSS
//import com.alibaba.sdk.android.oss.OSSClient
//import com.alibaba.sdk.android.oss.ServiceException
//import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
//import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider
//import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
//import com.alibaba.sdk.android.oss.model.CannedAccessControlList.PublicRead
//import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult
//import com.alibaba.sdk.android.oss.model.CreateBucketRequest
//import com.alibaba.sdk.android.oss.model.CreateBucketResult
//import com.alibaba.sdk.android.oss.model.GetObjectRequest
//import com.alibaba.sdk.android.oss.model.GetObjectResult
//import com.alibaba.sdk.android.oss.model.MultipartUploadRequest
//import com.android.http.oss.OssClient
//import com.android.resource.BuildConfig
//import com.android.utils.AppUtil
//import com.android.utils.FileUtil
//import com.android.utils.LogUtil
//import com.android.utils.data.FileData
//import java.io.File
//import java.io.IOException
//
///**
// * created by jiangshide on 2020/3/1.
// * email:18311271399@163.com
// */
//class OssClient private constructor() :Runnable {
//  private object OssClientHelp {
//    val instance = com.android.http.oss.OssClient()
//  }
//
//  private var oss: OSS? = null
//  private var mHttpListener: HttpListener? = null
//  private var mHttpFileListener: HttpFileListener? = null
//  private var mFileData: FileData? = null
//  fun init() {
//    //OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
//    //ClientConfiguration clientConfiguration = new ClientConfiguration();
//    //clientConfiguration.setConnectionTimeout(15 * 1000);
//    //clientConfiguration.setSocketTimeout(15 * 1000);
//    //clientConfiguration.setMaxConcurrentRequest(5);
//    //clientConfiguration.setMaxErrorRetry(2);
//    //clientConfiguration.setHttpDnsEnable(true);
//    ////clientConfiguration.setUserAgentMark("cusUserAgent");
//    //oss = new OSSClient(AppUtil.getApplicationContext(), BuildConfig.ENDPOINT, credentialProvider);
//
//    // 在移动端建议使用STS的方式初始化OSSClient。
//    val credentialProvider = OSSPlainTextAKSKCredentialProvider(
//        BuildConfig.ACCESSKEY_ID,
//        BuildConfig.ACCESSKEY_SECRET
//    )
//    val conf = ClientConfiguration()
//    conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒。
//    conf.socketTimeout = 15 * 1000 // socket超时，默认15秒。
//    conf.maxConcurrentRequest = 5 // 最大并发请求书，默认5个。
//    conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次。
//    oss = OSSClient(
//        AppUtil.getApplicationContext(), BuildConfig.ENDPOINT,
//        credentialProvider,
//        conf
//    )
//  }
//
//  fun getOss(): OSS? {
//    if (oss == null) {
//      init()
//    }
//    return oss
//  }
//
//  override fun run() {
//    uploadMultipart()
//  }
//
//  fun start(){
//    Thread(this).start()
//  }
//
//  private val handler: Handler = Handler {
//    if (mHttpFileListener == null) return@Handler false
//    val fileData = it.obj as FileData
//    when (it.what) {
//      HTTP_PROGRESS -> {
//        mHttpFileListener?.onProgress(fileData.currentSize, fileData.totalSize, fileData.progress)
//      }
//      HTTP_SUCCESS -> {
//        mHttpFileListener?.onSuccess(fileData.position, fileData.url!!)
//      }
//      HTTP_FAILURE -> {
//        mHttpFileListener?.onFailure(fileData.clientException!!, fileData.serviceException!!)
//      }
//    }
//    return@Handler false
//  }
//
//  fun createBucket(bucketName: String?) {
//    val createBucketRequest = CreateBucketRequest(bucketName)
//    // 设置存储空间的访问权限为公共读，默认为私有读写。
//    createBucketRequest.bucketACL = PublicRead
//    // 指定存储空间所在的地域。
//    createBucketRequest.locationConstraint = "oss-cn-beijing"
//    val createTask: OSSAsyncTask<*> = getOss()!!.asyncCreateBucket(createBucketRequest,
//        object : OSSCompletedCallback<CreateBucketRequest, CreateBucketResult?> {
//          override fun onSuccess(
//            request: CreateBucketRequest,
//            result: CreateBucketResult?
//          ) {
//            LogUtil.d(request.locationConstraint)
//          }
//
//          override fun onFailure(
//            request: CreateBucketRequest,
//            clientException: ClientException,
//            serviceException: ServiceException
//          ) {
//            // 请求异常。
//            clientException?.printStackTrace()
//            if (serviceException != null) {
//              // 服务异常。
//              LogUtil.e(serviceException)
//            }
//          }
//        })
//  }
//
//  fun uploadMultipart(): OSSAsyncTask<*> {
//    val name = FileUtil.getFileName(mFileData?.path)
//    val request: MultipartUploadRequest<*> = MultipartUploadRequest<MultipartUploadRequest<*>?>(
//        BuildConfig.BUCKET, "${mFileData?.dir}/$name", mFileData?.path
//    )
//    request.setProgressCallback { multipartUploadRequest, currentSize, totalSize ->
////      if (mHttpFileListener != null) {
//      val msg = Message()
//      mFileData?.currentSize = currentSize
//      mFileData?.totalSize = totalSize
//      mFileData?.progress = ((currentSize / totalSize.toDouble()) * 100).toInt()
//      msg.what = HTTP_PROGRESS
//      msg.obj = mFileData
//      handler.sendMessage(msg)
////        mHttpFileListener!!.onProgress(currentSize, totalSize)
////      }
//    }
//    val task: OSSAsyncTask<*> = oss!!.asyncMultipartUpload(request,
//        object : OSSCompletedCallback<MultipartUploadRequest<*>, CompleteMultipartUploadResult> {
//          override fun onSuccess(
//            request: MultipartUploadRequest<*>,
//            result: CompleteMultipartUploadResult
//          ) {
//
//            LogUtil.e("request:", request, " | result:", result)
//
////            if (mHttpFileListener != null) {
//            //try {
//            //mHttpFileListener.onSuccess(
//            //    getOss().presignConstrainedObjectURL(result.getBucketName(),
//            //        request.getObjectKey(),
//            //        System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10));
//            //
//
//            val msg = Message()
//            mFileData?.url = getOss()!!.presignPublicObjectURL(result.bucketName, request.objectKey)
//            msg.what = HTTP_SUCCESS
//            msg.obj = mFileData
//            handler.sendMessage(msg)
////              mHttpFileListener!!.onSuccess(
////                  position,
////                  getOss()!!.presignPublicObjectURL(result.bucketName, request.objectKey)
////              )
//            //} catch (ClientException e) {
//            //  e.printStackTrace();
//            //  mHttpFileListener.onFailure(new ClientException(e), null);
//            //}
////            }
//          }
//
//          override fun onFailure(
//            request: MultipartUploadRequest<*>,
//            clientException: ClientException,
//            serviceException: ServiceException
//          ) {
//            LogUtil.e(
//                "request:", request, " | clientException:", clientException, " | serviceException:",
//                serviceException
//            )
//            val msg = Message()
//            mFileData?.clientException = clientException
//            mFileData?.serviceException = serviceException
//            msg.what = HTTP_FAILURE
//            msg.obj = mFileData
//            handler.sendMessage(msg)
////            if (mHttpFileListener != null) {
////              mHttpFileListener!!.onFailure(clientException, serviceException)
////            }
//          }
//        })
//
//    // Thread.sleep(100);
//    //取消分片上传。
//    // task.cancel();
//    task.waitUntilFinished()
//    return task
//  }
//
//  fun download(): OSSAsyncTask<*> {
//    // 构造下载文件请求。
//    val get = GetObjectRequest("<bucketName>", "<objectName>")
//
//    // task.cancel(); // 可以取消任务。
//    // task.waitUntilFinished(); // 等待任务完成。
//    return getOss()!!.asyncGetObject(
//        get, object : OSSCompletedCallback<GetObjectRequest?, GetObjectResult> {
//      override fun onSuccess(
//        request: GetObjectRequest?,
//        result: GetObjectResult
//      ) {
//        LogUtil.e("request", request, " | result:", result)
//        val inputStream = result.objectContent
//        val buffer = ByteArray(2048)
//        var len: Int
//        try {
//          while (inputStream.read(buffer)
//                  .also { len = it } != -1
//          ) {
//            // 您可以在此处编写代码来处理下载的数据。
//          }
//        } catch (e: IOException) {
//          e.printStackTrace()
//        }
//      }
//
//      // GetObject请求成功，将返回GetObjectResult，其持有一个输入流的实例。返回的输入流，请自行处理。
//      override fun onFailure(
//        request: GetObjectRequest?,
//        clientExcepion: ClientException,
//        serviceException: ServiceException
//      ) {
//        LogUtil.e(
//            "request:", request, " | clientExcepion:", clientExcepion,
//            " | serviceException:", serviceException
//        )
//      }
//    })
//  }
//
//  fun setFileData(path: File): OssClient {
//    return setFileData(path.absolutePath)
//  }
//
//  fun setFileData(path: String): OssClient {
//    val fileData = FileData()
//    fileData.path = path
//    return setFileData(fileData)
//  }
//
//  fun setFileData(fileData: FileData): OssClient {
//    mFileData = fileData
//    return this
//  }
//
//  fun setListener(listener: HttpListener?): OssClient {
//    mHttpListener = listener
//    return this
//  }
//
//  fun setListener(listener: HttpFileListener?): OssClient {
//    mHttpFileListener = listener
//    return this
//  }
//
//  interface HttpFileListener {
//    fun onProgress(
//      currentSize: Long,
//      totalSize: Long,
//      progress: Int
//    )
//
//    fun onSuccess(
//      position: Int,
//      url: String
//    )
//
//    fun onFailure(
//      clientExcepion: Exception,
//      serviceException: Exception
//    )
//  }
//
//  interface HttpListener {
//    fun onFile(fileData: FileData?)
//  }
//
//  companion object {
//    val instance: OssClient
//      get() = OssClientHelp.instance
//  }
//}
//
//const val HTTP_PROGRESS = 1
//const val HTTP_SUCCESS = 2
//const val HTTP_FAILURE = -1