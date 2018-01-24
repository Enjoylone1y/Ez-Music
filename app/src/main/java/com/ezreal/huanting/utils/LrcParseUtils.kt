package com.ezreal.huanting.utils

import android.text.TextUtils
import android.text.format.DateUtils
import java.io.*
import java.util.*
import java.util.regex.Pattern

/**
 * 歌词 文件/字符集 解析工具类
 * Created by wudeng on 2018/1/24.
 */
object LrcParseUtils {

    fun parseLrc(lrcFile: File?): List<LrcEntry>? {
        if (lrcFile == null || !lrcFile.exists()) {
            return null
        }

        val entryList = ArrayList<LrcEntry>()
        try {
            val br = BufferedReader(InputStreamReader(FileInputStream(lrcFile),
                    "utf-8"))
            var line: String?
            line = br.readLine()
            while (line != null) {
                val list = parseLine(line)
                if (list != null && !list.isEmpty()) {
                    entryList.addAll(list)
                    line = br.readLine()
                }
            }
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Collections.sort(entryList)
        return entryList
    }

    fun parseLrc(lrcText: String): List<LrcEntry>? {
        if (TextUtils.isEmpty(lrcText)) {
            return null
        }

        val entryList = ArrayList<LrcEntry>()
        val array = lrcText.split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        array.mapNotNull { parseLine(it) }
                .filterNot { it.isEmpty() }
                .forEach { entryList.addAll(it) }

        Collections.sort(entryList)
        return entryList
    }

    private fun parseLine(line: String): List<LrcEntry>? {
        var s = line
        if (TextUtils.isEmpty(s)) {
            return null
        }

        s = s.trim { it <= ' ' }
        val lineMatcher = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d\\d\\])+)(.+)").matcher(s)
        if (!lineMatcher.matches()) {
            return null
        }

        val times = lineMatcher.group(1)
        val text = lineMatcher.group(3)
        val entryList = ArrayList<LrcEntry>()

        val timeMatcher = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d\\d)\\]").matcher(times)
        while (timeMatcher.find()) {
            val min = java.lang.Long.parseLong(timeMatcher.group(1))
            val sec = java.lang.Long.parseLong(timeMatcher.group(2))
            val mil = java.lang.Long.parseLong(timeMatcher.group(3))
            val time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil * 10
            entryList.add(LrcEntry(time, text))
        }
        return entryList
    }
}