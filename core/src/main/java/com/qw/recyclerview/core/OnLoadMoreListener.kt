package com.qw.recyclerview.core

interface OnLoadMoreListener {
    /**
     * 加载更多数据
     */
    fun onLoadMore()

    /**
     * 通知ui状态更新
     */
    fun onStateChanged(state: State) {}

    fun getState(): State = State.IDLE
}