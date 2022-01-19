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
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.footer.DefaultLoadMore
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SmartRefreshLayoutActivityBinding
import com.qw.recyclerview.smartrefreshlayout.SmartRefreshListComponent
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SmartRefreshLayout1Activity : AppCompatActivity() {
    private lateinit var mComponent: SmartRefreshListComponent<String>
    private lateinit var bind: SmartRefreshLayoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SmartRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        val mSmartRefresh = findViewById<SmartRefreshLayout>(R.id.mSmartRefreshLayout)
        mComponent = object : SmartRefreshListComponent<String>(mRecyclerView, mSmartRefresh) {
            inner class Holder(itemView: View) : BaseViewHolder(itemView) {
                override fun initData(position: Int) {
                    val label: TextView = itemView as TextView
                    val text = mComponent.modules[position]
                    label.text = text
                }
            }

            override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return Holder(
                    LayoutInflater.from(this@SmartRefreshLayout1Activity)
                        .inflate(android.R.layout.simple_list_item_1, parent, false)
                )
            }
        }
        mComponent.smart.setRefreshEnable(true)
        mComponent.smart.setLoadMoreEnable(true)
        mComponent.setLayoutManager(linearLayoutManager)
        mComponent.injectLoadMore(DefaultLoadMore())
        mComponent.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                loadMore()
            }
        })

        mComponent.smart.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                refresh()
            }
        })
        mComponent.smart.autoRefresh()
    }

    private fun refresh() {
        Handler(Looper.myLooper()!!).postDelayed({
            mComponent.modules.clear()
            for (i in 0..19) {
                mComponent.modules.add("" + i)
            }
            mComponent.smart.finishRefresh(true)
            mComponent.adapter.notifyDataSetChanged()
        }, 1000)
    }

    private fun loadMore() {
        Handler(Looper.myLooper()!!).postDelayed({
            val size = mComponent.modules.size
            for (i in size until size + 20) {
                mComponent.modules.add("" + i)
            }
            if (mComponent.modules.size < 100) {
                mComponent.smart.finishLoadMore(
                    success = true,
                    noMoreData = false
                )
            } else {
                mComponent.smart.finishLoadMore(
                    success = false,
                    noMoreData = true
                )
            }
            mComponent.adapter.notifyItemRangeInserted(size, 20)
        }, 1000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recyclerview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_linearLayout -> {
                mComponent.setLayoutManager(LinearLayoutManager(this))
            }
            R.id.action_gridLayout -> {
                mComponent.setLayoutManager(getGridLayoutManager(2))
            }
            R.id.action_staggeredGridLayout -> {
                mComponent.setLayoutManager(getStaggeredGridLayoutManager(2))
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
                    return if (mComponent.isLoadMoreShow(position)) {
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