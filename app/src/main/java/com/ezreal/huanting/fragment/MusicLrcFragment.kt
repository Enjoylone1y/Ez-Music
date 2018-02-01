package com.ezreal.huanting.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.*
import com.ezreal.huanting.R
import com.ezreal.huanting.activity.NowPlayingActivity
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.OnlineDownloadEvent
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.event.PlayProcessChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.LrcLoadHelper
import com.ezreal.huanting.helper.OnlineMusicHelper
import com.ezreal.huanting.utils.Constant
import kotlinx.android.synthetic.main.fragment_music_lrc.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

/**
 * 播放页 -- 歌词
 * Created by wudeng on 2017/12/29.
 */

class MusicLrcFragment :Fragment() {

    private var mCurrentPlay: MusicBean?= null
    private lateinit var mDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_music_lrc,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCurrentPlay = GlobalMusicData.getCurrentPlay()
        if (mCurrentPlay == null){
            return
        }
        bindView()
    }


    /**
     * 监听歌曲切换
     */
    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        mLrcView.clearLrc()
        mCurrentPlay = GlobalMusicData.getCurrentPlay()
        if (mCurrentPlay == null){
           return
        }
        bindView()
    }

    @Subscribe
    fun onOnlineDownloadEvent(event: OnlineDownloadEvent){
        if (event.type == Constant.DOWNLOAD_TYPE_LRC){
            if (event.code == 0){
                mLrcView.loadLrc(File(event.path))
            }else{
                Log.e("LrcFragment",event.message)
            }
        }
    }

    private fun bindView(){
        if (mCurrentPlay?.isOnline!!){
            if (TextUtils.isEmpty(mCurrentPlay?.lrcLocal)){
                OnlineMusicHelper.loadAndSaveLrc(mCurrentPlay?.musicId!!,mCurrentPlay?.lrcLink!!)
            }else{
                mLrcView.loadLrc(File(mCurrentPlay?.lrcLocal))
            }
        }else{
            searchFromBaidu()
        }
    }

    private fun searchFromBaidu() {
        LrcLoadHelper.loadLrcFileBaidu(mCurrentPlay!!, object : LrcLoadHelper.OnLoadLrcListener {
            override fun onSuccess(lrcPath: String) {
                mLrcView.loadLrc(File(lrcPath))
            }

            override fun onLoadOnline() {
                mLrcView.setLabel("从百度获取ing……")
            }

            override fun onFailed() {
                mLrcView.setLabel("暂无歌词")
            }

        })
    }

    private fun searchFromNetease() {
        LrcLoadHelper.loadLrcFromNetease(mCurrentPlay!!, object : LrcLoadHelper.OnLoadLrcListener {
            override fun onSuccess(lrcPath: String) {
                mLrcView.loadLrc(File(lrcPath))
            }

            override fun onLoadOnline() {
                mLrcView.setLabel("从网易云获取ing……")
            }

            override fun onFailed() {
                mLrcView.setLabel("暂无歌词")
            }

        })
    }

    /**
     * 监听播放进度更新,此方法将会由子线程发起
     */
    @Subscribe
    fun onProcessChange(event: PlayProcessChangeEvent) {
        if (mLrcView.hasLrc()){
            mLrcView.updateTime(event.process.toLong())
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}