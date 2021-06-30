package com.qw.recyclerview.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.qw.recyclerview.core.BaseViewHolder;
import com.qw.recyclerview.sample.databinding.ActivityMainBinding;
import com.qw.recyclerview.core.State;

public class MainActivity extends BaseListActivity<String> {
    private ActivityMainBinding bind;

    @Override
    protected void setContentView() {
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        for (int i = 0; i < 20; i++) {
            modules.add("" + i);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        smartRefreshHelper.getRecyclerView().postDelayed(new Runnable() {
            @Override
            public void run() {
                modules.clear();
                for (int i = 0; i < 20; i++) {
                    modules.add("" + i);
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "刷新", Toast.LENGTH_SHORT).show();
                adapter.notifyFooterDataSetChanged(State.IDLE);
                smartRefreshHelper.setRefreshing(false);
            }
        }, 3000);
    }

    @Override
    public void onLoadMore() {
        super.onLoadMore();
        smartRefreshHelper.getRecyclerView().postDelayed(new Runnable() {
            @Override
            public void run() {
                int size = modules.size();
                for (int i = size; i < size + 10; i++) {
                    modules.add("" + i);
                }
                if (modules.size() < 50) {
                    smartRefreshHelper.setLoadMore(true, false);
                } else {
                    smartRefreshHelper.setLoadMore(true, true);
                }
                adapter.notifyDataSetChanged();
            }
        }, 3000);
    }

    @Override
    protected BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(MainActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    class Holder extends BaseViewHolder {
        private final TextView label;

        public Holder(@NonNull View itemView) {
            super(itemView);
            label = (TextView) itemView;
        }

        public void initData(int position) {
            String text = modules.get(position);
            label.setText(text);
        }
    }
}