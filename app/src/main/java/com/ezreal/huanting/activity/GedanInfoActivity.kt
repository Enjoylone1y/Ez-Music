package com.ezreal.huanting.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
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
import com.ezreal.huanting.bean.GedanBean
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.helper.MusicDataHelper
import com.ezreal.huanting.helper.OnlineMusicHelper
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.result.GedanInfoResult
import com.ezreal.huanting.present.BasePresentImpl
import com.ezreal.huanting.utils.Constant
import com.ezreal.huanting.view.BaseViewImpl
import com.ezreal.huanting.widget.ReNestedScrollView
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.BitmapCallback
import com.lzy.okgo.model.Response
//import io.realm.Realm
import kotlinx.android.synthetic.main.activity_gedan_info.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 歌单
 * Created by wudeng on 2018/1/8.
 */

class GedanInfoActivity : BaseActivity<BaseViewImpl,BasePresentImpl>(), BaseViewImpl {

    private var mBackColor = Color.parseColor("#bfbfbf")

    private lateinit var mHeadView: View
    private lateinit var mHeadCover: ImageView
    private lateinit var mHeadName: TextView
    private lateinit var mHeadDesc: TextView

    private val mMusicList = ArrayList<MusicBean>()
    private lateinit var mAdapter: MusicAdapter
    private lateinit var mGedan: GedanBean

    private var mHeadViewHeight = 0

    private var isOnline = false
    private var listName = ""
    private var dsec = ""
    private var author = ""
    private var listId = -1L

    private val musicList = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gedan_info)

        isOnline = intent.getBooleanExtra("isOnline", false)
        listId = intent.getLongExtra("ListId", -1)

        initHeadView()
        initMusicList()
        initEvent()

        if (isOnline) {
            loadOnlineGedan()
        } else {
            loadLocalGedan()
        }

        EventBus.getDefault().register(this)
    }


    override fun createPresent(): BasePresentImpl {
        return BasePresentImpl(this)
    }

    private fun initHeadView() {
        // 状态栏透明
        FStatusBarUtils.translucent(this)
        // 构建 headView
        mHeadView = LayoutInflater.from(this)
                .inflate(R.layout.layout_gedan_head, null, false)
        mHeadViewHeight = FConvertUtils.dip2px(200f)
        mHeadCover = mHeadView.findViewById(R.id.mIvGedanCover)
        mHeadName = mHeadView.findViewById(R.id.mTvGedanName)
        mHeadDesc = mHeadView.findViewById(R.id.mTvDesc)
        // 添加默认 headView
        mRcvMusic.addHeaderView(mHeadView)
    }

    private fun initMusicList() {
        mRcvMusic.layoutManager = LinearLayoutManager(this)
        mRcvMusic.setPullRefreshEnabled(false)
        mRcvMusic.setLoadingMoreEnabled(false)
        mRcvMusic.isNestedScrollingEnabled = false
        mRcvMusic.setHasFixedSize(false)
        mAdapter = MusicAdapter(this, listId, mMusicList)
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
                    mTvTitle.text = listName
                } else {
                    mTvTitle.text = "歌单"
                }

                val drawable = mActionBar.background
                        ?: ContextCompat.getDrawable(this@GedanInfoActivity,
                                R.drawable.action_bar_bg_black)
                drawable?.mutate()?.alpha = (alpha * 255).toInt()
                mActionBar.background = drawable
            }

        })
    }

    /** 加载网络歌单数据 */
    private fun loadOnlineGedan() {
        BaiduMusicApi.loadGedanInfo(listId, object : BaiduMusicApi.OnGedanInfoListener {
            override fun onResult(code: Int, result: GedanInfoResult?, message: String?) {
                if (code == 0 && result != null) {
                    val gedan = GedanBean()
                    gedan.listId = listId
                    gedan.title = result.title
                    gedan.tag = result.tag
                    gedan.desc = result.desc
                    gedan.pic = result.pic
                    gedan.listenum = result.listenum
                    gedan.collectnum = result.collectnum

                    mGedan = gedan
                    listName = result.title
                    dsec = result.desc
                    setHeadViewData()

                    covert2Music(result.content)
                } else {
                    FToastUtils.init().show("读取歌单信息失败！")
                    finish()
                }
            }
        })
    }

    /** 加载本地歌单数据 ***/
    private fun loadLocalGedan() {
        MusicDataHelper.getMusicListById(listId, object : MusicDataHelper.OnListLoadListener {
            override fun loadSuccess(bill: List<GedanBean>) {
                if (bill.isEmpty()) {
                    FToastUtils.init().show("读取歌单信息失败！")
                    finish()
                } else {
                    mGedan = bill[0]
                    listName = mGedan.listName
                    author = mGedan.creatorName
                    setHeadViewData()

                    val musicList = listOf(MusicBean())
                    mMusicList.addAll(musicList)
                    mAdapter.notifyChangeWidthStatus()
                }
            }

            override fun loadFailed(message: String) {
                FToastUtils.init().show("读取歌单信息失败！")
                finish()
            }
        })
    }

    private fun covert2Music(list: List<GedanInfoResult.ContentBean>) {
        var afterSize = mMusicList.size + list.size
        val index = ArrayList<String>()

//        val mainRealm = Realm.getDefaultInstance()
//        // 从数据库中读取已保存过的数据
//        for (bean in list) {
//            val music = mainRealm.where(MusicBean::class.java)
//                    .equalTo("musicId", bean.song_id?.toLong()).findFirst()
//            if (music != null) {
//                mMusicList.add(music)
//            } else {
//                index.add(bean.song_id!!)
//            }
//        }
//        if (index.size == 0) {
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
//                        if (mMusicList.size == afterSize) {
//                            mainRealm.commitTransaction()
//                            mAdapter.notifyChangeWidthStatus()
//                        }
//                    } else {
//                        afterSize -= 1
//                    }
//                }
//            })
//        }
    }

    private fun setHeadViewData() {
        // 设置标题
        mHeadName.text = listName

        try {
            // 网络歌单
            if (isOnline) {
                loadOnlineBitmap(mGedan.pic)
                mHeadDesc.text = dsec
                return
            }

            mHeadDesc.text = author

            // 本地歌单
            // 如果有自定义封面
            if (!TextUtils.isEmpty(mGedan.coverPathByEd)) {
                val bitmap = BitmapFactory.decodeFile(mGedan.coverPathByEd)
                mHeadCover.setImageBitmap(bitmap)
                val palette = Palette.from(bitmap).generate()
                mBackColor = palette.darkVibrantSwatch?.rgb ?: palette.lightVibrantSwatch?.rgb!!
                setHeadViewBackColor()
                return
            }
            // 判断歌单是否为空
            if (mGedan.musicList.isEmpty()){
                setHeadViewBackColor()
                return
            }

            // 否则从第一首获取
//            val musicBean = mGedan.musicList.sort("musicTitle")[0]
            val musicBean = MusicBean()
            if (musicBean.isOnline) {
                loadOnlineBitmap(musicBean.bigPic)
            } else {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,
                        Uri.parse(musicBean.albumUri))
                mHeadCover.setImageBitmap(bitmap)
                val palette = Palette.from(bitmap).generate()
                mBackColor = palette.darkVibrantSwatch?.rgb ?: palette.lightVibrantSwatch?.rgb!!
                setHeadViewBackColor()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            setHeadViewBackColor()
        }
    }

    /** 加载网络封面  */
    @Throws(Exception::class)
    private fun loadOnlineBitmap(url: String) {
        OkGo.get<Bitmap>(url).execute(object : BitmapCallback() {
            override fun onSuccess(response: Response<Bitmap>?) {
                if (response?.body() != null) {
                    try {
                        mHeadCover.setImageBitmap(response.body())
                        val p =  Palette.from(response.body()).generate()
                        mBackColor =  p.darkVibrantSwatch?.rgb ?:  p.lightVibrantSwatch?.rgb!!
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    setHeadViewBackColor()
                }
            }
        })
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
        if (currentPlay != null && currentPlay.playFromListId == listId) {
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


