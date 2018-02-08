package com.ezreal.huanting.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.FragmentAdapter
import com.ezreal.huanting.event.SearchResultEvent
import com.ezreal.huanting.fragment.AlbumResultFragment
import com.ezreal.huanting.fragment.ArtistResultFragment
import com.ezreal.huanting.fragment.SongResultFragment
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.result.KeywordSearchResult
import kotlinx.android.synthetic.main.activity_global_search.*
import org.greenrobot.eventbus.EventBus

/**
 * 全局搜索页面
 * Created by wudeng on 2018/2/7.
 */

class GlobalSearchActivity:BaseActivity() {

    private lateinit var mAdapter: FragmentAdapter
    private var mFragmentList = ArrayList<Fragment>()
    private val mTitle = listOf("单曲", "歌手", "专辑")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_search)
        initViewPage()
        initEvent()
    }

    private fun initViewPage(){
        mFragmentList.add(SongResultFragment())
        mFragmentList.add(ArtistResultFragment())
        mFragmentList.add(AlbumResultFragment())
        mAdapter = FragmentAdapter(supportFragmentManager,mFragmentList,mTitle)
        mViewPage.adapter = mAdapter
        mTabLayout.setupWithViewPager(mViewPage)
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this,
                R.color.colorPrimary))
    }

    private fun initEvent(){
        mIvBack.setOnClickListener { finish() }
        mIvSearch.setOnClickListener {
            val key = mEtKeyword.text.toString()
            if (TextUtils.isEmpty(key)){
                FToastUtils.init().show("请输入搜索关键词~")
                return@setOnClickListener
            }
            search(key)
        }
    }

    private fun search(key:String){
        BaiduMusicApi.searchMusicByKey(key,object :BaiduMusicApi.OnKeywordSearchListener{
            override fun onResult(code: Int, result: KeywordSearchResult?, message: String?) {
                if (code == 0 && result != null){
                     EventBus.getDefault().post(SearchResultEvent(result))
                }else{
                    FToastUtils.init().show("搜索出错或无结果~")
                }
            }
        })
    }
}