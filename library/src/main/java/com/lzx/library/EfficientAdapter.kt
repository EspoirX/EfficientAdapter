package com.lzx.library

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

open class EfficientAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val FALLBACK_DELEGATE_VIEW_TYPE = Int.MAX_VALUE - 1
    }

    var items: MutableList<T>? = mutableListOf()
    private val typeHolders: SparseArrayCompat<ViewHolderCreator<T>> = SparseArrayCompat()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = getHolderForViewType(viewType)
                ?: throw NullPointerException("No Holder added for ViewType $viewType")
        return BaseViewHolder(parent, holder.getResourceId())
    }

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(viewHolder, position, mutableListOf())
    }

    override fun onBindViewHolder(
            viewHolder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        val holder = getHolderForViewType(viewHolder.itemViewType)
                ?: throw java.lang.NullPointerException(
                        "No Holder added for ViewType $viewHolder.itemViewType")
        holder.registerItemView(viewHolder.itemView)
        holder.onBindViewHolder(items?.get(position), items, position, holder, payloads)
    }

    override fun getItemViewType(position: Int): Int {
        if (items == null) {
            throw NullPointerException("adapter data source is null")
        }
        for (i in 0 until typeHolders.size()) {
            val holder = typeHolders.valueAt(i)
            val data = items!!.getOrNull(position)
            if (holder.isForViewType(data, position)) {
                return typeHolders.keyAt(i)
            }
        }
        val errorMessage: String
        errorMessage = if (items is List<*>) {
            val itemString = (items as List<*>)[position].toString()
            "No holder added that matches item=$itemString at position=$position in data source"
        } else {
            "No holder added for item at position=$position. items=$items"
        }
        throw java.lang.NullPointerException(errorMessage)
    }

    private fun getHolderForViewType(viewType: Int): ViewHolderCreator<T>? {
        return typeHolders.get(viewType)
    }

    fun addTypeHolder(holder: ViewHolderCreator<T>): EfficientAdapter<T> {
        var viewType: Int = typeHolders.size()
        while (typeHolders.get(viewType) != null) {
            viewType++
            require(
                    viewType != FALLBACK_DELEGATE_VIEW_TYPE) {
                "Oops, we are very close to Integer.MAX_VALUE. " +
                        "It seems that there are no more free and " +
                        "unused view type integers left to add another holder."
            }
        }
        return addTypeHolder(viewType, holder)
    }

    fun addTypeHolder(
            viewType: Int,
            holder: ViewHolderCreator<T>?
    ): EfficientAdapter<T> {
        if (holder == null) {
            throw java.lang.NullPointerException("holder is null!")
        }
        require(viewType != FALLBACK_DELEGATE_VIEW_TYPE) {
            ("The view type = " + FALLBACK_DELEGATE_VIEW_TYPE +
                    " is reserved for fallback adapter holder Please use another view type.")
        }
        require(typeHolders.get(viewType) == null) {
            ("An holder is already registered for the viewType = " +
                    viewType +
                    ". Already registered holder is " + typeHolders.get(viewType))
        }
        typeHolders.put(viewType, holder)
        return this
    }

    open fun insertedData(position: Int, data: T) = apply {
        items?.add(position, data)
        notifyItemInserted(position)
    }

    open fun removedData(position: Int) = apply {
        items?.removeAt(position)
        notifyItemRemoved(position)
    }

    open fun updateData(position: Int, data: T, payload: Boolean = true) {
        items?.set(position, data)
        if (payload) {
            notifyItemChanged(position, data)
        } else {
            notifyItemChanged(position)
        }
    }

    fun getItem(position: Int): T? = items?.getOrNull(position)

    fun register(holder: ViewHolderCreator<T>): EfficientAdapter<T> {
        var viewType: Int = typeHolders.size()
        while (typeHolders.get(viewType) != null) {
            viewType++
            require(
                    viewType != FALLBACK_DELEGATE_VIEW_TYPE) {
                "Oops, we are very close to Integer.MAX_VALUE. " +
                        "It seems that there are no more free" +
                        "and unused view type integers left to add another holder."
            }
        }
        return register(viewType, holder)
    }

    fun register(viewType: Int, holder: ViewHolderCreator<T>): EfficientAdapter<T> {
        require(viewType != FALLBACK_DELEGATE_VIEW_TYPE) {
            ("The view type = $FALLBACK_DELEGATE_VIEW_TYPE is reserved " +
                    "for fallback adapter holder). Please use another view type.")
        }
        typeHolders.put(viewType, holder)
        return this
    }

    fun attach(recyclerView: RecyclerView): EfficientAdapter<T> {
        recyclerView.adapter = this
        return this
    }

    fun submitList(list: MutableList<T>) {
        this.items?.clear()
        this.items?.addAll(list)
        notifyDataSetChanged()
    }
}