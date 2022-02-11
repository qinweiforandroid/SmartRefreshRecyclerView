package com.qw.recyclerview.loadmore

import android.view.ViewGroup
import com.qw.recyclerview.core.BaseViewHolder

/**
 * Created by qinwei on 2022/1/9 8:25 下午
 * email: qinwei_it@163.com
 */
abstract class AbsLoadMore {
    private var state = State.IDLE
    protected var retry: () -> Unit = {}

    fun onStateChanged(state: State) {
        this.state = state
    }

    fun getState(): State {
        return this.state
    }

    abstract fun onCreateLoadMoreViewHolder(parent: ViewGroup): BaseViewHolder
    fun setOnRetryListener(function: () -> Unit) {
        this.retry = function
    }
}