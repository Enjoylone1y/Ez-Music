package com.ezreal.huanting.widget

import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import com.ezreal.huanting.R
import com.ezreal.huanting.bean.MusicBillBean
import com.ezreal.huanting.event.MusicListChangeEvent
import com.ezreal.huanting.helper.MusicDataHelper
import org.greenrobot.eventbus.EventBus

/**
 * 歌曲菜单弹窗
 *
 * Created by wudeng on 2018/1/22.
 */

class BillMenuPopup : PopupWindow {

    private var mTvListTitle: TextView? = null
    private var mLayoutEdInfo: RelativeLayout? = null
    private var mLayoutDelete: RelativeLayout? = null
    private var mMusicBill: MusicBillBean? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        val rootView = LayoutInflater.from(context).inflate(R.layout.popu_list_menu,
                null, false)
        this.contentView = rootView
        this.width = context.resources.displayMetrics.widthPixels

        isTouchable = true
        isFocusable = true

        initView(context)
    }

    private fun initView(context: Context) {
        mTvListTitle = contentView.findViewById(R.id.mTvListTitle)
        mLayoutEdInfo = contentView.findViewById(R.id.mLayoutAdd2List)
        mLayoutDelete = contentView.findViewById(R.id.mLayoutDelete)

        mLayoutEdInfo?.setOnClickListener {
            dismiss()
        }

        mLayoutDelete?.setOnClickListener {
            showDialog(context)
            dismiss()
        }
    }

    private fun showDialog(context: Context) {
        val title = "确定删除 " + mMusicBill?.listName + " 吗？"
        AlertDialog.Builder(context, R.style.MyAlertDialog)
                .setTitle(title)
                .setNegativeButton("取消", { _, _ -> dismiss() })
                .setPositiveButton("确定", { _, _ ->
                    MusicDataHelper.deleteMusicList( mMusicBill?.listId!!)
                    EventBus.getDefault().post(MusicListChangeEvent(-1L))
                    dismiss()
                })
                .show()
    }

    fun setMusicList(musicBean: MusicBillBean) {
        mMusicBill = musicBean
        mTvListTitle?.text = musicBean.listName
    }

}