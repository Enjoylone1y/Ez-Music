package com.ezreal.huanting.http.result

import com.google.gson.annotations.SerializedName

/**
 * Created by wudeng on 2018/2/5.
 */

class RecomAlbumResult {

    @SerializedName("plaze_album_list")
    var plaze: Plaze? = null

    class Plaze {
        @SerializedName("RM")
        var rm: RMBean? = null

        class RMBean {

            @SerializedName("album_list")
            var album: Album? = null

            class Album {

                var total: Int = 0
                var list = ArrayList<RecomAlbumBean>()

                class RecomAlbumBean {
                    /**
                     * album_id : 571647053
                     * title : 遗忘
                     * publishcompany : 星创世纪
                     * songs_total : 1
                     * pic_big : http://qukufile2.qianqian.com/data2/pic/16464370,h_150
                     * artist_id : 269605516
                     * all_artist_id : 269605516
                     * author : 庆庆
                     * publishtime : 2018-02-02
                     */

                    lateinit var album_id: String
                    lateinit var title: String
                    lateinit var publishcompany: String
                    lateinit var songs_total: String
                    lateinit var pic_big: String
                    lateinit var artist_id: String
                    lateinit var all_artist_id: String
                    lateinit var author: String
                    lateinit var publishtime: String
                }
            }
        }
    }
}
