package com.qw.recyclerview.swiperefresh

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.SRLog
import com.qw.recyclerview.core.SmartRefreshable
import com.qw.recyclerview.core.State
import java.lang.IllegalArgumentException

/**
 * Created by qinwei on 2021/6/29 21:44
 */
class SwipeRefreshRecyclerView(
    private val mRecyclerView: RecyclerView,
    private val mSwipeRefreshLayout: SwipeRefreshLayout
) : SmartRefreshable {
    private var mRefreshEnable: Boolean = false
    private var mLoadMoreEnable: Boolean = false
    private var onRefreshListener: OnRefreshListener? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var state = SmartRefreshable.REFRESH_IDLE

    init {
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                SRLog.d("SwipeRefreshRecyclerView onScrollStateChanged newState:$newState")
                if (!mLoadMoreEnable) return
                if (onLoadMoreListener == null) return
                when (onLoadMoreListener?.getState()) {
                    State.NO_MORE,
                    State.EMPTY -> {
                        return
                    }
                    else -> {}
                }
                if (state != SmartRefreshable.REFRESH_IDLE) return

                if (newState == RecyclerView.SCROLL_STATE_IDLE && checkedIsNeedLoadMore()) {
                    state = SmartRefreshable.REFRESH_UP
                    onLoadMoreListener?.onStateChanged(State.LOADING)
                    SRLog.d("SwipeRefreshRecyclerView onScrollStateChanged onLoadMore")
                    onLoadMoreListener?.onLoadMore()
                }
            }
        })
        mSwipeRefreshLayout.setOnRefreshListener {
            state = SmartRefreshable.REFRESH_PULL
            SRLog.d("SwipeRefreshRecyclerView onRefresh")
            onRefreshListener?.onRefresh()
        }
        mSwipeRefreshLayout.isEnabled = mRefreshEnable

        if (mRecyclerView.adapter == null) {
            throw IllegalArgumentException("RecyclerView must be setAdapter")
        }
    }

    private fun markIdle() {
        state = SmartRefreshable.REFRESH_IDLE
    }

    private fun checkedIsNeedLoadMore(): Boolean {
        var lastVisiblePosition = 0
        val layoutManager: RecyclerView.LayoutManager = mRecyclerView.layoutManager!!
        if (layoutManager is LinearLayoutManager) {
            lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val sdlm = layoutManager
            lastVisiblePosition =
                sdlm.findLastCompletelyVisibleItemPositions(null)[sdlm.findLastCompletelyVisibleItemPositions(
                    null
                ).size - 1]
        }
        return mRecyclerView.adapter!!.itemCount - lastVisiblePosition <= 5
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
        mSwipeRefreshLayout.isEnabled = isEnabled
    }

    override fun isRefreshEnable(): Boolean {
        return mRefreshEnable
    }

    override fun setLoadMoreEnable(isEnabled: Boolean) {
        mLoadMoreEnable = isEnabled
    }

    override fun isLoadMoreEnable(): Boolean {
        return mLoadMoreEnable
    }

    override fun autoRefresh() {
        if (mRefreshEnable) {
            state = SmartRefreshable.REFRESH_PULL
            mSwipeRefreshLayout.isRefreshing = true
            onRefreshListener?.onRefresh()
        }
    }

    override fun finishRefresh(success: Boolean) {
        SRLog.d("SwipeRefreshRecyclerView finishRefresh success:$success")
        mSwipeRefreshLayout.isRefreshing = false
        if (success) {
            onLoadMoreListener?.onStateChanged(State.IDLE)
        }
        markIdle()
    }

    override fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        SRLog.d("SwipeRefreshRecyclerView finishLoadMore success:$success ,noMoreData:$noMoreData")
        if (!mLoadMoreEnable) {
            return
        }
        val state: State = if (success) {
            if (noMoreData) {
                State.NO_MORE
            } else {
                State.IDLE
            }
        } else {
            State.ERROR
        }
        onLoadMoreListener?.onStateChanged(state)
        markIdle()
    }
}