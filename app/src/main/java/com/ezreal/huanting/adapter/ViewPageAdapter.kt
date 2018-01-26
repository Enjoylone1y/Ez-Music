package com.ezreal.huanting.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * Created by wudeng on 2017/11/6.
 */

class ViewPageAdapter(private val mViews: List<View>?) : PagerAdapter() {

    override fun getCount(): Int {
        return mViews?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(mViews!![position])
        return mViews[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(mViews!![position])
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}
