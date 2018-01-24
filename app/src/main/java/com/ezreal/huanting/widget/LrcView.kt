package com.ezreal.huanting.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller

import com.ezreal.huanting.R
import com.ezreal.huanting.utils.LrcEntry
import com.ezreal.huanting.utils.LrcParseUtils

import java.io.File
import java.util.ArrayList
import java.util.Collections
import java.util.Locale


/**
 * 歌词
 * Created by wcy on 2015/11/9.
 */
class LrcView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    private val mLrcEntryList = ArrayList<LrcEntry>()
    private val mLrcPaint = TextPaint()
    private val mTimePaint = TextPaint()
    private var mTimeFontMetrics: Paint.FontMetrics? = null
    private var mPlayDrawable: Drawable? = null
    private var mDividerHeight: Float = 0.toFloat()
    private var mAnimationDuration: Long = 0
    private var mNormalTextColor: Int = 0
    private var mCurrentTextColor: Int = 0
    private var mTimelineTextColor: Int = 0
    private var mTimelineColor: Int = 0
    private var mTimeTextColor: Int = 0
    private var mDrawableWidth: Int = 0
    private var mTimeTextWidth: Int = 0
    private var mDefaultLabel: String? = null
    private var mLrcPadding: Float = 0.toFloat()
    private var mOnPlayClickListener: OnPlayClickListener? = null
    private var mAnimator: ValueAnimator? = null
    private var mGestureDetector: GestureDetector? = null
    private var mScroller: Scroller? = null
    private var mOffset: Float = 0.toFloat()
    private var mCurrentLine: Int = 0
    private var flag: Any? = null
    private var isShowTimeline: Boolean = false
    private var isTouching: Boolean = false
    private var isFling: Boolean = false

    private val ADJUST_DURATION: Long = 100
    private val TIMELINE_KEEP_TIME = 4 * DateUtils.SECOND_IN_MILLIS
    private var mStaticLayout: StaticLayout? = null

    private val mSimpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            if (hasLrc() && mOnPlayClickListener != null) {
                mScroller!!.forceFinished(true)
                removeCallbacks(hideTimelineRunnable)
                isTouching = true
                isShowTimeline = true
                invalidate()
                return true
            }
            return super.onDown(e)
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (hasLrc()) {
                mOffset += -distanceY
                mOffset = Math.min(mOffset, getOffset(0))
                mOffset = Math.max(mOffset, getOffset(mLrcEntryList.size - 1))
                invalidate()
                return true
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (hasLrc()) {
                mScroller!!.fling(0, mOffset.toInt(), 0, velocityY.toInt(), 0, 0, getOffset(mLrcEntryList.size - 1).toInt(), getOffset(0).toInt())
                isFling = true
                return true
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (hasLrc() && isShowTimeline && mPlayDrawable!!.bounds.contains(e.x.toInt(), e.y.toInt())) {
                val centerLine = centerLine
                val centerLineTime = mLrcEntryList[centerLine].time
                // onPlayClick 消费了才更新 UI
                if (mOnPlayClickListener != null && mOnPlayClickListener!!.onPlayClick(centerLineTime)) {
                    isShowTimeline = false
                    removeCallbacks(hideTimelineRunnable)
                    mCurrentLine = centerLine
                    invalidate()
                    return true
                }
            }
            return super.onSingleTapConfirmed(e)
        }
    }

    private val hideTimelineRunnable = Runnable {
        if (hasLrc() && isShowTimeline) {
            isShowTimeline = false
            scrollTo(mCurrentLine)
        }
    }

    private val centerLine: Int
        get() {
            var centerLine = 0
            var minDistance = java.lang.Float.MAX_VALUE
            for (i in mLrcEntryList.indices) {
                if (Math.abs(mOffset - getOffset(i)) < minDistance) {
                    minDistance = Math.abs(mOffset - getOffset(i))
                    centerLine = i
                }
            }
            return centerLine
        }

    private val lrcWidth: Float
        get() = width - mLrcPadding * 2

    /**
     * 播放按钮点击监听器，点击后应该跳转到指定播放位置
     */
    interface OnPlayClickListener {
        /**
         * 播放按钮被点击，应该跳转到指定播放位置
         *
         * @return 是否成功消费该事件，如果成功消费，则会更新UI
         */
        fun onPlayClick(time: Long): Boolean
    }

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LrcView)
        val lrcTextSize = ta.getDimension(R.styleable.LrcView_lrcTextSize, resources.getDimension(R.dimen.lrc_text_size))
        mDividerHeight = ta.getDimension(R.styleable.LrcView_lrcDividerHeight, resources.getDimension(R.dimen.lrc_divider_height))
        val defDuration = resources.getInteger(R.integer.lrc_animation_duration)
        mAnimationDuration = ta.getInt(R.styleable.LrcView_lrcAnimationDuration, defDuration).toLong()
        mAnimationDuration = if (mAnimationDuration < 0) defDuration.toLong() else mAnimationDuration
        mNormalTextColor = ta.getColor(R.styleable.LrcView_lrcNormalTextColor,
                ContextCompat.getColor(context, R.color.lrc_normal_text_color))
        mCurrentTextColor = ta.getColor(R.styleable.LrcView_lrcCurrentTextColor,
                ContextCompat.getColor(context, R.color.lrc_current_text_color))
        mTimelineTextColor = ta.getColor(R.styleable.LrcView_lrcTimelineTextColor,
                ContextCompat.getColor(context, R.color.lrc_timeline_text_color))
        mDefaultLabel = ta.getString(R.styleable.LrcView_lrcLabel)
        mDefaultLabel = if (TextUtils.isEmpty(mDefaultLabel)) context.getString(R.string.lrc_label) else mDefaultLabel
        mLrcPadding = ta.getDimension(R.styleable.LrcView_lrcPadding, 0f)
        mTimelineColor = ta.getColor(R.styleable.LrcView_lrcTimelineColor,
                ContextCompat.getColor(context, R.color.lrc_timeline_color))
        val timelineHeight = ta.getDimension(R.styleable.LrcView_lrcTimelineHeight, resources.getDimension(R.dimen.lrc_timeline_height))
        mPlayDrawable = ta.getDrawable(R.styleable.LrcView_lrcPlayDrawable) ?: ContextCompat.getDrawable(context, R.drawable.lrc_play)
        mTimeTextColor = ta.getColor(R.styleable.LrcView_lrcTimeTextColor,
                ContextCompat.getColor(context, R.color.lrc_time_text_color))
        val timeTextSize = ta.getDimension(R.styleable.LrcView_lrcTimeTextSize, resources.getDimension(R.dimen.lrc_time_text_size))
        ta.recycle()

        mDrawableWidth = resources.getDimension(R.dimen.lrc_drawable_width).toInt()
        mTimeTextWidth = resources.getDimension(R.dimen.lrc_time_width).toInt()

        mLrcPaint.isAntiAlias = true
        mLrcPaint.textSize = lrcTextSize
        mLrcPaint.textAlign = Paint.Align.LEFT
        mTimePaint.isAntiAlias = true
        mTimePaint.textSize = timeTextSize
        mTimePaint.textAlign = Paint.Align.CENTER

        mTimePaint.strokeWidth = timelineHeight
        mTimePaint.strokeCap = Paint.Cap.ROUND
        mTimeFontMetrics = mTimePaint.fontMetrics

        mGestureDetector = GestureDetector(context, mSimpleOnGestureListener)
        mGestureDetector!!.setIsLongpressEnabled(false)
        mScroller = Scroller(context)

        mStaticLayout = StaticLayout(mDefaultLabel, mLrcPaint, resources.displayMetrics.widthPixels,
                Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)

    }

    @SuppressLint("StaticFieldLeak")
    fun loadLrc(lrcFile: File) {
        reset()
        flag = lrcFile
        object : AsyncTask<File, Int, List<LrcEntry>>() {
            override fun doInBackground(vararg params: File): List<LrcEntry>? {
                return LrcParseUtils.parseLrc(params[0])
            }

            override fun onPostExecute(lrcEntries: List<LrcEntry>) {
                if (flag === lrcFile) {
                    onLrcLoaded(lrcEntries)
                    flag = null
                }
            }
        }.execute(lrcFile)
    }


    @SuppressLint("StaticFieldLeak")
    fun loadLrc(lrcText: String) {
        reset()
        flag = lrcText
        object : AsyncTask<String, Int, List<LrcEntry>>() {
            override fun doInBackground(vararg params: String): List<LrcEntry> {
                return LrcParseUtils.parseLrc(params[0])!!
            }

            override fun onPostExecute(lrcEntries: List<LrcEntry>) {
                if (flag === lrcText) {
                    onLrcLoaded(lrcEntries)
                    flag = null
                }
            }
        }.execute(lrcText)
    }

    fun hasLrc(): Boolean {
        return !mLrcEntryList.isEmpty()
    }

    fun updateTime(time: Long) {
        if (!hasLrc()) {
            return
        }
        val line = findShowLine(time)
        if (line != mCurrentLine) {
            mCurrentLine = line
            if (!isShowTimeline) {
                scrollTo(line)
            } else {
                invalidate()
            }
        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            initEntryList()
            val l = (mTimeTextWidth - mDrawableWidth) / 2
            val t = height / 2 - mDrawableWidth / 2
            val r = l + mDrawableWidth
            val b = t + mDrawableWidth
            mPlayDrawable!!.setBounds(l, t, r, b)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerY = height / 2
        // 无歌词文件
        if (!hasLrc()) {
            mLrcPaint.color = mCurrentTextColor
            drawText(canvas, mStaticLayout, centerY.toFloat())
            return
        }

        val centerLine = centerLine

        if (isShowTimeline) {
            mPlayDrawable!!.draw(canvas)

            mTimePaint.color = mTimelineColor
            canvas.drawLine(mTimeTextWidth.toFloat(), centerY.toFloat(), (width - mTimeTextWidth).toFloat(), centerY.toFloat(), mTimePaint)

            mTimePaint.color = mTimeTextColor
            val timeText = formatTime(mLrcEntryList[centerLine].time)
            val timeX = (width - mTimeTextWidth / 2).toFloat()
            val timeY = centerY - (mTimeFontMetrics!!.descent + mTimeFontMetrics!!.ascent) / 2
            canvas.drawText(timeText, timeX, timeY, mTimePaint)
        }

        canvas.translate(0f, mOffset)

        var y = 0f
        for (i in mLrcEntryList.indices) {
            if (i > 0) {
                y += (mLrcEntryList[i - 1].height + mLrcEntryList[i].height) / 2 + mDividerHeight
            }
            if (i == mCurrentLine) {
                mLrcPaint.color = mCurrentTextColor
            } else if (isShowTimeline && i == centerLine) {
                mLrcPaint.color = mTimelineTextColor
            } else {
                mLrcPaint.color = mNormalTextColor
            }
            drawText(canvas, mLrcEntryList[i].staticLayout, y)
        }
    }

    /**
     * 画一行歌词
     *
     * @param y 歌词中心 Y 坐标
     */
    private fun drawText(canvas: Canvas, staticLayout: StaticLayout?, y: Float) {
        canvas.save()
        canvas.translate(mLrcPadding, y - staticLayout!!.height / 2)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            isTouching = false
            if (hasLrc() && !isFling) {
                adjustCenter()
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME)
            }
        }
        return mGestureDetector!!.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            mOffset = mScroller!!.currY.toFloat()
            invalidate()
        }

        if (isFling && mScroller!!.isFinished) {
            isFling = false
            if (hasLrc() && !isTouching) {
                adjustCenter()
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME)
            }
        }
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(hideTimelineRunnable)
        super.onDetachedFromWindow()
    }

    private fun onLrcLoaded(entryList: List<LrcEntry>?) {
        if (entryList != null && !entryList.isEmpty()) {
            mLrcEntryList.addAll(entryList)
        }

        initEntryList()
        invalidate()
    }

    private fun initEntryList() {
        if (!hasLrc() || width == 0) {
            return
        }

        Collections.sort(mLrcEntryList)

        for (lrcEntry in mLrcEntryList) {
            lrcEntry.init(mLrcPaint, lrcWidth.toInt())
        }

        mOffset = (height / 2).toFloat()
    }

    private fun reset() {
        endAnimation()
        mScroller!!.forceFinished(true)
        isShowTimeline = false
        isTouching = false
        isFling = false
        removeCallbacks(hideTimelineRunnable)
        mLrcEntryList.clear()
        mOffset = 0f
        mCurrentLine = 0
        invalidate()
    }

    /**
     * 将中心行微调至正中心
     */
    private fun adjustCenter() {
        scrollTo(centerLine, ADJUST_DURATION)
    }

    private fun scrollTo(line: Int, duration: Long = mAnimationDuration) {
        val offset = getOffset(line)
        endAnimation()

        mAnimator = ValueAnimator.ofFloat(mOffset, offset)
        mAnimator!!.duration = duration
        mAnimator!!.interpolator = LinearInterpolator()
        mAnimator!!.addUpdateListener { animation ->
            mOffset = animation.animatedValue as Float
            invalidate()
        }
        mAnimator!!.start()
    }

    private fun endAnimation() {
        if (mAnimator != null && mAnimator!!.isRunning) {
            mAnimator!!.end()
        }
    }

    /**
     * 二分法查找当前时间应该显示的行数（最后一个 <= time 的行数）
     */
    private fun findShowLine(time: Long): Int {
        var left = 0
        var right = mLrcEntryList.size
        while (left <= right) {
            val middle = (left + right) / 2
            val middleTime = mLrcEntryList[middle].time

            if (time < middleTime) {
                right = middle - 1
            } else {
                if (middle + 1 >= mLrcEntryList.size || time < mLrcEntryList[middle + 1].time) {
                    return middle
                }

                left = middle + 1
            }
        }

        return 0
    }

    private fun getOffset(line: Int): Float {
        if (mLrcEntryList[line].offset == java.lang.Float.MIN_VALUE) {
            var offset = (height / 2).toFloat()
            for (i in 1..line) {
                offset -= (mLrcEntryList[i - 1].height + mLrcEntryList[i].height) / 2 + mDividerHeight
            }
            mLrcEntryList[line].offset = offset
        }

        return mLrcEntryList[line].offset
    }


    private fun formatTime(milli: Long): String {
        val m = (milli / DateUtils.MINUTE_IN_MILLIS).toInt()
        val s = (milli / DateUtils.SECOND_IN_MILLIS % 60).toInt()
        val mm = String.format(Locale.getDefault(), "%02d", m)
        val ss = String.format(Locale.getDefault(), "%02d", s)
        return mm + ":" + ss
    }


    fun setNormalColor(normalColor: Int) {
        mNormalTextColor = normalColor
        postInvalidate()
    }

    fun setCurrentColor(currentColor: Int) {
        mCurrentTextColor = currentColor
        postInvalidate()
    }

    fun setTimelineTextColor(timelineTextColor: Int) {
        mTimelineTextColor = timelineTextColor
        postInvalidate()
    }

    fun setTimelineColor(timelineColor: Int) {
        mTimelineColor = timelineColor
        postInvalidate()
    }

    fun setTimeTextColor(timeTextColor: Int) {
        mTimeTextColor = timeTextColor
        postInvalidate()
    }

    /**
     * 设置播放按钮点击监听器
     *
     * @param onPlayClickListener 如果为非 null ，则激活歌词拖动功能，否则将将禁用歌词拖动功能
     */
    fun setOnPlayClickListener(onPlayClickListener: OnPlayClickListener) {
        mOnPlayClickListener = onPlayClickListener
    }

    /**
     * 设置歌词为空时屏幕中央显示的文字，如“暂无歌词”
     */
    fun setLabel(label: String) {
        mDefaultLabel = label
        invalidate()
    }

}
