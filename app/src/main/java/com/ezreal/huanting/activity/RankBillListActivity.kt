package com.ezreal.huanting.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RankBillAdapter
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.RankBillBean
import com.ezreal.huanting.http.baidu.BaiduMusicApi
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import kotlinx.android.synthetic.main.activity_bill_list.*


/**
 * 榜单列表页
 * Created by wudeng on 2018/1/30.
 */
class RankBillListActivity : Activity() {

    private val mRankBillList = ArrayList<RankBillBean>()
    private lateinit var mBillAdapter: RecycleViewAdapter<RankBillBean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_list)
        init()
        loadData()
    }

    private fun init() {
        mRcvBillList.layoutManager = LinearLayoutManager(this)
        mRcvBillList.addItemDecoration(
                RecyclerViewDivider.with(this).color(android.R.color.white).size(5).build())
        mBillAdapter = RankBillAdapter(this, mRankBillList)
        mRcvBillList.adapter = mBillAdapter

        // 打开对应的榜单
        mBillAdapter.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(holder: RViewHolder, position: Int) {
                val intent = Intent(this@RankBillListActivity, RankBillActivity::class.java)
                intent.putExtra("BillID", mRankBillList[position].billType.toLong())
                startActivity(intent)
            }

        })

        mIvBack.setOnClickListener {
            finish()
        }
    }

    private fun loadData() {
        // 获取歌曲榜单
        val types = listOf(6, 8, 11, 20, 21, 22, 24, 25)
        BaiduMusicApi.searchBillList(types, 3, 0, object :
                BaiduMusicApi.OnBillListSearchListener {
            override fun onResult(code: Int, result: List<RankBillBean>?, message: String?) {
                if (code == 0 && result != null) {
                    mRankBillList.addAll(result)
                    mRankBillList.sortBy { it.billType }
                    mBillAdapter.notifyDataSetChanged()
                }
            }

        })
    }

}