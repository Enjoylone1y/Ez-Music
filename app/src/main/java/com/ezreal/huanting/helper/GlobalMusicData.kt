package com.ezreal.huanting.helper

import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.bean.RecentPlayBean
import com.ezreal.huanting.event.PlayListChangeEvent
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.event.PlayProcessChangeEvent
import com.ezreal.huanting.event.PlayStatusChangeEvent
import com.ezreal.huanting.utils.Constant
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 全局音乐数据对象类
 * 1、保存当前播放列表、播放歌曲、索引、播放进度等
 * 2、监听由 MusicPlayService 推送的播放状态和播放进度更新事件，更新当前播放歌曲状态和当前播放进度
 * 3、各页面（组件）通过调用 get/set 方法，对数据进行更新
 * 4、在执行3的更新命令后，将“数据更新”事件推送给其他页面（组件）
 *
 * Created by wudeng on 2017/11/28.
 */

object GlobalMusicData {

    private var mCurrentListId = -1L
    private var mCurrentPlayList = ArrayList<MusicBean>()
    private var mCurrentPlayIndex = -1
    private var mCurrentPlay: MusicBean? = null
    private var mCurrentProcess = 0

    /**
     * 初始化时，注册事件监听
     */
    init {
        EventBus.getDefault().register(this)
    }

    /**
     * 监听播放状态该表事件
     */
    @Subscribe
    fun updatePlayStatus(event: PlayStatusChangeEvent) {
        mCurrentPlay?.playStatus = event.status
    }

    /**
     * 监听播放进度更新事件
     */
    @Subscribe
    fun updatePlayProcess(event: PlayProcessChangeEvent) {
        mCurrentProcess = event.process
    }

    /**
     * 更新当前播放列表
     */
    fun updatePlayList(listId: Long, list: List<MusicBean>) {
        mCurrentListId = listId
        mCurrentPlayList.clear()
        mCurrentPlayList.addAll(list)
        list.forEach { it.playFromListId = listId }
        // 推送列表更新事件
        EventBus.getDefault().post(PlayListChangeEvent(mCurrentListId))
    }

    /**
     * 将歌曲添加到播放列表的下一首播放位置
     */
    fun addMusic2NextPlay(music: MusicBean, listId: Long) {
        music.playFromListId = listId
        when (mCurrentListId) {
            -1L -> {
                music.playStatus = Constant.PLAY_STATUS_PLAYING
                mCurrentListId = Constant.TEMP_MUSIL_LIST_ID
                mCurrentPlayList.add(music)
                updateCurrentPlay(0)
            }

            listId -> {
                val itemIndex = mCurrentPlayList.indexOf(music)
                if (music.playStatus == Constant.PLAY_STATUS_PLAYING) {
                    return
                }
                if (itemIndex == mCurrentPlayIndex + 1) {
                    return
                }
                if (itemIndex < mCurrentPlayIndex) {
                    mCurrentPlayList.remove(music)
                    mCurrentPlayList.add(mCurrentPlayIndex, music)
                    mCurrentPlayIndex -= 1
                } else {
                    mCurrentPlayList.remove(music)
                    mCurrentPlayIndex += 1
                }
            }

            else -> {
                mCurrentPlayList.add(mCurrentPlayIndex + 1, music)
            }
        }

        EventBus.getDefault().post(PlayListChangeEvent(mCurrentListId))
    }

    /**
     * 更新当前播放歌曲，并推送播放歌曲发生变化事件
     */
    fun updateCurrentPlay(newIndex: Int) {

        mCurrentPlayIndex = newIndex
        mCurrentPlay = mCurrentPlayList[mCurrentPlayIndex]
        mCurrentProcess = 0

        val musicRecentPlay = RecentPlayBean()
        musicRecentPlay.musicId = mCurrentPlay?.musicId!!
        musicRecentPlay.lastPlayTime = System.currentTimeMillis()

        if (mCurrentListId != Constant.RECENT_MUSIC_LIST_ID) {
            MusicDataHelper.addRecentPlay2DB(musicRecentPlay)
        }

        // 推送播放歌曲改变事件
        EventBus.getDefault().post(PlayMusicChangeEvent(newIndex))
    }

    /**
     * 从播放列表中删除指定歌曲
     */
    fun deleteMusicFromList(music: MusicBean){
        val index = mCurrentPlayList.indexOf(music)
        mCurrentPlayList.remove(music)
        if (index < mCurrentPlayIndex){
            mCurrentPlayIndex -= 1
        }
        EventBus.getDefault().post(PlayListChangeEvent(mCurrentListId))
    }

    /**
     *  清空播放列表，清空当前播放
     */
    fun clearPlayList(){
        mCurrentPlayList.clear()
        mCurrentPlay = null
        mCurrentPlayIndex = -1
        mCurrentProcess = 0
        EventBus.getDefault().post(PlayListChangeEvent(mCurrentListId))
        EventBus.getDefault().post(PlayMusicChangeEvent(-1))
    }

    /**
     * 获取当前播放列表 ID
     */
    fun getListId(): Long = mCurrentListId

    /**
     * 获取当前播放列表
     */
    fun getNowPlayingList(): ArrayList<MusicBean> = mCurrentPlayList

    /**
     * 获取当前播放列表歌曲数量
     */
    fun getListSize(): Int = mCurrentPlayList.size

    /**
     * 获取当前播放歌曲索引
     */
    fun getCurrentIndex(): Int = mCurrentPlayIndex

    /**
     * 获取当前播放歌曲
     */
    fun getCurrentPlay(): MusicBean? = mCurrentPlay

    /**
     * 获取当前播放进度
     */
    fun getProcess(): Int = mCurrentProcess

}