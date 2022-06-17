package com.qw.recyclerview.layout

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * create by qinwei at 2022/6/17 18:27
 */
class MyLinearLayoutManager : LinearLayoutManager, ILayoutManager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return this
    }
}