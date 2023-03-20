package com.android.sanskrit.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.sanskrit.mine.fragment.SetFragment
import com.android.tablayout.indicators.LinePagerIndicator
import kotlinx.android.synthetic.main.mine_manager_fragment.*

/**
 * created by jiangshide on 2020/5/27.
 * email:18311271399@163.com
 */
class MineManagerFragment : MyFragment(), OnPageChangeListener {

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
        mineManagerViewPager.adapter = mineManagerViewPager.create(childFragmentManager)
            .setTitles(
                "我的", "设置"
            )
            .setFragment(
                MineFragment(isShowBack = false),
                SetFragment()
            )
            .initTabs(activity!!, tabsMineManager, mineManagerViewPager, true)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelectedColor(R.color.black)
            .setTxtSelecteColor(R.color.gray)
            .setLinePagerIndicator(getColor(R.color.blue))
            .setListener(this)
        ZdEvent.get()
            .with(MINE_OPEN, Int::class.java)
            .observes(this, Observer {
                mineManagerViewPager.currentItem = it
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
    }

    override fun onBackPressed(): Boolean {
        if (mineManagerViewPager.currentItem > 0) {
            mineManagerViewPager.currentItem = 0
            return true
        }
        return super.onBackPressed()
    }
}

const val MINE_OPEN = "mineOpen"
const val MINE_OPEN_PROFILE = 0
const val MINE_OPEN_SET = 1