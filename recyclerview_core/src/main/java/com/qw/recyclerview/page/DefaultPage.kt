package com.qw.recyclerview.page

/**
 * Created by qinwei on 2022/6/27 21:41
 * email: qinwei_it@163.com
 */
class DefaultPage : IPage {
    private var mCurrentPage = 0
    private var mWillLoadPage = 1
    private var mLastPage = -1

    override fun pullToDown() {
        mWillLoadPage = 1
    }

    override fun pullToUp() {
        mWillLoadPage = mCurrentPage + 1
    }

    override fun isFirstPage(): Boolean {
        return mCurrentPage <= 1
    }

    override fun hasMore(): Boolean {
        return mCurrentPage != mLastPage
    }

    override fun onPageChanged(isLastPage: Boolean) {
        mCurrentPage = mWillLoadPage
        if (isLastPage) {
            mLastPage = mCurrentPage
        }
    }

    fun getCurrentPage(): Int {
        return mCurrentPage
    }
}