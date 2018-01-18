package com.ezreal.huanting.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import cn.hotapk.fastandrutils.utils.FScreenUtils
import cn.hotapk.fastandrutils.utils.FStatusBarUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.FragmentAdapter
import com.ezreal.huanting.fragment.AlbumListFragment
import com.ezreal.huanting.fragment.FolderListFragment
import com.ezreal.huanting.fragment.ArtistListFragment
import com.ezreal.huanting.fragment.MusicListFragment
import kotlinx.android.synthetic.main.activity_local_music.*
import kotlin.collections.ArrayList

/**
 *
 * Created by wudeng on 2017/11/17.
 */

class LocalMusicActivity : AppCompatActivity() {

    private var mAdapter:FragmentAdapter ?= null
    private var mFragmentList = ArrayList<Fragment>()
    private val mTitle = listOf("歌曲", "歌手", "专辑", "文件夹")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_music)
        initViewPage()
        initListener()
    }


    private fun initViewPage() {
        mFragmentList.add(MusicListFragment())
        mFragmentList.add(ArtistListFragment())
        mFragmentList.add(AlbumListFragment())
        mFragmentList.add(FolderListFragment())

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
        mIvSearch.setOnClickListener {
            // TODO 跳转到本地音乐搜索页
        }
        mIvMenu.setOnClickListener {
            // TODO 打开菜单弹窗
        }
        mTabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }
        })
    }

}
