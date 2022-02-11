package com.qw.recyclerview.core

import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.loadmore.State

interface ISmartRecyclerView {
    companion object {
        const val REFRESH_IDLE = 0
        const val REFRESH_PULL = 1
        const val REFRESH_UP = 2
    }


    fun getRecyclerView(): RecyclerView

    /**
     * 设置布局管理器
     */
    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager): ISmartRecyclerView

    /**
     * 设置动画
     */
    fun setItemAnimator(itemAnimator: RecyclerView.ItemAnimator): ISmartRecyclerView

    /**
     * 设置下拉刷新listener
     */
    fun setOnRefreshListener(onRefreshListener: OnRefreshListener): ISmartRecyclerView

    /**
     * 设置加载更多listener
     */
    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener): ISmartRecyclerView

    /**
     * 启用下拉刷新
     */
    fun setRefreshEnable(isEnabled: Boolean): ISmartRecyclerView

    /**
     * 是否支持下拉刷新
     */
    fun isRefreshEnable(): Boolean

    /**
     * 启用加载更多
     */
    fun setLoadMoreEnable(isEnabled: Boolean): ISmartRecyclerView


    /**
     * 是否支持加载更多
     */
    fun isLoadMoreEnable(): Boolean

    /**
     * 自动刷新
     */
    fun autoRefresh()

    /**
     * 刷新完成更新ui
     */
    fun finishRefresh(success: Boolean)

    /**
     * 刷新完成更新ui
     * @param success true刷新成功，false 刷新失敗
     * @param state 加载更多状态
     */
    fun finishRefresh(success: Boolean, state: State)

    fun finishLoadMore(success: Boolean, noMoreData: Boolean)
}