package com.ezreal.huanting.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.SeekBar
import com.ezreal.huanting.R
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.*
import com.ezreal.huanting.fragment.MusicCoverFragment
import com.ezreal.huanting.fragment.MusicLrcFragment
import com.ezreal.huanting.helper.GlobalMusicList
import com.ezreal.huanting.utils.Constant
import com.ezreal.huanting.utils.ConvertUtils
import com.ezreal.huanting.widget.NowPlayListWindow
import kotlinx.android.synthetic.main.activty_now_playing.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 音乐播放页
 * Created by wudeng on 2017/11/28.
 */

class NowPlayingActivity : AppCompatActivity() {

    private var mCurrentPlay: MusicBean? = null
    private val mCoverFragment by lazy { MusicCoverFragment() }
    private val mLrcFragment by lazy { MusicLrcFragment() }
    private var mCurrentView: Fragment? = null
    private var showCover = false
    private var mListWindow: NowPlayListWindow ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_now_playing)

        // 初始的专辑封面
        supportFragmentManager.beginTransaction()
                .add(R.id.mCoverLrcView, mCoverFragment).commit()
        mCurrentView = mCoverFragment
        showCover = true

        // 当前播放歌曲信息
        mCurrentPlay = GlobalMusicList.getCurrentPlay()
        bindView()

        // 初始化监听事件
        initListener()
        EventBus.getDefault().register(this)
    }


    /**
     * 初始化各控件事件监听
     */
    private fun initListener() {
        // 播放/暂停
        mIvPlay.setOnClickListener {
            when (mCurrentPlay?.status) {
                Constant.PLAY_STATUS_PLAYING -> {
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PAUSE))
                }
                Constant.PLAY_STATUS_PAUSE -> {
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.RESUME))
                }
                Constant.PLAY_STATUS_NORMAL -> {
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PLAY))
                }
            }
        }
        // 下一曲
        mIvNext.setOnClickListener {
            EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.NEXT))
        }
        // 上一曲
        mIvPre.setOnClickListener {
            EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PRE))
        }
        // 进度条拖动监听
        mProcessBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                EventBus.getDefault().post(SeekActionEvent(seekBar?.progress!!))
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mTvCurrentTime.text = ConvertUtils.getTimeWithProcess(progress)
            }

        })
        // 打开播放列表
        mIvList.setOnClickListener {
            if (mListWindow == null){
            mListWindow = NowPlayListWindow(this)
            mListWindow?.isOutsideTouchable = true
            mListWindow?.animationStyle = R.style.MyPopupStyle
            mListWindow?.setOnDismissListener {
                lightOn()
            }
        }
        mListWindow?.loadMusicList()
        val location = IntArray(2)
        it.getLocationOnScreen(location)
        lightOff()
        mListWindow?.showAtLocation(it, Gravity.START or Gravity.BOTTOM,
                0, -location[1])
        }
        // 返回前一页页面
        mIvBack.setOnClickListener {
            finish()
        }
        // 封面/歌词页面切换
        mCoverLrcView.setOnClickListener {
            if (showCover) {
                showCover = false
                switchFragment(mLrcFragment)
            } else {
                showCover = true
                switchFragment(mCoverFragment)
            }
        }
    }


    private fun lightOn() {
        try {
            val attributes = window?.attributes
            attributes?.alpha = 1.0f
            window?.attributes = attributes
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun lightOff() {
        try {
            val attributes = window?.attributes
            attributes?.alpha = 0.6f
            window?.attributes = attributes
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 监听歌曲切换
     */
    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        mCurrentPlay = GlobalMusicList.getCurrentPlay()
        bindView()
    }

    /**
     * 监听播放状态改变
     */
    @Subscribe
    fun onPlayStatusChange(event: PlayStatusChangeEvent) {
        mCurrentPlay?.status = event.status
        if (mCurrentPlay?.status == Constant.PLAY_STATUS_PLAYING) {
            mIvPlay.setImageResource(R.mipmap.ic_pause_main)
        } else {
            mIvPlay.setImageResource(R.mipmap.song_play)
        }
    }

    /**
     * 监听播放进度更新,此方法将会由子线程发起
     */
    @Subscribe
    fun onProcessChange(event: PlayProcessChangeEvent) {
        mProcessBar.progress = event.process
        runOnUiThread {
            mTvCurrentTime.text = ConvertUtils.getTimeWithProcess(event.process)
        }
    }

    /**
     * 绑定歌曲数据
     */
    private fun bindView() {
        if (mCurrentPlay == null) return
        mTvMusicTitle.text = mCurrentPlay?.musicTitle
        mTvArtist.text = mCurrentPlay?.artist
        mProcessBar.max = mCurrentPlay?.duration!!
        mProcessBar.progress = GlobalMusicList.getProcess()
        mTvCurrentTime.text = ConvertUtils.getTimeWithProcess(GlobalMusicList.getProcess())
        mTvTotalTime.text = ConvertUtils.getTimeWithProcess(mCurrentPlay?.duration!!)
        if (mCurrentPlay?.status == Constant.PLAY_STATUS_PLAYING){
            mIvPlay.setImageResource(R.mipmap.ic_pause_main)
        }else{
            mIvPlay.setImageResource(R.mipmap.song_play)
        }
    }


    /**
     * Fragment 跳转
     */
    private fun switchFragment(fragment: Fragment) {
        if (fragment != mCurrentView) {
            if (fragment.isAdded) {
                supportFragmentManager.beginTransaction()
                        .hide(mCurrentView).show(fragment).commit()
            } else {
                supportFragmentManager.beginTransaction()
                        .hide(mCurrentView).add(R.id.mCoverLrcView, fragment).commit()
            }
            mCurrentView = fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
