package com.qw.recyclerview.smartrefreshlayout

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.BaseListComponent
import com.qw.recyclerview.core.SmartRefreshHelper
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SmartRefresh1ListComponent<T> constructor(
    mRecyclerView: RecyclerView,
    mSmartRefreshLayout: SmartRefreshLayout
) : BaseListComponent<T>(mRecyclerView) {
    val smart: SmartRefreshHelper = SmartRefreshHelper()

    init {
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        smart.inject(SmartRefreshLayout1RecyclerView(mRecyclerView, mSmartRefreshLayout))
        smart.setRefreshEnable(false)
        smart.setLoadMoreEnable(false)
    }
}