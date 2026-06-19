package com.qw.recyclerview.page

/**
 * Created by qinwei on 2022/6/27 21:34
 * email: qinwei_it@163.com
 */
interface IPage {
    fun prepareRefresh()

    fun prepareLoadMore()

    fun isFirstPageRequest(): Boolean

    fun hasNextPage(): Boolean

    fun commitLoadSuccess(hasNextPage: Boolean)

    fun commitLoadFailure()
}
