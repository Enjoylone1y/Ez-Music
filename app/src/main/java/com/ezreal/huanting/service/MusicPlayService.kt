package com.ezreal.huanting.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import cn.hotapk.fastandrutils.utils.FSharedPrefsUtils
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.event.*
import com.ezreal.huanting.utils.Constant
import com.ezreal.huanting.helper.GlobalMusicList
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 后台播放音乐服务
 * Created by wudeng on 2017/11/27.
 */

class MusicPlayService : Service() {
    private val TAG = MusicPlayService::class.java.name
    private val mPlayer: MediaPlayer = MediaPlayer()
    private var mAudioManager: AudioManager? = null
    private var mTimeCountThread: TimeCountThread? = null

    override fun onCreate() {
        super.onCreate()
        initPlayerListener()
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        EventBus.getDefault().register(this)
    }

    /**
     * 设置播放器事件监听
     */
    private fun initPlayerListener() {

        // 装载流媒体文件监听
        mPlayer.setOnPreparedListener { mp ->
            // 开始播放
            mp.start()
            // 启动播放事件记录线程
            mTimeCountThread = TimeCountThread()
            mTimeCountThread?.start()
            // 推送播放状态更新事件
            GlobalMusicList.updatePlayStatus(Constant.PLAY_STATUS_PLAYING)
            EventBus.getDefault().post(PlayStatusChangeEvent(Constant.PLAY_STATUS_PLAYING))
        }

        // 播放结束监听
        mPlayer.setOnCompletionListener { mp ->
            val mode = FSharedPrefsUtils.getInt(Constant.OPTION_TABLE,
                    Constant.OPTION_PLAY_MODE)
            when (mode) {
                Constant.PLAY_MODE_SINGLE_RECYCLE -> {
                    // 开始播放音乐并启动播放进度统计线程
                    mp?.start()
                }

                Constant.PLAY_MODE_LIST_RECYCLE -> {
                    // TODO PlayNext
                }

                Constant.PLAY_MODE_RANDOM -> {
                    // TODO PlayRandom
                }
            }

        }

        // 播放出错监听
        mPlayer.setOnErrorListener { mp, what, extra ->
            when (what) {
                MediaPlayer.MEDIA_ERROR_IO ->
                    FToastUtils.init().show("Play Error IO ")
                MediaPlayer.MEDIA_ERROR_UNKNOWN ->
                    FToastUtils.init().show("Play Error UNKNOWN ")
            }
            // 播放出错时，自动播放下一首
            dealNextAction()
            true
        }
    }


    /**
     * 监听由各个页面发送的音乐播放指令，并进行相应处理
     */
    @Subscribe
    fun recivePlayAction(event: PlayActionEvent) {
        when (event.action) {
            MusicPlayAction.PLAY -> dealPlayAction()
            MusicPlayAction.NEXT -> dealNextAction()
            MusicPlayAction.PRE -> dealPreAction()
            MusicPlayAction.PAUSE -> dealPauseAction()
            MusicPlayAction.RESUME -> dealResumeAction()
        }
    }

    /**
     * 监听播放进度拖动事件
     */
    @Subscribe
    fun reciveSeekAction(event: SeekActionEvent) {
        dealSeekAction(event.seekTo)
    }

    private fun dealPlayAction() {
        val currentPlay = GlobalMusicList.getCurrentPlay() ?: return
        val path = currentPlay.dataPath!!
        playImp(path)
    }

    private fun dealPauseAction() {
        try {
            mPlayer.pause()
            GlobalMusicList.updatePlayStatus(Constant.PLAY_STATUS_PAUSE)
            EventBus.getDefault().post(PlayStatusChangeEvent(Constant.PLAY_STATUS_PAUSE))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun dealResumeAction() {
        try {
            mPlayer.start()
            GlobalMusicList.updatePlayStatus(Constant.PLAY_STATUS_PLAYING)
            EventBus.getDefault().post(PlayStatusChangeEvent(Constant.PLAY_STATUS_PLAYING))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dealNextAction() {
        try {
            // 停止当前播放
            if (mPlayer.isPlaying) mPlayer.stop()
            GlobalMusicList.updatePlayProcess(0)
            // 更新播放音乐
            val currentIndex = GlobalMusicList.getCurrentIndex()
            var newIndex = 0
            if (GlobalMusicList.getCurrentIndex() != GlobalMusicList.getListSize() - 1){
                newIndex = currentIndex + 1
            }
            GlobalMusicList.updatePlayIndex(newIndex)
            // 执行播放逻辑
            dealPlayAction()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun dealPreAction() {
        try {
            // 停止当前播放
            if (mPlayer.isPlaying) mPlayer.stop()
            GlobalMusicList.updatePlayProcess(0)
            // 更新播放音乐
            val currentIndex = GlobalMusicList.getCurrentIndex()
            var newIndex = currentIndex - 1
            if (currentIndex == 0){
                newIndex = GlobalMusicList.getListSize() - 1
            }
            GlobalMusicList.updatePlayIndex(newIndex)
            // 执行播放逻辑
            dealPlayAction()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun dealSeekAction(seekTo: Int) {
        try {
            mPlayer.seekTo(seekTo)
            EventBus.getDefault().post(SeekActionEvent(seekTo))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 播放音乐具体实现
     */
    private fun playImp(path: String) {
        try {
            val focus = mAudioManager?.requestAudioFocus(MyAudioFocusListener(),
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            if (focus != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                FToastUtils.init().show("播放出错，请重试~")
                return
            }
            mPlayer.reset()
            mPlayer.setDataSource(path)
            // 异步装载音乐文件
            mPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            FToastUtils.init().show("播放出错，请重试~")
        }
    }

    /**
     * 播放过程进程，记录播放进度并每隔一秒钟推送一次进度更新事件
     */
    inner class TimeCountThread : Thread() {
        override fun run() {
            super.run()
            do {
                Thread.sleep(1000)
                if (mPlayer.isPlaying){
                    GlobalMusicList.updatePlayProcess(mPlayer.currentPosition)
                    EventBus.getDefault().post(PlayProcessChangeEvent(mPlayer.currentPosition))
                }
            } while (!isInterrupted)
        }
    }

    /**
     * 音频焦点监听
     */
    inner class MyAudioFocusListener : AudioManager.OnAudioFocusChangeListener {
        override fun onAudioFocusChange(focusChange: Int) {

        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("MusicPlayService", "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}