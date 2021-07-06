package com.qw.recyclerview.sample.ui

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.qw.recyclerview.core.adapter.BaseViewHolder
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.core.BaseSwipeRefreshLayoutListActivity
import com.qw.recyclerview.sample.loading.State

class SwipeRefreshLayoutPlusActivity : BaseSwipeRefreshLayoutListActivity<String>() {
    override fun setContentView() {
        setContentView(R.layout.swipe_refresh_layout_activity)
    }

    override fun initData(savedInstanceState: Bundle?) {
        loading.setOnRetryListener {
            for (i in 0..19) {
                modules.add("" + i)
            }
            adapter.notifyDataSetChanged()
            loading.notifyDataChanged(State.done)
        }
        loading.notifyDataChanged(State.ing)
        Handler().postDelayed({
            loading.notifyDataChanged(State.error)
        }, 2000)
    }

    override fun onRefresh() {
        smartRefresh.getRecyclerView().postDelayed({
            modules.clear()
            for (i in 0..19) {
                modules.add("" + i)
            }
            adapter.notifyDataSetChanged()
            smartRefresh.finishRefresh(false)
        }, 2000)
    }

    override fun onLoadMore() {
        smartRefresh.getRecyclerView().postDelayed({
            val size = modules.size
            for (i in size until size + 10) {
                modules.add("" + i)
            }
            if (modules.size < 50) {
                smartRefresh.finishLoadMore(success = true, noMoreData = false)
            } else {
                smartRefresh.finishLoadMore(success = true, noMoreData = true)
            }
            adapter.notifyDataSetChanged()
        }, 3000)
    }

    override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return object : BaseViewHolder(LayoutInflater.from(this@SwipeRefreshLayoutPlusActivity).inflate(android.R.layout.simple_list_item_1, parent, false)) {
            override fun initData(position: Int) {
                val label = itemView as TextView
                label.text = modules[position]
            }
        }
    }
}