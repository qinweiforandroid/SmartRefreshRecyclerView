package com.qw.recyclerview.swiperefresh

import android.util.Log
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.BaseListAdapter
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.State

/**
 * Created by qinwei on 2021/6/30 12:45
 */
abstract class BaseListAdapter : BaseListAdapter() {
    companion object {
        const val TYPE_HEADER = -2
        const val TYPE_FOOTER = -1
    }

    /**
     * 控制RecyclerView 头部显示隐藏
     */
    var isHeaderShow = false

    /**
     * 控制RecyclerView 底部显示隐藏
     */
    var isFooterShow = false

    protected var footerState: State = State.IDLE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (isHeaderShow && viewType == TYPE_HEADER) {
            onCreateHeaderHolder(parent)
        } else if (isFooterShow && viewType == TYPE_FOOTER) {
            onCreateFooterHolder(parent)
        } else {
            onCreateBaseViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        //数据下标重新计算
        var position = position
        if (isHeaderShow) {
            position--
        }
        super.onBindViewHolder(holder, position)
    }

    private fun isHeaderShow(position: Int): Boolean {
        return isHeaderShow && position == 0
    }

    private fun isFooterShow(position: Int): Boolean {
        return isFooterShow && position == itemCount - 1
    }

    override fun getItemCount(): Int {
        return getItemViewCount() + (if (isHeaderShow) 1 else 0) + if (isFooterShow) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        var position = position
        if (isHeaderShow(position)) {
            return TYPE_HEADER
        } else if (isFooterShow(position)) {
            return TYPE_FOOTER
        }
        if (isHeaderShow) {
            position--
        }
        return getItemViewTypeByPosition(position)
    }

    fun notifyFooterDataSetChanged(state: State) {
        this.footerState = state
        Log.d("adapter", state.name + " count:$itemCount")
        notifyItemChanged(itemCount - 1)
    }

    /**
     * 获取item视图个数
     *
     * @return
     */
    protected abstract fun getItemViewCount(): Int

    /**
     * 获取item视图
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    /**
     * 获取头部视图
     *
     * @param parent
     * @return
     */
    protected abstract fun onCreateHeaderHolder(parent: ViewGroup): BaseViewHolder

    /**
     * 获取脚步视图
     *
     * @param parent
     * @return
     */
    protected abstract fun onCreateFooterHolder(parent: ViewGroup): BaseViewHolder

    /**
     * 根据position获取视图类型
     *
     * @param position
     * @return
     */
    protected fun getItemViewTypeByPosition(position: Int): Int {
        return 0
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        val lp = holder.itemView.layoutParams
        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
            lp.isFullSpan = isHeaderShow(holder.layoutPosition) || isFooterShow(holder.layoutPosition)
        }
    }

    fun canLoadMore(): Boolean {
        return when (footerState) {
            State.IDLE, State.ERROR ->
                true
            else -> {
                false
            }
        }
    }
}