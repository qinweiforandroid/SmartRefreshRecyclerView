package com.qw.recyclerview.sample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qw.recyclerview.sample.databinding.ActivityMainBinding;
import com.qw.recyclerview.sample.ui.smart.ListWithRefreshActivity;
import com.qw.recyclerview.sample.ui.smart.SmartRefresh1ListComponentActivity;
import com.qw.recyclerview.sample.ui.smart.ListWithRefreshAndLoadMoreActivity;
import com.qw.recyclerview.sample.ui.swipe.SwipeListWithRefreshActivity;
import com.qw.recyclerview.sample.ui.swipe.SwipeListWithRefreshAndLoadMoreActivity;
import com.qw.recyclerview.sample.ui.swipe.SwipeRefreshListComponentActivity;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.mSwipeRefreshRecyclerViewSample1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SwipeListWithRefreshActivity.class));
            }
        });

        bind.mSwipeRefreshRecyclerViewSample2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SwipeListWithRefreshAndLoadMoreActivity.class));
            }
        });

        bind.mSwipeRefreshRecyclerViewSample3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SwipeRefreshListComponentActivity.class));
            }
        });
        bind.mSmartRefreshLayoutRecyclerViewSampleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListWithRefreshAndLoadMoreActivity.class));
            }
        });
        bind.mSmartRefreshLayoutRecyclerView1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListWithRefreshActivity.class));
            }
        });
        bind.mSmartRefreshLayoutRecyclerView2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SmartRefresh1ListComponentActivity.class));
            }
        });
    }
}