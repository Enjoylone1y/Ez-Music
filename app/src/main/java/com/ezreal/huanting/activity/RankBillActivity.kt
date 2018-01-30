package com.ezreal.huanting.activity

import android.app.Activity
import android.os.Bundle
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.MusicAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.http.baidu.RankBillSearchResult


/**
 * 网络榜单 页面
 * Created by wudeng on 2018/1/30.
 */
class RankBillActivity :Activity(){

    private var mRankBill:RankBillSearchResult.BillboardBean ?= null

    private val mMusicList = ArrayList<MusicBean>()
    private lateinit var mAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recom_list)
    }
}