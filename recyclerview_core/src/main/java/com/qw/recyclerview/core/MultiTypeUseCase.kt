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
        if (!types.contains(viewType)) {
            types[viewType] = delegate
        }
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return types[viewType]!!.onCreateViewHolder(parent.context, parent)
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