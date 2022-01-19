package com.qw.recyclerview.smartrefreshlayout

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.*
import com.qw.recyclerview.loadmore.State
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * Created by qinwei on 2021/6/29 21:44
 */
class SmartRefreshLayoutRecyclerView(
    private val mRecyclerView: RecyclerView,
    private val mSmartRefreshLayout: SmartRefreshLayout
) : SmartRefreshable {
    private var mRefreshEnable: Boolean = false
    private var mLoadMoreEnable: Boolean = false
    private var onRefreshListener: OnRefreshListener? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var state = SmartRefreshable.REFRESH_IDLE
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
                if (state != SmartRefreshable.REFRESH_IDLE) return

                if (newState == RecyclerView.SCROLL_STATE_IDLE && checkedIsNeedLoadMore()) {
                    state = SmartRefreshable.REFRESH_UP
                    onLoadMoreListener?.onStateChanged(State.LOADING)
                    SRLog.d("SmartRefreshLayoutRecyclerView onScrollStateChanged onLoadMore")
                    onLoadMoreListener?.onLoadMore()
                }
            }
        })
        mSmartRefreshLayout.setOnRefreshListener {
            state = SmartRefreshable.REFRESH_PULL
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
        mSmartRefreshLayout.setEnableRefresh(isEnabled)
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
            mSmartRefreshLayout.autoRefresh()
        }
    }

    override fun finishRefresh(success: Boolean) {
        mSmartRefreshLayout.finishRefresh(success)
        if (mLoadMoreEnable) {
            loadMoreState = if (success) {
                State.IDLE
            } else {
                State.ERROR
            }
            onLoadMoreListener?.onStateChanged(loadMoreState)
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