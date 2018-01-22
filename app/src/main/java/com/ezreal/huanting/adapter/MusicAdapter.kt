package com.ezreal.huanting.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import com.ezreal.huanting.R
import com.ezreal.huanting.activity.NowPlayingActivity
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.MusicPlayAction
import com.ezreal.huanting.event.PlayActionEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.utils.Constant
import com.ezreal.huanting.utils.PopupShowUtils
import com.ezreal.huanting.widget.MusicMenuPopup
import org.greenrobot.eventbus.EventBus


/**
 * 音乐列表适配器
 * Created by wudeng on 2017/11/27.
 */

class MusicAdapter(private val mContext: Context, private val listId: Long,
                   private val mList: List<MusicBean>)
    : RecycleViewAdapter<MusicBean>(mContext, mList) {

    private var mMenuPopupWindow: MusicMenuPopup? = null

    init {
        val currentPlay = GlobalMusicData.getCurrentPlay()
        if (currentPlay != null && currentPlay.playFromList == listId) {
            mList.first { it.musicId == currentPlay.musicId }.status = Constant.PLAY_STATUS_PLAYING
        }
    }

    override fun setItemLayoutId(position: Int): Int = R.layout.item_music

    override fun bindView(holder: RViewHolder, position: Int) {
        holder.setText(R.id.mTvSongTitle, mList[position].musicTitle!!)
        holder.setText(R.id.mTvArtist, mList[position].artist!!)
        holder.setText(R.id.tv_album, mList[position].album!!)
        holder.getImageView(R.id.iv_play_status)?.visibility = View.GONE
        when {
            mList[position].status == Constant.PLAY_STATUS_PLAYING -> {
                holder.setImageResource(R.id.iv_play_status, R.mipmap.play)
                holder.getImageView(R.id.iv_play_status)?.visibility = View.VISIBLE
            }
            mList[position].status == Constant.PLAY_STATUS_PAUSE -> {
                holder.setImageResource(R.id.iv_play_status, R.mipmap.pause)
                holder.getImageView(R.id.iv_play_status)?.visibility = View.VISIBLE
            }
        }
        val view = holder.convertView.findViewById<ImageView>(R.id.iv_item_menu)
        view.setOnClickListener {
            showPopupWindow(view, mList[position])
        }
    }

    private fun showPopupWindow(view: View, music: MusicBean) {
        if (mMenuPopupWindow == null) {
            mMenuPopupWindow = MusicMenuPopup(mContext)
        }
        mMenuPopupWindow?.setMusic(music, listId)
        mMenuPopupWindow?.isOutsideTouchable = true
        mMenuPopupWindow?.animationStyle = R.style.MyPopupStyle
        mMenuPopupWindow?.setOnDismissListener {
            PopupShowUtils.lightOn(mContext as Activity)
        }
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        mMenuPopupWindow?.showAtLocation(view, Gravity.START or Gravity.BOTTOM,
                0, -location[1])
        PopupShowUtils.lightOff(mContext as Activity)
    }

    fun checkPlaySong(musicPosition: Int, viewPosition: Int) {
        // 如果当前播放列表不是本地音乐列表，更新播放列表，并切换播放歌曲
        if (GlobalMusicData.getListId() != listId) {
            // 恢复旧播放列表歌曲状态
            if (!GlobalMusicData.getNowPlayingList().isEmpty()
                    && GlobalMusicData.getCurrentIndex() != -1) {
                GlobalMusicData.getNowPlayingList()[GlobalMusicData.getCurrentIndex()].status =
                        Constant.PLAY_STATUS_NORMAL
            }
            // 切换播放歌曲
            playNewMusic(musicPosition, viewPosition)
            return
        }

        // 当前播放列表是本地音乐列表，若点击了当前播放的 item 打开音乐播放页，否则切换歌曲
        if (GlobalMusicData.getCurrentIndex() == musicPosition) {
            mContext.startActivity(Intent(mContext, NowPlayingActivity::class.java))
            return
        }

        // 将原来播放的 item 状态重置
        if (GlobalMusicData.getCurrentIndex() != -1) {
            val oldPosition = GlobalMusicData.getCurrentIndex()
            mList[oldPosition].status = Constant.PLAY_STATUS_NORMAL
            notifyItemChanged(oldPosition + (viewPosition - musicPosition))
        }

        // 更新播放歌曲状态
        playNewMusic(musicPosition, viewPosition)
    }

    private fun playNewMusic(musicPosition: Int, viewPosition: Int) {
        GlobalMusicData.updatePlayList(listId, mList)
        GlobalMusicData.updateCurrentPlay(musicPosition)
        mList[musicPosition].status = Constant.PLAY_STATUS_PLAYING
        notifyItemChanged(viewPosition)
        EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PLAY, -1))
    }

}