package com.qw.recyclerview.core

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by qinwei on 2021/6/30 11:37
 */
abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun initData(position: Int)
}