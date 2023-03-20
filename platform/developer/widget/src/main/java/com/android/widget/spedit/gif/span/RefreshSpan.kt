package com.android.widget.spedit.gif.span

import com.android.widget.spedit.gif.drawable.InvalidateDrawable

interface RefreshSpan {

    fun getInvalidateDrawable(): InvalidateDrawable?
}
