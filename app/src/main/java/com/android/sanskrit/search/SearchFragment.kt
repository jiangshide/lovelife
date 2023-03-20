package com.android.sanskrit.search

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.android.player.dplay.player.VideoViewManager
import com.android.resource.MyFragment
import com.android.resource.Resource
import com.android.resource.vm.blog.OnWordListener
import com.android.resource.vm.channel.data.Word
import com.android.sanskrit.R
import com.android.tablayout.indicators.LinePagerIndicator
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.search.*
import kotlinx.android.synthetic.main.search_item.view.*
import java.util.*

/**
 * created by jiangshide on 2020/7/15.
 * email:18311271399@163.com
 */
class SearchFragment(private val word: Word? = null) : MyFragment() {

    private var history: MutableList<Word>? = null
    private var guess: MutableList<Word>? = null

    private var searchUserFragment: SearchUserFragment? = null
    private var searchChannelFragment: SearchChannelFragment? = null
    private var searchBlogFragment: SearchBlogFragment? = null
    private var searchAudioFragment: SearchAudioFragment? = null
    private var searchVideoFragment: SearchVideoFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setView(R.layout.search)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setTopBar(searchRootL)
        searchCancel.setOnClickListener {
            onBack()
        }
        searchEdit.setListener { s, input ->
            if (!TextUtils.isEmpty(input)) {
                blogVM?.banner(name = input, listener = object : OnWordListener {
                    override fun onWords(
                        data: MutableList<Word>?,
                        err: Exception?
                    ) {
                        if (data != null) {
                            showList(input, data)
                        }
                    }
                })
            } else {
                showList("", arrayListOf())
            }
        }
        searchEdit.setOnEditorActionListener { textView, i, keyEvent ->
            val content = searchEdit?.text.toString()
            if (i == EditorInfo.IME_ACTION_SEARCH && !TextUtils.isEmpty(content)) {
                searchEdit?.hide()
                val word = Word(name = content)
                update(word)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        showPagers()
        if (word != null) {
            update(word)
            searchEdit?.hint = word.name
        }

        blogVM?.banner(listener = object : OnWordListener {
            override fun onWords(
                data: MutableList<Word>?,
                err: Exception?
            ) {
                if (data != null) {
                    guess = data
                    val list = arrayListOf<String>()
                    guess?.forEach {
                        list.add(it.name!!)
                    }
                    showGuessTag(list)
                }
            }
        })
        channelVM?.word(listener = object : OnWordListener {
            override fun onWords(
                data: MutableList<Word>?,
                err: Exception?
            ) {
                if (data != null) {
                    history = data
                    val list = arrayListOf<String>()
                    history?.forEach {
                        list.add(it.name!!)
                    }
                    showHistoryTag(list)
                }
            }
        })
    }

    private fun onBack(): Boolean {
        if (searchL.visibility == View.VISIBLE) {
            searchL.visibility = View.GONE
            searchRecycleView.visibility = View.GONE
            searchTagL.visibility = View.VISIBLE
            return false
        } else {
            pop()
        }
        return true
    }

    private fun showHistoryTag(datas: ArrayList<String>?) {
        searchHistoryTag.setRandomColor(true)
            .setDatas(datas)
            .setListener { zdButton, position, data ->
//          searchEdit?.hide()
                update(history!![position])
            }
    }

    private fun showGuessTag(datas: ArrayList<String>) {
        searchGuessTag.setRandomColor(true)
            .setDatas(datas)
            .setListener { zdButton, position, data ->
//          searchEdit?.hide()
                update(guess!![position])
            }

    }

    private fun showList(
        input: String,
        data: MutableList<Word>
    ) {
        searchRecycleView.create(data, R.layout.search_item, {
            val name = it.name
            searchItem.text = name
            var index = name?.indexOf(input)
            val inputLength = input.length
            val spannableStringBuilder =
                SpannableStringBuilder()
            spannableStringBuilder.append(name)
            val userColor =
                ForegroundColorSpan(randomColor())
            if (index == -1) {
                index = 0
            }
            spannableStringBuilder.setSpan(
                userColor, index!!, index + inputLength,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            searchItem.movementMethod = LinkMovementMethod.getInstance()
            searchItem.text = spannableStringBuilder
        }, {
            searchEdit?.hide()
            update(this)
        })
        if (data != null && data.size > 0) {
            searchRecycleView.visibility = View.VISIBLE
            searchL.visibility = View.GONE
            searchTagL.visibility = View.GONE
        } else {
            searchRecycleView.visibility = View.GONE
            searchL.visibility = View.GONE
            searchTagL.visibility = View.VISIBLE
        }
    }

    /**
     * 设置随机颜色
     */
    fun randomColor(): Int {
        val random = Random()
        return -0x1000000 or random.nextInt(0x00ffffff)
    }

    override fun onBackPressed(): Boolean {
        return onBack()
    }

    private fun showPagers() {
        searchUserFragment = SearchUserFragment()
        searchChannelFragment = SearchChannelFragment()
        searchBlogFragment = SearchBlogFragment()
        searchAudioFragment = SearchAudioFragment()
        searchVideoFragment = SearchVideoFragment()
        searchViewPager.adapter = searchViewPager.create(childFragmentManager)
            .setTitles(
                "用户", "频道", "动态", "音乐", "视频"
            )
            .setFragment(
                searchUserFragment,
                searchChannelFragment,
                searchBlogFragment,
                searchAudioFragment,
                searchVideoFragment
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setLinePagerIndicator(getColor(R.color.blue))
            .setPersistent(true)
            .initTabs(activity!!, tabsSearch, searchViewPager, true)
    }

    private fun update(word: Word?) {
        val name: String = word?.name!!
        when (word?.source) {
            1 -> {
                searchViewPager.currentItem = 0
            }
            2 -> {
                searchViewPager.currentItem = 1
            }
            3 -> {
                searchViewPager.currentItem = 2
            }
            else -> {
                searchViewPager.currentItem = 0
            }
        }
        searchUserFragment?.updateData(name)
        searchChannelFragment?.updateData(name)
        searchBlogFragment?.updateData(name)
        searchAudioFragment?.updateData(name)
        searchVideoFragment?.updateData(name)
        searchL.visibility = View.VISIBLE
        searchRecycleView.visibility = View.GONE
        searchTagL.visibility = View.GONE
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