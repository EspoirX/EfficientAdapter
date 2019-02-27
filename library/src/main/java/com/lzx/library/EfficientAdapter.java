package com.lzx.library;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;


import com.lzx.library.differ.AsyncListDiffer;

import java.util.ArrayList;
import java.util.List;

public class EfficientAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int DEFAULT_ITEM_TYPE = 0; //int 的默认值是0
    private Context mContext;
    private Object[] mObjects;
    private List<MultiTypeEntity> mDataList;
    private SparseArray<IViewHolderCreator> typeHolders;
    private boolean isUseDiffUtil = true;
    private AsyncListDiffer<MultiTypeEntity> mHelper;

    public EfficientAdapter(Context context) {
        initAdapter(context, true);
    }

    public EfficientAdapter(Context context, boolean isUseDiffUtil) {
        initAdapter(context, isUseDiffUtil);
    }

    public EfficientAdapter(Context context, Object[] objects) {
        mObjects = objects;
        initAdapter(context, true);
    }

    public EfficientAdapter(Context context, boolean isUseDiffUtil, Object[] objects) {
        mObjects = objects;
        initAdapter(context, isUseDiffUtil);
    }

    public void setObjects(Object[] objects) {
        mObjects = objects;
    }

    private void initAdapter(Context context, boolean isUseDiffUtil) {
        mContext = context;
        this.isUseDiffUtil = isUseDiffUtil;
        typeHolders = new SparseArray<>();
        if (isUseDiffUtil) {
            mHelper = new AsyncListDiffer<>(this, new AsyncListDiffer.ListChangedCallback<MultiTypeEntity>() {
                @Override
                public void onListChanged(List<MultiTypeEntity> currentList) {
                    mDataList = currentList;
                }
            }, new DiffUtil.ItemCallback<MultiTypeEntity>() {
                // 返回两个item是否相同
                // 例如：此处两个item的数据实体是User类，所以以id作为两个item是否相同的依据
                // 即此处返回两个user的id是否相同
                @Override
                public boolean areItemsTheSame(@NonNull MultiTypeEntity oldItem, @NonNull MultiTypeEntity newItem) {
                    return oldItem.areItemsTheSame(newItem);
                }

                // 当areItemsTheSame返回true时，我们还需要判断两个item的内容是否相同
                // 此处以User的age作为两个item内容是否相同的依据
                // 即返回两个user的age是否相同
                @Override
                public boolean areContentsTheSame(@NonNull MultiTypeEntity oldItem, @NonNull MultiTypeEntity newItem) {
                    return oldItem.areContentsTheSame(newItem);
                }
            });
        }
    }

    public <T extends MultiTypeEntity> EfficientAdapter register(int viewType, final HolderInjector<T> injector) {
        typeHolders.put(viewType, new IViewHolderCreator<T>() {
            @Override
            public BaseViewHolder<T> create(ViewGroup parent) {
                return new BaseViewHolder<T>(parent, injector.getLayoutRes()) {

                    @Override
                    protected void setItemView(View itemView) {
                        injector.setItemView(itemView);
                    }

                    @Override
                    protected void onBindPayloads(Context context, List<Object> payloads, int position, Object... objects) {
                        injector.onInjectUpdate(context, (T) payloads.get(0), position, objects);
                    }

                    @Override
                    protected void onBind(Context context, T data, int position, Object... objects) {
                        injector.onInject(context, data, position, objects);
                    }
                };
            }
        });
        return this;
    }

    public <T extends MultiTypeEntity> EfficientAdapter register(final HolderInjector<T> injector) {
        register(DEFAULT_ITEM_TYPE, injector);
        return this;
    }

    public <T> void submitList(@Nullable List<T> list) {
        if (list == null) {
            return;
        }
        if (isUseDiffUtil) {
            this.mHelper.submitList((List<MultiTypeEntity>) list);
        } else {
            mDataList = (List<MultiTypeEntity>) list;
            notifyDataSetChanged();
        }
    }

    public List<? extends MultiTypeEntity> getCurrentList() {
        if (isUseDiffUtil) {
            return new ArrayList<>(mHelper.getCurrentList());
        } else {
            return mDataList;
        }
    }

    public <T extends MultiTypeEntity> void insertedData(int position, T data) {
        if (isUseDiffUtil) {
            List<MultiTypeEntity> newList = new ArrayList<>(mDataList);
            newList.add(position, data);
            submitList(newList);
        } else {
            mDataList.add(position, data);
            notifyItemInserted(position);
        }
    }

    public void removedData(int position) {
        if (isUseDiffUtil) {
            List<MultiTypeEntity> newList = new ArrayList<>(mDataList);
            newList.remove(position);
            submitList(newList);
        } else {
            mDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public <T extends MultiTypeEntity> void removedData(T data) {
        if (isUseDiffUtil) {
            List<MultiTypeEntity> newList = new ArrayList<>(mDataList);
            newList.remove(data);
            submitList(newList);
        } else {
            int removePosition = mDataList.indexOf(data);
            mDataList.remove(data);
            notifyItemRemoved(removePosition);
        }
    }

    public <T extends MultiTypeEntity> void updateData(int position, T data) {
        updateData(position, data, false);
    }

    public <T extends MultiTypeEntity> void updateData(int position, T data, boolean isEfficient) {
        if (isUseDiffUtil) {
            List<MultiTypeEntity> newList = new ArrayList<>(mDataList);
            newList.set(position, data);
            mHelper.updateOldData(newList);
        } else {
            mDataList.set(position, data);
        }
        if (isEfficient) {
            notifyItemChanged(position, data);
        } else {
            notifyItemChanged(position);
        }
    }

    public void clear() {
        if (isUseDiffUtil) {
            mHelper.submitList(null);
        } else {
            if (mDataList != null) {
                mDataList.clear();
            }
        }
    }

    private interface IViewHolderCreator<T extends MultiTypeEntity> {
        BaseViewHolder<T> create(ViewGroup parent);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        IViewHolderCreator creator = typeHolders.get(viewType);
        return creator.create(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(mContext, (MultiTypeEntity) getItem(position), null, position, mObjects);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.bind(mContext, (MultiTypeEntity) getItem(position), payloads, position, mObjects);
    }

    public Object getItem(int position) {
        return mDataList != null ? mDataList.get(position) : null;
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        MultiTypeEntity entity = (MultiTypeEntity) getItem(position);
        return entity.getItemType();
    }
}
