package com.lzx.library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseViewHolder<T extends MultiTypeEntity> extends RecyclerView.ViewHolder {

    public BaseViewHolder(ViewGroup parent, int resource) {
        super(LayoutInflater.from(parent.getContext()).inflate(resource, parent, false));
    }

    public void bind(Context context, T data, List<Object> payloads, int position, Object... objects) {
        setItemView(itemView);
        if (payloads == null || payloads.isEmpty()) {
            onBind(context, data, position, objects);
        } else {
            onBindPayloads(context, payloads, position, objects);
        }
    }

    protected abstract void setItemView(View itemView);

    protected abstract void onBind(Context context, T data, int position, Object... objects);

    protected abstract void onBindPayloads(Context context, List<Object> payloads, int position, Object... objects);
}
