package com.ezreal.huanting.adapter

import android.content.Context
import com.ezreal.huanting.R
import com.ezreal.huanting.http.baidu.RankBillSearchResult.BillSongBean

/**
 * Created by wudeng on 2018/2/1.
 */

class OnlineItemAdapter(private val mContext: Context,private val mList:List<BillSongBean>):
        RecycleViewAdapter<BillSongBean>(mContext, mList) {
    override fun setItemLayoutId(position: Int): Int {
        return R.layout.item_online_music
    }

    override fun bindView(holder: RViewHolder, position: Int) {
        val bean = mList[position]
        holder.setImageByUrl(mContext, R.id.iv_big_pic, bean.pic_big, R.drawable.splash)
        holder.setText(R.id.tv_music_title, bean.title)
        holder.setText(R.id.tv_artist, bean.artist_name)
    }

}