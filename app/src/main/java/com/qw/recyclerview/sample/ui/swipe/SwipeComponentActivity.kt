package com.qw.recyclerview.sample.ui.swipe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
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
import com.qw.recyclerview.swiperefresh.template.SwipeListComponent

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class SwipeComponentActivity : AppCompatActivity() {
    private lateinit var mList: SwipeListComponent<String>
    private lateinit var bind: SwipeRefreshLayoutActivityBinding
    private lateinit var mVM: SwipeComponentVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SwipeRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        mVM = ViewModelProvider(this)[SwipeComponentVM::class.java]
        mVM.result.observe(this) {
            if (mVM.isFirstPage()) {
                mList.modules.clear()
                mList.modules.addAll(it)
                mList.finishRefresh(true)
                mList.adapter.notifyDataSetChanged()
            } else {
                val size = mList.modules.size
                mList.modules.addAll(it)
                mList.finishLoadMore(true, !mVM.hasMore())
                mList.adapter.notifyItemRangeInserted(size, it.size)
            }
        }
        mList =
            object : SwipeListComponent<String>(bind.mRecyclerView, bind.mSwipeRefreshLayout) {
                override fun onCreateBaseViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): BaseViewHolder {
                    return Holder(
                        LayoutInflater.from(this@SwipeComponentActivity)
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
        val loadMore = DefaultLoadMore()
            .setEmptyHint("我是有底线的……")
            .setFailHint("哎呦，加载失败了")
            .setLoadingHint("努力加载中")
        mList.supportLoadMore(loadMore, object : OnLoadMoreListener {
            override fun onLoadMore() {
                Handler(Looper.myLooper()!!).postDelayed({
                    mVM.loadMore()
                }, 1000)
            }
        })
        mList.setLayoutManager(MyLinearLayoutManager(this))
            .setRefreshEnable(true)
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    Handler(Looper.myLooper()!!).postDelayed({
                        mVM.refresh()
                    }, 1000)
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