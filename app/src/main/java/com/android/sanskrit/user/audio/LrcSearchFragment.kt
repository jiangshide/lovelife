package com.android.sanskrit.user.audio

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.resource.MyFragment
import com.android.resource.audio.AudioPlay
import com.android.resource.vm.blog.data.Blog
import com.android.resource.vm.blog.data.Lrc
import com.android.sanskrit.R
import com.android.tablayout.indicators.LinePagerIndicator
import com.android.utils.LogUtil
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.user_audio_lrc_search_fragment.*

/**
 * created by jiangshide on 2020/5/29.
 * email:18311271399@163.com
 */
class LrcSearchFragment : MyFragment() {

  private var name: String? = ""
  private var author = ""
  private var language = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.user_audio_lrc_search_fragment)
  }

  override fun onPause() {
    super.onPause()
    showFloatMenu(false)
  }

  override fun onResume() {
    super.onResume()
    showFloatMenu(false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    val blog: Blog = arguments?.getParcelable("data")!!
    LogUtil.e("----blog:", blog)
    name = blog.name
    songNameEdit.setText(name)
    songNameEdit.setListener { s, input ->
      this.name = input
      validate()
    }
    songAuthorEdit.setListener { s, input ->
      this.author = input
      validate()
    }

    songTypeGroup.setOnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        R.id.songTypeGroupZh -> {
          language = 0
        }
        R.id.songTypeGrouEs -> {
          language = 1
        }
      }
    }

    songSearchBtn.setOnClickListener {
      blogVM?.searchLrc(blog.uid, name!!, author, language)
      showLoading()
    }

    ZdEvent.get()
        .with(LRC_SELECTED, Lrc::class.java)
        .observes(this, Observer { lrc ->
          blog.lrcZh = lrc.lrc
          setRight("完成").setRightListener {
            blogVM?.lrcUpdate(blog.id,name, lrc.lrc)
          }
        })

    blogVM!!.lrcUpdate.observe(this, Observer {
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      AudioPlay.getInstance()
          .update(blog)
      ZdEvent.get()
          .with(LRC_REFRESH)
          .post(blog)
      pop()
    })

    blogVM!!.searchLrc.observe(this, Observer {
      hiddle()
      LogUtil.e("err:", it.error, " | data:", it.data)
      if (it.error != null) {
        songSearchResult.text = "搜索结果为0"
        return@Observer
      }

      songSearchResult.text = "搜索结果为${it.data.count}"
      if (it?.data?.result?.size!! > 0) {
        showLrcManager(it.data.result!!)
      }
    })
    blogVM?.searchLrc(blog.uid, name!!, author, language)
    showLoading()
    validate()
  }

  private fun showLrcManager(data: MutableList<Lrc>) {

    val fragments = ArrayList<LrcFragment>()
    val titles = ArrayList<String>()

    data.forEach { lrc ->
      titles.add(".")
      fragments.add(LrcFragment(lrc))
    }

    lrcViewPager.adapter = lrcViewPager.create(childFragmentManager)
        .setTitles(
            titles
        )
        .setFragment(
            fragments
        )
        .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
        .setLinePagerIndicator(getColor(R.color.blue))
        .initTabs(activity!!, tabsLrc, lrcViewPager)
  }

  private fun validate() {
    val disable = TextUtils.isEmpty(name)
    songSearchBtn.setTextColor(getColor(if (disable) R.color.fontLight else R.color.font))
    songSearchBtn.isEnabled = !disable
  }
}

const val LRC_SELECTED = "lrcSelected"