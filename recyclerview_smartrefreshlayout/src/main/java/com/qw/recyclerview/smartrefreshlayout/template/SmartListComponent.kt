package com.qw.recyclerview.smartrefreshlayout.template

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.*
import com.qw.recyclerview.layout.ILayoutManager
import com.qw.recyclerview.layout.MyGridLayoutManager
import com.qw.recyclerview.loadmore.AbsLoadMore
import com.qw.recyclerview.loadmore.State
import com.qw.recyclerview.smartrefreshlayout.SmartRecyclerView
import com.qw.recyclerview.template.BaseListComponent
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SmartListComponent<T> constructor(
    private val mRecyclerView: RecyclerView,
    private val mSmartRefreshLayout: SmartRefreshLayout
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
    val smart: ISmartRecyclerView = SmartRecyclerView(mRecyclerView, mSmartRefreshLayout).apply {
        setRefreshEnable(false)
        setLoadMoreEnable(false)
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
    }
    val adapter: BaseListAdapter
        get() {
            return listComponent.adapter
        }

    val modules: ArrayList<T>
        get() {
            return listComponent.modules
        }


    fun supportLoadMore(loadMore: AbsLoadMore, onLoadMoreListener: OnLoadMoreListener) {
        this.loadMore = loadMore
        this.loadMore?.setOnRetryListener {
            this.loadMore?.onStateChanged(State.LOADING)
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
    }

    abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    open fun getItemViewTypeByPosition(position: Int): Int = 0
    fun setLayoutManager(layoutManager: ILayoutManager): ISmartRecyclerView {
        listComponent.setLayoutManager(layoutManager.getLayoutManager())
        return smart
    }

    fun isLoadMoreShow(position: Int): Boolean {
        return smart.isLoadMoreEnable() && adapter.itemCount - 1 == position
    }

    fun getSwipeRefreshLayout(): SmartRefreshLayout {
        return mSmartRefreshLayout
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