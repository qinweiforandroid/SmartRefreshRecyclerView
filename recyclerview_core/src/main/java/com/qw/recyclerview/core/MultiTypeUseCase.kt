package com.qw.recyclerview.core

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap

/**
 * Created by qinwei on 2023/5/27 16:20
 * email: qinwei_it@163.com
 */
class MultiTypeUseCase {
    private val types: ArrayMap<Int, ItemViewDelegate> = ArrayMap()

    fun register(viewType: Int, delegate: ItemViewDelegate) {
        check(!types.contains(viewType)) {
            "Duplicate ItemViewDelegate registration for viewType=$viewType."
        }
        types[viewType] = delegate
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val delegate = checkNotNull(types[viewType]) {
            "No ItemViewDelegate registered for viewType=$viewType."
        }
        return delegate.onCreateViewHolder(parent.context, parent)
    }
}

interface IItemViewType {
    fun getItemViewType(): Int
}

interface ItemViewDelegate {
    fun onCreateViewHolder(context: Context, parent: ViewGroup): BaseViewHolder
}

abstract class AbsItemViewDelegate(private val layoutId: Int) : ItemViewDelegate {
    final override fun onCreateViewHolder(context: Context, parent: ViewGroup): BaseViewHolder {
        return onCreateViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false))
    }

    abstract fun onCreateViewHolder(view: View): BaseViewHolder


}
