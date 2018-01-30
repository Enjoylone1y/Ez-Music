package com.ezreal.huanting.adapter

import android.content.Context
import com.ezreal.huanting.R
import com.ezreal.huanting.bean.RankBillBean

/**
 * 百度音乐榜单列表适配器
 * Created by wudeng on 2018/1/30.
 */
class RankBillAdapter(private val mContext: Context,private val mBillList:List<RankBillBean>)
    :RecycleViewAdapter<RankBillBean>(mContext,mBillList) {

    override fun setItemLayoutId(position: Int): Int {
        return R.layout.item_rank_bill
    }

    override fun bindView(holder: RViewHolder, position: Int) {
        val billBean = mBillList[position]
        holder.setImageByUrl(mContext,R.id.iv_bill_cover,billBean.billCoverUrl!!,
                R.drawable.splash)
        if (billBean.musicFirst != null){
            holder.setText(R.id.tv_first_title,billBean.musicFirst?.title!!)
            holder.setText(R.id.tv_first_artist,billBean.musicFirst?.artist_name!!)
        }

        if (billBean.musicSecond != null){
            holder.setText(R.id.tv_second_title,billBean.musicSecond?.title!!)
            holder.setText(R.id.tv_second_artist,billBean.musicSecond?.artist_name!!)
        }

        if (billBean.musicThird != null){
            holder.setText(R.id.tv_third_title,billBean.musicThird?.title!!)
            holder.setText(R.id.tv_third_artist,billBean.musicThird?.artist_name!!)
        }
    }
}