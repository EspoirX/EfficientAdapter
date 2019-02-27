package com.lzx.library;

public interface MultiTypeEntity<T extends MultiTypeEntity> {
    /**
     * item类型
     */
    int getItemType();

    boolean areItemsTheSame(T newItem);

    boolean areContentsTheSame(T newItem);
}
