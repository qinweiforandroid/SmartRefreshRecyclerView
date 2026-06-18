package com.qw.recyclerview.core

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.layout.ILayoutManager
import com.qw.recyclerview.loadmore.LoadMoreResult
import com.qw.recyclerview.loadmore.State

/**
 * Coordinates scroll-driven load more behavior for a RecyclerView host.
 */
class ScrollLoadMoreCoordinator(
    private val recyclerView: RecyclerView,
    private val logTag: String,
    private val host: Host
) {

    interface Host {
        fun canTriggerLoadMore(): Boolean

        fun onLoadMoreTriggered()
    }

    private var loadMoreEnable = false
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var loadMoreState = State.IDLE

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                SRLog.d("$logTag onScrollStateChanged newState:$newState")
                if (!shouldTriggerLoadMore(newState)) {
                    return
                }
                host.onLoadMoreTriggered()
                onLoadMoreListener?.onStateChanged(State.LOADING)
                SRLog.d("$logTag onScrollStateChanged onLoadMore")
                onLoadMoreListener?.onLoadMore()
            }
        })
    }

    fun setLoadMoreEnable(isEnabled: Boolean) {
        loadMoreEnable = isEnabled
    }

    fun isLoadMoreEnable(): Boolean {
        return loadMoreEnable
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    fun syncLoadMoreState(state: State) {
        if (!loadMoreEnable) {
            return
        }
        loadMoreState = state
        onLoadMoreListener?.onStateChanged(state)
    }

    fun finishLoadMore(result: LoadMoreResult) {
        if (!loadMoreEnable) {
            return
        }
        loadMoreState = result.state
        onLoadMoreListener?.onStateChanged(loadMoreState)
    }

    private fun shouldTriggerLoadMore(newState: Int): Boolean {
        if (!loadMoreEnable) {
            return false
        }
        if (onLoadMoreListener == null) {
            return false
        }
        when (loadMoreState) {
            State.NO_MORE,
            State.ERROR,
            State.EMPTY -> {
                return false
            }

            else -> {
            }
        }
        if (!host.canTriggerLoadMore()) {
            return false
        }
        return newState == RecyclerView.SCROLL_STATE_IDLE && isNearListEnd()
    }

    private fun isNearListEnd(): Boolean {
        val layoutManager = recyclerView.layoutManager ?: return false
        val adapter = recyclerView.adapter ?: return false
        val lastVisiblePosition = when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is StaggeredGridLayoutManager -> {
                val positions = layoutManager.findLastCompletelyVisibleItemPositions(null)
                positions[positions.size - 1]
            }

            is ILayoutManager -> layoutManager.getLastVisibleItemPosition()
            else -> return false
        }
        return adapter.itemCount - lastVisiblePosition <= LOAD_MORE_PRELOAD_THRESHOLD
    }

    private companion object {
        const val LOAD_MORE_PRELOAD_THRESHOLD = 5
    }
}
