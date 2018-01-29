package com.ezreal.huanting.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.RankBillBean
import com.ezreal.huanting.http.HttpRequest
import com.ezreal.huanting.http.RankBillSearchResult
import com.ezreal.huanting.http.RecomSearchResult
import com.ezreal.huanting.utils.ConvertUtils
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import kotlinx.android.synthetic.main.fragment_online_music.*


/**
 * 在线音乐
 * Created by wudeng on 2017/11/16.
 */
class OnlineFragment : Fragment() {

    // 推荐歌曲
    private val mRecomMusicList = ArrayList<RecomSearchResult.RecomSongBean>()
    // 最热歌曲
    private val mHotMusicList = ArrayList<RankBillSearchResult.BillSongBean>()
    // 最新歌曲
    private val mNewMusicList = ArrayList<RankBillSearchResult.BillSongBean>()
    // 音乐榜单
    private val mRankBillList = ArrayList<RankBillBean>()

    private lateinit var mRecomAdapter: RecycleViewAdapter<RecomSearchResult.RecomSongBean>
    private lateinit var mHotAdapter: RecycleViewAdapter<RankBillSearchResult.BillSongBean>
    private lateinit var mNewAdapter: RecycleViewAdapter<RankBillSearchResult.BillSongBean>

    private lateinit var mBillAdapter: RecycleViewAdapter<RankBillBean>

    private val mHandler = MyHandler()
    private class MyHandler: Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_online_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        loadData()
    }

    private fun initView() {
        // 推荐音乐
        mRcvRecomMusic.layoutManager = GridLayoutManager(context, 3)
        mRcvRecomMusic.addItemDecoration(
                RecyclerViewDivider.with(context!!).color(android.R.color.white).size(5).build())
        mRcvRecomMusic.isNestedScrollingEnabled = false
        mRcvRecomMusic.setHasFixedSize(false)
        mRecomAdapter = object :
                RecycleViewAdapter<RecomSearchResult.RecomSongBean>(context!!, mRecomMusicList) {
            override fun setItemLayoutId(position: Int): Int {
                return R.layout.item_online_music
            }

            override fun bindView(holder: RViewHolder, position: Int) {
                val bean = mRecomMusicList[position]
                holder.setImageByUrl(context!!, R.id.iv_big_pic, bean.pic_big, R.drawable.splash)
                holder.setText(R.id.tv_music_title, bean.title)
                holder.setText(R.id.tv_artist, bean.author)
            }
        }
        mRcvRecomMusic.adapter = mRecomAdapter

        // 最热音乐
        mRcvHotMusic.layoutManager = GridLayoutManager(context, 3)
        mRcvHotMusic.addItemDecoration(
                RecyclerViewDivider.with(context!!).color(android.R.color.white).size(5).build())
        mRcvHotMusic.isNestedScrollingEnabled = false
        mRcvHotMusic.setHasFixedSize(false)
        mHotAdapter = object :
                RecycleViewAdapter<RankBillSearchResult.BillSongBean>(context!!, mNewMusicList) {
            override fun setItemLayoutId(position: Int): Int {
                return R.layout.item_online_music
            }

            override fun bindView(holder: RViewHolder, position: Int) {
                val bean = mNewMusicList[position]
                holder.setImageByUrl(context!!, R.id.iv_big_pic, bean.pic_big, R.drawable.splash)
                holder.setText(R.id.tv_music_title, bean.title)
                holder.setText(R.id.tv_artist, bean.artist_name)
            }

        }
        mRcvHotMusic.adapter = mHotAdapter

        // 最新音乐
        mRcvNewMusic.layoutManager = GridLayoutManager(context, 3)
        mRcvNewMusic.addItemDecoration(
                RecyclerViewDivider.with(context!!).color(android.R.color.white).size(5).build())
        mRcvNewMusic.isNestedScrollingEnabled = false
        mRcvNewMusic.setHasFixedSize(false)
        mNewAdapter = object :
                RecycleViewAdapter<RankBillSearchResult.BillSongBean>(context!!, mHotMusicList) {
            override fun setItemLayoutId(position: Int): Int {
                return R.layout.item_online_music
            }

            override fun bindView(holder: RViewHolder, position: Int) {
                val bean = mHotMusicList[position]
                holder.setImageByUrl(context!!, R.id.iv_big_pic, bean.pic_big, R.drawable.splash)
                holder.setText(R.id.tv_music_title, bean.title)
                holder.setText(R.id.tv_artist, bean.artist_name)
            }

        }
        mRcvNewMusic.adapter = mNewAdapter

        // 音乐榜单
        mRcvMusicRank.layoutManager = LinearLayoutManager(context)
        mRcvMusicRank.isNestedScrollingEnabled = false
        mRcvMusicRank.setHasFixedSize(false)
        mRcvMusicRank.addItemDecoration(
                RecyclerViewDivider.with(context!!).color(android.R.color.white).size(5).build())
        mBillAdapter = object : RecycleViewAdapter<RankBillBean>(context!!, mRankBillList) {
            override fun setItemLayoutId(position: Int): Int {
                return R.layout.item_rank_bill
            }

            override fun bindView(holder: RViewHolder, position: Int) {
                val billBean = mRankBillList[position]
                holder.setImageByUrl(context!!,R.id.iv_bill_cover,billBean.billCoverUrl!!,
                        R.drawable.splash)
                if (billBean.musicFirst != null){
                    holder.setText(R.id.tv_first_title,billBean.musicFirst?.title!!)
                    holder.setText(R.id.tv_first_artist,billBean.musicFirst?.artist_name!!)
                }

                if (billBean.musicSecond != null){
                    holder.setText(R.id.tv_second_title,billBean.musicSecond?.title!!)
                    holder.setText(R.id.tv_second_artist,billBean.musicSecond?.artist_name!!)
                }

                if (billBean.musicThird != null){
                    holder.setText(R.id.tv_third_title,billBean.musicThird?.title!!)
                    holder.setText(R.id.tv_third_artist,billBean.musicThird?.artist_name!!)
                }

            }
        }
        mRcvMusicRank.adapter = mBillAdapter
    }

    private fun initEvent() {

    }


    private fun loadData() {
        // 获取推荐音乐 以数据库中歌曲播放次数为基准

        HttpRequest.searchRecomMusic("44805341",
                6, object :HttpRequest.OnRecomSearchListener{
            override fun onResult(code: Int, result: List<RecomSearchResult.RecomSongBean>?, message: String?) {
                if (code == 0 && result != null){
                    mRecomMusicList.addAll(result)
                    mRecomAdapter.notifyDataSetChanged()
                }
            }
        })

        // 获取最热音乐
        HttpRequest.searchRankBill(2, 6, 0, object : HttpRequest.OnBillSearchListener {
            override fun onResult(code: Int, result: RankBillSearchResult?, message: String?) {
                if (code == 0 && result?.song_list != null) {
                    mHotMusicList.addAll(result.song_list)
                    mHotAdapter.notifyDataSetChanged()
                }
            }
        })

        // 获取最新音乐
        HttpRequest.searchRankBill(1, 6, 0, object : HttpRequest.OnBillSearchListener {
            override fun onResult(code: Int, result: RankBillSearchResult?, message: String?) {
                if (code == 0 && result?.song_list != null) {
                    mNewMusicList.addAll(result.song_list)
                    mNewAdapter.notifyDataSetChanged()
                }
            }
        })

        // 获取歌曲榜单
        val types = listOf(20, 21, 22, 23, 24, 25)
        HttpRequest.searchBillList(types, 3, 0, object : HttpRequest.OnBillListSearchListener {
            override fun onResult(code: Int, result: List<RankBillBean>?, message: String?) {
                if (code == 0 && result != null) {
                    mRankBillList.addAll(result)
                    mBillAdapter.notifyDataSetChanged()
                }
            }

        })
    }

}