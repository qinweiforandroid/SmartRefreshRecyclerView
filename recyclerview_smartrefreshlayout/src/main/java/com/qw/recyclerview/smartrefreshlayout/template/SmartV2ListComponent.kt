package com.qw.recyclerview.smartrefreshlayout.template

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.BaseListAdapter
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.template.BaseListComponent
import com.qw.recyclerview.core.ISmartRecyclerView
import com.qw.recyclerview.core.SRLog
import com.qw.recyclerview.layout.ILayoutManager
import com.qw.recyclerview.layout.MyGridLayoutManager
import com.qw.recyclerview.smartrefreshlayout.SmartV2RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SmartV2ListComponent<T> constructor(
    mRecyclerView: RecyclerView,
    mSmartRefreshLayout: SmartRefreshLayout
) {
    private val listComponent = object : BaseListComponent<T>(mRecyclerView) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return onCreateBaseViewHolder(parent, viewType)
        }

        override fun getItemViewType(position: Int): Int {
            return getItemViewTypeByPosition(position)
        }
    }
    val smart: ISmartRecyclerView = SmartV2RecyclerView(mRecyclerView, mSmartRefreshLayout).apply {
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        setRefreshEnable(false)
        setLoadMoreEnable(false)
    }
    val adapter: BaseListAdapter
        get() {
            return listComponent.adapter
        }

    val modules: ArrayList<T>
        get() {
            return listComponent.modules
        }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager): ISmartRecyclerView {
        listComponent.setLayoutManager(layoutManager)
        return smart
    }

    abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    open fun getItemViewTypeByPosition(position: Int): Int = 0
}