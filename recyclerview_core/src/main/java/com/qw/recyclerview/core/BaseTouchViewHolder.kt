package com.qw.recyclerview.core

import android.view.View

/**
 * Created by qinwei on 2023/3/14 20:42
 * email: qinwei_it@163.com
 */
abstract class BaseTouchViewHolder(itemView: View) : BaseViewHolder(itemView),
    ItemDragCallback {

    override fun onDragStart() {
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onDragFinished() {

    }
}