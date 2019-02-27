package com.lzx.efficientadapter.bean;


import com.lzx.library.MultiTypeEntity;

public class Music implements MultiTypeEntity<Music> {

    public static int TYPE_MUSIC = 0x002;
    private String name;
    private int coverRes;

    public Music(String name, int coverRes) {
        this.name = name;
        this.coverRes = coverRes;
    }

    public String getName() {
        return name;
    }

    public int getCoverRes() {
        return coverRes;
    }

    @Override
    public int getItemType() {
        return TYPE_MUSIC;
    }

    @Override
    public boolean areItemsTheSame(Music newItem) {
        return name.equals(newItem.name);
    }

    @Override
    public boolean areContentsTheSame(Music newItem) {
        return true;
    }
}
