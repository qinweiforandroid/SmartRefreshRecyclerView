package com.qw.recyclerview.sample.ui.drag

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
        return true
    }

    override fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        fromPos: Int,
        target: RecyclerView.ViewHolder,
        toPos: Int,
        x: Int,
        y: Int
    ) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
        onMoved(viewHolder.adapterPosition, target.adapterPosition)
    }

    abstract fun onMoved(from: Int, target: Int)

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwiped(direction, viewHolder.adapterPosition)
    }

    abstract fun onSwiped(direction: Int, position: Int)

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