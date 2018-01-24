package com.ezreal.huanting.utils

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

class LrcEntry (val time: Long, private val text: String) : Comparable<LrcEntry> {
    var staticLayout: StaticLayout? = null
        private set
    var offset = java.lang.Float.MIN_VALUE

    val height: Int
        get() = if (staticLayout == null) {
            0
        } else staticLayout!!.height

    fun init(paint: TextPaint, width: Int) {
        staticLayout = StaticLayout(text, paint, width,
                Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
    }

    override fun compareTo(other: LrcEntry): Int {
        return (time - other.time).toInt()
    }


}

