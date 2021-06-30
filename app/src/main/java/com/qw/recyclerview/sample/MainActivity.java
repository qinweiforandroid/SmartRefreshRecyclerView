package com.qw.recyclerview.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.qw.recyclerview.core.BaseViewHolder;
import com.qw.recyclerview.core.OnLoadMoreListener;
import com.qw.recyclerview.core.OnRefreshListener;
import com.qw.recyclerview.core.SmartRefreshHelper;
import com.qw.recyclerview.sample.databinding.ActivityMainBinding;
import com.qw.recyclerview.swiperefresh.BaseListAdapter;
import com.qw.recyclerview.swiperefresh.State;
import com.qw.recyclerview.swiperefresh.SwipeRefreshRecyclerView;
import com.qw.recyclerview.swiperefresh.footer.FooterView;
import com.qw.recyclerview.swiperefresh.footer.IFooter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FooterView.OnFooterViewListener {
    private ActivityMainBinding bind;
    private ArrayList<String> modules = new ArrayList<>();
    private QAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        SmartRefreshHelper smartRefreshHelper = new SmartRefreshHelper();
        smartRefreshHelper.inject(new SwipeRefreshRecyclerView(bind.mRecyclerView, bind.mSwipeRefreshLayout));
        smartRefreshHelper.setRefreshEnable(true);
        smartRefreshHelper.setLoadMoreEnable(true);
        smartRefreshHelper.setLayoutManager(new LinearLayoutManager(this));
        smartRefreshHelper.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                bind.mSwipeRefreshLayout.postDelayed(new Runnable() {
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
        });
        smartRefreshHelper.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                bind.mRecyclerView.postDelayed(new Runnable() {
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
        });
        adapter = new QAdapter();
        smartRefreshHelper.setAdapter(adapter);
        modules.clear();
        for (int i = 0; i < 20; i++) {
            modules.add("" + i);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFooterClick() {

    }

    private class QAdapter extends BaseListAdapter {

        @Override
        protected int getItemViewCount() {
            return modules.size();
        }

        @NonNull
        @Override
        protected BaseViewHolder onCreateBaseViewHolder(@NotNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(MainActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false));
        }


        @NonNull
        @Override
        protected BaseViewHolder onCreateFooterHolder(@NotNull ViewGroup parent) {
            FooterView footerView = new FooterView(MainActivity.this);
            footerView.setOnFooterViewListener(MainActivity.this);
            footerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(footerView);
        }

        @NonNull
        @Override
        protected BaseViewHolder onCreateHeaderHolder(@NotNull ViewGroup parent) {
            return null;
        }

        public class FooterViewHolder extends BaseViewHolder {
            private IFooter footer;

            public FooterViewHolder(View itemView) {
                super(itemView);
                if (itemView instanceof IFooter) {
                    footer = (IFooter) itemView;
                } else {
                    throw new IllegalArgumentException("the view must impl IFooter interface");
                }
            }

            @Override
            public void initData(int position) {
                footer.onFooterChanged(getFooterState());
            }
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
}