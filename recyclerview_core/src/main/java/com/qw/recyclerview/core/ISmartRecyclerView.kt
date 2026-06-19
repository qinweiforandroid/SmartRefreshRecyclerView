package com.qw.recyclerview.core

import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.loadmore.LoadMoreResult
import com.qw.recyclerview.loadmore.State

interface ISmartRecyclerView {
    companion object {
        const val REFRESH_IDLE = 0
        const val REFRESH_PULL = 1
        const val REFRESH_UP = 2
    }

    val recyclerView: RecyclerView

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
    fun setRefreshEnable(enabled: Boolean): ISmartRecyclerView

    /**
     * 是否支持下拉刷新
     */
    fun isRefreshEnable(): Boolean

    /**
     * 启用加载更多
     */
    fun setLoadMoreEnable(enabled: Boolean): ISmartRecyclerView


    /**
     * 是否支持加载更多
     */
    fun isLoadMoreEnable(): Boolean


    /**
     * 是否正在刷新
     */
    fun isPull(): Boolean

    /**
     * 是否是正在加载更多
     */
    fun isUp(): Boolean

    fun setRefreshing(
        refreshing: Boolean,
        afterRefreshCompleted: ISmartRecyclerView.() -> Unit = {}
    )

    fun setLoadMoreResult(result: LoadMoreResult)

}
