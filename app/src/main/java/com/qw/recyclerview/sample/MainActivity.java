package com.qw.recyclerview.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qw.recyclerview.core.OnLoadMoreListener;
import com.qw.recyclerview.core.OnRefreshListener;
import com.qw.recyclerview.core.QSmartRefreshHelper;
import com.qw.recyclerview.sample.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding bind;
    private ArrayList<String> modules = new ArrayList<>();
    private QAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        SwipeRefreshRecyclerView swipeRefreshRecyclerView = new SwipeRefreshRecyclerView(bind.mRecyclerView, bind.mSwipeRefreshLayout);
        swipeRefreshRecyclerView.setRefreshEnable(true);
        swipeRefreshRecyclerView.setLoadMoreEnable(true);
        swipeRefreshRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, "start 刷新", Toast.LENGTH_SHORT).show();
                bind.mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "end 刷新", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < 20; i++) {
                            modules.add("" + i);
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshRecyclerView.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        swipeRefreshRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Toast.makeText(MainActivity.this, "start 加载更多", Toast.LENGTH_SHORT).show();
                bind.mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int size = modules.size();
                        for (int i = size; i < size + 10; i++) {
                            modules.add("" + i);
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "end 加载更多", Toast.LENGTH_SHORT).show();
                        swipeRefreshRecyclerView.setLoadMore(0,true,false);
                    }
                }, 3000);
            }
        });
        adapter = new QAdapter();
        swipeRefreshRecyclerView.setAdapter(adapter);
        modules.clear();
        for (int i = 0; i < 20; i++) {
            modules.add("" + i);
        }
        adapter.notifyDataSetChanged();
    }

    private class QAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(MainActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof Holder) {
                ((Holder) holder).initData(position);
            }
        }

        @Override
        public int getItemCount() {
            return modules.size();
        }

        class Holder extends RecyclerView.ViewHolder {
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
}