package com.ezreal.huanting.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.activity.GedanInfoActivity
import com.ezreal.huanting.activity.GedanListActivity
import com.ezreal.huanting.activity.RankBillListActivity
import com.ezreal.huanting.activity.RecomListActivity
import com.ezreal.huanting.adapter.*
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.result.HotGedanResult.ContentBean.ListBean
import com.ezreal.huanting.http.result.RecomAlbumResult.Plaze.RMBean.Album.RecomAlbumBean
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import kotlinx.android.synthetic.main.fragment_online_music.*
import kotlinx.android.synthetic.main.layout_online_banna.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 在线音乐
 * Created by wudeng on 2017/11/16.
 */
class OnlineFragment : Fragment() {

    private val mImageUrlList = ArrayList<String>()
    private val mRecomBillList = ArrayList<ListBean>()
    private val mRecomAlbumList = ArrayList<RecomAlbumBean>()

    private lateinit var mPageAdapter: AutoPageAdapter
    private lateinit var mRecomBillAdapter: GedanAdapter
    private lateinit var mRecomAlbumAdapter:RecomAlbumAdapter

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

        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd")
        val day = format.format(calendar.time)
        mTvDay.text = day

        mPageAdapter = AutoPageAdapter(mImageUrlList,mViewPage)
        mViewPage.setAdapter(mPageAdapter)

        mRcvRecomBill.layoutManager = GridLayoutManager(context!!, 3)
        mRcvRecomBill.addItemDecoration(RecyclerViewDivider.with(context!!)
                .color(android.R.color.white).size(5).build())
        mRcvRecomBill.isNestedScrollingEnabled = false
        mRcvRecomBill.setHasFixedSize(false)
        mRecomBillAdapter = GedanAdapter()
        mRcvRecomBill.adapter = mRecomBillAdapter

        mRcvRecomAlbum.layoutManager = GridLayoutManager(context, 3)
        mRcvRecomAlbum.addItemDecoration(RecyclerViewDivider.with(context!!)
                .color(android.R.color.white).size(5).build())
        mRcvRecomAlbum.isNestedScrollingEnabled = false
        mRcvRecomAlbum.setHasFixedSize(false)
        mRecomAlbumAdapter = RecomAlbumAdapter(context!!,mRecomAlbumList)
        mRcvRecomAlbum.adapter = mRecomAlbumAdapter
    }

    private fun initEvent() {
        // 打开每日推荐
        mLayoutRecomMusic.setOnClickListener {
            startActivity(Intent(context,RecomListActivity::class.java))
        }

        // 打开歌单列表
        mLayoutBill.setOnClickListener {
            startActivity(Intent(context, GedanListActivity::class.java))
        }

        // 打开歌手列表
        mLayoutArtist.setOnClickListener {
            FToastUtils.init().show("歌手列表页面还没做好喔~")
        }

        // 打开排行榜
         mLayoutRank.setOnClickListener {
             startActivity(Intent(context,RankBillListActivity::class.java))
        }

        mRecomBillAdapter.setItemClickListener(object :
                RecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(holder: RViewHolder, position: Int) {
                val bean = mRecomBillList[position]
                val intent = Intent(context, GedanInfoActivity::class.java)
                intent.putExtra("ListId",bean.listid.toLong())
                intent.putExtra("isOnline",true)
                context?.startActivity(intent)
            }
        })
    }

    private fun loadData() {
        BaiduMusicApi.loadImageUrls(5,object :BaiduMusicApi.OnUrlLoadListener{
            override fun onResult(code: Int, result: List<String>?, message: String?) {
                if (code == 0){
                    mImageUrlList.addAll(result!!)
                    mPageAdapter.notifyDataSetChanged()
                }
            }

        })

        BaiduMusicApi.loadHotBillList(6,object:BaiduMusicApi.OnHotBillLoadListener{
            override fun onResult(code: Int, result: List<ListBean>?, message: String?) {
                if (code == 0 && result != null){
                    mRecomBillList.addAll(result)
                    mRecomBillAdapter.notifyDataSetChanged()
                }
            }

        })

        BaiduMusicApi.loadRecomAlbum(0,6,object :BaiduMusicApi.OnRecomAlbumListener{
            override fun onResult(code: Int, result: List<RecomAlbumBean>?, total: Int, message: String?) {
                if (code == 0 && result != null){
                    mRecomAlbumList.addAll(result)
                    mRecomAlbumAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    inner class GedanAdapter:RecycleViewAdapter<ListBean>(context!!,mRecomBillList){
        override fun setItemLayoutId(position: Int): Int {
            return R.layout.item_diy_bill
        }

        override fun bindView(holder: RViewHolder, position: Int) {
            val bean = mRecomBillList[position]
            holder.setText(R.id.tv_bill_title,bean.title)
            holder.setText(R.id.tv_listen_count,getNumString(bean.listenum.toLong()))
            holder.setImageByUrl(context!!,R.id.iv_bill_cover,bean.pic,R.drawable.splash)
        }

        private fun getNumString(num:Long):String{
            return if (num > 10000) (num / 10000).toString() + "万" else num.toString()
        }
    }
}