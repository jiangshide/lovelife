package com.android.widget.spedit.emoji

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.android.widget.spedit.emoji.Emoji

open class EmojiconMenuBase : LinearLayout {

    var listener: ((emoji: Emoji) -> Unit)? = null

    constructor(context: Context) : super(context)

    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


}
