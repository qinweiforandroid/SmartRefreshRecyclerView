package com.qw.recyclerview.swiperefresh.template

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.SRLog
import com.qw.recyclerview.layout.MyGridLayoutManager
import com.qw.recyclerview.loadmore.AbsLoadMore
import com.qw.recyclerview.loadmore.State
import com.qw.recyclerview.swiperefresh.SwipeRecyclerView
import com.qw.recyclerview.template.ListCompat

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SwipeListCompat<T> constructor(
    private val mRecyclerView: RecyclerView,
    mSwipeRefreshLayout: SwipeRefreshLayout
) : ListCompat<T>(mRecyclerView) {
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var loadMore: AbsLoadMore? = null
    private val typeLoadMore = -1

    val smart: ISmartRecyclerView = SwipeRecyclerView(mRecyclerView, mSwipeRefreshLayout).apply {
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        setRefreshEnable(false)
        setLoadMoreEnable(false)
    }

    fun supportLoadMore(
        loadMore: AbsLoadMore,
        onLoadMoreListener: OnLoadMoreListener
    ) {
        this.loadMore = loadMore
        this.loadMore!!.setOnRetryListener {
            this.loadMore!!.onStateChanged(State.LOADING)
            adapter.notifyItemChanged(adapter.itemCount - 1)
            onLoadMoreListener.onLoadMore()
        }
        this.onLoadMoreListener = onLoadMoreListener
        smart.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                onLoadMoreListener.onLoadMore()
            }

            override fun onStateChanged(state: State) {
                loadMore.onStateChanged(state)
                adapter.notifyItemChanged(adapter.itemCount - 1)
            }
        })
        smart.setLoadMoreEnable(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (viewType == typeLoadMore) {
            SRLog.d("SwipeListCompat onCreateViewHolder load more type")
            return loadMore!!.onCreateLoadMoreViewHolder(parent)
        }
        return onCreateBaseViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int {
        var count = super.getItemCount()
        if (smart.isLoadMoreEnable()) {
            count++
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        if (isLoadMoreShow(position)) {
            return typeLoadMore
        }
        return getItemViewTypeByPosition(position)
    }

    protected abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    protected open fun getItemViewTypeByPosition(position: Int): Int = 0

    private fun isLoadMoreShow(position: Int): Boolean {
        return smart.isLoadMoreEnable() && adapter.itemCount - 1 == position
    }

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

    fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        smart.finishLoadMore(success, noMoreData)
    }

    fun getGridLayoutManager(spanCount: Int): MyGridLayoutManager {
        return MyGridLayoutManager(mRecyclerView.context, spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isLoadMoreShow(position)) {
                        spanCount
                    } else {
                        1
                    }
                }
            }
        }
    }
}