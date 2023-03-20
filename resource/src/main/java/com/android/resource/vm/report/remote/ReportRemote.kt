package com.android.resource.vm.report.remote

import com.android.http.vm.data.RespData
import com.android.resource.Resource
import com.android.resource.vm.report.data.Type
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * created by jiangshide on 2020/4/5.
 * email:18311271399@163.com
 */
interface ReportRemote {

  @GET("api/report/type")
  fun types(): Observable<Response<RespData<MutableList<Type>>>>

  @FormUrlEncoded
  @POST("api/report/add")
  fun reportAdd(
    @Field("id") id: Long = 0,
    @Field("uid") uid: Long? = Resource.uid,
    @Field("contentId") contentId: Long?,
    @Field("type") type: Int,
    @Field("source") source: Int,
    @Field("status") status: Int,
    @Field("reason") reason: String?
  ): Observable<Response<RespData<Int>>>

  @FormUrlEncoded
  @POST("api/report/feedback")
  fun feedback(
    @Field("uid") uid: Long? = Resource.uid,
    @Field("contentId") contentId: Long?=0,
    @Field("content") content: String,
    @Field("url") url: String? = "",
    @Field("contact") contact: String,
    @Field("source") source: Int?=0
  ): Observable<Response<RespData<Long>>>
}