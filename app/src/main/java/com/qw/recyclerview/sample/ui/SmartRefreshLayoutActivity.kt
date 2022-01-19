package com.qw.recyclerview.sample.ui

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.SmartRefreshHelper
import com.qw.recyclerview.core.BaseListAdapter
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SmartRefreshLayoutActivityBinding
import com.qw.recyclerview.smartrefreshlayout.SmartRefreshLayout1RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SmartRefreshLayoutActivity : AppCompatActivity() {
    private lateinit var bind: SmartRefreshLayoutActivityBinding
    private lateinit var smartRefresh: SmartRefreshHelper
    private lateinit var adapter: ListAdapter
    private var modules = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SmartRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)

        //1.配置RecyclerView
        val mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListAdapter()
        mRecyclerView.adapter = adapter

        //2.配置SmartRefreshLayout
        val mSmartRefreshLayout = findViewById<SmartRefreshLayout>(R.id.mSmartRefreshLayout)


        //3.配置SmartRefreshHelper
        smartRefresh = SmartRefreshHelper()
        //SmartRefreshLayoutRecyclerView将mRecyclerView和mSmartRefreshLayout打包后，交给SmartRefreshHelper进行管理
        smartRefresh.inject(SmartRefreshLayout1RecyclerView(mRecyclerView, mSmartRefreshLayout))

        //设置下拉刷新可用
        smartRefresh.setRefreshEnable(true)
        //设置加载更多可用
        smartRefresh.setLoadMoreEnable(true)
        //设置下拉刷新监听
        smartRefresh.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                refresh()
            }
        })
        //设置加载更多监听
        smartRefresh.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                loadMore()
            }
        })
        smartRefresh.autoRefresh()
    }

    private fun loadMore() {
        smartRefresh.getRecyclerView().postDelayed({
            val size = modules.size
            for (i in size until size + 20) {
                modules.add("" + i)
            }
            if (modules.size < 100) {
                smartRefresh.finishLoadMore(true, false)
            } else {
                smartRefresh.finishLoadMore(true, true)
            }
            adapter.notifyDataSetChanged()
        }, 1000)
    }

    private fun refresh() {
        smartRefresh.getRecyclerView().postDelayed({
            modules.clear()
            for (i in 0..19) {
                modules.add("" + i)
            }
            adapter.notifyDataSetChanged()
            smartRefresh.finishRefresh(true)
        }, 1000)
    }

    internal inner class ListAdapter : BaseListAdapter() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return object : BaseViewHolder(
                LayoutInflater.from(this@SmartRefreshLayoutActivity)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            ) {
                private val label: TextView = itemView as TextView
                override fun initData(position: Int) {
                    val text = modules[position]
                    label.text = text
                }
            }
        }

        override fun getItemCount(): Int {
            return modules.size
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recyclerview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_linearLayout) {
            smartRefresh.setLayoutManager(linearLayoutManager)
        } else if (itemId == R.id.action_gridLayout) {
            smartRefresh.setLayoutManager(getGridLayoutManager(2))
        } else if (itemId == R.id.action_staggeredGridLayout) {
            smartRefresh.setLayoutManager(getStaggeredGridLayoutManager(2))
        }
        return super.onOptionsItemSelected(item)
    }

    private val linearLayoutManager: RecyclerView.LayoutManager
        get() = LinearLayoutManager(this)

    private fun getGridLayoutManager(spanCount: Int): GridLayoutManager {
        val manager = GridLayoutManager(this, spanCount)
//        manager.spanSizeLookup = object : SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return if (adapter.isHeaderShow(position) || adapter.isFooterShow(position)) {
//                    manager.spanCount
//                } else 1
//            }
//        }
        return manager
    }

    private fun getStaggeredGridLayoutManager(spanCount: Int): StaggeredGridLayoutManager {
        return StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }
}