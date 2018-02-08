package com.ezreal.huanting.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.KeyEvent
import cn.hotapk.fastandrutils.utils.FToastUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.fragment.DynamicFragment
import com.ezreal.huanting.fragment.PersonalFragment
import com.ezreal.huanting.fragment.OnlineFragment
import com.ezreal.huanting.widget.MainMenuLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_title.*

class MainActivity : BaseActivity(){

    private val mPersonalView  by lazy { PersonalFragment() }
    private val mOnlineView  by lazy{ OnlineFragment() }
    private val mDynamicView by lazy { DynamicFragment() }

    private var mCurrentView:Fragment ?= null
    private  var mClickBackCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView(){
        mLayoutMenu.addView(MainMenuLayout(this))

        mIvMenu.setOnClickListener{
            if (drawer_layout.isDrawerOpen(mLayoutMenu)){
                drawer_layout.closeDrawer(mLayoutMenu)
            }else{
                drawer_layout.openDrawer(mLayoutMenu)
            }
        }
        mIvPersonalMusic.setOnClickListener{
            switchFragment(mPersonalView)
        }
        mIvOnlineMusic.setOnClickListener{
            switchFragment(mOnlineView)
        }
        mIvUser.setOnClickListener{
            switchFragment(mDynamicView)
        }
        mIvSearch.setOnClickListener{
            startActivity(Intent(this, GlobalSearchActivity::class.java))
        }

        // 先显示本地音乐列表
        supportFragmentManager.beginTransaction()
                .add(R.id.mLayoutContent, mPersonalView).commit()
        mCurrentView = mPersonalView
    }

    /**
     * Fragment 跳转
     */
    private fun switchFragment(fragment : Fragment){
        if (fragment != mCurrentView){
            if (fragment.isAdded){
                supportFragmentManager.beginTransaction()
                        .hide(mCurrentView).show(fragment).commit()
            }else{
                supportFragmentManager.beginTransaction()
                        .hide(mCurrentView).add(R.id.mLayoutContent,fragment).commit()
            }
            mCurrentView = fragment
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_BACK){
            mClickBackCount ++
            if(mClickBackCount == 1){
                FToastUtils.init().show("再点击一次退出~~")
                Handler().postDelayed({
                    mClickBackCount = 0
                },1500)
            }else if(mClickBackCount == 2){
                finish()
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(0)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

