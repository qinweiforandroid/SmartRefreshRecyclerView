package com.qw.recyclerview.template

import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.loadmore.LoadMoreState
import com.qw.recyclerview.page.IPage
import com.qw.recyclerview.page.PageAction

/**
 * Applies paged data and error states onto SmartListCompat's modules and adapter.
 */
internal class PagingDataDelegate<T>(
    private val smart: ISmartRecyclerView,
    private val modulesProvider: () -> ArrayList<T>,
    private val adapterProvider: () -> RecyclerView.Adapter<*>
) {
    var isInitialized = false
    private var page: IPage? = null

    fun setPage(page: IPage) {
        this.page = page
        this.isInitialized = true
    }

    fun onPageLoadSuccess(data: ArrayList<T>) {
        val page = requirePage()
        val modules = modulesProvider()
        val adapter = adapterProvider()
        if (page.state.action == PageAction.REFRESH) {
            modules.clear()
            modules.addAll(data)
            smart.setRefreshing(false) {
                if (!page.state.hasNextPage) {
                    this.setLoadMoreState(LoadMoreState.NO_MORE)
                } else {
                    this.setLoadMoreState(LoadMoreState.SUCCESS)
                }
            }
            adapter.notifyDataSetChanged()
        } else {
            val size = modules.size
            modules.addAll(data)
            smart.setLoadMoreState(
                if (page.state.hasNextPage) {
                    LoadMoreState.SUCCESS
                } else {
                    LoadMoreState.NO_MORE
                }
            )
            adapter.notifyItemRangeInserted(size, data.size)
        }
    }

    fun onPageLoadFailure() {
        val page = requirePage()
        if (page.state.action == PageAction.REFRESH) {
            smart.setRefreshing(false) {
                val modules = modulesProvider()
                if (modules.isEmpty()) {
                    setLoadMoreState(LoadMoreState.HIDDEN)
                }
            }
        } else {
            smart.setLoadMoreState(LoadMoreState.ERROR)
        }
    }

    private fun requirePage(): IPage {
        return checkNotNull(page) {
            "PagingDataDelegate requires setPage(page) before submitting load-more data."
        }
    }
}
