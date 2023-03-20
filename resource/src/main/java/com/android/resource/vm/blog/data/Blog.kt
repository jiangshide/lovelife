package com.android.resource.vm.blog.data

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.amap.api.maps.model.LatLng
import com.android.http.oss.OssClient
import com.android.http.oss.OssClient.HttpFileListener
import com.android.img.Img
import com.android.location.ZdLocation
import com.android.resource.FOLLOW
import com.android.resource.MEN
import com.android.resource.R
import com.android.resource.Resource
import com.android.resource.WOMEN
import com.android.resource.data.Music
import com.android.resource.vm.publish.PublishVM
import com.android.resource.vm.publish.data.File
import com.android.utils.*
import com.android.utils.data.*
import com.android.widget.ZdButton
import com.android.widget.extension.setDrawableLeft

/**
 * created by jiangshide on 2019-10-07.
 * email:18311271399@163.com
 */
data class Blog(
    var id: Long = 0,
    var uid: Long = 0,
    var channelId: Long = 0,//频道ID
    var positionId: Long = 0,//当前位置ID
    var title: String? = "",//动态名称
    var des: String? = "",//动态描述
    var latitude: Double = 0.0,//精度
    var longitude: Double = 0.0,//纬度
    var locationType: String? = "",//定位类型
    var country: String? = "",//国家
    var city: String? = "",//城市
    var position: String? = "",//位置
    var address: String? = "",//详细位置
    var cityCode: String? = "",//城市编码
    var adCode: String? = ZdLocation.getInstance().adCode,//区域码
    var timeCone: String? = "",//时区
    var tag: String? = "",//标签
    var status: Int = 0,//状态:0~未审核,1~审核中,2~审核通过,-1~移到回忆箱,-2~审核拒绝,-3～禁言，-4~关闭/折叠,-5~被投诉
    var reason: String? = "",//原由
    var official: Int = 0,//官方推荐:-1~取消推荐,0~未推荐,1~推荐,2~特别推荐
    var url: String? = "",//文件Url
    var cover: String? = "",//封面
    var name: String? = "",//文件名称
    var sufix: String? = "",//文件名后缀
    var format: Int = 0,//内容格式:0:图片,1:音频,2:视频,3:文档,4:web,5:VR
    var duration: Long = 0,//内容时长
    var width: Int = 0,//内容宽
    var height: Int = 0,//内容高
    var size: Long = 0,//内容尺寸
    var rotate: Int = 0,//角度旋转
    var bitrate: Int = 0,//采用率
    var sampleRate: Int = 0,//频率
    var level: Int = 0,//质量:0~标准
    var mode: Int = 0,//模式
    var wave: String? = "",//频谱
    var lrcZh: String? = "",//字幕~中文
    var lrcEn: String? = "",//字母~英文
    var source: Int = 0,//创作类型:0~原创,1~其它
    var date: String? = "",//创建时间

    var icon: String? = "",//用户头像
    var nick: String? = "",//用户昵称
    var sex: Int = 0,//用户性别
    var age: Int = 0,//用户年龄
    var zodiac: String? = "",//用户生肖
    var ucity: String? = "",//用户出生城市

    var remark: String? = "",//备注名称

    var channel: String? = "",//频道名称

    var praiseNum: Int = 0,//点赞次数
    var viewNum: Int = 0,//预览次数
    var shareNum: Int = 0,//共享次数
    var commentNum: Int = 0,//评论次数

    //点赞
    var praises: Int = 0,//点赞状态
    //举报
    var reportr: String? = "",//举报原因
    //关注
    var follows: Int = 0,//关注状态
    //收藏
    var collections: Int = 0,//收藏状态
    //置顶
    var tops: Int = 0,//置顶状态
    //推荐
    var recommends: Int = 0,//推荐状态

    var urls: MutableList<File>? = null,
    var comments: MutableList<Comment>? = null,
    val ats: MutableList<At>? = null,

    var isPlay: Boolean = false,
    var index: Int = 0,
    var refresh: String? = "",
    var isSync: Boolean = true,
    var content: String? = "",
    var style: Style? = null,
    var path: String? = null,
    var collectionNum: Int = 0,//收藏次数
    var channelCover:String=""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArrayList(File),
        parcel.createTypedArrayList(Comment),
        parcel.createTypedArrayList(At),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    fun getFollow(): Boolean {
        return follows == FOLLOW
    }

    fun getMusic(): Music {
        val music = Music()
        music.audioWave = wave
        music.duration = duration.toInt()
        music.name = name
        music.url = url
        music.waveArray = getWave(wave!!)
        return music
    }

    fun syncStatus(
        publishVM: PublishVM,
        listener: OnSyncStatusListener
    ) {
        publishVM?.syncStatus(name = name, sufix = sufix, url = url, listener = listener)
    }

    fun syncBlog(
        id: Long,
        progressBar: ProgressBar,
        listener: OnSyncListener,
        publishVM: PublishVM
    ) {
        channelId = id
        val file = FileData()
        file.path = url
        file.format = AUDIO
        file.dir = "audio"
        OssClient.instance.setListener(object : HttpFileListener {
            override fun onProgress(
                currentSize: Long,
                totalSize: Long,
                progress: Int
            ) {
                progressBar?.visibility = View.VISIBLE
                progressBar?.max = totalSize.toInt()
                progressBar?.progress = currentSize.toInt()
                listener?.syncResult(0, url = url!!)
            }

            override fun onSuccess(
                position: Int,
                currUrl: String
            ) {
                url = currUrl
                val zdLocation = ZdLocation.getInstance()
                latitude = zdLocation.latitude
                longitude = zdLocation.longitude
                locationType = zdLocation.locationType
                adCode = zdLocation.adCode
                uid = Resource.uid!!
                publishVM?.syncBlog(this@Blog, listener)
            }

            override fun onFailure(
                clientExcepion: Exception,
                serviceException: Exception
            ) {
                listener?.syncResult(-1, url = url!!)
            }

        })
            .setFileData(file)
            .start()
    }

    fun getFile(): File {
        return File(
            url = url,
            cover = cover,
            name = name,
            sufix = sufix,
            format = format,
            duration = duration,
            width = width,
            height = height,
            size = size,
            rotate = rotate,
            bitrate = bitrate,
            sampleRate = sampleRate,
            level = level,
            mode = mode,
            wave = wave,
            lrcEs = lrcEn,
            lrcZh = lrcZh,
            source = source
        )
    }

    fun getFiles(): List<File> {
        val data = ArrayList<File>()
        data.add(getFile())
        if (urls != null) {
            data.addAll(urls!!)
        }
        return data
    }

    fun setDate(text: TextView) {
        text.text = DateUtil.showTimeAhead(DateUtil.stringToLong(date))
    }

    fun setUserInfo(
        img: ImageView,
        txt: TextView
    ) {
        Img.loadImageCircle(icon, img, R.mipmap.default_user)
        setSex(txt)
    }

    fun setSex(text: TextView) {
        val context = AppUtil.getApplicationContext().applicationContext
        when (sex) {
            MEN -> {
                text.setDrawableLeft(R.mipmap.sex_man)
            }
            WOMEN -> {
                text.setDrawableLeft(R.mipmap.sex_women)
            }
        }
        var str = ""
        if (age > 0) {
            str = context.getString(R.string.dot) + " $age"
        }
        if (!TextUtils.isEmpty(city)) {
            str += context.getString(R.string.dot) + " $city"
        }
        text.text = str
        StringUtil.setDot(text, context.getString(R.string.dot))
    }

    fun setImg(
        activity: Activity,
        formatImg: ImageView,
        sizeTxt: TextView? = null,
        coverImg: ImageView? = null,
        channelCover: String? = "",
        isRandom: Boolean
    ) {
        var url = url
        var height = ScreenUtil.getRtScreenHeight(activity) / 3
        when (format) {
            IMG -> {
                if (TextUtils.isEmpty(url) && urls != null && urls!!.size > 0) {
                    height = if (isRandom) urls!![0].randomH else urls!![0].height
                    url = urls!![0].url
                    if (sizeTxt != null) {
                        if (urls!!.size > 1) {
                            sizeTxt.text = "1/${urls!!.size}"
                        }
                    }
                }
                formatImg.setImageResource(R.mipmap.img)
            }
            AUDIO -> {
                url = getCoverUrl(channelCover)
                formatImg.setImageResource(R.mipmap.audio)
            }
            VIDEO -> {
                if (!TextUtils.isEmpty(cover)) {
                    url = cover
                }
                if (TextUtils.isEmpty(url) && urls != null && urls!!.size > 0) {
                    url = urls!![0].url
                }
                formatImg.setImageResource(R.mipmap.video)
            }
            DOC -> {

            }
            WEB -> {

            }
            VR -> {

            }
        }
        if (TextUtils.isEmpty(url)) {
            url = cover
        }
        if (coverImg != null) {
            val layoutParams =
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    height
                )
            coverImg?.layoutParams = layoutParams
            Img.loadImageRound(url, coverImg, 10)
        }
    }

    fun getLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    fun getCoverUrl(cv: String? = ""): String? {
        var urlStr = ""
        if (urls != null && urls!!.size > 0) {
            urls?.forEach {
                if (!TextUtils.isEmpty(it.url) && FileUtil.isImg(it.url)) {
                    urlStr = it.url!!
                } else if (!TextUtils.isEmpty(it.cover)) {
                    urlStr = it.cover!!
                }
            }
        }
        if (TextUtils.isEmpty(urlStr) && !TextUtils.isEmpty(url) && FileUtil.isImg(url)) {
            urlStr = url!!
        }
        if (TextUtils.isEmpty(urlStr) && !TextUtils.isEmpty(cover)) {
            urlStr = cover!!
        }

        if(TextUtils.isEmpty(urlStr)){
            urlStr = channelCover
        }

        if (TextUtils.isEmpty(urlStr) && !TextUtils.isEmpty(cv)) {
            urlStr = cv!!
        }
        if (TextUtils.isEmpty(urlStr)) {
            urlStr = icon!!
        }
        return urlStr
    }

    fun setCover(
        imageView: ImageView,
        zdButton: ZdButton
    ) {
        val cover = getCoverUrl()
        if (!TextUtils.isEmpty(cover)) {
            Img.loadImageCircle(cover, imageView)
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
            if (!TextUtils.isEmpty(name)) {
                val length: Int = name?.length!!
                zdButton.text = name?.substring(length - 1, length)
                zdButton.setRandomColor()
            }
        }
    }

    fun showNum(
        txtView: TextView,
        num: Int,
        praises: Int = -2
    ) {
        if (praises != -2) {
            txtView.isSelected = praises == 1
        }
        when {
            num >= 10000 -> {
                txtView?.text = "${num / 10000.0}万"
            }
            num >= 1000 -> {
                txtView?.text = "${num / 1000.0}千"
            }
            num > 0 -> {
                txtView?.text = "$num"
            }
            else -> {
//        txtView?.text = ""
            }
        }
    }

    fun getWave(wave: String): List<Int> {
        if (TextUtils.isEmpty(wave)) return emptyList()
        val byteArray = EncryptUtils.base64Decode(wave)
        val list = List(byteArray.size) { index ->
            val n = byteArray[index].toInt()
            if (n < 0) {
                return@List n + 256
            }
            return@List n
        }
        return list
    }

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int
    ) {
        parcel.writeLong(id)
        parcel.writeLong(uid)
        parcel.writeLong(channelId)
        parcel.writeLong(positionId)
        parcel.writeString(title)
        parcel.writeString(des)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(locationType)
        parcel.writeString(country)
        parcel.writeString(city)
        parcel.writeString(position)
        parcel.writeString(address)
        parcel.writeString(cityCode)
        parcel.writeString(adCode)
        parcel.writeString(timeCone)
        parcel.writeString(tag)
        parcel.writeInt(status)
        parcel.writeString(reason)
        parcel.writeInt(official)
        parcel.writeString(url)
        parcel.writeString(cover)
        parcel.writeString(name)
        parcel.writeString(sufix)
        parcel.writeInt(format)
        parcel.writeLong(duration)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeLong(size)
        parcel.writeInt(rotate)
        parcel.writeInt(bitrate)
        parcel.writeInt(sampleRate)
        parcel.writeInt(level)
        parcel.writeInt(mode)
        parcel.writeString(wave)
        parcel.writeString(lrcZh)
        parcel.writeString(lrcEn)
        parcel.writeInt(source)
        parcel.writeString(date)
        parcel.writeString(icon)
        parcel.writeString(nick)
        parcel.writeInt(sex)
        parcel.writeInt(age)
        parcel.writeString(zodiac)
        parcel.writeString(ucity)
        parcel.writeString(remark)
        parcel.writeString(channel)
        parcel.writeInt(praiseNum)
        parcel.writeInt(viewNum)
        parcel.writeInt(shareNum)
        parcel.writeInt(commentNum)
        parcel.writeInt(praises)
        parcel.writeString(reportr)
        parcel.writeInt(follows)
        parcel.writeInt(collections)
        parcel.writeInt(tops)
        parcel.writeInt(recommends)
        parcel.writeByte(if (isPlay) 1 else 0)
        parcel.writeInt(index)
        parcel.writeString(refresh)
        parcel.writeByte(if (isSync) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Blog(id=$id, uid=$uid, channelId=$channelId, positionId=$positionId, title=$title, des=$des, latitude=$latitude, longitude=$longitude, locationType=$locationType, country=$country, city=$city, position=$position, address=$address, cityCode=$cityCode, adCode=$adCode, timeCone=$timeCone, tag=$tag, status=$status, reason=$reason, official=$official, url=$url, cover=$cover, name=$name, sufix=$sufix, format=$format, duration=$duration, width=$width, height=$height, size=$size, rotate=$rotate, bitrate=$bitrate, sampleRate=$sampleRate, level=$level, mode=$mode, wave=$wave, lrcZh=$lrcZh, lrcEn=$lrcEn, source=$source, date=$date, icon=$icon, nick=$nick, sex=$sex, age=$age, zodiac=$zodiac, ucity=$ucity, remark=$remark, channel=$channel, praiseNum=$praiseNum, viewNum=$viewNum, shareNum=$shareNum, commentNum=$commentNum, praises=$praises, reportr=$reportr, follows=$follows, collections=$collections, tops=$tops, recommends=$recommends, urls=$urls, comments=$comments, isPlay=$isPlay, index=$index, refresh=$refresh, isSync=$isSync)"
    }

    companion object CREATOR : Creator<Blog> {
        override fun createFromParcel(parcel: Parcel): Blog {
            return Blog(parcel)
        }

        override fun newArray(size: Int): Array<Blog?> {
            return arrayOfNulls(size)
        }
    }

}

interface OnSyncListener {
    fun syncResult(
        status: Int,
        url: String
    )
}

interface OnSyncStatusListener {
    fun syncResult(
        status: Int,
        blog: Blog? = null
    )
}
