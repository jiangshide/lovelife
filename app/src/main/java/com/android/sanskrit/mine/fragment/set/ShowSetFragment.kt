package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.sanskrit.home.HOME_INDEX
import com.android.utils.SPUtil
import kotlinx.android.synthetic.main.mine_set_home_show.*

/**
 * created by jiangshide on 2020/7/30.
 * email:18311271399@163.com
 */
class ShowSetFragment : MyFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.mine_set_home_show)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val index = SPUtil.getInt(HOME_INDEX, 1)
        switchFollow.isChecked = index == 0
        switchRecommend.isChecked = index == 1
        switchFind.isChecked = index == 2

        switchFollow.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                SPUtil.putInt(HOME_INDEX, 0)
                setStatus(follow = true, recommend = false, find = false)
            } else {
                if (!switchRecommend.isChecked && !switchFind.isChecked) {
                    setStatus(follow = false, recommend = true, find = false)
                    SPUtil.putInt(HOME_INDEX, 1)
                }
            }
        }

        switchRecommend.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                setStatus(follow = false, recommend = true, find = false)
                SPUtil.putInt(HOME_INDEX, 1)
            } else {
                if (!switchFollow.isChecked && !switchFind.isChecked) {
                    setStatus(follow = false, recommend = true, find = false)
                    SPUtil.putInt(HOME_INDEX, 1)
                }
            }
        }

        switchFind.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                setStatus(follow = false, recommend = false, find = true)
                SPUtil.putInt(HOME_INDEX, 2)
            } else {
                if (!switchFollow.isChecked && !switchRecommend.isChecked) {
                    setStatus(follow = false, recommend = true, find = false)
                    SPUtil.putInt(HOME_INDEX, 1)
                }
            }
        }
    }

    private fun setStatus(follow: Boolean, recommend: Boolean, find: Boolean) {
        switchFollow.isChecked = follow
        switchRecommend.isChecked = recommend
        switchFind.isChecked = find
    }
}