package com.android.sanskrit.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.MessageEvent
import com.android.event.ZdEvent
import com.android.jpush.PUSH
import com.android.jpush.data.PushBean
import com.android.player.dplay.player.VideoViewManager
import com.android.resource.FOLLOW
import com.android.resource.MyFragment
import com.android.sanskrit.DOT
import com.android.sanskrit.MESSAGE
import com.android.sanskrit.R
import com.android.sanskrit.message.fragment.*
import com.android.tablayout.indicators.LinePagerIndicator
import com.android.utils.LogUtil
import com.android.utils.SPUtil
import com.android.widget.anim.Anim
import kotlinx.android.synthetic.main.message_fragment.*
import kotlinx.android.synthetic.main.message_head_fragment.*

/**
 *
WebSocketHandler.getInstance()
.setUrl("ws://192.168.3.7:9094/echo")
.connect()
WebSocketHandler.getInstance()
.send("the jankey!!s")
 *T
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class MessageFragment : MyFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTopView(false).setTitleView(R.layout.message_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setTopBar(messageL)
        JMessageClient.registerEventReceiver(MainActivity@ this)
        msgTopNotification.setOnClickListener {
            push(ContactFragment())
        }

        messageViewPager.adapter = messageViewPager.create(childFragmentManager)
            .setTitles(
                "推荐关注", "已关注"
            )
            .setFragment(
                FollowFragment(refresh = MESSAGE_FOLLOW_REFRESH),
                FollowFragment(FOLLOW, refresh = MESSAGE_FOLLOWED_REFRESH)
            )
            .initTabs(activity!!, tabsMessage, messageViewPager, true)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setLinePagerIndicator(getColor(R.color.blue))

        messagePraise.setOnClickListener {
            push(PraiseBlogFragment().setTitle("点赞"))
            anim(messagePraise, messagePraiseNum, 0)

            val msg = SPUtil.getInt(MESSAGE, 0)
            val praise = SPUtil.getInt(PRAISE_DOT, 0)
            val count = msg - praise
            LogUtil.e("----------msg:", msg, " | praise:", praise, " | count:", count)
            SPUtil.putInt(MESSAGE, count)
            ZdEvent.get()
                .with(DOT)
                .post(DOT)
        }
        messageFans.setOnClickListener {
            push(FansFragment().setTitle("粉丝"))
            anim(messageFans, messageFansNum, 0)
            SPUtil.putInt(MESSAGE, SPUtil.getInt(MESSAGE, 0) - SPUtil.getInt(FOLLOW_DOT, 0))
            ZdEvent.get()
                .with(DOT)
                .post(DOT)
        }

        messageLetter.setOnClickListener {
            push(ConversationFragment().setTitle("私信"))
        }

        messageView.setOnClickListener {
            push(BrowseFragment().setTitle("浏览"))
            anim(messageView, messageViewNum, 0)
            SPUtil.putInt(MESSAGE, SPUtil.getInt(MESSAGE, 0) - SPUtil.getInt(BROW_DOT, 0))
            ZdEvent.get()
                .with(DOT)
                .post(DOT)
        }

//    Anim.anim(messagePraise, R.anim.heat)
//    Anim.anim(messageFans, R.anim.heat)
//    Anim.anim(messageLetter, R.anim.heat)
//    Anim.anim(messageView, R.anim.heat)

        ZdEvent.get()
            .with(MESSAGE_REFRESH)
            .observes(this, Observer {
                letter()
                ZdEvent.get()
                    .with(MESSAGE_FOLLOW_REFRESH)
                    .post(MESSAGE_FOLLOW_REFRESH)
                ZdEvent.get()
                    .with(MESSAGE_FOLLOWED_REFRESH)
                    .post(MESSAGE_FOLLOWED_REFRESH)
            })

        ZdEvent.get()
            .with(PUSH, PushBean::class.java)
            .observes(this, Observer {
                LogUtil.e("---it:", it)
                when (it.action) {
                    2 -> {
                        if (it.onClick) {
                            anim(messagePraise, messagePraiseNum, 0)
                        } else {
                            anim(messagePraise, messagePraiseNum, -1)
                        }
                        var size = SPUtil.getInt(PRAISE_DOT, 0) + 1
                        SPUtil.putInt(PRAISE_DOT, size)
                        val msg = SPUtil.getInt(MESSAGE, 0)
                        SPUtil.putInt(MESSAGE, msg + 1)
                    }
                    3 -> {
                        if (it.onClick) {
                            anim(messageFans, messageFansNum, 0)
                        } else {
                            anim(messageFans, messageFansNum, -1)
                        }

                        var size = SPUtil.getInt(FOLLOW_DOT, 0) + 1
                        SPUtil.putInt(FOLLOW_DOT, size)
                        val msg = SPUtil.getInt(MESSAGE, 0)
                        SPUtil.putInt(MESSAGE, msg + 1)
                    }
                    4 -> {
                        if (it.onClick) {
                            anim(messageView, messageViewNum, 0)
                        } else {
                            anim(messageView, messageViewNum, -1)
                        }

                        var size = SPUtil.getInt(BROW_DOT, 0) + 1
                        SPUtil.putInt(BROW_DOT, size)
                        val msg = SPUtil.getInt(MESSAGE, 0)
                        SPUtil.putInt(MESSAGE, msg + 1)
                        LogUtil.e(
                            "--------size:",
                            size,
                            " | msg:",
                            msg,
                            " | sss:",
                            SPUtil.getInt(MESSAGE, 0)
                        )
                    }
                }
                ZdEvent.get()
                    .with(DOT)
                    .post(DOT)
            })
        letter()
        ZdEvent.get()
            .with(DOT)
            .observes(this, Observer {
                letter()
            })
    }

    fun onEventMainThread(event: MessageEvent) {
        letter()
    }

    private fun letter() {
        val count = JMessageClient.getAllUnReadMsgCount()
        anim(messageLetter, messageLetterNum, count)
    }

    private fun anim(
        view: View,
        dotView: TextView,
        num: Int
    ) {
        if (num == 0) {
            view.clearAnimation()
            dotView.visibility = View.GONE
            return
        }
        if (num > 0) {
            dotView.text = "$num"
        }
        dotView.visibility = View.VISIBLE
        Anim.anim(view, R.anim.heat)
    }

    override fun onResume() {
        super.onResume()
        VideoViewManager.instance()
            .pause()
    }
}

const val MESSAGE_REFRESH = "messageRefresh"
const val MESSAGE_FOLLOW_REFRESH = "messageFollowRefresh"
const val MESSAGE_FOLLOWED_REFRESH = "messageFollowedRefresh"
const val PRAISE_DOT = "praiseDot"
const val FOLLOW_DOT = "followDot"
const val BROW_DOT = "browDot"

