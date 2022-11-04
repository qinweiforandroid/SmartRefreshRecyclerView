package com.qw.recyclerview.template

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.BaseListAdapter
import com.qw.recyclerview.core.BaseViewHolder

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class BaseListComponent<T> constructor(private val mRecyclerView: RecyclerView) {
    val modules = ArrayList<T>()
    val adapter = object : BaseListAdapter() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return this@BaseListComponent.onCreateViewHolder(parent, viewType)
        }

        override fun getItemViewType(position: Int): Int {
            return this@BaseListComponent.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            return this@BaseListComponent.getItemCount()
        }
    }

    init {
        mRecyclerView.adapter = adapter
    }

    open fun getItemViewType(position: Int): Int = 0

    open fun getItemCount(): Int {
        return modules.size
    }

    protected abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    fun getRecyclerView(): RecyclerView {
        return mRecyclerView
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        mRecyclerView.layoutManager = layoutManager
    }
}