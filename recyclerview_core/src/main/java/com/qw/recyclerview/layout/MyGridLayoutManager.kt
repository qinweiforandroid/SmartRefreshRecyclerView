package com.qw.recyclerview.layout

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * create by qinwei at 2022/6/17 19:18
 */
class MyGridLayoutManager : GridLayoutManager, ILayoutManager {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context?, spanCount: Int) : super(context, spanCount)

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return this
    }

    override fun getLastVisibleItemPosition(): Int {
        return findLastVisibleItemPosition()
    }
}