package com.android.widget.spedit.view

import android.text.Editable
import android.view.KeyEvent

interface KeyEventProxy {

    fun onKeyEvent(keyEvent: KeyEvent, text: Editable): Boolean
}
