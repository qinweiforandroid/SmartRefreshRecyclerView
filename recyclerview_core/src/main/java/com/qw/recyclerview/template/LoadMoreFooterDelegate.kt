package com.qw.recyclerview.template

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.layout.MyGridLayoutManager
import com.qw.recyclerview.loadmore.AbsLoadMore
import com.qw.recyclerview.loadmore.State

/**
 * Handles footer UI, retry wiring and load-more state updates for SmartListCompat.
 */
internal class LoadMoreFooterDelegate(
    private val smart: ISmartRecyclerView,
    private val adapterProvider: () -> RecyclerView.Adapter<*>
) {

    private var loadMore: AbsLoadMore? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null

    fun setLoadMore(loadMore: AbsLoadMore) {
        this.loadMore = loadMore
        loadMore.setOnRetryListener {
            this.loadMore?.onStateChanged(State.LOADING)
            notifyFooterChanged()
            onLoadMoreListener?.onLoadMore()
        }
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener
        smart.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                onLoadMoreListener.onLoadMore()
            }

            override fun onStateChanged(state: State) {
                loadMore?.let {
                    it.onStateChanged(state)
                    notifyFooterChanged()
                }
            }
        })
    }

    fun getExtraItemCount(): Int {
        return if (isFooterEnabled()) 1 else 0
    }

    fun resolveItemViewType(
        position: Int,
        dataItemCount: Int,
        contentItemViewType: () -> Int
    ): Int {
        return if (isFooterPosition(position, dataItemCount)) {
            LOAD_MORE_VIEW_TYPE
        } else {
            contentItemViewType()
        }
    }

    fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
        contentViewHolder: () -> BaseViewHolder
    ): BaseViewHolder {
        return if (viewType == LOAD_MORE_VIEW_TYPE) {
            checkNotNull(loadMore) { "Load more view must be configured before footer creation." }
                .onCreateLoadMoreViewHolder(parent)
        } else {
            contentViewHolder()
        }
    }

    fun createGridLayoutManager(
        recyclerView: RecyclerView,
        spanCount: Int,
        dataItemCountProvider: () -> Int
    ): MyGridLayoutManager {
        return MyGridLayoutManager(recyclerView.context, spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isFooterPosition(position, dataItemCountProvider())) {
                        spanCount
                    } else {
                        1
                    }
                }
            }
        }
    }

    private fun isFooterEnabled(): Boolean {
        return smart.isLoadMoreEnable() && loadMore != null
    }

    private fun isFooterPosition(position: Int, dataItemCount: Int): Boolean {
        return isFooterEnabled() && position == dataItemCount
    }

    private fun notifyFooterChanged() {
        val adapter = adapterProvider()
        if (adapter.itemCount > 0) {
            adapter.notifyItemChanged(adapter.itemCount - 1)
        }
    }

    private companion object {
        const val LOAD_MORE_VIEW_TYPE = -1
    }
}
