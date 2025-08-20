package com.qw.recyclerview.sample.ui.stick

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Created by lenovo on 2017/1/6.
 */
class StickyItemDecoration(private val mStickyHeadContainer: StickyHeadContainer,
    private val mStickyHeadType: Int) : ItemDecoration() {
    private var mFirstVisiblePosition = 0
    private var mStickyHeadPosition = 0
    private lateinit var mInto: IntArray
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mEnableStickyHead = true
    private var mOnStickyChangeListener: OnStickyChangeListener? = null
    fun setOnStickyChangeListener(onStickyChangeListener: OnStickyChangeListener?) {
        mOnStickyChangeListener = onStickyChangeListener
    }

    // 当我们调用mRecyclerView.addItemDecoration()方法添加decoration的时候，RecyclerView在绘制的时候，去会绘制decorator，即调用该类的onDraw和onDrawOver方法，
    // 1.onDraw方法先于drawChildren
    // 2.onDrawOver在drawChildren之后，一般我们选择复写其中一个即可。
    // 3.getItemOffsets 可以通过outRect.set()为每个Item设置一定的偏移量，主要用于绘制Decorator。
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        checkCache(parent)
        if (mAdapter == null) {
            // checkCache的话RecyclerView未设置之前mAdapter为空
            return
        }
        calculateStickyHeadPosition(parent)
        if (mEnableStickyHead && mFirstVisiblePosition >= mStickyHeadPosition && mStickyHeadPosition != -1) {
            val belowView = parent.findChildViewUnder((c.width / 2).toFloat(),
                mStickyHeadContainer.childHeight + 0.01f)
            mStickyHeadContainer.onDataChange(mStickyHeadPosition)
            val offset: Int
            offset = if (isStickyHead(parent, belowView) && belowView!!.top > 0) {
                belowView.top - mStickyHeadContainer.childHeight
            } else {
                0
            }
            if (mOnStickyChangeListener != null) {
                mOnStickyChangeListener!!.onScrollable(offset)
            }
        } else {
            if (mOnStickyChangeListener != null) {
                mOnStickyChangeListener!!.onInVisible()
            }
        }
    }

    fun enableStickyHead(enableStickyHead: Boolean) {
        mEnableStickyHead = enableStickyHead
        if (!mEnableStickyHead) {
            mStickyHeadContainer.visibility = View.INVISIBLE
        }
    }

    private fun calculateStickyHeadPosition(parent: RecyclerView) {
        val layoutManager = parent.layoutManager
        // 获取第一个可见的item位置
        mFirstVisiblePosition = findFirstVisiblePosition(layoutManager)
        // 获取标签的位置，
        val stickyHeadPosition = findStickyHeadPosition(mFirstVisiblePosition)
        if (stickyHeadPosition >= 0 && mStickyHeadPosition != stickyHeadPosition) {
            // 标签位置有效并且和缓存的位置不同
            mStickyHeadPosition = stickyHeadPosition
        }
    }

    /**
     * 从传入位置递减找出标签的位置
     *
     * @param formPosition
     * @return
     */
    private fun findStickyHeadPosition(formPosition: Int): Int {
        for (position in formPosition downTo 0) {
            // 位置递减，只要查到位置是标签，立即返回此位置
            val type = mAdapter!!.getItemViewType(position)
            if (isStickyHeadType(type)) {
                return position
            }
        }
        return -1
    }

    /**
     * 通过适配器告知类型是否为标签
     *
     * @param type
     * @return
     */
    private fun isStickyHeadType(type: Int): Boolean {
        return mStickyHeadType == type
    }

    /**
     * 找出第一个可见的Item的位置
     *
     * @param layoutManager
     * @return
     */
    private fun findFirstVisiblePosition(layoutManager: RecyclerView.LayoutManager?): Int {
        var firstVisiblePosition = 0
        if (layoutManager is GridLayoutManager) {
            firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        } else if (layoutManager is LinearLayoutManager) {
            firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            mInto = IntArray(layoutManager.spanCount)
            layoutManager.findFirstVisibleItemPositions(mInto)
            firstVisiblePosition = Int.MAX_VALUE
            for (pos in mInto) {
                firstVisiblePosition = pos.coerceAtMost(firstVisiblePosition)
            }
        }
        return firstVisiblePosition
    }

    /**
     * 检查缓存
     *
     * @param parent
     */
    private fun checkCache(parent: RecyclerView) {
        val adapter = parent.adapter
        if (mAdapter !== adapter) {
            mAdapter = adapter
            // 适配器为null或者不同，清空缓存
            mStickyHeadPosition = -1
            mAdapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
                override fun onChanged() {
                    reset()
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    reset()
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                    reset()
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    reset()
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    reset()
                }

                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    reset()
                }
            })
        }
    }

    private fun reset() {
        mStickyHeadContainer.reset()
    }

    /**
     * 查找到view对应的位置从而判断出是否标签类型
     *
     * @param parent
     * @param view
     * @return
     */
    private fun isStickyHead(parent: RecyclerView, view: View?): Boolean {
        val position = parent.getChildAdapterPosition(view!!)
        if (position == RecyclerView.NO_POSITION) {
            return false
        }
        val type = mAdapter!!.getItemViewType(position)
        return isStickyHeadType(type)
    }
}

interface OnStickyChangeListener {
    fun onScrollable(offset: Int)
    fun onInVisible()
}
