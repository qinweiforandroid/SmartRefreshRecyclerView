package com.qw.recyclerview.swiperefresh

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.*
import com.qw.recyclerview.core.adapter.BaseListAdapter
import com.qw.recyclerview.core.adapter.BaseViewHolder

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SwipeRefreshListComponent<T> constructor(
    private val mRecyclerView: RecyclerView,
    private val mSwipeRefreshLayout: SwipeRefreshLayout
) {
    private lateinit var mLoadMoreView: ILoadMore
    private val smartRefresh: SmartRefreshHelper = SmartRefreshHelper()
    private val modules = ArrayList<T>()
    private var mInnerAdapter: BaseListAdapter = ListAdapter()
    private var mLoadMoreState = State.IDLE
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private val typeLoadMore = -1


    init {
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        mRecyclerView.adapter = mInnerAdapter
        smartRefresh.inject(SwipeRefreshRecyclerView(mRecyclerView, mSwipeRefreshLayout))
        smartRefresh.setRefreshEnable(false)
        smartRefresh.setLoadMoreEnable(false)
    }

    fun setRefreshEnable(isEnabled: Boolean) {
        smartRefresh.setRefreshEnable(isEnabled)
    }

    fun setLoadMoreEnable(isEnabled: Boolean) {
        smartRefresh.setLoadMoreEnable(isEnabled)
    }

    fun injectLoadMore(loadMore: ILoadMore) {
        this.mLoadMoreView = loadMore
    }

    fun setOnRefreshListener(onRefreshListener: OnRefreshListener) {
        smartRefresh.setOnRefreshListener(onRefreshListener)
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener
        smartRefresh.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                onLoadMoreListener.onLoadMore()
            }

            override fun onStateChanged(state: State) {
                mLoadMoreState = state
                SRLog.d("SwipeRefreshRecyclerViewComponent onStateChanged:${mLoadMoreState.name} ")
                //解决第一次加载无数据情况
                if (state == State.NO_MORE || state == State.IDLE || state == State.ERROR) {
                    if (modules.size == 0) {
                        mLoadMoreState = State.EMPTY
                    }
                }
                mInnerAdapter.notifyItemChanged(mInnerAdapter.itemCount - 1)
//                mInnerAdapter.notifyDataSetChanged()
            }

            override fun getState(): State {
                return mLoadMoreState
            }
        })
    }

    inner class ListAdapter : BaseListAdapter() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (viewType == typeLoadMore) {
                SRLog.d("SwipeRefreshRecyclerViewComponent typeLoadMore getView")
                return object : BaseViewHolder(mLoadMoreView.getView(parent.context)) {
                    init {
                        mLoadMoreView.setOnRetryListener {
                            mLoadMoreState = State.LOADING
                            mLoadMoreView.onStateChanged(mLoadMoreState)
                            onLoadMoreListener?.onLoadMore()
                        }
                    }

                    override fun initData(position: Int) {
                        SRLog.d("SwipeRefreshRecyclerViewComponent initData:${mLoadMoreState.name}")
                        mLoadMoreView.onStateChanged(mLoadMoreState)
                    }
                }
            }
            return this@SwipeRefreshListComponent.onCreateViewHolder(parent, viewType)
        }

        override fun getItemViewType(position: Int): Int {
            if (isLoadMoreViewShow(position)) {
                return typeLoadMore
            }
            return this@SwipeRefreshListComponent.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            var count = modules.size
            if (smartRefresh.isLoadMoreEnable()) {
                count++
            }
            return count
        }

        override fun onViewAttachedToWindow(holder: BaseViewHolder) {
            super.onViewAttachedToWindow(holder)
            val lp = holder.itemView.layoutParams
            if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
                lp.isFullSpan = isLoadMoreViewShow(holder.layoutPosition)
            }
        }
    }

    open fun getItemViewType(position: Int): Int = 0

    abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    fun isLoadMoreViewShow(position: Int): Boolean {
        return smartRefresh.isLoadMoreEnable() && mInnerAdapter.itemCount - 1 == position
    }

    fun autoRefresh() {
        smartRefresh.autoRefresh()
    }

    fun clear() {
        modules.clear()
    }

    fun add(t: T) {
        modules.add(t)
    }

    fun size(): Int {
        return modules.size
    }

    fun getRecyclerView(): RecyclerView {
        return mRecyclerView
    }

    fun getSwipeRefreshLayout(): SwipeRefreshLayout {
        return mSwipeRefreshLayout
    }

    fun finishRefresh(b: Boolean) {
        smartRefresh.finishRefresh(b)
    }

    fun notifyDataSetChanged() {
        mInnerAdapter.notifyDataSetChanged()
    }

    fun notifyItemRangeInserted(positionStart: Int, itemCount: Int) {
        mInnerAdapter.notifyItemRangeInserted(positionStart, itemCount)
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        mRecyclerView.layoutManager = layoutManager
    }

    fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        smartRefresh.finishLoadMore(success, noMoreData)
    }

    fun getItem(position: Int): T {
        return modules[position]
    }
}