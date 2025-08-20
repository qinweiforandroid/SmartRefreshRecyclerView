package com.qw.recyclerview.swiperefresh

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.SRLog
import com.qw.recyclerview.layout.ILayoutManager
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
    private var mLoadMoreEnable: Boolean = false
    private var onRefreshListener: OnRefreshListener? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var state = ISmartRecyclerView.REFRESH_IDLE

    private var loadMoreState = State.IDLE

    init {
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                SRLog.d("SwipeRecyclerView onScrollStateChanged newState:$newState")
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
                    SRLog.d("SwipeRecyclerView onScrollStateChanged onLoadMore")
                    onLoadMoreListener?.onLoadMore()
                }
            }
        })
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
        mSwipeRefreshLayout.isEnabled = isEnabled
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
        if (mLoadMoreEnable) {
            loadMoreState = footerState
            onLoadMoreListener?.onStateChanged(footerState)
        }
        markIdle()
    }

    override fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        SRLog.d("SwipeRecyclerView finishLoadMore success:$success ,noMoreData:$noMoreData")
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