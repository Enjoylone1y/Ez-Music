package com.ezreal.huanting.widget

import android.content.Context
import android.util.AttributeSet

/**
 * 为支持在文字显示不全自动左右滚动（跑马灯）效果
 * 强制将 TextView 的 Focused 设置为 true
 * Created by wudeng on 2017/12/1.
 */

class MarqueeTextView : android.support.v7.widget.AppCompatTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun isFocused(): Boolean {
        return true
    }
}
