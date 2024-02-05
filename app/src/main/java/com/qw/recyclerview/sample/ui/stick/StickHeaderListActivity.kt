package com.qw.recyclerview.sample.ui.stick

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.IItemViewType
import com.qw.recyclerview.core.ItemViewDelegate
import com.qw.recyclerview.core.MultiTypeUseCase
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.template.ListCompat


/**
 * Created by qinwei on 2024/2/4 20:43
 * email: qinwei_it@163.com
 */
class StickHeaderListActivity : AppCompatActivity() {
    private lateinit var mStickyHeadContainer: StickyHeadContainer
    private lateinit var mStickyRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stick_header)
        mStickyRecyclerView = findViewById(R.id.mStickyRecyclerView)
        mStickyHeadContainer = findViewById(R.id.mStickyHeadContainer)
        val mMultiTypeUseCase = MultiTypeUseCase()
        mMultiTypeUseCase.register(1, object : ItemViewDelegate {
            override fun onCreateViewHolder(context: Context, parent: ViewGroup): BaseViewHolder {
                return Holder(layoutInflater.inflate(R.layout.stick_item_layout,
                    parent,
                    false))
            }

            inner class Holder(itemView: View) : BaseViewHolder(itemView) {
                val label = itemView as TextView
                override fun initData(position: Int) {
                    val item = model as Header
                    label.setBackgroundColor(Color.RED)
                    label.setTextColor(Color.WHITE)
                    label.text = item.title
                }

            }
        })
        mMultiTypeUseCase.register(2, object : ItemViewDelegate {
            override fun onCreateViewHolder(context: Context, parent: ViewGroup): BaseViewHolder {
                return Holder(layoutInflater.inflate(R.layout.stick_item_layout,
                    parent,
                    false))
            }

            inner class Holder(itemView: View) : BaseViewHolder(itemView) {
                val label = itemView as TextView
                override fun initData(position: Int) {
                    val item = model as Item
                    label.text = item.title
                }
            }
        })
        val list = object : ListCompat<IItemViewType>(mStickyRecyclerView) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return mMultiTypeUseCase.onCreateViewHolder(parent, viewType)
            }

            override fun getItemViewType(position: Int): Int {
                return modules[position].getItemViewType()
            }
        }
        mStickyRecyclerView.addItemDecoration(StickyItemDecoration(mStickyHeadContainer, 1).apply {
            setOnStickyChangeListener(object : OnStickyChangeListener {
                override fun onScrollable(offset: Int) {
                    mStickyHeadContainer.scrollChild(offset)
                    mStickyHeadContainer.visibility = View.VISIBLE
                }

                override fun onInVisible() {
                    mStickyHeadContainer.visibility = View.INVISIBLE
                }
            })
        })
        mStickyHeadContainer.setDataCallback(object : StickyHeadContainer.DataCallback {
            override fun onDataChange(pos: Int) {
                val item = list.modules[pos] as Header
                val label = mStickyHeadContainer.findViewById<TextView>(R.id.label)
                label.setOnClickListener {
                    Toast.makeText(this@StickHeaderListActivity,
                        item.title,
                        Toast.LENGTH_SHORT).show()
                }
                label.setBackgroundColor(Color.RED)
                label.setTextColor(Color.WHITE)
                label.text = item.title
            }
        })
        list.modules.add(Header("header0"))
        list.modules.add(Item("item"))
        list.modules.add(Item("item0"))
        list.modules.add(Header("header1"))
        for (i in 0..10) {
            list.modules.add(Item("item${i}"))
        }
        list.modules.add(Header("header2"))
        for (i in 11..16) {
            list.modules.add(Item("item${i}"))
        }
        list.modules.add(Header("header3"))
        for (i in 21..35) {
            list.modules.add(Item("item${i}"))
        }
        list.adapter.notifyDataSetChanged()
    }

    data class Header(val title: String) : IItemViewType {
        override fun getItemViewType(): Int {
            return 1
        }
    }

    data class Item(val title: String) : IItemViewType {
        override fun getItemViewType(): Int {
            return 2
        }
    }
}