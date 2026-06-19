package com.qw.recyclerview.loadmore

/**
 * 加载更多视图的状态
 */
enum class State {
    /**
     * 空闲状态
     */
    IDLE,

    /**
     * 加载中
     */
    LOADING,

    /**
     * 失败状态
     */
    ERROR,

    /**
     * 隐藏
     */
    HIDDEN,

    /**
     * 没有更多数据
     */
    NO_MORE
}