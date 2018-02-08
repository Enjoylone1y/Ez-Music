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
import com.ezreal.huanting.http.result.KeywordSearchResult.ArtistBean
import kotlinx.android.synthetic.main.fragment_artist_result.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 搜索结果 - 歌手
 * Created by wudeng on 2018/2/8.
 */

class ArtistResultFragment :Fragment() {

    private val mArtistLit = ArrayList<ArtistBean>()
    private lateinit var mAdapter: ArtistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_result,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRcvArtistList.layoutManager = LinearLayoutManager(context)
        mAdapter = ArtistAdapter(context!!)
        mRcvArtistList.adapter = mAdapter
    }

    @Subscribe
    fun onSearchResult(event: SearchResultEvent){
        mArtistLit.clear()
        mArtistLit.addAll(event.result.artist)
        mAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    inner class ArtistAdapter(mContext: Context):
            RecycleViewAdapter<ArtistBean>(mContext,mArtistLit){
        override fun setItemLayoutId(position: Int): Int {
            return R.layout.item_artist_result
        }

        override fun bindView(holder: RViewHolder, position: Int) {
            val artistBean = mArtistLit[position]
            holder.setImageByUrl(context!!,R.id.mIvArtistPic,artistBean.artistpic!!,
                    R.drawable.local_music_white)
            holder.setText(R.id.mTvArtist,artistBean.artistname)
        }
    }
}