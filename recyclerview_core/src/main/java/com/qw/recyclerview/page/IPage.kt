package com.qw.recyclerview.page

/**
 * Created by qinwei on 2022/6/27 21:34
 * email: qinwei_it@163.com
 */
enum class PageAction {
    REFRESH,
    LOAD_MORE
}

enum class PagePhase {
    IDLE,
    LOADING,
    ERROR
}

data class PageState(
    val action: PageAction? = null,
    val phase: PagePhase = PagePhase.IDLE,
    val hasNextPage: Boolean = true,
    val isFirstPage: Boolean = true
)

interface IPage {
    /**
     * 分页状态
     */
    val state: PageState

    /**
     * 记录加载第一页
     */
    fun onLoadFirstPage()

    /**
     * 记录加载下一页
     */
    fun onLoadNextPage()

    /**
     * 当前是否加载的第一页
     */
    fun isLoadFirstPage(): Boolean

    /**
     * 是否有下一页
     */
    fun hasNextPage(): Boolean

    /**
     * 加载成功
     * @param hasNextPage 是否有下一页
     */
    fun onLoadSuccess(hasNextPage: Boolean)

    /**
     * 加载失败
     */
    fun onLoadFailure()
}
