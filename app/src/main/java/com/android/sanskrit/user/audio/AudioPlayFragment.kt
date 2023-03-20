package com.android.sanskrit.user.audio

import android.animation.ObjectAnimator
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.base.BaseActivity
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.player.audio.AudioView
import com.android.resource.FOLLOW
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.UNFOLLOW
import com.android.resource.audio.*
import com.android.resource.view.like.LikeButton
import com.android.resource.view.like.OnLikeListener
import com.android.resource.vm.blog.OnPraiseListener
import com.android.resource.vm.blog.data.Blog
import com.android.sanskrit.R
import com.android.sanskrit.blog.fragment.CommentDialogFragment
import com.android.sanskrit.blog.fragment.OnTotalListener
import com.android.sanskrit.home.HOME_REFRESH
import com.android.sanskrit.mine.fragment.set.RechargeFragment
import com.android.sanskrit.user.UserFragment
import com.android.sanskrit.user.fragment.AUDIO_STATE_CLOSE
import com.android.sanskrit.user.fragment.AudioBlurListener
import com.android.sanskrit.user.fragment.AudioListener
import com.android.tablayout.indicators.LinePagerIndicator
import com.android.utils.DateUtil
import com.android.widget.ZdToast
import com.android.widget.anim.Anim
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlinx.android.synthetic.main.mine_fragment.appBarLayout
import kotlinx.android.synthetic.main.user_audio_play_fragment.playViewPager
import kotlinx.android.synthetic.main.user_audio_play_fragment.tabsPlay
import kotlinx.android.synthetic.main.user_audio_play_fragment_head.*

/**
 * created by jiangshide on 2020/4/21.
 * email:18311271399@163.com
 */
class AudioPlayFragment(
    private val myActivity: BaseActivity,
    private val audioBlurListener: AudioBlurListener,
    private val audioListener: AudioListener
) : MyFragment(),
    OnOffsetChangedListener {

    private var anim: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isBackground = false
        return setView(R.layout.user_audio_play_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
//    setLeftListener {
//      ZdStatusBar.changStatusIconCollor(mActivity, true)
//    }
        ZdEvent.get()
            .with(AUDIO_REFRESH)
            .observes(this, Observer {

            })

        playViewPager.adapter = playViewPager.create(childFragmentManager)
            .setTitles(
                "播放列表",
                "上次听过",
                "本地列表"
            )
            .setFragment(
                PlayListFragment(),
                HistoryAudioFragment(),
                LocalMusicFragment()
            )
            .initTabs(activity!!, tabsPlay, playViewPager, true)
            .setMode(LinePagerIndicator.MODE_MATCH_EDGE)
            .setTxtSelectedColor(R.color.blue)
            .setTxtSelecteColor(R.color.fontLight)
            .setTxtSelecteSize(10)
            .setTxtSelectedSize(13)
            .setLinePagerIndicator(getColor(R.color.whiteLight))
        appBarLayout.addOnOffsetChangedListener(this)
        audioPlay.setOnClickListener {
            AudioService.startAudioCommand(AudioService.CMD_PLAY)
        }
        audioPlayCover.setOnClickListener {
            AudioService.startAudioCommand(AudioService.CMD_START)
        }
        audioPlayMenusMode.setOnClickListener {
            audioPlayMenusMode.setImageResource(
                AudioService.MODE[AudioPlay.getInstance()
                    .setMode()]
            )
        }
        audioPlayMenusPre.setOnClickListener {
            AudioService.startAudioCommand(AudioService.CMD_PREV)
        }
        audioPlayMenusPlay.setOnClickListener {
            AudioService.startAudioCommand(AudioService.CMD_START)
        }
        audioPlayMenusNext.setOnClickListener {
            AudioService.startAudioCommand(AudioService.CMD_NEXT)
        }
        audioPlayMenusDown.setOnClickListener {
            audioListener.audio(AUDIO_STATE_CLOSE)
            myActivity.push(RechargeFragment().setTitle("充值中心"))
        }
        audioPlayLikeBtn.isEnabled = Resource.user != null
        audioPlayLikeBtn.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
                val data = AudioPlay.getInstance().data
                if (data != null) {
                    data.praises = FOLLOW
                    data.praiseNum += 1
                    setFollow(data)
                    blogVM?.praiseAdd(data, object : OnPraiseListener {
                        override fun onPraise(blog: Blog) {
                            setFollow(blog)
                        }
                    })
                }
            }

            override fun unLiked(likeButton: LikeButton?) {
                val data = AudioPlay.getInstance().data
                if (data != null) {
                    data.praises = UNFOLLOW
                    data.praiseNum -= 1
                    setFollow(data)
                    blogVM?.praiseAdd(data, object : OnPraiseListener {
                        override fun onPraise(blog: Blog) {
                            setFollow(blog)
                        }
                    })
                }
            }
        })
        anim = Anim.anim(audioPlayCover)
        audioPlayFollow.setOnClickListener {
            if(Resource.user == null){
                goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            val blog = AudioPlay.getInstance().data ?: return@setOnClickListener
            blog.follows = FOLLOW - blog.follows
            blogVM?.followAdd(blog = AudioPlay.getInstance().data)
        }

        audioPlayChatBtn.setOnClickListener {
            if(Resource.user == null){
                goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            val blog = AudioPlay.getInstance().data ?: return@setOnClickListener
            val commentDialogFragment = CommentDialogFragment(this, blog, object : OnTotalListener {
                override fun total(total: Long?) {
                    blog.showNum(audioPlayChatNum, total?.toInt()!!)
                }
            })
            commentDialogFragment.show(childFragmentManager, blog.toString())
        }

        audioPlayWave.setStyle(
            AudioView.ShowStyle.STYLE_HOLLOW_LUMP, AudioView.ShowStyle.STYLE_WAVE
        )
        requestAudioEvent()
        showProgress(
            AudioPlay.getInstance()
                .duration()
        )

        blogVM!!.followAdd.observe(this, Observer {
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
            setFollow(it.data)
        })
        setFollow(AudioPlay.getInstance().data)
    }

    private fun setFollow(blog: Blog? = null) {
        if (blog == null) {
            audioPlayDuration.visibility = View.GONE
            audioPlayTime.visibility = View.GONE
            audioPlayFollow.visibility = View.GONE
            audioPlayLike.visibility = View.GONE
            audioPlayMenusMsgF.visibility = View.GONE
            return
        }
        if (!blog.isSync) {
            audioPlayDuration.visibility = View.GONE
            audioPlayTime.visibility = View.GONE
            audioPlayFollow.visibility = View.GONE
            audioPlayLike.visibility = View.GONE
            audioPlayMenusMsgF.visibility = View.GONE
        } else {
            audioPlayDuration.visibility = View.VISIBLE
            audioPlayTime.visibility = View.VISIBLE
            audioPlayFollow.visibility = View.VISIBLE
            audioPlayLike.visibility = View.VISIBLE
            audioPlayMenusMsgF.visibility = View.VISIBLE
        }

        if (blog.follows == 1) {
            audioPlayFollow.text = "取消关注"
            audioPlayFollow.normalColor = R.color.alpha
        } else {
            audioPlayFollow.text = "关注"
            audioPlayFollow.normalColor = R.color.blue
        }
        audioPlayLikeBtn.isLiked = blog.praises == 1
        audioPlayLike.text = "${blog.praiseNum}"
        blog.showNum(audioPlayChatNum, blog.commentNum)
        ZdEvent.get()
            .with(HOME_REFRESH)
            .post(HOME_REFRESH)
    }

    private fun showProgress(duration: Int) {
        audioPlayDuration.text = DateUtil.formatSeconds(duration / 1000)
        audioPlayProgress.max = duration
        if (AudioPlay.getInstance().isPlaying) {
            anim?.start()
            audioPlay.visibility = View.GONE
        }
        val fileData = AudioPlay.getInstance().data
        var url = Resource.icon
        if (!TextUtils.isEmpty(fileData?.icon)) {
            url = fileData?.icon!!
        }
        Img.loadImageCircle(url, audioPlayHead, R.mipmap.default_user)
        if (fileData != null && !TextUtils.isEmpty(fileData.cover)) {
            url = fileData.cover!!
        }
        Img.loadImageCircle(url, audioPlayCover, R.mipmap.default_user)
        audioBlurListener.blur(url)
        setDes(AudioPlay.getInstance().isPlaying)
    }

    private fun requestAudioEvent() {
        ZdEvent.get()
            .with(AUDIO_STATE, AudioPlay.PlayState::class.java)
            .observes(this, Observer {
                if (audioPlay == null) return@Observer
                when (it) {
                    AudioPlay.PlayState.STATE_INITIALIZING -> {
                        val data = AudioPlay.getInstance().data
                        if (data != null && data.id > 0) {
                            blogVM?.historyAdd(contentId = data.id, source = 4)
                        }
                    }
                    AudioPlay.PlayState.STATE_PLAYING -> {
                        ZdEvent.get()
                            .with(AUDIO_REFRESH)
                            .post(AUDIO_REFRESH)
                        setDes(true)
                        audioPlayMenusMode.setImageResource(
                            AudioService.MODE[AudioPlay.getInstance()
                                .mMode]
                        )
                        showProgress(
                            AudioPlay.getInstance()
                                .duration()
                        )
                        anim?.start()
                    }
                    AudioPlay.PlayState.STATE_RESUME -> {
                        showProgress(
                            AudioPlay.getInstance()
                                .duration()
                        )
                        audioPlayMenusPlay.isSelected = true
                        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                            anim?.resume()
                        }
                    }
                    AudioPlay.PlayState.STATE_PAUSE -> {
                        audioPlay.visibility = View.VISIBLE
                        audioPlay.setImageResource(R.mipmap.play_pause)
                        audioPlayMenusPlay.isSelected = false
                        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                            anim?.pause()
                        }
                    }
                    AudioPlay.PlayState.STATE_IDLE -> {
                        audioPlay.visibility = View.VISIBLE
                        audioPlay.setImageResource(R.mipmap.play)
                        audioPlayMenusPlay.isSelected = false
                        audioPlayProgress.progress = 0
                        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                            anim?.pause()
                        }
                    }
                }
            })
        ZdEvent.get()
            .with(AUDIO_PROGRESS, Int::class.java)
            .observes(this, Observer {
                if (audioPlayProgress == null) return@Observer
                audioPlayProgress.progress = it.toInt()
                audioPlayTime.text = DateUtil.formatSeconds(it / 1000)
            })
        ZdEvent.get()
            .with(AUDIO_CAPTURE, ByteArray::class.java)
            .observes(this, Observer {
                if (it == null) return@Observer
                audioPlayWave?.post {
                    audioPlayWave?.setWaveData(it)
                }
            })
        val data = AudioPlay.getInstance().data
        if (data != null) {
            setFollow(data)
        }
    }

    private fun setDes(isPlay: Boolean) {
        val fileData = AudioPlay.getInstance().data ?: return
        audioPlayName.text = AudioPlay.getInstance().data.name
        val nick = AudioPlay.getInstance().data.nick
        if (!TextUtils.isEmpty(nick)) {
            audioPlayAuth.text = "(${AudioPlay.getInstance().data.nick})"
            audioPlayAuth.visibility = View.VISIBLE
        } else {
            audioPlayAuth.visibility = View.GONE
        }
        audioPlayMenusPlay.isSelected = isPlay
        setFollow(AudioPlay.getInstance().data)
    }

    override fun onOffsetChanged(
        appBarLayout: AppBarLayout?,
        verticalOffset: Int
    ) {

    }
}

const val AUDIO_REFRESH = "audioRefresh"