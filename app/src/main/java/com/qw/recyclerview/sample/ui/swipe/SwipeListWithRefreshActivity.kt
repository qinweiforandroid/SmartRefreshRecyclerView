package com.qw.recyclerview.sample.ui.swipe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.sample.databinding.SwipeRefreshLayoutActivityBinding
import com.qw.recyclerview.sample.loading.ILoading
import com.qw.recyclerview.sample.loading.State
import com.qw.recyclerview.swiperefresh.SwipeRecyclerView
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SwipeListWithRefreshActivity : AppCompatActivity() {
    private lateinit var mLoading: ILoading
    private lateinit var bind: SwipeRefreshLayoutActivityBinding
    private lateinit var smart: ISmartRecyclerView
    private lateinit var adapter: ListAdapter
    private var modules = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SwipeRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        //转圈工具
        mLoading = bind.mLoading

        //1.配置RecyclerView
        bind.mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        adapter = ListAdapter()
        bind.mRecyclerView.adapter = adapter
        //2.配置SwipeRefreshLayout

        //3.配置SmartHelper
        smart = SwipeRecyclerView(bind.mRecyclerView, bind.mSwipeRefreshLayout)
            .setLayoutManager(LinearLayoutManager(this))
            .setRefreshEnable(true)//设置下拉刷新可用
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    smart.getRecyclerView().postDelayed({
                        this@SwipeListWithRefreshActivity.onRefresh()
                    }, 1000)
                }
            })
        mLoading.setOnRetryListener {
            //重试回调
            mLoading.notifyDataChanged(State.ing)
            smart.autoRefresh()
        }
        mLoading.notifyDataChanged(State.ing)
        Handler(Looper.myLooper()!!).postDelayed({
            for (i in 0..19) {
                modules.add("" + i)
            }
            adapter.notifyDataSetChanged()
            mLoading.notifyDataChanged(State.done)
        }, 1500)
    }

    private fun onRefresh(){
        modules.clear()
        for (i in 0..19) {
            modules.add("" + i)
        }
        adapter.notifyDataSetChanged()
        smart.finishRefresh(true)
    }

    internal inner class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int {
            return modules.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : BaseViewHolder(
                LayoutInflater.from(this@SwipeListWithRefreshActivity)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            ) {
                override fun initData(position: Int) {
                    val label: TextView = itemView as TextView
                    val text = modules[position]
                    label.text = text
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
        override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: List<Any>
        ) {
            (holder as BaseViewHolder).initData(position, payloads)
        }
    }
}