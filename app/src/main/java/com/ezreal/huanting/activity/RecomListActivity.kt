package com.ezreal.huanting.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.widget.TextView
import cn.hotapk.fastandrutils.utils.FSharedPrefsUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.MusicAdapter
import com.ezreal.huanting.bean.GedanBean
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.OnlineMusicHelper
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.result.RecomSearchResult
import com.ezreal.huanting.utils.Constant
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_recom_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*

/**
 * 推荐歌曲列表 页面
 * Created by wudeng on 2018/1/30.
 */

class RecomListActivity : BaseActivity() {

    private val mMusicList = ArrayList<MusicBean>()
    private lateinit var mAdapter:MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recom_list)

        mIvBack.setOnClickListener { finish() }

        initView()
        checkAndLoadRecom()

        EventBus.getDefault().register(this)
    }

    private fun initView(){

        val headView = LayoutInflater.from(this).inflate(R.layout.layout_recom_head,
                null, false)
        val dayText = headView.findViewById<TextView>(R.id.mTvRecomDay)
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd")
        dayText.text = format.format(calendar.time)
        mRcvRecomList.addHeaderView(headView)

        mRcvRecomList.setPullRefreshEnabled(false)
        mRcvRecomList.setLoadingMoreEnabled(false)
        mRcvRecomList.layoutManager = LinearLayoutManager(this)
        mAdapter = MusicAdapter(this,Constant.RECOM_MUSIC_LIST_ID,mMusicList)
        mRcvRecomList.adapter = mAdapter
    }


    private fun checkAndLoadRecom(){
        val oneDay = 24 * 60 * 60 * 1000
        val time = FSharedPrefsUtils.getLong(Constant.PRE_APP_OPTION_TABLE,
                Constant.PRE_LOAD_RECOM_TIME, 0)
        val current = System.currentTimeMillis()
        if ((current - time) >= oneDay ){
            loadRecomOnline()
        }else {
            loadRecomLocal()
        }
    }

    private fun loadRecomOnline(){
        val baseId = OnlineMusicHelper.getRecomBaseId()
        BaiduMusicApi.searchRecomById(baseId,20,object :
                BaiduMusicApi.OnRecomSearchListener{
            override fun onResult(code: Int, result: List<RecomSearchResult.RecomSongBean>?,
                                  message: String?) {
                if (code == 0 && result != null){
                    covert2Music(result)
                    updateLoadTime()
                }
            }
        })
    }

    private fun loadRecomLocal(){
        val mainRealm = Realm.getDefaultInstance()
        val gedanBean = mainRealm.where(GedanBean::class.java).equalTo("listId",
                Constant.RECOM_MUSIC_LIST_ID).findFirst()
        if (gedanBean.musicList.isEmpty()){
            loadRecomOnline()
        }else{
            mMusicList += gedanBean.musicList
            mAdapter.notifyChangeWidthStatus()
        }
    }

    private fun covert2Music(list: List<RecomSearchResult.RecomSongBean>) {
        var afterSize = list.size
        val index = ArrayList<String>()

        val mainRealm = Realm.getDefaultInstance()
        mainRealm.beginTransaction()

        // 清空原有推荐歌曲
        val recomGedan = mainRealm.where(GedanBean::class.java).equalTo("listId",
                Constant.RECOM_MUSIC_LIST_ID).findFirst()
        recomGedan.musicList.clear()

        // 从数据库中读取已保存过的数据
        for (bean in list) {
            val music = mainRealm.where(MusicBean::class.java)
                    .equalTo("musicId", bean.song_id.toLong()).findFirst()
            if (music != null) {
                mMusicList.add(music)
                recomGedan.musicList.add(music)
            } else {
                index.add(bean.song_id)
            }
        }
        if (index.size == 0){
            mAdapter.notifyChangeWidthStatus()
            return
        }

        // 对于未保存的数据，从网络获取，并存到数据库
        for (id in index) {
            OnlineMusicHelper.loadAndSaveInfo(id, object : OnlineMusicHelper.OnInfoLoadedListener {
                override fun onResult(code: Int, musicBean: MusicBean?, message: String?) {
                    if (code == 0 && musicBean != null) {
                        mMusicList.add(musicBean)
                        mainRealm.insert(musicBean)
                        recomGedan.musicList.add(musicBean)
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

    private fun updateLoadTime(){
        val instance = Calendar.getInstance()
        instance.set(Calendar.HOUR_OF_DAY,6)
        instance.set(Calendar.MINUTE,0)
        instance.set(Calendar.SECOND,0)

        FSharedPrefsUtils.putLong(Constant.PRE_APP_OPTION_TABLE,
                Constant.PRE_LOAD_RECOM_TIME, instance.timeInMillis)
    }

    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        // 恢复前一首播放状态
        val prePlay = mMusicList.firstOrNull { it.playStatus == Constant.PLAY_STATUS_PLAYING }
        if (prePlay != null) {
            val preIndex = mMusicList.indexOf(prePlay)
            prePlay.playStatus = Constant.PLAY_STATUS_NORMAL
            mAdapter.notifyItemChanged(preIndex + 2)
        }

        if (event.newIndex == -1){
            return
        }

        // 更新新播放歌曲状态
        val currentPlay = GlobalMusicData.getCurrentPlay()
        if (currentPlay != null && currentPlay.playFromListId == Constant.RECOM_MUSIC_LIST_ID) {
            val currentIndex = mMusicList.indexOf(currentPlay)
            mMusicList[currentIndex].playStatus = Constant.PLAY_STATUS_PLAYING
            mAdapter.notifyItemChanged(currentIndex + 2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}