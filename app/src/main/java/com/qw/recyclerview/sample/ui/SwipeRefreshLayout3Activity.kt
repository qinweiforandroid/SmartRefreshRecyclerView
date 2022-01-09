package com.qw.recyclerview.sample.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.adapter.BaseListAdapter
import com.qw.recyclerview.core.adapter.BaseViewHolder
import com.qw.recyclerview.sample.DefaultLoadMore
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.SwipeRefreshRecyclerViewComponent
import com.qw.recyclerview.sample.databinding.SwipeRefreshLayoutActivityBinding
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SwipeRefreshLayout3Activity : AppCompatActivity() {
    private lateinit var mListComponent: SwipeRefreshRecyclerViewComponent<String>
    private lateinit var bind: SwipeRefreshLayoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SwipeRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        mListComponent = SwipeRefreshRecyclerViewComponent(this)
        mListComponent.setLayoutManager(linearLayoutManager)
        mListComponent.setAdapter(object : BaseListAdapter() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return Holder(
                    LayoutInflater.from(this@SwipeRefreshLayout3Activity)
                        .inflate(android.R.layout.simple_list_item_1, parent, false)
                )
            }

            inner class Holder(itemView: View) : BaseViewHolder(itemView) {
                override fun initData(position: Int) {
                    val label: TextView = itemView as TextView
                    val text = mListComponent.getItem(position)
                    label.text = text
                }
            }
        })
        mListComponent.setLoadMoreEnable(true)
        mListComponent.setRefreshEnable(true)
        mListComponent.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                loadMore()
            }
        })
        mListComponent.injectLoadMore(DefaultLoadMore())
        mListComponent.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                refresh()
            }
        })
        mListComponent.autoRefresh()
    }

    private fun refresh() {
        Handler(Looper.myLooper()!!).postDelayed({
            mListComponent.clear()
            for (i in 0..19) {
                mListComponent.add("" + i)
            }
            mListComponent.finishRefresh(true)
            mListComponent.notifyDataSetChanged()
        }, 1000)
    }

    private fun loadMore() {
        Handler(Looper.myLooper()!!).postDelayed({
            val size = mListComponent.size()
            for (i in size until size + 20) {
                mListComponent.add("" + i)
            }
            if (mListComponent.size() < 100) {
                mListComponent.finishLoadMore(
                    success = true,
                    noMoreData = false
                )
            } else {
                mListComponent.finishLoadMore(
                    success = false,
                    noMoreData = true
                )
            }
            mListComponent.notifyItemRangeInserted(size, 20)
        }, 1000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recyclerview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_linearLayout) {
            mListComponent.setLayoutManager(LinearLayoutManager(this))
        } else if (itemId == R.id.action_gridLayout) {
            mListComponent.setLayoutManager(getGridLayoutManager(2))
        } else if (itemId == R.id.action_staggeredGridLayout) {
            mListComponent.setLayoutManager(getStaggeredGridLayoutManager(2))
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
        return GridLayoutManager(this, spanCount)
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