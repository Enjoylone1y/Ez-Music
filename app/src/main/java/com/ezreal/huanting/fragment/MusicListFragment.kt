package com.ezreal.huanting.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.LocalMusicAdapter
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.MusicDataHelper
import com.ezreal.huanting.utils.Constant
import kotlinx.android.synthetic.main.fragment_song_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
 * 本地音乐单曲列表
 * Created by wudeng on 2017/11/16.
 */
class MusicListFragment : Fragment() {

    private val mMusicList = ArrayList<MusicBean>()
    private var mAdapter: LocalMusicAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

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
        MusicDataHelper.loadMusicFromDB(object : MusicDataHelper.OnMusicLoadListener {
            override fun loadSuccess(musicList: List<MusicBean>) {
                musicLoaded(musicList)
            }

            override fun loadFailed(message: String) {
                FToastUtils.init().show("加载失败:" + message)
            }
        })
    }

    private fun musicLoaded(musicList: List<MusicBean>){
        mMusicList.addAll(musicList)
        mAdapter = LocalMusicAdapter(context!!, Constant.LOCAL_MUSIC_LIST_ID, mMusicList)
        mAdapter?.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(holder: RViewHolder, position: Int) {
                mAdapter?.checkPlaySong(position - 1, position)
            }
        })
        mRecyclerView.adapter = mAdapter
    }

    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        // 恢复前一首播放状态
        val prePlay = mMusicList.firstOrNull { it.playStatus == Constant.PLAY_STATUS_PLAYING }
        if (prePlay != null) {
            val preIndex = mMusicList.indexOf(prePlay)
            prePlay.playStatus = Constant.PLAY_STATUS_NORMAL
            mAdapter?.notifyItemChanged(preIndex + 1)
        }
        // 更新新播放歌曲状态
        val currentPlay = GlobalMusicData.getCurrentPlay()
        if (currentPlay != null && currentPlay.playFromListId == Constant.LOCAL_MUSIC_LIST_ID) {
            val currentIndex = mMusicList.indexOf(currentPlay)
            mMusicList[currentIndex].playStatus = Constant.PLAY_STATUS_PLAYING
            mAdapter?.notifyItemChanged(currentIndex + 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
