package com.qw.recyclerview.core

import androidx.recyclerview.widget.RecyclerView

/**
 * 适配器
 * Created by qinwei on 2021/6/30 12:45
 */
abstract class BaseListAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.initData(position)
    }

    override fun getItemCount(): Int {
        return 0
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.initData(position, payloads)
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        holder.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        holder.onViewDetachedFromWindow()
    }
}