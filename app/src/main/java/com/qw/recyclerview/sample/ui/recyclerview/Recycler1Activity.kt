package com.qw.recyclerview.sample.ui.recyclerview

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
import com.qw.recyclerview.sample.databinding.RecyclerviewLayoutActivityBinding
import com.qw.recyclerview.sample.loading.State

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class Recycler1Activity : AppCompatActivity() {
    private lateinit var bind: RecyclerviewLayoutActivityBinding
    private lateinit var adapter: ListAdapter
    private var modules = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = RecyclerviewLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        bind.mRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListAdapter()
        bind.mRecyclerView.adapter = adapter

        bind.mLoading.notifyDataChanged(State.ing)
        Handler(Looper.myLooper()!!).postDelayed({
            for (i in 0..19) {
                modules.add("" + i)
            }
            adapter.notifyDataSetChanged()
            bind.mLoading.notifyDataChanged(State.done)
        }, 1000)
    }

    internal inner class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int {
            return modules.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : BaseViewHolder(
                LayoutInflater.from(this@Recycler1Activity)
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