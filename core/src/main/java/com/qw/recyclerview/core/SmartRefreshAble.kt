package com.qw.recyclerview.core

import androidx.recyclerview.widget.RecyclerView

interface SmartRefreshable {
    companion object {
        const val REFRESH_IDLE = 0
        const val REFRESH_PULL = 1
        const val REFRESH_UP = 2
    }


    fun getRecyclerView(): RecyclerView

    /**
     * 设置布局管理器
     */
    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager)

    /**
     * 设置动画
     */
    fun setItemAnimator(itemAnimator: RecyclerView.ItemAnimator)

    /**
     * 设置下拉刷新listener
     */
    fun setOnRefreshListener(onRefreshListener: OnRefreshListener)

    /**
     * 设置加载更多listener
     */
    fun setOnLoadMoreListener(onRefreshListener: OnLoadMoreListener)

    /**
     * 启用下拉刷新
     */
    fun setRefreshEnable(isEnabled: Boolean)

    /**
     * 启用加载更多
     */
    fun setLoadMoreEnable(isEnabled: Boolean)

    /**
     * 自动刷新
     */
    fun autoRefresh()

    /**
     * 刷新完成更新ui
     */
    fun finishRefresh(success: Boolean)

    fun setLoadMore(success: Boolean, noMoreData: Boolean)
}