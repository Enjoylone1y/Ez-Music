package com.ezreal.huanting.widget

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import cn.hotapk.fastandrutils.utils.FSharedPrefsUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.activity.NowPlayingActivity
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.MusicPlayAction
import com.ezreal.huanting.event.PlayActionEvent
import com.ezreal.huanting.event.PlayModeChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.utils.Constant
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

/**
 * 播放列表弹窗
 * Created by wudeng on 2017/12/4.
 */
class PlayListPopup : PopupWindow {

    private val mPlayMode = listOf(Constant.PLAY_MODE_LIST_RECYCLE,
            Constant.PLAY_MODE_SINGLE_RECYCLE, Constant.PLAY_MODE_RANDOM)
    private val mPlayModeIcon = listOf(R.mipmap.list_recycle_g,
            R.mipmap.single_recycle_g, R.mipmap.random_play_g)
    private var mCurrentModeIndex = 0

    private var mLayoutMode: LinearLayout? = null
    private var mIvPlayMode: ImageView? = null
    private var mTvPlayMode: TextView? = null
    private var mIvDelete: ImageView? = null
    private var mRecyclerView: RecyclerView? = null
    private var mSongList = ArrayList<MusicBean>()
    private var mAdapter: RecycleViewAdapter<MusicBean>? = null


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        val root = LayoutInflater.from(context).inflate(R.layout.popup_now_play_list,
                null, true)
        contentView = root
        this.height = context?.resources?.displayMetrics?.heightPixels!! / 2
        this.width = context.resources?.displayMetrics?.widthPixels!!

        mLayoutMode = root.findViewById(R.id.layout_play_mode) as LinearLayout?
        mIvPlayMode = root.findViewById(R.id.iv_play_mode) as ImageView?
        mTvPlayMode = root.findViewById(R.id.tv_play_mode) as TextView?
        mIvDelete = root.findViewById(R.id.iv_delete) as ImageView?
        mRecyclerView = root.findViewById(R.id.rcv_playing_list) as RecyclerView?

        val mode = FSharedPrefsUtils.getInt(Constant.OPTION_TABLE,
                Constant.OPTION_PLAY_MODE, Constant.PLAY_MODE_LIST_RECYCLE)
        mCurrentModeIndex = mPlayMode.indexOf(mode)
        mIvPlayMode?.setImageResource(mPlayModeIcon[mCurrentModeIndex])
        setPlayModeText()

        initList(context)
        initEvent()
        loadPlayList()
    }

    private fun initList(context: Context) {
        // 播放列表初始化
        mRecyclerView?.layoutManager = LinearLayoutManager(context)
        mAdapter = object : RecycleViewAdapter<MusicBean>(context, mSongList) {
            override fun setItemLayoutId(position: Int): Int = R.layout.item_music_in_popu

            override fun bindView(holder: RViewHolder, position: Int) {
                val item = mSongList[position]
                holder.setVisible(R.id.iv_playing, false)
                if (position == GlobalMusicData.getCurrentIndex()) {
                    holder.setVisible(R.id.iv_playing, true)
                }
                holder.setText(R.id.mTvSongTitle, item.musicTitle!!)
                holder.setText(R.id.mTvArtist, item.artist!!)
                val delete = holder.convertView.findViewById<ImageView>(R.id.iv_delete)
                delete.setOnClickListener {
                    deleteItem(position, item)
                }
            }
        }

        mAdapter?.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(holder: RViewHolder, position: Int) {
                if (position == GlobalMusicData.getCurrentIndex()) {
                    context.startActivity(Intent(context, NowPlayingActivity::class.java))
                } else {
                    val index = GlobalMusicData.getCurrentIndex()
                    mSongList[index].status = Constant.PLAY_STATUS_NORMAL
                    mAdapter?.notifyItemChanged((index))
                    GlobalMusicData.updateCurrentPlay(position)
                    mSongList[position].status = Constant.PLAY_STATUS_PLAYING
                    EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PLAY, -1))
                    mAdapter?.notifyItemChanged((position))
                }
            }
        })
        mRecyclerView?.adapter = mAdapter
    }

    private fun initEvent() {
        mLayoutMode?.setOnClickListener {
            mCurrentModeIndex = (mCurrentModeIndex + 1) % mPlayMode.size
            mIvPlayMode?.setImageResource(mPlayModeIcon[mCurrentModeIndex])
            FSharedPrefsUtils.putInt(Constant.OPTION_TABLE,
                    Constant.OPTION_PLAY_MODE, mPlayMode[mCurrentModeIndex])
            setPlayModeText()
            EventBus.getDefault().post(PlayModeChangeEvent(mPlayMode[mCurrentModeIndex]))
        }

        mIvDelete?.setOnClickListener {
            AlertDialog.Builder(contentView.context, R.style.MyAlertDialog)
                    .setTitle("清空播放列表并停止播放吗？")
                    .setCancelable(true)
                    .setNegativeButton("取消", { _, _ ->
                        dismiss()
                    })
                    .setPositiveButton("确定", { _, _ ->
                        GlobalMusicData.clearPlayList()
                        EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.STOP, -1))
                        dismiss()
                    }).show()
        }
    }

    private fun deleteItem(position: Int, item: MusicBean) {
        // 如果当前仅一首，则清空播放列表,停止播放
        if (GlobalMusicData.getListSize() == 1) {
            mSongList.clear()
            mAdapter?.notifyDataSetChanged()
            dismiss()
            GlobalMusicData.clearPlayList()
            EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.STOP, -1))
            return
        }
        // 如果正在播放，则播放下一曲，并更新播放列表
        if (position == GlobalMusicData.getCurrentIndex()) {
            EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.NEXT, -1))
        }
        GlobalMusicData.deleteMusicFromList(item)

        // 更新页面
        mSongList.remove(item)
        mSongList.firstOrNull { it.status == Constant.PLAY_STATUS_PLAYING }
                ?.status = Constant.PLAY_STATUS_NORMAL
        mSongList[GlobalMusicData.getCurrentIndex()].status = Constant.PLAY_STATUS_PLAYING
        mAdapter?.notifyDataSetChanged()
    }

    private fun setPlayModeText() {
        when (mPlayMode[mCurrentModeIndex]) {
            Constant.PLAY_MODE_LIST_RECYCLE -> {
                mTvPlayMode?.text = "列表循环"
            }
            Constant.PLAY_MODE_SINGLE_RECYCLE -> {
                mTvPlayMode?.text = "单曲循环"
            }
            Constant.PLAY_MODE_RANDOM -> {
                mTvPlayMode?.text = "随机播放"
            }
        }
    }

    /**
     * 更新播放列表
     */
    fun loadPlayList() {
        if (GlobalMusicData.getListId() != -1L) {
            mSongList.clear()
            mSongList.addAll(GlobalMusicData.getNowPlayingList())
            mAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * 根据播放模式更新事件，更新播放模式
     */
    fun updatePlayModeByEvent(mode:Int){
        val index = mPlayMode.indexOf(mode)
        if (index == mCurrentModeIndex) return
        mCurrentModeIndex = mPlayMode.indexOf(mode)
        mIvPlayMode?.setImageResource(mPlayModeIcon[mCurrentModeIndex])
        setPlayModeText()
    }

}