package com.qw.recyclerview.footer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.qw.recyclerview.loadmore.AbsLoadMore
import com.qw.recyclerview.core.SRLog
import com.qw.recyclerview.loadmore.State
import com.qw.recyclerview.core.BaseViewHolder

/**
 * @author qinwei
 */
class DefaultLoadMore : AbsLoadMore() {

    override fun onCreateLoadMoreViewHolder(parent: ViewGroup): BaseViewHolder {
        return FooterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.sr_widget_footer, parent, false)
        )
    }

    private inner class FooterHolder(itemView: View) : BaseViewHolder(itemView),
        View.OnClickListener {
        private val mProgressBar = itemView.findViewById<View>(R.id.mProgressBar) as ProgressBar
        private val mFooterLabel = itemView.findViewById<View>(R.id.mFooterLabel) as TextView
        override fun initData(position: Int) {
            itemView.setOnClickListener(null)
            SRLog.d("DefaultLoadMore initData:${getState().name}")
            when (getState()) {
                State.ERROR -> {
                    itemView.setOnClickListener(this)
                    mFooterLabel.text = "加载失败,点击重试"
                    mProgressBar.visibility = LinearLayout.GONE
                    itemView.visibility = LinearLayout.VISIBLE
                }
                State.EMPTY,
                State.IDLE -> {
                    itemView.visibility = LinearLayout.INVISIBLE
                }
                State.LOADING -> {
                    mFooterLabel.text = "正在加载..."
                    mProgressBar.visibility = LinearLayout.VISIBLE
                    itemView.visibility = LinearLayout.VISIBLE
                }
                State.NO_MORE -> {
                    mProgressBar.visibility = LinearLayout.GONE
                    mFooterLabel.text = "--没有更多数据了--"
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