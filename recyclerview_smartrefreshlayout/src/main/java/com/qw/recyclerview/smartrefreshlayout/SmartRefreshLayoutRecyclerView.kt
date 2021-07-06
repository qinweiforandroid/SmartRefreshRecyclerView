package com.qw.recyclerview.smartrefreshlayout

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.adapter.ILoadMore
import com.qw.recyclerview.core.OnLoadMoreListener
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.core.SmartRefreshable
import com.qw.recyclerview.core.footer.State
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.lang.IllegalArgumentException

/**
 * Created by qinwei on 2021/6/29 21:44
 */
class SmartRefreshLayoutRecyclerView(private val mRecyclerView: RecyclerView, private val mSmartRefreshLayout: SmartRefreshLayout) : SmartRefreshable {
    private var mRefreshEnable: Boolean = false
    private var mLoadMoreEnable: Boolean = false
    private var onRefreshListener: OnRefreshListener? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var state = SmartRefreshable.REFRESH_IDLE
    private var loadMore: ILoadMore? = null

    init {
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!mLoadMoreEnable) {
                    return
                }
                if (loadMore == null) {
                    return
                }
                if (state != SmartRefreshable.REFRESH_IDLE) {
                    return
                }
                if (!loadMore!!.canLoadMore()) {
                    return
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && checkedIsNeedLoadMore()) {
                    state = SmartRefreshable.REFRESH_UP
                    mSmartRefreshLayout.setEnableRefresh(false)
                    loadMore?.notifyFooterDataSetChanged(State.LOADING)
                    onLoadMoreListener?.onLoadMore()
                }
            }
        })
        mSmartRefreshLayout.setOnRefreshListener {
            state = SmartRefreshable.REFRESH_PULL
            onRefreshListener?.onRefresh()
        }
        mSmartRefreshLayout.setEnableRefresh(mRefreshEnable)
        //使用adapter的loadmore功能
        mSmartRefreshLayout.setEnableLoadMore(false)

        if (mRecyclerView.adapter == null) {
            throw IllegalArgumentException("RecyclerView must be setAdapter")
        }

        if (mRecyclerView.adapter is ILoadMore) {
            loadMore = mRecyclerView.adapter as ILoadMore
        } else {
            //没有实现ILoadMore接口的adapter不具备加载更多功能
            setLoadMoreEnable(false)
        }
    }

    private fun markIdle() {
        state = SmartRefreshable.REFRESH_IDLE
    }

    private fun checkedIsNeedLoadMore(): Boolean {
        var lastVisiblePosition = 0
        val layoutManager: RecyclerView.LayoutManager = mRecyclerView.layoutManager!!
        if (layoutManager is LinearLayoutManager) {
            lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val sdlm = layoutManager
            lastVisiblePosition = sdlm.findLastCompletelyVisibleItemPositions(null)[sdlm.findLastCompletelyVisibleItemPositions(null).size - 1]
        }
        return mRecyclerView.adapter!!.itemCount - lastVisiblePosition <= 5
    }

    override fun getRecyclerView(): RecyclerView {
        return mRecyclerView
    }

    override fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        mRecyclerView.layoutManager = layoutManager
    }

    override fun setItemAnimator(itemAnimator: RecyclerView.ItemAnimator) {
        mRecyclerView.itemAnimator = itemAnimator
    }

    override fun setOnRefreshListener(onRefreshListener: OnRefreshListener) {
        this.onRefreshListener = onRefreshListener
    }

    override fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    override fun setRefreshEnable(isEnabled: Boolean) {
        mRefreshEnable = isEnabled
        mSmartRefreshLayout.setEnableRefresh(isEnabled)
    }

    override fun setLoadMoreEnable(isEnabled: Boolean) {
        mLoadMoreEnable = isEnabled
        loadMore?.setShowLoadMoreFooter(mLoadMoreEnable)
    }

    override fun autoRefresh() {
        if (mRefreshEnable) {
            state = SmartRefreshable.REFRESH_PULL
            mSmartRefreshLayout.autoRefresh()
        }
    }

    override fun finishRefresh(success: Boolean) {
        mSmartRefreshLayout.finishRefresh()
        if (success) {
            loadMore?.notifyFooterDataSetChanged(State.IDLE)
        }
        markIdle()
    }

    override fun finishLoadMore(success: Boolean, noMoreData: Boolean) {
        if (!mLoadMoreEnable) {
            return
        }
        getRecyclerView().postDelayed({
            val state: State = if (success) {
                if (noMoreData) {
                    State.EMPTY
                } else {
                    State.IDLE
                }
            } else {
                State.ERROR
            }
            loadMore?.notifyFooterDataSetChanged(state)
            if (mRefreshEnable) {
                mSmartRefreshLayout.setEnableRefresh(true)
            }
            markIdle()
        }, 200)
    }
}