package com.ezreal.huanting.activity

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
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.MusicAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.OnlineMusicHelper
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.result.AlbumInfoResult
import com.ezreal.huanting.utils.Constant
import com.ezreal.huanting.widget.ReNestedScrollView
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.BitmapCallback
import com.lzy.okgo.model.Response
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_album_info.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 歌单
 * Created by wudeng on 2018/1/8.
 */

class AlbumInfoActivity : BaseActivity() {

    private var mBackColor = Color.parseColor("#bfbfbf")

    private lateinit var mHeadView: View
    private lateinit var mHeadCover: ImageView
    private lateinit var mHeadName: TextView
    private lateinit var mHeadAuthor: TextView

    private val mMusicList = ArrayList<MusicBean>()
    private lateinit var mAdapter: MusicAdapter
    private lateinit var mAlbumInfo:AlbumInfoResult.AlbumInfoBean

    private var mHeadViewHeight = 0

    private var albumTitle = ""
    private var albumAuthor = ""
    private var albumId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_info)

        albumId = intent.getStringExtra("AlbumId")

        initHeadView()
        initMusicList()
        initEvent()

        loadAlbumInfo()

        EventBus.getDefault().register(this)
    }


    private fun initHeadView() {
        // 状态栏透明
        FStatusBarUtils.translucent(this)
        // 构建 headView
        mHeadView = LayoutInflater.from(this)
                .inflate(R.layout.layout_album_head, null, false)
        mHeadViewHeight = FConvertUtils.dip2px(200f)
        mHeadCover = mHeadView.findViewById(R.id.mIvAlbumCover)
        mHeadName = mHeadView.findViewById(R.id.mTvAlbumName)
        mHeadAuthor = mHeadView.findViewById(R.id.mTvAuthor)
        // 添加默认 headView
        mRcvMusic.addHeaderView(mHeadView)
    }

    private fun initMusicList() {
        mRcvMusic.layoutManager = LinearLayoutManager(this)
        mRcvMusic.setPullRefreshEnabled(false)
        mRcvMusic.setLoadingMoreEnabled(false)
        mRcvMusic.isNestedScrollingEnabled = false
        mRcvMusic.setHasFixedSize(false)
        mAdapter = MusicAdapter(this, albumId.toLong(), mMusicList)
        mRcvMusic.adapter = mAdapter
    }

    private fun initEvent() {
        mIvBack.setOnClickListener { finish() }
        mScrollView.setOnMyScrollChangeListener(object : ReNestedScrollView.ScrollInterface {
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
                    mTvTitle.text = albumTitle
                } else {
                    mTvTitle.text = "专辑"
                }

                val drawable = mActionBar.background
                        ?: ContextCompat.getDrawable(this@AlbumInfoActivity,
                                R.drawable.action_bar_bg_black)
                drawable?.mutate()?.alpha = (alpha * 255).toInt()
                mActionBar.background = drawable
            }

        })
    }

    private fun loadAlbumInfo() {
        BaiduMusicApi.loadAlbumInfo(albumId,object :BaiduMusicApi.OnAlbumInfoListener{
            override fun onResult(code: Int, result: AlbumInfoResult?, message: String?) {
                if (code == 0 && result != null){
                    mAlbumInfo = result.albumInfo
                    albumTitle = mAlbumInfo.title
                    albumAuthor = mAlbumInfo.author
                    setHeadViewData()

                    covert2Music(result.songlist)
                }
            }

        })

    }

    private fun covert2Music(list: List<AlbumInfoResult.AlbumSongBean>) {
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
        if (index.size == 0) {
            mAdapter.notifyChangeWidthStatus()
            return
        }

        // 对于未保存的数据，从网络获取，并存到数据库
        mainRealm.beginTransaction()
        for (id in index) {
            OnlineMusicHelper.loadAndSaveInfo(id, object : OnlineMusicHelper.OnInfoLoadedListener {
                override fun onResult(code: Int, musicBean: MusicBean?, message: String?) {
                    if (code == 0 && musicBean != null) {
                        mMusicList.add(musicBean)
                        mainRealm.insert(musicBean)
                        // 在添加完成后更新数据库，刷新页面
                        if (mMusicList.size == afterSize) {
                            mainRealm.commitTransaction()
                            mAdapter.notifyChangeWidthStatus()
                        }
                    } else {
                        afterSize -= 1
                    }
                }
            })
        }
    }

    private fun setHeadViewData() {
        // 设置标题
        mHeadName.text = albumTitle
        mHeadAuthor.text = albumAuthor
        try {
            OkGo.get<Bitmap>(mAlbumInfo.pic).execute(object : BitmapCallback() {
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
        } catch (e: Exception) {
            e.printStackTrace()
            setHeadViewBackColor()
        }
    }


    /** 设置封面背景色 */
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
        if (currentPlay != null && currentPlay.playFromListId == albumId.toLong()) {
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


