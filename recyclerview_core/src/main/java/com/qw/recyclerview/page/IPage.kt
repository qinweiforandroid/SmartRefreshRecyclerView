package com.qw.recyclerview.page

/**
 * Created by qinwei on 2022/6/27 21:34
 * email: qinwei_it@163.com
 */
interface IPage {
    /**
     * 下拉
     */
    fun pullToDown()

    /**
     * 上拉
     */
    fun pullToUp()

    /**
     * 是否是首页
     */
    fun isFirstPage(): Boolean

    /**
     * 是否有更多
     */
    fun hasMore(): Boolean

    /**
     * 标记最后一页
     */
    fun onPageChanged(isLastPage: Boolean = false)
}