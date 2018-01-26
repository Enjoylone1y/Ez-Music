package com.ezreal.huanting.adapter

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezreal.huanting.R
import com.ezreal.huanting.bean.MusicBillBean
import com.ezreal.huanting.utils.PopupShowUtils
import com.ezreal.huanting.widget.BillMenuPopup

/**
 * 歌单列表适配器
 * Created by wudeng on 2018/1/8.
 */
class MusicBillAdapter(private val mContext: Context, private val mBill: List<MusicBillBean>)
    : RecycleViewAdapter<MusicBillBean>(mContext, mBill) {

    private var mBillMenuPopup: BillMenuPopup? = null

    override fun setItemLayoutId(position: Int): Int {
        return R.layout.item_music_list
    }

    override fun bindView(holder: RViewHolder, position: Int) {
        val bean = mBill[position]
        holder.setText(R.id.mTvListTitle, bean.listName!!)
        val size = bean.musicList.size
        holder.setText(R.id.mTvMusicNum, size.toString())
        val cover = holder.getImageView(R.id.mIvListCover)
        when {
            position == 0 -> cover?.setImageResource(R.mipmap.love)
            size > 0 -> {
                val albumUri = bean.musicList[0].albumUri
                Glide.with(mContext)
                        .load(albumUri)
                        .asBitmap()
                        .error(R.mipmap.ic_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(cover)
            }
            else -> cover?.setImageResource(R.mipmap.ic_logo)
        }

        holder.getImageView(R.id.mIvListMenu)?.setOnClickListener {
            showPopupWindow(bean, it)
        }

    }

    private fun showPopupWindow(billBean: MusicBillBean, view: View) {
        if (mBillMenuPopup == null) {
            mBillMenuPopup = BillMenuPopup(mContext)
        }
        mBillMenuPopup?.setMusicList(billBean)
        mBillMenuPopup?.isOutsideTouchable = true
        mBillMenuPopup?.animationStyle = R.style.MyPopupStyle
        mBillMenuPopup?.setOnDismissListener {
            PopupShowUtils.lightOn(mContext as Activity)
        }
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        mBillMenuPopup?.showAtLocation(view, Gravity.START or Gravity.BOTTOM,
                0, -location[1])
        PopupShowUtils.lightOff(mContext as Activity)
    }
}