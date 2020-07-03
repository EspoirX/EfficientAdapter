package com.lzx.library


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class BaseViewHolder(parent: ViewGroup, resource: Int) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(resource, parent, false)
)


