package com.qw.recyclerview.sample

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.SmartRefreshHelper
import com.qw.recyclerview.core.State
import com.qw.recyclerview.core.adapter.BaseListAdapter
import com.qw.recyclerview.core.adapter.BaseViewHolder
import com.qw.recyclerview.swiperefresh.SwipeRefreshRecyclerView

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SwipeRefreshRecyclerViewComponent<T> {
    private val mRecyclerView: RecyclerView
    private val mSwipeRefreshLayout: SwipeRefreshLayout
    private val smartRefresh: SmartRefreshHelper = SmartRefreshHelper()
    protected val modules = ArrayList<T>()
    private val adapter: ListAdapter = ListAdapter()
    private var loadMoreState = State.IDLE
    private var onLoadMoreListener: OnLoadMoreListener? = null

    private val TYPE_LOAD_MORE = -1

    constructor(activity: Activity) {
        mRecyclerView = activity.findViewById(R.id.mRecyclerView)
        mSwipeRefreshLayout = activity.findViewById(R.id.mSwipeRefreshLayout)
        init()
    }

    constructor(fragment: Fragment) {
        mRecyclerView = fragment.requireView().findViewById(R.id.mRecyclerView)
        mSwipeRefreshLayout = fragment.requireView().findViewById(R.id.mSwipeRefreshLayout)
        init()
    }

    private fun init() {
        mRecyclerView.adapter = adapter
        smartRefresh.inject(SwipeRefreshRecyclerView(mRecyclerView, mSwipeRefreshLayout))
        smartRefresh.setRefreshEnable(true)
        smartRefresh.setLoadMoreEnable(true)
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
                loadMoreState = state
                adapter.notifyDataSetChanged()
            }

            override fun getState(): State {
                return loadMoreState
            }
        })
    }

    inner class ListAdapter : BaseListAdapter() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (viewType == TYPE_LOAD_MORE) {
                return object : BaseViewHolder(FooterView.create(parent.context)) {
                    override fun initData(position: Int) {
                        (itemView as FooterView).onStateChanged(loadMoreState)
                        itemView.setOnFooterViewListener {
                            loadMoreState = State.LOADING
                            itemView.onStateChanged(loadMoreState)
                            onLoadMoreListener?.onLoadMore()
                        }
                    }
                }
            }
            return this@SwipeRefreshRecyclerViewComponent.onCreateViewHolder(parent, viewType)
        }

        override fun getItemViewType(position: Int): Int {
            if (position == itemCount - 1 && isLoadMoreViewShow()) {
                return TYPE_LOAD_MORE
            }
            return this@SwipeRefreshRecyclerViewComponent.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            var count = modules.size
            if (isLoadMoreViewShow()) {
                count++
            }
            return count
        }

        override fun onViewAttachedToWindow(holder: BaseViewHolder) {
            super.onViewAttachedToWindow(holder)
        }
    }

    open fun getItemViewType(position: Int): Int {
        return 0
    }

    abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    private fun isLoadMoreViewShow(): Boolean {
        return true
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
        adapter.notifyDataSetChanged()
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        mRecyclerView.layoutManager = layoutManager
    }

    fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        smartRefresh.finishLoadMore(success, noMoreData)
    }
}