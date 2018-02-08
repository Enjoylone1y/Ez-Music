package com.ezreal.huanting.http

import android.text.TextUtils
import com.lzy.okgo.callback.AbsCallback
import java.io.File
import java.io.FileOutputStream

/**
 * 文件下载
 * Created by wudeng on 2018/2/8.
 */

abstract class FileCallBack(val path:String): AbsCallback<File>(){

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