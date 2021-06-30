package com.qw.recyclerview.core

import androidx.recyclerview.widget.RecyclerView

interface SmartRefreshable {
    companion object {
        const val REFRESH_IDLE = 0
        const val REFRESH_PULL = 1
        const val REFRESH_UP = 2
    }

    /**
     *  设置适配器
     */
    fun setAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>)

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
     * 设置刷新状态
     */
    fun setRefreshing(refresh: Boolean)

    fun setLoadMore(delayed: Int, success: Boolean, noMoreData: Boolean)
}