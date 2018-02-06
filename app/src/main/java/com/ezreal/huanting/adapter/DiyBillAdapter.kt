package com.ezreal.huanting.adapter

import android.content.Context
import com.ezreal.huanting.R
import com.ezreal.huanting.http.result.HotBillResult.ContentBean.ListBean

/**
 * （个人创建）歌单列表 适配器
 * Created by wudeng on 2018/2/5.
 */

class DiyBillAdapter(private val mContext:Context,private val mList: ArrayList<ListBean>):
        RecycleViewAdapter<ListBean>(mContext,mList){
    override fun setItemLayoutId(position: Int): Int {
        return R.layout.item_diy_bill
    }

    override fun bindView(holder: RViewHolder, position: Int) {
        val bean = mList[position]
        holder.setText(R.id.tv_bill_title,bean.title)
        holder.setText(R.id.tv_listen_count,getNumString(bean.listenum.toLong()))
        holder.setImageByUrl(mContext,R.id.iv_bill_cover,bean.pic,R.drawable.splash)
    }

    private fun getNumString(num:Long):String{
        return if (num > 10000) (num / 10000).toString() + "万" else num.toString()
    }
}