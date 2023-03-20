package com.android.sanskrit.publish.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.services.core.PoiItem
import com.android.event.ZdEvent
import com.android.img.Img
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.channel.data.Channel
import com.android.resource.vm.publish.data.Publish
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.utils.FileUtil
import com.android.utils.LogUtil
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import com.android.widget.recycleview.divider.CommonItemDecoration
import kotlinx.android.synthetic.main.publish_common.*
import kotlinx.android.synthetic.main.publish_users_item_icon.view.*

/**
 * created by jiangshide on 2020/7/8.
 * email:18311271399@163.com
 */
open class PublishBaseFragment : MyFragment(), OnAudioFinishListener {

    private var adapter: KAdapter<User>? = null

    var poiItem: PoiItem? = null

    val publish = Publish()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        publishAudioL.setOnClickListener {
            val fileData = publish.files!![0]
            push(
                VideoEditFragment(
                    fileData.path!!,
                    fileData!!.duration.toFloat(), this
                ).setTitle(FileUtil.getFileNameNoExtension(fileData.path))
            )
        }

        publishRecommendChannelL.setOnClickListener {
            push(ChannelManagerFragment())
        }

        publishRemindL.setOnClickListener {
            push(UsersFragment(adapter?.datas()).setTitle("@用户"))
        }

        publishChannelL.setOnClickListener {
            push(ChannelManagerFragment())
            LogUtil.e("it:", it)
        }

        publishLocationL.setOnClickListener {
            push((LocationFragment()))
        }

        publishSyncL.setOnClickListener {
            LogUtil.e("it:", it)
        }

        publishSyncSwitch.setOnCheckedChangeListener { view, isChecked ->
//            if (isChecked) {
//                publishSyncSwitchIcon.setDrawableLeft(R.mipmap.weixined)
//                publishSyncSwitchTips.text = "没想好"
//                publish.weiXin = 1
//            } else {
//                publishSyncSwitchIcon.setDrawableLeft(R.mipmap.unweixin)
//                publishSyncSwitchTips.text = "露一下"
//                publish.weiXin = 0
//            }
        }

        ZdEvent.get()
            .with(LOCATION, PoiItem::class.java)
            .observes(this, Observer {
                this.poiItem = it
                publish.city = poiItem?.cityName
                publish.position = it.title
                publish.address = it.snippet
                publishLocationName.text = it.title
                publishLocationNameIcon.isSelected = true
            })

        ZdEvent.get()
            .with(CHANNEL, Channel::class.java)
            .observes(this, Observer {
                publishChanneName.text = it.name
                publish.channelId = it.id
                publish.uid = Resource.uid!!
                publish.channelCover = it.cover
                Img.loadImageRound(it.cover, publishChanneNameCover, 5)
                publishChanneNameIcon.isSelected = true
                validate()
            })

        ZdEvent.get()
            .with(USERS)
            .observes(this, Observer {
                val users = it as MutableList<User>
                publishRemindTipsIcon.isSelected = users?.size > 0
                val arr = arrayListOf<Long>()
                users.forEach {
                    arr.add(it.id)
                }
                publish.atsJson = arr.toString()
                val layoutManager = LinearLayoutManager(mActivity)
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                adapter =
                    publishRemindRecycleView?.create(users, R.layout.publish_users_item_icon, {
                        Img.loadImageRound(it.icon, usersSelectedIcon, 10)
                    }, {
                        push(UsersFragment(adapter?.datas()).setTitle("@用户"))
                    }, layoutManager, CommonItemDecoration(-20, 0))
            })
    }

    open fun validate() {
//    val disable =
//      TextUtils.isEmpty(publish.name) || publish.channelId == 0L
        val disable =
            TextUtils.isEmpty(publish.title)
        setRightEnable(!disable)
    }

    override fun onPath(path: String?, mixOut: String?) {
        publish?.files!![0].outPath = path
        if (!TextUtils.isEmpty(mixOut)) {
            publishAudio?.text = mixOut
        }
    }
}