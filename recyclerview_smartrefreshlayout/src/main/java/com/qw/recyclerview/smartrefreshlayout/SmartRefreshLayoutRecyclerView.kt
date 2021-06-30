package com.qw.recyclerview.smartrefreshlayout

import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.SmartRefreshable
import com.qw.recyclerview.core.State
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * Created by qinwei on 2021/6/30 17:47
 */
class SmartRefreshLayoutRecyclerView(private val mRecyclerView: RecyclerView, private val mSmartRefreshLayout: SmartRefreshLayout):SmartRefreshable {
    private lateinit var adapter: BaseListAdapter
    private var mRefreshEnable: Boolean = false
    private var mLoadMoreEnable: Boolean = false
    private var onRefreshListener: OnRefreshListener? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var state = SmartRefreshable.REFRESH_IDLE

    init {
        mSmartRefreshLayout.setOnLoadMoreListener {
            state = SmartRefreshable.REFRESH_UP
            adapter.notifyFooterDataSetChanged(State.LOADING)
            onLoadMoreListener?.onLoadMore()
        }
        mSmartRefreshLayout.setOnRefreshListener {
            state = SmartRefreshable.REFRESH_PULL
            onRefreshListener?.onRefresh()
        }
        mSmartRefreshLayout.isEnabled = mRefreshEnable
    }

    private fun markIdle() {
        state = SmartRefreshable.REFRESH_IDLE
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
        mRecyclerView.adapter = adapter
        if (adapter is BaseListAdapter) {
            this.adapter = adapter
        }
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
        mSmartRefreshLayout.isEnabled = isEnabled
    }

    override fun setLoadMoreEnable(isEnabled: Boolean) {
        mLoadMoreEnable = isEnabled
    }

    override fun autoRefresh() {
        mSmartRefreshLayout.autoRefresh()
    }

    override fun finishRefresh(success: Boolean) {
        mSmartRefreshLayout.finishRefresh(300, success, false)
        markIdle()
    }

    override fun setLoadMore(success: Boolean, noMoreData: Boolean) {
        if (!mLoadMoreEnable) {
            return
        }
        mSmartRefreshLayout.finishLoadMore(300,success,noMoreData)
    }
}