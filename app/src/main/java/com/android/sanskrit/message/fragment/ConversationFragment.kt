package com.android.sanskrit.message.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.UserInfo
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.refresh.api.RefreshLayout
import com.android.resource.BuildConfig
import com.android.resource.MyFragment
import com.android.sanskrit.R
import com.android.sanskrit.message.MESSAGE_REFRESH
import com.android.utils.DateUtil
import com.android.utils.LogUtil
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.message_conversation_fragment.*
import kotlinx.android.synthetic.main.message_conversation_fragment_item.view.*

/**
 * created by jiangshide on 2020/5/22.
 * email:18311271399@163.com
 */
class ConversationFragment : MyFragment() {

    private var adapter: KAdapter<Conversation>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.message_conversation_fragment, true,false)
    }

    override fun onTips(view: View?) {
        super.onTips(view)
        initData()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
//    ZdEvent.get().with(MESSAGE_CHAT).observes(this, Observer {
//      initData()
//    })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        initData()
    }

    private fun initData() {
        showLoading()
        val list = JMessageClient.getConversationList()
        hiddle()
        cancelRefresh()
        if (list != null && list.size > 0) {
            if (adapter == null) {
                initView()
            }
            LogUtil.e("list2:", list)
            adapter?.add(list, true)
        } else {
            LogUtil.e("list3:", list)
            noNet("暂无频道!").setTipsRes(R.mipmap.no_data)
        }
    }

    private fun initView() {
        adapter = conversationRecycleView.create(
            arrayListOf<Conversation>(), R.layout.message_conversation_fragment_item, {
                val conversation = it
                conversationDel.setOnClickListener {
                    JMessageClient.deleteSingleConversation(conversation.targetId,conversation.targetAppKey)
                    adapter?.remove(conversation)
                }
                content.setOnClickListener {
                    conversation.resetUnreadCount()
                    JMessageClient.getUserInfo(
                        conversation.latestMessage.targetID,
                        BuildConfig.PUSH_APPKEY,
                        object :
                            GetUserInfoCallback() {
                            override fun gotResult(
                                p0: Int,
                                p1: String?,
                                p2: UserInfo?
                            ) {
                                push(
                                    ChatFragment().setTitle(p2?.nickname),
                                    set("userName", conversation.latestMessage.targetID)
                                )
                            }
                        })
                    adapter?.notifyDataSetChanged()
                    ZdEvent.get().with(MESSAGE_REFRESH).post(MESSAGE_REFRESH)
//      ZdEvent.get().with(MESSAGE_CHAT).post(this)
                }
                if (it.latestMessage != null && it.latestMessage.content is TextContent) {
                    val msgContent = it.latestMessage.content as TextContent
                    conversationDes.text = msgContent.text
                    conversationDate.text = DateUtil.showTimeAhead(it.lastMsgDate)
                    LogUtil.e("------cnt:", it.unReadMsgCnt)
                    if (it.unReadMsgCnt > 0) {
                        conversationNum.visibility = View.VISIBLE
                        conversationNum.text = "${it.unReadMsgCnt}"
                    } else {
                        it.resetUnreadCount()
                        conversationNum.visibility = View.GONE
                    }
                    JMessageClient.getUserInfo(
                        it.latestMessage.targetID,
                        BuildConfig.PUSH_APPKEY,
                        object :
                            GetUserInfoCallback() {
                            override fun gotResult(
                                p0: Int,
                                p1: String?,
                                p2: UserInfo?
                            ) {
                                LogUtil.e("----------p2:", p2, " | p1:", p1)
                                Img.loadImageCircle(p2?.avatar, conversationIcon)
                                conversationName.text = p2?.nickname
                            }

                        })
                } else {
                    conversationName.text = it.latestText
                }
            })
    }
}