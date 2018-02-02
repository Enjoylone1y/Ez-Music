package com.ezreal.huanting.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.MusicAdapter
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.OnlineMusicHelper
import com.ezreal.huanting.http.baidu.BaiduMusicApi
import com.ezreal.huanting.http.baidu.RecomSearchResult.RecomSongBean
import com.ezreal.huanting.utils.Constant
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_recom_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 推荐歌曲列表 页面
 * Created by wudeng on 2018/1/30.
 */

class RecomListActivity:Activity() {

    private val mMusicList = ArrayList<MusicBean>()
    private lateinit var mAdapter:MusicAdapter
    private var mBaseId:String = "74172066"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recom_list)

        mIvBack.setOnClickListener { finish() }

        mBaseId = intent.getStringExtra("BaseID")
        initView()
        loadRecomList()

        EventBus.getDefault().register(this)
    }

    private fun initView(){
        mRcvRecomList.setPullRefreshEnabled(false)
        mRcvRecomList.layoutManager = LinearLayoutManager(this)
        mAdapter = MusicAdapter(this,Constant.RECOM_MUSIC_LIST_ID,mMusicList)
        mAdapter.setItemClickListener(object :RecycleViewAdapter.OnItemClickListener{
            override fun onItemClick(holder: RViewHolder, position: Int) {
                mAdapter.playMusic(position - 1)
            }
        })
        mRcvRecomList.adapter = mAdapter
    }

    private fun loadRecomList(){
        BaiduMusicApi.searchRecomMusic(mBaseId,
                15, object : BaiduMusicApi.OnRecomSearchListener {
            override fun onResult(code: Int, result: List<RecomSongBean>?, message: String?) {
                if (code == 0 && result != null) {
                    covert2Music(result)
                }
            }
        })
    }

    private fun covert2Music(list: List<RecomSongBean>) {

        var afterSize = 15
        val index = ArrayList<String>()

        val mainRealm = Realm.getDefaultInstance()
        // 从数据库中读取已保存过的数据
        for (bean in list) {
            val music = mainRealm.where(MusicBean::class.java)
                    .equalTo("musicId", bean.song_id.toLong()).findFirst()
            if (music != null) {
                mMusicList.add(music)
            } else {
                index.add(bean.song_id)
            }
        }
        if (index.size == 0){
            mAdapter.notifyChangeWidthStatus()
            return
        }

        // 对于未保存的数据，从网络获取，并存到数据库
        mainRealm.beginTransaction()
        for (id in index) {
            OnlineMusicHelper.loadAndSaveInfo(id, object : OnlineMusicHelper.OnInfoLoadedListener {
                override fun onResult(code: Int, musicBean: MusicBean?, message: String?) {
                    if (code == 0 && musicBean != null) {
                        mMusicList.add(musicBean)
                        mainRealm.insert(musicBean)
                        // 在添加完成后更新数据库，刷新页面
                        if (mMusicList.size == afterSize){
                            mainRealm.commitTransaction()
                            mAdapter.notifyChangeWidthStatus()
                        }
                    }else{
                        afterSize -= 1
                    }
                }
            })
        }
    }

    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        // 恢复前一首播放状态
        val prePlay = mMusicList.firstOrNull { it.playStatus == Constant.PLAY_STATUS_PLAYING }
        if (prePlay != null) {
            val preIndex = mMusicList.indexOf(prePlay)
            prePlay.playStatus = Constant.PLAY_STATUS_NORMAL
            mAdapter.notifyItemChanged(preIndex + 1)
        }

        if (event.newIndex == -1){
            return
        }

        // 更新新播放歌曲状态
        val currentPlay = GlobalMusicData.getCurrentPlay()
        if (currentPlay != null && currentPlay.playFromListId == Constant.RECOM_MUSIC_LIST_ID) {
            val currentIndex = mMusicList.indexOf(currentPlay)
            mMusicList[currentIndex].playStatus = Constant.PLAY_STATUS_PLAYING
            mAdapter.notifyItemChanged(currentIndex + 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}