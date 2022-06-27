package com.qw.recyclerview.layout

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * create by qinwei at 2022/6/17 19:44
 */
class MyStaggeredGridLayoutManager: StaggeredGridLayoutManager,ILayoutManager {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(spanCount: Int, orientation: Int) : super(spanCount, orientation)

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return this
    }

    override fun getLastVisibleItemPosition(): Int {
       return findLastCompletelyVisibleItemPositions(null)[findLastCompletelyVisibleItemPositions(
            null
        ).size - 1]
    }
}