package com.ezreal.huanting.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezreal.huanting.R
import com.jude.rollviewpager.RollPagerView
import com.jude.rollviewpager.adapter.LoopPagerAdapter

/**
 * 自动轮播 viewPage 适配器
 * Created by wudeng on 2018/2/5.
 */

class AutoPageAdapter(private val urls:List<String>,viewPage:RollPagerView)
    :LoopPagerAdapter(viewPage) {

    override fun getView(container: ViewGroup?, position: Int): View {
        val imageView = ImageView(container?.context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        Glide.with(container?.context).load(urls[position])
                .asBitmap()
                .error(R.drawable.main_menu_head)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        return imageView
    }

    override fun getRealCount(): Int = urls.size
}