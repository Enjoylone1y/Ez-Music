package com.ezreal.huanting.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Recycler View Adapter
 * Created by wudeng on 2017/3/9.
 */

abstract class RecycleViewAdapter<T>(private val mContext: Context, private val mList: List<T>?) :
        RecyclerView.Adapter<RViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)
    private var mLongClickListener: OnItemLongClickListener? = null
    private var mClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, itemLayout: Int): RViewHolder {
        val view = mInflater.inflate(itemLayout, parent, false)
        return RViewHolder(mContext, view)
    }

    override fun onBindViewHolder(holder: RViewHolder, position: Int) {
        val view = holder.convertView
        view.setOnClickListener {
            if (mClickListener != null) {
                mClickListener!!.onItemClick(holder, holder.adapterPosition)
            }
        }

        view.setOnLongClickListener {
            if (mLongClickListener != null) {
                mLongClickListener!!.onItemLongClick(holder, holder.adapterPosition)
            }
            false
        }
        bindView(holder, position)
    }

    override fun getItemViewType(position: Int): Int = this.setItemLayoutId(position)

    override fun getItemCount(): Int = mList?.size!!


    /**
     * set item layout id
     * @param position item'process in list
     * @return layout id
     */
    abstract fun setItemLayoutId(position: Int): Int

    /**
     * bind view by holder
     * @param holder view holder
     * @param position process in data list
     */
    abstract fun bindView(holder: RViewHolder, position: Int)

    fun setItemLongClickListener(longClickListener: OnItemLongClickListener) {
        this.mLongClickListener = longClickListener
    }

    fun setItemClickListener(clickListener: OnItemClickListener) {
        this.mClickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(holder: RViewHolder, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(holder: RViewHolder, position: Int)
    }

}
