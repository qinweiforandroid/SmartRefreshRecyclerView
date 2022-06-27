package com.qw.recyclerview.smartrefreshlayout

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.*
import com.qw.recyclerview.layout.ILayoutManager
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
    private var mLoadMoreEnable: Boolean = false
    private var onRefreshListener: OnRefreshListener? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var state = ISmartRecyclerView.REFRESH_IDLE
    private var loadMoreState = State.IDLE

    init {
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                SRLog.d("SmartRefreshLayoutRecyclerView onScrollStateChanged newState:$newState")
                if (!mLoadMoreEnable) return
                if (onLoadMoreListener == null) return
                when (loadMoreState) {
                    State.NO_MORE,
                    State.ERROR,
                    State.EMPTY -> {
                        return
                    }
                    else -> {
                    }
                }
                if (state != ISmartRecyclerView.REFRESH_IDLE) return

                if (newState == RecyclerView.SCROLL_STATE_IDLE && checkedIsNeedLoadMore()) {
                    state = ISmartRecyclerView.REFRESH_UP
                    onLoadMoreListener?.onStateChanged(State.LOADING)
                    SRLog.d("SmartRefreshLayoutRecyclerView onScrollStateChanged onLoadMore")
                    onLoadMoreListener?.onLoadMore()
                }
            }
        })
        mSmartRefreshLayout.setOnRefreshListener {
            state = ISmartRecyclerView.REFRESH_PULL
            onRefreshListener?.onRefresh()
        }
        mSmartRefreshLayout.setEnableRefresh(mRefreshEnable)
        //使用adapter的loadmore功能
        mSmartRefreshLayout.setEnableLoadMore(false)

        if (mRecyclerView.adapter == null) {
            throw IllegalArgumentException("RecyclerView must be setAdapter")
        }
    }

    private fun markIdle() {
        state = ISmartRecyclerView.REFRESH_IDLE
    }

    private fun checkedIsNeedLoadMore(): Boolean {
        var lastVisiblePosition = 0
        val layoutManager: RecyclerView.LayoutManager = mRecyclerView.layoutManager!!
        if (layoutManager is LinearLayoutManager) {
            lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            lastVisiblePosition =
                layoutManager.findLastCompletelyVisibleItemPositions(null)[layoutManager
                    .findLastCompletelyVisibleItemPositions(
                        null
                    ).size - 1]
        } else {
            if (layoutManager is ILayoutManager) {
                lastVisiblePosition = layoutManager.getLastVisibleItemPosition()
            }
        }
        return mRecyclerView.adapter!!.itemCount - lastVisiblePosition <= 5
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
        return this
    }

    override fun isLoadMoreEnable(): Boolean {
        return mLoadMoreEnable
    }

    override fun autoRefresh() {
        if (mRefreshEnable) {
            state = ISmartRecyclerView.REFRESH_PULL
            mSmartRefreshLayout.autoRefresh()
        }
    }

    override fun finishRefresh(success: Boolean) {
        finishRefresh(
            success, if (success) {
                State.IDLE
            } else {
                State.ERROR
            }
        )
    }

    override fun finishRefresh(success: Boolean, state: State) {
        mSmartRefreshLayout.finishRefresh(success)
        if (mLoadMoreEnable) {
            loadMoreState = state
            onLoadMoreListener?.onStateChanged(state)
        }
        markIdle()
    }

    override fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        if (!mLoadMoreEnable) {
            return
        }
        loadMoreState = if (success) {
            if (noMoreData) {
                State.NO_MORE
            } else {
                State.IDLE
            }
        } else {
            State.ERROR
        }
        onLoadMoreListener?.onStateChanged(loadMoreState)
        markIdle()
    }
}