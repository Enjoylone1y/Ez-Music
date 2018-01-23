package com.ezreal.huanting.event

/**
 * 播放指令事件
 */
data class PlayActionEvent(val action: MusicPlayAction,val seekTo: Int)

/**
 * 当前播放歌曲改变事件
 */
data class PlayMusicChangeEvent(val newIndex:Int)

/**
 * 当前播放列表更新事件
 */
 data class PlayListChangeEvent(val listId: Long)

/**
 * 播放状态改变事件
 */
data class PlayStatusChangeEvent(val status: Int)

/**
 * 播放进度更新事件
 */
data class PlayProcessChangeEvent(val process:Int)


/**
 * 歌单变化事件
 */
data class MusicListChangeEvent(val listId:Long)

/**
 * 播放模式变化事件
 */
data class PlayModeChangeEvent(val mode:Int)


