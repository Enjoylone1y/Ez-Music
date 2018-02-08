package com.ezreal.huanting.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.event.SearchResultEvent
import com.ezreal.huanting.http.result.KeywordSearchResult.AlbumBean
import kotlinx.android.synthetic.main.fragment_album_result.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 搜索结果 - 专辑
 * Created by wudeng on 2018/2/8.
 */

class AlbumResultFragment :Fragment() {

    private val mAlbumList = ArrayList<AlbumBean>()
    private lateinit var mAdapter:AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album_result,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRcvAlbumList.layoutManager = LinearLayoutManager(context)
        mAdapter = AlbumAdapter(context!!)
        mRcvAlbumList.adapter = mAdapter
    }

    @Subscribe
    fun onSearchResult(event:SearchResultEvent){
        mAlbumList.clear()
        mAlbumList.addAll(event.result.album)
        mAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    inner class AlbumAdapter(mContext: Context):
            RecycleViewAdapter<AlbumBean>(mContext,mAlbumList){
        override fun setItemLayoutId(position: Int): Int {
            return R.layout.item_album_result
        }

        override fun bindView(holder: RViewHolder, position: Int) {
            val albumBean = mAlbumList[position]
            holder.setImageByUrl(context!!,R.id.mIvAlbumCover,albumBean.artistpic!!,
                    R.drawable.local_music_white)
            holder.setText(R.id.mTvAlbumName,albumBean.albumname)
            holder.setText(R.id.mTvArtist,albumBean.artistname)
        }
    }

}