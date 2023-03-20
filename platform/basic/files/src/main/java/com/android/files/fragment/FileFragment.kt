package com.android.files.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.text.TextUtils
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.base.BaseFragment
import com.android.files.Files
import com.android.files.R
import com.android.files.RECODER_VIDEO_DURATION
import com.android.player.dplay.player.VideoViewManager
import com.android.utils.*
import com.android.utils.data.*
import com.android.widget.adapter.AbstractAdapter.OnItemListener
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.file.*
import kotlinx.android.synthetic.main.file_item.view.*
import kotlinx.android.synthetic.main.file_item_dir.view.*
import kotlinx.android.synthetic.main.file_item_doc.view.*
import java.io.File
import java.io.IOException

/**
 * created by jiangshide on 2020-01-09.
 * email:18311271399@163.com
 */
class FileFragment(private val listener: OnFileFinishListener? = null) : BaseFragment(),
    OnItemListener<FileData> {

    private var files: Files? = null

    private var adapterDir: KAdapter<MutableList<FileData>>? = null
    private var adapter: KAdapter<FileData>? = null

    private var currentPhotoPath: File? = null
    private var currentFileName: String = ""

    private var uri: Uri? = null

    private var index = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTopView(SPUtil.getInt(File_SHOW_TOP, 0) == 0).setTitleView(R.layout.file)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setLeftListener {
            fileFinish()
        }
        files = arguments?.getParcelable("files")!!
        if (files?.mMax!! > 1) {
            setTitle("${files?.mTitle}(${files?.mMax}/${files?.mSelectedFiles!!.size})")
        } else {
            setTitle(files?.mTitle)
        }

        validate()
        fileSearchEdit.visibility = View.GONE
        fileFloatBtn.visibility = View.VISIBLE
        fileFloatBtn.visibility = if (files!!.mFloat) View.VISIBLE else View.GONE
        when (files?.mType) {
            IMG -> {
                fileFloatBtn.text = "拍照"
                fileFloatBtn.setOnClickListener {
                    if (files?.mOnFloatListener != null) {
                        fileFinish()
                        files?.mOnFloatListener!!.onFloat(files!!.mType)
                    } else {
                        goCamera()
                    }
                }
            }
            AUDIO -> {
                fileFloatBtn.text = "录音"
                fileFloatBtn.setOnClickListener {
                    fileFinish()
                    if (files?.mOnFloatListener != null) {
                        files?.mOnFloatListener!!.onFloat(files!!.mType)
                    }
                }
            }
            VIDEO -> {
                fileFloatBtn.text = "录像"
                fileFloatBtn.setOnClickListener {
                    if (files?.mOnFloatListener != null) {
                        fileFinish()
                        files?.mOnFloatListener!!.onFloat(files!!.mType)
                    } else {
                        goVideo()
                    }
                }
            }
            DOC -> {
                fileFloatBtn.visibility = View.GONE
                fileSearchEdit.visibility = View.VISIBLE
            }
        }
        load()
        showLoading()

        fileSearchEdit.setListener { s, input ->

        }
    }

    private val handler = Handler {
        hiddle()
        if (it.what == 1) {
            val data = it.obj as MutableList<MutableList<FileData>>
            if (data.size == 1) {
                if (files?.mType == DOC) {
                    showViewDoc(data[0])
                } else {
                    showView(data[0])
                }
            } else {
                showDir(it.obj as MutableList<MutableList<FileData>>)
            }
        }
        return@Handler false
    }

    private fun load() {
        Thread {
            kotlin.run {
                val list = files?.fileList()
                val msg = Message()
                msg.what = 1
                msg.obj = list
                handler.sendMessage(msg)
            }
        }.start()
    }

    private fun showDir(data: MutableList<MutableList<FileData>>) {
        if (data == null || data.size == 0) {
            noData()
            return
        }
        if (files?.mSelectedFiles != null && files?.mSelectedFiles!!.size > 0) {
            files?.mSelectedFiles!![0]?.selectedDirList?.forEach {
            }
        }
        val layoutManager = LinearLayoutManager(mActivity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        adapterDir = dirRecycleView?.create(data, R.layout.file_item_dir, {
            fileDirItemIcon.load(it[0].path)
            fileDirItemTitle.text = it[0].bucketName
            fileDirItemSize.text = "${it.size}"
            val fileData = selectedDir(it[0])
            if (fileData?.selectedDir!!) {
                fileDirItemSelected.visibility = View.VISIBLE
                fileDirItemBg.visibility = View.GONE
                fileDirItemSelected.text = "${fileData.selectedDirSize}"
            } else {
                fileDirItemSelected.visibility = View.GONE
                fileDirItemBg.visibility = View.VISIBLE
            }
        }, {}, layoutManager)
            ?.setItemListener(object : OnItemListener<MutableList<FileData>> {
                override fun onItem(
                    position: Int,
                    item: MutableList<FileData>
                ) {
                    index = position
                    if (files?.mType == DOC) {
                        showViewDoc(item)
                    } else {
                        showView(item)

                    }
                }
            })
        if (files?.mType == DOC) {
            showViewDoc(data[0])
        } else {
            showView(data[0])
        }
    }

    private fun selectedDir(fileData: FileData): FileData? {
        if (files?.mSelectedFiles == null || files?.mSelectedFiles?.size == 0) return fileData
        files?.mSelectedFiles!![0].selectedDirList?.forEach {
            if (fileData.path == it.path) {
                return it
            }
        }
        return fileData
    }

    private fun showViewDoc(data: MutableList<FileData>) {
        adapter = fileRecycleView.create(data, R.layout.file_item_doc, {
            setBackgroundResource(if (it.selected) R.color.gray else R.color.white)
            fileItemDocIcon.setImageResource(R.mipmap.icon_file_doc)
            fileItemDocType.text = "apk"
            var name = ""
            if (!TextUtils.isEmpty(it.name)) {
                name = it.name!!
            } else {
                name = FileUtil.getFileName(it.path)
            }
            fileItemDocName.text = name
            fileItemDocSize.text = Formatter.formatShortFileSize(context, it.size)
            fileItemDocCheck.visibility = if (it.selected) View.VISIBLE else View.GONE
            fileItemDocCheck.isChecked = it.selected
        }, {})
            .setItemListener(this)
    }

    private fun showView(data: MutableList<FileData>) {
        val layoutManager = GridLayoutManager(mActivity, files!!.mSpaceCount)
        adapter = fileRecycleView.create(data, R.layout.file_item, {
            if (it.format == IMG) {
                fileItemIcon.load(it.path)
                fileItemPlay.setImageResource(R.mipmap.img)
            } else if (it.format == AUDIO) {
                val bitmap = ImgUtil.loadCover(it.path)
                if (bitmap != null) {
                    fileItemIcon.setImageBitmap(bitmap)
                    it.cover = FileUtil.bitmap2File(bitmap, FileUtil.getFileName(it.path))
                } else {
                    fileItemIcon.setImageResource(R.mipmap.image_placeholder)
                }
                fileItemPlay.setImageResource(R.mipmap.audio)
            } else {
                fileItemIcon.load(it.path)
                fileItemPlay.setImageResource(R.mipmap.video)
                fileItemTime.text = DateUtil.formatSeconds(it.duration / 1000)
            }

            if (it.selected || selected(it)) {
                if (!fileItemCheck.isChecked) {
                    fileItemCheck.setChecked(true)
                }
                fileItemCheck.visibility = View.VISIBLE
                fileItemBg.visibility = View.VISIBLE
                it.selected = true
            } else {
                fileItemCheck.setChecked(false)
                fileItemBg.visibility = View.GONE
                fileItemCheck.visibility = View.GONE
            }
            fileItemDate.text = DateUtil.long2String(it.dateAdded * 1000, "yyyy-MM-dd HH:mm:ss")
        }, {

        }, layoutManager)
            .setItemListener(this)
    }

    private fun selected(fileData: FileData): Boolean {
        files?.mSelectedFiles?.forEach {
            if (fileData.path == it.path) return true
        }
        return return false
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == AppUtil.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (files!!.mCrop) {
                AppUtil.cropUri(this, uri, currentPhotoPath)
            } else {
                notifyMediaStoreDatabase(false)
                cameraResult(false)
            }
        } else if (requestCode == AppUtil.REQUEST_CODE_VIDEO) {
            notifyMediaStoreDatabase(true)
            cameraResult(true)
        } else if (requestCode == AppUtil.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            notifyMediaStoreDatabase(false)
            cameraResult(false)
        }
    }

    private fun cameraResult(isVideo: Boolean) {
        val fileData = FileData()
        fileData.path = currentPhotoPath?.absolutePath
        if (isVideo) {
            val bounds = ImgUtil.getVideoBounds(fileData.path)
            fileData.width = bounds[0]
            fileData.height = bounds[1]
            fileData.rotate = bounds[2]
            fileData.duration = bounds[3].toLong()
            fileData.bitrate = bounds[4]
            fileData.dir = "video"
            fileData.format = VIDEO
        } else {
            val bounds = ImgUtil.getBitmapBounds(fileData.path)
            fileData.width = bounds[0]
            fileData.height = bounds[1]
            fileData.dir = "img"
            fileData.format = IMG
        }
        fileData.source = CAMERA

        fileData.size = FileUtil.getFileLength(fileData.path)
        fileData.name = FileUtil.getFileName(fileData.path)
        files?.mSelectedFiles?.add(fileData)
        LogUtil.e("fileData:", fileData)
        publish()
    }

    private fun publish() {
        fileFinish()
        if (Files?.mOnFileListener != null) {
            Files?.mOnFileListener?.onFile(files?.mSelectedFiles!![0])
        } else {
            Files?.mOnFilesListener?.onFiles(files?.mSelectedFiles!!)
        }
    }

    private fun fileFinish() {
        if (files!!.fromFragment) {
            pop()
        } else {
            finish()
        }
    }

    private fun goCamera(): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(mActivity.packageManager) == null) return null
        currentPhotoPath = createFile(false)
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            FileProvider.getUriForFile(
                mActivity, mActivity?.packageName + ".provider", currentPhotoPath!!.absoluteFile
            )
        } else {
            Uri.fromFile(currentPhotoPath)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, true)
        intent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true)
        this.startActivityForResult(intent, AppUtil.REQUEST_CODE_CAMERA)
        return intent
    }

    private fun goVideo(): Intent {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        currentPhotoPath = createFile(true)
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            FileProvider.getUriForFile(
                mActivity, mActivity?.packageName + ".provider", currentPhotoPath!!.absoluteFile
            )
        } else {
            Uri.fromFile(currentPhotoPath)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, true)
        intent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.putExtra(
            MediaStore.EXTRA_DURATION_LIMIT,
            SPUtil.getInt(RECODER_VIDEO_DURATION, 15)
        )//录制时间默认15秒
        this.startActivityForResult(intent, AppUtil.REQUEST_CODE_VIDEO)
        return intent
    }

    private fun notifyMediaStoreDatabase(isVideo: Boolean): File? {
        if (!isVideo) {
            MediaStore.Images.Media.insertImage(
                mActivity.contentResolver, currentPhotoPath!!.absolutePath, currentFileName,
                currentFileName
            )
        }
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = uri
        mActivity.sendBroadcast(intent)
        return currentPhotoPath
    }

    @Throws(IOException::class)
    private fun createFile(isVideo: Boolean): File {
        currentFileName = AppUtil.getAppName() + System.currentTimeMillis() + ".jpg"
        if (isVideo) {
            currentFileName = AppUtil.getAppName() + System.currentTimeMillis() + ".3gp"
        }
        return FileUtil.getFile(currentFileName)
    }

    override fun onItem(
        position: Int,
        item: FileData
    ) {
        if (files?.mMax == 1) {
            adapter?.datas()
                ?.forEach {
                    if (item.path == it.path) {
                        files?.mSelectedFiles?.clear()
                        files?.mSelectedFiles?.add(item)
                        it.selected = true
                    } else {
                        it.selected = false
                    }
                }
        } else {
            if (files?.mSelectedFiles?.size == 0) {
                item.selected = true
                files?.mSelectedFiles?.add(item)
            } else if (files?.mSelectedFiles?.size!! < files!!.mMax) {
                var isAdd = true
                var fileData: FileData? = null
                files?.mSelectedFiles?.forEach {
                    if (it.path == item.path) {
                        isAdd = false
                        fileData = it
                    }
                }
                if (isAdd) {
                    item.selected = true
                    files?.mSelectedFiles?.add(item)
                } else {
                    if (item.selected) {
                        item.selected = false
                        val remove = files?.mSelectedFiles?.remove(fileData)
                    }
                }
            } else {
                var isRemove = false
                files?.mSelectedFiles?.forEach {
                    if (item.path == it.path) {
                        item.selected = false
                        isRemove = true
                    }
                }
                if (isRemove) {
                    files?.mSelectedFiles?.remove(item)
                }
            }
        }
        if (files?.mMax!! > 1) {
            setTitle("${files?.mTitle}(${files?.mMax}/${files?.mSelectedFiles!!.size})")
            var size = 0
            adapter!!.datas()
                ?.forEach {
                    if (it.selected) {
                        size++
                    }
                }
            val selectedDir = size > 0
            if (files?.mSelectedFiles != null && files?.mSelectedFiles!!.size > 0) {
                if (files?.mSelectedFiles!![0]?.selectedDirList == null) {
                    val list = ArrayList<FileData>()
                    adapterDir?.datas()
                        ?.forEach {
                            list.add(FileData())
                        }
                    files?.mSelectedFiles!![0]?.selectedDirList = list
                }
                files?.mSelectedFiles!![0]?.selectedDirList?.get(index)?.selectedDirSize = size
                files?.mSelectedFiles!![0]?.selectedDirList?.get(index)?.selectedDir = selectedDir
                files?.mSelectedFiles!![0]?.selectedDirList?.get(index)?.path =
                    adapterDir!!.datas()[index][0].path
            }
            adapterDir?.notifyDataSetChanged()
        }
        adapter?.notifyDataSetChanged()
        validate()
    }

    private fun validate() {
        val disable = files?.mSelectedFiles == null || files!!.mSelectedFiles!!.size == 0
        setRight(files?.mRightTxt).setRightEnable(!disable)
            .setRightListener {
                publish()
            }
    }

    override fun onResume() {
        super.onResume()
        showFloatMenu(false)
        VideoViewManager.instance()
            .pause()
        listener?.onStatus(true)
    }

    override fun onPause() {
        super.onPause()
        SPUtil.putInt(File_SHOW_TOP, 0)
//        showFloatMenu(true)
//    if (Resource.TAB_INDEX == 0) {
//      VideoViewManager.instance()
//        .resume()
//    }
        listener?.onStatus(false)
    }
}

public interface OnFileFinishListener {
    fun onStatus(isResume: Boolean)
}

const val File_SHOW_TOP = "fileShowTop"
