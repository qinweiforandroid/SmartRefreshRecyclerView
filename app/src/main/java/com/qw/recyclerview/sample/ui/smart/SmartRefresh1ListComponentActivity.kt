package com.qw.recyclerview.sample.ui.smart

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
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SmartRefreshLayoutActivityBinding
import com.qw.recyclerview.smartrefreshlayout.template.SmartV2ListComponent
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SmartRefresh1ListComponentActivity : AppCompatActivity() {
    private lateinit var mList: SmartV2ListComponent<String>
    private lateinit var bind: SmartRefreshLayoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SmartRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        mList = object :
            SmartV2ListComponent<String>(bind.mRecyclerView, bind.mSmartRefreshLayout) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return Holder(
                    LayoutInflater.from(this@SmartRefresh1ListComponentActivity)
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
            .setOnLoadMoreListener(object : OnLoadMoreListener {
                override fun onLoadMore() {
                    Handler(Looper.myLooper()!!).postDelayed({
                        loadMore()
                    }, 1000)
                }
            })
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    Handler(Looper.myLooper()!!).postDelayed({
                        refresh()
                    }, 1000)
                }
            }).autoRefresh()
    }

    private fun refresh() {
        mList.modules.clear()
        for (i in 0..19) {
            mList.modules.add("" + i)
        }
        mList.smart.finishRefresh(true)
        mList.adapter.notifyDataSetChanged()
    }

    private fun loadMore() {
        val size = mList.modules.size
        for (i in size until size + 20) {
            mList.modules.add("" + i)
        }
        mList.smart.finishLoadMore(true, mList.modules.size > 100)
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