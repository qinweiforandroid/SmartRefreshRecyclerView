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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qw.recyclerview.sample.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding bind;
    private ArrayList<String> modules = new ArrayList<>();
    private QAdapter adapter;

    private int REFRESH_IDLE = 0;
    private int REFRESH_PULL = 1;
    private int REFRESH_UP = 2;
    private int refresh_state = REFRESH_IDLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh_state = REFRESH_PULL;
                Toast.makeText(MainActivity.this, "start 刷新", Toast.LENGTH_SHORT).show();
                bind.mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "end 刷新", Toast.LENGTH_SHORT).show();
                        bind.mSwipeRefreshLayout.setRefreshing(false);
                        for (int i = 0; i < 20; i++) {
                            modules.add("" + i);
                        }
                        adapter.notifyDataSetChanged();
                        markIdle();
                    }
                }, 3000);
            }
        });

        adapter = new QAdapter();
        bind.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bind.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "newState：" + newState);
                if (refresh_state != REFRESH_IDLE) {
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && checkedIsNeedLoadMore()) {
                    refresh_state = REFRESH_PULL;
                    Toast.makeText(MainActivity.this, "start 加载更多", Toast.LENGTH_SHORT).show();
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int size = modules.size();
                            for (int i = size; i < size + 10; i++) {
                                modules.add("" + i);
                            }
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "end 加载更多", Toast.LENGTH_SHORT).show();
                            markIdle();
                        }
                    }, 3000);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

        });

        bind.mRecyclerView.setAdapter(adapter);
        modules.clear();
        for (int i = 0; i < 20; i++) {
            modules.add("" + i);
        }
        adapter.notifyDataSetChanged();

    }

    private void markIdle() {
        refresh_state = REFRESH_IDLE;
    }

    private boolean checkedIsNeedLoadMore() {
        int lastVisiblePosition = 0;
        RecyclerView.LayoutManager layoutManager = bind.mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            lastVisiblePosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager sdlm = (StaggeredGridLayoutManager) layoutManager;
            lastVisiblePosition = sdlm.findLastCompletelyVisibleItemPositions(null)[sdlm.findLastCompletelyVisibleItemPositions(null).length - 1];
        }
        return adapter.getItemCount() - lastVisiblePosition <= 5;
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