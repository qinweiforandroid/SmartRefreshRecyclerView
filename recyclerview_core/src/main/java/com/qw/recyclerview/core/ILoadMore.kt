package com.qw.recyclerview.core

import android.view.ViewGroup
import com.qw.recyclerview.core.adapter.BaseViewHolder

/**
 * Created by qinwei on 2022/1/9 8:25 下午
 * email: qinwei_it@163.com
 */
interface ILoadMore {

    fun getLoadMoreViewHolder(parent: ViewGroup): BaseViewHolder

    fun notifyStateChanged(state: State)

    fun getState(): State

    fun setOnRetryListener(function: () -> Unit)
}