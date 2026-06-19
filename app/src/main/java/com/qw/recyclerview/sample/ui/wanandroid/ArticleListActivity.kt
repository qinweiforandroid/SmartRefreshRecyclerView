package com.qw.recyclerview.sample.ui.wanandroid

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.loadmore.DefaultLoadMore
import com.qw.recyclerview.layout.MyLinearLayoutManager
import com.qw.recyclerview.page.DefaultPage
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.repository.entities.ArticleBean
import com.qw.recyclerview.swiperefresh.SwipeRecyclerView
import com.qw.recyclerview.template.SmartListCompat

/**
 * Created by qinwei on 2024/3/23 16:09
 * email: qinwei_it@163.com
 */
class ArticleListActivity : AppCompatActivity(R.layout.activity_article_list) {

    private lateinit var mVM: ArticleListVM
    private lateinit var list: SmartListCompat<ArticleBean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rv = findViewById<RecyclerView>(R.id.mRecyclerView)
        ViewCompat.setOnApplyWindowInsetsListener(rv) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val swipe = findViewById<SwipeRefreshLayout>(R.id.mSwipeRefreshLayout)
        mVM = ViewModelProvider(this)[ArticleListVM::class.java]
        mVM.articles.observe(this) {
            if (it.isSuccess) {
                list.submitPageData(it.getOrNull()!!)
            } else {
                list.submitPageError()
            }
        }
        list = object : SmartListCompat<ArticleBean>(SwipeRecyclerView(rv, swipe)) {
            override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int) =
                object : BaseViewHolder(layoutInflater.inflate(R.layout.activity_article_item, parent, false)) {
                    val mTitleLabel = itemView.findViewById<TextView>(R.id.mItemTitleLabel)
                    val mItemAuthorLabel = itemView.findViewById<TextView>(R.id.mItemAuthorLabel)
                    override fun initData(position: Int) {
                        val item = model as ArticleBean
                        mTitleLabel.text = item.title
                        if (item.author.isNullOrBlank()) {
                            mItemAuthorLabel.text = "分享人:${item.shareUser}"
                        } else {
                            mItemAuthorLabel.text = "作者:${item.author}"
                        }
                        mItemAuthorLabel.append("\t时间:${item.niceDate}")
                    }
                }
        }
        val page = DefaultPage(1)
        mVM.injectPage(page)
        list.setLoadMoreEnable(true)
            .setRefreshEnable(true)
            .setPaging(page)
            .setUpLayoutManager(MyLinearLayoutManager(this))
            .setLoadMoreView(DefaultLoadMore())
            .setOnLoadMoreListener(object : OnLoadMoreListener {
                override fun onLoadMore() {
                    mVM.onLoadMore()
                }
            })
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    mVM.onRefresh()
                }
            }).setRefreshing(true)
    }
}
