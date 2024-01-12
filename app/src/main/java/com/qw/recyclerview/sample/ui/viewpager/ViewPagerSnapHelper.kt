package com.qw.recyclerview.sample.ui.viewpager

import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.BaseViewHolder
import kotlin.math.abs

/**
 * Created by qinwei on 2023/10/14 11:27
 * email: qinwei_it@163.com
 */
class ViewPagerSnapHelper {
    companion object {
        const val SCROLL_STATE_IDLE = 0

        const val SCROLL_STATE_DRAGGING = 1

        const val SCROLL_STATE_SETTLING = 2
    }


    private lateinit var mRecyclerView: RecyclerView
    private val mPagerSnapHelper = PagerSnapHelper()
    private var pageChangedListener: OnPageChangeListener? = null
    private var curPos = -1

    fun attachToRecyclerView(mRecyclerView: RecyclerView): ViewPagerSnapHelper {
        this.mRecyclerView = mRecyclerView
        mPagerSnapHelper.attachToRecyclerView(mRecyclerView)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val manager = recyclerView.layoutManager!!
                    val snapView = mPagerSnapHelper.findSnapView(manager)
                    snapView?.let {
                        val pos = manager.getPosition(it)
                        if (curPos != pos) {
                            curPos = pos
                            onPageChanged(curPos)
                        }
                    }
                }
                pageChangedListener?.onPageScrollStateChanged(
                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> {
                            SCROLL_STATE_IDLE
                        }

                        RecyclerView.SCROLL_STATE_DRAGGING -> {
                            SCROLL_STATE_DRAGGING
                        }

                        else -> {
                            SCROLL_STATE_SETTLING
                        }
                    }
                )
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                pageChangedListener?.onPageScrolled(curPos, dx, dy)
            }
        })
        return this
    }

    private fun onPageChanged(curPos: Int) {
        val holder = mRecyclerView.findViewHolderForAdapterPosition(curPos) ?: return
        if (holder is OnPageListener) {
            holder.onPageShow()
        }
        pageChangedListener?.onPageSelected(curPos)
    }

    fun setOnPageChangedListener(listener: OnPageChangeListener): ViewPagerSnapHelper {
        this.pageChangedListener = listener
        return this
    }

    fun getCurrentItem(): Int {
        return curPos
    }

    fun setCurrentItem(pos: Int, smoothScroll: Boolean = false) {
        val count = mRecyclerView.adapter?.itemCount ?: 0
        // check pos
        if (pos < 0 || pos > count - 1) {
            return
        }
        if (!smoothScroll) {
            mRecyclerView.scrollToPosition(pos)
            mRecyclerView.post {
                onPageChanged(pos)
            }
            return
        }
        val size = pos - curPos
        if (abs(size) > 1) {
            if (size > 0) {
                mRecyclerView.scrollToPosition(pos - 1)
            } else {
                mRecyclerView.scrollToPosition(pos + 1)
            }
            mRecyclerView.post {
                mRecyclerView.smoothScrollToPosition(pos)
            }
        } else {
            mRecyclerView.smoothScrollToPosition(pos)
        }
    }

    /**
     * 数据首次刷新
     * @param pos 定位的位置
     */
    fun notifyDataSetChanged(pos: Int = 0) {
        if (pos < 0) return
        mRecyclerView.adapter?.notifyDataSetChanged()
        mRecyclerView.post {
            val itemCount = mRecyclerView.adapter?.itemCount ?: 0
            if (itemCount >= pos) {
                curPos = pos
                setCurrentItem(curPos)
            }
        }
    }
}

abstract class BasePageViewHolder(itemView: View) : BaseViewHolder(itemView), OnPageListener

interface OnPageListener {
    fun onPageShow()
}

interface OnPageChangeListener {
    fun onPageScrolled(
        position: Int,
        dx: Int,
        dy: Int
    ) {
    }

    fun onPageSelected(position: Int) {}

    fun onPageScrollStateChanged(state: Int) {}
}