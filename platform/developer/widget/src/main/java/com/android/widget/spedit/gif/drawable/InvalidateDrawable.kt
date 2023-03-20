package com.android.widget.spedit.gif.drawable

import com.android.widget.spedit.gif.listener.RefreshListener

interface InvalidateDrawable {
    var mRefreshListeners: MutableCollection<RefreshListener>
    fun addRefreshListener(callback: RefreshListener)
    fun removeRefreshListener(callback: RefreshListener)
    fun refresh()
}
