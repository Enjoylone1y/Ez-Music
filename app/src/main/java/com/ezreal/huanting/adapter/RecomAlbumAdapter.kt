package com.ezreal.huanting.adapter

import android.content.Context
import com.ezreal.huanting.R
import com.ezreal.huanting.http.result.RecomAlbumResult.Plaze.RMBean.Album.RecomAlbumBean

/**
 * 推荐专辑列表适配器
 * Created by wudeng on 2018/2/5.
 */

class RecomAlbumAdapter(private val mContext: Context,private val mList:ArrayList<RecomAlbumBean>):
    RecycleViewAdapter<RecomAlbumBean>(mContext,mList){
    override fun setItemLayoutId(position: Int): Int {
        return R.layout.item_recom_album
    }

    override fun bindView(holder: RViewHolder, position: Int) {
        val bean = mList[position]
        holder.setText(R.id.tv_album_title,bean.title)
        holder.setText(R.id.tv_album_author,bean.author)
        holder.setText(R.id.tv_song_num,bean.songs_total)
        holder.setText(R.id.tv_publish_time,bean.publishtime)
        holder.setImageByUrl(mContext,R.id.iv_album_cover,bean.pic_big,R.drawable.splash)
    }
}