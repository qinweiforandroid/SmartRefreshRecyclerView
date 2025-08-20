package com.qw.recyclerview.sample.ui.wanandroid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qw.recyclerview.page.DefaultPage
import com.qw.recyclerview.sample.repository.SmartRefreshRepository
import com.qw.recyclerview.sample.repository.entities.ArticleBean
import kotlinx.coroutines.launch

/**
 * Created by qinwei on 2024/3/23 16:13
 * email: qinwei_it@163.com
 */
class ArticleListVM : ViewModel() {

    val page = DefaultPage(0)
    val articles = MutableLiveData<Result<ArrayList<ArticleBean>>>()
    fun onRefresh() {
        page.pullToDown()
        loadData()
    }

    fun onLoadMore() {
        page.pullToUp()
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val response = SmartRefreshRepository.loadArticles(page.getWillPage())
                if (response.errorCode == 0) {
                    val pageCount = response.data.pageCount ?: 0
                    articles.value = Result.success(response.data.datas!!.apply {
                        page.onPageChanged(page.getWillPage() >= pageCount)
                    })
                } else {
                    articles.value = Result.failure(RuntimeException(response.errorMsg))
                }
            } catch (e: Exception) {
                articles.value = Result.failure(e)
            }
        }
    }
}