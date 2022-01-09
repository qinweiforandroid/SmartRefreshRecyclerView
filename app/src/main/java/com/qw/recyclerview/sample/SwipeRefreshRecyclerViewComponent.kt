package com.qw.recyclerview.sample

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.*
import com.qw.recyclerview.core.ILoadMore
import com.qw.recyclerview.core.adapter.BaseListAdapter
import com.qw.recyclerview.core.adapter.BaseViewHolder
import com.qw.recyclerview.swiperefresh.SwipeRefreshRecyclerView

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
class SwipeRefreshRecyclerViewComponent<T> {
    private lateinit var mLoadMoreView: ILoadMore
    private val mRecyclerView: RecyclerView
    private val mSwipeRefreshLayout: SwipeRefreshLayout
    private val smartRefresh: SmartRefreshHelper = SmartRefreshHelper()
    private val modules = ArrayList<T>()
    private var mInnerAdapter: BaseListAdapter = ListAdapter()
    private var mLoadMoreState = State.IDLE
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private val typeLoadMore = -1

    private lateinit var mAdapter: BaseListAdapter

    constructor(activity: Activity) {
        mRecyclerView = activity.findViewById(R.id.mRecyclerView)
        mSwipeRefreshLayout = activity.findViewById(R.id.mSwipeRefreshLayout)
        init(activity)
    }

    constructor(fragment: Fragment) {
        mRecyclerView = fragment.requireView().findViewById(R.id.mRecyclerView)
        mSwipeRefreshLayout = fragment.requireView().findViewById(R.id.mSwipeRefreshLayout)
        init(fragment.requireContext())
    }

    private fun init(context: Context) {
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
                if (state == State.NO_MORE || state == State.IDLE) {
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
            return mAdapter.onCreateViewHolder(parent, viewType)
        }

        override fun getItemViewType(position: Int): Int {
            if (isLoadMoreViewShow(position)) {
                return typeLoadMore
            }
            return mAdapter.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            var count = modules.size
            if (smartRefresh.isLoadMoreEnable()) {
                count++
            }
            return count
        }
    }

    private fun isLoadMoreViewShow(position: Int): Boolean {
        return mInnerAdapter.itemCount - 1 == position
    }

    fun setAdapter(adapter: BaseListAdapter) {
        this.mAdapter = adapter
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