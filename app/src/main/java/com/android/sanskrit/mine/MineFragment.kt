package com.android.sanskrit.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.event.ZdEvent
import com.android.http.exception.HttpException
import com.android.img.Img
import com.android.player.dplay.player.VideoViewManager
import com.android.resource.*
import com.android.resource.vm.report.data.Report
import com.android.resource.vm.user.OnFollowListener
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.sanskrit.blog.adapter.FROM_MINE
import com.android.sanskrit.blog.fragment.MINE_USER_BLOG_REFRESH
import com.android.sanskrit.blog.fragment.UserBlogFragment
import com.android.sanskrit.home.HOME_MANAGER
import com.android.sanskrit.home.HOME_REFRESH
import com.android.sanskrit.message.fragment.ChatFragment
import com.android.sanskrit.mine.fragment.*
import com.android.sanskrit.mine.fragment.profile.EditRemarksFragment
import com.android.sanskrit.report.ReportFragment
import com.android.sanskrit.user.UserFragment
import com.android.sanskrit.user.audio.HistoryAudioFragment
import com.android.tablayout.indicators.LinePagerIndicator
import com.android.utils.AppUtil
import com.android.utils.LogUtil
import com.android.widget.ZdDialog
import com.android.widget.ZdToast
import com.android.widget.extension.setDrawableLeft
import com.android.widget.extension.setDrawableRight
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlinx.android.synthetic.main.mine_fragment.*
import kotlinx.android.synthetic.main.mine_fragment_head.*
import kotlinx.android.synthetic.main.vip.*

/**
 * created by jiangshide on 2020/3/19.
 * email:18311271399@163.com
 */
class MineFragment(
    private val from: Int = FROM_MINE,
    private var id: Long? = Resource.uid,
    private val isBack: Boolean? = false,
    private val isBottomBar:Boolean=true,
    private val isShowBack:Boolean?=true
) : MyFragment(), OnOffsetChangedListener, OnPageChangeListener, OnFollowListener {

    private var uid = id

    private var isBlack = false

    private var userBlogFragment: UserBlogFragment? = null
    private var albumFragment: AlbumFragment? = null
    private var myChannelFragment: MyChannelFragment? = null
    private var historyAudioFragment: HistoryAudioFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.mine_fragment)
    }

    fun updateData(id: Long) {
        uid = id
        userVM?.profile(id)
        userBlogFragment?.updateData(id)
        albumFragment?.updateData(id)
        myChannelFragment?.updateData(id)
        historyAudioFragment?.updateData(id)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setTopBar(mineL)
        if(isBottomBar){
            bottomBar.visibility = View.VISIBLE
        }else{
            bottomBar.visibility = View.GONE
        }
        userInvitationCode.setOnClickListener {
            AppUtil.copy2Clipboard(userInvitationCode.text.toString())
            ZdToast.txt("已复制,去分享给TA吧!")
        }

        vipL.setOnClickListener {
            push(VipFragment())
        }

        userBlogFragment = UserBlogFragment(from = from, id = uid)
        albumFragment = AlbumFragment(id = uid)
        myChannelFragment = MyChannelFragment(id = uid)

        var titles: ArrayList<String>? = null
        var fragments: ArrayList<MyFragment>? = null
        if (uid == Resource.uid && !isShowBack!!) {
            titles = arrayListOf("动态", "相册", "频道")
            fragments = arrayListOf(
                userBlogFragment!!,
                albumFragment!!,
                myChannelFragment!!
            )
        } else {
            historyAudioFragment = HistoryAudioFragment(id = uid)
            titles = arrayListOf("动态", "相册", "频道", "乐听")
            fragments = arrayListOf(
                userBlogFragment!!,
                albumFragment!!,
                myChannelFragment!!, historyAudioFragment!!
            )
        }
        mineViewPager.adapter = mineViewPager.create(childFragmentManager)
            .setTitles(titles)
            .setFragment(fragments)
            .initTabs(activity!!, tabsMine, mineViewPager)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelecteSize(13)
            .setTxtSelectedSize(16)
            .setLinePagerIndicator(getColor(R.color.blue))
            .setListener(this)
        onPageSelected(0)
        appBarLayout.addOnOffsetChangedListener(this)

        userVM!!.profileUid.observe(this, Observer {
            hiddle()
            if (it.error != null) {
//                ZdToast.txt(it.error.message)
                return@Observer
            }
            setProfile(it.data)
        })

        userVM!!.friendAdd.observe(this, Observer {
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
            isBlack = !isBlack
            ZdEvent.get()
                .with(HOME_REFRESH)
                .post(HOME_REFRESH)
            pop()
        })

        ZdEvent.get()
            .with(REFRESH_MINE)
            .observes(this, Observer {
                userVM?.profile(uid)
                showLoading()
            })
        showView()
    }

    private fun showView() {
        if (uid == Resource.uid && !isShowBack!!) {
            mineEdit.setOnClickListener {
                push(EditProfileFragment().setTitle("编辑资料"))
            }
//      mineTopBack.visibility = View.GONE
            mineTopFollow.visibility = View.GONE
            mineTopBack.setDrawableLeft(R.mipmap.card)
            mineTopBack.setOnClickListener {
                push(BusinessCardFragment().setTitle("名片"))
            }
            mineTopMore.setDrawableRight(R.mipmap.unmenu)
            mineTopMore.setOnClickListener {
                ZdEvent.get()
                    .with(MINE_OPEN)
                    .post(MINE_OPEN_SET)
            }
            mineEdit.text = "编辑资料"
            mineEdit?.visibility = View.VISIBLE
            mineVisitor.text = "历程"
            mineVisitor.setOnClickListener {
//        push(
//            VisitorFragment()
//                .setTitle("访客")
//        )
                push(
                    CourseFragment(Resource.uid!!)
                        .setTitle("历程")
                )
            }
            setProfile()
        } else {
            mineTopBack.visibility = View.VISIBLE
            mineTopBack.setDrawableLeft(R.mipmap.back)
            mineTopBack.setOnClickListener {
                if (isBack!!) {
                    ZdEvent.get()
                        .with(HOME_MANAGER)
                        .post(HOME_MANAGER)
                } else {
                    pop()
                }
            }
            mineTopFollow.visibility = View.VISIBLE
            mineTopMore.setDrawableRight(R.mipmap.more)
            userVM?.profile(uid)
            showLoading()
        }
    }

    private fun showDialog(
        user: User,
        data: ArrayList<String>,
        dataColors: ArrayList<Int>
    ) {
        if(Resource.user == null){
            goFragment(UserFragment::class.java)
            return
        }
        ZdDialog.createList(context, data, dataColors)
            .setOnItemListener { parent, view, position, id ->
                when (data[position]) {
                    "关注", "取消关注" -> {
                        user.follows = FOLLOW - user.follows
                        follow(user)
                    }
                    "特别关注", "取消特别关注" -> {
                        if (user.follows == ESPECIALLY) {
                            user.follows = FOLLOW
                        } else {
                            user.follows = ESPECIALLY
                        }
                        follow(user)
                    }
                    "设置备注名" -> {
                        push(EditRemarksFragment(), user)
                    }
                    "举报" -> {
                        val report = Report(
                            uid = Resource.uid!!, contentId = user.id, source = CONTENT_FROM_USER,
                            name = user.nick
                        )
                        push(ReportFragment(), report)
                    }
                    "拉黑" -> {
//              push(EditBlackFragment(),set("id",user.id))
                        ZdDialog.create(mActivity)
                            .setTitles("加入黑名单")
                            .setContent("加入黑名单，你将不再收到对方的消息,也不会看到对方发布的动态")
                            .setListener { isCancel, editMessage ->
                                userVM?.friendAdd(uid = user.id, status = -1)
                            }
                            .show()
                    }
                    "取消拉黑" -> {
                        userVM?.friendAdd(uid = user.id, status = 0)
                    }
                }
            }
            .show()
    }

    private fun follow(user: User) {
        if(Resource.user == null){
            goFragment(UserFragment::class.java)
            return
        }
        if(user.id == Resource.uid){
            ZdToast.txt("不能关注自己!")
            return
        }
        userVM?.followAdd(uid = user.id, status = user.follows, listener = this)
        showLoading()
    }

    private fun setProfile(
        user: User? = Resource.user
    ) {
        val user = user ?: return

        setTitle(user.nick)
        userNick.text = user.nick
        user.setSex(mineSexAgeAddr)
        Img.loadImageCircle(user.icon, userIcon, R.mipmap.default_user)
        Img.loadImageCircle(user.icon, mineTopIcon, R.mipmap.default_user)
        userInvitationCode.text = "记号:${user.id}"
        if (user.id != Resource.uid) {
            mineTopMore.setOnClickListener {
                var black = "拉黑"
                if (isBlack) {
                    black = "取消拉黑"
                }
                if (user?.follows == ESPECIALLY) {
                    showDialog(
                        user!!, arrayListOf("取消特别关注", "取消关注", "设置备注名", "举报", black),
                        arrayListOf<Int>(R.color.font, R.color.font, R.color.redLight)
                    )
                } else if (user?.follows == FOLLOW) {
                    showDialog(
                        user!!, arrayListOf("特别关注", "取消关注", "设置备注名", "举报", black),
                        arrayListOf<Int>(R.color.blue, R.color.font, R.color.redLight)
                    )
                } else {
                    showDialog(
                        user!!, arrayListOf("特别关注", "设置备注名", "举报", black),
                        arrayListOf<Int>(R.color.blue, R.color.redLight)
                    )
                }
            }
            mineEdit.text = "私信"
            mineEdit.visibility = View.VISIBLE
            mineEdit.setOnClickListener {
                if(Resource.user == null){
                    goFragment(UserFragment::class.java)
                    return@setOnClickListener
                }
                push(ChatFragment().setTitle(user.nick), set("userName", user.unionId))
            }
            mineTopTitle.text = user.nick
            mineTopTitleSmall.text = "记号：${user.id}"

            setFollow(user.follows)

            mineVisitor.setOnClickListener {
                user.follows = 1 - user.follows
                follow(user)
            }
            mineTopFollow.setOnClickListener {
                user.follows = 1 - user.follows
                follow(user)
            }
        } else {
            mineTopTitle.text = user.nick
            mineTopTitleSmall.text = "记号：${user.id}"
        }
        mineIntroValue.setTxt(user.intro)
        userIcon.setOnClickListener {
            push(ProfileFragment().setTitle("个人资料"), user)
        }
    }

    private fun setFollow(status: Int) {
        if (mineVisitor == null) return
        ZdEvent.get()
            .with(HOME_REFRESH)
            .post(HOME_REFRESH)
        mineVisitor?.visibility = View.VISIBLE
        if (status == FOLLOW) {
            mineVisitor.text = "取消关注"
            mineTopFollow.text = "取消关注"
            mineVisitor.setTextColor(getColor(R.color.fontLight))
        } else {
            mineVisitor.text = "关注TA"
            mineTopFollow.text = "关注TA"
            mineVisitor.setTextColor(getColor(R.color.font))
        }
    }

    override fun onOffsetChanged(
        appBarLayout: AppBarLayout?,
        verticalOffset: Int
    ) {
        val size = verticalOffset * -1 / 308f
//    zdTopView.topRightBtn.alpha = size
        mineTopTitleL.alpha = size
        mineTopFollow.alpha = size
        if (size == 0.0f) {
            mineTopTitleL.visibility = View.GONE
            mineTopFollow.visibility = View.GONE
        } else {
            mineTopTitleL.visibility = View.VISIBLE
            if (uid != Resource.uid) {
                mineTopFollow.visibility = View.VISIBLE
            } else {
                mineTopFollow.visibility = View.GONE
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
                setRefreshEvent(MINE_USER_BLOG_REFRESH)
            }
            1 -> {
                setRefreshEvent(MINE_ALBUM_REFRESH)
            }
            2 -> {
                setRefreshEvent(MINE_MY_CHANNEL_REFRESH)
            }
        }
    }

    override fun follow(
        status: Int,
        e: HttpException?
    ) {
        hiddle()
        if (e != null) {
            ZdToast.txt(e.message)
            return
        }
        setFollow(status)
    }

    override fun onResume() {
        super.onResume()
        VideoViewManager.instance()
            .pause()
    }

    override fun onPause() {
        super.onPause()
        if (Resource.TAB_INDEX == 0) {
            VideoViewManager.instance()
                .resume()
        }
    }
}