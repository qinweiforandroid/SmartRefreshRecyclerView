package com.qw.recyclerview.sample.ui.swipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.footer.DefaultLoadMore
import com.qw.recyclerview.layout.MyLinearLayoutManager
import com.qw.recyclerview.layout.MyStaggeredGridLayoutManager
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SwipeRefreshLayoutActivityBinding
import com.qw.recyclerview.smartrefreshlayout.SmartRecyclerView
import com.qw.recyclerview.swiperefresh.SwipeRecyclerView
import com.qw.recyclerview.template.SmartListCompat

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SwipeCompatActivity : AppCompatActivity() {
    private lateinit var mList: SmartListCompat<String>
    private lateinit var bind: SwipeRefreshLayoutActivityBinding
    private lateinit var mVM: SwipeCompatVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SwipeRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        mVM = ViewModelProvider(this)[SwipeCompatVM::class.java]
        val smart = SwipeRecyclerView(bind.mRecyclerView, bind.mSwipeRefreshLayout)
        mList = object : SmartListCompat<String>(smart) {
            override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int) = Holder(
                LayoutInflater.from(this@SwipeCompatActivity)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            )

            inner class Holder(itemView: View) : BaseViewHolder(itemView) {
                override fun initData(position: Int) {
                    val label: TextView = itemView as TextView
                    val text = mList.modules[position]
                    label.text = text
                }
            }
        }
        val loadMore = DefaultLoadMore()
            .setEmptyHint("我是有底线的……")
            .setFailHint("哎呦，加载失败了")
            .setLoadingHint("努力加载中")
        mVM.result.observe(this, mList::notifyDataChanged)
        mList.setUpPage(mVM.page)
            .setUpLoadMore(loadMore)
            .setUpLayoutManager(MyLinearLayoutManager(this))
            .setLoadMoreEnable(true)
            .setRefreshEnable(true)
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    mVM.refresh()
                }
            })
            .setOnLoadMoreListener(object : OnLoadMoreListener {
                override fun onLoadMore() {
                    mVM.loadMore()
                }
            }).autoRefresh()
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
                        2,
                        StaggeredGridLayoutManager.VERTICAL
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}