package com.qw.recyclerview.sample.core

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.SmartRefreshHelper
import com.qw.recyclerview.core.SmartRefreshable
import com.qw.recyclerview.core.adapter.BaseListAdapter
import com.qw.recyclerview.core.adapter.BaseViewHolder
import com.qw.recyclerview.core.footer.FooterView
import com.qw.recyclerview.core.footer.FooterView.OnFooterViewListener
import com.qw.recyclerview.core.footer.IFooter
import com.qw.recyclerview.core.footer.State
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.smartrefreshlayout.SmartRefreshLayoutRecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.util.*

abstract class BaseSmartRefreshLayoutListActivity<T> : AppCompatActivity(), OnFooterViewListener, OnRefreshListener, OnLoadMoreListener {
    protected lateinit var smartRefresh: SmartRefreshHelper
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
        mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val mSmartRefreshLayout = findViewById<SmartRefreshLayout>(R.id.mSmartRefreshLayout)
        smartRefresh = SmartRefreshHelper()
        smartRefresh.inject(getSmartRefreshable(mRecyclerView, mSmartRefreshLayout))
        smartRefresh.setLayoutManager(LinearLayoutManager(this))
        smartRefresh.setRefreshEnable(true)
        smartRefresh.setLoadMoreEnable(true)
        smartRefresh.setOnRefreshListener(this)
        smartRefresh.setOnLoadMoreListener(this)
    }

    open abstract fun initData(savedInstanceState: Bundle?)
    protected fun getSmartRefreshable(recyclerView: RecyclerView, smartRefreshLayout: SmartRefreshLayout): SmartRefreshable {
        return SmartRefreshLayoutRecyclerView(recyclerView, smartRefreshLayout)
    }

    override fun onRefresh() {}
    override fun onLoadMore() {}
    override fun onFooterClick() {
        adapter.notifyFooterDataSetChanged(State.LOADING)
        onLoadMore()
    }

    inner class ListAdapter : BaseListAdapter() {
        override fun getItemViewCount(): Int {
            return modules.size
        }

        override fun getItemViewTypeByPosition(position: Int): Int {
            return this@BaseSmartRefreshLayoutListActivity.getItemViewTypeByPosition(position)
        }

        override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return this@BaseSmartRefreshLayoutListActivity.onCreateBaseViewHolder(parent, viewType)
        }

        override fun onCreateFooterHolder(parent: ViewGroup): BaseViewHolder {
            val baseViewHolder = this@BaseSmartRefreshLayoutListActivity.onCreateFooterHolder(parent)
            if (baseViewHolder == null) {
                val footerView = FooterView(this@BaseSmartRefreshLayoutListActivity)
                footerView.setOnFooterViewListener(this@BaseSmartRefreshLayoutListActivity)
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
            return this@BaseSmartRefreshLayoutListActivity.onCreateHeaderHolder(parent)
                    ?: throw java.lang.IllegalArgumentException("HeaderHolder not be null")
        }
    }

    protected fun getItemViewTypeByPosition(position: Int): Int {
        return 0
    }

    protected fun onCreateFooterHolder(parent: ViewGroup?): BaseViewHolder? {
        return null
    }

    protected fun onCreateHeaderHolder(parent: ViewGroup?): BaseViewHolder? {
        return null
    }

    protected abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
}