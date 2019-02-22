package com.lzx.library;

import android.content.Context;

public interface IInjector<T> {
    int getLayoutRes();

    void onInject(Context context, T data, int position, Object... objects);

    void onInjectUpdate(Context context, T data, int position, Object... objects);
}
