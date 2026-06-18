package com.qw.recyclerview.template

import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.loadmore.LoadMoreResult
import com.qw.recyclerview.page.IPage

/**
 * Applies paged data and error states onto SmartListCompat's modules and adapter.
 */
internal class PagingDataDelegate<T>(
    private val smart: ISmartRecyclerView,
    private val modulesProvider: () -> ArrayList<T>,
    private val adapterProvider: () -> RecyclerView.Adapter<*>
) {

    private var page: IPage? = null

    fun setPage(page: IPage) {
        this.page = page
    }

    fun submitPageData(data: ArrayList<T>) {
        val modules = modulesProvider()
        val adapter = adapterProvider()
        if (smart.isPull()) {
            modules.clear()
            modules.addAll(data)
            smart.finishRefresh(true)
            adapter.notifyDataSetChanged()
            return
        }

        val size = modules.size
        modules.addAll(data)
        smart.finishLoadMore(
            if (requirePage().hasMore()) {
                LoadMoreResult.SUCCESS
            } else {
                LoadMoreResult.NO_MORE
            }
        )
        adapter.notifyItemRangeInserted(size, data.size)
    }

    fun submitPageError() {
        if (smart.isPull()) {
            smart.finishRefresh(false)
        } else {
            smart.finishLoadMore(LoadMoreResult.ERROR)
        }
    }

    private fun requirePage(): IPage {
        return checkNotNull(page) {
            "PagingDataDelegate requires setPage(page) before submitting load-more data."
        }
    }
}
