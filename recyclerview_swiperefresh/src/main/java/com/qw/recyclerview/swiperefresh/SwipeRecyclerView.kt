package com.qw.recyclerview.swiperefresh

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.*
import com.qw.recyclerview.loadmore.State
import java.lang.IllegalArgumentException

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
                SRLog.d("SwipeRefreshRecyclerView onScrollStateChanged newState:$newState")
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
                    SRLog.d("SwipeRefreshRecyclerView onScrollStateChanged onLoadMore")
                    onLoadMoreListener?.onLoadMore()
                }
            }
        })
        mSwipeRefreshLayout.setOnRefreshListener {
            state = ISmartRecyclerView.REFRESH_PULL
            SRLog.d("SwipeRefreshRecyclerView onRefresh")
            onRefreshListener?.onRefresh()
        }
        mSwipeRefreshLayout.isEnabled = mRefreshEnable

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
                layoutManager.findLastCompletelyVisibleItemPositions(null)[layoutManager.findLastCompletelyVisibleItemPositions(
                    null
                ).size - 1]
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

    override fun autoRefresh() {
        if (mRefreshEnable) {
            state = ISmartRecyclerView.REFRESH_PULL
            mSwipeRefreshLayout.isRefreshing = true
            onRefreshListener?.onRefresh()
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
        SRLog.d("SwipeRefreshRecyclerView finishRefresh success:$success")
        mSwipeRefreshLayout.isRefreshing = false
        if (mLoadMoreEnable) {
            loadMoreState = state
            onLoadMoreListener?.onStateChanged(state)
        }
        markIdle()
    }

    override fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        SRLog.d("SwipeRefreshRecyclerView finishLoadMore success:$success ,noMoreData:$noMoreData")
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