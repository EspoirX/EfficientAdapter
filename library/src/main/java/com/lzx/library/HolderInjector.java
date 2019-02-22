package com.lzx.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class HolderInjector<T> implements IInjector<T> {

    private View itemView;

    public void setItemView(View itemView) {
        this.itemView = itemView;
    }

    public <T extends View> T findViewById(int viewId) {
        return itemView.findViewById(viewId);
    }

    @Override
    public void onInjectUpdate(Context context, T data, int position, Object... objects) {

    }

    public HolderInjector setText(int viewId, String text) {
        TextView textView = findViewById(viewId);
        textView.setText(text);
        return this;
    }

    public HolderInjector setImageResource(int viewId, int resId) {
        ImageView imageView = findViewById(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    public HolderInjector setImageBitmap(int viewId, Bitmap bm) {
        ImageView imageView = findViewById(viewId);
        imageView.setImageBitmap(bm);
        return this;
    }

    public HolderInjector setImageDrawable(int viewId, Drawable drawable) {
        ImageView imageView = findViewById(viewId);
        imageView.setImageDrawable(drawable);
        return this;
    }

    public HolderInjector setBackground(int viewId, Drawable drawable) {
        ImageView imageView = findViewById(viewId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(drawable);
        } else {
            imageView.setBackgroundDrawable(drawable);
        }
        return this;
    }

    public HolderInjector setBackgroundResource(int viewId, int resid) {
        ImageView imageView = findViewById(viewId);
        imageView.setBackgroundResource(resid);
        return this;
    }

    public HolderInjector visible(int id) {
        findViewById(id).setVisibility(View.VISIBLE);
        return this;
    }

    public HolderInjector invisible(int id) {
        findViewById(id).setVisibility(View.INVISIBLE);
        return this;
    }

    public HolderInjector gone(int id) {
        findViewById(id).setVisibility(View.GONE);
        return this;
    }

    public HolderInjector visibility(int id, int visibility) {
        findViewById(id).setVisibility(visibility);
        return this;
    }

    public HolderInjector setTextColor(int id, int color) {
        TextView view = findViewById(id);
        view.setTextColor(color);
        return this;
    }

    public HolderInjector setTextSize(int id, int sp) {
        TextView view = findViewById(id);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        return this;
    }

    public HolderInjector clicked(int id, View.OnClickListener listener) {
        findViewById(id).setOnClickListener(listener);
        return this;
    }

    public HolderInjector longClicked(int id, View.OnLongClickListener listener) {
        findViewById(id).setOnLongClickListener(listener);
        return this;
    }

    public HolderInjector enable(int id, boolean enable) {
        findViewById(id).setEnabled(enable);
        return this;
    }

    public HolderInjector enable(int id) {
        findViewById(id).setEnabled(true);
        return this;
    }

    public HolderInjector<T> disable(int id) {
        findViewById(id).setEnabled(false);
        return this;
    }

    public HolderInjector addView(int id, View... views) {
        ViewGroup viewGroup = findViewById(id);
        for (View view : views) {
            viewGroup.addView(view);
        }
        return this;
    }

    public HolderInjector addView(int id, View view, ViewGroup.LayoutParams params) {
        ViewGroup viewGroup = findViewById(id);
        viewGroup.addView(view, params);
        return this;
    }

    public HolderInjector removeAllViews(int id) {
        ViewGroup viewGroup = findViewById(id);
        viewGroup.removeAllViews();
        return this;
    }

    public HolderInjector removeView(int id, View view) {
        ViewGroup viewGroup = findViewById(id);
        viewGroup.removeView(view);
        return this;
    }
}
