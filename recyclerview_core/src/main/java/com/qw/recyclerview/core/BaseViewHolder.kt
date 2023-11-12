package com.qw.recyclerview.core

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by qinwei on 2021/6/30 11:37
 */
abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected lateinit var model: Any
    abstract fun initData(position: Int)
    open fun initData(position: Int, payloads: List<Any>) {
        initData(position)
    }

    fun bindModel(model: Any) {
        this.model = model
    }

    open fun onViewAttachedToWindow() {

    }

    open fun onViewDetachedFromWindow() {}
}