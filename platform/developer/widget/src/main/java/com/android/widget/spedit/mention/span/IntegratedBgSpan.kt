package com.android.widget.spedit.mention.span

import android.graphics.Color
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import com.android.utils.LogUtil

interface IntegratedBgSpan : IntegratedSpan {
    var isShow: Boolean
    var bgSpan: BackgroundColorSpan?

    fun removeBg(text: Spannable) {
        isShow = false
        bgSpan?.run {
            text.removeSpan(this)
        }
    }

    fun IntegratedBgSpan.createStoredBgSpan()
            : BackgroundColorSpan {
        bgSpan = generateBgSpan()
        return bgSpan!!
    }

    fun generateBgSpan(): BackgroundColorSpan = BackgroundColorSpan(Color.BLUE)
}
