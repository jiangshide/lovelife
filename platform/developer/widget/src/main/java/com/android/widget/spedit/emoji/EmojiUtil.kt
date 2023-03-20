package com.android.widget.spedit.emoji

import android.content.Context
import net.lingala.zip4j.core.ZipFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * created by jiangshide on 2020/5/11.
 * email:18311271399@163.com
 */
class EmojiUtil{
  companion object {
    val EMOJI = "emoji"
    fun getEmojiDir(context: Context): File {
      val f = File(context.filesDir, EMOJI)
      if (!f.exists()) {
        f.mkdirs()
      }
      return f
    }


    fun unzipFromAssets(context: Context, dir: File, name: String): Boolean {
      val path = File(dir, name)

      val tmp = File(dir, "android_tmp")
      val zip = File(dir, "$name.zip")
      try {
        val `is` = context.assets.open("$name.zip")
        inputStreamToFile(`is`, zip)
        `is`.close()
        unzipAllFile(zip, tmp.absolutePath)
        zip.delete()
        if (path.exists()) {
          deleteDir(path)
        }
        path.mkdirs()
        return renameFile(tmp, path)
      } catch (e: Exception) {
        e.printStackTrace()
        if (tmp.exists()) {
          deleteDir(tmp)
        }
        return false
      }

    }


    fun deleteDir(dir: File): Boolean {
      if (dir.isDirectory) {
        val files = dir.listFiles()
        if (files != null) {
          for (file in files) {
            val success = deleteDir(file)
            if (!success) {
              return false
            }
          }
        }
      }
      return dir.delete()
    }

    fun renameFile(oldFile: File?, newFile: File?): Boolean {
      if (oldFile == null || !oldFile.exists()) {
        return false
      }
      if (newFile == null) {
        return false
      }
      if (oldFile == newFile) {
        return false
      }
      return if (newFile.exists() && !newFile.delete()) {
        false
      } else oldFile.renameTo(newFile)
    }

    @Throws(Exception::class)
    fun unzipAllFile(zip: File, dir: String) {
      val zipFile = ZipFile(zip)
      zipFile.setFileNameCharset("utf-8")
      zipFile.extractAll(dir)
    }

    @Throws(IOException::class)
    fun inputStreamToFile(inputStream: InputStream, file: File) {
      if (!file.exists()) {
        file.createNewFile()
      }
      val fileOutputStream = FileOutputStream(file)
      inputStreamToOutputStream(inputStream, fileOutputStream)
      fileOutputStream.close()
    }

    @Throws(IOException::class)
    fun inputStreamToOutputStream(inputStream: InputStream, outputStream: OutputStream) {
      inputStream.copyTo(outputStream)
    }

    private val TAG = "sp_data"
    private val EMOJI_INIT = "emoji_init"
    fun getEmojiInitResult(context: Context): Boolean {
      val pref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
      return pref.getBoolean(EMOJI_INIT, false)
    }

    fun setEmojiInitResult(context: Context, initResult: Boolean) {
      val pref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
      pref.edit().putBoolean(EMOJI_INIT, initResult).apply()
    }

    fun dp2px(var0: Context, var1: Float): Int {
      val var2 = var0.resources.displayMetrics.density
      return (var1 * var2 + 0.5f).toInt()
    }

    fun px2dip(var0: Context, var1: Float): Int {
      val var2 = var0.resources.displayMetrics.density
      return (var1 / var2 + 0.5f).toInt()
    }

    fun sp2px(var0: Context, var1: Float): Int {
      val var2 = var0.resources.displayMetrics.scaledDensity
      return (var1 * var2 + 0.5f).toInt()
    }

    fun px2sp(var0: Context, var1: Float): Int {
      val var2 = var0.resources.displayMetrics.scaledDensity
      return (var1 / var2 + 0.5f).toInt()
    }
  }

}