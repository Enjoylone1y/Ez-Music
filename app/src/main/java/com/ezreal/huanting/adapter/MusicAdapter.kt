package com.ezreal.huanting.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
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
 * 本地歌单音乐列表适配器
 * Created by wudeng on 2017/11/27.
 */

class MusicAdapter(private val mContext: Context, private val listId: Long,
                   private val mList: List<MusicBean>)
    : RecycleViewAdapter<MusicBean>(mContext, mList) {

    private var mMenuPopupWindow: MusicMenuPopup? = null

    init {
        val currentPlay = GlobalMusicData.getCurrentPlay()
        if (currentPlay != null && currentPlay.playFromListId == listId) {
            mList.firstOrNull {
                it.musicId == currentPlay.musicId }?.playStatus = Constant.PLAY_STATUS_PLAYING
        }
    }

    override fun setItemLayoutId(position: Int): Int = R.layout.item_music

    override fun bindView(holder: RViewHolder, position: Int) {
        holder.setText(R.id.mTvSongTitle, mList[position].musicTitle)
        holder.setText(R.id.mTvArtist, mList[position].artistName)
        holder.setText(R.id.mTvAlbum, mList[position].albumName)
        holder.getImageView(R.id.iv_play_status)?.visibility = View.GONE
        when {
            mList[position].playStatus == Constant.PLAY_STATUS_PLAYING -> {
                holder.setImageResource(R.id.iv_play_status, R.mipmap.play)
                holder.getImageView(R.id.iv_play_status)?.visibility = View.VISIBLE
            }
            mList[position].playStatus == Constant.PLAY_STATUS_PAUSE -> {
                holder.setImageResource(R.id.iv_play_status, R.mipmap.pause)
                holder.getImageView(R.id.iv_play_status)?.visibility = View.VISIBLE
            }
        }
        val view = holder.convertView.findViewById<ImageView>(R.id.iv_item_menu)
        view.setOnClickListener {
            showPopupWindow(view, mList[position])
        }

        holder.convertView.setOnClickListener {
            playMusic(position)
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

    private fun playMusic(position: Int) {
        if(GlobalMusicData.getListId() == listId){
            if (GlobalMusicData.getCurrentIndex() == position) {
                mContext.startActivity(Intent(mContext, NowPlayingActivity::class.java))
                return
            }
        }else{
            GlobalMusicData.updatePlayList(listId, mList)
        }
        GlobalMusicData.updateCurrentPlay(position)
        EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PLAY, -1))
    }


    fun notifyChangeWidthStatus(){
        checkAndSetStatus()
        notifyDataSetChanged()
    }

    private fun checkAndSetStatus(){
        val currentPlay = GlobalMusicData.getCurrentPlay()
        if (currentPlay != null && currentPlay.playFromListId == listId) {
            mList.firstOrNull {
                it.musicId == currentPlay.musicId }?.playStatus= Constant.PLAY_STATUS_PLAYING
        }
    }
}