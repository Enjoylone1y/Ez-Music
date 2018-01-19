package com.ezreal.huanting.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import cn.hotapk.fastandrutils.utils.FScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezreal.huanting.R
import com.ezreal.huanting.activity.NowPlayingActivity
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.*
import com.ezreal.huanting.helper.GlobalMusicList
import com.ezreal.huanting.utils.Constant
import kotlinx.android.synthetic.main.layout_play_music.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 音乐播放控制条
 * Created by wudeng on 2017/11/17.
 */

class MusicPlayLayout : RelativeLayout {

    private var mListWindow: NowPlayListWindow ?= null
    private var mCurrentPlay: MusicBean? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_play_music,
                this, true)
        view.setOnClickListener {
            context?.startActivity(Intent(context, NowPlayingActivity::class.java))
        }
        mCurrentPlay = GlobalMusicList.getCurrentPlay()
        bindView()
        initListener()
    }

    private fun initListener() {
        mIvPlay.setOnClickListener {
            when (mCurrentPlay?.status) {
                Constant.PLAY_STATUS_PLAYING -> {
                    // 发送暂停令
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PAUSE))
                }
                Constant.PLAY_STATUS_PAUSE -> {
                    // 发送恢复播放指令
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.RESUME))
                }
                Constant.PLAY_STATUS_NORMAL -> {
                    // 发送播放指令
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PLAY))
                }
            }
        }
        mIvMusicList.setOnClickListener {
            if (mListWindow == null){
                mListWindow = NowPlayListWindow(context)
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
    }

    private fun lightOn() {
        try {
            val activity = context as Activity
            val attributes = activity.window?.attributes
            attributes?.alpha = 1.0f
            activity.window?.attributes = attributes
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun lightOff() {
        try {
            val activity = context as Activity
            val attributes = activity.window?.attributes
            attributes?.alpha = 0.6f
            activity.window?.attributes = attributes
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
     * 播放状态改变事件，MusicPlayService 发出
     */

    @Subscribe
    fun onPlayStatusChange(event: PlayStatusChangeEvent) {
        when (event.status) {
            Constant.PLAY_STATUS_PLAYING -> {
                mCurrentPlay?.status = event.status
                mIvPlay.setImageResource(R.mipmap.ic_pause)
            }

            Constant.PLAY_STATUS_PAUSE -> {
                mCurrentPlay?.status = event.status
                mIvPlay.setImageResource(R.mipmap.ic_play)
            }

            Constant.PLAY_STATUS_NORMAL -> {
                mCurrentPlay?.status = event.status
                mIvPlay.setImageResource(R.mipmap.ic_play)
            }
        }
    }

    private fun bindView() {
        if (mCurrentPlay == null){
            rootView.findViewById<View>(R.id.layout_music_play).visibility = View.GONE
        }else{
            Glide.with(context)
                    .load(mCurrentPlay?.albumUri)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.local_music_white)
                    .into(mIvMusicCover)
            mTvMusicTitle.text = mCurrentPlay?.musicTitle
            mTvArtist.text = mCurrentPlay?.artist
            rootView.findViewById<View>(R.id.layout_music_play).visibility = View.VISIBLE

            when (mCurrentPlay?.status) {
                Constant.PLAY_STATUS_PLAYING -> {
                    mIvPlay.setImageResource(R.mipmap.ic_pause)
                }

                Constant.PLAY_STATUS_PAUSE -> {
                    mIvPlay.setImageResource(R.mipmap.ic_play)
                }

                Constant.PLAY_STATUS_NORMAL -> {
                    mIvPlay.setImageResource(R.mipmap.ic_play)
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBus.getDefault().register(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBus.getDefault().unregister(this)
    }

}