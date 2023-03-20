package com.android.resource.vm.publish.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.annotation.Keep

/**
 * created by jiangshide on 2020/3/30.
 * email:18311271399@163.com
 */
@Keep
data class File(
  var id: Long = 0,
  var contentId: Long = 0,//内容来源ID
  var classify: Int = 3,//内容分类:1~用户,2~频道,3~动态,4~评论
  var url: String? = "",//内容Url
  var cover:String?="",//文件封面
  var name: String? = "",//文件名称
  var sufix:String?="",//文件名后缀
  var format: Int = 0,//内容格式:0:图片,1:音频,2:视频,3:文档,4:web,5:VR
  var duration: Long = 0,//内容时长
  var width: Int = 0,//内容宽
  var height: Int = 0,//内容高
  var size: Long = 0,//内容尺寸
  var rotate: Int = 0,//角度旋转
  var bitrate: Int = 0,//采样率
  var sampleRate: Int = 0,//频率
  var level: Int = 0,//质量:0~标准
  var mode: Int = 0,//模式
  var wave: String? = "",//频谱
  var lrcZh: String? = "",//字幕~中文
  var lrcEs: String? = "",//字母~英文
  var source: Int = 0,//创作类型:0~原创,1~其它
  var randomH: Int=0//获取随机高度
) : Parcelable {
  constructor(parcel: Parcel) : this(
      parcel.readLong(),
      parcel.readLong(),
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
      parcel.readInt()
  ) {
  }

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(id)
    parcel.writeLong(contentId)
    parcel.writeInt(classify)
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
    parcel.writeString(lrcEs)
    parcel.writeInt(source)
    parcel.writeInt(randomH)
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun toString(): String {
    return "File(id=$id, contentId=$contentId, classify=$classify, url=$url, cover=$cover, name=$name, sufix=$sufix, format=$format, duration=$duration, width=$width, height=$height, size=$size, rotate=$rotate, bitrate=$bitrate, sampleRate=$sampleRate, level=$level, mode=$mode, wave=$wave, lrcZh=$lrcZh, lrcEs=$lrcEs, source=$source, randomH=$randomH)"
  }

  companion object CREATOR : Creator<File> {
    override fun createFromParcel(parcel: Parcel): File {
      return File(parcel)
    }

    override fun newArray(size: Int): Array<File?> {
      return arrayOfNulls(size)
    }
  }

}