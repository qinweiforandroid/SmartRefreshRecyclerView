package com.qw.recyclerview.sample.ui.swipe

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.qw.recyclerview.page.DefaultPage

/**
 * Created by qinwei on 2022/6/27 22:14
 * email: qinwei_it@163.com
 */
class SwipeComponentVM : ViewModel() {
    private val page = DefaultPage()
    val result: MediatorLiveData<ArrayList<String>> = MediatorLiveData()
    private var size = 0
    fun refresh() {
        page.pullToDown()
        val list = ArrayList<String>()
        for (i in 0..19) {
            list.add("" + i)
        }
        size = list.size
        page.onPageChanged()
        result.value = list
    }

    fun hasMore(): Boolean {
        return page.hasMore()
    }

    fun loadMore() {
        page.pullToUp()
        val list = ArrayList<String>()
        for (i in size until size + 20) {
            list.add("" + i)
        }
        size += list.size
        page.onPageChanged(size > 70)
        result.value = list
    }

    fun isFirstPage(): Boolean {
        return page.isFirstPage()
    }
}