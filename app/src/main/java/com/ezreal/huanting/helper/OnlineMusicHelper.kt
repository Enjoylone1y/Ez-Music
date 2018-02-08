package com.ezreal.huanting.helper

import android.text.TextUtils
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.OnlineDownloadEvent
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.FileCallBack
import com.ezreal.huanting.http.result.KeywordSearchResult
import com.ezreal.huanting.http.result.MusicSearchResult
import com.ezreal.huanting.utils.Constant
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.model.Response
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream

/**
 * 网络歌曲播放工具类
 * Created by wudeng on 2018/1/31.
 */

object OnlineMusicHelper {

    fun loadAndSaveInfo(musicId: String,listener: OnInfoLoadedListener){
        BaiduMusicApi.searchMusicInfoById(musicId, object :
                BaiduMusicApi.OnMusicInfoSearchListener {
            override fun onResult(code: Int, result: MusicSearchResult?, message: String?) {
                if (code == 0 && result != null){
                    val musicBean = MusicBean()
                    musicBean.isOnline = true
                    // 基本信息
                    musicBean.musicId = result.songinfo?.song_id?.toLong()!!
                    musicBean.musicTitle = result.songinfo?.title!!
                    musicBean.artistName = result.songinfo?.author!!
                    musicBean.albumName = result.songinfo?.album_title!!
                    musicBean.albumId = result.songinfo?.album_id?.toLong()!!

                    // 歌曲信息
                    musicBean.bigPic = result.songinfo?.pic_big!!
                    musicBean.lrcLink = result.songinfo?.lrclink!!
                    musicBean.tingUid = result.songinfo?.ting_uid!!

                    // 播放信息
                    musicBean.fileLink = result.bitrate?.file_link!!
                    musicBean.fileSize = result.bitrate?.file_size!!
                    musicBean.duration = result.bitrate?.file_duration!! * 1000

                    // 辅助信息
                    musicBean.playStatus = Constant.PLAY_STATUS_NORMAL
                    musicBean.playCount = 0
                    musicBean.lastPlayTime = 0

                    listener.onResult(0,musicBean,"success")
                }else{
                    listener.onResult(-1,null,"failed")
                }
            }
        })
    }


    fun loadAndSaveLrc(music:MusicBean){
        if (TextUtils.isEmpty(music.lrcLink)){
            EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWNLOAD_TYPE_LRC,
                    -1,null,"lrcLink is null or empty"))
            return
        }
        val realm = Realm.getDefaultInstance()
        val path = Constant.APP_LRC_PATH + File.separator + music.musicId + ".lrc"
        val lrcFile = File(path)
        if (lrcFile.exists()){
            EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWNLOAD_TYPE_LRC,
                    0,path,"success"))
            realm.beginTransaction()
            music.lrcLocal = path
            realm.commitTransaction()
            return
        }else{
            lrcFile.createNewFile()
        }

        // 下载歌词文件
        OkGo.get<File>(music.lrcLink).execute(object : FileCallBack(path){
            override fun onSuccess(response: Response<File>?) {
                if (response?.body() != null){
                    EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWNLOAD_TYPE_LRC,
                            0,path,"success"))
                    realm.beginTransaction()
                    music.lrcLocal = path
                    realm.commitTransaction()
                }else{
                    EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWNLOAD_TYPE_LRC,
                            -1,null,"download failed"))
                }
            }
        })
    }

    /**
     * 下载并保存封面
     */
    fun loadAndSavePic(music:MusicBean){
        if (TextUtils.isEmpty(music.bigPic)){
            EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWLOAD_TYPE_PIC,
                    -1,null,"picLink is null or empty"))
            return
        }
        val realm = Realm.getDefaultInstance()
        // 下载封面文件
        val path = Constant.APP_IMAGE_PATH + File.separator + music.musicId + ".jpg"
        val picFile =  File(path)
        if (picFile.exists()){
            EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWLOAD_TYPE_PIC,
                    0,path,"success"))
            realm.beginTransaction()
            music.picLocal = path
            realm.commitTransaction()
            return
        }else{
            picFile.createNewFile()
        }
        OkGo.get<File>(music.bigPic).execute(object :FileCallBack(path){
            override fun onSuccess(response: Response<File>?) {
                if (response?.body() != null){
                    EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWLOAD_TYPE_PIC,
                            0,path,"success"))
                    realm.beginTransaction()
                    music.picLocal = path
                    realm.commitTransaction()
                }else{
                    EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWLOAD_TYPE_PIC,
                            -1,null,"download failed"))
                }
            }
        })
    }


    private fun getRecomBaseId() {
        var mBaseId = ""
        // 获取推荐音乐 以数据库中歌曲播放次数为基准
        val realm = Realm.getDefaultInstance()
        val last = realm.where(MusicBean::class.java)
                .equalTo("isOnline", true)
                .findAllSorted("playCount")
                .lastOrNull()

        if (last != null) {
            mBaseId = last.musicId.toString()
            return
        }
        val lastLocal = realm.where(MusicBean::class.java)
                .equalTo("isOnline", false)
                .findAllSorted("playCount")
                .lastOrNull()
        if (lastLocal == null || lastLocal.playCount == 0L) {
            mBaseId = "74172066"
            return
        }


    }
    interface OnInfoLoadedListener {
         fun onResult(code: Int,musicBean: MusicBean?,message: String?)
    }
}