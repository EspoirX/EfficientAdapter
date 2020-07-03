package com.lzx.library

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

fun <T : Any> efficientAdapter(init: EfficientAdapter<T>.() -> Unit)
        : EfficientAdapter<T> {
    val adapter = EfficientAdapter<T>()
    adapter.init()
    return adapter
}

fun <T : Any> EfficientAdapter<T>.addItem(
        resourceId: Int, init: ViewHolderDsl<T>.() -> Unit
) {
    val holder = ViewHolderDsl<T>(resourceId)
    holder.init()
    register(holder)
}

class ViewHolderDsl<T>(private val resourceId: Int) : ViewHolderCreator<T>() {

    private var viewType: ((data: T?) -> Boolean)? = null
    private var viewHolder: ((data: T?, position: Int, holder: ViewHolderCreator<T>) -> Unit)? =
            null

    fun isForViewType(viewType: (data: T?) -> Boolean) {
        this.viewType = viewType
    }

    fun bindViewHolder(holder: (data: T?, position: Int, holder: ViewHolderCreator<T>) -> Unit) {
        viewHolder = holder
    }

    override fun isForViewType(data: T?, position: Int): Boolean {
        return viewType?.invoke(data) ?: (data != null)
    }

    override fun getResourceId() = resourceId

    override fun onBindViewHolder(
            data: T?, items: MutableList<T>?, position: Int, holder: ViewHolderCreator<T>,
            payloads: MutableList<Any>
    ) {
        viewHolder?.invoke(data, position, holder)
    }
}

class RecycleSetup<T> internal constructor(private val recyclerView: RecyclerView) {

    var items = mutableListOf<T>()
    var adapter: EfficientAdapter<T>? = null

    fun dataSource(items: MutableList<T>) {
        this.items.clear()
        this.items = items
    }

    fun withLayoutManager(init: RecycleSetup<T>.() -> RecyclerView.LayoutManager) =
            apply { recyclerView.layoutManager = init() }

    fun adapter(init: EfficientAdapter<T>.() -> Unit) {
        this.adapter = EfficientAdapter()
        init.invoke(adapter!!)
        recyclerView.adapter = adapter
        adapter?.submitList(this.items)
    }

    fun submitList(list: MutableList<T>) {
        this.items.clear()
        this.items = list
        adapter?.submitList(this.items)
    }

    fun insertedData(position: Int, data: T) = apply {
        items.add(position, data)
        adapter?.insertedData(position, data)
    }

    fun removedData(position: Int) = apply {
        items.removeAt(position)
        adapter?.removedData(position)
    }

    fun updateData(position: Int, data: T, payload: Boolean = true) {
        items[position] = data
        adapter?.updateData(position, data, payload)
    }

    fun getItem(position: Int): T = items[position]
}

fun <T> RecyclerView.setup(block: RecycleSetup<T>.() -> Unit): RecycleSetup<T> {
    val setup = RecycleSetup<T>(this).apply(block)
    if (layoutManager == null) {
        layoutManager = LinearLayoutManager(context)
    }
    return setup
}

fun <T> RecyclerView.submitList(items: MutableList<T>) {
    if (adapter != null && adapter is EfficientAdapter<*>) {
        (adapter as EfficientAdapter<T>).submitList(items)
    }
}

fun <T> RecyclerView.insertedData(position: Int, data: T) {
    if (adapter != null && adapter is EfficientAdapter<*>) {
        (adapter as EfficientAdapter<T>).insertedData(position, data)
    }
}

fun <T> RecyclerView.removedData(position: Int) {
    if (adapter != null && adapter is EfficientAdapter<*>) {
        (adapter as EfficientAdapter<T>).removedData(position)
    }
}

fun <T> RecyclerView.updateData(position: Int, data: T, payload: Boolean = true) {
    if (adapter != null && adapter is EfficientAdapter<*>) {
        (adapter as EfficientAdapter<T>).updateData(position, data, payload)
    }
}

fun <T> RecyclerView.getItem(position: Int): T? {
    return if (adapter != null && adapter is EfficientAdapter<*>) {
        (adapter as EfficientAdapter<T>).getItem(position)
    } else {
        null
    }
}