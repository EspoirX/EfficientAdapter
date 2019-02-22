package com.lzx.library.differ;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.DiffUtil.DiffResult;
import android.support.v7.util.DiffUtil.ItemCallback;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;


import com.lzx.library.MultiTypeEntity;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class AsyncListDiffer<T extends MultiTypeEntity> {
    private final ListUpdateCallback mUpdateCallback;
    private ListChangedCallback<T> mListChangedCallback;
    final AsyncDifferConfig<T> mConfig;
    final Executor mMainThreadExecutor;
    private static final Executor sMainThreadExecutor = new MainThreadExecutor();
    @Nullable
    private List<T> mList; //旧数据
    @NonNull
    private List<T> mReadOnlyList; //计算后的数据
    int mMaxScheduledGeneration;

    public AsyncListDiffer(@NonNull RecyclerView.Adapter adapter, ListChangedCallback<T> changedCallback, @NonNull ItemCallback<T> diffCallback) {
        this(adapter, changedCallback, (new AsyncDifferConfig.Builder(diffCallback)).build());
    }

    public AsyncListDiffer(@NonNull RecyclerView.Adapter adapter, ListChangedCallback<T> changedCallback, @NonNull AsyncDifferConfig<T> config) {
        this.mReadOnlyList = Collections.emptyList();
        this.mUpdateCallback = new AdapterListUpdateCallback(adapter);
        this.mConfig = config;
        this.mListChangedCallback = changedCallback;
        this.mMainThreadExecutor = sMainThreadExecutor;
    }

    public ListUpdateCallback getUpdateCallback() {
        return mUpdateCallback;
    }

    @NonNull
    public List<T> getCurrentList() {
        return this.mReadOnlyList;
    }

    public void submitList(@Nullable final List<T> newList) {
        // 用于控制计算线程，防止在上一次submitList未完成时，
        // 又多次调用submitList，这里只返回最后一个计算的DiffResult
        final int runGeneration = ++this.mMaxScheduledGeneration;
        if (newList != this.mList) {
            if (newList == null) {
                int countRemoved = this.mList.size();
                this.mList = null;
                this.mReadOnlyList = Collections.emptyList();
                mListChangedCallback.onListChanged(mReadOnlyList);
                this.mUpdateCallback.onRemoved(0, countRemoved);
            } else if (this.mList == null) {
                this.mList = newList;
                this.mReadOnlyList = Collections.unmodifiableList(newList);
                mListChangedCallback.onListChanged(mReadOnlyList);
                this.mUpdateCallback.onInserted(0, newList.size());
            } else {
                // 在子线程中计算DiffResult
                final List<T> oldList = this.mList;
                this.mConfig.getBackgroundThreadExecutor().execute(new Runnable() {
                    public void run() {
                        final DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                            public int getOldListSize() {
                                return oldList.size();
                            }

                            public int getNewListSize() {
                                return newList.size();
                            }

                            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                                T oldItem = oldList.get(oldItemPosition);
                                T newItem = newList.get(newItemPosition);
                                if (oldItem != null && newItem != null) {
                                    return mConfig.getDiffCallback().areItemsTheSame(oldItem, newItem);
                                } else {
                                    return oldItem == null && newItem == null;
                                }
                            }

                            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                                T oldItem = oldList.get(oldItemPosition);
                                T newItem = newList.get(newItemPosition);
                                if (oldItem != null && newItem != null) {
                                    return mConfig.getDiffCallback().areContentsTheSame(oldItem, newItem);
                                } else if (oldItem == null && newItem == null) {
                                    return true;
                                } else {
                                    throw new AssertionError();
                                }
                            }

                            @Nullable
                            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                                T oldItem = oldList.get(oldItemPosition);
                                T newItem = newList.get(newItemPosition);
                                if (oldItem != null && newItem != null) {
                                    return mConfig.getDiffCallback().getChangePayload(oldItem, newItem);
                                } else {
                                    throw new AssertionError();
                                }
                            }
                        });
                        //主线程更新
                        mMainThreadExecutor.execute(new Runnable() {
                            public void run() {
                                if (mMaxScheduledGeneration == runGeneration) {
                                    latchList(newList, result);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    void latchList(@NonNull List<T> newList, @NonNull DiffResult diffResult) {
        this.mList = newList;
        this.mReadOnlyList = Collections.unmodifiableList(newList);
        mListChangedCallback.onListChanged(mReadOnlyList);
        diffResult.dispatchUpdatesTo(this.mUpdateCallback);
    }

    /**
     * 更新旧数据
     */
    public void updateOldData(List<T> newList) {
        this.mList = newList;
        mListChangedCallback.onListChanged(mList);
    }

    /**
     * 主线程
     */
    private static class MainThreadExecutor implements Executor {
        final Handler mHandler = new Handler(Looper.getMainLooper());

        MainThreadExecutor() {
        }

        public void execute(@NonNull Runnable command) {
            this.mHandler.post(command);
        }
    }

    /**
     * 列表改变监听
     */
    public interface ListChangedCallback<T> {
        void onListChanged(List<T> currentList);
    }


}