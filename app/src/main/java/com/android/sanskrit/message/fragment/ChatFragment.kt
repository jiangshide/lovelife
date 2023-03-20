package com.android.sanskrit.message.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.android.api.options.MessageSendingOptions
import cn.jpush.im.api.BasicCallback
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.refresh.header.ClassicsHeader
import com.android.resource.BuildConfig
import com.android.resource.MyFragment
import com.android.sanskrit.DOT
import com.android.sanskrit.R
import com.android.sanskrit.SoftKeyBoardListener
import com.android.sanskrit.SoftKeyBoardListener.OnSoftKeyBoardChangeListener
import com.android.utils.DateUtil
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.message_chat_fragment.*
import kotlinx.android.synthetic.main.message_chat_fragment_item.view.*
import kotlinx.android.synthetic.main.message_chat_fragment_item_left.view.*
import kotlinx.android.synthetic.main.message_chat_fragment_item_right.view.*
import java.util.Collections
import kotlin.Comparator
import kotlin.Int
import kotlin.String

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class ChatFragment : MyFragment(), OnSoftKeyBoardChangeListener {

    private var adapter: KAdapter<Message>? = null
    private var conversation: Conversation? = null

    private var userName = ""
    private var msg: String = ""

    private var offset: Int = 0
    private var limit: Int = 10

    private var date = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.message_chat_fragment)
    }

    override fun onTips(view: View?) {
        super.onTips(view)
        loadData()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        ZdEvent.get().with(DOT).post(DOT)
        SoftKeyBoardListener.setListener(mActivity, this)
        showFloat(false)
        JMessageClient.registerEventReceiver(this)
        chatRefresh.setRefreshHeader(ClassicsHeader(mActivity))
        chatRefresh.setOnRefreshListener(this)
        chatRefresh.setEnableLoadMore(false)
        userName = arguments?.getString("userName")!!
        conversation = Conversation.createSingleConversation(userName, BuildConfig.PUSH_APPKEY)

        chatEdit.setListener { s, input ->
            msg = input
            validate()
        }
        commentEditSend.setOnClickListener {
            sendMsg(msg)
        }
        showView()
        loadData()
        chatRecycleView.setOnTouchListener { _, _ ->
            hide()
            return@setOnTouchListener false
        }
    }

    private fun hide() {
        if (chatEdit != null && chatEdit.isFocused) {
            chatEdit.hide()
            chatRecycleView?.scrollToPosition(adapter?.count()!! - 1)
            chatEdit.clearFocus()
        }
    }

    private fun loadData() {
        val datas = conversation?.getMessagesFromNewest(offset * limit, limit)
        chatRefresh?.finishRefresh()
        if (datas != null && datas.size > 0) {
            if (adapter != null && adapter?.count()!! > 0) {
                Collections.sort(datas, Comparator { o1, o2 ->
                    if (o1.createTime > o2.createTime) {
                        return@Comparator 1
                    }
                    return@Comparator -1
                })
                adapter?.add(0, datas)
            } else {
                Collections.sort(datas, Comparator { o1, o2 ->
                    if (o1.createTime > o2.createTime) {
                        return@Comparator 1
                    }
                    return@Comparator -1
                })
                adapter?.add(datas)
                chatRecycleView?.scrollToPosition(adapter?.count()!! - 1)
            }
            ++offset
        }
        hiddle()
        cancelRefresh()
//    if (adapter == null || adapter?.count() == 0) {
//      noNet("暂无消息!").setTipsRes(R.mipmap.no_data)
//    }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        loadData()
    }

    private fun showView() {
        adapter = chatRecycleView.create(arrayListOf(), R.layout.message_chat_fragment_item, {
            chatLeftR.visibility = View.GONE
            chatRightR.visibility = View.GONE
            val msgContent = it.content as TextContent
            val currDate = DateUtil.showTimeAhead(it.createTime)
            if (date != currDate) {
                date = currDate
                chatDate.visibility = View.VISIBLE
                chatDate.text = date
            } else {
                chatDate.visibility = View.GONE
            }
            if (it.direct.name == "send") {
                chatRightR.visibility = View.VISIBLE
                chatRightDes.text = msgContent.text
                JMessageClient.getUserInfo(it.fromID, BuildConfig.PUSH_APPKEY, object :
                    GetUserInfoCallback() {
                    override fun gotResult(
                        p0: Int,
                        p1: String?,
                        p2: UserInfo?
                    ) {
                        Img.loadImageCircle(p2?.avatar, chatRightIcon)
                        chatRightName.text = p2?.nickname
                    }

                })
            } else {
                chatLeftR.visibility = View.VISIBLE
                chatLeftDes.text = msgContent.text
                chatLeftIcon.setOnClickListener {
//          push(MineFragment(name = fromId))
                }
                JMessageClient.getUserInfo(it.fromID, BuildConfig.PUSH_APPKEY, object :
                    GetUserInfoCallback() {
                    override fun gotResult(
                        p0: Int,
                        p1: String?,
                        p2: UserInfo?
                    ) {
                        Img.loadImageCircle(p2?.avatar, chatLeftIcon)
                        chatLeftName.text = p2?.nickname
                    }

                })
            }
        }, {
            hide()
        })
    }

    private fun sendMsg(msg: String) {
        val msg = conversation?.createSendMessage(TextContent(msg))
        msg?.setOnSendCompleteCallback(object : BasicCallback() {
            override fun gotResult(
                responseCode: Int,
                responseDesc: String?
            ) {
                if (responseCode == 0) {
                    if (adapter != null) {
                        adapter?.add(msg)
                    }
                    chatRecycleView?.scrollToPosition(adapter?.count()!! - 1)
                    chatEdit.setText("")
                }
            }
        })
        val options = MessageSendingOptions()
        options.isRetainOffline = true
        JMessageClient.sendMessage(msg)
    }

    /**
     * 接收在线消息
     */
    fun onEventMainThread(event: MessageEvent) {
        //获取事件发生的会话对象
        val newMessage = event.message //获取此次离线期间会话收到的新消息列表
        adapter?.add(newMessage)
        chatRecycleView?.scrollToPosition(adapter?.count()!! - 1)
        conversation?.resetUnreadCount()
        ZdEvent.get().with(DOT).post(DOT)
    }

    private fun validate() {
        val disable = !TextUtils.isEmpty(msg)
        commentEditSend.isSelected = disable
        commentEditSend.isEnabled = disable
    }

    override fun onPause() {
        super.onPause()
        JMessageClient.unRegisterEventReceiver(this)
    }

    override fun keyBoardShow(height: Int) {
        val animator = ObjectAnimator.ofFloat(mRootView, "translationY", 0f, -height.toFloat())
        animator.duration = 100
        animator.start()
        chatEdit?.isFocusable = true
        chatEdit?.isFocusableInTouchMode = true
        chatEdit?.requestFocus()
        chatRecycleView?.scrollToPosition(adapter?.count()!! - 1)
    }

    override fun keyBoardHide(height: Int) {
        val animator = ObjectAnimator.ofFloat(mRootView, "translationY", -height.toFloat(), 0f)
        animator.duration = 100
        animator.start()
        chatRecycleView?.scrollToPosition(adapter?.count()!! - 1)
    }
}