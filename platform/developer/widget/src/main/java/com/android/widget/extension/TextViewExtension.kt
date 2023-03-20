package com.android.widget.extension

import android.widget.TextView

/**
 * Extension method to set a drawable to the left of a TextView.
 */
fun TextView.setDrawableLeft(drawable: Int) {
  this.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
}

/**
 * Extension method to set a drawable to the right of a TextView.
 */
fun TextView.setDrawableTop(drawable: Int) {
  this.setCompoundDrawablesWithIntrinsicBounds(0, drawable, 0, 0)
}

/**
 * Extension method to set a drawable to the right of a TextView.
 */
fun TextView.setDrawableRight(drawable: Int) {
  this.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
}

/**
 * Extension method to set a drawable to the right of a TextView.
 */
fun TextView.setDrawableBottom(drawable: Int) {
  this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, drawable)
}
