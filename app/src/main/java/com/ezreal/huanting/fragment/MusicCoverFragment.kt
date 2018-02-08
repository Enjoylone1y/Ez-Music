package com.ezreal.huanting.fragment

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.*
import com.ezreal.huanting.R
import com.ezreal.huanting.activity.NowPlayingActivity
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.OnlineDownloadEvent
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.event.PlayStatusChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.OnlineMusicHelper
import com.ezreal.huanting.utils.Constant
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_music_cover.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

/**
 * 播放页 -- 专辑封面
 * Created by wudeng on 2017/12/28.
 */
class MusicCoverFragment : Fragment() {

    private var mCurrentPlay: MusicBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_music_cover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCurrentPlay = GlobalMusicData.getCurrentPlay()
        mCoverView.initNeedle(false)
        if (mCurrentPlay == null) {
            return
        }
        bindView()
        initEvent()
    }

    private fun initEvent(){
        mIvLove.setOnClickListener {
            Log.e("CoverFragment","mIvLove click")
        }

        mIvCommon.setOnClickListener {
            Log.e("CoverFragment","mIvCommon click")
        }

        mIvShare.setOnClickListener {
            Log.e("CoverFragment","mIvShare click")
        }

        mIvDownload.setOnClickListener {
            Log.e("CoverFragment","mIvDownload click")
        }
    }

    /**
     * 监听歌曲切换
     */
    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        mCurrentPlay = GlobalMusicData.getCurrentPlay()
        if (mCurrentPlay == null) {
            return
        }
        bindView()
    }

    /**
     * 监听播放状态改变
     */
    @Subscribe
    fun onPlayStatusChange(event: PlayStatusChangeEvent) {
        mCurrentPlay?.playStatus = event.status
        if (event.status == Constant.PLAY_STATUS_PLAYING) {
            mCoverView.start()
        } else {
            mCoverView.pause()
        }
    }

    @Subscribe
    fun onOnlineDownloadEvent(event:OnlineDownloadEvent){
        if (event.type == Constant.DOWLOAD_TYPE_PIC){
            if (event.code == 0){
                setMusicCover(1, event.path!!)
            }else{
                Log.e("CoverFragment",event.message)
            }
        }
    }

    private fun bindView() {
        // 恢复默认
        mCoverView.setCoverBitmap(BitmapFactory.decodeResource(context?.resources,
                R.drawable.default_play_cover))
        // 设置封面
        if (mCurrentPlay?.isOnline!!) {
            val path = mCurrentPlay?.picLocal
            if (!TextUtils.isEmpty(path) && File(path).exists()) {
                setMusicCover(1, path!!)
            } else {
                OnlineMusicHelper.loadAndSavePic(mCurrentPlay!!)
            }
        } else {
            val path = mCurrentPlay?.albumUri
            if (!TextUtils.isEmpty(path)) {
                setMusicCover(2, path!!)
            }
        }

        // 设置状态
        if (mCurrentPlay?.playStatus == Constant.PLAY_STATUS_PLAYING) {
            mCoverView.initNeedle(true)
            mCoverView.start()
        } else {
            mCoverView.initNeedle(false)
            mCoverView.pause()
        }
    }

    private fun setMusicCover(type: Int, path: String) {
        try {
            val bitmap = if (type == 1) BitmapFactory.decodeFile(path)
            else MediaStore.Images.Media.getBitmap(context?.contentResolver, Uri.parse(path))
            mCoverView.setCoverBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CoverFragment", "setMusicCover error")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}