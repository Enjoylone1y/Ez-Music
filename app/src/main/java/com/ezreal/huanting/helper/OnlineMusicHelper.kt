package com.ezreal.huanting.helper

import android.text.TextUtils
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.event.OnlineDownloadEvent
import com.ezreal.huanting.http.baidu.BaiduMusicApi
import com.ezreal.huanting.http.baidu.MusicSearchResult
import com.ezreal.huanting.utils.Constant
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.model.Response
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

                    // 歌曲信息
                    musicBean.bigPic = result.songinfo?.pic_big!!
                    musicBean.lrcLink = result.songinfo?.lrclink!!
                    musicBean.tingUid = result.songinfo?.ting_uid!!

                    // 播放信息
                    musicBean.fileLink = result.bitrate?.file_link!!
                    musicBean.fileSize = result.bitrate?.file_size!!
                    musicBean.duration = result.bitrate?.file_duration!! * 1000
                    listener.onResult(0,musicBean,"success")
                }else{
                    listener.onResult(-1,null,"failed")
                }
            }
        })
    }


    fun loadAndSaveLrc(musicId:Long,lrcLink:String){
        if (TextUtils.isEmpty(lrcLink)){
            EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWNLOAD_TYPE_LRC,
                    -1,null,"lrcLink is null or empty"))
            return
        }
        // 下载歌词文件
        val path = Constant.APP_LRC_PATH + File.separator + musicId + ".lrc"
        val lrcFile = File(path)
        if (lrcFile.exists()){
            EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWNLOAD_TYPE_LRC,
                    0,path,"success"))
            return
        }else{
            lrcFile.createNewFile()
        }

        OkGo.get<File>(lrcLink).execute(object :FileCallBack(path){
            override fun onSuccess(response: Response<File>?) {
                if (response?.body() != null){
                    EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWNLOAD_TYPE_LRC,
                            0,path,"success"))
                }else{
                    EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWNLOAD_TYPE_LRC,
                            -1,null,"download failed"))
                }
            }
        })
    }

    fun loadAndSavePic(musicId:Long,picLink:String){
        if (TextUtils.isEmpty(picLink)){
            EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWLOAD_TYPE_PIC,
                    -1,null,"picLink is null or empty"))
            return
        }
        // 下载封面文件
        val path = Constant.APP_IMAGE_PATH + File.separator + musicId + ".jpg"
        val picFile =  File(path)
        if (picFile.exists()){
            EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWLOAD_TYPE_PIC,
                    0,path,"success"))
            return
        }else{
            picFile.createNewFile()
        }
        OkGo.get<File>(picLink).execute(object :FileCallBack(path){
            override fun onSuccess(response: Response<File>?) {
                if (response?.body() != null){
                    EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWLOAD_TYPE_PIC,
                            0,path,"success"))
                }else{
                    EventBus.getDefault().post(OnlineDownloadEvent(Constant.DOWLOAD_TYPE_PIC,
                            -1,null,"download failed"))
                }
            }
        })
    }

    interface OnInfoLoadedListener {
         fun onResult(code: Int,musicBean: MusicBean?,message: String?)
    }

    abstract class FileCallBack(val path:String):AbsCallback<File>(){
        override fun convertResponse(response: okhttp3.Response?): File? {
            if (TextUtils.isEmpty(path)){
                return null
            }
            val file = File(path)
            if (!file.exists()){
                file.createNewFile()
            }
            val byteStream = response?.body()?.byteStream()
            if (byteStream != null){
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var length:Int
                length = byteStream.read(buffer)
                while (length != -1){
                    outputStream.write(buffer,0,length)
                    length = byteStream.read(buffer)
                }
                outputStream.flush()
                outputStream.close()
                byteStream.close()
                return file
            }else{
                return null
            }
        }
    }
}