package com.android.resource.audio

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.os.Binder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.IBinder
import android.text.TextUtils
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import cn.jpush.android.service.PushReceiver
import com.android.event.ZdEvent.Companion.get
import com.android.http.exception.HttpException
import com.android.img.Img
import com.android.img.listener.IBitmapListener
import com.android.resource.FOLLOW
import com.android.resource.R
import com.android.resource.R.*
import com.android.resource.Resource
import com.android.resource.audio.AudioPlay.PlayState
import com.android.resource.audio.AudioPlay.PlayStateListener
import com.android.resource.vm.blog.BlogVM
import com.android.resource.vm.blog.OnPraiseListener
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.user.OnFollowListener
import com.android.resource.vm.user.UserVM
import com.android.utils.*
import com.android.utils.CountDown.OnCountDownListener
import com.android.widget.ZdNotification
import com.android.widget.ZdToast
import java.util.*

/**
 * created by jiangshide on 2020/4/26.
 * email:18311271399@163.com
 */
class AudioService : Service(), PlayStateListener, OnDataCaptureListener, OnCountDownListener {
    private val NOTIFICATION_ID = 0x1000000
    private var remoteViews: RemoteViews? = null
    private var remoteViewsBig: RemoteViews? = null
    private var remoteViewsHeadUp: RemoteViews? = null
    private val mBinder: IBinder = AudioServiceBinder()
    private var blogVM: BlogVM? = null
    private var userVM: UserVM? = null

    private var notificationManager: NotificationManager? = null

    private var countDown = CountDown()

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        blogVM = BlogVM()
        userVM = UserVM()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (intent != null) {
            val action = intent.action
            val command = intent.getStringExtra(CMD_NAME)
            val position = intent.getIntExtra("position", 0)
            if (ACTION_CMD == action && !TextUtils.isEmpty(command)) {
                when (command) {
                    CMD_INIT -> {
                        countDown.setInterval(50)
                            .setListener(this)
                        notification()
                    }
                    CMD_PLAY -> {
                        AudioPlay.getInstance()
                            .setPlayStateListener(this)
                            .setDataCaptureListener(this)
                        AudioPlay.getInstance()
                            .play(position)
                    }
                    CMD_MODE -> {
                        AudioPlay.getInstance()
                            .setMode()
                        notification()
                    }
                    CMD_PREV -> AudioPlay.getInstance()
                        .prev()
                    CMD_START -> {
                        AudioPlay.getInstance()
                            .start()
                        notification()
                    }
                    CMD_NEXT -> AudioPlay.getInstance()
                        .next()
                    CMD_STOP -> {
                        AudioPlay.getInstance()
                            .stop()
//            stopForeground(true)
                        notification()
                    }
                    CMD_CLOSE -> {
                        AudioPlay.getInstance().release()
                        notification()
                        ZdNotification.getInstance()
                            .clear(NOTIFICATION_ID)
                    }
                    CMD_PRAISE -> {
                        val blog = AudioPlay.getInstance().data
                        if (blog != null) {
                            blog?.praises = FOLLOW - blog.praises
                            if (blog?.praises == FOLLOW) {
                                blog.praiseNum += 1
                            } else {
                                blog.praiseNum -= 1
                            }
                            AudioPlay.getInstance().data.praises = blog?.praises!!
                            notification()
                            blogVM?.praiseAdd(blog, object : OnPraiseListener {
                                override fun onPraise(blog: Blog) {
                                    AudioPlay.getInstance().mData?.forEachIndexed { index, it ->
                                        if (blog.id == it.id) {
                                            AudioPlay.getInstance().mData[index].praises =
                                                blog.praises
                                            notification()
                                            return@forEachIndexed
                                        }
                                    }
                                }
                            })
                        }
                    }
                    CMD_CHANNEL -> get()
                        .with("channel")
                        .post("channel")
                    CMD_FOLLOW -> {
                        val blog = AudioPlay.getInstance().data
                        if (blog != null) {
                            if (blog.uid == Resource.uid) {
                                ZdToast.txt("不能关注自己!")
                            } else {
                                AudioPlay.getInstance().data.follows = FOLLOW
                                notification()
                                userVM?.followAdd(blog.uid, FOLLOW, object : OnFollowListener {
                                    override fun follow(
                                        status: Int,
                                        e: HttpException?
                                    ) {
                                        if (e != null) {
                                            ZdToast.txt(e.message)
                                            return
                                        }
                                        AudioPlay.getInstance().mData?.forEachIndexed { index, it ->
                                            if (it.id == blog.id) {
                                                AudioPlay.getInstance().mData[index].follows =
                                                    status
                                                notification()
                                                return@forEachIndexed
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
        }
        return START_STICKY
    }

    private fun notification() {
        notificationManager = ZdNotification.getInstance()
            .create()
            .setId(NOTIFICATION_ID)
            .setService(this)
            .setTitle("音乐世界")
            .setContent("这个世界很美好!")
            .setIsClear(true)
            .setIsRing(true)
            .setLights(true)
            .setIcon(mipmap.unaudio)
            .setVibrate(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
//        .setView(remoteViews)
            .setCustomContentView(customContentView())
            .setCustomBigContentView(setCustomBigContentView())
            .setCustomHeadsUpContentView(customContentView())
            .setClass(PushReceiver::class.java)
            .build()
    }

    private fun setCustomHeadsUpContentView(): RemoteViews {
        if (remoteViewsHeadUp == null) {
            remoteViewsHeadUp =
                RemoteViews(AppUtil.getPackageName(), layout.audio_notification_headup)
        }
        return remoteViewsHeadUp!!
    }

    private fun setCustomBigContentView(): RemoteViews {
        if (remoteViewsBig == null) {
            remoteViewsBig = RemoteViews(AppUtil.getPackageName(), layout.audio_notification_big)
//            remoteViewsBig!!.setOnClickPendingIntent(
//                id.audioNotificationFollow,
//                getPendingIntent(CMD_FOLLOW)
//            )
            initView(remoteViewsBig)
        }
        updateRemoteViews(remoteViewsBig, 1)
        return remoteViewsBig!!
    }

    private fun customContentView(
    ): RemoteViews {
        if (remoteViews == null) {
            remoteViews = RemoteViews(
                AppUtil.getPackageName(), layout.audio_notification
            )
            initView(remoteViews)
        }
        updateRemoteViews(
            remoteViews, 0
        )
        return remoteViews!!
    }

    private fun initView(remoteView: RemoteViews?) {
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationFollow,
            getPendingIntent(CMD_FOLLOW)
        )
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationChannel,
            getPendingIntent(CMD_CHANNEL)
        )
        remoteView!!.setOnClickPendingIntent(id.audioNotificationMode, getPendingIntent(CMD_MODE))
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationPrev,
            getPendingIntent(CMD_PREV)
        )
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationNext,
            getPendingIntent(CMD_NEXT)
        )
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationPlay,
            getPendingIntent(CMD_START)
        )
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationFormat,
            getPendingIntent(CMD_FORMAT)
        )
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationDuration, getPendingIntent(
                CMD_DURATION
            )
        )
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationProgress, getPendingIntent(
                CMD_PROGRESS
            )
        )
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationTime, getPendingIntent(
                CMD_TIME
            )
        )
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationClose,
            getPendingIntent(CMD_CLOSE)
        )
        remoteView!!.setOnClickPendingIntent(
            id.audioNotificationPraise,
            getPendingIntent(CMD_PRAISE)
        )
    }

    private fun updateRemoteViews(
        remoteView: RemoteViews?,
        type: Int
    ) {
        val audioPlay = AudioPlay.getInstance()
        val data = audioPlay.data
        LogUtil.e("data:", data)
        if (data == null) return
        var cover = Resource.icon
        if (data != null) {
            var name = data.name
            if (TextUtils.isEmpty(name)) {
                name = AppUtil.getAppName()
            }
            if (!TextUtils.isEmpty(data.cover)) {
                cover = data.cover!!
            }
            val time = audioPlay.currentPosition() / 1000
            remoteView!!.setTextViewText(
                id.audioNotificationDuration, DateUtil.formatSeconds(data.duration / 1000)
            )
            remoteView!!.setTextViewText(
                id.audioNotificationTime, DateUtil.formatSeconds(time)
            )
            remoteView!!.setProgressBar(
                id.audioNotificationProgress, data.duration.toInt(), time, false
            )
            remoteView?.setTextViewText(id.audioNotificationName, name)
            remoteView?.setTextViewText(id.audioNotificationChannel, "# $name")
            var des = data.des
            if (TextUtils.isEmpty(des)) {
                des = data.name
            }
            remoteView?.setTextViewText(id.audioNotificationDes, des)
        } else {
            remoteView!!.setTextViewText(
                id.audioNotificationName, AppUtil.getAppName()
            )
            remoteView.setTextViewText(
                id.audioNotificationChannel, AppUtil.getAppName()
            )
        }
        remoteView.setImageViewResource(id.audioNotificationMode, MODE[audioPlay.mMode])
        remoteView.setImageViewResource(
            id.audioNotificationPlay,
            if (audioPlay.isPlaying) mipmap.play_pause else mipmap.play
        )
        remoteView.setImageViewResource(
            id.audioNotificationPraise,
            if (data.praises == FOLLOW) mipmap.liked else mipmap.unlike
        )
        remoteView.setImageViewResource(
            id.audioNotificationFollow, if (data.follows == FOLLOW) mipmap.unmine else mipmap.follow
        )

//    remoteView.setImageViewUri(id.audioNotificationCover, Uri.parse("http://zd112.oss-cn-beijing.aliyuncs.com/img/IMG_20200424_183201.jpg"))
        var width = ScreenUtil.getRtScreenWidth(AppUtil.getApplicationContext())
        var height = 400
        if (type == 0) {
            width = 200
            height = 200
        }
        if (data != null && !TextUtils.isEmpty(data.getCoverUrl())) {
            Img.loadImage(Img.thumbAliOssUrl(data.getCoverUrl(), width, height), 0,
                object : IBitmapListener {
                    override fun onSuccess(bitmap: Bitmap): Boolean {
                        if (remoteView == null) return false
                        if (bitmap == null) {
                            remoteView.setImageViewResource(
                                id.audioNotificationCover,
                                mipmap.ic_launcher
                            )
                        } else {
                            remoteView.setImageViewBitmap(
                                id.audioNotificationCover, bitmap
                            )
                        }
                        return false
                    }

                    override fun onFailure(): Boolean {
                        if (remoteView == null) return false
                        remoteView.setImageViewResource(
                            id.audioNotificationCover,
                            mipmap.ic_launcher
                        )
                        return false
                    }
                })
        }
    }

    private fun getPendingIntent(cmd: String): PendingIntent {
        val intent = Intent(ACTION_CMD)
        intent.putExtra(CMD_NAME, cmd)
        // requestCode使用UUID处理携带的数据失效问题
        return PendingIntent.getService(
            this, UUID.randomUUID()
                .hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    inner class AudioServiceBinder : Binder() {
        val service: AudioService
            get() = this@AudioService
    }

    companion object {

        const val CMD_NAME = "CMD_NAME"
        const val ACTION_CMD = "com.android.sanskrit.AUDIO_ACTION_CMD"

        const val CMD_INIT = "cmdInit"

        const val CMD_START = "cmdStart"

        /**
         * 播放模式
         */
        const val CMD_MODE = "cmdMode"

        //  播放/暂停
        const val CMD_PLAY = "cmdPlay"

        //  播放上一首
        const val CMD_PREV = "cmdPrev"

        //  播放下一首
        const val CMD_NEXT = "cmdNext"

        //  停止服务
        const val CMD_STOP = "cmdStop"

        //  关闭
        const val CMD_CLOSE = "cmdClose" //关闭
        const val CMD_FORMAT = "cmdFormat" //内容格式
        const val CMD_DURATION = "cmdDuration"//时间长度
        const val CMD_PROGRESS = "cmdProgress"//进度
        const val CMD_TIME = "cmdTime"//进度时间
        const val CMD_CHANNEL = "cmdChannel" //频道
        const val CMD_PRAISE = "cmdPraise" //点赞
        const val CMD_FOLLOW = "cmdFollow"//关注

        val MODE = listOf(
            R.mipmap.play_loop,
            R.mipmap.play_loop_single, R.mipmap.play_sequence,
            R.mipmap.play_random, R.mipmap.play_single
        )

        //  开启服务
        fun startAudioService() {
            val context: Context = AppUtil.getApplicationContext()
            val intent = Intent(context, AudioService::class.java)
            context.startService(intent)
        }

        //  停止服务
        fun stopAudioService() {
            val context: Context = AppUtil.getApplicationContext()
            val intent = Intent(context, AudioService::class.java)
            context.stopService(intent)
        }

        //  绑定服务
        fun bindAudioService(
            context: Context,
            conn: ServiceConnection?,
            flags: Int
        ) {
            val intent = Intent()
            intent.setClass(context, AudioService::class.java)
            context.bindService(intent, conn!!, flags)
        }

        //  通过该方法发出命令
        fun startAudioCommand(
            cmd: String,
            position: Int = 0,
            data: ArrayList<Blog>? = null
        ) {
            val context: Context = AppUtil.getApplicationContext()
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_CMD
            if (data != null) {
                AudioPlay.getInstance()
                    .setData(data)
//        intent.putExtra("data", data)
            }
            intent.putExtra("position", position)
            intent.putExtra(CMD_NAME, cmd)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onFinish() {
        LogUtil.e("finish")
    }

    override fun onPause(duration: Long) {
    }

    override fun onTick(duration: Long) {
//    notification(true)
        get()
            .with(AUDIO_PROGRESS, Int::class.java)
            .post(
                AudioPlay.getInstance()
                    .currentPosition()
            )
    }

    override fun onStateChange(state: PlayState) {
        get().with(AUDIO_STATE)
            .post(state)
        when (state) {
            PlayState.STATE_INITIALIZING -> {
                countDown.stop()
            }
            PlayState.STATE_PLAYING -> {
                countDown.start()
                notification()
            }
            PlayState.STATE_RESUME -> {
                countDown.start()
                notification()
            }
            PlayState.STATE_PAUSE -> {
                countDown.pause()
                notification()
            }
            PlayState.STATE_IDLE -> {
                countDown.stop()

            }
        }
    }

    override fun onFftDataCapture(
        visualizer: Visualizer?,
        fft: ByteArray,
        samplingRate: Int
    ) {
        get()
            .with(AUDIO_CAPTURE)
            .post(fft)
    }

    override fun onWaveFormDataCapture(
        visualizer: Visualizer?,
        waveform: ByteArray,
        samplingRate: Int
    ) {
        get()
            .with(AUDIO_CAPTURE)
            .post(waveform)
    }
}

const val AUDIO_STATE = "audioState"
const val AUDIO_CAPTURE = "audioCapture"
const val AUDIO_PROGRESS = "audioProgress"