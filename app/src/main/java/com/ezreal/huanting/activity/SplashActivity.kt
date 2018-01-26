package com.ezreal.huanting.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import cn.hotapk.fastandrutils.utils.FSharedPrefsUtils
import com.ezreal.huanting.R
import com.ezreal.huanting.helper.MusicDataHelper
import com.ezreal.huanting.service.MusicPlayService
import com.ezreal.huanting.utils.Constant
import java.io.File



/**
 * 引导页
 * Created by wudeng on 2017/12/26.
 */

class SplashActivity : AppCompatActivity() {

    private val PERMIISION = listOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 全屏幕
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.layout_splash)
        initPermission()
    }

    /**
     * 检查、申请权限
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initPermission() {
        val permission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMIISION.toTypedArray(), 1)
        } else {
            initApplication()
            openMain()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        initApplication()
        openMain()
    }


    private fun initApplication() {
        // 启动音乐播放后台服务
        startService(Intent(this, MusicPlayService::class.java))
        Thread({
            // 创建程序文件夹
            if (!File(Constant.APP_MAIN_DIR_PATH).exists()) {
                File(Constant.APP_MAIN_DIR_PATH).mkdir()
            }

            if (!File(Constant.APP_MUSIC_PATH).exists()) {
                File(Constant.APP_MUSIC_PATH).mkdir()
            }

            if (!File(Constant.APP_LRC_PATH).exists()) {
                File(Constant.APP_LRC_PATH).mkdir()
            }

            if (!File(Constant.APP_IMAGE_PATH).exists()) {
                File(Constant.APP_IMAGE_PATH).mkdir()
            }

            // 创建 “我喜欢的音乐” 歌单
            val created = FSharedPrefsUtils.getBoolean(Constant.PRE_APP_OPTION_TABLE,
                    Constant.PRE_APP_OPTION_LOVE_CREATED, false)
            if (!created) {
                MusicDataHelper.createLoveList(object : MusicDataHelper.OnListCreateListener {
                    override fun createdResult(code: Int, listId: Long, message: String) {
                        if (code == 0) {
                            FSharedPrefsUtils.putBoolean(Constant.PRE_APP_OPTION_TABLE,
                                    Constant.PRE_APP_OPTION_LOVE_CREATED, true)
                        }
                    }
                })
            }

            val initSync = FSharedPrefsUtils.getBoolean(Constant.PRE_APP_OPTION_TABLE,
                    Constant.PRE_APP_OPTION_INIT_SYNC, false)
            if (!initSync) {
                // 同步本地音乐至数据库
                MusicDataHelper.syncLocalMusic(this,
                        object : MusicDataHelper.OnSyncLocalMusicListener {
                            override fun onResult(code: Int, message: String) {
                                if (code == 0){
                                    FSharedPrefsUtils.putBoolean(Constant.PRE_APP_OPTION_TABLE,
                                            Constant.PRE_APP_OPTION_INIT_SYNC, true)
                                }
                            }
                        })
            }

        }).start()
    }

    private fun openMain() {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}