package com.ezreal.huanting.helper

import android.util.Log
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.bean.MusicRecentPlay
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.utils.Constant
import org.greenrobot.eventbus.EventBus

/**
 * 播放歌曲列表
 *
 * 保存当前播放列表，播放歌曲索引，供 view 层读取和更新播放状态
 * 当播放列表或当前播放歌曲发生改变时，会通过 EventBus 发送更新事件
 * Created by wudeng on 2017/11/28.
 */

object GlobalMusicList {

    private var mListId = -1L
    private var mPlayList = ArrayList<MusicBean>()
    private var mCurrentPlayIndex = -1 // 默认-1，无歌曲播放
    private var sMCurrentPlay: MusicBean? = null
    private var mCurrentProcess = 0
    private var isPause = false

    /**
     * 更新当前播放列表
     */
    fun updateList(listId: Long, list: List<MusicBean>) {
        mListId = listId
        mPlayList.clear()
        mPlayList.addAll(list)
    }

    fun getNowPlayingList(): ArrayList<MusicBean> = mPlayList

    /**
     * 更新当前播放歌曲(index 指定)，并推送播放歌曲发生变化事件
     */
    fun updatePlayIndex(newIndex: Int) {
        if (newIndex < 0) {
            Log.e(GlobalMusicList::javaClass.name, "Index must >= 0")
            return
        }
        mCurrentPlayIndex = newIndex
        sMCurrentPlay = mPlayList[mCurrentPlayIndex]
        mCurrentProcess = 0

        val musicRecentPlay = MusicRecentPlay()
        musicRecentPlay.musicId = sMCurrentPlay?.musicId
        musicRecentPlay.lastPlayTime = System.currentTimeMillis()

        if (mListId != Constant.RECENT_MUSIC_LIST_ID){
            MusicDataHelper.addRecentPlay2DB(musicRecentPlay)
        }

        EventBus.getDefault().post(PlayMusicChangeEvent(newIndex))
    }

    /**
     * 更细当前
     */
    fun updatePlayStatus(status: Int){
        sMCurrentPlay?.status = status
    }

    /**
     * 更新当前播放进度
     */
    fun updatePlayProcess(newProcess:Int) {
        mCurrentProcess = newProcess
    }

    /**
     * 获取当前播放进度
     */
    fun getProcess() : Int = mCurrentProcess

    /**
     * 获取档期播放列表 ID
     */
    fun getListId(): Long = mListId

    /**
     *
     */
    fun isPause():Boolean = isPause

    /**
     * 获取当前播放歌曲索引
     */
    fun getCurrentIndex(): Int = mCurrentPlayIndex

    fun getCurrentPlay(): MusicBean? = sMCurrentPlay

    fun getListSize():Int = mPlayList.size
}