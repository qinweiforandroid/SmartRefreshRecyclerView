package com.qw.recyclerview.sample.ui

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.SmartRefreshHelper
import com.qw.recyclerview.core.State
import com.qw.recyclerview.core.adapter.BaseListAdapter
import com.qw.recyclerview.core.adapter.BaseViewHolder
import com.qw.recyclerview.sample.FooterView
import com.qw.recyclerview.sample.FooterView.OnFooterViewListener
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SwipeRefreshLayoutActivityBinding
import com.qw.recyclerview.swiperefresh.SwipeRefreshRecyclerView
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SwipeRefreshLayout2Activity : AppCompatActivity(), OnFooterViewListener {
    private lateinit var bind: SwipeRefreshLayoutActivityBinding
    private lateinit var smartRefresh: SmartRefreshHelper
    private lateinit var adapter: ListAdapter
    private var modules = ArrayList<Any>()
    private var loadMoreState = State.IDLE
    private val TYPE_LOAD_MORE = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SwipeRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)

        //1.配置RecyclerView
        val mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListAdapter()
        mRecyclerView.adapter = adapter

        //2.配置SwipeRefreshLayout
        val mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.mSwipeRefreshLayout)
        //mSwipeRefreshLayout.setColorSchemeColors();

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

            override fun onStateChanged(state: State) {
                loadMoreState = state
                adapter.notifyDataSetChanged()
            }

            override fun getState(): State {
                return loadMoreState
            }
        })
        //自动刷新
        smartRefresh.autoRefresh()
    }

    private fun refresh() {
        smartRefresh.getRecyclerView().postDelayed({
            modules.clear()
            for (i in 0..19) {
                modules.add("" + i)
            }
            smartRefresh.finishRefresh(true)
            adapter.notifyDataSetChanged()
        }, 1000)
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
                smartRefresh.finishLoadMore(success = false, noMoreData = true)
            }
            adapter.notifyDataSetChanged()
        }, 1000)
    }

    override fun onFooterClick() {
        loadMore()
    }

    inner class ListAdapter : BaseListAdapter() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            if (viewType == TYPE_LOAD_MORE) {
                return object : BaseViewHolder(FooterView.create(parent.context)) {
                    override fun initData(position: Int) {
                        (itemView as FooterView).onStateChanged(loadMoreState)
                        itemView.setOnFooterViewListener {
                            loadMoreState = State.LOADING
                            itemView.onStateChanged(loadMoreState)
                            loadMore()
                        }
                    }
                }
            }
            return object : BaseViewHolder(
                LayoutInflater.from(this@SwipeRefreshLayout2Activity)
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
            if (position == itemCount - 1 && isLoadMoreViewShow()) {
                return TYPE_LOAD_MORE
            }
            return super.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            var count = modules.size
            if (isLoadMoreViewShow()) {
                count++
            }
            return count
        }

        override fun onViewAttachedToWindow(holder: BaseViewHolder) {
            super.onViewAttachedToWindow(holder)

        }
    }

    private fun isLoadMoreViewShow(): Boolean {
        return true
//        return loadMoreState != State.IDLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recyclerview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_linearLayout) {
            smartRefresh.setLayoutManager(linearLayoutManager)
        } else if (itemId == R.id.action_gridLayout) {
            smartRefresh.setLayoutManager(getGridLayoutManager(2))
        } else if (itemId == R.id.action_staggeredGridLayout) {
            smartRefresh.setLayoutManager(getStaggeredGridLayoutManager(2))
        }
        return super.onOptionsItemSelected(item)
    }

    private val linearLayoutManager: RecyclerView.LayoutManager
        get() = LinearLayoutManager(this)

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
                return if (adapter.itemCount - 1 == position && isLoadMoreViewShow()) {
                    manager.spanCount
                } else 1
            }
        }
        return manager
    }

    /**
     * 得到StaggeredGridLayoutManager
     *
     * @param spanCount 列数
     * @return
     */
    private fun getStaggeredGridLayoutManager(spanCount: Int): StaggeredGridLayoutManager {
        return StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }
}