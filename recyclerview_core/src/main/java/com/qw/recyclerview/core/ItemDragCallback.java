package com.qw.recyclerview.core;

/**
 * item拖拽监听
 * Created by qinwei on 2018/1/29.
 */

public interface ItemDragCallback {
    /**
     * 开始拖拽
     */
    void onDragStart();

    boolean isLongPressDragEnabled();

    /**
     * 精准控制item view swipe
     *
     * @return
     */
    boolean isItemViewSwipeEnabled();

    /**
     * 结束拖拽
     */
    void onDragFinished();
}
