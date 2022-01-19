package com.qw.recyclerview.core

import com.qw.recyclerview.loadmore.State

interface OnLoadMoreListener {
    /**
     * 加载更多数据
     */
    fun onLoadMore()

    /**
     * 状态变化监听
     */
    fun onStateChanged(state: State) {}
}