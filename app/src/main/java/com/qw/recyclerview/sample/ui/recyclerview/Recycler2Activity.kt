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
import com.qw.recyclerview.sample.loading.ILoading
import com.qw.recyclerview.sample.loading.State
import com.qw.recyclerview.template.BaseListComponent

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class Recycler2Activity : AppCompatActivity() {
    private lateinit var bind: RecyclerviewLayoutActivityBinding
    private lateinit var listComponent: BaseListComponent<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = RecyclerviewLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        listComponent = object : BaseListComponent<String>(bind.mRecyclerView) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return object : BaseViewHolder(
                    LayoutInflater.from(this@Recycler2Activity)
                        .inflate(android.R.layout.simple_list_item_1, parent, false)
                ) {
                    override fun initData(position: Int) {
                        val label: TextView = itemView as TextView
                        val text = modules[position]
                        label.text = text
                    }
                }
            }
        }
        listComponent.setLayoutManager(LinearLayoutManager(this))

        bind.mLoading.notifyDataChanged(State.ing)
        Handler(Looper.myLooper()!!).postDelayed({
            for (i in 0..19) {
                listComponent.modules.add("" + i)
            }
            listComponent.adapter.notifyDataSetChanged()
            bind.mLoading.notifyDataChanged(State.done)
        }, 1000)
    }
}