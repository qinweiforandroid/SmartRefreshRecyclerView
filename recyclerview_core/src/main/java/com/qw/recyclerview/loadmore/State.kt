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
     * 加载失败
     */
    ERROR,

    /**
     * 空数据
     */
    EMPTY,

    /**
     * 没有更多数据
     */
    NO_MORE
}