package com.ezreal.huanting.fragment

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezreal.huanting.R
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.event.PlayStatusChangeEvent
import com.ezreal.huanting.helper.GlobalMusicList
import com.ezreal.huanting.utils.Constant
import kotlinx.android.synthetic.main.fragment_music_cover.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

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
        mCurrentPlay = GlobalMusicList.getCurrentPlay()
        mCoverView.initNeedle(false)
        bindView()
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
        if (event.status == Constant.PLAY_STATUS_PLAYING) {
            mCoverView.start()
        } else {
            mCoverView.pause()
        }
    }

    private fun bindView() {
        if (mCurrentPlay == null) {
            return
        }
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver,
                    Uri.parse(mCurrentPlay?.albumUri))
            mCoverView.setCoverBitmap(bitmap)
        } catch (e: Exception) {
            mCoverView.setCoverBitmap(null)
        }

        if (mCurrentPlay?.status == Constant.PLAY_STATUS_PLAYING) {
            mCoverView.initNeedle(true)
            mCoverView.start()
        } else {
            mCoverView.initNeedle(false)
            mCoverView.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}