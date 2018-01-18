package com.ezreal.huanting.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.helper.GlobalMusicList
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import java.util.ArrayList

/**
 * 当前播放列表弹窗
 * Created by wudeng on 2017/12/4.
 */
class NowPlayListWindow(context: Context) : PopupWindow(context) {

    private var mLayoutSort: LinearLayout? = null
    private var mIvSortMode: ImageView? = null
    private var mTvSortMode: TextView? = null
    private var mIvDelete: ImageView? = null
    private var mRecyclerView: RecyclerView? = null
    private var mSongList = ArrayList<MusicBean>()
    private var mAdapter: RecycleViewAdapter<MusicBean>? = null

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.popup_now_play_list,
                null, true)
        this.contentView = root
        this.height = context.resources.displayMetrics.heightPixels / 2
        mLayoutSort = root.findViewById(R.id.layout_sort) as LinearLayout?
        mIvSortMode = root.findViewById(R.id.iv_list_sort) as ImageView?
        mTvSortMode = root.findViewById(R.id.tv_list_sort) as TextView?
        mIvDelete = root.findViewById(R.id.iv_delete) as ImageView?
        mRecyclerView = root.findViewById(R.id.rcv_playing_list) as RecyclerView?

        initList(context)
        initEvent()
    }

    private fun initList(context: Context) {
        // 播放列表初始化
        mRecyclerView?.layoutManager = LinearLayoutManager(context)
        mRecyclerView?.addItemDecoration(
                RecyclerViewDivider
                        .with(context)
                        .color(R.color.color_light_gray)
                        .size(1)
                        .build())
        mAdapter = object : RecycleViewAdapter<MusicBean>(context, mSongList) {
            override fun setItemLayoutId(position: Int): Int = R.layout.item_music_in_popu

            override fun bindView(holder: RViewHolder, position: Int) {
                val item = mSongList[position]
                holder.setVisible(R.id.iv_playing, false)
                if (position == GlobalMusicList.getCurrentIndex()) {
                    holder.setVisible(R.id.iv_playing, true)
                }
                holder.setText(R.id.mTvSongTitle, item.musicTitle!!)
                holder.setText(R.id.mTvArtist, item.artist!!)
                val  delete = holder.convertView.findViewById<ImageView>(R.id.iv_delete)
                delete.setOnClickListener {
                    // TODO 将自己从播放列表中移除
                }
            }
        }
        mAdapter?.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(holder: RViewHolder, position: Int) {
                // TODO 若为当前播放，则打开播放页面，否则播放点击歌曲
            }

        })
        mRecyclerView?.adapter = mAdapter
    }


    private fun initEvent() {
        mLayoutSort?.setOnClickListener {
            // TODO 改变播放列表排序
        }

        mIvDelete?.setOnClickListener {
            // TODO 弹窗提示是否删除当前播放列表
        }
    }

}