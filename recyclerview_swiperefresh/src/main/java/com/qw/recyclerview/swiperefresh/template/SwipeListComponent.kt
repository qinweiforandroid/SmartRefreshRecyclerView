package com.qw.recyclerview.swiperefresh.template

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.SRLog
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
    mRecyclerView: RecyclerView,
    private val mSwipeRefreshLayout: SwipeRefreshLayout
) : BaseListComponent<T>(mRecyclerView) {

    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var loadMore: AbsLoadMore? = null
    private val typeLoadMore = -1
    val smart: ISmartRecyclerView = SwipeRecyclerView(mRecyclerView, mSwipeRefreshLayout).apply {
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        setRefreshEnable(false)
        setLoadMoreEnable(false)
    }

    fun supportLoadMore(loadMore: AbsLoadMore, onLoadMoreListener: OnLoadMoreListener) {
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
    }

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

    final override fun getItemViewType(position: Int): Int {
        if (isLoadMoreShow(position)) {
            return typeLoadMore
        }
        return getItemViewTypeByPosition(position)
    }


    abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    open fun getItemViewTypeByPosition(position: Int): Int = 0

    fun isLoadMoreShow(position: Int): Boolean {
        return smart.isLoadMoreEnable() && adapter.itemCount - 1 == position
    }

    fun getSwipeRefreshLayout(): SwipeRefreshLayout {
        return mSwipeRefreshLayout
    }
}