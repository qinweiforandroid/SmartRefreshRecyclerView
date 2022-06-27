package com.qw.recyclerview.layout

import androidx.recyclerview.widget.RecyclerView

/**
 * create by qinwei at 2022/6/17 18:26
 */
interface ILayoutManager {
    fun getLayoutManager(): RecyclerView.LayoutManager

    fun getLastVisibleItemPosition(): Int
}