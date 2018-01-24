package com.ezreal.huanting.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezreal.huanting.R
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.event.PlayProcessChangeEvent
import com.ezreal.huanting.event.PlayStatusChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.utils.Constant
import kotlinx.android.synthetic.main.fragment_music_lrc.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

/**
 * 播放页 -- 歌词
 * Created by wudeng on 2017/12/29.
 */
class MusicLrcFragment :Fragment() {

    private var mCurrentPlay: MusicBean?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_music_lrc,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCurrentPlay = GlobalMusicData.getCurrentPlay()
        if (mCurrentPlay != null && mCurrentPlay?.musicTitle?.equals("成都")!!){
            val lrcPath = Constant.APP_LRC_PATH + File.separator + "成都_赵雷" + ".lrc"
            mLrcView.loadLrc(File(lrcPath))
        }
    }

    /**
     * 监听歌曲切换
     */
    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        mCurrentPlay = GlobalMusicData.getCurrentPlay()
        if (mCurrentPlay != null && mCurrentPlay?.musicTitle?.equals("成都")!!){
            val lrcPath = Constant.APP_LRC_PATH + File.separator + "成都_赵雷" + ".lrc"
            mLrcView.loadLrc(File(lrcPath))
        }
    }


    /**
     * 监听播放进度更新,此方法将会由子线程发起
     */
    @Subscribe
    fun onProcessChange(event: PlayProcessChangeEvent) {
        if (mLrcView.hasLrc()){
            mLrcView.updateTime(event.process.toLong())
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}