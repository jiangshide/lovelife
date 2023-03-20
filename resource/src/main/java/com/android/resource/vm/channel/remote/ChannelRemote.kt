package com.android.resource.vm.channel.remote

import com.android.http.vm.data.RespData
import com.android.resource.Resource
import com.android.resource.vm.channel.data.Channel
import com.android.resource.vm.channel.data.ChannelBlog
import com.android.resource.vm.channel.data.ChannelNature
import com.android.resource.vm.channel.data.ChannelType
import com.android.resource.vm.channel.data.Word
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
interface ChannelRemote {

  @GET("api/channel/follow")
  fun follow(
    @Query("uid")uid:Long?=Resource.uid,
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int
  ):Observable<Response<RespData<MutableList<ChannelBlog>>>>

  @GET("api/channel/id")
  fun channelId(
    @Query("id") id: Long?,
    @Query("uid") uid: Long?,
    @Query("blogId") blogId: Long,
    @Query("fromId") fromId: Long? = Resource.uid
  ): Observable<Response<RespData<Channel>>>

  @GET("api/channel/search")
  fun search(
    @Query("word") word: String,
    @Query("uid") uid: Long?,
    @Query("source") source: Int?,
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int
  ): Observable<Response<RespData<MutableList<ChannelBlog>>>>

  @GET("api/format/word")
  fun word(
    @Query("uid") uid: Long?,
    @Query("source") source: Int=0
  ): Observable<Response<RespData<MutableList<Word>>>>

  @GET("/api/channel")
  fun channel(
    @Query("status") status: Int?,
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int
  ): Observable<Response<RespData<MutableList<ChannelBlog>>>>

  @GET("/api/channel/official")
  fun channelOfficial(
    @Query("official") official: Int?,
    @Query("uid") uid: Long? = Resource.uid,
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int
  ): Observable<Response<RespData<MutableList<ChannelBlog>>>>

  @GET("/api/channel/channelId")
  fun channelId(
    @Query("status") status: Int?,
    @Query("id") uid: Long?,
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int
  ): Observable<Response<RespData<MutableList<Channel>>>>

  @GET("/api/channel/channelType")
  fun channelType(
    @Query("status") status: Int?,
    @Query("id") id: Int?,
    @Query("name") name: String = "",
    @Query("uid") uid: Long? = Resource.uid,
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int
  ): Observable<Response<RespData<MutableList<ChannelBlog>>>>

  @GET("/api/channel/user")
  fun channelUser(
    @Query("status") status: Int?,
    @Query("uid") uid: Long?,
    @Query("fromUid")fromUid:Long?,
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int
  ): Observable<Response<RespData<MutableList<ChannelBlog>>>>

  @GET("api/channel/recommend")
  fun channelRecommend(
    @Query("status") status: Int,
    @Query("uid") uid: Long?,
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int
  ): Observable<Response<RespData<MutableList<ChannelBlog>>>>

  @GET("/api/channel/type")
  fun channelType(
    @Query("uid") uid: Long?
  ): Observable<Response<RespData<ArrayList<ChannelType>>>>

  @GET("/api/channel/nature")
  fun channelNature(
    @Query("uid") uid: Long? = Resource.uid
  ): Observable<Response<RespData<MutableList<ChannelNature>>>>

  @FormUrlEncoded
  @POST("/api/channel/add")
  fun createChannel(
    @Field("id") id: Long?,
    @Field("uid") uid: Long? = Resource.uid,
    @Field("channelNatureId") channelNatureId: Int,
    @Field("name") name: String,
    @Field("cover") cover: String,
    @Field("des") des: String
  ): Observable<Response<RespData<Long>>>
}