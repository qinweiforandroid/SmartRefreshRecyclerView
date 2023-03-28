package com.qw.recyclerview.drag;

/**
 * item拖拽监听
 * Created by qinwei on 2018/1/29.
 */

public interface ItemDragCallback {
    boolean isLongPressDragEnabled();

    /**
     * 精准控制item view swipe
     *
     * @return
     */
    boolean isItemViewSwipeEnabled();

    /**
     * 开始拖拽
     */
    void onDragStart();

    /**
     * 结束拖拽
     */
    void onDragFinished();
}
