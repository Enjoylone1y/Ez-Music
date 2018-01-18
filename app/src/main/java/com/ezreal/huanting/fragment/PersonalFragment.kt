package com.ezreal.huanting.fragment

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
import com.ezreal.huanting.activity.MusicListActivity
import com.ezreal.huanting.adapter.MusicListAdapter
import com.ezreal.huanting.adapter.RViewHolder
import com.ezreal.huanting.adapter.RecycleViewAdapter
import com.ezreal.huanting.bean.MusicListBean
import com.ezreal.huanting.event.MusicListChangeEvent
import com.ezreal.huanting.event.PlayMusicChangeEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 个人音乐页面
 * Created by wudeng on 2017/11/16.
 */
class PersonalFragment : Fragment() {

    private val mMusicList = ArrayList<MusicListBean>()
    private var mListAdapter: MusicListAdapter  ?= null

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
        mListAdapter = MusicListAdapter(context!!,mMusicList)
        mListAdapter?.setItemClickListener(object :RecycleViewAdapter.OnItemClickListener{
            override fun onItemClick(holder: RViewHolder, position: Int) {
                val intent = Intent(context,MusicListActivity::class.java)
                intent.putExtra("ListId",mMusicList[position].listId)
                context?.startActivity(intent)
            }

        })
        mRvMyMusicList.adapter = mListAdapter
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
            override fun loadSuccess(list: List<MusicListBean>) {
                mMusicList.addAll(list)
                mListAdapter?.notifyDataSetChanged()
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
    fun onMusicListChange(event:MusicListChangeEvent){
        val changeItem = mMusicList.first { it.listId == event.listId }
        val index = mMusicList.indexOf(changeItem)
        MusicDataHelper.getMusicListById(event.listId,object :MusicDataHelper.OnListLoadListener{
            override fun loadSuccess(list: List<MusicListBean>) {
                if (list.isNotEmpty()){
                    mMusicList[index] = list[0]
                    mListAdapter?.notifyItemChanged(index)
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
            lightOn()
        }
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        popupWindow.showAtLocation(view, Gravity.START or Gravity.BOTTOM,
                0, -location[1])
        lightOff()

        rootView.findViewById<TextView>(R.id.mTvCreateList).setOnClickListener {
            popupWindow.dismiss()
            showCreateListDialog()
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
        rootView.findViewById<TextView>(R.id.mTvCancel).setOnClickListener {
            dialog.dismiss()
        }
        rootView.findViewById<TextView>(R.id.mTvConform).setOnClickListener {
            val title = et.text.toString()
            if (TextUtils.isEmpty(title)) {
                FToastUtils.init().show("标题不能为空~~")
            } else {
                MusicDataHelper.createMusicList(title, object :
                        MusicDataHelper.OnListCreateListener {
                    override fun createdResult(code: Int, listId: Long, message: String) {
                        if (code == 0) {
                            FToastUtils.init().show("创建成功~~")
                            MusicDataHelper.getMusicListById(listId, object :
                                    MusicDataHelper.OnListLoadListener {
                                override fun loadSuccess(list: List<MusicListBean>) {
                                     if (list.isNotEmpty()){
                                         mMusicList.add(list[0])
                                         mListAdapter?.notifyDataSetChanged()
                                         mTvMyListNum.text = mMusicList.size.toString()
                                     }
                                }

                                override fun loadFailed(message: String) {
                                    FToastUtils.init().show("读取新歌单失败：" + message)
                                }
                            })
                        } else {
                            FToastUtils.init().show("创建失败：" + message)
                        }
                    }
                })
                dialog.dismiss()
            }
        }
        dialog.show()

    }

    private fun lightOn() {
        try {
            val attributes = activity?.window?.attributes
            attributes?.alpha = 1.0f
            activity?.window?.attributes = attributes
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun lightOff() {
        try {
            val attributes = activity?.window?.attributes
            attributes?.alpha = 0.6f
            activity?.window?.attributes = attributes
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
