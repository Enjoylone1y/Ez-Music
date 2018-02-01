package com.ezreal.huanting.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.ezreal.huanting.activity.LocalMusicActivity
import com.ezreal.huanting.R
import com.ezreal.huanting.activity.RecentPlayActivity
import com.ezreal.huanting.helper.MusicDataHelper
import com.ezreal.huanting.utils.Constant
import kotlinx.android.synthetic.main.fragment_personal.*
import java.io.File
import android.view.Gravity
import android.widget.EditText
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.activity.LocalBillActivity
import com.ezreal.huanting.adapter.MusicBillAdapter
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicBillBean
import com.ezreal.huanting.event.MusicListChangeEvent
import com.ezreal.huanting.event.PlayMusicChangeEvent
import com.ezreal.huanting.utils.PopupShowUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 个人音乐页面
 * Created by wudeng on 2017/11/16.
 */
class PersonalFragment : Fragment() {

    private val mMusicList = ArrayList<MusicBillBean>()
    private var mBillAdapter: MusicBillAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        loadMyMusicList()
    }

    private fun initView() {
        mRvMyMusicList.layoutManager = LinearLayoutManager(context!!)
        mBillAdapter = MusicBillAdapter(context!!, mMusicList,true)
        mBillAdapter?.setItemClickListener(object : RecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(holder: RViewHolder, position: Int) {
                val intent = Intent(context, LocalBillActivity::class.java)
                intent.putExtra("ListId", mMusicList[position].listId)
                context?.startActivity(intent)
            }

        })
        mRvMyMusicList.adapter = mBillAdapter
        mLocalMusicCount.text = MusicDataHelper.getLocalMusicCount(context!!).toString()
        mRecentPlayCount.text = MusicDataHelper.getRecentPlayCount().toString()
        mDownLoadCount.text = File(Constant.APP_MUSIC_PATH).list().size.toString()
    }

    private fun initListener() {
        layout_loc_music.setOnClickListener {
            this.startActivity(Intent(context, LocalMusicActivity::class.java))
        }

        layout_rec_play.setOnClickListener {
            this.startActivity(Intent(context, RecentPlayActivity::class.java))
        }

        layout_down_manager.setOnClickListener {
            // TODO 打开下载管理页
        }

        mIvListSetting.setOnClickListener { view ->
            showPopupWindow(view)
        }

    }

    private fun loadMyMusicList() {
        MusicDataHelper.loadMusicListAll(object : MusicDataHelper.OnListLoadListener {
            override fun loadSuccess(bill: List<MusicBillBean>) {
                mMusicList.clear()
                mMusicList.addAll(bill)
                mBillAdapter?.notifyDataSetChanged()
                mTvMyListNum.text = mMusicList.size.toString()
            }

            override fun loadFailed(message: String) {
                FToastUtils.init().show("加载歌单失败：" + message)
            }
        })
    }

    @Subscribe
    fun onPlayMusicChange(event: PlayMusicChangeEvent) {
        mRecentPlayCount.text = MusicDataHelper.getRecentPlayCount().toString()
    }

    @Subscribe
    fun onMusicListChange(event: MusicListChangeEvent) {
        // -1 代表歌单被删除
        if (event.listId == -1L){
            loadMyMusicList()
            return
        }
        // 否则是歌单信息更新
        val changeItem = mMusicList.first { it.listId == event.listId }
        val index = mMusicList.indexOf(changeItem)
        MusicDataHelper.getMusicListById(event.listId, object : MusicDataHelper.OnListLoadListener {
            override fun loadSuccess(bill: List<MusicBillBean>) {
                if (bill.isNotEmpty()) {
                    mMusicList[index] = bill[0]
                    mBillAdapter?.notifyItemChanged(index)
                }
            }
            override fun loadFailed(message: String) {}
        })
    }

    private fun showPopupWindow(view: View) {
        val rootView = LayoutInflater.from(context)
                .inflate(R.layout.popu_list_setting, null, false)
        val popupWindow = PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.isOutsideTouchable = true
        popupWindow.animationStyle = R.style.MyPopupStyle
        popupWindow.setOnDismissListener {
            PopupShowUtils.lightOn(context as Activity)
        }
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        popupWindow.showAtLocation(view, Gravity.START or Gravity.BOTTOM,
                0, -location[1])
        PopupShowUtils.lightOff(context as Activity)

        rootView.findViewById<TextView>(R.id.mTvCreateList).setOnClickListener {
            showCreateListDialog()
            popupWindow.dismiss()
        }

        rootView.findViewById<TextView>(R.id.mTvManagerList).setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun showCreateListDialog() {
        val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_create_list,
                null, false)
        val dialog = Dialog(context)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.addContentView(rootView, layoutParams)
        dialog.setCanceledOnTouchOutside(false)
        val et = rootView.findViewById<EditText>(R.id.mEtListTitle)
        rootView.findViewById<TextView>(R.id.mTvConform).setOnClickListener {
            if (TextUtils.isEmpty(et.text.toString())) {
                FToastUtils.init().show("标题不能为空~~")
            } else {
                createMusicList(et.text.toString())
            }
            dialog.dismiss()
        }
        rootView.findViewById<TextView>(R.id.mTvCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun createMusicList(title: String) {
        MusicDataHelper.createMusicBill(title, object : MusicDataHelper.OnBillCreatedListener {
            override fun createdResult(code: Int, listId: Long, message: String) {
                if (code != 0) {
                    FToastUtils.init().show("创建失败:" + message)
                    return
                }

                FToastUtils.init().show("创建成功~~")

                MusicDataHelper.getMusicListById(listId, object : MusicDataHelper.OnListLoadListener {
                    override fun loadSuccess(bill: List<MusicBillBean>) {
                        if (bill.isNotEmpty()) {
                            mMusicList.add(bill[0])
                            mBillAdapter?.notifyDataSetChanged()
                            mTvMyListNum.text = mMusicList.size.toString()
                        }
                    }

                    override fun loadFailed(message: String) {
                        FToastUtils.init().show("读取新歌单失败：" + message)
                    }
                })
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
