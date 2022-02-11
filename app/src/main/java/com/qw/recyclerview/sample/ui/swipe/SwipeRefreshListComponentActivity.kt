package com.qw.recyclerview.sample.ui.swipe

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
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.footer.DefaultLoadMore
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SwipeRefreshLayoutActivityBinding
import com.qw.recyclerview.swiperefresh.template.SwipeListComponent

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SwipeRefreshListComponentActivity : AppCompatActivity() {
    private lateinit var mList: SwipeListComponent<String>
    private lateinit var bind: SwipeRefreshLayoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SwipeRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        mList =
            object : SwipeListComponent<String>(bind.mRecyclerView, bind.mSwipeRefreshLayout) {
                override fun onCreateBaseViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): BaseViewHolder {
                    return Holder(
                        LayoutInflater.from(this@SwipeRefreshListComponentActivity)
                            .inflate(android.R.layout.simple_list_item_1, parent, false)
                    )
                }

                inner class Holder(itemView: View) : BaseViewHolder(itemView) {
                    override fun initData(position: Int) {
                        val label: TextView = itemView as TextView
                        val text = mList.modules[position]
                        label.text = text
                    }
                }
            }
        mList.smart.setLayoutManager(linearLayoutManager)
            .setRefreshEnable(true)
            .setLoadMoreEnable(true)
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    Handler(Looper.myLooper()!!).postDelayed({
                        refresh()
                    }, 1000)
                }
            })
        mList.supportLoadMore(DefaultLoadMore(), object : OnLoadMoreListener {
            override fun onLoadMore() {
                Handler(Looper.myLooper()!!).postDelayed({
                    loadMore()
                }, 1000)
            }
        })
        mList.smart.autoRefresh()
    }

    private fun refresh() {
        mList.modules.clear()
        for (i in 0..19) {
            mList.modules.add("" + i)
        }
        mList.adapter.notifyDataSetChanged()
        mList.smart.finishRefresh(true)
    }

    private fun loadMore() {
        val size = mList.modules.size
        for (i in size until size + 20) {
            mList.modules.add("" + i)
        }
        mList.smart.finishLoadMore(success = false, noMoreData = mList.modules.size > 100)
        mList.adapter.notifyItemRangeInserted(size, 20)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recyclerview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_linearLayout -> {
                mList.setLayoutManager(LinearLayoutManager(this))
            }
            R.id.action_gridLayout -> {
                mList.setLayoutManager(getGridLayoutManager(2))
            }
            R.id.action_staggeredGridLayout -> {
                mList.setLayoutManager(getStaggeredGridLayoutManager(2))
            }
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
        return GridLayoutManager(this, spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (mList.isLoadMoreShow(position)) {
                        spanCount
                    } else {
                        1
                    }
                }
            }
        }
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