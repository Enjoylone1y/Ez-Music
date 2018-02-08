package com.ezreal.huanting.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.event.SearchResultEvent
import com.ezreal.huanting.http.result.KeywordSearchResult.SongBean
import kotlinx.android.synthetic.main.fragment_music_result.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 搜索结果 -  单曲
 * Created by wudeng on 2018/2/8.
 */

class SongResultFragment :Fragment() {

    private val mSongList = ArrayList<SongBean>()
    private lateinit var mAdapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_music_result,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRcvMusicList.layoutManager = LinearLayoutManager(context)
        mAdapter = SongAdapter(context!!)
        mRcvMusicList.adapter = mAdapter
    }

    @Subscribe
    fun onSearchResult(event: SearchResultEvent){
        mSongList.clear()
        mSongList.addAll(event.result.song)
        mAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    inner class SongAdapter(mContext: Context):
            RecycleViewAdapter<SongBean>(mContext,mSongList){
        override fun setItemLayoutId(position: Int): Int {
            return R.layout.item_song_result
        }

        override fun bindView(holder: RViewHolder, position: Int) {
            val bean = mSongList[position]
            holder.setText(R.id.mTvSongTitle,bean.songname)
            holder.setText(R.id.mTvArtist,bean.artistname)
        }
    }
}