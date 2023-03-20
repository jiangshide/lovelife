package com.android.sanskrit

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.api.BasicCallback
import com.amap.api.services.poisearch.PoiResult
import com.android.base.BaseActivity
import com.android.base.BaseFragment
import com.android.event.ZdEvent
import com.android.files.Files
import com.android.files.Files.*
import com.android.files.fragment.OnFileFinishListener
import com.android.img.Img
import com.android.jpush.JPush
import com.android.location.ZdLocation
import com.android.location.ZdLocation.Errors
import com.android.location.listener.IPoiSearchListener
import com.android.player.dplay.player.VideoViewManager
import com.android.resource.Resource
import com.android.resource.WOMEN
import com.android.resource.audio.AUDIO_STATE
import com.android.resource.audio.AudioPlay
import com.android.resource.task.PublishTask
import com.android.resource.task.PublishTask.PublishTaskListener
import com.android.resource.vm.publish.PublishVM
import com.android.resource.vm.publish.data.Publish
import com.android.sanskrit.R.drawable
import com.android.sanskrit.R.mipmap
import com.android.sanskrit.channel.ChannelFragment
import com.android.sanskrit.home.HOME_REFRESH
import com.android.sanskrit.home.HomeManagerFragment
import com.android.sanskrit.message.MessageFragment
import com.android.sanskrit.mine.MineManagerFragment
import com.android.sanskrit.publish.DocFragment
import com.android.sanskrit.publish.EmptyFragment
import com.android.sanskrit.publish.PublishFragment
import com.android.sanskrit.publish.RecordFragment
import com.android.sanskrit.user.UserFragment
import com.android.sanskrit.user.fragment.AudioFragment
import com.android.sanskrit.wxapi.WXApiManager
import com.android.utils.AppUtil
import com.android.utils.EncryptUtils
import com.android.utils.LogUtil
import com.android.utils.SPUtil
import com.android.utils.data.*
import com.android.widget.ZdTab
import com.android.widget.ZdTab.OnTabClickListener
import com.android.widget.ZdTab.OnTabLongClickListener
import com.android.widget.ZdToast
import com.android.widget.anim.Anim
import com.android.widget.menu.ButtonData
import com.android.widget.menu.ButtonEventListener

open class MainActivity : BaseActivity(), OnTabClickListener, OnTabLongClickListener,
    PublishTaskListener,
    ButtonEventListener, OnCompressListener, OnFileFinishListener {

    private var animDrawable: AnimationDrawable? = null
    private var publishVM: PublishVM? = null

    override fun onPause() {
        super.onPause()
        VideoViewManager.instance()
            .pause()
    }

    override fun onResume() {
        super.onResume()
        VideoViewManager.instance()
            .resume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Resource.first = false
        if (null == mZdTab) mZdTab = ZdTab(this, supportFragmentManager)
        setContentView(
            mZdTab.init(supportFragmentManager)
                .setFragments(
                    HomeManagerFragment(), ChannelFragment(),
                    EmptyFragment(), MessageFragment(),
                    MineManagerFragment()
                )
                .setIcons(
                    drawable.ic_app_home, drawable.ic_app_search, drawable.ic_app_publish,
                    drawable.ic_app_message,
                    drawable.ic_app_me
                )
                .setTags(HOME, CHANNEL, PUBLISH, MESSAGE, MINE)
                .setState(ZdTab.TAB, ZdTab.TAB, ZdTab.NO_TAB, ZdTab.TAB, ZdTab.TAB)
                .setTabOnClickListener(this)
                .setTabLongClickListener(this)
                .create()
                .setTab(SPUtil.getInt("main", 0))
        )
        initTabMenus()
        ZdEvent.get()
            .with(PUBLISH_UPLOAD)
            .observes(this, Observer {
                PublishTask().setCompressListener(this)
                    .setListener(this)
                    .publish()
            })
        publishVM = ViewModelProviders.of(this)
            .get(PublishVM::class.java)
        publishVM!!.publishBlog.observe(this, Observer {
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
            WXApiManager.shareTxt(it.data.des)
            Resource.clearPublish()
            Img.loadImageCircle(
                Resource.icon, mZdTab.floatIcon, R.mipmap.default_user
            )
            ZdEvent.get()
                .with(HOME_REFRESH)
                .post(HOME_REFRESH)
        })

        ZdEvent.get()
            .with(AUDIO_STATE, AudioPlay.PlayState::class.java)
            .observes(this, Observer {
                when (it) {
                    AudioPlay.PlayState.STATE_PLAYING -> {
                        refreshAudioBtnStatus(true)
                    }
                    AudioPlay.PlayState.STATE_RESUME -> {
                        refreshAudioBtnStatus(true)
                    }
                    AudioPlay.PlayState.STATE_PAUSE -> {
                        refreshAudioBtnStatus(false)
                    }
                    AudioPlay.PlayState.STATE_IDLE -> {
                        refreshAudioBtnStatus(false)
                    }
                }
            })

        PublishTask().setCompressListener(this)
            .setListener(this)
            .publish()

        var userName = Resource.user?.unionId
        if (TextUtils.isEmpty(userName)) return
        val password = EncryptUtils.encryptMd5(userName)
        LogUtil.e("----------jpush~userName:", userName, " | password:", password)
        JMessageClient.login(userName, password, object : BasicCallback() {
            override fun gotResult(
                p0: Int,
                p1: String?
            ) {
                LogUtil.e("--------jpush~p0:", p0, " | p1:", p1)
            }
        })
        JMessageClient.registerEventReceiver(MainActivity@ this)
        val user = Resource.user
        mZdTab?.isLogin = user != null
        JPush().setAlias(AppUtil.getApplicationContext(), user?.id!!.toInt(), "${user?.id}")
        val set = HashSet<String>()
        set.add("sanskrit")
        if (user?.sex == WOMEN) {
            set.add("women")
        } else {
            set.add("men")
        }
        JPush().setTag(AppUtil.getApplicationContext(), Resource.uid!!.toInt(), set)
        ZdEvent.get()
            .with(DOT)
            .observes(this, Observer {
                dotStatus()
            })
        dotStatus()
    }

    private fun dotStatus() {
        var count = JMessageClient.getAllUnReadMsgCount()
        if (count <= 0) {
            count = 0
        }
        SPUtil.putInt(MESSAGE, count + SPUtil.getInt(MESSAGE, 0))
        val home = SPUtil.getInt(HOME, 0)
        val channel = SPUtil.getInt(CHANNEL, 0)
//        val publish = SPUtil.getInt(PUBLISH, 0)
        val message = SPUtil.getInt(MESSAGE, 0)
        val mine = SPUtil.getInt(MINE, 0)
        mZdTab?.setDot(0, home, home > 0)
        mZdTab?.setDot(1, channel, channel > 0)
//        mZdTab?.setDot(2, publish, publish > 0)
        mZdTab?.setDot(3, message, message > 0)
        mZdTab?.setDot(4, mine, mine > 0)
    }

    fun onEventMainThread(event: MessageEvent) {
        dotStatus()
    }

    override fun onTab(position: Int) {
        mZdTab.floatL.visibility = View.VISIBLE
        if (position != 0) {
            VideoViewManager.instance()
                .pause()
        } else {
            VideoViewManager.instance()
                .resume()
        }
        Resource.TAB_INDEX = position
        mZdTab?.setDot(position, 0, false)
    }

    override fun onNoTab(position: Int) {
        VideoViewManager.instance()
            .pause()
        mZdTab.floatL.visibility = View.GONE
        openImg()
    }

    override fun DoubleOnClick(
        tabId: Int,
        tag: String?
    ) {
        mZdTab.setTabIcon(mipmap.refresh)
        startLocation()
    }

    override fun unUser() {
        goFragment(UserFragment::class.java)
    }

    override fun onTabLongClick(
        view: View?,
        position: Int
    ) {
        if (position == 2) {
            openAudio()
        }
    }

    private fun startLocation() {
        ZdLocation.getInstance()
            .getCity(this, object : IPoiSearchListener {
                override fun onError(errors: Errors?) {
                    LogUtil.e("errors:", errors)
                    ZdLocation.showLocationError(mContext, errors)
                }

                override fun onPoiResult(poiResult: PoiResult?) {
                    if (poiResult == null || poiResult.pois.isNullOrEmpty()) return
                    LogUtil.e("poiResult:", poiResult)
//            poiItem = poiResult.pois[0]
                }
            })
    }

    override fun onBackPressed() {
        if (VideoViewManager.instance()
                ?.onBackPressed()!!
        ) return
        val count = mZdTab.count
        if (count >= 1) {
            super.onBackPressed()
            return
        }
        supportFragmentManager.fragments.forEach {
            if (it is BaseFragment) {
                val isBack = it.onBackPressed()
                if (isBack) {
                    return
                }
            }
        }
        moveTaskToBack(true)
    }

    private fun initTabMenus() {
        mZdTab.floatL.visibility = View.VISIBLE
        val buttonDatas: MutableList<ButtonData> = ArrayList()
        val drawable = intArrayOf(
            R.mipmap.publish, R.mipmap.unimg, R.mipmap.audio,
            R.mipmap.unvideo, R.mipmap.txt, R.mipmap.unaudio
        )
        for (i in 0..5) {
            val buttonData = ButtonData.buildIconButton(this, drawable[i], 0f)
            buttonData.setBackgroundColorId(this, R.color.alpha)
            buttonDatas.add(buttonData)
        }
        mZdTab.floatMenus.buttonDatas = buttonDatas
        mZdTab.floatMenus.setButtonEventListener(this)
        refreshAudioBtnStatus()

        Anim.anim(mZdTab.floatIcon)
        mZdTab.floatPlay.setOnClickListener {
            showAudio()
        }
    }

    override fun start(fileData: FileData) {
        setFloatCover(fileData.getCoverUrl())
    }

    private fun setFloatCover(url: String? = Resource.icon) {
        Img.loadImageCircle(
            Img.thumbAliOssUrl(url, 100, 100), mZdTab.floatIcon, R.mipmap.publish
        )
        Anim.anim(mZdTab.floatIcon, R.anim.circulate)
    }

    override fun progress(
        totalProgress: Int,
        progress: Int
    ) {
        LogUtil.e("progress:", progress)
        mZdTab.setProgress(totalProgress)
        mZdTab.floatBtn.text = "$progress"
//    mZdTab.homePublishImg.setProgress(progress)
    }

    override fun onFail(throwable: Throwable?) {
        LogUtil.e("throwable:", throwable)
        mZdTab.floatBtn.text = "重试"
    }

    override fun onSuccess(publish: Publish?) {
        LogUtil.e("publish:", publish)
        publishVM?.publish(publish!!)
        mZdTab.setProgress(0)
        mZdTab.floatBtn.text = ""
    }

    override fun onButtonClicked(index: Int) {
        mZdTab.floatL.visibility = View.GONE
        if (index > 1 && Resource.user == null) {
            goFragment(UserFragment::class.java)
            return
        }
        when (index) {
            1 -> {
                openImg()
            }
            2 -> {
                openAudio()
            }
            3 -> {
                openVideo()
            }
            4 -> {
                push(DocFragment())
            }
            5 -> {
                showAudio()
            }
        }
    }

    private fun showAudio() {
        push(AudioFragment())
    }

    private fun refreshAudioBtnStatus(isAnim: Boolean = false) {
        if (isAnim) {
            mZdTab.floatPlay.visibility = View.VISIBLE
            mZdTab.floatPlay.setImageResource(R.drawable.audio_anim)
            animDrawable = mZdTab.floatPlay.drawable as AnimationDrawable?
            DrawableCompat.setTint(animDrawable!!, ContextCompat.getColor(this, R.color.blueLight))
            animDrawable?.start()
            val data = AudioPlay.getInstance().data
            if (data != null) {
                setFloatCover(data.getCoverUrl())
            }
        } else {
            mZdTab.floatPlay.visibility = View.GONE
            animDrawable?.stop()
        }
    }

    override fun onExpand() {
        mZdTab.floatPlay.visibility = View.GONE
        mZdTab.floatIcon.visibility = View.GONE
        mZdTab.floatBtn.visibility = View.GONE
        mZdTab.floatIcon.visibility = View.VISIBLE
        ZdEvent.get().with(SHOW_MENUS).post(true)
    }

    override fun onCollapse() {
        mZdTab.floatPlay.visibility = View.VISIBLE
        mZdTab.floatIcon.visibility = View.VISIBLE
        mZdTab.floatBtn.visibility = View.VISIBLE
        mZdTab.floatIcon.visibility = View.GONE
        ZdEvent.get().with(SHOW_MENUS).post(false)
    }

    private fun openImg() {
        Files().setType(IMG)
            .setMax(9)
            .setFloat(true)
            .setFileFinish(this)
            .setMultipleListener(object : OnFilesListener {
                override fun onFiles(data: MutableList<FileData>) {
                    push(PublishFragment(data))
                }
            })
            .open(EmptyFragment())
    }

    private fun openAudio() {
        Files().setType(AUDIO)
            .setMax(1)
            .setFloat(true)
            .setFileFinish(this)
            .setFloatListener(object : OnFloatListener {
                override fun onFloat(type: Int) {
                    push(RecordFragment())
                }
            })
            .setSingleListener(object : OnFileListener {
                override fun onFile(data: FileData) {
                    push(RecordFragment(data))
                }
            })
            .open(EmptyFragment())
    }

    private fun openVideo() {
        Files().setType(VIDEO)
            .setMax(1)
            .setFloat(true)
            .setFileFinish(this)
            .setMultipleListener(object : OnFilesListener {
                override fun onFiles(data: MutableList<FileData>) {
                    push(PublishFragment(data))
                }
            })
            .open(EmptyFragment())
    }

    override fun onResult(dest: String?) {
        mZdTab.setProgress(0)
        mZdTab.floatBtn.text = ""
    }

    override fun onProgress(percent: Float) {
        val progress = percent.toInt()
        mZdTab.setProgress(progress)
        mZdTab.floatBtn.text = "${progress}"
    }

    override fun onStatus(isResume: Boolean) {
        if (isResume) {
            VideoViewManager.instance()
                .pause()
        } else {
            if (Resource.TAB_INDEX == 0) {
                VideoViewManager.instance()
                    .resume()
            }
        }
    }
}

const val FLOW_MENUS = "flowMenus"

//const val PUBLISH = "publish"
const val PUBLISH_UPLOAD = "publishUpload"

const val HOME = "home"
const val CHANNEL = "channel"
const val PUBLISH = "publish"
const val MESSAGE = "message"
const val MINE = "mine"
const val DOT = "dot"

const val SHOW_MENUS = "showMenus"
