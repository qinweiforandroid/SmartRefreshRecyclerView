package com.qw.recyclerview.loadmore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.R
import com.qw.recyclerview.core.SRLog

/**
 * @author qinwei
 */
class DefaultLoadMore : AbsLoadMore() {
    @LayoutRes
    private var mFooterLayoutId: Int = R.layout.sr_widget_footer
    private var mFailHint = "加载失败,点击重试"
    private var mLoadingHint = "正在加载..."
    private var mEmptyHint = "--没有更多数据了--"
    fun setFooterLayoutId(@LayoutRes layoutId: Int): DefaultLoadMore {
        mFooterLayoutId = layoutId
        return this
    }

    fun setFailHint(text: String): DefaultLoadMore {
        mFailHint = text
        return this
    }

    fun setLoadingHint(text: String): DefaultLoadMore {
        mLoadingHint = text
        return this
    }

    fun setEmptyHint(text: String): DefaultLoadMore {
        mEmptyHint = text
        return this
    }

    override fun onCreateLoadMoreViewHolder(parent: ViewGroup): BaseViewHolder {
        return FooterHolder(
            LayoutInflater.from(parent.context).inflate(mFooterLayoutId, parent, false)
        )
    }

    private inner class FooterHolder(itemView: View) : BaseViewHolder(itemView),
        View.OnClickListener {
        private val mProgressBar = itemView.findViewById<View>(R.id.mProgressBar) as ProgressBar
        private val mFooterLabel = itemView.findViewById<View>(R.id.mFooterLabel) as TextView
        override fun onViewAttachedToWindow() {
            val lp = itemView.layoutParams
            if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
                lp.isFullSpan = true
            }
        }

        override fun initData(position: Int) {
            itemView.setOnClickListener(null)
            SRLog.d("DefaultLoadMore initData:${getState().name}")
            when (getState()) {
                State.ERROR -> {
                    itemView.setOnClickListener(this)
                    mFooterLabel.text = mFailHint
                    mProgressBar.visibility = LinearLayout.GONE
                    itemView.visibility = LinearLayout.VISIBLE
                }
                State.EMPTY,
                State.IDLE -> {
                    itemView.visibility = LinearLayout.INVISIBLE
                }
                State.LOADING -> {
                    mFooterLabel.text = mLoadingHint
                    mProgressBar.visibility = LinearLayout.VISIBLE
                    itemView.visibility = LinearLayout.VISIBLE
                }
                State.NO_MORE -> {
                    mProgressBar.visibility = LinearLayout.GONE
                    mFooterLabel.text = mEmptyHint
                    itemView.visibility = LinearLayout.VISIBLE
                }
                else -> {

                }
            }
        }

        override fun onClick(v: View?) {
            retry.invoke()
        }
    }
}