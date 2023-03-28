package com.qw.recyclerview.smartrefreshlayout.template

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.template.ListCompat
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.smartrefreshlayout.SmartV2RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SmartListV2Compat<T> constructor(
    mRecyclerView: RecyclerView,
    mSmartRefreshLayout: SmartRefreshLayout
) : ListCompat<T>(mRecyclerView) {
    val smart: ISmartRecyclerView = SmartV2RecyclerView(mRecyclerView, mSmartRefreshLayout).apply {
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        setRefreshEnable(false)
        setLoadMoreEnable(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return onCreateBaseViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return getItemViewTypeByPosition(position)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    open fun getItemViewTypeByPosition(position: Int): Int = 0
}