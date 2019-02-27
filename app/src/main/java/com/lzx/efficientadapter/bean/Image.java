package com.lzx.efficientadapter.bean;


import com.lzx.library.MultiTypeEntity;

public class Image implements MultiTypeEntity<Image> {

    public static int TYPE_IMAGE = 0x001;
    private int res;

    public Image(int res) {
        this.res = res;
    }

    public int getRes() {
        return res;
    }

    @Override
    public int getItemType() {
        return TYPE_IMAGE;
    }

    @Override
    public boolean areItemsTheSame(Image newItem) {
        return res == newItem.res;
    }

    @Override
    public boolean areContentsTheSame(Image newItem) {
        return true;
    }
}
