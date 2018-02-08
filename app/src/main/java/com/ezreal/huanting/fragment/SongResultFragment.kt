package com.ezreal.huanting.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.activity.NowPlayingActivity
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.MusicPlayAction
import com.ezreal.huanting.event.PlayActionEvent
import com.ezreal.huanting.event.SearchResultEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.OnlineMusicHelper
import com.ezreal.huanting.http.result.KeywordSearchResult.SongBean
import com.ezreal.huanting.utils.Constant
import io.realm.Realm
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
        mAdapter.setItemClickListener(object :RecycleViewAdapter.OnItemClickListener{
            override fun onItemClick(holder: RViewHolder, position: Int) {
                val id = mSongList[position].songid
                val realm = Realm.getDefaultInstance()
                val findFirst = realm.where(MusicBean::class.java)
                        .equalTo("musicId", id.toLong()).findFirst()
                if (findFirst != null) {
                    playMusic(findFirst)
                }else{
                    OnlineMusicHelper.loadAndSaveInfo(id,object :OnlineMusicHelper.OnInfoLoadedListener{
                        override fun onResult(code: Int, musicBean: MusicBean?, message: String?) {
                            if (code == 0 && musicBean != null){
                                playMusic(musicBean)
                            }else{
                                FToastUtils.init().show("播放出错，请重试~")
                            }
                        }

                    })
                }
            }

        })
        mRcvMusicList.adapter = mAdapter
    }

    private fun playMusic(musicBean: MusicBean){
        GlobalMusicData.addMusic2NextPlay(musicBean,Constant.TEMP_MUSIC_LIST_ID)
        if (GlobalMusicData.getListSize() == 1){
            EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PLAY,-1))
            startActivity(Intent(context,NowPlayingActivity::class.java))
        }else{
            EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.NEXT,-1))
        }
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