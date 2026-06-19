package com.qw.recyclerview.smartrefreshlayout

import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.loadmore.LoadMoreState
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * Created by qinwei on 2021/6/30 17:47
 */
class SmartV2RecyclerView(
    private val mRecyclerView: RecyclerView,
    private val mSmartRefreshLayout: SmartRefreshLayout
) : ISmartRecyclerView {
    private var mRefreshEnable: Boolean = false
    private var mLoadMoreEnable: Boolean = false
    private var onRefreshListener: OnRefreshListener? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var state = ISmartRecyclerView.REFRESH_IDLE
    override val recyclerView: RecyclerView
        get() = mRecyclerView

    init {
        mSmartRefreshLayout.setOnLoadMoreListener {
            state = ISmartRecyclerView.REFRESH_UP
            onLoadMoreListener?.onLoadMore()
        }
        mSmartRefreshLayout.setOnRefreshListener {
            state = ISmartRecyclerView.REFRESH_PULL
            onRefreshListener?.onRefresh()
        }
        mSmartRefreshLayout.setEnableRefresh(mRefreshEnable)
        mSmartRefreshLayout.setEnableLoadMore(mLoadMoreEnable)
    }

    private fun markIdle() {
        state = ISmartRecyclerView.REFRESH_IDLE
    }

    override fun setOnRefreshListener(onRefreshListener: OnRefreshListener): ISmartRecyclerView {
        this.onRefreshListener = onRefreshListener
        return this
    }

    override fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener): ISmartRecyclerView {
        this.onLoadMoreListener = onLoadMoreListener
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
        mLoadMoreEnable = enabled
        mSmartRefreshLayout.setEnableLoadMore(enabled)
        return this
    }

    override fun isLoadMoreEnable(): Boolean {
        return mLoadMoreEnable
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
        if (!mLoadMoreEnable) {
            return
        }
        when (state) {
            LoadMoreState.SUCCESS -> {
                mSmartRefreshLayout.finishLoadMore(300, true, false)
            }

            LoadMoreState.HIDDEN,
            LoadMoreState.NO_MORE -> {
                mSmartRefreshLayout.finishLoadMore(300, true, true)
            }

            LoadMoreState.ERROR -> {
                mSmartRefreshLayout.finishLoadMore(300, false, false)
            }
        }
    }
}
