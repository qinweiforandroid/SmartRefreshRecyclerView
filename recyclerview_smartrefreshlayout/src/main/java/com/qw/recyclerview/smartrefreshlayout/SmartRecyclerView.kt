package com.qw.recyclerview.smartrefreshlayout

import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.ScrollLoadMoreCoordinator
import com.qw.recyclerview.loadmore.LoadMoreResult
import com.qw.recyclerview.loadmore.State
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
        mSmartRefreshLayout.setEnableRefresh(isEnabled)
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
            mSmartRefreshLayout.autoRefresh()
        }
    }


    override fun finishRefresh(success: Boolean, footerState: State) {
        mSmartRefreshLayout.finishRefresh(success)
        loadMoreCoordinator.syncLoadMoreState(footerState)
        markIdle()
    }

    override fun finishLoadMore(result: LoadMoreResult) {
        loadMoreCoordinator.finishLoadMore(result)
        markIdle()
    }

    @Deprecated(
        message = "Use finishLoadMore(result) to avoid ambiguous boolean combinations.",
        replaceWith = ReplaceWith(
            expression = "finishLoadMore(LoadMoreResult.from(success, noMoreData))",
            imports = ["com.qw.recyclerview.loadmore.LoadMoreResult"]
        )
    )
    override fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        finishLoadMore(LoadMoreResult.from(success, noMoreData))
    }
}
