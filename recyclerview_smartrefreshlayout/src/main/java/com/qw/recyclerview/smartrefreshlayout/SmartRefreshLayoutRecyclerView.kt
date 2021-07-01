package com.qw.recyclerview.smartrefreshlayout

import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.SmartRefreshable
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.lang.IllegalArgumentException

/**
 * Created by qinwei on 2021/6/30 17:47
 */
class SmartRefreshLayoutRecyclerView(private val mRecyclerView: RecyclerView, private val mSmartRefreshLayout: SmartRefreshLayout) : SmartRefreshable {
    private var mRefreshEnable: Boolean = false
    private var mLoadMoreEnable: Boolean = false
    private var onRefreshListener: OnRefreshListener? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var state = SmartRefreshable.REFRESH_IDLE

    init {
        mSmartRefreshLayout.setOnLoadMoreListener {
            state = SmartRefreshable.REFRESH_UP
            onLoadMoreListener?.onLoadMore()
        }
        mSmartRefreshLayout.setOnRefreshListener {
            state = SmartRefreshable.REFRESH_PULL
            onRefreshListener?.onRefresh()
        }
        mSmartRefreshLayout.setEnableRefresh(mRefreshEnable)
        mSmartRefreshLayout.setEnableLoadMore(mLoadMoreEnable)
        if (mRecyclerView.adapter == null) {
            throw IllegalArgumentException("RecyclerView must be setAdapter")
        }
    }

    private fun markIdle() {
        state = SmartRefreshable.REFRESH_IDLE
    }

    override fun getRecyclerView(): RecyclerView {
        return mRecyclerView
    }

    override fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        mRecyclerView.layoutManager = layoutManager
    }

    override fun setItemAnimator(itemAnimator: RecyclerView.ItemAnimator) {
        mRecyclerView.itemAnimator = itemAnimator
    }

    override fun setOnRefreshListener(onRefreshListener: OnRefreshListener) {
        this.onRefreshListener = onRefreshListener
    }

    override fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    override fun setRefreshEnable(isEnabled: Boolean) {
        mRefreshEnable = isEnabled
        mSmartRefreshLayout.setEnableRefresh(isEnabled)
    }

    override fun setLoadMoreEnable(isEnabled: Boolean) {
        mLoadMoreEnable = isEnabled
        mSmartRefreshLayout.setEnableLoadMore(isEnabled)
    }

    override fun autoRefresh() {
        if (mRefreshEnable) {
            mSmartRefreshLayout.autoRefresh()
        }
    }

    override fun finishRefresh(success: Boolean) {
        mSmartRefreshLayout.finishRefresh(300, success, false)
        markIdle()
    }

    override fun setLoadMore(success: Boolean, noMoreData: Boolean) {
        if (!mLoadMoreEnable) {
            return
        }
        mSmartRefreshLayout.finishLoadMore(300, success, noMoreData)
    }
}