package com.lzx.library

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.util.TypedValue
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val views = SparseArray<View>()

    fun <T : View> findViewById(@IdRes viewId: Int): T {
        var view = views[viewId]
        if (view == null) {
            view = itemView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T
    }
}

abstract class ViewHolderCreator<T> {
    abstract fun isForViewType(data: T?, position: Int): Boolean
    abstract fun getResourceId(): Int
    abstract fun onBindViewHolder(
        data: T?,
        items: MutableList<T>?,
        position: Int,
        holder: ViewHolderCreator<T>
    )

    var viewHolder: BaseViewHolder? = null

    fun registerViewHolder(viewHolder: BaseViewHolder?) {
        this.viewHolder = viewHolder
    }

    fun <V : View> findViewById(viewId: Int): V {
        checkItemView()
        return viewHolder!!.findViewById(viewId)
    }

    private fun checkItemView() {
        if (viewHolder == null) {
            throw NullPointerException("itemView is null")
        }
    }
}

fun <T> ViewHolderCreator<T>.setText(viewId: Int, text: String?) = apply {
    val textView: TextView = findViewById(viewId)
    textView.text = text
}

fun <T> ViewHolderCreator<T>.setImageResource(viewId: Int, resId: Int) = apply {
    val imageView: ImageView = findViewById(viewId)
    imageView.setImageResource(resId)
}

fun <T> ViewHolderCreator<T>.setImageBitmap(viewId: Int, bm: Bitmap?) = apply {
    val imageView: ImageView = findViewById(viewId)
    imageView.setImageBitmap(bm)
}

fun <T> ViewHolderCreator<T>.setImageDrawable(viewId: Int, drawable: Drawable?) = apply {
    val imageView: ImageView = findViewById(viewId)
    imageView.setImageDrawable(drawable)
}

fun <T> ViewHolderCreator<T>.setBackground(viewId: Int, drawable: Drawable?) = apply {
    val imageView: ImageView = findViewById(viewId)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        imageView.background = drawable
    } else {
        imageView.setBackgroundDrawable(drawable)
    }
}

fun <T> ViewHolderCreator<T>.setBackgroundResource(viewId: Int, resid: Int) = apply {
    val imageView: ImageView = findViewById(viewId)
    imageView.setBackgroundResource(resid)
}

fun <T> ViewHolderCreator<T>.visible(id: Int) =
    apply { findViewById<View>(id).visibility = View.VISIBLE }

fun <T> ViewHolderCreator<T>.invisible(id: Int) =
    apply { findViewById<View>(id).visibility = View.INVISIBLE }

fun <T> ViewHolderCreator<T>.gone(id: Int) = apply { findViewById<View>(id).visibility = View.GONE }

fun <T> ViewHolderCreator<T>.visibility(id: Int, visibility: Int) =
    apply { findViewById<View>(id).visibility = visibility }

fun <T> ViewHolderCreator<T>.setTextColor(id: Int, color: Int) = apply {
    val view: TextView = findViewById(id)
    view.setTextColor(color)
}

fun <T> ViewHolderCreator<T>.setTextSize(id: Int, sp: Int) = apply {
    val view: TextView = findViewById(id)
    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp.toFloat())
}

fun <T> ViewHolderCreator<T>.clicked(id: Int, listener: View.OnClickListener?) =
    apply { findViewById<View>(id).setOnClickListener(listener) }

fun <T> ViewHolderCreator<T>.itemClicked(listener: View.OnClickListener?) =
    apply { viewHolder?.itemView?.setOnClickListener(listener) }

fun <T> ViewHolderCreator<T>.longClicked(id: Int, listener: OnLongClickListener?) =
    apply { findViewById<View>(id).setOnLongClickListener(listener) }

fun <T> ViewHolderCreator<T>.isEnabled(id: Int, enable: Boolean = true) =
    apply { findViewById<View>(id).isEnabled = enable }

fun <T> ViewHolderCreator<T>.addView(id: Int, vararg views: View?) = apply {
    val viewGroup: ViewGroup = findViewById(id)
    for (view in views) {
        viewGroup.addView(view)
    }
}

fun <T> ViewHolderCreator<T>.addView(id: Int, view: View?, params: ViewGroup.LayoutParams?) =
    apply {
        val viewGroup: ViewGroup = findViewById(id)
        viewGroup.addView(view, params)
    }

fun <T> ViewHolderCreator<T>.removeAllViews(id: Int) = apply {
    val viewGroup: ViewGroup = findViewById(id)
    viewGroup.removeAllViews()
}

fun <T> ViewHolderCreator<T>.removeView(id: Int, view: View?) = apply {
    val viewGroup: ViewGroup = findViewById(id)
    viewGroup.removeView(view)
}
