package com.qw.recyclerview.swiperefresh

import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qw.recyclerview.core.*
import com.qw.recyclerview.core.adapter.BaseViewHolder

/**
 * SwipeRefreshRecyclerView模版组件
 * Created by qinwei on 2022/1/9 2:46 下午
 * email: qinwei_it@163.com
 */
abstract class SwipeRefreshListComponent<T> constructor(
    mRecyclerView: RecyclerView,
    private val mSwipeRefreshLayout: SwipeRefreshLayout
) : BaseListComponent<T>(mRecyclerView) {
    private lateinit var mLoadMoreView: ILoadMore
    val smart: SmartRefreshHelper = SmartRefreshHelper()
    private var mLoadMoreState = State.IDLE
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private val typeLoadMore = -1

    init {
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        smart.inject(SwipeRefreshRecyclerView(mRecyclerView, mSwipeRefreshLayout))
        smart.setRefreshEnable(false)
        smart.setLoadMoreEnable(false)
    }

    fun injectLoadMore(loadMore: ILoadMore) {
        this.mLoadMoreView = loadMore
    }


    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener
        smart.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                onLoadMoreListener.onLoadMore()
            }

            override fun onStateChanged(state: State) {
                mLoadMoreState = state
                SRLog.d("SwipeRefreshRecyclerViewComponent onStateChanged:${mLoadMoreState.name} ")
                //解决第一次加载无数据情况
                if (state == State.NO_MORE || state == State.IDLE || state == State.ERROR) {
                    if (modules.size == 0) {
                        mLoadMoreState = State.EMPTY
                    }
                }
                adapter.notifyItemChanged(adapter.itemCount - 1)
//                mInnerAdapter.notifyDataSetChanged()
            }

            override fun getState(): State {
                return mLoadMoreState
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (viewType == typeLoadMore) {
            SRLog.d("SwipeRefreshRecyclerViewComponent typeLoadMore getView")
            return object : BaseViewHolder(mLoadMoreView.getView(parent.context)) {
                init {
                    mLoadMoreView.setOnRetryListener {
                        mLoadMoreState = State.LOADING
                        mLoadMoreView.onStateChanged(mLoadMoreState)
                        onLoadMoreListener?.onLoadMore()
                    }
                }

                override fun initData(position: Int) {
                    SRLog.d("SwipeRefreshRecyclerViewComponent initData:${mLoadMoreState.name}")
                    mLoadMoreView.onStateChanged(mLoadMoreState)
                }
            }
        }
        return onCreateBaseViewHolder(parent, viewType)
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        val lp = holder.itemView.layoutParams
        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
            lp.isFullSpan = isLoadMoreShow(holder.layoutPosition)
        }
    }

    override fun getItemCount(): Int {
        var count = super.getItemCount()
        if (smart.isLoadMoreEnable()) {
            count++
        }
        return count
    }

    final override fun getItemViewType(position: Int): Int {
        if (isLoadMoreShow(position)) {
            return typeLoadMore
        }
        return getItemViewTypeByPosition(position)
    }


    abstract fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    open fun getItemViewTypeByPosition(position: Int): Int = 0

    fun isLoadMoreShow(position: Int): Boolean {
        return smart.isLoadMoreEnable() && adapter.itemCount - 1 == position
    }


    fun getSwipeRefreshLayout(): SwipeRefreshLayout {
        return mSwipeRefreshLayout
    }
}