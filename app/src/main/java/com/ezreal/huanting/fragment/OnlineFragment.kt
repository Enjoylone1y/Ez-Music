package com.ezreal.huanting.fragment

import android.annotation.SuppressLint
import android.content.Intent
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
import com.ezreal.huanting.activity.RankBillActivity
import com.ezreal.huanting.activity.RankBillListActivity
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RankBillAdapter
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.bean.RankBillBean
import com.ezreal.huanting.http.baidu.BaiduMusicApi
import com.ezreal.huanting.http.baidu.KeywordSearchResult
import com.ezreal.huanting.http.baidu.RankBillSearchResult
import com.ezreal.huanting.http.baidu.RecomSearchResult
import com.ezreal.huanting.utils.Constant
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_online_music.*


/**
 * 在线音乐
 * Created by wudeng on 2017/11/16.
 */
class OnlineFragment : Fragment() {

    private val MSG_LOAD_RECOM_DATA = 0x100

    private lateinit var mBaseId: String
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

    @SuppressLint("HandlerLeak")
    inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_LOAD_RECOM_DATA -> {
                    loadRecomMusic()
                }
            }
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
        mBillAdapter = RankBillAdapter(context!!,mRankBillList)
        mRcvMusicRank.adapter = mBillAdapter
    }

    private fun initEvent() {
        // 打开推荐音乐列表
        mLayoutRecomMusic.setOnClickListener {

        }

        // 打开最热歌曲列表
        mLayoutHotMusic.setOnClickListener {
            val intent = Intent(context, RankBillActivity::class.java)
            intent.putExtra("BillID",Constant.HOT_MUSIC_LIST_ID)
            startActivity(intent)
        }
        // 打开最新歌曲列表
        mLayoutNewMusic.setOnClickListener {
            val intent = Intent(context, RankBillActivity::class.java)
            intent.putExtra("BillID",Constant.NEW_MUSIC_LIST_ID)
            startActivity(intent)
        }
        // 打开榜单列表
        mLayoutMusicBill.setOnClickListener {
            startActivity(Intent(context,RankBillListActivity::class.java))
        }

        // 打开推荐歌曲列表
        mRecomAdapter.setItemClickListener(object :RecycleViewAdapter.OnItemClickListener{
            override fun onItemClick(holder: RViewHolder, position: Int) {

            }

        })
        // 打开最热歌曲列表
        mHotAdapter.setItemClickListener(object :RecycleViewAdapter.OnItemClickListener{
            override fun onItemClick(holder: RViewHolder, position: Int) {
                val intent = Intent(context, RankBillActivity::class.java)
                intent.putExtra("BillID",Constant.HOT_MUSIC_LIST_ID)
                startActivity(intent)
            }

        })
        // 打开最新歌曲列表
         mNewAdapter.setItemClickListener(object :RecycleViewAdapter.OnItemClickListener{
            override fun onItemClick(holder: RViewHolder, position: Int) {
                val intent = Intent(context, RankBillActivity::class.java)
                intent.putExtra("BillID",Constant.NEW_MUSIC_LIST_ID)
                startActivity(intent)
            }

        })

        // 打开对应的榜单
        mBillAdapter.setItemClickListener(object :RecycleViewAdapter.OnItemClickListener{
            override fun onItemClick(holder: RViewHolder, position: Int) {
                val intent = Intent(context, RankBillActivity::class.java)
                intent.putExtra("BillID", mRankBillList[position].billType.toLong())
                startActivity(intent)
            }
        })

    }

    private fun loadData() {
        // getRecomBaseId()
        loadRecomMusic()

        // 获取最热音乐
        BaiduMusicApi.searchRankBill(2, 6, 0, object : BaiduMusicApi.OnBillSearchListener {
            override fun onResult(code: Int, result: RankBillSearchResult?, message: String?) {
                if (code == 0 && result?.song_list != null) {
                    mHotMusicList.addAll(result.song_list)
                    mHotAdapter.notifyDataSetChanged()
                }
            }
        })

        // 获取最新音乐
        BaiduMusicApi.searchRankBill(1, 6, 0, object : BaiduMusicApi.OnBillSearchListener {
            override fun onResult(code: Int, result: RankBillSearchResult?, message: String?) {
                if (code == 0 && result?.song_list != null) {
                    mNewMusicList.addAll(result.song_list)
                    mNewAdapter.notifyDataSetChanged()
                }
            }
        })

        // 获取歌曲榜单
        val types = listOf(20, 21, 22)
        BaiduMusicApi.searchBillList(types, 3, 0, object : BaiduMusicApi.OnBillListSearchListener {
            override fun onResult(code: Int, result: List<RankBillBean>?, message: String?) {
                if (code == 0 && result != null) {
                    mRankBillList.addAll(result)
                    mRankBillList.sortBy { it.billType }
                    mBillAdapter.notifyDataSetChanged()
                }
            }

        })
    }

    private fun loadRecomMusic() {
        BaiduMusicApi.searchRecomMusic("74172066",
                6, object : BaiduMusicApi.OnRecomSearchListener {
            override fun onResult(code: Int, result: List<RecomSearchResult.RecomSongBean>?, message: String?) {
                if (code == 0 && result != null) {
                    mRecomMusicList.addAll(result)
                    mRecomAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun getRecomBaseId() {
        // 获取推荐音乐 以数据库中歌曲播放次数为基准
        val realm = Realm.getDefaultInstance()
        val lastOnline = realm.where(MusicBean::class.java)
                .equalTo("isOnline", true)
                .findAllSorted("playCount")
                .lastOrNull()
        if (lastOnline != null) {
            mBaseId = lastOnline.musicId.toString()
            mHandler.sendEmptyMessage(MSG_LOAD_RECOM_DATA)
            return
        }
        val max = realm.where(MusicBean::class.java)
                .equalTo("isOnline", false)
                .max("playCount")
        val lastLocal = realm.where(MusicBean::class.java)
                .equalTo("playCount", max.toLong())
                .findFirst()
        if (lastLocal == null || lastLocal.playCount == 0L) {
            mBaseId = "74172066"
            mHandler.sendEmptyMessage(MSG_LOAD_RECOM_DATA)
            return
        }
        val key = lastLocal.musicTitle + " " + lastLocal.artistName
        BaiduMusicApi.searchMusicByKey(key, object : BaiduMusicApi.OnKeywordSearchListener {
            override fun onResult(code: Int, result: KeywordSearchResult.SongBean?, message: String?) {
                mBaseId = if (code == 0 && result != null) {
                    result.songid
                } else {
                    "74172066"
                }
                mHandler.sendEmptyMessage(MSG_LOAD_RECOM_DATA)
            }
        })
    }

}