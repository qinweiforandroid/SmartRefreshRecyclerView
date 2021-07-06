package com.qw.recyclerview.sample.core

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.SmartRefreshHelper
import com.qw.recyclerview.core.adapter.BaseListAdapter
import com.qw.recyclerview.core.adapter.BaseViewHolder
import com.qw.recyclerview.core.footer.FooterView
import com.qw.recyclerview.core.footer.FooterView.OnFooterViewListener
import com.qw.recyclerview.core.footer.IFooter
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.loading.ILoading
import com.qw.recyclerview.sample.loading.LoadingHelper
import com.qw.recyclerview.swiperefresh.SwipeRefreshRecyclerView
import java.util.*

abstract class BaseSwipeRefreshLayoutListActivity<T> : AppCompatActivity(), OnFooterViewListener, OnRefreshListener, OnLoadMoreListener {
    protected lateinit var smartRefresh: SmartRefreshHelper
    protected lateinit var loading: LoadingHelper
    protected lateinit var adapter: ListAdapter
    protected var modules = ArrayList<T>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        initView()
        initData(savedInstanceState)
    }

    protected abstract fun setContentView()
    protected fun initView() {
        val mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        adapter = ListAdapter()
        mRecyclerView.adapter = adapter
        val mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.mSwipeRefreshLayout)

        val mLoading = findViewById<View>(R.id.mLoading)
        if (mLoading != null && mLoading is ILoading) {
            loading = LoadingHelper()
            loading.inject(mLoading)
            loading.setContentView(mSwipeRefreshLayout)
        }

        smartRefresh = SmartRefreshHelper()
        smartRefresh.inject(SwipeRefreshRecyclerView(mRecyclerView, mSwipeRefreshLayout))
        smartRefresh.setLayoutManager(LinearLayoutManager(this))
        smartRefresh.setRefreshEnable(true)
        smartRefresh.setLoadMoreEnable(true)
        smartRefresh.setOnRefreshListener(this)
        smartRefresh.setOnLoadMoreListener(this)
    }

    open fun initData(savedInstanceState: Bundle?) {}
    override fun onRefresh() {}
    override fun onLoadMore() {}
    override fun onFooterClick() {}
    inner class ListAdapter : BaseListAdapter() {
        override fun getItemViewCount(): Int {
            return modules.size
        }

        override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return this@BaseSwipeRefreshLayoutListActivity.onCreateBaseViewHolder(parent, viewType)
        }

        override fun getItemViewTypeByPosition(position: Int): Int {
            return this@BaseSwipeRefreshLayoutListActivity.getItemViewTypeByPosition(position)
        }

        override fun onCreateFooterHolder(parent: ViewGroup): BaseViewHolder {
            val baseViewHolder = this@BaseSwipeRefreshLayoutListActivity.onCreateFooterHolder(parent)
            if (baseViewHolder == null) {
                val footerView = FooterView(this@BaseSwipeRefreshLayoutListActivity)
                footerView.setOnFooterViewListener(this@BaseSwipeRefreshLayoutListActivity)
                footerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                return FooterViewHolder(footerView)
            }
            return baseViewHolder
        }

        inner class FooterViewHolder(itemView: View) : BaseViewHolder(itemView) {
            private var footer: IFooter = if (itemView is IFooter) {
                itemView
            } else {
                throw IllegalArgumentException("the view must impl IFooter interface")
            }

            override fun initData(position: Int) {
                footer.onFooterChanged(adapter.footerState)
            }

        }

        override fun onCreateHeaderHolder(parent: ViewGroup): BaseViewHolder {
            return this@BaseSwipeRefreshLayoutListActivity.onCreateHeaderHolder(parent)
                    ?: throw java.lang.IllegalArgumentException("HeaderHolder not be null")
        }
    }

    protected fun getItemViewTypeByPosition(position: Int): Int {
        return 0
    }

    protected fun onCreateHeaderHolder(parent: ViewGroup?): BaseViewHolder? {
        return null
    }

    private fun onCreateFooterHolder(parent: ViewGroup): BaseViewHolder? {
        return null
    }

    protected abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    /**
     * 得到GridLayoutManager
     *
     * @param spanCount 列数
     * @return
     */
    fun getGridLayoutManager(spanCount: Int): GridLayoutManager {
        val manager = GridLayoutManager(this, spanCount)
        manager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.isHeaderShow(position) || adapter.isFooterShow(position)) {
                    manager.spanCount
                } else 1
            }
        }
        return manager
    }

    /**
     * 得到StaggeredGridLayoutManager
     *
     * @param spanCount 列数
     * @return
     */
    fun getStaggeredGridLayoutManager(spanCount: Int): StaggeredGridLayoutManager {
        return StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }
}