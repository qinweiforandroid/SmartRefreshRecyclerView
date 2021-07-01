package com.qw.recyclerview.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.qw.recyclerview.core.BaseViewHolder;
import com.qw.recyclerview.core.State;
import com.qw.recyclerview.sample.core.BaseSwipeRefreshLayoutListActivity;
import com.qw.recyclerview.sample.databinding.SwipeRefreshLayoutActivityBinding;

public class SwipeRefreshLayoutPlusActivity extends BaseSwipeRefreshLayoutListActivity<String> {
    private SwipeRefreshLayoutActivityBinding bind;

    @Override
    protected void setContentView() {
        bind = SwipeRefreshLayoutActivityBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
       smartRefreshHelper.autoRefresh();
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
                adapter.notifyFooterDataSetChanged(State.IDLE);
                smartRefreshHelper.finishRefresh(true);
            }
        }, 2000);
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
        return new Holder(LayoutInflater.from(SwipeRefreshLayoutPlusActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false));
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