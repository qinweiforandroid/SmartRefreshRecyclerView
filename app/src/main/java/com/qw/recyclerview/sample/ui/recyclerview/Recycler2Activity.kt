package com.qw.recyclerview.sample.ui.recyclerview

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.sample.databinding.RecyclerviewLayoutActivityBinding
import com.qw.recyclerview.sample.loading.State
import com.qw.recyclerview.template.ListCompat

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class Recycler2Activity : AppCompatActivity() {
    private lateinit var bind: RecyclerviewLayoutActivityBinding
    private lateinit var listCompat: ListCompat<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = RecyclerviewLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(bind.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listCompat = object : ListCompat<String>(bind.mRecyclerView) {
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
        listCompat.setLayoutManager(LinearLayoutManager(this))

        bind.mLoading.notifyDataChanged(State.ing)
        Handler(Looper.myLooper()!!).postDelayed({
            for (i in 0..19) {
                listCompat.modules.add("" + i)
            }
            listCompat.adapter.notifyDataSetChanged()
            bind.mLoading.notifyDataChanged(State.done)
        }, 1000)
    }
}