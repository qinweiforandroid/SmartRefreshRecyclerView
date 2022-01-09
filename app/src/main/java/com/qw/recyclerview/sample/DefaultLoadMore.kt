package com.qw.recyclerview.sample

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.qw.recyclerview.core.ILoadMore
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import com.qw.recyclerview.core.State

/**
 * @author qinwei
 */
class DefaultLoadMore : ILoadMore {

    private lateinit var footerView: FooterView
    private lateinit var function: () -> Unit

    inner class FooterView : LinearLayout, View.OnClickListener {
        private var mProgressBar: ProgressBar? = null
        private var mFooterLabel: TextView? = null
        private var state = State.IDLE

        constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
            context,
            attrs,
            defStyle
        ) {
            initializeView(context)
        }

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            initializeView(context)
        }

        constructor(context: Context) : super(context) {
            initializeView(context)
        }

        private fun initializeView(context: Context) {
            LayoutInflater.from(context).inflate(R.layout.sr_widget_pulltorefresh_footer, this)
            mProgressBar = findViewById<View>(R.id.mProgressBar) as ProgressBar
            mFooterLabel = findViewById<View>(R.id.mFooterLabel) as TextView
            onStateChanged(State.IDLE)
        }

        override fun onClick(v: View) {
            function.invoke()
        }

        fun onStateChanged(state: State) {
            this.state = state
            setOnClickListener(null)
            when (state) {
                State.ERROR -> {
                    setOnClickListener(this)
                    mFooterLabel!!.text = "加载失败,点击重试"
                    mProgressBar!!.visibility = GONE
                    this.visibility = VISIBLE
                }
                State.EMPTY,
                State.IDLE -> {
                    this.visibility = INVISIBLE
                }
                State.LOADING -> {
                    mFooterLabel!!.text = "正在加载..."
                    mProgressBar!!.visibility = VISIBLE
                    this.visibility = VISIBLE
                }
                State.NO_MORE -> {
                    mProgressBar!!.visibility = GONE
                    mFooterLabel!!.text = "--没有更多数据了--"
                    this.visibility = VISIBLE
                }

                else -> {

                }
            }
        }
    }

    override fun getView(context: Context): View {
        footerView = FooterView(context)
        footerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        return footerView
    }

    override fun onStateChanged(loadMoreState: State) {
        footerView.onStateChanged(loadMoreState)
    }

    override fun setOnRetryListener(function: () -> Unit) {
        this.function = function
    }
}
