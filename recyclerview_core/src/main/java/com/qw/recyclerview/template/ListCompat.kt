package com.qw.recyclerview.template

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.BaseListAdapter
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.IItemViewType
import com.qw.recyclerview.core.ItemViewDelegate
import com.qw.recyclerview.core.MultiTypeUseCase

/**
 * ListCompat列表组件，内置adapter和数据源
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class ListCompat<T>(private val mRecyclerView: RecyclerView) {
    val modules = ArrayList<T>()
    val adapter = object : BaseListAdapter() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return this@ListCompat.onCreateViewHolder(parent, viewType)
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            tryBindModel(holder, position)
            super.onBindViewHolder(holder, position)
        }

        private fun tryBindModel(holder: BaseViewHolder, position: Int) {
            if (position < modules.size) {
                holder.bindModel(modules[position] as Any)
            } else {
                holder.bindModel(Any())
            }
        }

        override fun onBindViewHolder(
            holder: BaseViewHolder, position: Int, payloads: MutableList<Any>
        ) {
            tryBindModel(holder, position)
            super.onBindViewHolder(holder, position, payloads)
        }

        override fun getItemViewType(position: Int): Int {
            return this@ListCompat.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            return this@ListCompat.getItemCount()
        }
    }

    init {
        setLayoutManager(LinearLayoutManager(mRecyclerView.context))
        mRecyclerView.adapter = adapter
    }

    /**
     * 更改数据源位置并且通知adapter更新位置
     * @param from 原位置
     * @param target 目标位置
     */
    fun onMoved(from: Int, target: Int) {
        val fromItem = modules.removeAt(from)
        modules.add(target, fromItem)
        adapter.notifyItemMoved(from, target)
    }

    /**
     * 删除指定位置数据并通知adapter更新
     * @param position 侧滑位置
     */
    fun onRemoved(position: Int) {
        modules.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    /**
     * 获取视图类型 call from RecyclerView.Adapter.getItemViewType(position: Int)
     * @param position 视图所处位置
     */
    open fun getItemViewType(position: Int): Int = 0

    /**
     * call from  RecyclerView.Adapter.getItemCount()
     */
    open fun getItemCount(): Int {
        return modules.size
    }

    /**
     * call from  RecyclerView.Adapter.onCreateViewHolder(parent: ViewGroup, viewType: Int)
     */
    protected abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    fun getRecyclerView(): RecyclerView {
        return mRecyclerView
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        mRecyclerView.layoutManager = layoutManager
    }

    class MultiTypeBuilder {

        private val mMultiType = MultiTypeUseCase()

        fun register(
            viewType: Int,
            delegate: ItemViewDelegate
        ): MultiTypeBuilder {
            mMultiType.register(viewType, delegate)
            return this
        }

        fun <T> create(
            mRecyclerView: RecyclerView
        ): ListCompat<T> {
            return object : ListCompat<T>(mRecyclerView) {

                override fun getItemViewType(position: Int): Int {
                    val item = modules[position]
                    if (item is IItemViewType) {
                        return item.getItemViewType()
                    }
                    throw IllegalArgumentException("module must be impl IItemViewType interface")
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                    return mMultiType.onCreateViewHolder(parent, viewType)
                }
            }
        }
    }
}