package com.qw.recyclerview.sample.ui.drag

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.*
import com.qw.recyclerview.layout.MyLinearLayoutManager
import com.qw.recyclerview.layout.MyStaggeredGridLayoutManager
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.SwipeRefreshLayoutActivityBinding
import com.qw.recyclerview.sample.ui.swipe.SwipeComponentVM
import com.qw.recyclerview.swiperefresh.template.SwipeListComponent
import java.util.*

/**
 * Created by qinwei on 2021/7/1 20:38
 */
class DragSwipeListActivity : AppCompatActivity() {
    private lateinit var mList: SwipeListComponent<String>
    private lateinit var bind: SwipeRefreshLayoutActivityBinding
    private lateinit var mVM: SwipeComponentVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SwipeRefreshLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        mVM = ViewModelProvider(this)[SwipeComponentVM::class.java]
        mVM.result.observe(this) {
            if (mVM.isFirstPage()) {
                mList.modules.clear()
                mList.modules.addAll(it)
                mList.finishRefresh(true)
                mList.adapter.notifyDataSetChanged()
            } else {
                val size = mList.modules.size
                mList.modules.addAll(it)
                mList.finishLoadMore(true, !mVM.hasMore())
                mList.adapter.notifyItemRangeInserted(size, it.size)
            }
        }
        bind.mRecyclerView.itemAnimator = DefaultItemAnimator().apply {
            moveDuration = 50
        }
        mList = object : SwipeListComponent<String>(bind.mRecyclerView, bind.mSwipeRefreshLayout) {
            override fun onCreateBaseViewHolder(
                parent: ViewGroup, viewType: Int
            ): BaseViewHolder {
                return Holder(
                    LayoutInflater.from(this@DragSwipeListActivity)
                        .inflate(android.R.layout.simple_list_item_1, parent, false)
                )
            }

            inner class Holder(itemView: View) : BaseTouchViewHolder(itemView) {
                var pos = 0
                override fun initData(position: Int) {
                    this.pos = position
                    val label: TextView = itemView as TextView
                    val text = mList.modules[position]
                    label.text = text
                }

                override fun isLongPressDragEnabled(): Boolean {
                    return pos != 0
                }

                override fun onDragStart() {
                    super.onDragStart()
                    itemView.setBackgroundColor(Color.YELLOW)
                }

                override fun onDragFinished() {
                    super.onDragFinished()
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
        ItemTouchHelper(object : ItemTouchCallback() {
            override fun onMove(fromPosition: Int, toPosition: Int) {
                Collections.swap(mList.modules, fromPosition, toPosition)
                mList.adapter.notifyItemMoved(fromPosition, toPosition)
            }

            override fun onMoved(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                fromPos: Int,
                target: RecyclerView.ViewHolder,
                toPos: Int,
                x: Int,
                y: Int
            ) {
                if (target is ItemDragCallback) {
                    Log.d("qinwei", "fromPos:$fromPos toPos:${toPos} Y:${y}")
                }
                targetPos = toPos
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
            }

            var targetPos = 0

            override fun onDragFinished() {
                super.onDragFinished()
                targetPos = -1
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                Log.d("qinwei", "onChildDraw dY:${dY}")
                if (viewHolder.adapterPosition == 1) {
                    if (dY < 0) return
                }
                if (targetPos != 1) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

            override fun onSwiped(position: Int) {
                mList.modules.removeAt(position)
                mList.adapter.notifyItemRemoved(position)
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }


        }).attachToRecyclerView(bind.mRecyclerView)

        mList.setLayoutManager(MyLinearLayoutManager(this)).setRefreshEnable(true)
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    Handler(Looper.myLooper()!!).postDelayed({
                        mVM.refresh()
                    }, 1000)
                }
            }).autoRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recyclerview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_linearLayout -> {
                mList.setLayoutManager(MyLinearLayoutManager(this))
            }
            R.id.action_gridLayout -> {
                mList.setLayoutManager(mList.getGridLayoutManager(2))
            }
            R.id.action_staggeredGridLayout -> {
                mList.setLayoutManager(
                    MyStaggeredGridLayoutManager(
                        2, StaggeredGridLayoutManager.VERTICAL
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}