package com.ezreal.huanting.helper

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import cn.hotapk.fastandrutils.utils.FDeviceUtils
import cn.hotapk.fastandrutils.utils.FSharedPrefsUtils
import com.ezreal.huanting.bean.AlbumBean
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.bean.MusicBillBean
import com.ezreal.huanting.bean.RecentPlayBean
import com.ezreal.huanting.event.MusicListChangeEvent
import com.ezreal.huanting.utils.Constant
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import org.greenrobot.eventbus.EventBus

/**
 * 本地音乐加载工具类
 * Created by wudeng on 2017/11/17.
 */
object MusicDataHelper {

    fun loadMusicFromDB(listener: OnMusicLoadListener?) {
        try {
            val realm = Realm.getDefaultInstance()
            realm.where(MusicBean::class.java).findAllSortedAsync("musicTitle")
                    .addChangeListener { element ->
                        listener?.loadSuccess(element)
                    }
        } catch (e: Exception) {
            e.printStackTrace()
            listener?.loadFailed(e.message!!)
        }
    }

    fun loadRecentPlayFromDB(listener: OnMusicLoadListener?) {
        try {
            val realm = Realm.getDefaultInstance()
            val results = realm.where(RecentPlayBean::class.java)
                    .findAllSorted("lastPlayTime")
            if (results.isEmpty()) {
                listener?.loadSuccess(arrayListOf())
                return
            }
            val ids = arrayOfNulls<Long>(results.size)
            for (i in results.indices) {
                ids[i] = results[i].musicId
            }
            realm.where(MusicBean::class.java).`in`("musicId", ids)
                    .findAllAsync().addChangeListener { element ->
                listener?.loadSuccess(element)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            listener?.loadFailed(e.message!!)
        }
    }

    fun getRecentPlayCount(): Int {
        var count = 0
        val realm = Realm.getDefaultInstance()
        try {
            count = realm.where(RecentPlayBean::class.java).count().toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return count
    }

    fun addRecentPlay2DB(recentPlayBean: RecentPlayBean) {
        try {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            val findFirst = realm.where(RecentPlayBean::class.java)
                    .equalTo("musicId", recentPlayBean.musicId)
                    .findFirst()
            findFirst?.deleteFromRealm()
            realm.insert(recentPlayBean)
            val count = realm.where(RecentPlayBean::class.java).count()
            if (count > 100) {
                realm.where(RecentPlayBean::class.java).findFirst().deleteFromRealm()
            }
            realm.commitTransaction()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearRecentPlay() {
        try {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.delete(RecentPlayBean::class.java)
            realm.commitTransaction()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createMusicList(title: String, listener: OnListCreateListener?) {
        try {
            val userId = FSharedPrefsUtils.getLong(Constant.PRE_USER_TABLE,
                    Constant.PRE_USER_ID, 0)
            val userName = FSharedPrefsUtils.getString(Constant.PRE_USER_TABLE,
                    Constant.PRE_USER_NAME, "unKnow")

            val realm = Realm.getDefaultInstance()
            val exit = realm.where(MusicBillBean::class.java)
                    .beginsWith("listName", title)
                    .equalTo("creatorId", userId)
                    .count()
            if (exit > 0) {
                listener?.createdResult(-1, -1, "歌单已存在")
                realm.close()
                return
            }

            var count = realm.where(MusicBillBean::class.java).count()
            val listBean = MusicBillBean()
            listBean.listId = ++count
            listBean.listName = title
            listBean.createTime = System.currentTimeMillis()
            listBean.creatorId = userId
            listBean.creatorName = userName
            realm.beginTransaction()
            realm.insert(listBean)
            realm.commitTransaction()
            listener?.createdResult(0, listBean.listId!!, "Create Success")
            realm.close()
        } catch (e: Exception) {
            e.printStackTrace()
            listener?.createdResult(-1, -1, e.message!!)
        }
    }

    fun deleteMusicList(listId: Long) {
        val instance = Realm.getDefaultInstance()
        instance.beginTransaction()
        instance.where(MusicBillBean::class.java)
                .equalTo("listId", listId)
                .findFirst().deleteFromRealm()
        instance.commitTransaction()
    }

    fun loadMusicListAll(listener: OnListLoadListener?) {
        try {
            val realm = Realm.getDefaultInstance()
            val realmResults = realm.where(MusicBillBean::class.java).findAll()
            listener?.loadSuccess(realmResults)
        } catch (e: Exception) {
            e.printStackTrace()
            listener?.loadFailed(e.message!!)
        }
    }

    fun getMusicListById(listId: Long, listener: OnListLoadListener?) {
        try {
            val realm = Realm.getDefaultInstance()
            val results = realm.where(MusicBillBean::class.java)
                    .equalTo("listId", listId)
                    .findAll()
            listener?.loadSuccess(results)
        } catch (e: Exception) {
            e.printStackTrace()
            listener?.loadFailed(e.message!!)
        }
    }

    fun createLoveList(listener: OnListCreateListener?) {
        try {
            val listBean = MusicBillBean()
            listBean.listId = Constant.MY_LOVE_MUSIC_LIST_ID
            listBean.listName = "我喜欢的音乐"
            listBean.createTime = System.currentTimeMillis()
            val userId = FSharedPrefsUtils.getLong(Constant.PRE_USER_TABLE,
                    Constant.PRE_USER_ID, 0)
            val userName = FSharedPrefsUtils.getString(Constant.PRE_USER_TABLE,
                    Constant.PRE_USER_NAME, "unKnow")
            listBean.creatorId = userId
            listBean.creatorName = userName
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.insertOrUpdate(listBean)
            realm.commitTransaction()
            realm.close()
            listener?.createdResult(0, Constant.MY_LOVE_MUSIC_LIST_ID, "Create Success")
        } catch (e: Exception) {
            e.printStackTrace()
            listener?.createdResult(-1, -1, e.message!!)
        }
    }

    fun addMusic2List(musicBean: MusicBean, listId: Long, listener: OnAddMusic2ListListener?) {
        try {
            val realm = Realm.getDefaultInstance()
            val results = realm.where(MusicBillBean::class.java)
                    .equalTo("listId", listId).findFirst()
            val findFirst = results.musicList.where().equalTo("musicId",
                    musicBean.musicId).findFirst()
            if (findFirst != null) {
                listener?.addResult(-1, "歌单中已包含该歌曲~")
                return
            }
            realm.beginTransaction()
            results.musicList.add(musicBean)
            realm.commitTransaction()
            listener?.addResult(0, "收藏成功~~")
            EventBus.getDefault().post(MusicListChangeEvent(listId))
        } catch (e: Exception) {
            e.printStackTrace()
            listener?.addResult(-1, "添加发生错误,请重试")
        }

    }

    fun syncLocalMusic(context: Context, listener: OnSyncLocalMusicListener?) {
        val songList = ArrayList<MusicBean>()
        try {
            val filterSize = 500 * 1024 // 500 k
            val filterTime = 60 * 1000 // 1 min
            val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Audio.AudioColumns._ID,
                            MediaStore.Audio.AudioColumns.IS_MUSIC,
                            MediaStore.Audio.AudioColumns.TITLE,
                            MediaStore.Audio.AudioColumns.ARTIST,
                            MediaStore.Audio.AudioColumns.ALBUM,
                            MediaStore.Audio.AudioColumns.ALBUM_ID,
                            MediaStore.Audio.AudioColumns.DATA,
                            MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                            MediaStore.Audio.AudioColumns.SIZE,
                            MediaStore.Audio.AudioColumns.DURATION
                    ), MediaStore.Audio.AudioColumns.SIZE + " >= ? and "
                    + MediaStore.Audio.AudioColumns.DURATION + " >= ?",
                    arrayOf(filterSize.toString(), filterTime.toString()), null)
            while (cursor.moveToNext()) {
                val isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio
                        .AudioColumns.IS_MUSIC))
                if (!FDeviceUtils.isFlyme() && isMusic == 0) continue
                val displayName = cursor.getString(cursor.getColumnIndex(MediaStore
                        .Audio.AudioColumns.DISPLAY_NAME))
                if (!displayName.endsWith(".mp3")) continue
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                val albumID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA))
                val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE))
                val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION))
                val albumUri = getAlbumCoverUri(albumID)
                val music = MusicBean()
                music.musicId = id
                music.musicTitle = title
                music.artistName = artist
                music.albumName = album
                music.albumId = albumID
                music.albumUri = albumUri
                music.lrcPath = ""
                music.duration = duration
                music.fileSize = size
                music.filePath = path
                music.playStatus = Constant.PLAY_STATUS_NORMAL
                songList.add(music)
            }
            cursor.close()
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.insertOrUpdate(songList)
            realm.commitTransaction()
            realm.close()
            listener?.onResult(0, "success")
        } catch (e: Exception) {
            e.printStackTrace()
            listener?.onResult(-1, e.message!!)
        }
    }


    fun getLocalMusicCount(context: Context): Int {
        var count = 0
        try {
            val filterSize = 500 * 1024 // 500 k
            val filterTime = 60 * 1000 // 1 min
            val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Audio.AudioColumns.IS_MUSIC,
                            MediaStore.Audio.AudioColumns.DISPLAY_NAME
                    ), MediaStore.Audio.AudioColumns.SIZE + " >= ? and "
                    + MediaStore.Audio.AudioColumns.DURATION + " >= ?",
                    arrayOf(filterSize.toString(), filterTime.toString()), null)
            while (cursor.moveToNext()) {
                val isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC))
                if (!FDeviceUtils.isFlyme() && isMusic == 0) continue
                val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio
                        .AudioColumns.DISPLAY_NAME))
                if (!displayName.endsWith(".mp3")) continue
                count++
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return count
    }


    fun loadAllArtistList(context: Context, listener: OnArtistLoadListener) {
        Observable.create(ObservableOnSubscribe<ArrayList<String>> { em ->
            try {
                val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Audio.AudioColumns._ID,
                                MediaStore.Audio.AudioColumns.IS_MUSIC,
                                MediaStore.Audio.AudioColumns.ARTIST
                        ), null, null, null)
                val artistList = ArrayList<String>()
                while (cursor.moveToNext()) {
                    val isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC))
                    if (isMusic == 0) continue
                    val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                    artistList.add(artist)
                }
                cursor.close()
                em.onNext(artistList)
            } catch (e: Exception) {
                Log.e(MusicDataHelper.javaClass.name, e.message)
                em.onError(Throwable(e.message))
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> listener.loadSuccess(result) },
                        { error -> listener.loadFailed(error.message!!) }
                )
    }

    fun loadAllAlbumList(context: Context, listener: OnAlbumLoadListener) {
        Observable.create(ObservableOnSubscribe<ArrayList<AlbumBean>> { em ->
            try {
                val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Audio.AudioColumns._ID,
                                MediaStore.Audio.AudioColumns.IS_MUSIC,
                                MediaStore.Audio.AudioColumns.ALBUM_ID,
                                MediaStore.Audio.AudioColumns.ALBUM
                        ), null, null, null)
                val artistList = ArrayList<AlbumBean>()
                var album: AlbumBean?
                while (cursor.moveToNext()) {
                    val isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC))
                    if (isMusic == 0) continue
                    val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
                    val albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                    album = AlbumBean(albumId, albumName)
                    artistList.add(album)
                }
                cursor.close()
                em.onNext(artistList)
            } catch (e: Exception) {
                em.onError(Throwable(e.message))
            }

        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> listener.loadSuccess(result) },
                        { error -> listener.loadFailed(error.message!!) }
                )
    }

    private fun getAlbumCoverUri(albumId: Long):String{
        return "content://media/external/audio/albumart/" + albumId
    }

    interface OnAlbumLoadListener {
        fun loadSuccess(albumList: List<AlbumBean>)
        fun loadFailed(message: String)
    }

    interface OnArtistLoadListener {
        fun loadSuccess(artistList: List<String>)
        fun loadFailed(message: String)
    }

    interface OnMusicLoadListener {
        fun loadSuccess(musicList: List<MusicBean>)
        fun loadFailed(message: String)
    }

    interface OnListCreateListener {
        fun createdResult(code: Int, listId: Long, message: String)
    }

    interface OnListLoadListener {
        fun loadSuccess(bill: List<MusicBillBean>)
        fun loadFailed(message: String)
    }

    interface OnAddMusic2ListListener {
        fun addResult(code: Int, message: String)
    }

    interface OnSyncLocalMusicListener {
        fun onResult(code: Int, message: String)
    }
}
