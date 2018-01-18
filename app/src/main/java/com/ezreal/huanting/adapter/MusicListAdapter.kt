package com.ezreal.huanting.adapter

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezreal.huanting.R
import com.ezreal.huanting.bean.MusicListBean

/**
 * 歌单列表适配器
 * Created by wudeng on 2018/1/8.
 */
class MusicListAdapter(private val mContext: Context, private val mList: List<MusicListBean>)
    : RecycleViewAdapter<MusicListBean>(mContext, mList) {

    override fun setItemLayoutId(position: Int): Int {
        return R.layout.item_music_list
    }

    override fun bindView(holder: RViewHolder, position: Int) {
        val bean = mList[position]
        holder.setText(R.id.mTvListTitle, bean.listName!!)
        val size = bean.musicList.size
        holder.setText(R.id.mTvMusicNum, size.toString())
        val cover = holder.getImageView(R.id.mIvListCover)
        when {
            position == 0 -> cover?.setImageResource(R.mipmap.love)
            size > 0 -> {
                val albumUri = bean.musicList[0].albumUri
                Glide.with(mContext)
                        .load(albumUri)
                        .asBitmap()
                        .error(R.mipmap.ic_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).
                        into(cover)
            }
            else -> cover?.setImageResource(R.mipmap.ic_logo)
        }
    }
}