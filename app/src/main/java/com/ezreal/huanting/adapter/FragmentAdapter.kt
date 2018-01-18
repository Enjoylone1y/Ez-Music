package com.ezreal.huanting.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by wudeng on 2017/11/16.
 */

class FragmentAdapter(fm: FragmentManager, fragmentList: List<Fragment>, titleList: List<String>) :
        FragmentPagerAdapter(fm) {

    private var mFragmentList:List<Fragment> ?= fragmentList
    private var mTitleList:List<String> ?= titleList

    override fun getItem(position: Int): Fragment = mFragmentList!![position]

    override fun getCount(): Int = mFragmentList?.size ?: 0

    override fun getPageTitle(position: Int): String? = mTitleList?.get(position)
}
