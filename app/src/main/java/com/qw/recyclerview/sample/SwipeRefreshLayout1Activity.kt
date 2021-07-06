package com.qw.recyclerview.sample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.SmartRefreshHelper
import com.qw.recyclerview.core.adapter.BaseViewHolder
import com.qw.recyclerview.sample.databinding.SwipeRefreshLayoutActivityBinding
import com.qw.recyclerview.sample.loading.LoadingHelper
import com.qw.recyclerview.sample.loading.State
import com.qw.recyclerview.swiperefresh.SwipeRefreshRecyclerView
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SwipeRefreshLayout1Activity : AppCompatActivity() {
    private lateinit var mLoading: LoadingHelper
    private lateinit var bind: SwipeRefreshLayoutActivityBinding
    private lateinit var smartRefresh: SmartRefreshHelper
    private lateinit var adapter: ListAdapter
    private var modules = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SwipeRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        mLoading = LoadingHelper()
        mLoading.inject(bind.mLoading)

        //1.配置RecyclerView
        val mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListAdapter()
        mRecyclerView.adapter = adapter

        //2.配置SwipeRefreshLayout
        val mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.mSwipeRefreshLayout)

        //3.配置SmartRefreshHelper
        smartRefresh = SmartRefreshHelper()
        //SmartRefreshLayoutRecyclerView将mRecyclerView和mSmartRefreshLayout打包后，交给SmartRefreshHelper进行管理
        smartRefresh.inject(SwipeRefreshRecyclerView(mRecyclerView, mSwipeRefreshLayout))

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
//        smartRefresh.autoRefresh()
        mLoading.setOnRetryListener {
            //重试回调
        }
        mLoading.notifyDataChanged(State.ing)
        Handler(Looper.myLooper()!!).postDelayed({
            modules.clear()
            for (i in 0..19) {
                modules.add("" + i)
            }
            adapter.notifyDataSetChanged()
            mLoading.notifyDataChanged(State.done)
        }, 1500)
    }

    private fun loadMore() {
        smartRefresh.getRecyclerView().postDelayed({
            val size = modules.size
            for (i in size until size + 20) {
                modules.add("" + i)
            }
            if (modules.size < 100) {
                smartRefresh.finishLoadMore(success = true, noMoreData = false)
            } else {
                smartRefresh.finishLoadMore(success = true, noMoreData = true)
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

    internal inner class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int {
            return modules.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : BaseViewHolder(LayoutInflater.from(this@SwipeRefreshLayout1Activity).inflate(android.R.layout.simple_list_item_1, parent, false)) {
                private val label: TextView = itemView as TextView
                override fun initData(position: Int) {
                    val text = modules[position]
                    label.text = text
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
            (holder as BaseViewHolder).initData(position, payloads)
        }
    }
}