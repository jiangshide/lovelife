package com.android.sanskrit.mine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.audio.AudioService
import com.android.sanskrit.AppChannel
import com.android.sanskrit.R
import com.android.sanskrit.mine.MINE_OPEN
import com.android.sanskrit.mine.MINE_OPEN_PROFILE
import com.android.sanskrit.mine.fragment.set.*
import com.android.sanskrit.user.UserFragment
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.mine_set_fragment.*
import kotlinx.android.synthetic.main.mine_set_fragment_item.view.*

/**
 * created by jiangshide on 2020/3/19.
 * email:18311271399@163.com
 */
class SetFragment : MyFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.mine_set_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setTopBar(setL)
        mineSetBack.setOnClickListener {
            ZdEvent.get()
                .with(MINE_OPEN)
                .post(MINE_OPEN_PROFILE)
        }

        setRecycleView.create(
            arrayListOf(
                "回忆箱",
                "收藏夹",
                "频道管理",
                "黑名单管理",
//                "账户",
//                "卡券",
//                "实名",
                "显示设置",
                "给点评分",
//                "流量优化",
//                "字体大小",
                "缓存管理",
                "消息通知",
                "关于Zd112",
                "退出登录"
            ), R.layout.mine_set_fragment_item, {
                setItemName.text = it
                if (it == "实名" && Resource.user!!.certification > 0) {
                    setItemDes.text = "已实名"
                } else {
                    setItemDes.text = ""
                }
            }, {
                when (this) {
                    "回忆箱" -> {
                        push(MemoryFragment().setTitle(this))
                    }
                    "收藏夹" -> {
                        push(CollectionFragment().setTitle(this))
                    }
                    "频道管理" -> {
                        push(MyChannelManagerFragment().setTitle(this))
                    }
                    "黑名单管理" -> {
                        push(BlackListFragment().setTitle(this))
                    }
//                    "账户" -> {
//                        push(AccountFragment().setTitle(this))
//                    }
//                    "卡券" -> {
//                        push(CardTicketFragment().setTitle(this))
//                    }
//                    "实名" -> {
//                        push(
//                            CertificationFragment()
//                                .setTitle(this)
//                        )
//                    }
                    "显示设置"->{
                        push(ShowSetFragment().setTitle(this))
                    }
                    "给点评分" -> {
//                        AppChannel.loadApps(mActivity)
//                        push(CacheFragment().setTitle(this))
                        AppChannel.launchAppDetail(mActivity, mActivity.packageName, "")
                    }
//                    "流量优化" -> {
//                        push(FlowFragment().setTitle(this))
//                    }
//                    "字体大小" -> {
//                        push(TypefaceFragment().setTitle(this))
//                    }
                    "缓存管理" -> {
                        push(CacheFragment().setTitle(this))
                    }
                    "消息通知" -> {
                        push(SwitchFragment().setTitle(this))
                    }
                    "关于Zd112" -> {
                        push(AboutFragment().setTitle(this))
                    }
                    "退出登录" -> {
                        AudioService.startAudioCommand(AudioService.CMD_STOP)
                        goFragment(UserFragment::class.java)
                        Resource.clearUser()
                        finish()
                    }
                }
            })
    }
}