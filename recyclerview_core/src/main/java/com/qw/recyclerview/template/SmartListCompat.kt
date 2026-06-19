package com.qw.recyclerview.template

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.IItemViewType
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.ItemViewDelegate
import com.qw.recyclerview.core.MultiTypeUseCase
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.loadmore.AbsLoadMore
import com.qw.recyclerview.loadmore.LoadMoreResult
import com.qw.recyclerview.loadmore.State
import com.qw.recyclerview.page.IPage

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SmartListCompat<T>(private val smart: ISmartRecyclerView) :
    ListCompat<T>(smart.getRecyclerView()) {

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
            (getRecyclerView().itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
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

        fun <T> create(smart: ISmartRecyclerView): SmartListCompat<T> {
            return object : SmartListCompat<T>(smart) {
                override fun onCreateBaseViewHolder(
                    parent: ViewGroup, viewType: Int
                ): BaseViewHolder {
                    return mMultiType.onCreateViewHolder(parent, viewType)
                }

                override fun getItemViewTypeByPosition(position: Int): Int {
                    val item = modules[position]
                    if (item is IItemViewType) {
                        return item.getItemViewType()
                    }
                    throw IllegalArgumentException("module must be impl IItemViewType interface")
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

    fun autoRefresh() {
        smart.autoRefresh()
    }

    /**
     * 刷新完成更新ui
     */
    fun finishRefresh(success: Boolean) {
        smart.finishRefresh(success)
    }

    /**
     * 刷新完成更新ui
     * @param success true刷新成功，false 刷新失敗
     * @param state 加载更多状态
     */
    fun finishRefresh(success: Boolean, state: State) {
        smart.finishRefresh(success, state)
    }

    fun finishLoadMore(result: LoadMoreResult) {
        smart.finishLoadMore(result)
    }

    fun getGridLayoutManager(spanCount: Int): GridLayoutManager {
        return loadMoreFooterDelegate.createGridLayoutManager(
            recyclerView = smart.getRecyclerView(),
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
        pagingDataDelegate.submitPageData(it)
    }

    fun submitPageError() {
        pagingDataDelegate.submitPageError()
    }
}
