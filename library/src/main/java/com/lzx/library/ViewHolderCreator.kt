package com.lzx.library

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

abstract class ViewHolderCreator<T> {
    abstract fun isForViewType(data: T?, position: Int): Boolean
    abstract fun getResourceId(): Int
    abstract fun onBindViewHolder(
            data: T?,
            items: MutableList<T>?,
            position: Int,
            holder: ViewHolderCreator<T>,
            payloads: MutableList<Any>
    )

    private var itemView: View? = null

    fun registerItemView(itemView: View?) {
        this.itemView = itemView
    }

    fun <V : View> findViewById(viewId: Int): V {
        checkItemView()
        return itemView!!.findViewById(viewId)
    }

    fun setText(viewId: Int, text: String?) = apply {
        val textView: TextView = findViewById(viewId)
        textView.text = text
    }

    fun setImageResource(viewId: Int, resId: Int) = apply {
        val imageView: ImageView = findViewById(viewId)
        imageView.setImageResource(resId)
    }

    fun setImageBitmap(viewId: Int, bm: Bitmap?) = apply {
        val imageView: ImageView = findViewById(viewId)
        imageView.setImageBitmap(bm)
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable?) = apply {
        val imageView: ImageView = findViewById(viewId)
        imageView.setImageDrawable(drawable)
    }

    fun setBackground(viewId: Int, drawable: Drawable?) = apply {
        val imageView: ImageView = findViewById(viewId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.background = drawable
        } else {
            imageView.setBackgroundDrawable(drawable)
        }
    }

    fun setBackgroundResource(viewId: Int, resid: Int) = apply {
        val imageView: ImageView = findViewById(viewId)
        imageView.setBackgroundResource(resid)
    }

    fun visible(id: Int) = apply { findViewById<View>(id).visibility = View.VISIBLE }

    fun invisible(id: Int) = apply { findViewById<View>(id).visibility = View.INVISIBLE }

    fun gone(id: Int) = apply { findViewById<View>(id).visibility = View.GONE }

    fun visibility(id: Int, visibility: Int) =
            apply { findViewById<View>(id).visibility = visibility }

    fun setTextColor(id: Int, color: Int) = apply {
        val view: TextView = findViewById(id)
        view.setTextColor(color)
    }

    fun setTextSize(id: Int, sp: Int) = apply {
        val view: TextView = findViewById(id)
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp.toFloat())
    }

    fun clicked(id: Int, listener: View.OnClickListener?) =
            apply { findViewById<View>(id).setOnClickListener(listener) }

    fun itemClicked(listener: View.OnClickListener?) =
            apply { itemView?.setOnClickListener(listener) }

    fun longClicked(id: Int, listener: OnLongClickListener?) =
            apply { findViewById<View>(id).setOnLongClickListener(listener) }

    fun isEnabled(id: Int, enable: Boolean = true) =
            apply { findViewById<View>(id).isEnabled = enable }

    fun addView(id: Int, vararg views: View?) = apply {
        val viewGroup: ViewGroup = findViewById(id)
        for (view in views) {
            viewGroup.addView(view)
        }
    }

    fun addView(id: Int, view: View?, params: ViewGroup.LayoutParams?) = apply {
        val viewGroup: ViewGroup = findViewById(id)
        viewGroup.addView(view, params)
    }

    fun removeAllViews(id: Int) = apply {
        val viewGroup: ViewGroup = findViewById(id)
        viewGroup.removeAllViews()
    }

    fun removeView(id: Int, view: View?) = apply {
        val viewGroup: ViewGroup = findViewById(id)
        viewGroup.removeView(view)
    }

    private fun checkItemView() {
        if (itemView == null) {
            throw NullPointerException("itemView is null")
        }
    }
}