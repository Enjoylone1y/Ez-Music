package com.ezreal.huanting.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.MusicAdapter
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.MusicDataHelper
import com.ezreal.huanting.utils.Constant
import kotlinx.android.synthetic.main.activity_recnet_play.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

/**
 * 最近播放列表
 * Created by wudeng on 2018/1/3.
 */
class RecentPlayActivity : AppCompatActivity() {

    private val mMusicList = ArrayList<MusicBean>()
    private var mAdapter:MusicAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recnet_play)
        initView()
        initListener()
        loadSongList()
        EventBus.getDefault().register(this)
    }

    private fun initView() {
        mRvRecentPlay.layoutManager = LinearLayoutManager(this)
        mRvRecentPlay.setLoadingMoreEnabled(false)
        mRvRecentPlay.setPullRefreshEnabled(false)
    }

    private fun initListener() {
        mIvBack.setOnClickListener {
            this.finish()
        }

        mTvClear.setOnClickListener {
            AlertDialog.Builder(this, R.style.MyAlertDialog)
                    .setTitle("清空最近播放列表")
                    .setCancelable(true)
                    .setNegativeButton("取消", { dialog, _ -> dialog.dismiss() })
                    .setPositiveButton("清空", { dialog, _ ->
                        mMusicList.clear()
                        mAdapter?.notifyDataSetChanged()
                        MusicDataHelper.clearRecentPlay()
                        dialog.dismiss()
                    })
                    .show()
        }
    }

    private fun loadSongList() {
        MusicDataHelper.loadRecentPlayFromDB(object : MusicDataHelper.OnMusicLoadListener {
            override fun loadSuccess(musicList: List<MusicBean>) {
                mMusicList.addAll(musicList)
                mAdapter = MusicAdapter(this@RecentPlayActivity,
                        Constant.RECENT_MUSIC_LIST_ID ,mMusicList)
                mAdapter?.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener{
                    override fun onItemClick(holder: RViewHolder, position: Int) {
                        mAdapter?.checkPlaySong(position-1,position)
                    }
                })
                mRvRecentPlay.adapter = mAdapter
            }

            override fun loadFailed(message: String) {
                FToastUtils.init().show("加载出错：" + message)
            }
        })
    }

    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent){
        // 恢复前一首播放状态
        val prePlay = mMusicList.firstOrNull { it.playStatus == Constant.PLAY_STATUS_PLAYING }
        if (prePlay != null){
            val preIndex = mMusicList.indexOf(prePlay)
            prePlay.playStatus = Constant.PLAY_STATUS_NORMAL
            mAdapter?.notifyItemChanged(preIndex + 1)
        }
        // 更新新播放歌曲状态
        val currentPlay = GlobalMusicData.getCurrentPlay()
        if (currentPlay != null && currentPlay.playFromListId == Constant.RECENT_MUSIC_LIST_ID){
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