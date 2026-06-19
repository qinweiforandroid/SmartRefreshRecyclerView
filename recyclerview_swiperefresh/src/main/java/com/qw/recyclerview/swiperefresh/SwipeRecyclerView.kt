package com.qw.recyclerview.swiperefresh

import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.ScrollLoadMoreCoordinator
import com.qw.recyclerview.core.SRLog
import com.qw.recyclerview.loadmore.LoadMoreState

/**
 * 下拉刷新: 由SwipeRefreshLayout提供
 * 加载更多: 需要自己实现，目前方案是监听RecyclerView滑动事件
 * Created by qinwei on 2021/6/29 21:44
 */
class SwipeRecyclerView(
    private val mRecyclerView: RecyclerView,
    private val mSwipeRefreshLayout: SwipeRefreshLayout
) : ISmartRecyclerView {

    private var mRefreshEnable: Boolean = false
    private var onRefreshListener: OnRefreshListener? = null
    private var state = ISmartRecyclerView.REFRESH_IDLE
    override val recyclerView: RecyclerView
        get() = mRecyclerView
    private val loadMoreCoordinator = ScrollLoadMoreCoordinator(
        recyclerView = mRecyclerView,
        logTag = "SwipeRecyclerView",
        host = object : ScrollLoadMoreCoordinator.Host {
            override fun canTriggerLoadMore(): Boolean {
                return state == ISmartRecyclerView.REFRESH_IDLE
            }

            override fun onLoadMoreTriggered() {
                state = ISmartRecyclerView.REFRESH_UP
            }
        }
    )

    init {
        mSwipeRefreshLayout.setOnRefreshListener {
            state = ISmartRecyclerView.REFRESH_PULL
            SRLog.d("SwipeRecyclerView onRefresh")
            onRefreshListener?.onRefresh()
        }
        mSwipeRefreshLayout.isEnabled = mRefreshEnable
    }

    private fun markIdle() {
        state = ISmartRecyclerView.REFRESH_IDLE
    }

    override fun setOnRefreshListener(onRefreshListener: OnRefreshListener): ISmartRecyclerView {
        this.onRefreshListener = onRefreshListener
        return this
    }

    override fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener): ISmartRecyclerView {
        loadMoreCoordinator.setOnLoadMoreListener(onLoadMoreListener)
        return this
    }

    override fun setRefreshEnable(enabled: Boolean): ISmartRecyclerView {
        mRefreshEnable = enabled
        mSwipeRefreshLayout.isEnabled = enabled
        return this
    }

    override fun isRefreshEnable(): Boolean {
        return mRefreshEnable
    }


    override fun setLoadMoreEnable(enabled: Boolean): ISmartRecyclerView {
        loadMoreCoordinator.setLoadMoreEnable(enabled)
        return this
    }

    override fun isLoadMoreEnable(): Boolean {
        return loadMoreCoordinator.isLoadMoreEnable()
    }

    override fun isPull(): Boolean {
        return state == ISmartRecyclerView.REFRESH_PULL || state == ISmartRecyclerView.REFRESH_IDLE
    }

    override fun isUp(): Boolean {
        return state == ISmartRecyclerView.REFRESH_UP
    }

    override fun setRefreshing(
        refreshing: Boolean,
        afterRefreshCompleted: ISmartRecyclerView.() -> Unit
    ) {
        if (refreshing) {
            if (!mRefreshEnable) {
                return
            }
            state = ISmartRecyclerView.REFRESH_PULL
            mSwipeRefreshLayout.isRefreshing = true
            onRefreshListener?.onRefresh()
            return
        }
        mSwipeRefreshLayout.isRefreshing = false
        afterRefreshCompleted(this)
        markIdle()
    }

    override fun setLoadMoreState(state: LoadMoreState) {
        SRLog.d("SwipeRecyclerView setLoadMoreResult result:$state")
        loadMoreCoordinator.finishLoadMore(state)
        markIdle()
    }
}
