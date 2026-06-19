package com.qw.recyclerview.template

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.ItemViewDelegate
import com.qw.recyclerview.core.MultiTypeUseCase
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.loadmore.AbsLoadMore
import com.qw.recyclerview.loadmore.LoadMoreState
import com.qw.recyclerview.page.IPage

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SmartListCompat<T>(private val smart: ISmartRecyclerView) :
    ListCompat<T>(smart.recyclerView) {

    private val loadMoreFooterDelegate = LoadMoreFooterDelegate(
        smart = smart,
        adapterProvider = { adapter }
    )
    private val pagingDataDelegate = PagingDataDelegate(
        smart = smart,
        modulesProvider = { modules },
        adapterProvider = { adapter }
    )

    init {
        smart.apply {
            (recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            setRefreshEnable(false)
            setLoadMoreEnable(false)
        }
    }

    class MultiTypeBuilder {
        private val mMultiType = MultiTypeUseCase()

        fun register(
            viewType: Int,
            delegate: ItemViewDelegate
        ): MultiTypeBuilder {
            mMultiType.register(viewType, delegate)
            return this
        }

        fun <T> build(
            smart: ISmartRecyclerView,
            viewTypeProvider: (item: T) -> Int
        ): SmartListCompat<T> {
            return object : SmartListCompat<T>(smart) {
                override fun onCreateBaseViewHolder(
                    parent: ViewGroup, viewType: Int
                ): BaseViewHolder {
                    return mMultiType.onCreateViewHolder(parent, viewType)
                }

                override fun getItemViewTypeByPosition(position: Int): Int {
                    return viewTypeProvider(modules[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return loadMoreFooterDelegate.onCreateViewHolder(parent, viewType) {
            onCreateBaseViewHolder(parent, viewType)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + loadMoreFooterDelegate.getExtraItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        return loadMoreFooterDelegate.resolveItemViewType(
            position = position,
            dataItemCount = super.getItemCount()
        ) {
            getItemViewTypeByPosition(position)
        }
    }

    protected abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    protected open fun getItemViewTypeByPosition(position: Int): Int = 0

    fun setRefreshing(
        refreshing: Boolean,
        afterRefreshCompleted: ISmartRecyclerView.() -> Unit = {}
    ) {
        smart.setRefreshing(refreshing, afterRefreshCompleted)
    }

    fun setLoadMoreResult(result: LoadMoreState) {
        smart.setLoadMoreState(result)
    }

    fun getGridLayoutManager(spanCount: Int): GridLayoutManager {
        return loadMoreFooterDelegate.createGridLayoutManager(
            recyclerView = smart.recyclerView,
            spanCount = spanCount,
            dataItemCountProvider = { super.getItemCount() }
        )
    }

    fun setPaging(page: IPage): SmartListCompat<T> {
        pagingDataDelegate.setPage(page)
        return this
    }

    fun setLoadMoreEnable(isEnabled: Boolean): SmartListCompat<T> {
        this.smart.setLoadMoreEnable(isEnabled)
        return this
    }

    fun setRefreshEnable(isEnabled: Boolean): SmartListCompat<T> {
        this.smart.setRefreshEnable(isEnabled)
        return this
    }

    fun setUpLayoutManager(layoutManager: RecyclerView.LayoutManager): SmartListCompat<T> {
        setLayoutManager(layoutManager)
        return this
    }

    fun setLoadMoreView(loadMore: AbsLoadMore): SmartListCompat<T> {
        loadMoreFooterDelegate.setLoadMore(loadMore)
        return this
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener): SmartListCompat<T> {
        loadMoreFooterDelegate.setOnLoadMoreListener(onLoadMoreListener)
        return this
    }

    fun setOnRefreshListener(onRefreshListener: OnRefreshListener): SmartListCompat<T> {
        smart.setOnRefreshListener(onRefreshListener)
        return this
    }

    fun submitPageData(it: ArrayList<T>) {
        if (pagingDataDelegate.isInitialized) {
            pagingDataDelegate.onPageLoadSuccess(it)
        } else {
            modules.clear()
            modules.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    fun submitPageError() {
        if (pagingDataDelegate.isInitialized) {
            pagingDataDelegate.onPageLoadFailure()
        }
    }
}
