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
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.footer.DefaultLoadMore
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SmartRefreshLayoutActivityBinding
import com.qw.recyclerview.smartrefreshlayout.template.SmartListComponent
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class ListWithRefreshActivity : AppCompatActivity() {
    private lateinit var mList: SmartListComponent<String>
    private lateinit var bind: SmartRefreshLayoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SmartRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.mRecyclerView.layoutManager = linearLayoutManager
        mList = object : SmartListComponent<String>(bind.mRecyclerView, bind.mSmartRefreshLayout) {
            inner class Holder(itemView: View) : BaseViewHolder(itemView) {
                override fun initData(position: Int) {
                    val label: TextView = itemView as TextView
                    val text = mList.modules[position]
                    label.text = text
                }
            }

            override fun onCreateBaseViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): BaseViewHolder {
                return Holder(
                    LayoutInflater.from(this@ListWithRefreshActivity)
                        .inflate(android.R.layout.simple_list_item_1, parent, false)
                )
            }
        }
        mList.smart.setRefreshEnable(true)
            .setLoadMoreEnable(false)
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    refresh()
                }
            })
        mList.injectLoadMore(DefaultLoadMore())
        mList.smart.autoRefresh()
    }

    private fun refresh() {
        Handler(Looper.myLooper()!!).postDelayed({
            mList.modules.clear()
            for (i in 0..19) {
                mList.modules.add("" + i)
            }
            mList.smart.finishRefresh(true)
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