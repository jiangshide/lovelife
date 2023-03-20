package com.android.files

import android.database.Cursor
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.provider.MediaStore
import android.provider.MediaStore.*
import android.provider.MediaStore.Files.FileColumns
import android.provider.MediaStore.Images.ImageColumns
import android.provider.MediaStore.Images.Media
import android.text.TextUtils
import com.android.base.BaseActivity
import com.android.base.BaseFragment
import com.android.files.R.color
import com.android.files.fragment.FileFragment
import com.android.files.fragment.OnFileFinishListener
import com.android.utils.AppUtil
import com.android.utils.FileUtil
import com.android.utils.LogUtil
import com.android.utils.data.*
import java.util.*

/**
 * created by jiangshide on 2020-01-09.
 * email:18311271399@163.com
 */
class Files() : Parcelable {
    var mTitle: String? = "图片"
    var mTopColor = 0
    var mTitleColor = 0
    var mRightTxt: String? = "完成"
    var mRightTxtColor = 0
    var mTabNormalColor = 0
    var mTabSelectedColor = 0
    var mTabLineColor = 0
    var mCrop = false
    var mFloat = false
    var mSelectedFiles: MutableList<FileData>? = null
    var mMax = 9
    var mSpaceCount = 3
    var fromFragment = true
    var mType = IMG
    var mDocType: Array<String>? =
        arrayOf("text/plain", "application/pdf", "application/msword", "application/vnd.ms-excel")

    var mFileFinishListener: OnFileFinishListener? = null

    //    arrayOf(".pdf",".txt",".apk",".word")
    private var mColumns: Array<String>? = arrayOf(
        FileColumns._ID,
        FileColumns.BUCKET_DISPLAY_NAME,
        FileColumns.TITLE,
        FileColumns.DATA,
        FileColumns.SIZE,
        FileColumns.DATE_ADDED,
        FileColumns.DATE_MODIFIED,
        Media.LATITUDE,
        Media.LONGITUDE,
        Media.WIDTH,
        Media.HEIGHT
    )
    var mSelection: String? = null
    var mSelectionArgs: Array<String>? = null
    var mSortOrder: String? =
        FileColumns.DATE_ADDED + " desc"
    var mOnFloatListener: OnFloatListener? = null

    fun setTitle(title: String?): Files {
        mTitle = title
        return this
    }

    fun setTopColor(color: Int): Files {
        mTopColor = color
        return this
    }

    fun setTitleColor(color: Int): Files {
        mTitleColor = color
        return this
    }

    fun setRightTxt(txt: Int): Files {
        return this.setRightTxt(
            AppUtil.getApplicationContext()
                .resources
                .getString(txt)
        )
    }

    fun setRightTxt(txt: String): Files {
        mRightTxt = txt
        return this
    }

    fun setRightColor(color: Int): Files {
        mRightTxtColor = color
        return this
    }

    fun setTabNormalColor(tabNormalColor: Int): Files {
        mTabNormalColor = tabNormalColor
        return this
    }

    fun setTabSelectedColor(tabSelectedColor: Int): Files {
        mTabSelectedColor = tabSelectedColor
        return this
    }

    fun setTabLineColor(tabLineColor: Int): Files {
        mTabLineColor = tabLineColor
        return this
    }

    fun setCrop(crop: Boolean): Files {
        mCrop = crop
        return this
    }

    fun setFloat(float: Boolean): Files {
        mFloat = float
        return this
    }

    fun setType(type: Int): Files {
        mType = type
        when (mType) {
            IMG -> {
                mTitle = "图片"
            }
            AUDIO -> {
                mTitle = "音频"
                mColumns = arrayOf(
                    FileColumns._ID,
//            FileColumns.BUCKET_DISPLAY_NAME,
                    FileColumns.TITLE,
                    FileColumns.DURATION,
                    FileColumns.DATA,
                    FileColumns.SIZE,
                    FileColumns.DATE_ADDED,
                    FileColumns.DATE_MODIFIED
                )
            }
            VIDEO -> {
                mTitle = "视频"
                mColumns = arrayOf(
                    FileColumns._ID,
                    FileColumns.BUCKET_DISPLAY_NAME,
                    FileColumns.TITLE,
                    FileColumns.DURATION,
                    FileColumns.DATA,
                    FileColumns.SIZE,
                    FileColumns.DATE_ADDED,
                    FileColumns.DATE_MODIFIED
                )
            }
            DOC -> {
                mTitle = "文档"

            }
        }
        return this
    }

    fun setDocType(docType: Array<String>): Files {
        mDocType = docType
        return this
    }

    fun setColumns(columns: Array<String>): Files {
        mColumns = columns
        return this
    }

    fun setSelection(selection: String?): Files {
        mSelection = selection
        return this
    }

    fun setSelection(vararg sufixs: String): Files {
        val list: MutableList<String> = ArrayList()
        for (sufix in sufixs) {
            list.add(sufix)
        }
        return setSelection(list)
    }

    fun setSelection(sufixs: List<String>): Files {
        var selection = ""
        for (i in sufixs.indices) {
            if (i != 0) {
                selection = "$selection OR "
            }
            selection =
                selection + FileColumns.DATA + " LIKE '%" + sufixs[i] + "'"
        }
        mSelection = selection
        return this
    }

    fun setSelectionArgs(selectionArgs: Array<String>): Files {
        mSelectionArgs = selectionArgs
        return this
    }

    fun setSortOrder(sortOrder: String): Files {
        mSortOrder = sortOrder
        return this
    }

    fun setFloatListener(listener: OnFloatListener): Files {
        this.mOnFloatListener = listener
        return this
    }

    fun setSingleListener(listener: OnFileListener): Files {
        mOnFileListener = listener
        mOnFilesListener = null
        mMax = 1
        return this
    }

    fun setMultipleListener(listener: OnFilesListener): Files {
        mOnFilesListener = listener
        mOnFileListener = null
        return this
    }

    fun setFileFinish(listener: OnFileFinishListener): Files {
        mFileFinishListener = listener
        return this
    }

    fun setSelectedFile(fileData: FileData? = null): Files {
        if (mSelectedFiles == null) {
            mSelectedFiles = ArrayList()
        }
        mSelectedFiles!!.clear()
        if (fileData != null) {
            mSelectedFiles!!.add(fileData!!)
        }
        return this
    }

    fun setSelectedFile(files: ArrayList<FileData>?): Files {
        mSelectedFiles = files
        return this
    }

    fun setMax(max: Int): Files {
        mMax = max
        return this
    }

    fun setSpaceCount(spaceCount: Int): Files {
        mSpaceCount = spaceCount
        return this
    }

    fun open(`object`: Any) {
        if (mSelectedFiles == null) {
            mSelectedFiles = ArrayList()
        }
        mTopColor = if (mTopColor == 0) color.white else mTopColor
        mTitleColor = if (mTitleColor == 0) color.black else mTitleColor
        mRightTxtColor = if (mRightTxtColor == 0) color.black else mRightTxtColor
        mTabNormalColor = if (mTabNormalColor == 0) color.black else mTabNormalColor
        mTabSelectedColor =
            if (mTabSelectedColor == 0) color.white else mTabSelectedColor
        mTabLineColor = if (mTabLineColor == 0) color.red else mTabLineColor
        val bundle = Bundle()
        bundle.putParcelable("files", this)
        if (`object` is BaseFragment) {
            fromFragment = true
            `object`.push(FileFragment(mFileFinishListener), bundle)
        } else {
            fromFragment = false
            (`object` as BaseActivity).goFragment(FileFragment::class.java, bundle)
        }
    }

    val uri: Uri
        get() = getUri(IMG)

    fun getUri(type: Int): Uri {
        if (type == AUDIO) return Audio.Media.getContentUri(
            "external"
        )
        if (type == VIDEO) return Video.Media.getContentUri(
            "external"
        )
        if (type == DOC) return MediaStore.Files.getContentUri(
            "external"
        )
        if (type == DOWNLOADS) {
            if (VERSION.SDK_INT >= VERSION_CODES.Q) {
                return Downloads.getContentUri("external")
            }
        }
        return Media.getContentUri("external")
    }

    fun selectionDoc(): String {
        val selection = StringBuilder()
        mDocType?.forEach {
            selection.append(
                "(" + FileColumns.MIME_TYPE + "=='" + it + "') OR "
            )
        }
        return selection.substring(0, selection.lastIndexOf(")") + 1)
    }

    constructor(parcel: Parcel) : this() {
        mTitle = parcel.readString()
        mTopColor = parcel.readInt()
        mTitleColor = parcel.readInt()
        mRightTxt = parcel.readString()
        mRightTxtColor = parcel.readInt()
        mTabNormalColor = parcel.readInt()
        mTabSelectedColor = parcel.readInt()
        mTabLineColor = parcel.readInt()
        mCrop = parcel.readByte() != 0.toByte()
        mFloat = parcel.readByte() != 0.toByte()
        mSelectedFiles = parcel.createTypedArrayList(FileData)
        mMax = parcel.readInt()
        mSpaceCount = parcel.readInt()
        fromFragment = parcel.readByte() != 0.toByte()
        mType = parcel.readInt()
        mDocType = parcel.createStringArray()
        mColumns = parcel.createStringArray()
        mSelection = parcel.readString()
        mSelectionArgs = parcel.createStringArray()
        mSortOrder = parcel.readString()
    }

    fun fileList(): List<List<FileData>> {
        return fileList(mType, mColumns, mSelection, mSelectionArgs, mSortOrder)
    }

    fun fileList(type: Int): List<List<FileData>> {
        return fileList(type, mColumns, mSelection, mSelectionArgs, mSortOrder)
    }

    fun fileList(
        type: Int,
        selection: String?
    ): List<List<FileData>> {
        return fileList(type, mColumns, selection, mSelectionArgs, mSortOrder)
    }

    fun fileList(
        type: Int,
        selection: String?,
        sortOrder: String?
    ): List<List<FileData>> {
        return fileList(type, mColumns, selection, mSelectionArgs, sortOrder)
    }

    fun fileList(
        type: Int,
        columns: Array<String>?,
        selection: String?,
        mSectionArgs: Array<String>?,
        sortOrder: String?
    ): List<List<FileData>> {
        var selection = selection
        if (type == DOC && TextUtils.isEmpty(selection)) {
            selection = selectionDoc()
        }
        val hashMap =
            HashMap<String, MutableList<FileData>>()
        var cursor: Cursor? = null
        try {
            cursor = AppUtil.getApplicationContext()
                .contentResolver
                .query(
                    getUri(type), columns, selection, mSectionArgs, sortOrder
                )
            while (cursor!!.moveToNext()) {
                val fileData = FileData()
                fileData.format = type
                fileData.id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FileColumns._ID)
                )
                fileData.path = cursor.getString(
                    cursor.getColumnIndexOrThrow(FileColumns.DATA)
                )
                fileData.sufix = FileUtil.getFileName(fileData.path)
                fileData.size = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FileColumns.SIZE)
                )
                fileData.name = cursor.getString(
                    cursor.getColumnIndexOrThrow(MediaColumns.TITLE)
                )
                fileData.dateModified = cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        FileColumns.DATE_MODIFIED
                    )
                )
                fileData.dateAdded = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FileColumns.DATE_ADDED)
                )

//        fileData.latitude = cursor.getFloat(
//            cursor.getColumnIndexOrThrow(Media.LATITUDE)
//        )
//        fileData.longitude = cursor.getFloat(
//            cursor.getColumnIndexOrThrow(Media.LONGITUDE)
//        )
                fileData.dir = DIR[type]
                when (type) {
                    IMG -> {
                        fileData.bucketName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                ImageColumns.BUCKET_DISPLAY_NAME
                            )
                        )
                        fileData.width = cursor.getInt(
                            cursor.getColumnIndexOrThrow(FileColumns.WIDTH)
                        )
                        fileData.height = cursor.getInt(
                            cursor.getColumnIndexOrThrow(FileColumns.HEIGHT)
                        )
                    }
                    VIDEO -> {
                        fileData.bucketName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                ImageColumns.BUCKET_DISPLAY_NAME
                            )
                        )
                        fileData.duration = cursor.getLong(
                            cursor.getColumnIndexOrThrow(FileColumns.DURATION)
                        )
//          val bounds = ImgUtil.getVideoBounds(fileData.path)
//          fileData.width = bounds[0]
//          fileData.height = bounds[1]
//          fileData.rotate = bounds[2]
//          fileData.duration = bounds[3]
//              .toLong()
//          fileData.bitrate = bounds[4]
                    }
                    AUDIO -> {
                        fileData.duration = cursor.getLong(
                            cursor.getColumnIndexOrThrow(FileColumns.DURATION)
                        )
                    }
                    DOC -> {
//            val sufix = FileUtil.getFileSufix(fileData.path)
//            if (!TextUtils.isEmpty(sufix)) {
//              fileData.dir = sufix
//            }
                    }

                }
                if (TextUtils.isEmpty(fileData.bucketName)) {
                    fileData.bucketName = "jsd"
                }
                if (hashMap.containsKey(fileData.bucketName)) {
                    val data =
                        hashMap[fileData.bucketName]!!
                    data.add(fileData)
                    hashMap[fileData.bucketName!!] = data
                } else {
                    val data: MutableList<FileData> =
                        ArrayList()
                    hashMap[fileData.bucketName!!] = data
                }
            }
        } catch (e: Exception) {
            LogUtil.e(e)
        } finally {
            cursor?.close()
        }
        val data: MutableList<List<FileData>> =
            ArrayList()
        for ((_, value) in hashMap) {
            if (value.size > 0) {
                data.add(value)
            }
        }
        return data
    }

    interface OnFloatListener {
        fun onFloat(type: Int)
    }

    interface OnFileListener {
        fun onFile(fileData: FileData)
    }

    interface OnFilesListener {
        fun onFiles(data: MutableList<FileData>)
    }

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int
    ) {
        parcel.writeString(mTitle)
        parcel.writeInt(mTopColor)
        parcel.writeInt(mTitleColor)
        parcel.writeString(mRightTxt)
        parcel.writeInt(mRightTxtColor)
        parcel.writeInt(mTabNormalColor)
        parcel.writeInt(mTabSelectedColor)
        parcel.writeInt(mTabLineColor)
        parcel.writeByte(if (mCrop) 1 else 0)
        parcel.writeByte(if (mFloat) 1 else 0)
        parcel.writeTypedList(mSelectedFiles)
        parcel.writeInt(mMax)
        parcel.writeInt(mSpaceCount)
        parcel.writeByte(if (fromFragment) 1 else 0)
        parcel.writeInt(mType)
        parcel.writeStringArray(mDocType)
        parcel.writeStringArray(mColumns)
        parcel.writeString(mSelection)
        parcel.writeStringArray(mSelectionArgs)
        parcel.writeString(mSortOrder)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Files> {
        override fun createFromParcel(parcel: Parcel): Files {
            return Files(parcel)
        }

        override fun newArray(size: Int): Array<Files?> {
            return arrayOfNulls(size)
        }

        var mOnFilesListener: OnFilesListener? = null
        var mOnFileListener: OnFileListener? = null

    }

}

var DIR = listOf("img", "audio", "video", "doc", "web", "vr", "downloads", "camera")

const val RECODER_VIDEO_DURATION = "recoderVideoDuration"