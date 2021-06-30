package com.qw.recyclerview.core

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by qinwei on 2021/6/30 12:17
 */
abstract class BaseListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is BaseViewHolder){
            holder.initData(position)
        }
    }
}