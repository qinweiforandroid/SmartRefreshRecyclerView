package com.qw.recyclerview.sample.ui.smart

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.footer.DefaultLoadMore
import com.qw.recyclerview.layout.MyLinearLayoutManager
import com.qw.recyclerview.layout.MyStaggeredGridLayoutManager
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SmartRefreshLayoutActivityBinding
import com.qw.recyclerview.smartrefreshlayout.SmartRecyclerView
import com.qw.recyclerview.template.SmartListCompat

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SmartCompatActivity : AppCompatActivity() {
    private lateinit var mList: SmartListCompat<String>
    private lateinit var bind: SmartRefreshLayoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SmartRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val smart = SmartRecyclerView(bind.mRecyclerView, bind.mSmartRefreshLayout)
        mList = object : SmartListCompat<String>(smart) {
            override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int) = Holder(
                LayoutInflater.from(this@SmartCompatActivity)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            )
        }
        mList.setRefreshEnable(true)
            .setLoadMoreEnable(true)
            .setUpLayoutManager(MyLinearLayoutManager(this))
            .setUpLoadMore(DefaultLoadMore())
            .setOnLoadMoreListener(object : OnLoadMoreListener {
                override fun onLoadMore() {
                    Handler(Looper.myLooper()!!).postDelayed({
                        loadMore()
                    }, 1000)
                }
            })
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    refresh()
                }
            }).autoRefresh()

    }

    inner class Holder(itemView: View) : BaseViewHolder(itemView) {
        override fun initData(position: Int) {
            val label: TextView = itemView as TextView
            val text = mList.modules[position]
            label.text = text
        }
    }

    private fun loadMore() {
        val size = mList.modules.size
        for (i in size until size + 20) {
            mList.modules.add("" + i)
        }
        mList.finishLoadMore(success = false, noMoreData = mList.modules.size > 100)
        mList.adapter.notifyItemRangeInserted(size, 20)
    }

    private fun refresh() {
        Handler(Looper.myLooper()!!).postDelayed({
            mList.modules.clear()
            for (i in 0..19) {
                mList.modules.add("" + i)
            }
            mList.finishRefresh(true)
            mList.adapter.notifyDataSetChanged()
        }, 1000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recyclerview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_linearLayout -> {
                mList.setLayoutManager(MyLinearLayoutManager(this))
            }

            R.id.action_gridLayout -> {
                mList.setLayoutManager(mList.getGridLayoutManager(2))
            }

            R.id.action_staggeredGridLayout -> {
                mList.setLayoutManager(
                    MyStaggeredGridLayoutManager(
                        2, StaggeredGridLayoutManager.VERTICAL
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}