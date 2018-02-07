package com.ezreal.huanting.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

/**
 * 持续获取焦点的TextView
 * Created by wudeng on 2018/2/7.
 */
class FocusedTextView : TextView {

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun isFocused(): Boolean = true
}