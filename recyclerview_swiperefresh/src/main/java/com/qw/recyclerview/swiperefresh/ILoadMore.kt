package com.qw.recyclerview.swiperefresh

import com.qw.recyclerview.core.State

interface ILoadMore {
    /**
     * 是否可加载更多
     */
    fun canLoadMore(): Boolean

    /**
     * 通知ui更新
     */
    fun notifyFooterDataSetChanged(state: State)

    /**
     * 设置是否显示footer
     */
    fun setShowLoadMoreFooter(show: Boolean)
}