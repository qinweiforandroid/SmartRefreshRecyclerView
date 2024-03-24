package com.qw.recyclerview.sample.ui.swipe

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.*
import com.qw.recyclerview.footer.DefaultLoadMore
import com.qw.recyclerview.loadmore.State
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SwipeRefreshLayoutActivityBinding
import com.qw.recyclerview.swiperefresh.SwipeRecyclerView
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SwipeRecyclerViewActivity : AppCompatActivity() {
    private lateinit var bind: SwipeRefreshLayoutActivityBinding
    private lateinit var smart: ISmartRecyclerView
    private lateinit var adapter: ListAdapter
    private var modules = ArrayList<Any>()
    private val typeLoadMore = -1

    private val loadMoreModule = DefaultLoadMore().apply {
        setOnRetryListener {
            onStateChanged(State.LOADING)
            adapter.notifyItemChanged(adapter.itemCount - 1)
            loadMore()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SwipeRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        bind.mRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListAdapter()
        bind.mRecyclerView.adapter = adapter

        smart = SwipeRecyclerView(bind.mRecyclerView, bind.mSwipeRefreshLayout)
            .setRefreshEnable(true)
            .setLoadMoreEnable(true)
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    smart.getRecyclerView().postDelayed({
                        refresh()
                    }, 1000)
                }
            })
            .setOnLoadMoreListener(object : OnLoadMoreListener {
                override fun onLoadMore() {
                    loadMore()
                }

                override fun onStateChanged(state: State) {
                    loadMoreModule.onStateChanged(state)
                    adapter.notifyItemChanged(adapter.itemCount - 1)
                }
            })
        smart.autoRefresh()
    }

    private fun refresh() {
        modules.clear()
        for (i in 0..19) {
            modules.add("" + i)
        }
        adapter.notifyDataSetChanged()
        smart.finishRefresh(true)
    }

    private fun loadMore() {
        smart.getRecyclerView().postDelayed({
            val size = modules.size
            for (i in size until size + 20) {
                modules.add("" + i)
            }
            adapter.notifyDataSetChanged()
            smart.finishLoadMore(success = true, noMoreData = modules.size > 100)
        }, 1000)
    }

    inner class ListAdapter : BaseListAdapter() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (viewType == typeLoadMore) {
                return loadMoreModule.onCreateLoadMoreViewHolder(parent)
            }
            return object : BaseViewHolder(
                LayoutInflater.from(this@SwipeRecyclerViewActivity)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            ) {
                override fun initData(position: Int) {
                    val label: TextView = itemView as TextView
                    val text = modules[position]
                    label.text = text.toString()
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            if (isLoadMoreShow(position)) {
                return typeLoadMore
            }
            return super.getItemViewType(position)
        }


        override fun getItemCount(): Int {
            var count = modules.size
            if (smart.isLoadMoreEnable()) {
                count++
            }
            return count
        }
    }

    fun isLoadMoreShow(position: Int): Boolean {
        return smart.isLoadMoreEnable() && adapter.itemCount - 1 == position
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recyclerview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_linearLayout -> {
                smart.setLayoutManager(LinearLayoutManager(this))
            }

            R.id.action_gridLayout -> {
                smart.setLayoutManager(getGridLayoutManager(2))
            }

            R.id.action_staggeredGridLayout -> {
                smart.setLayoutManager(
                    StaggeredGridLayoutManager(
                        2,
                        StaggeredGridLayoutManager.VERTICAL
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 得到GridLayoutManager
     *
     * @param spanCount 列数
     * @return
     */
    private fun getGridLayoutManager(spanCount: Int): GridLayoutManager {
        val manager = GridLayoutManager(this, spanCount)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (isLoadMoreShow(position)) {
                    manager.spanCount
                } else 1
            }
        }
        return manager
    }
}