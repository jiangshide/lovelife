package com.android.sanskrit.blog.adapter

import android.R.color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.android.event.ZdEvent
import com.android.http.exception.HttpException
import com.android.player.dplay.player.VideoView
import com.android.resource.FOLLOW
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.UNFOLLOW
import com.android.resource.cache.PreloadManager
import com.android.resource.view.RedHeartLayout
import com.android.resource.view.like.LikeButton
import com.android.resource.view.like.OnLikeListener
import com.android.resource.vm.blog.BlogVM
import com.android.resource.vm.blog.OnBlogListener
import com.android.resource.vm.blog.OnPraiseListener
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.user.OnFollowListener
import com.android.resource.vm.user.UserVM
import com.android.sanskrit.R.id
import com.android.sanskrit.R.layout
import com.android.sanskrit.blog.TikTokController
import com.android.sanskrit.blog.fragment.CalculateDistanceFragment
import com.android.sanskrit.blog.fragment.CommentDialogFragment
import com.android.sanskrit.blog.fragment.OnTotalListener
import com.android.sanskrit.channel.fragment.ChannelManagerBlogFragment
import com.android.sanskrit.home.HOME_REFRESH
import com.android.sanskrit.mine.MineFragment
import com.android.sanskrit.user.UserFragment
import com.android.utils.LogUtil
import com.android.utils.SPUtil
import com.android.widget.ZdButton
import com.android.widget.ZdImageView
import com.android.widget.ZdToast
import com.bumptech.glide.Glide
import java.util.*

/**
 * created by jiangshide on 2020/7/10.
 * email:18311271399@163.com
 */
class VideoAdapter(
    private val fragment: MyFragment,
    val userVM: UserVM,
    val blogVM: BlogVM,
    private var mDatas: MutableList<Blog>?
) :
    PagerAdapter() {
    /**
     * View缓存池，从ViewPager中移除的item将会存到这里面，用来复用
     */
    private val mViewPool: MutableList<View> = ArrayList()

    fun add(list: List<Blog>?) {
        this.add(-1, list)
    }

    fun add(
        list: List<Blog>?,
        isRefresh: Boolean
    ) {
        this.add(-1, list, isRefresh)
    }

    @JvmOverloads
    fun add(
        index: Int,
        list: List<Blog>?,
        isRefresh: Boolean = false
    ) {
        if (list == null || list.isEmpty()) return
        if (mDatas == null) {
            mDatas = ArrayList()
        }
        if (isRefresh) {
            mDatas!!.clear()
        }
        if (index != -1) {
            mDatas!!.addAll(index, list)
        } else {
            mDatas!!.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun add(t: Blog) {
        if (mDatas == null) {
            mDatas = ArrayList()
        }
        mDatas!!.add(t)
        notifyDataSetChanged()
    }

    fun update(datas: MutableList<Blog>?) {
        if (datas == null) return
        mDatas = datas
        notifyDataSetChanged()
    }

    fun update(t: Blog?) {
        if (mDatas == null || t == null) return
        val size = mDatas!!.size - 1
        for (i in 0 until size) {
            if (mDatas!![i] == t) {
                mDatas!![i] = t
            }
        }
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        if (mDatas == null || mDatas!!.size - 1 < position) return
        mDatas!!.removeAt(position)
        notifyDataSetChanged()
    }

    fun remove(t: Blog?) {
        if (mDatas == null) return
        mDatas!!.remove(t)
        notifyDataSetChanged()
    }

    fun top(t: Blog) {
        if (mDatas == null) return
        mDatas!!.remove(t)
        mDatas!!.add(0, t)
        notifyDataSetChanged()
    }

    val datas: List<Blog>?
        get() = mDatas

    val size: Int
        get() = if (mDatas == null) 0 else mDatas!!.size

    fun getData(position: Int): Blog {
        return mDatas!![position]
    }

    fun clear() {
        if (mDatas == null) return
        mDatas!!.clear()
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (mDatas == null) 0 else mDatas!!.size
    }

    override fun isViewFromObject(
        view: View,
        o: Any
    ): Boolean {
        return view === o
    }

    override fun instantiateItem(
        container: ViewGroup,
        position: Int
    ): Any {
        val context = container.context
        var view: View? = null
        if (mViewPool.size > 0) { //取第一个进行复用
            view = mViewPool[0]
            mViewPool.removeAt(0)
        }
        val viewHolder: ViewHolder
        if (view == null) {
            view = LayoutInflater.from(context)
                .inflate(layout.home_video_fragment_item, container, false)
            viewHolder = ViewHolder(view)
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val item = mDatas!![position]

        viewHolder.mVideoItemSound.isSelected = SPUtil.getBoolean(VIDEO_SOUND, true)

        var cover = item.cover
        if (TextUtils.isEmpty(cover)) {
            cover = item.url
        }
//        viewHolder.mVideoItemRotate.setOnClickListener {
//            var rotate = viewHolder.mVideoView.rotation
//            if(rotate > 270){
//                rotate = 0f
//            }
//            viewHolder.mVideoView.rotation = rotate+90
//            LogUtil.e("---------jsd~rotate:",viewHolder.mVideoView.rotation)
//        }
//        val percent = item.width.toFloat() / item.height.toFloat()
//        val layoutParams = viewHolder.mThumb.layoutParams
//        if (percent >= 1.77) {
//            viewHolder.mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9)
//            var width = item.width
//            var height = item.height
//            if (height > width / 16 * 9) {
//                height = width / 16 * 9
//            } else {
//                width = height / 9 * 16
//            }
//            layoutParams.width = width
//            layoutParams.height = height
//            viewHolder.mThumb.layoutParams = layoutParams
//        } else {
//            viewHolder.mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP)
//        }
        Glide.with(context)
            .load(cover)
            .placeholder(color.white)
            .into(viewHolder.mThumb)
        //开始预加载
        PreloadManager.getInstance(context)
            .addPreloadTask(item.url, position)

        viewHolder.mPosition = position
        viewHolder.mVideoItemIcon.loadCircle(item.icon)
        viewHolder.mVideoItemIcon.setOnClickListener {
            if (Resource.user == null) {
                fragment?.goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            fragment.push(MineFragment(id = item.uid, isBottomBar = false))
        }
        viewHolder.mVideoItemFollow.setOnClickListener {

        }
        viewHolder.mVideoItemPraiseIcon.isLiked = item.getFollow()
        viewHolder.mRedHeart.setOnListener {
            if (it) {
                viewHolder.mVideoItemPraiseIcon.onClick(viewHolder.mVideoItemPraiseIcon)
            }
        }
        viewHolder.mVideoItemFollow.visibility =
            if (item.follows > 0 || item.uid == Resource.uid) View.GONE else View.VISIBLE
        viewHolder.mVideoItemFollow.setOnClickListener {
            if (Resource.user == null) {
                fragment?.goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            userVM?.followAdd(item.uid, FOLLOW, object : OnFollowListener {
                override fun follow(
                    status: Int,
                    e: HttpException?
                ) {
                    if (e != null) {
                        ZdToast.txt(e.message)
                        return
                    }
                    item.status = status
                    viewHolder?.mVideoItemFollow?.visibility = View.GONE
                    ZdEvent.get()
                        .with(HOME_REFRESH)
                        .post(HOME_REFRESH)
                }
            })
        }
        viewHolder.mVideoItemPraiseIcon.isLiked = item.praises > 0
        item.showNum(viewHolder.mVideoItemPraiseTxt, item.praiseNum)
        viewHolder.mVideoItemPraiseIcon.isEnabled = true
        viewHolder.mVideoItemPraiseIcon.setOnAnimationEndListener {

        }
        viewHolder.mVideoItemPraiseIcon.isEnabled = Resource.user != null
        viewHolder.mVideoItemPraiseIcon.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {

                item.praises = FOLLOW
                item.praiseNum += 1
                viewHolder.mVideoItemPraiseIcon.isLiked = true
                item.showNum(viewHolder.mVideoItemPraiseTxt, item.praiseNum)
                blogVM?.praiseAdd(item, object : OnPraiseListener {
                    override fun onPraise(it: Blog) {
                        item?.praiseNum = it.praiseNum
                        item?.praises = it.praises
                        viewHolder.mVideoItemPraiseIcon.isLiked = it.praises == FOLLOW
                        item.showNum(viewHolder.mVideoItemPraiseTxt, it.praiseNum)
                        ZdEvent.get()
                            .with(HOME_REFRESH)
                            .post(HOME_REFRESH)
                    }
                })
            }

            override fun unLiked(likeButton: LikeButton?) {
                item.praises = UNFOLLOW
                item.praiseNum -= 1
                viewHolder.mVideoItemPraiseIcon.isLiked = false
                item.showNum(viewHolder.mVideoItemPraiseTxt, item.praiseNum)
                blogVM?.praiseAdd(item, object : OnPraiseListener {
                    override fun onPraise(it: Blog) {
                        item?.praiseNum = it.praiseNum
                        item?.praises = it.praises
                        viewHolder.mVideoItemPraiseIcon.isLiked = it.praises == FOLLOW
                        item.showNum(viewHolder.mVideoItemPraiseTxt, it.praiseNum)
                        ZdEvent.get()
                            .with(HOME_REFRESH)
                            .post(HOME_REFRESH)
                    }
                })
            }
        })
        item.showNum(viewHolder.mVideoItemCommentTxt, item.commentNum)
        viewHolder.mVideoItemCommentL.setOnClickListener {
            if (Resource.user == null) {
                fragment?.goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            showComment(item, object : OnTotalListener {
                override fun total(total: Long?) {
                    item.showNum(viewHolder.mVideoItemCommentTxt, total?.toInt()!!)
                    ZdEvent.get()
                        .with(HOME_REFRESH)
                        .post(HOME_REFRESH)
                }
            })
        }
        item.showNum(viewHolder.mVideoItemShareTxt, item.collectionNum)
        viewHolder.mVideoItemShareIcon?.isSelected = item.collections > 0
        viewHolder.mVideoItemShareL.setOnClickListener {
            if (Resource.user == null) {
                fragment?.goFragment(UserFragment::class.java)
                return@setOnClickListener
            }
            item.collections = FOLLOW - item.collections
            viewHolder?.mVideoItemShareIcon?.isSelected = item.collections > 0
            blogVM?.collectionAdd(item, object : OnBlogListener {
                override fun onBlog(blog: Blog?, e: Exception?) {
                    if (e != null) {
                        viewHolder?.mVideoItemShareIcon?.isSelected =
                            !viewHolder?.mVideoItemShareIcon?.isSelected
                    }
                }
            })
//            ZdDialog.createList(context, listOf("站内好友", "微信朋友圈", "微信好友"))
//                .setOnItemListener { parent, view, position, id ->
//                    blogVM?.shareAdd(item, arrayListOf(2, 3, 4))
//                }
//                .show()
        }
        if (!TextUtils.isEmpty(item.channel)) {
            viewHolder.mVideoItemChannel.text = "# ${item.channel}"
            viewHolder.mVideoItemChannel.visibility = View.VISIBLE
            viewHolder.mVideoItemChannel.setOnClickListener {
                if (Resource.user == null) {
                    fragment?.goFragment(UserFragment::class.java)
                    return@setOnClickListener
                }
                fragment?.push(
                    ChannelManagerBlogFragment(
                        uid = item.uid, channelId = item.channelId, blogId = item.id
                    )
                )
            }
        } else {
            viewHolder.mVideoItemChannel.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(item.city)) {
            viewHolder.mVideoItemLocation.text = item.city
            viewHolder.mVideoItemLocation.visibility = View.VISIBLE
            viewHolder.mVideoItemLocation.setOnClickListener {
                if (Resource.user == null) {
                    fragment?.goFragment(UserFragment::class.java)
                    return@setOnClickListener
                }
                fragment?.push(CalculateDistanceFragment(), item)
            }
        } else {
            viewHolder.mVideoItemLocation.visibility = View.GONE
        }
        viewHolder.mVideoItemName.text = item.title
        if (!TextUtils.isEmpty(item.des)) {
            viewHolder.mVideoItemDes.text = item.des
            viewHolder.mVideoItemDes.visibility = View.VISIBLE
        } else {
            viewHolder.mVideoItemDes.visibility = View.GONE
        }
        //viewHolder.mVideoSeekBar.setMax((int) item.getDuration());
        //viewHolder.mVideoView.setProgressManager(new ProgressManager() {
        //  @Override public void saveProgress(String url, long progress) {
        //    LogUtil.e("-------url:", url, " | progress:", progress);
        //    viewHolder.mVideoSeekBar.setProgress((int) progress);
        //  }
        //
        //  @Override public long getSavedProgress(String url) {
        //    return 0;
        //  }
        //});
        container.addView(view)
        return view!!
    }

    private fun showComment(
        blog: Blog,
        listener: OnTotalListener
    ) {
        val commentDialogFragment = CommentDialogFragment(fragment, blog, listener)
        commentDialogFragment.show(fragment.childFragmentManager, blog.toString())
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        val itemView = `object` as View
        container.removeView(itemView)
        val item = mDatas!![position]
        //取消预加载
        PreloadManager.getInstance(container.context)
            .removePreloadTask(item.url)
        //保存起来用来复用
        mViewPool.add(itemView)
//    val itemView = `object` as View
//    container.removeView(itemView)
//    val (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, url) = mDatas!![position]
//    //取消预加载
//    PreloadManager.getInstance(container.context)
//        .removePreloadTask(url)
//    //保存起来用来复用
//    mViewPool.add(itemView)
    }

    /**
     * 借鉴ListView item复用方法
     */
    class ViewHolder internal constructor(itemView: View?) {
        var mTikTokController: TikTokController
        var mVideoView: VideoView<*>
        var mPosition = 0
        var mThumb //封面图
                : ImageView
        var mRedHeart: RedHeartLayout
        var mVideoItemIcon: ZdImageView
        var mVideoItemFollow: ZdButton
        var mVideoItemPraiseL: LinearLayout
        var mVideoItemPraiseIcon: LikeButton
        var mVideoItemPraiseTxt: TextView
        var mVideoItemCommentL: LinearLayout
        var mVideoItemCommentIcon: ZdImageView
        var mVideoItemCommentTxt: TextView
        var mVideoItemShareL: LinearLayout
        var mVideoItemShareIcon: ZdImageView
        var mVideoItemShareTxt: TextView
        var mVideoItemChannel: ZdButton
        var mVideoItemLocation: TextView
        var mVideoItemName: TextView
        var mVideoItemDes: TextView
        var mVideoItemSound: ImageView
//        var mVideoItemRotate:ZdButton

        init {
//            mVideoItemRotate = itemView!!.findViewById(id.videoItemRotate)
            mVideoItemSound = itemView!!.findViewById(id.videoItemSound)
            mVideoView = itemView!!.findViewById(id.videoItemVideo)
            mRedHeart = itemView.findViewById(id.redHeart)
            mVideoItemIcon = itemView.findViewById(id.videoItemIcon)
            mVideoItemFollow = itemView.findViewById(id.videoItemFollow)
            mVideoItemPraiseL = itemView.findViewById(
                id.videoItemPraiseL
            )
            mVideoItemPraiseIcon = itemView.findViewById(id.videoItemPraiseIcon)
            mVideoItemPraiseTxt =
                itemView.findViewById(id.videoItemPraiseTxt)
            mVideoItemCommentL = itemView.findViewById(
                id.videoItemCommentL
            )
            mVideoItemCommentIcon = itemView.findViewById(id.videoItemCommentIcon)
            mVideoItemCommentTxt = itemView.findViewById(
                id.videoItemCommentTxt
            )
            mVideoItemShareL = itemView.findViewById(
                id.videoItemShareL
            )
            mVideoItemShareIcon = itemView.findViewById(id.videoItemShareIcon)
            mVideoItemShareTxt =
                itemView.findViewById(id.videoItemShareTxt)
            mVideoItemChannel = itemView.findViewById(id.videoItemChannel)
            mVideoItemLocation =
                itemView.findViewById(id.videoItemLocation)
            mVideoItemName =
                itemView.findViewById(id.videoItemName)
            mVideoItemDes =
                itemView.findViewById(id.videoItemDes)
            mVideoView.setLooping(true)
            mVideoView.setScreenScaleType(
                VideoView.SCREEN_SCALE_CENTER_CROP
            )
            mTikTokController = TikTokController(itemView.context)
            mVideoView.setVideoController(mTikTokController)
            mThumb = mTikTokController.findViewById(
                id.videoThumb
            )
            mVideoItemSound.setOnClickListener {
                var isSound = !SPUtil.getBoolean(VIDEO_SOUND, true)
                SPUtil.putBoolean(VIDEO_SOUND, isSound)
                mVideoItemSound.isSelected = isSound
                mVideoView.isMute = isSound
            }
            itemView.tag = this
        }
    }
}

const val VIDEO_SOUND = "videoSound"