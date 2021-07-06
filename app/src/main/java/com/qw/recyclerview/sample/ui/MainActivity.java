package com.qw.recyclerview.sample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qw.recyclerview.sample.databinding.ActivityMainBinding;


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
                startActivity(new Intent(MainActivity.this, SwipeRefreshLayout1Activity.class));
            }
        });

        bind.mSwipeRefreshRecyclerViewSample2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SwipeRefreshLayout2Activity.class));
            }
        });
        
        bind.mSwipeRefreshRecyclerViewSamplePlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SwipeRefreshLayoutPlusActivity.class));
            }
        });
        bind.mSmartRefreshLayoutRecyclerViewSampleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SmartRefreshLayoutActivity.class));
            }
        });
        bind.mSmartRefreshLayoutRecyclerViewSamplePlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SmartRefreshLayoutPlusActivity.class));
            }
        });
    }
}