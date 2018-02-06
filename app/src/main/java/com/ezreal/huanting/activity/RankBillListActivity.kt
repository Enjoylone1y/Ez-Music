package com.ezreal.huanting.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.result.RankBillListResult
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import kotlinx.android.synthetic.main.activity_bill_list.*


/**
 * 榜单列表页
 * Created by wudeng on 2018/1/30.
 */
class RankBillListActivity : BaseActivity() {

    private val mRankBillList = ArrayList<RankBillListResult.RankBillBean>()
    private lateinit var mBillAdapter: RankBillListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_list)
        init()
        loadData()
    }

    private fun init() {
        mRcvBillList.layoutManager = LinearLayoutManager(this)
        mRcvBillList.addItemDecoration(
                RecyclerViewDivider.with(this)
                        .color(android.R.color.white).size(5).build())
        mBillAdapter = RankBillListAdapter()
        mRcvBillList.adapter = mBillAdapter

        // 打开对应的榜单
        mBillAdapter.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(holder: RViewHolder, position: Int) {
                val intent = Intent(this@RankBillListActivity,
                        RankBillActivity::class.java)
                intent.putExtra("BillID", mRankBillList[position].type.toLong())
                startActivity(intent)
            }
        })

        mIvBack.setOnClickListener {
            finish()
        }
    }

    private fun loadData(){
        BaiduMusicApi.loadRankBillList(object :BaiduMusicApi.OnRankBillListListener{
            override fun onResult(code: Int, result: List<RankBillListResult.RankBillBean>?,
                                  message: String?) {
                if (code == 0 && result != null){
                    result.filterTo(mRankBillList) { it.type < 100 }
                    mBillAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    inner class RankBillListAdapter :
            RecycleViewAdapter<RankBillListResult.RankBillBean>(this,mRankBillList){
        override fun setItemLayoutId(position: Int): Int {
            return R.layout.item_rank_bill
        }

        override fun bindView(holder: RViewHolder, position: Int) {
            val billBean = mRankBillList[position]
            holder.setImageByUrl(this@RankBillListActivity,
                    R.id.iv_bill_cover,billBean.pic,R.drawable.splash)
            val first = billBean.content[0]
            val second = billBean.content[1]
            val third = billBean.content[2]
            holder.setText(R.id.tv_first_title,first.title)
            holder.setText(R.id.tv_first_artist,first.author)
            holder.setText(R.id.tv_second_title,second.title)
            holder.setText(R.id.tv_second_artist,second.author)
            holder.setText(R.id.tv_third_title,third.title)
            holder.setText(R.id.tv_third_artist,third.author)
        }
    }
}