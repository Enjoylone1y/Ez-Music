package com.ezreal.huanting.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import cn.hotapk.fastandrutils.utils.FSharedPrefsUtils
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.event.*
import com.ezreal.huanting.utils.Constant
import com.ezreal.huanting.helper.GlobalMusicList
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 音乐播放后台服务
 * 1、负责音乐播放任务
 * 2、监听并执行从各个界面发出的播放控制指令
 * 3、推送指令执行结果
 * 4、推送播放状态和进度
 * Created by wudeng on 2017/11/27.
 */

class MusicPlayService : Service() {
    private val mPlayer: MediaPlayer = MediaPlayer()
    private var mAudioManager: AudioManager? = null
    private var mTimeCountThread: TimeCountThread? = null
    private val mUpdateProcessMsg = 0x1111
    private val mHandler: Handler = Handler(Looper.getMainLooper()) { msg ->
        if (msg.what == mUpdateProcessMsg) {
            EventBus.getDefault().post(PlayProcessChangeEvent(mPlayer.currentPosition))
        }
        true
    }

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

        // 播放器初始化监听
        mPlayer.setOnPreparedListener { mp ->
            // 开始播放
            mp.start()
            // 启动播放时间记录线程
            mTimeCountThread = TimeCountThread()
            mTimeCountThread?.start()
            // 推送播放状态更新事件
            EventBus.getDefault().post(PlayStatusChangeEvent(Constant.PLAY_STATUS_PLAYING))
        }

        // 播放结束监听，根据当前播放模式执行不同操作
        mPlayer.setOnCompletionListener { mp ->
            val mode = FSharedPrefsUtils.getInt(Constant.OPTION_TABLE,
                    Constant.OPTION_PLAY_MODE, Constant.PLAY_MODE_LIST_RECYCLE)
            when (mode) {
                Constant.PLAY_MODE_SINGLE_RECYCLE -> {
                    mp?.start()
                }

                Constant.PLAY_MODE_LIST_RECYCLE -> {
                    dealNextAction()
                }

                Constant.PLAY_MODE_RANDOM -> {
                    // TODO PlayRandom
                }
            }

        }

        // 播放出错监听
        mPlayer.setOnErrorListener { _, what, _ ->
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
     *  监听由各个页面推送的播放控制事件
     */
    @Subscribe
    fun recivePlayAction(event: PlayActionEvent) {
        when (event.action) {
            MusicPlayAction.PLAY -> dealPlayAction()
            MusicPlayAction.NEXT -> dealNextAction()
            MusicPlayAction.PRE -> dealPreAction()
            MusicPlayAction.PAUSE -> dealPauseAction()
            MusicPlayAction.RESUME -> dealResumeAction()
            MusicPlayAction.SEEK -> dealSeekAction(event.seekTo)
        }
    }

    /**
     * 处理 “播放事件”
     */
    private fun dealPlayAction() {
        val currentPlay = GlobalMusicList.getCurrentPlay() ?: return
        val path = currentPlay.dataPath!!
        playImp(path)
    }

    /**
     * 处理 “播放-暂停” 事件
     */
    private fun dealPauseAction() {
        try {
            mPlayer.pause()
            EventBus.getDefault().post(PlayStatusChangeEvent(Constant.PLAY_STATUS_PAUSE))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 处理 “暂停->恢复” 事件
     */
    private fun dealResumeAction() {
        try {
            mPlayer.start()
            EventBus.getDefault().post(PlayStatusChangeEvent(Constant.PLAY_STATUS_PLAYING))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 处理 “下一曲” 事件
     */
    private fun dealNextAction() {
        try {
            // 停止当前播放
            if (mPlayer.isPlaying) mPlayer.stop()
            EventBus.getDefault().post(PlayProcessChangeEvent(0))
            // 更新播放音乐
            val currentIndex = GlobalMusicList.getCurrentIndex()
            var newIndex = 0
            if (GlobalMusicList.getCurrentIndex() != GlobalMusicList.getListSize() - 1) {
                newIndex = currentIndex + 1
            }
            GlobalMusicList.updatePlayIndex(newIndex)
            // 执行播放逻辑
            dealPlayAction()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 处理 “上一曲” 事件
     */
    private fun dealPreAction() {
        try {
            // 停止当前播放
            if (mPlayer.isPlaying) mPlayer.stop()
            EventBus.getDefault().post(PlayProcessChangeEvent(0))
            // 更新播放音乐
            val currentIndex = GlobalMusicList.getCurrentIndex()
            var newIndex = currentIndex - 1
            if (currentIndex == 0) {
                newIndex = GlobalMusicList.getListSize() - 1
            }
            GlobalMusicList.updatePlayIndex(newIndex)
            // 执行播放逻辑
            dealPlayAction()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 处理进度拖动事件
     */
    private fun dealSeekAction(seekTo: Int) {
        try {
            mPlayer.seekTo(seekTo)
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
     * 播放过程进程，每隔一秒钟推送一次进度更新事件
     */
    inner class TimeCountThread : Thread() {
        override fun run() {
            super.run()
            do {
                Thread.sleep(1000)
                if (mPlayer.isPlaying) {
                    mHandler.sendEmptyMessage(mUpdateProcessMsg)
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

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(null)
        mPlayer.release()
        EventBus.getDefault().unregister(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("MusicPlayService", "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}