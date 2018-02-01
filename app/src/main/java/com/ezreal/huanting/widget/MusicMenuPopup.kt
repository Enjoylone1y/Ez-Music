package com.ezreal.huanting.widget

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.activity.NowPlayingActivity
import com.ezreal.huanting.adapter.MusicBillAdapter
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.bean.MusicBillBean
import com.ezreal.huanting.event.MusicPlayAction
import com.ezreal.huanting.event.PlayActionEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.MusicDataHelper
import org.greenrobot.eventbus.EventBus

/**
 * 歌曲菜单弹窗
 *
 * Created by wudeng on 2018/1/22.
 */

class MusicMenuPopup : PopupWindow {

    private lateinit var mTvMusicTitle: TextView
    private lateinit var mTvArtist: TextView
    private lateinit var mTvAlbum: TextView
    private lateinit var mTvDownload: TextView
    private lateinit var mLayoutPlayNext: RelativeLayout
    private lateinit var mLayoutAdd2List: RelativeLayout
    private lateinit var mLayoutShare: RelativeLayout
    private lateinit var mLayoutDownload: RelativeLayout
    private lateinit var mLayoutDelete: RelativeLayout

    private var mListId: Long = -1L
    private var mMusic: MusicBean? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        val rootView = LayoutInflater.from(context).inflate(R.layout.popu_music_menu,
                null, false)
        this.contentView = rootView
        this.width = context.resources.displayMetrics.widthPixels
        isTouchable = true
        isFocusable = true

        initView(context)
    }

    private fun initView(context: Context) {
        mTvMusicTitle = contentView.findViewById(R.id.mTvMusicTitle)
        mTvArtist = contentView.findViewById(R.id.mTvArtist)
        mTvAlbum = contentView.findViewById(R.id.mTvAlbum)
        mLayoutPlayNext = contentView.findViewById(R.id.mLayoutPlayNext)
        mLayoutAdd2List = contentView.findViewById(R.id.mLayoutAdd2List)
        mLayoutDownload = contentView.findViewById(R.id.mLayoutDownload)
        mLayoutShare = contentView.findViewById(R.id.mLayoutShare)
        mLayoutDelete = contentView.findViewById(R.id.mLayoutDelete)

        mTvDownload = contentView.findViewById(R.id.mTvDownload)

        mLayoutPlayNext.setOnClickListener {
            val listId = GlobalMusicData.getListId()
            GlobalMusicData.addMusic2NextPlay(mMusic!!, mListId)
            if (listId == -1L){
                EventBus.getDefault().post(PlayActionEvent(MusicPlayAction.PLAY, -1))
                context.startActivity(Intent(context, NowPlayingActivity::class.java))
            }
            dismiss()
        }

        mLayoutAdd2List.setOnClickListener {
            add2MusicList(context)
            dismiss()
        }

        mLayoutDelete.setOnClickListener {
            // TODO  提示是否删除
            dismiss()
        }

        mLayoutShare.setOnClickListener {
            // TODO 分享
        }

        mLayoutDownload.setOnClickListener {
            // TODO 下载
        }

    }

    fun setMusic(musicBean: MusicBean, listId: Long) {
        mMusic = musicBean
        mListId = listId
        mTvMusicTitle.text = musicBean.musicTitle
        mTvArtist.text = musicBean.artistName
        mTvAlbum.text = musicBean.albumName
        if (musicBean.isOnline){
            mLayoutDownload.isClickable = true
            val color = ContextCompat.getColor(contentView.context, R.color.color_black)
            mTvDownload.setTextColor(color)
        }else{
            val color = ContextCompat.getColor(contentView.context, R.color.color_light_gray)
            mTvDownload.setTextColor(color)
            mLayoutDownload.isClickable = false
        }
    }

    private fun add2MusicList(context: Context) {
        MusicDataHelper.loadMusicListAll(object : MusicDataHelper.OnListLoadListener {
            override fun loadSuccess(bill: List<MusicBillBean>) {
                showSelectList(context, bill)
            }

            override fun loadFailed(message: String) {
                FToastUtils.init().show("获取歌单列表失败：" + message)
            }
        })
    }

    private fun showSelectList(context: Context, bill: List<MusicBillBean>) {
        // 构造 dialog
        val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_add_2_list,
                null, false)
        val dialog = Dialog(context)
        dialog.setCanceledOnTouchOutside(true)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.addContentView(rootView, layoutParams)
        dialog.show()

        // 绑定数据
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.mRcvMusicList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = MusicBillAdapter(context, bill)
        adapter.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(holder: RViewHolder, position: Int) {
                MusicDataHelper.addMusic2List(mMusic!!, bill[position].listId,
                        object : MusicDataHelper.OnAddMusic2ListListener {
                            override fun addResult(code: Int, message: String) {
                                FToastUtils.init().show(message)
                            }
                        })
                dialog.dismiss()
            }

        })
        recyclerView.adapter = adapter
    }
}