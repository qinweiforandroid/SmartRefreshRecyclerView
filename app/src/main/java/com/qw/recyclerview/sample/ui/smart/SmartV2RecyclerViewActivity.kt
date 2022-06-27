package com.qw.recyclerview.sample.ui.smart

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.*
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SmartRefreshLayoutActivityBinding
import com.qw.recyclerview.smartrefreshlayout.SmartV2RecyclerView
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SmartV2RecyclerViewActivity : AppCompatActivity() {
    private lateinit var bind: SmartRefreshLayoutActivityBinding
    private lateinit var smart: ISmartRecyclerView
    private lateinit var adapter: ListAdapter
    private var modules = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SmartRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)

        //1.配置RecyclerView
        bind.mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        bind.mRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListAdapter()
        bind.mRecyclerView.adapter = adapter
        //2.配置SmartRefreshLayout
        smart = SmartV2RecyclerView(bind.mRecyclerView, bind.mSmartRefreshLayout)
            .setRefreshEnable(true) //设置下拉刷新可用
            .setLoadMoreEnable(true) //设置加载更多可用
            .setOnRefreshListener(object : OnRefreshListener {
                //设置下拉刷新监听
                override fun onRefresh() {
                    refresh()
                }
            }).setOnLoadMoreListener(object : OnLoadMoreListener {
                //设置加载更多监听
                override fun onLoadMore() {
                    loadMore()
                }
            })
        smart.autoRefresh()
    }

    private fun loadMore() {
        smart.getRecyclerView().postDelayed({
            val size = modules.size
            for (i in size until size + 20) {
                modules.add("" + i)
            }
            smart.finishLoadMore(true, modules.size > 100)
            adapter.notifyDataSetChanged()
        }, 1000)
    }

    private fun refresh() {
        smart.getRecyclerView().postDelayed({
            modules.clear()
            for (i in 0..19) {
                modules.add("" + i)
            }
            adapter.notifyDataSetChanged()
            smart.finishRefresh(true)
        }, 1000)
    }

    internal inner class ListAdapter : BaseListAdapter() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return object : BaseViewHolder(
                LayoutInflater.from(this@SmartV2RecyclerViewActivity)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            ) {
                override fun initData(position: Int) {
                    val text = modules[position]
                    val label: TextView = itemView as TextView
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
        when (item.itemId) {
            R.id.action_linearLayout -> {
                smart.setLayoutManager(linearLayoutManager)
            }
            R.id.action_gridLayout -> {
                smart.setLayoutManager(getGridLayoutManager(2))
            }
            R.id.action_staggeredGridLayout -> {
                smart.setLayoutManager(getStaggeredGridLayoutManager(2))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val linearLayoutManager: RecyclerView.LayoutManager
        get() = LinearLayoutManager(this)

    private fun getGridLayoutManager(spanCount: Int): GridLayoutManager {
        return GridLayoutManager(this, spanCount)
    }

    private fun getStaggeredGridLayoutManager(spanCount: Int): StaggeredGridLayoutManager {
        return StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }
}