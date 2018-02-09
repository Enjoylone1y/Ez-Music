package com.ezreal.huanting.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.result.GedanInfoResult
import com.ezreal.huanting.http.result.GedanListResult
import com.ezreal.huanting.http.result.HotGedanResult.ContentBean.ListBean
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.BitmapCallback
import com.lzy.okgo.model.Response
import com.zhouwei.blurlibrary.EasyBlur
import kotlinx.android.synthetic.main.activity_gedan_list.*
import java.util.*

/**
 * 歌单列表
 * Created by wudeng on 2018/2/7.
 */

class GedanListActivity : BaseActivity() {

    private lateinit var mHeadView: View
    private lateinit var mHeadBack:ImageView
    private lateinit var mHeadCover: ImageView
    private lateinit var mHeadTip: TextView
    private lateinit var mHeadTitle: TextView
    private lateinit var mHeadDes: TextView

    private val mGedanList = ArrayList<GedanListResult.GedanBean>()
    private lateinit var mGedanAdapter: GedanListAdapter
    private var mPageNo = 1
    private var mSelectedTag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gedan_list)
        initView()
        initEvent()
        loadRecomGedan()
        loadGedanList()
    }

    private fun initView() {
        createHeadView()
        mRecGedanList.layoutManager = GridLayoutManager(this, 2)
        mRecGedanList.setPullRefreshEnabled(false)
        mRecGedanList.setLoadingMoreEnabled(false)
        mGedanAdapter = GedanListAdapter()
        mGedanAdapter.setItemClickListener(object :RecycleViewAdapter.OnItemClickListener{
            override fun onItemClick(holder: RViewHolder, position: Int) {
                val bean = mGedanList[position - 2]
                val intent = Intent(this@GedanListActivity,
                        GedanInfoActivity::class.java)
                intent.putExtra("ListId",bean.listid.toLong())
                intent.putExtra("isOnline",true)
                startActivity(intent)
            }
        })
        mRecGedanList.adapter = mGedanAdapter
    }

    private fun initEvent(){
        mIvBack.setOnClickListener { finish() }
        mRecGedanList.setLoadingListener(object :XRecyclerView.LoadingListener{
            override fun onLoadMore() {
                mPageNo++
                loadGedanList()
            }

            override fun onRefresh() {

            }
        })
    }

    private fun createHeadView() {
        mHeadView = LayoutInflater.from(this).inflate(R.layout.layout_gedan_list_head,
                null, false)
        mHeadCover = mHeadView.findViewById(R.id.mIvHeadCover)
        mHeadBack = mHeadView.findViewById(R.id.mIvHeadBack)
        mHeadTip = mHeadView.findViewById(R.id.mTvHeadTip)
        mHeadTitle = mHeadView.findViewById(R.id.mTvHeadTitle)
        mHeadDes = mHeadView.findViewById(R.id.mTvHeadDes)
        mRecGedanList.addHeaderView(mHeadView)
    }

    private fun loadRecomGedan(){
        try {
            BaiduMusicApi.loadHotBillList(1,object :BaiduMusicApi.OnHotBillLoadListener{
                override fun onResult(code: Int, result: List<ListBean>?, message: String?) {
                    if (code == 0 && result != null){
                        loadHeadInfo(result[0].listid.toLong())
                    }
                }
            })
        }catch (e:Exception){
            e.printStackTrace()
            FToastUtils.init().show("推荐歌单获取失败~")
        }
    }

    private fun loadHeadInfo(listId:Long){
        BaiduMusicApi.loadGedanInfo(listId,object :BaiduMusicApi.OnGedanInfoListener{
            override fun onResult(code: Int, result: GedanInfoResult?, message: String?) {
                if (code == 0 && result != null){
                    mHeadTitle.text = result.title
                    mHeadDes.text = result.desc
                    loadOnlineBitmap(result.pic)
                }
            }
        })
    }

    /** 加载网络封面  */
    private fun loadOnlineBitmap(url: String) {
        OkGo.get<Bitmap>(url).execute(object : BitmapCallback() {
            override fun onSuccess(response: Response<Bitmap>?) {
                if (response?.body() != null) {
                    mHeadCover.setImageBitmap(response.body())
                    val blurBitmap = EasyBlur.with(this@GedanListActivity)
                            .bitmap(response.body()) //要模糊的图片
                            .radius(10)//模糊半径
                            .scale(8)//指定模糊前缩小的倍数
                            .blur()
                    mHeadBack.setImageBitmap(blurBitmap)
                }
            }
        })
    }

    private fun loadGedanList() {
        if (TextUtils.isEmpty(mSelectedTag)) {
            BaiduMusicApi.loadAllGedan(mPageNo, object : BaiduMusicApi.OnGedanListListener {
                override fun onResult(code: Int, result: List<GedanListResult.GedanBean>?,
                                      total: Int, message: String?) {
                    if (code == 0 && result != null) {
                        mGedanList.addAll(result)
                        mGedanAdapter.notifyDataSetChanged()
                        if (mGedanList.size < total){
                            mRecGedanList.setLoadingMoreEnabled(true)
                        }
                    }else{
                        mPageNo--
                    }
                    mRecGedanList.loadMoreComplete()
                }
            })
        } else {
            BaiduMusicApi.loadGedanByTag(mPageNo, mSelectedTag, object :
                    BaiduMusicApi.OnGedanListListener {
                override fun onResult(code: Int, result: List<GedanListResult.GedanBean>?,
                                      total: Int, message: String?) {
                    if (code == 0 && result != null) {
                        mGedanList.addAll(result)
                        mGedanAdapter.notifyDataSetChanged()
                    }else{
                        mPageNo--
                    }
                    mRecGedanList.loadMoreComplete()
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null){
            val tag = data.getStringExtra("TAG")
            if (tag == mSelectedTag){
                return
            }
            mSelectedTag = tag
            mPageNo = 1
            mGedanList.clear()
            mGedanAdapter.notifyDataSetChanged()
            loadGedanList()
        }
    }

    inner class GedanListAdapter :
            RecycleViewAdapter<GedanListResult.GedanBean>(this, mGedanList) {
        override fun setItemLayoutId(position: Int): Int {
            return R.layout.item_gedan
        }

        override fun bindView(holder: RViewHolder, position: Int) {
            val bean = mGedanList[position]
            holder.setText(R.id.tv_bill_title, bean.title)
            holder.setText(R.id.tv_listen_count, getNumString(bean.listenum.toLong()))
            holder.setImageByUrl(this@GedanListActivity,
                    R.id.iv_bill_cover, bean.pic, R.drawable.splash)
        }

        private fun getNumString(num: Long): String {
            return if (num > 10000) (num / 10000).toString() + "万" else num.toString()
        }
    }
}