package com.ezreal.huanting.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.FragmentAdapter
import com.ezreal.huanting.fragment.DownDoneFragment
import com.ezreal.huanting.fragment.DownIngFragment
import kotlinx.android.synthetic.main.activity_down_manager.*

/**
 * 下载管理页
 * Created by wudeng on 2018/2/2.
 */

class DownManagerActivity : BaseActivity(){

    private var mAdapter: FragmentAdapter?= null
    private var mFragmentList = ArrayList<Fragment>()
    private val mTitle = listOf("已下载", "下载中")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_down_manager)
        initViewPage()
        initListener()
    }


    private fun initViewPage() {
        mFragmentList.add(DownDoneFragment())
        mFragmentList.add(DownIngFragment())

        mAdapter = FragmentAdapter(supportFragmentManager, mFragmentList, mTitle)
        mViewPage.adapter = mAdapter
        mTabLayout.setupWithViewPager(mViewPage,true)
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this,
                R.color.colorPrimary))
    }

    /**
     * 标题栏点击事件
     */
    private fun initListener() {
        mIvBack.setOnClickListener {
            finish()
        }

        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }
        })
    }

}