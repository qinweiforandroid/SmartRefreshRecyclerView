package com.qw.recyclerview.swiperefresh.template

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.*
import com.qw.recyclerview.layout.ILayoutManager
import com.qw.recyclerview.layout.MyGridLayoutManager
import com.qw.recyclerview.loadmore.AbsLoadMore
import com.qw.recyclerview.loadmore.State
import com.qw.recyclerview.swiperefresh.SwipeRecyclerView
import com.qw.recyclerview.template.BaseListComponent

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SwipeListComponent<T> constructor(
    private val mRecyclerView: RecyclerView,
    mSwipeRefreshLayout: SwipeRefreshLayout
) {

    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var loadMore: AbsLoadMore? = null
    private val typeLoadMore = -1
    private val listComponent = object : BaseListComponent<T>(mRecyclerView) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (viewType == typeLoadMore) {
                SRLog.d("SwipeRefreshRecyclerViewComponent typeLoadMore getView")
                return loadMore!!.onCreateLoadMoreViewHolder(parent)
            }
            return onCreateBaseViewHolder(parent, viewType)
        }

        override fun onViewAttachedToWindow(holder: BaseViewHolder) {
            val lp = holder.itemView.layoutParams
            if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
                lp.isFullSpan = isLoadMoreShow(holder.layoutPosition)
            }
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
    }

    val adapter: BaseListAdapter
        get() {
            return listComponent.adapter
        }

    val modules: ArrayList<T>
        get() {
            return listComponent.modules
        }

    private val smart: ISmartRecyclerView =
        SwipeRecyclerView(mRecyclerView, mSwipeRefreshLayout).apply {
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
            listComponent.adapter.notifyItemChanged(listComponent.adapter.itemCount - 1)
            onLoadMoreListener.onLoadMore()
        }
        this.onLoadMoreListener = onLoadMoreListener
        smart.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                onLoadMoreListener.onLoadMore()
            }

            override fun onStateChanged(state: State) {
                loadMore.onStateChanged(state)
                listComponent.adapter.notifyItemChanged(listComponent.adapter.itemCount - 1)
            }
        })
        smart.setLoadMoreEnable(true)
    }


    protected abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    protected open fun getItemViewTypeByPosition(position: Int): Int = 0

    fun isLoadMoreShow(position: Int): Boolean {
        return smart.isLoadMoreEnable() && adapter.itemCount - 1 == position
    }

    fun setLayoutManager(layoutManager: ILayoutManager): ISmartRecyclerView {
        listComponent.setLayoutManager(layoutManager.getLayoutManager())
        return smart
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