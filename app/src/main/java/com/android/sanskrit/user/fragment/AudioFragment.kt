package com.android.sanskrit.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.base.BaseActivity
import com.android.img.Img
import com.android.player.dplay.player.VideoViewManager
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.sanskrit.R
import com.android.sanskrit.user.audio.AudioLrcFragment
import com.android.sanskrit.user.audio.AudioPlayFragment
import com.android.sanskrit.user.audio.AudioRecommendFragment
import com.android.tablayout.indicators.LinePagerIndicator
import com.android.utils.LogUtil
import com.android.utils.SystemUtil
import com.android.widget.ZdDialog
import kotlinx.android.synthetic.main.user_audio_fragment.*

/**
 * created by jiangshide on 2020/3/18.
 * email:18311271399@163.com
 */
class AudioFragment : MyFragment(),
    AudioBlurListener,
    AudioListener, OnPageChangeListener {

    private var blurUrl: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.user_audio_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        showFloatMenu(false)
        VideoViewManager.instance()
            .pause()
    }

    override fun onPause() {
        super.onPause()
        showFloatMenu(true)
        mActivity.mZdTab.setAnim(false)
        if (Resource.TAB_INDEX == 0) {
            VideoViewManager.instance()
                .resume()
        }
    }

    private fun initView() {
        statusBarView.layoutParams.height = SystemUtil.getStatusBarHeight()
        audioBack?.setOnClickListener {
            pop()
        }
        audioShare?.setOnClickListener {
            ZdDialog.createList(context, listOf("站内好友", "微信朋友圈", "微信好友"))
                .setOnItemListener { parent, view, position, id ->
                }
                .show()
        }
        audioViewPager?.adapter = audioViewPager!!.create(childFragmentManager)
            .setTitles(
                "推荐", "歌曲", "歌词"
            )
            .setFragment(
                AudioRecommendFragment(),
                AudioPlayFragment(mActivity as BaseActivity, this, this),
                AudioLrcFragment()
            )
            .initTabs(activity!!, tabsAudio, audioViewPager)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setLinePagerIndicator(getColor(R.color.blue))
            .setListener(this)
        audioViewPager?.setCurrentItem(1, true)
    }

    override fun blur(url: String) {
        blurUrl = url
        Img.loadImagBlur(blurUrl, audioBgImg, 25, 25)
        onPageSelected(audioViewPager.currentItem)
    }

    override fun audio(status: Int) {
        when (status) {
            AUDIO_STATE_CLOSE -> {
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                Img.loadImagBlur(blurUrl, audioBgImg, 25, 10)
            }
            1 -> {
                Img.loadImagBlur(blurUrl, audioBgImg, 25, 25)
            }
            2 -> {
                Img.loadImage(blurUrl, audioBgImg)
            }
        }
    }
}

interface AudioBlurListener {
    fun blur(url: String)
}

interface AudioListener {
    fun audio(status: Int)//-1~close;
}

const val AUDIO_STATE_CLOSE = -1
