package com.ezreal.huanting.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezreal.huanting.R
import com.ezreal.huanting.activity.NowPlayingActivity
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.*
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.utils.Constant
import com.ezreal.huanting.utils.PopupShowUtils
import kotlinx.android.synthetic.main.layout_play_music.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 音乐播放控制条
 * Created by wudeng on 2017/11/17.
 */

class MusicPlayLayout : RelativeLayout {

    private var mListPopup: PlayListPopup?= null
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
        mCurrentPlay = GlobalMusicData.getCurrentPlay()
        bindView()
        initListener()
    }

    private fun initListener() {
        mIvPlay.setOnClickListener {
            when (mCurrentPlay?.playStatus) {
                Constant.PLAY_STATUS_PLAYING -> {
                    // 发送暂停令
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PAUSE,-1))
                }
                Constant.PLAY_STATUS_PAUSE -> {
                    // 发送恢复播放指令
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.RESUME,-1))
                }
                Constant.PLAY_STATUS_NORMAL -> {
                    // 发送播放指令
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PLAY,-1))
                }
            }
        }
        mIvMusicList.setOnClickListener {
            if (mListPopup == null){
                mListPopup = PlayListPopup(context)
                mListPopup?.isOutsideTouchable = true
                mListPopup?.animationStyle = R.style.MyPopupStyle
                mListPopup?.setOnDismissListener {
                    PopupShowUtils.lightOn(context as Activity)
                }
            }
            val location = IntArray(2)
            it.getLocationOnScreen(location)
            PopupShowUtils.lightOff(context as Activity)
            mListPopup?.showAtLocation(it, Gravity.START or Gravity.BOTTOM,
                    0, -location[1])
        }
    }

    /**
     * 监听歌曲切换
     */
    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        mCurrentPlay = GlobalMusicData.getCurrentPlay()
        bindView()
    }

    /**
     * 监听播放状态改变
     */
    @Subscribe
    fun onPlayStatusChange(event: PlayStatusChangeEvent) {
        when (event.status) {
            Constant.PLAY_STATUS_PLAYING -> {
                mCurrentPlay?.playStatus = event.status
                mIvPlay.setImageResource(R.mipmap.ic_pause)
            }

            Constant.PLAY_STATUS_PAUSE -> {
                mCurrentPlay?.playStatus = event.status
                mIvPlay.setImageResource(R.mipmap.ic_play)
            }

            Constant.PLAY_STATUS_NORMAL -> {
                mCurrentPlay?.playStatus = event.status
                mIvPlay.setImageResource(R.mipmap.ic_play)
            }
        }
    }

    /**
     * 监听播放列表更新事件
     */
    @Subscribe
    fun onPlayListChange(event: PlayListChangeEvent) {
        if (mListPopup == null) return
        mListPopup?.loadPlayList()
    }

    /**
     * 监听播放模式更新事件
     */
    @Subscribe
    fun updatePlayModeByEvent(event: PlayModeChangeEvent){
        if (mListPopup == null) return
        mListPopup?.updatePlayModeByEvent(event.mode)
    }


    private fun bindView() {
        if (mCurrentPlay == null){
            rootView.findViewById<View>(R.id.layout_music_play).visibility = View.GONE
        }else{
            Glide.with(context)
                    .load(Uri.parse(mCurrentPlay?.albumUri))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.local_music_white)
                    .into(mIvMusicCover)
            mTvMusicTitle.text = mCurrentPlay?.musicTitle
            mTvArtist.text = mCurrentPlay?.artistName
            rootView.findViewById<View>(R.id.layout_music_play).visibility = View.VISIBLE

            when (mCurrentPlay?.playStatus) {
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