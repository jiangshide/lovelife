package com.android.sanskrit.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.sanskrit.home.fragment.PLAYING_ID
import com.android.sanskrit.mine.MineFragment
import com.android.tablayout.indicators.LinePagerIndicator
import com.android.utils.LogUtil
import kotlinx.android.synthetic.main.mine_manager_fragment.*

/**
 * created by jiangshide on 2020/7/15.
 * email:18311271399@163.com
 */
class HomeManagerFragment : MyFragment(), OnPageChangeListener {

    private var mineFragment: MineFragment? = null
    private var id: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.mine_manager_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mineFragment = MineFragment(id = -1, isBack = true, isBottomBar = false)
        mineManagerViewPager.adapter = mineManagerViewPager.create(childFragmentManager)
            .setTitles(
                "Home", "Mine"
            )
            .setFragment(
                HomeFragment(),
                mineFragment
            )
            .initTabs(activity!!, tabsMineManager, mineManagerViewPager, true)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setLinePagerIndicator(getColor(R.color.blue))
            .setPersistent(false)
            .setListener(this)
        ZdEvent.get()
            .with(HOME_MANAGER, String::class.java)
            .observes(this, Observer {
                if (it == HOME_MANAGER) {
                    mineManagerViewPager.currentItem = 0
                } else {
                    mineManagerViewPager.currentItem = 1
                    ZdEvent.get().with(HOME_PLAY).post(false)
                }
            })
        ZdEvent.get()
            .with(PLAYING_ID, Long::class.java)
            .observes(this, Observer {
                this.id = it
                mineFragment?.updateData(it)
            })
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
        showTabHost(position == 1)
        if (position == 1) {
            mineFragment?.updateData(id)
        }
    }

    override fun onBackPressed(): Boolean {
        if (mineManagerViewPager.currentItem > 0) {
            mineManagerViewPager.currentItem = 0
            return true
        }
        return super.onBackPressed()
    }
}

const val HOME_MANAGER = "homeManager"