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

    /**
     * case 1:  mWillLoadPage=2
     * pull down mWillLoadPage=1, req error mCurrentPage=0 isFirstPage() true
     * check data is empty  if true state view show error else toast error msg
     *
     * case 2:  mWillLoadPage=2
     * pull down mWillLoadPage=1, req ok  mCurrentPage=1 isFirstPage() true
     * clear data  add new data notify refresh
     */
    override fun isFirstPage(): Boolean {
        return mWillLoadPage == 1
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