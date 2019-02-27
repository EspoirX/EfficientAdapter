package com.lzx.efficientadapter.bean;


import com.lzx.library.MultiTypeEntity;

public class NumberInfo implements MultiTypeEntity<NumberInfo> {
    public int number;

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public boolean areItemsTheSame(NumberInfo newItem) {
        return number == newItem.number;
    }

    @Override
    public boolean areContentsTheSame(NumberInfo newItem) {
        return true;
    }
}
