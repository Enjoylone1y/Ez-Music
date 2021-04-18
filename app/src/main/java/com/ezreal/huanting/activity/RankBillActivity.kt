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
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.MusicAdapter
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.OnlineMusicHelper
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.result.RankBillSearchResult
import com.ezreal.huanting.utils.Constant
import com.ezreal.huanting.utils.ConvertUtils
import com.ezreal.huanting.widget.ReNestedScrollView
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.BitmapCallback
import com.lzy.okgo.model.Response

import kotlinx.android.synthetic.main.activity_rank_bill.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception

/**
 * 网络榜单 页面
 * Created by wudeng on 2018/1/30.
 */
class RankBillActivity :Activity(){

    private lateinit var mHeadView: View
    private lateinit var mHeadCover:ImageView
    private lateinit var mHeadName:TextView
    private lateinit var mHeadUpdate:TextView

    private var mBackColor = Color.parseColor("#bfbfbf")
    private var mHeadViewHeight = 0

    private var mBillId = 0L
    private lateinit var mBillName: String
    private var mRankBill: RankBillSearchResult.BillboardBean ?= null
    private val mMusicList = ArrayList<MusicBean>()
    private lateinit var mAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_bill)

        mBillId = intent.getLongExtra("BillID", Constant.NEW_MUSIC_LIST_ID)

        initHeadView()
        initMusicList()
        initEvent()

        loadRankBill()
        loadBillMusic(0)

        EventBus.getDefault().register(this)
    }

    private fun initHeadView() {
        // 状态栏透明
        FStatusBarUtils.translucent(this)
        // 构建 headView
        mHeadView = LayoutInflater.from(this)
                .inflate(R.layout.layout_rank_bill_head, null, false)
        mHeadViewHeight = FConvertUtils.dip2px(200f)
        mHeadCover = mHeadView.findViewById(R.id.mIvBillCover)
        mHeadName = mHeadView.findViewById(R.id.mTvBillName)
        mHeadUpdate = mHeadView.findViewById(R.id.mTvUpdate)
        // 添加默认 headView
        mRcvMusic.addHeaderView(mHeadView)
    }

    private fun initMusicList() {
        mRcvMusic.layoutManager = LinearLayoutManager(this)
        mRcvMusic.setPullRefreshEnabled(false)
        mRcvMusic.setLoadingMoreEnabled(false)
        mRcvMusic.isNestedScrollingEnabled = false
        mRcvMusic.setHasFixedSize(false)
        mAdapter = MusicAdapter(this, mBillId, mMusicList)
        mRcvMusic.adapter = mAdapter
    }


    private fun initEvent() {

        mIvBack.setOnClickListener { finish() }
        mScrollView.setOnMyScrollChangeListener(object :ReNestedScrollView.ScrollInterface{
            override fun onScrollChange(scrollX: Int, scrollY: Int,
                                        oldScrollX: Int, oldScrollY: Int) {
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
                        ?: ContextCompat.getDrawable(this@RankBillActivity,
                                R.drawable.action_bar_bg_black)
                drawable?.mutate()?.alpha = (alpha * 255).toInt()
                mActionBar.background = drawable
            }

        })
    }


    private fun loadRankBill() {
        mBillName = ConvertUtils.getTypeName(mBillId.toInt())
        BaiduMusicApi.searchRankBill(mBillId.toInt(), 1, 0, object :
                BaiduMusicApi.OnBillSearchListener {
            override fun onResult(code: Int, result: RankBillSearchResult?, message: String?) {
                if (code == 0 && result?.billboard != null) {
                    mRankBill = result.billboard
                    setHeadViewData()
                } else {
                    FToastUtils.init().show("榜单加载失败，请重试~~")
                    finish()
                    return
                }
            }
        })
    }

    private fun setHeadViewData(){
        mHeadName.text = mBillName
        mHeadUpdate.text = mRankBill?.update
        try {
            OkGo.get<Bitmap>(mRankBill?.pic!!).execute(object : BitmapCallback() {
                override fun onSuccess(response: Response<Bitmap>?) {
                    if (response?.body() != null) {
                        try {
                            mHeadCover.setImageBitmap(response.body())
                            val palette = Palette.from(response.body()).generate()
                            mBackColor = palette.darkVibrantSwatch?.rgb ?:
                                    palette.lightVibrantSwatch?.rgb!!
                            setHeadViewBackColor()
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                    }
                }
            })
        }catch (e:Exception){
            e.printStackTrace()
            setHeadViewBackColor()
        }
    }

    private fun setHeadViewBackColor() {
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


    private fun loadBillMusic(offset:Int){
        BaiduMusicApi.searchRankBill(mBillId.toInt(), 10, offset, object :
                BaiduMusicApi.OnBillSearchListener {
            override fun onResult(code: Int, result: RankBillSearchResult?, message: String?) {
                if (code == 0 && result?.list != null) {
                    covert2Music(result.list)
                } else {
                    FToastUtils.init().show("加载失败，请重试~~")
                }
            }
        })
    }

    private fun covert2Music(list: List<RankBillSearchResult.BillSongBean>) {

        var afterSize = mMusicList.size + list.size
        val index = ArrayList<String>()

//        val mainRealm = Realm.getDefaultInstance()
//        // 从数据库中读取已保存过的数据
//        for (bean in list) {
//            val music = mainRealm.where(MusicBean::class.java)
//                    .equalTo("musicId", bean.song_id.toLong()).findFirst()
//            if (music != null) {
//                mMusicList.add(music)
//            } else {
//                index.add(bean.song_id)
//            }
//        }
//        if (index.size == 0){
//            mAdapter.notifyChangeWidthStatus()
//            return
//        }
//
//        // 对于未保存的数据，从网络获取，并存到数据库
//        mainRealm.beginTransaction()
//        for (id in index) {
//            OnlineMusicHelper.loadAndSaveInfo(id, object : OnlineMusicHelper.OnInfoLoadedListener {
//                override fun onResult(code: Int, musicBean: MusicBean?, message: String?) {
//                    if (code == 0 && musicBean != null) {
//                        mMusicList.add(musicBean)
//                        mainRealm.insert(musicBean)
//                        // 在添加完成后更新数据库，刷新页面
//                        if (mMusicList.size == afterSize){
//                            mainRealm.commitTransaction()
//                            mAdapter.notifyChangeWidthStatus()
//                        }
//                    }else{
//                        afterSize -= 1
//                    }
//                }
//            })
//        }
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

        if (event.newIndex == -1){
            return
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