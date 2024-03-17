package com.qw.recyclerview.smartrefreshlayout

import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.loadmore.State
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
        if (mRecyclerView.adapter == null) {
            throw IllegalArgumentException("RecyclerView must be setAdapter")
        }
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
        this.onLoadMoreListener = onLoadMoreListener
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
        mLoadMoreEnable = isEnabled
        mSmartRefreshLayout.setEnableLoadMore(isEnabled)
        return this
    }

    override fun isLoadMoreEnable(): Boolean {
        return mLoadMoreEnable
    }

    override fun autoRefresh() {
        if (mRefreshEnable) {
            mSmartRefreshLayout.autoRefresh()
        }
    }


    override fun finishRefresh(success: Boolean, footerState: State) {
        mSmartRefreshLayout.finishRefresh(success)
        markIdle()
    }

    override fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        if (!mLoadMoreEnable) {
            return
        }
        mSmartRefreshLayout.finishLoadMore(300, success, noMoreData)
    }
}