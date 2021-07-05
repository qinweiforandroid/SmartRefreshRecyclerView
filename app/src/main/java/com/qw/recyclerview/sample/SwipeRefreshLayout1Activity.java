package com.qw.recyclerview.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qw.recyclerview.core.OnLoadMoreListener;
import com.qw.recyclerview.core.OnRefreshListener;
import com.qw.recyclerview.core.SmartRefreshHelper;
import com.qw.recyclerview.sample.databinding.SwipeRefreshLayoutActivityBinding;
import com.qw.recyclerview.swiperefresh.SwipeRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qinwei on 2021/7/1 20:38
 */
public class SwipeRefreshLayout1Activity extends AppCompatActivity {

    private SwipeRefreshLayoutActivityBinding bind;

    private SmartRefreshHelper smartRefresh;

    private ListAdapter adapter;
    protected ArrayList<String> modules = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = SwipeRefreshLayoutActivityBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        //1.配置RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListAdapter();
        mRecyclerView.setAdapter(adapter);

        //2.配置SwipeRefreshLayout
        SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);

        //3.配置SmartRefreshHelper
        smartRefresh = new SmartRefreshHelper();
        //SmartRefreshLayoutRecyclerView将mRecyclerView和mSmartRefreshLayout打包后，交给SmartRefreshHelper进行管理
        smartRefresh.inject(new SwipeRefreshRecyclerView(mRecyclerView, mSwipeRefreshLayout));

        //设置下拉刷新可用
        smartRefresh.setRefreshEnable(true);
        //设置加载更多可用
        smartRefresh.setLoadMoreEnable(true);
        //设置下拉刷新监听
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                smartRefresh.getRecyclerView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        modules.clear();
                        for (int i = 0; i < 20; i++) {
                            modules.add("" + i);
                        }
                        adapter.notifyDataSetChanged();
                        smartRefresh.finishRefresh(true);
                    }
                }, 1000);
            }
        });
        //设置加载更多监听
        smartRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                smartRefresh.getRecyclerView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int size = modules.size();
                        for (int i = size; i < size + 20; i++) {
                            modules.add("" + i);
                        }
                        if (modules.size() < 100) {
                            smartRefresh.setLoadMore(true, false);
                        } else {
                            smartRefresh.setLoadMore(true, true);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, 1000);
            }
        });
        smartRefresh.autoRefresh();
    }


    class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public int getItemCount() {
            return modules.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(SwipeRefreshLayout1Activity.this).inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
            ((Holder) holder).initData(position, payloads);
        }
    }

    class Holder extends RecyclerView.ViewHolder {
        private final TextView label;

        public Holder(@NonNull View itemView) {
            super(itemView);
            label = (TextView) itemView;
        }

        public void initData(int position, @NonNull List<Object> payloads) {
            String text = modules.get(position);
            label.setText(text);
        }
    }
}