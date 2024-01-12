package com.qw.recyclerview.sample.ui.viewpager

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.layout.MyLinearLayoutManager
import com.qw.recyclerview.layout.MyStaggeredGridLayoutManager
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.ActivityViewpagerBinding
import com.qw.recyclerview.sample.databinding.ActivityViewpagerItemBinding
import com.qw.recyclerview.template.ListCompat

/**
 * Created by qinwei on 2023/12/11 16:42
 * email: qinwei_it@163.com
 */
class ViewPagerActivity : AppCompatActivity(), OnPageChangeListener {
    private lateinit var mViewPagerSnapHelper: ViewPagerSnapHelper
    private lateinit var mListCompat: ListCompat<String>
    private lateinit var binding: ActivityViewpagerBinding

    companion object {
        private const val TAG = "ViewPagerActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewpagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        mListCompat = object : ListCompat<String>(binding.mRecyclerView) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return PageHolder(ActivityViewpagerItemBinding.inflate(layoutInflater, parent, false))
            }

            inner class PageHolder(val itemBinding: ActivityViewpagerItemBinding) :
                BasePageViewHolder(itemBinding.root) {
                override fun initData(position: Int) {
                    Log.d(TAG, "initData $position")
                    itemBinding.textView.text = "$position-$model"
                }

                override fun onPageShow() {
                    Log.d(TAG, "onPageShow $adapterPosition")
                }
            }
        }
        mViewPagerSnapHelper = ViewPagerSnapHelper()
            .attachToRecyclerView(binding.mRecyclerView)
            .setOnPageChangedListener(this)
        for (i in 100..106) {
            mListCompat.modules.add("$i")
        }
        mViewPagerSnapHelper.notifyDataSetChanged(2)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_position, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_pos -> {
                mViewPagerSnapHelper.setCurrentItem(4)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPageSelected(position: Int) {
        Log.d(TAG, "onPageSelected $position")
    }
}