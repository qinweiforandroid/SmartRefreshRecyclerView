package com.qw.recyclerview.smartrefreshlayout

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.*
import com.qw.recyclerview.core.adapter.BaseListAdapter
import com.qw.recyclerview.core.adapter.BaseViewHolder
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SmartRefresh1ListComponent<T> constructor(
    private val mRecyclerView: RecyclerView,
    private val mSmartRefreshLayout: SmartRefreshLayout
) {
    private lateinit var mLoadMoreView: ILoadMore
    private val smartRefresh: SmartRefreshHelper = SmartRefreshHelper()
    private val modules = ArrayList<T>()
    private var mInnerAdapter: BaseListAdapter = ListAdapter()
    private var onLoadMoreListener: OnLoadMoreListener? = null

    init {
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        mRecyclerView.adapter = mInnerAdapter
        smartRefresh.inject(SmartRefreshLayout1RecyclerView(mRecyclerView, mSmartRefreshLayout))
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
        })
    }

    inner class ListAdapter : BaseListAdapter() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return this@SmartRefresh1ListComponent.onCreateViewHolder(parent, viewType)
        }

        override fun getItemViewType(position: Int): Int {
            return this@SmartRefresh1ListComponent.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            return modules.size
        }
    }

    open fun getItemViewType(position: Int): Int = 0

    abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

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