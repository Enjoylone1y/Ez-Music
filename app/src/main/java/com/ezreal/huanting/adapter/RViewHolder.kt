package com.ezreal.huanting.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.util.Linkify
import android.util.SparseArray
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Checkable
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


/**
 * RecyclerView View Holder
 * Created by wudeng on 2017/3/9.
 */

class RViewHolder(private val mContext: Context, val convertView: View) :
        RecyclerView.ViewHolder(convertView) {
    private val mViews: SparseArray<View> = SparseArray()

    private fun <T : View> getView(viewId: Int): T? {
        var view: View? = mViews.get(viewId)
        if (view == null) {
            view = convertView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view as T?
    }

    fun getTextView(viewId: Int): TextView? = getView(viewId)

    fun getImageView(viewId: Int): ImageView? = getView(viewId)

    fun setText(viewId: Int, text: String) {
        val tv = getView<TextView>(viewId)
        tv?.text = text
    }

    fun setImageResource(viewId: Int, resId: Int) {
        val view = getView<ImageView>(viewId)
        view?.setImageResource(resId)
    }

    fun setImageBitmap(viewId: Int, bitmap: Bitmap) {
        val view = getView<ImageView>(viewId)
        view?.setImageBitmap(bitmap)
    }

    fun setImageByUrl(context: Context, viewId: Int, url: String, default_img_id: Int) {
        setImageWithGlide(context, viewId, url, default_img_id)
    }

    fun setImageByUri(context: Context, viewId: Int, uri: String, default_img_id: Int) {
        setImageWithGlide(context, viewId, uri, default_img_id)
    }

    fun setImageByFilePath(context: Context, viewId: Int, path: String, default_img_id: Int) {
        setImageWithGlide(context, viewId, path, default_img_id)
    }

    private fun setImageWithGlide(context: Context, viewId: Int, s: String, default_img_id: Int) {
        val view = getView<ImageView>(viewId)
        Glide.with(context)
                .load(s)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(default_img_id)
                .into(view)
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable) {
        val view = getView<ImageView>(viewId)
        view?.setImageDrawable(drawable)
    }

    fun setBackgroundColor(viewId: Int, color: Int) {
        val view = getView<View>(viewId)
        view?.setBackgroundColor(color)
    }

    fun setBackgroundRes(viewId: Int, backgroundRes: Int) {
        val view = getView<View>(viewId)
        view?.setBackgroundResource(backgroundRes)
    }

    fun setTextColor(viewId: Int, textColor: Int) {
        val view = getView<TextView>(viewId)
        view?.setTextColor(textColor)
    }

    fun setTextColorRes(viewId: Int, textColorRes: Int) {
        val view = getView<TextView>(viewId)
        view?.setTextColor(mContext.resources.getColor(textColorRes))
    }

    @SuppressLint("NewApi")
    fun setAlpha(viewId: Int, value: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView<View>(viewId)?.alpha = value
        } else {
            // Pre-honeycomb hack to set Alpha value
            val alpha = AlphaAnimation(value, value)
            alpha.duration = 0
            alpha.fillAfter = true
            getView<View>(viewId)?.startAnimation(alpha)
        }
    }

    fun setVisible(viewId: Int, visible: Boolean) {
        val view = getView<View>(viewId)
        view?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun linkify(viewId: Int) {
        val view = getView<TextView>(viewId)
        Linkify.addLinks(view, Linkify.ALL)
    }

    fun setTypeface(typeface: Typeface, vararg viewIds: Int) {
        for (viewId in viewIds) {
            val view = getView<TextView>(viewId)
            view?.typeface = typeface
            view?.paintFlags = view?.paintFlags!! or Paint.SUBPIXEL_TEXT_FLAG
        }
    }

    fun setProgress(viewId: Int, progress: Int) {
        val view = getView<ProgressBar>(viewId)
        view?.progress = progress
    }

    fun setProgress(viewId: Int, progress: Int, max: Int) {
        val view = getView<ProgressBar>(viewId)
        view?.max = max
        view?.progress = progress
    }

    fun setMax(viewId: Int, max: Int) {
        val view = getView<ProgressBar>(viewId)
        view?.max = max
    }

    fun setRating(viewId: Int, rating: Float) {
        val view = getView<RatingBar>(viewId)
        view?.rating = rating
    }

    fun setRating(viewId: Int, rating: Float, max: Int) {
        val view = getView<RatingBar>(viewId)
        view?.max = max
        view?.rating = rating
    }

    fun setTag(viewId: Int, tag: Any) {
        val view = getView<View>(viewId)
        view?.tag = tag
    }

    fun setTag(viewId: Int, key: Int, tag: Any) {
        val view = getView<View>(viewId)
        view?.setTag(key, tag)
    }

    fun setChecked(viewId: Int, checked: Boolean) {
        val view = getView<View>(viewId) as Checkable
        view.isChecked = checked
    }

    fun setOnClickListener(viewId: Int, listener: View.OnClickListener) {
        val view = getView<View>(viewId)
        view?.setOnClickListener(listener)
    }

    fun setOnTouchListener(viewId: Int, listener: View.OnTouchListener) {
        val view = getView<View>(viewId)
        view?.setOnTouchListener(listener)
    }

    fun setOnLongClickListener(viewId: Int, listener: View.OnLongClickListener) {
        val view = getView<View>(viewId)
        view?.setOnLongClickListener(listener)
    }

}
