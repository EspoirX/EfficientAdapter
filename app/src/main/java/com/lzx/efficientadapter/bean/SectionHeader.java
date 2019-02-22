package com.lzx.efficientadapter.bean;


import com.lzx.library.MultiTypeEntity;

public class SectionHeader implements MultiTypeEntity<SectionHeader> {
    public static int TYPE_HEADER = 0x003;
    private String title;

    public SectionHeader(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int getItemType() {
        return TYPE_HEADER;
    }

    @Override
    public boolean areItemsTheSame(SectionHeader newItem) {
        return title.equals(newItem.title);
    }
}
