package com.qw.recyclerview.swiperefresh

import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.ScrollLoadMoreCoordinator
import com.qw.recyclerview.core.SRLog
import com.qw.recyclerview.loadmore.LoadMoreResult
import com.qw.recyclerview.loadmore.State

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

    override fun getRecyclerView(): RecyclerView {
        return mRecyclerView
    }

    override fun setLayoutManager(layoutManager: RecyclerView.LayoutManager): ISmartRecyclerView {
        mRecyclerView.layoutManager = layoutManager
        return this
    }

    override fun setItemAnimator(itemAnimator: RecyclerView.ItemAnimator): ISmartRecyclerView {
        mRecyclerView.itemAnimator = itemAnimator
        return this
    }

    override fun setOnRefreshListener(onRefreshListener: OnRefreshListener): ISmartRecyclerView {
        this.onRefreshListener = onRefreshListener
        return this
    }

    override fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener): ISmartRecyclerView {
        loadMoreCoordinator.setOnLoadMoreListener(onLoadMoreListener)
        return this
    }

    override fun setRefreshEnable(isEnabled: Boolean): ISmartRecyclerView {
        mRefreshEnable = isEnabled
        mSwipeRefreshLayout.isEnabled = isEnabled
        return this
    }

    override fun isRefreshEnable(): Boolean {
        return mRefreshEnable
    }


    override fun setLoadMoreEnable(isEnabled: Boolean): ISmartRecyclerView {
        loadMoreCoordinator.setLoadMoreEnable(isEnabled)
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

    override fun autoRefresh() {
        if (mRefreshEnable) {
            state = ISmartRecyclerView.REFRESH_PULL
            mSwipeRefreshLayout.isRefreshing = true
            onRefreshListener?.onRefresh()
        }
    }

    override fun finishRefresh(success: Boolean, footerState: State) {
        SRLog.d("SwipeRecyclerView finishRefresh success:$success")
        mSwipeRefreshLayout.isRefreshing = false
        loadMoreCoordinator.syncLoadMoreState(footerState)
        markIdle()
    }

    override fun finishLoadMore(result: LoadMoreResult) {
        SRLog.d("SwipeRecyclerView finishLoadMore result:$result")
        loadMoreCoordinator.finishLoadMore(result)
        markIdle()
    }
}
