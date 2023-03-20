package com.android.sanskrit.publish.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.sanskrit.user.audio.HistoryAudioFragment
import com.android.sanskrit.user.audio.LocalMusicFragment
import com.android.tablayout.indicators.LinePagerIndicator
import kotlinx.android.synthetic.main.video_edit_audio.*

/**
 * created by jiangshide on 2020/7/27.
 * email:18311271399@163.com
 */
class EditAudioManagerFragment(private val listener: OnAudioFinishListener? = null) : MyFragment() {

    private var editAudioFragment: EditAudioFragment? = null
    private var historyAudioFragment: HistoryAudioFragment? = null
    private var localMusicFragment: LocalMusicFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.video_edit_audio)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mixSearchCancel.setOnClickListener {
            pop()
        }
        mixSearchEdit.setListener { s, input ->
            when (mixAudioViewPager.currentItem) {
                0 -> {
                    editAudioFragment?.search(input)
                    historyAudioFragment?.search("")
                    localMusicFragment?.search("")
                }
                1 -> {
                    historyAudioFragment?.search(input)
                    editAudioFragment?.search("")
                    localMusicFragment?.search("")
                }
                2 -> {
                    localMusicFragment?.search(input)
                    editAudioFragment?.search("")
                    historyAudioFragment?.search("")
                }
            }
        }
        editAudioFragment = EditAudioFragment(listener)
        historyAudioFragment = HistoryAudioFragment(listener)
        localMusicFragment = LocalMusicFragment(listener)
        mixAudioViewPager.adapter = mixAudioViewPager.create(childFragmentManager)
            .setTitles(
                "推荐", "乐听", "本地"
            )
            .setFragment(
                editAudioFragment,
                historyAudioFragment,
                localMusicFragment
            )
            .initTabs(activity!!, tabsMixAudio, mixAudioViewPager, true)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setLinePagerIndicator(getColor(R.color.blue))
    }
}


public interface OnAudioFinishListener {
    fun onPath(path: String?,mixOut:String?=null)
}