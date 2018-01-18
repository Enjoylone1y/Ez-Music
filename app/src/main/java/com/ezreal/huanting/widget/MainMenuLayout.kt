package com.ezreal.huanting.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.ezreal.huanting.R

/**
 * 主菜单
 * Created by wudeng on 2018/1/18.
 */
class MainMenuLayout: LinearLayout{

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr){
        LayoutInflater.from(context).inflate(R.layout.layout_main_menu,
                this, true)
    }
}