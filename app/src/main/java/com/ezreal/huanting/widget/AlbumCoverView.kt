package com.ezreal.huanting.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.ezreal.huanting.R


/**
 * 专辑封面
 */
class AlbumCoverView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener {

    private val mHandler = Handler()
    private var mDiscBitmap: Bitmap? = null
    private var mCoverBitmap: Bitmap? = null
    private var mNeedleBitmap: Bitmap? = null
    private var mTopLine: Drawable? = null
    private var mCoverBorder: Drawable? = null

    private var mTopLineHeight: Int = 0
    private var mCoverBorderWidth: Int = 0
    private val mDiscMatrix = Matrix()
    private val mCoverMatrix = Matrix()
    private val mNeedleMatrix = Matrix()
    private var mPlayAnimator: ValueAnimator? = null
    private var mPauseAnimator: ValueAnimator? = null
    private var mDiscRotation = 0.0f
    private var mNeedleRotation = NEEDLE_ROTATION_PLAY
    private var isPlaying = false

    // 图片起始坐标
    private val mDiscPoint = Point()
    private val mCoverPoint = Point()
    private val mNeedlePoint = Point()
    // 旋转中心坐标
    private val mDiscCenterPoint = Point()
    private val mCoverCenterPoint = Point()
    private val mNeedleCenterPoint = Point()

    /**
     * 封面角度旋转
     */
    private val mRotationRunnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                mDiscRotation += DISC_ROTATION_INCREASE
                if (mDiscRotation >= 360) {
                    mDiscRotation = 0f
                }
                invalidate()
            }
            mHandler.postDelayed(this, TIME_UPDATE)
        }
    }

    private val screenWidth: Int
        get() {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }


    init {
        mTopLine = ContextCompat.getDrawable(context,R.drawable.play_cover_border_shape)
        mCoverBorder = ContextCompat.getDrawable(context,R.drawable.play_cover_border_shape)
        mDiscBitmap = BitmapFactory.decodeResource(resources, R.drawable.play_page_disc)
        mDiscBitmap = resizeImage(mDiscBitmap, (screenWidth * 0.75).toInt(),
                (screenWidth * 0.75).toInt())
        mCoverBitmap = BitmapFactory.decodeResource(context.resources,
                R.drawable.default_play_cover)
        mNeedleBitmap = BitmapFactory.decodeResource(resources, R.drawable.play_page_needle)
        mNeedleBitmap = resizeImage(mNeedleBitmap, (screenWidth * 0.25).toInt(),
                (screenWidth * 0.375).toInt())
        mTopLineHeight = dp2px(1f)
        mCoverBorderWidth = dp2px(1f)

        mPlayAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PAUSE, NEEDLE_ROTATION_PLAY)
        mPlayAnimator?.duration = 300
        mPlayAnimator?.addUpdateListener(this)
        mPauseAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PLAY, NEEDLE_ROTATION_PAUSE)
        mPauseAnimator?.duration = 300
        mPauseAnimator?.addUpdateListener(this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initSize()
    }

    /**
     * 确定图片起始坐标与旋转中心坐标
     */
    private fun initSize() {
        val discOffsetY = mNeedleBitmap!!.height / 2
        mDiscPoint.x = (width - mDiscBitmap!!.width) / 2
        mDiscPoint.y = discOffsetY
        mCoverPoint.x = (width - mCoverBitmap!!.width) / 2
        mCoverPoint.y = discOffsetY + (mDiscBitmap!!.height - mCoverBitmap!!.height) / 2
        mNeedlePoint.x = width / 2 - mNeedleBitmap!!.width / 6
        mNeedlePoint.y = -mNeedleBitmap!!.width / 6
        mDiscCenterPoint.x = width / 2
        mDiscCenterPoint.y = mDiscBitmap!!.height / 2 + discOffsetY
        mCoverCenterPoint.x = mDiscCenterPoint.x
        mCoverCenterPoint.y = mDiscCenterPoint.y
        mNeedleCenterPoint.x = mDiscCenterPoint.x
        mNeedleCenterPoint.y = 0
    }

    override fun onDraw(canvas: Canvas) {
        // 1.绘制顶部虚线
        mTopLine?.setBounds(0, 0, width, mTopLineHeight)
        mTopLine?.draw(canvas)
        // 2.绘制黑胶唱片外侧半透明边框
        mCoverBorder?.setBounds(mDiscPoint.x - mCoverBorderWidth,
                mDiscPoint.y - mCoverBorderWidth,
                mDiscPoint.x + mDiscBitmap!!.width + mCoverBorderWidth,
                mDiscPoint.y + mDiscBitmap!!.height + mCoverBorderWidth)
        mCoverBorder?.draw(canvas)

        // 3.绘制黑胶
        // 设置旋转中心和旋转角度，setRotate和preTranslate顺序很重要
        mDiscMatrix.setRotate(mDiscRotation, mDiscCenterPoint.x.toFloat(),
                mDiscCenterPoint.y.toFloat())
        // 设置图片起始坐标
        mDiscMatrix.preTranslate(mDiscPoint.x.toFloat(), mDiscPoint.y.toFloat())
        canvas.drawBitmap(mDiscBitmap!!, mDiscMatrix, null)
        // 4.绘制封面
        mCoverMatrix.setRotate(mDiscRotation, mCoverCenterPoint.x.toFloat(),
                mCoverCenterPoint.y.toFloat())
        mCoverMatrix.preTranslate(mCoverPoint.x.toFloat(), mCoverPoint.y.toFloat())
        canvas.drawBitmap(mCoverBitmap!!, mCoverMatrix, null)
        // 5.绘制指针
        mNeedleMatrix.setRotate(mNeedleRotation, mNeedleCenterPoint.x.toFloat(),
                mNeedleCenterPoint.y.toFloat())
        mNeedleMatrix.preTranslate(mNeedlePoint.x.toFloat(), mNeedlePoint.y.toFloat())
        canvas.drawBitmap(mNeedleBitmap!!, mNeedleMatrix, null)
    }

    fun initNeedle(isPlaying: Boolean) {
        mNeedleRotation = if (isPlaying) NEEDLE_ROTATION_PLAY else NEEDLE_ROTATION_PAUSE
        invalidate()
    }

    fun setCoverBitmap(bitmap: Bitmap?) {
        mCoverBitmap = if (bitmap == null){
            BitmapFactory.decodeResource(context.resources,
                    R.drawable.default_play_cover)
        }else{
            val width = mCoverBitmap?.width ?: 500
            val height = mCoverBitmap?.height ?: 500
            toRound(resizeImage(bitmap,width,height)!!)
        }
        mDiscRotation = 0.0f
        invalidate()
    }

    fun start() {
        if (isPlaying) {
            return
        }
        isPlaying = true
        mHandler.post(mRotationRunnable)
        mPlayAnimator?.start()
    }

    fun pause() {
        if (!isPlaying) {
            return
        }
        isPlaying = false
        mPauseAnimator?.pause()
        mHandler.removeCallbacks(mRotationRunnable)
        initNeedle(false)
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        mNeedleRotation = animation.animatedValue as Float
        invalidate()
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun resizeImage(source: Bitmap?, dstWidth: Int, dstHeight: Int): Bitmap? {
        return if (source == null) {
            null
        } else {
            Bitmap.createScaledBitmap(source, dstWidth, dstHeight, true)
        }
    }

    private fun toRound(src: Bitmap): Bitmap? {
        if (mCoverBitmap == null) return null
        val width = src.width
        val height = src.height
        val size = Math.min(width, height)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val ret = Bitmap.createBitmap(width, height, src.config)
        val center = size / 2f
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        rectF.inset((width - size) / 2f, (height - size) / 2f)
        val matrix = Matrix()
        matrix.setTranslate(rectF.left, rectF.top)
        matrix.preScale(size.toFloat() / width, size.toFloat() / height)
        val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        shader.setLocalMatrix(matrix)
        paint.shader = shader
        val canvas = Canvas(ret)
        canvas.drawRoundRect(rectF, center, center, paint)
        return ret
    }

    companion object {
        private val TIME_UPDATE = 50L
        private val DISC_ROTATION_INCREASE = 0.5f
        private val NEEDLE_ROTATION_PLAY = 0.0f
        private val NEEDLE_ROTATION_PAUSE = -25.0f
    }
}
