package com.ezreal.huanting.activity

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.hotapk.fastandrutils.utils.FConvertUtils
import cn.hotapk.fastandrutils.utils.FStatusBarUtils
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.bumptech.glide.Glide
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.MusicAdapter
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.OnlineMusicHelper
import com.ezreal.huanting.http.baidu.BaiduMusicApi
import com.ezreal.huanting.http.baidu.RankBillSearchResult
import com.ezreal.huanting.utils.Constant
import com.ezreal.huanting.utils.ConvertUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.BitmapCallback
import com.lzy.okgo.model.Response
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_rank_bill.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * 网络榜单 页面
 * Created by wudeng on 2018/1/30.
 */
class RankBillActivity :Activity(){

    private lateinit var mHeadView: View
    private var mBackColor = Color.parseColor("#bfbfbf")
    private var mHeadViewHeight = 0

    private var mBillId = 0L
    private lateinit var mBillName: String
    private var mRankBill:RankBillSearchResult.BillboardBean ?= null
    private val mMusicList = ArrayList<MusicBean>()
    private lateinit var mAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_bill)
        initMusicList()
        loadRankBill()
        EventBus.getDefault().register(this)
    }

    private fun loadRankBill() {
        mBillId = intent.getLongExtra("BillID", Constant.NEW_MUSIC_LIST_ID)
        mBillName = ConvertUtils.getTypeName(mBillId.toInt())
        BaiduMusicApi.searchRankBill(mBillId.toInt(), 15, 0, object :
                BaiduMusicApi.OnBillSearchListener {
            override fun onResult(code: Int, result: RankBillSearchResult?, message: String?) {
                if (code == 0 && result?.billboard != null) {
                    mRankBill = result.billboard
                    initHeadView()
                    if (result.song_list != null) {
                        covert2Music(result.song_list)
                    }
                } else {
                    FToastUtils.init().show("榜单加载失败，请重试~~")
                    finish()
                    return
                }
            }
        })
    }

    private fun initHeadView() {
        FStatusBarUtils.translucent(this)
        if (mRankBill == null) {
            return
        }

        val coverUrl = mRankBill?.pic_s640
        OkGo.get<Bitmap>(coverUrl).execute(object : BitmapCallback() {
            override fun onSuccess(response: Response<Bitmap>?) {
                if (response?.body() != null) {
                    mBackColor = Palette.from(response.body()).generate().darkVibrantSwatch?.rgb
                            ?: ContextCompat.getColor(this@RankBillActivity, R.color.color_gray)
                    setHeadViewBackByCover()
                }
            }
        })

        createHeadView()
        mHeadViewHeight = FConvertUtils.dip2px(200f)
    }


    private fun initMusicList() {
        mRcvMusic.layoutManager = LinearLayoutManager(this)
        mRcvMusic.setPullRefreshEnabled(false)
        mRcvMusic.setLoadingMoreEnabled(false)
        mRcvMusic.isNestedScrollingEnabled = false
        mRcvMusic.setHasFixedSize(false)
        mAdapter = MusicAdapter(this, mBillId, mMusicList)
        mAdapter.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(holder: RViewHolder, position: Int) {
                mAdapter.playMusic(position - 2)
            }
        })
        mRcvMusic.adapter = mAdapter

        mScrollView.setOnMyScrollChangeListener { _, scrollY, _, _ ->
            var scroll = scrollY
            if (scrollY < 0) {
                scroll = 0
            }
            var alpha = 0F
            if (scroll in 1..mHeadViewHeight) {
                alpha = scroll * 1.0F / mHeadViewHeight

            } else if (scroll > mHeadViewHeight) {
                alpha = 1F
            }

            if (scroll > mHeadViewHeight / 2) {
                mTvTitle.text = mBillName
            } else {
                mTvTitle.text = "音乐榜单"
            }

            val drawable = mActionBar.background
                    ?: ContextCompat.getDrawable(this, R.drawable.action_bar_bg_black)
            drawable?.mutate()?.alpha = (alpha * 255).toInt()
            mActionBar.background = drawable
        }
    }


    private fun createHeadView() {
        mHeadView = LayoutInflater.from(this)
                .inflate(R.layout.layout_rank_bill_head, null, false)
        val cover = mHeadView.findViewById<ImageView>(R.id.mIvBillCover)
        val name = mHeadView.findViewById<TextView>(R.id.mTvBillName)
        val update = mHeadView.findViewById<TextView>(R.id.mTvUpdate)

        name.text = mBillName
        update.text = mRankBill?.update_date

        if (mRankBill?.pic_s640.isNullOrEmpty()) {
            cover.setImageResource(R.drawable.splash)
        } else {
            Glide.with(this)
                    .load(mRankBill?.pic_s640)
                    .asBitmap()
                    .error(R.drawable.splash)
                    .into(cover)
        }
        mRcvMusic.addHeaderView(mHeadView)
    }

    private fun setHeadViewBackByCover() {
        val headBarBitmap = Bitmap.createBitmap(resources.displayMetrics.widthPixels,
                FConvertUtils.dip2px(271f), Bitmap.Config.ARGB_8888)
        headBarBitmap.eraseColor(mBackColor)//填充颜色
        val headDrawable = BitmapDrawable(resources, headBarBitmap)
        mHeadView.background = headDrawable

        val actionBarBitmap = Bitmap.createBitmap(resources.displayMetrics.widthPixels,
                FConvertUtils.dip2px(71f), Bitmap.Config.ARGB_8888)
        actionBarBitmap.eraseColor(mBackColor)//填充颜色
        val actionBarDrawable = BitmapDrawable(resources, actionBarBitmap)
        actionBarDrawable.mutate().alpha = 0
        mActionBar.background = actionBarDrawable
    }

    private fun covert2Music(list: List<RankBillSearchResult.BillSongBean>) {

        var afterSize = mMusicList.size + list.size
        val index = ArrayList<String>()

        val mainRealm = Realm.getDefaultInstance()
        // 从数据库中读取已保存过的数据
        for (bean in list) {
            val music = mainRealm.where(MusicBean::class.java)
                    .equalTo("musicId", bean.song_id.toLong()).findFirst()
            if (music != null) {
                mMusicList.add(music)
            } else {
                index.add(bean.song_id)
            }
        }
        if (index.size == 0){
            mAdapter.notifyDataSetChanged()
            return
        }

        // 对于未保存的数据，从网络获取，并存到数据库
        mainRealm.beginTransaction()
        for (id in index) {
            OnlineMusicHelper.loadAndSaveInfo(id, object :
                    OnlineMusicHelper.OnInfoLoadedListener {
                override fun onResult(code: Int, musicBean: MusicBean?, message: String?) {
                    if (code == 0 && musicBean != null) {
                        mMusicList.add(musicBean)
                        mainRealm.insert(musicBean)
                        // 在添加完成后更新数据库，刷新页面
                        if (mMusicList.size == afterSize){
                            mainRealm.commitTransaction()
                            mAdapter.notifyDataSetChanged()
                        }
                    }else{
                        afterSize -= 1
                    }
                }
            })
        }
    }


    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        // 恢复前一首播放状态
        val prePlay = mMusicList.firstOrNull { it.playStatus == Constant.PLAY_STATUS_PLAYING }
        if (prePlay != null) {
            val preIndex = mMusicList.indexOf(prePlay)
            prePlay.playStatus = Constant.PLAY_STATUS_NORMAL
            mAdapter.notifyItemChanged(preIndex + 2)
        }
        // 更新新播放歌曲状态
        val currentPlay = GlobalMusicData.getCurrentPlay()
        if (currentPlay != null && currentPlay.playFromListId == mBillId) {
            val currentIndex = mMusicList.indexOf(currentPlay)
            mMusicList[currentIndex].playStatus = Constant.PLAY_STATUS_PLAYING
            mAdapter.notifyItemChanged(currentIndex + 2)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}