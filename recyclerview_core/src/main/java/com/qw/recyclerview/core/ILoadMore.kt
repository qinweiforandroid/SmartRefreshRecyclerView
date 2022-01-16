package com.qw.recyclerview.core

import android.content.Context
import android.view.View

/**
 * Created by qinwei on 2022/1/9 8:25 下午
 * email: qinwei_it@163.com
 */
interface ILoadMore {
    fun getView(context: Context): View
    fun onStateChanged(loadMoreState: State)
    fun setOnRetryListener(function: () -> Unit)
}