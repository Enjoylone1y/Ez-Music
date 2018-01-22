package com.ezreal.huanting.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.MusicAdapter
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.helper.MusicDataHelper
import com.ezreal.huanting.utils.Constant
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import kotlinx.android.synthetic.main.fragment_song_list.*
import java.util.*

/**
 * 本地音乐单曲列表
 * Created by wudeng on 2017/11/16.
 */
class MusicListFragment : Fragment() {

    private var mSongList = ArrayList<MusicBean>()
    private var mAdapter: MusicAdapter ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.setLoadingMoreEnabled(false)
        mRecyclerView.setPullRefreshEnabled(false)
        loadSongList()
    }

    private fun loadSongList() {
        MusicDataHelper.loadLocalMusic(context!!, false,
                object : MusicDataHelper.OnMusicLoadListener {
                    override fun loadSuccess(musicList: List<MusicBean>) {
                        for (music in musicList){
                            music.playFromList = Constant.LOCAL_MUSIC_LIST_ID
                        }
                        mSongList.addAll(musicList)
                        mAdapter = MusicAdapter(context!!,Constant.LOCAL_MUSIC_LIST_ID,mSongList)
                        mAdapter?.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener{
                            override fun onItemClick(holder: RViewHolder, position: Int) {
                                mAdapter?.checkPlaySong(position-1,position)
                            }
                        })
                        mRecyclerView.adapter = mAdapter
                    }

                    override fun loadFailed(message: String) {
                        FToastUtils.init().show("歌曲加载失败:" + message)
                    }
                })
    }
}
