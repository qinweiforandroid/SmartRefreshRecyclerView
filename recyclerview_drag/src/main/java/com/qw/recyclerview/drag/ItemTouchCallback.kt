package com.qw.recyclerview.drag

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Created by qinwei on 2018/1/29.
 */
abstract class ItemTouchCallback : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
    ): Int {
        var swipeFlags: Int = ItemTouchHelper.START or ItemTouchHelper.END
        var dragFlags: Int =
            if (recyclerView.layoutManager is GridLayoutManager || recyclerView.layoutManager is StaggeredGridLayoutManager) {
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            } else {
                ItemTouchHelper.UP or ItemTouchHelper.DOWN
            }
        if (viewHolder is ItemDragCallback) {
            if (!viewHolder.isItemViewSwipeEnabled) {
                swipeFlags = 0
            }
            if (!viewHolder.isLongPressDragEnabled) {
                dragFlags = 0
            }
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (target is ItemDragCallback && !target.isLongPressDragEnabled) {
            return false
        }
        return super.canDropOver(recyclerView, current, target)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        onMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }


    abstract fun onMove(from: Int, target: Int)

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwiped(viewHolder.adapterPosition)
    }

    abstract fun onSwiped(position: Int)

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is ItemDragCallback) {
                viewHolder.onDragStart()
            }
            onDragStart()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    open fun onDragStart() {}
    open fun onDragFinished() {}

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is ItemDragCallback) {
            viewHolder.onDragFinished()
        }
        onDragFinished()
    }
}