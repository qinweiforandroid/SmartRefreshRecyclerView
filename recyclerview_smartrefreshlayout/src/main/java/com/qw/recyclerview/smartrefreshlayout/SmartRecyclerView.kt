package com.qw.recyclerview.smartrefreshlayout

import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.ScrollLoadMoreCoordinator
import com.qw.recyclerview.loadmore.LoadMoreState
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * Created by qinwei on 2021/6/29 21:44
 */
class SmartRecyclerView(
    private val mRecyclerView: RecyclerView,
    private val mSmartRefreshLayout: SmartRefreshLayout
) : ISmartRecyclerView {
    private var mRefreshEnable: Boolean = false
    private var onRefreshListener: OnRefreshListener? = null
    private var state = ISmartRecyclerView.REFRESH_IDLE
    override val recyclerView: RecyclerView
        get() = mRecyclerView
    private val loadMoreCoordinator = ScrollLoadMoreCoordinator(
        recyclerView = mRecyclerView,
        logTag = "SmartRecyclerView",
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
        mSmartRefreshLayout.setOnRefreshListener {
            state = ISmartRecyclerView.REFRESH_PULL
            onRefreshListener?.onRefresh()
        }
        mSmartRefreshLayout.setEnableRefresh(mRefreshEnable)
        //使用adapter的load more功能
        mSmartRefreshLayout.setEnableLoadMore(false)
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
        mSmartRefreshLayout.setEnableRefresh(enabled)
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
            mSmartRefreshLayout.autoRefresh()
            return
        }
        mSmartRefreshLayout.finishRefresh()
        afterRefreshCompleted(this)
        markIdle()
    }

    override fun setLoadMoreState(state: LoadMoreState) {
        loadMoreCoordinator.finishLoadMore(state)
        markIdle()
    }
}
