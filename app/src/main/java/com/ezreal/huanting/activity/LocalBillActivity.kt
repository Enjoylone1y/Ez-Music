package com.ezreal.huanting.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.MusicAdapter
import com.ezreal.huanting.bean.MusicBillBean
import com.ezreal.huanting.helper.MusicDataHelper
import kotlinx.android.synthetic.main.activity_local_bill.*
import android.provider.MediaStore
import cn.hotapk.fastandrutils.utils.*
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.graphics.Palette
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.helper.GlobalMusicData
import com.ezreal.huanting.utils.Constant
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * 本地歌单详情页
 * Created by wudeng on 2018/1/8.
 */

class LocalBillActivity : AppCompatActivity() {

    private val mMusicList = ArrayList<MusicBean>()
    private lateinit var mAdapter: MusicAdapter
    private lateinit var mBill: MusicBillBean
    private var mBackColor = Color.parseColor("#bfbfbf")
    private var mHeadViewHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_bill)
        initActionBar()
        getMusicList()

        EventBus.getDefault().register(this)
    }

    private fun getMusicList() {
        val listId = intent.getLongExtra("ListId", -1)
        MusicDataHelper.getMusicListById(listId, object : MusicDataHelper.OnListLoadListener {
            override fun loadSuccess(bill: List<MusicBillBean>) {
                if (bill.isEmpty()) {
                    FToastUtils.init().show("读取歌单信息失败！")
                    finish()
                } else {
                    mBill = bill[0]
                    val musicList = mBill.musicList
                    mMusicList.addAll(musicList)
                    initHeadView()
                    initMusicList()
                }
            }

            override fun loadFailed(message: String) {
                FToastUtils.init().show("读取歌单信息失败！")
                finish()
            }
        })
    }

    private fun initActionBar() {
        mTvTitle.text = "歌单"
        mIvBack.setOnClickListener { finish() }
    }

    private fun initHeadView() {

        FStatusBarUtils.translucent(this)
        val list = mBill.musicList
        if (list.isEmpty()) {
            return
        }
        val albumUri = list[0].albumUri

        val bitmap = try {
            MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(albumUri))
        } catch (e: Exception) {
            e.printStackTrace()
            BitmapFactory.decodeResource(resources, R.drawable.splash)
        }

        mBackColor = if (bitmap != null)
            Palette.from(bitmap).generate().darkVibrantSwatch?.rgb
                    ?: ContextCompat.getColor(this, R.color.color_gray)
        else
            ContextCompat.getColor(this, R.color.color_gray)


        setHeadViewDrawable()

        mHeadViewHeight = FConvertUtils.dip2px(200f)
    }

    private fun setHeadViewDrawable() {
        val actionBarBitmap = Bitmap.createBitmap(resources.displayMetrics.widthPixels,
                FConvertUtils.dip2px(71f), Bitmap.Config.ARGB_8888)
        actionBarBitmap.eraseColor(mBackColor)//填充颜色
        val actionBarDrawable = BitmapDrawable(resources, actionBarBitmap)
        actionBarDrawable.mutate().alpha = 0
        mActionBar.background = actionBarDrawable
    }

    private fun initMusicList() {
        mRcvMusic.layoutManager = LinearLayoutManager(this)
        mRcvMusic.setPullRefreshEnabled(false)
        mRcvMusic.setLoadingMoreEnabled(false)
        mRcvMusic.isNestedScrollingEnabled = false
        mRcvMusic.setHasFixedSize(false)
        mRcvMusic.addHeaderView(createHeadView())
        mAdapter = MusicAdapter(this, mBill.listId, mMusicList)
        mAdapter.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(holder: RViewHolder, position: Int) {
                mAdapter.playLocalMusic(position - 2)
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
                mTvTitle.text = mBill.listName
            } else {
                mTvTitle.text = "歌单"
            }

            val drawable = mActionBar.background
                    ?: ContextCompat.getDrawable(this, R.drawable.action_bar_bg_black)
            drawable?.mutate()?.alpha = (alpha * 255).toInt()
            mActionBar.background = drawable
        }
    }

    private fun createHeadView(): View {
        val head = LayoutInflater.from(this)
                .inflate(R.layout.layout_local_bill_head, null, false)

        val headBarBitmap = Bitmap.createBitmap(resources.displayMetrics.widthPixels,
                FConvertUtils.dip2px(271f), Bitmap.Config.ARGB_8888)
        headBarBitmap.eraseColor(mBackColor)//填充颜色
        val headDrawable = BitmapDrawable(resources, headBarBitmap)
        head.background = headDrawable

        val listCover = head.findViewById<ImageView>(R.id.mIvListCover)
        val listName = head.findViewById<TextView>(R.id.mTvListName)
        val userName = head.findViewById<TextView>(R.id.mTvCreator)

        userName.text = resources.getString(R.string.app_name)
        listName.text = mBill.listName

        if (mBill.musicList.isEmpty()) {
            listCover.setImageResource(R.drawable.splash)
        } else {
            Glide.with(this)
                    .load(mBill.musicList[0]?.albumUri)
                    .asBitmap()
                    .error(R.drawable.splash)
                    .into(listCover)
        }
        return head
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
        if (currentPlay != null && currentPlay.playFromListId == mBill.listId) {
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


