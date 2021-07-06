package com.qw.recyclerview.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.qw.recyclerview.core.adapter.BaseViewHolder
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.core.BaseSmartRefreshLayoutListActivity

class SmartRefreshLayoutPlusActivity : BaseSmartRefreshLayoutListActivity<String?>() {
    override fun setContentView() {
        setContentView(R.layout.smart_refresh_layout_activity)
    }

    override fun initData(savedInstanceState: Bundle?) {
        smartRefresh.autoRefresh()
    }

    override fun onRefresh() {
        smartRefresh.getRecyclerView().postDelayed({
            modules.clear()
            for (i in 0..19) {
                modules.add("" + i)
            }
            adapter.notifyDataSetChanged()
            smartRefresh.finishRefresh(true)
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
        }, 2000)
    }

    override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return object : BaseViewHolder(LayoutInflater.from(this@SmartRefreshLayoutPlusActivity).inflate(android.R.layout.simple_list_item_1, parent, false)) {
            override fun initData(position: Int) {
                val label = itemView as TextView
                label.text = modules[position]
            }
        }
    }
}