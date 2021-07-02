package com.qw.recyclerview.core

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by qinwei on 2021/6/30 12:17
 */
abstract class BaseListAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
//        holder.initData(position)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.initData(position, payloads)
    }
}