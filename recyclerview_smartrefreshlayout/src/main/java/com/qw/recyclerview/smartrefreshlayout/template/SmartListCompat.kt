package com.qw.recyclerview.smartrefreshlayout.template

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.*
import com.qw.recyclerview.layout.MyGridLayoutManager
import com.qw.recyclerview.loadmore.AbsLoadMore
import com.qw.recyclerview.loadmore.State
import com.qw.recyclerview.smartrefreshlayout.SmartRecyclerView
import com.qw.recyclerview.template.ListCompat
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * SwipeRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SmartListCompat<T> constructor(
    private val mRecyclerView: RecyclerView,
    mSmartRefreshLayout: SmartRefreshLayout
) : ListCompat<T>(mRecyclerView) {

    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var loadMore: AbsLoadMore? = null
    private val typeLoadMore = -1
    val smart: ISmartRecyclerView = SmartRecyclerView(mRecyclerView, mSmartRefreshLayout).apply {
        setRefreshEnable(false)
        setLoadMoreEnable(false)
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (viewType == typeLoadMore) {
            SRLog.d("SmartListCompat typeLoadMore getView")
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

    final override fun getItemViewType(position: Int): Int {
        if (isLoadMoreShow(position)) {
            return typeLoadMore
        }
        return getItemViewTypeByPosition(position)
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

    fun isLoadMoreShow(position: Int): Boolean {
        return smart.isLoadMoreEnable() && adapter.itemCount - 1 == position
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