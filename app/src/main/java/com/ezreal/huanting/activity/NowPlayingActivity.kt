package com.ezreal.huanting.activity

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.SeekBar
import cn.hotapk.fastandrutils.utils.FSharedPrefsUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.*
import com.ezreal.huanting.fragment.MusicCoverFragment
import com.ezreal.huanting.fragment.MusicLrcFragment
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.OnlineMusicHelper
import com.ezreal.huanting.utils.Constant
import com.ezreal.huanting.utils.ConvertUtils
import com.ezreal.huanting.utils.PopupShowUtils
import com.ezreal.huanting.widget.PlayListPopup
import com.zhouwei.blurlibrary.EasyBlur
import kotlinx.android.synthetic.main.activty_now_playing.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * 音乐播放页
 * Created by wudeng on 2017/11/28.
 */

class NowPlayingActivity : BaseActivity() {

    private val mPlayMode = listOf(Constant.PLAY_MODE_LIST_RECYCLE,
            Constant.PLAY_MODE_SINGLE_RECYCLE, Constant.PLAY_MODE_RANDOM)
    private val mPlayModeIcon = listOf(R.mipmap.list_recycle_g,
            R.mipmap.single_recycle_g, R.mipmap.random_play_g)
    private var mCurrentModeIndex = 0

    // 专辑封面 Fragment
    private val mCoverFragment by lazy { MusicCoverFragment() }
    // 歌词 Fragment
    private val mLrcFragment by lazy { MusicLrcFragment() }
    // 记录当前的 Fragment
    private var mCurrentView: Fragment? = null
    // 记录当前显示的是“专辑封面”还是“歌词”
    private var showCover = false
    // 是否正在拖动进度条，是的情况下不会根据事件更新进度条
    private var isSeeking = false
    // 当前播放歌曲的引用
    private var mCurrentPlay: MusicBean? = null
    // 播放列表 view
    private var mListPopup: PlayListPopup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_now_playing)

        // 初始的专辑封面
        supportFragmentManager.beginTransaction()
                .add(R.id.mCoverLrcView, mCoverFragment).commit()
        mCurrentView = mCoverFragment
        showCover = true

        // 当前播放歌曲信息
        mCurrentPlay = GlobalMusicData.getCurrentPlay()
        if (mCurrentPlay == null) finish()

        val mode = FSharedPrefsUtils.getInt(Constant.OPTION_TABLE,
                Constant.OPTION_PLAY_MODE, Constant.PLAY_MODE_LIST_RECYCLE)
        mCurrentModeIndex = mPlayMode.indexOf(mode)

        mIvPlayMode.setImageResource(mPlayModeIcon[mCurrentModeIndex])

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
            when (mCurrentPlay?.playStatus) {
                Constant.PLAY_STATUS_PLAYING -> {
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PAUSE, -1))
                }
                Constant.PLAY_STATUS_PAUSE -> {
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.RESUME, -1))
                }
                Constant.PLAY_STATUS_NORMAL -> {
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PLAY, -1))
                }
            }
        }
        // 下一曲
        mIvNext.setOnClickListener {
            EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.NEXT, -1))
        }
        // 上一曲
        mIvPre.setOnClickListener {
            EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PRE, -1))
        }
        // 进度条拖动事件
        mProcessBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeeking = false
                // 拖动结束时，发送进度更新指令
                EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.SEEK, mProcessBar.progress))
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mTvCurrentTime.text = ConvertUtils.getTimeWithProcess(progress)
            }

        })
        // 打开播放列表
        mIvList.setOnClickListener {
            if (mListPopup == null) {
                mListPopup = PlayListPopup(this)
                mListPopup?.isOutsideTouchable = true
                mListPopup?.animationStyle = R.style.MyPopupStyle
                mListPopup?.setOnDismissListener {
                    PopupShowUtils.lightOn(this)
                }
            }
            val location = IntArray(2)
            it.getLocationOnScreen(location)
            PopupShowUtils.lightOff(this)
            mListPopup?.showAtLocation(it, Gravity.START or Gravity.BOTTOM,
                    0, -location[1])
        }

        // 返回前一页页面
        mIvBack.setOnClickListener {
            finish()
        }


        mIvPlayMode.setOnClickListener {
            mCurrentModeIndex = (mCurrentModeIndex + 1) % mPlayMode.size
            mIvPlayMode.setImageResource(mPlayModeIcon[mCurrentModeIndex])
            FSharedPrefsUtils.putInt(Constant.OPTION_TABLE,
                    Constant.OPTION_PLAY_MODE, mPlayMode[mCurrentModeIndex])
            EventBus.getDefault().post(PlayModeChangeEvent(mPlayMode[mCurrentModeIndex]))

            if (mListPopup == null) return@setOnClickListener
            mListPopup?.updatePlayModeByEvent(mPlayMode[mCurrentModeIndex])
        }


        mCoverLrcView.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                changeView()
            }
            false
        }
    }



    /**
     * 监听歌曲切换事件
     */
    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        mCurrentPlay = GlobalMusicData.getCurrentPlay()
        if (mCurrentPlay == null) {
            finish()
            return
        }
        bindView()
    }

    /**
     * 监听播放状态改变事件
     */
    @Subscribe
    fun onPlayStatusChange(event: PlayStatusChangeEvent) {
        mCurrentPlay?.playStatus = event.status
        if (mCurrentPlay?.playStatus == Constant.PLAY_STATUS_PLAYING) {
            mIvPlay.setImageResource(R.mipmap.ic_pause_main)
        } else {
            mIvPlay.setImageResource(R.mipmap.song_play)
        }
    }

    /**
     * 监听播放进度更新事件
     */
    @Subscribe
    fun onProcessChange(event: PlayProcessChangeEvent) {
        if (isSeeking) return
        mProcessBar.progress = event.process
        mTvCurrentTime.text = ConvertUtils.getTimeWithProcess(event.process)
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
    fun updatePlayModeByEvent(event: PlayModeChangeEvent) {
        val index = mPlayMode.indexOf(event.mode)
        if (index == mCurrentModeIndex) return
        mCurrentModeIndex = mPlayMode.indexOf(event.mode)
        mIvPlayMode?.setImageResource(mPlayModeIcon[mCurrentModeIndex])

        if (mListPopup == null) return
        mListPopup?.updatePlayModeByEvent(event.mode)
    }

    /**
     * 监听网络歌曲缓冲事件
     */
    @Subscribe
    fun bufferUpdateEvent(event: PlayBufferUpdateEvent) {
        val buffer = event.percent * 1.0 / 100.0 * mProcessBar.max
        mProcessBar.secondaryProgress = buffer.toInt()
    }


    @Subscribe
    fun onOnlineDownloadEvent(event: OnlineDownloadEvent) {
        if (event.type == Constant.DOWLOAD_TYPE_PIC) {
            if (event.code == 0) {
                setBackWithBitmap(1, event.path!!)
            } else {
                Log.e("NowPlayActivity", event.message)
            }
        }
    }

    /**
     * 绑定当前播放歌曲数据，更新界面
     */
    private fun bindView() {
        // 设置标题和歌手名（必不为空）
        mTvMusicTitle.text = mCurrentPlay?.musicTitle
        mTvArtist.text = mCurrentPlay?.artistName

        // 恢复进度条,背景,播放状态
        mProcessBar.max = 0
        mProcessBar.progress = 0
        mTvCurrentTime.text = getString(R.string.time_zero)
        mTvTotalTime.text = getString(R.string.time_zero)
        mIvBackGround.setImageResource(R.drawable.default_play_bg)
        mIvPlay.setImageResource(R.mipmap.song_play)

        // 设置数据值
        mProcessBar.max = mCurrentPlay?.duration?.toInt()!!
        mProcessBar.progress = GlobalMusicData.getProcess()
        mTvCurrentTime.text = ConvertUtils.getTimeWithProcess(GlobalMusicData.getProcess())
        mTvTotalTime.text = ConvertUtils.getTimeWithProcess(mCurrentPlay?.duration?.toInt()!!)

        if (mCurrentPlay?.playStatus == Constant.PLAY_STATUS_PLAYING) {
            mIvPlay.setImageResource(R.mipmap.ic_pause_main)
        } else {
            mIvPlay.setImageResource(R.mipmap.song_play)
        }

        if (mCurrentPlay?.isOnline!!){
            val url = mCurrentPlay?.picLocal
            if (TextUtils.isEmpty(url)) {
                // 本地封面为空，从网络下载
                OnlineMusicHelper.loadAndSavePic(mCurrentPlay?.musicId!!, mCurrentPlay?.bigPic!!)
            } else {
                setBackWithBitmap(1, url!!)
            }

        } else {
            val uri = mCurrentPlay?.albumUri
            if (!TextUtils.isEmpty(uri)) {
                setBackWithBitmap(2, uri!!)
            }
        }
    }

    private fun setBackWithBitmap(type: Int, path: String) {
        try {
            val srcBitmap = if (type == 1) BitmapFactory.decodeFile(path)
            else MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(path))
            val blurBitmap = EasyBlur.with(this)
                    .bitmap(srcBitmap) //要模糊的图片
                    .radius(10)//模糊半径
                    .scale(8)//指定模糊前缩小的倍数
                    .blur()
            mIvBackGround.setImageBitmap(blurBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("NowPlayingActivity", "setBackWithBitmap error")
        }
    }

    /**
     * 封面/歌词页面切换,由mLrcFragment，mCoverFragment 根据手势调用
     */

    fun changeView(){
        if (showCover) {
            showCover = false
            switchFragment(mLrcFragment)
        } else {
            showCover = true
            switchFragment(mCoverFragment)
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
