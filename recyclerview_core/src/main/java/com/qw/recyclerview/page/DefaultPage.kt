package com.qw.recyclerview.page

/**
 * Created by qinwei on 2022/6/27 21:41
 * email: qinwei_it@163.com
 */
class DefaultPage(private val firstPage: Int = 1) : IPage {
    private var mCurrentPage = 0
    private var mWillLoadPage = firstPage
    private var mLastPage = -1

    override fun prepareRefresh() {
        mWillLoadPage = firstPage
    }

    override fun prepareLoadMore() {
        mWillLoadPage = mCurrentPage + 1
    }

    /**
     * case 1:  mWillLoadPage=2
     * pull down mWillLoadPage=1, req error mCurrentPage=0 isFirstPage() true
     * check data is empty  if true state view show error else toast error msg
     *
     * case 2:  mWillLoadPage=2
     * pull down mWillLoadPage=1, req ok  mCurrentPage=1 isFirstPage() true
     * clear data  add new data notify refresh
     */
    override fun isFirstPageRequest(): Boolean {
        return mWillLoadPage == firstPage
    }


    override fun hasNextPage(): Boolean {
        return mCurrentPage != mLastPage
    }

    override fun commitLoadSuccess(hasNextPage: Boolean) {
        mCurrentPage = mWillLoadPage
        if (!hasNextPage) {
            mLastPage = mCurrentPage
        }
    }

    override fun commitLoadFailure() {
        mWillLoadPage = mCurrentPage
    }

    fun getRequestPage(): Int {
        return mWillLoadPage
    }

    fun getCurrentPage(): Int {
        return mCurrentPage
    }
}
