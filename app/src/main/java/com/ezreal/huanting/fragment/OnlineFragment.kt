package com.ezreal.huanting.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezreal.huanting.R
import com.ezreal.huanting.http.HttpRequest
import com.ezreal.huanting.http.RecomSongBean

/**
 * 在线音乐
 * Created by wudeng on 2017/11/16.
 */
class OnlineFragment : Fragment() {

    private val mHandler = MyHandler()

    private class MyHandler: Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_online_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createViews()
    }


    private fun createViews() {
        HttpRequest.searchRecomMusic("44805341",
                6, object :HttpRequest.OnRecomSearchListener{
            override fun onResult(code: Int, result: List<RecomSongBean>?, message: String?) {
                if (code == 0 && result != null){

                }
            }
        })
    }

}