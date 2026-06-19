package com.qw.recyclerview.page

/**
 * Created by qinwei on 2022/6/27 21:41
 * email: qinwei_it@163.com
 */
class DefaultPage(private val firstPage: Int = 1) : IPage {
    private var currentPage = 0
    private var willLoadPage = firstPage
    private var lastPage = -1
    private var pageState = PageState()

    override val state: PageState
        get() = pageState

    override fun onLoadFirstPage() {
        willLoadPage = firstPage
        pageState = pageState.copy(
            action = PageAction.REFRESH,
            phase = PagePhase.LOADING,
            isFirstPage = true
        )
    }

    override fun onLoadNextPage() {
        willLoadPage = currentPage + 1
        pageState = pageState.copy(
            action = PageAction.LOAD_MORE,
            phase = PagePhase.LOADING,
            isFirstPage = false
        )
    }

    override fun isLoadFirstPage(): Boolean {
        return pageState.isFirstPage
    }

    override fun hasNextPage(): Boolean {
        return pageState.hasNextPage
    }

    override fun onLoadSuccess(hasNextPage: Boolean) {
        currentPage = willLoadPage
        if (!hasNextPage) {
            lastPage = currentPage
        }
        pageState = pageState.copy(
            phase = PagePhase.IDLE,
            hasNextPage = hasNextPage,
            isFirstPage = currentPage == firstPage
        )
    }

    override fun onLoadFailure() {
        willLoadPage = currentPage
        pageState = pageState.copy(
            phase = PagePhase.ERROR
        )
    }

    fun getWillLoadPage(): Int {
        return willLoadPage
    }

    fun getCurrentPage(): Int {
        return currentPage
    }
}
