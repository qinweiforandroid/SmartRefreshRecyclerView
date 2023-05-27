package com.qw.recyclerview.sample.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.qw.recyclerview.sample.databinding.ActivityMainBinding
import com.qw.recyclerview.sample.ui.chat.ChatActivity
import com.qw.recyclerview.sample.ui.drag.DragSwipeListActivity
import com.qw.recyclerview.sample.ui.recyclerview.Recycler1Activity
import com.qw.recyclerview.sample.ui.recyclerview.Recycler2Activity
import com.qw.recyclerview.sample.ui.swipe.SwipeRecyclerViewActivity
import com.qw.recyclerview.sample.ui.swipe.SwipeCompatActivity
import com.qw.recyclerview.sample.ui.smart.SmartV2RecyclerViewActivity
import com.qw.recyclerview.sample.ui.smart.SmartCompatActivity
import com.qw.recyclerview.sample.ui.smart.SmartV2CompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.mRecyclerViewSample1Btn.setOnClickListener {
            startActivity(Intent(this, Recycler1Activity::class.java))
        }
        bind.mRecyclerViewSample2Btn.setOnClickListener {
            startActivity(Intent(this, Recycler2Activity::class.java))
        }
        bind.mSwipeRefreshRecyclerViewSample2Btn.setOnClickListener {
            startActivity(Intent(this, SwipeRecyclerViewActivity::class.java))
        }
        bind.mSwipeRefreshRecyclerViewSample3Btn.setOnClickListener {
            startActivity(Intent(this, SwipeCompatActivity::class.java))
        }
        bind.mSmartRefreshLayoutRecyclerViewSampleBtn.setOnClickListener {
            startActivity(Intent(this, SmartV2RecyclerViewActivity::class.java))
        }
        bind.mSmartRefreshLayoutRecyclerView1Btn.setOnClickListener {
            startActivity(Intent(this, SmartCompatActivity::class.java))
        }
        bind.mSmartRefreshLayoutRecyclerView2Btn.setOnClickListener {
            startActivity(Intent(this, SmartV2CompatActivity::class.java))
        }
        bind.mDragSwipeBtn.setOnClickListener {
            startActivity(Intent(this, DragSwipeListActivity::class.java))
        }
        bind.mChatBtn.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }
}