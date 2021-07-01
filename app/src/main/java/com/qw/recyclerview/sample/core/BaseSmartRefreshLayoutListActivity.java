package com.qw.recyclerview.sample.core;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qw.recyclerview.core.BaseViewHolder;
import com.qw.recyclerview.core.OnLoadMoreListener;
import com.qw.recyclerview.core.OnRefreshListener;
import com.qw.recyclerview.core.SmartRefreshHelper;
import com.qw.recyclerview.core.footer.FooterView;
import com.qw.recyclerview.sample.R;
import com.qw.recyclerview.smartrefreshlayout.BaseListAdapter;
import com.qw.recyclerview.smartrefreshlayout.SmartRefreshLayoutRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class BaseSmartRefreshLayoutListActivity<T> extends AppCompatActivity implements FooterView.OnFooterViewListener, OnRefreshListener, OnLoadMoreListener {
    protected SmartRefreshHelper smartRefreshHelper;
    protected ListAdapter adapter;
    protected ArrayList<T> modules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initView();
        initData(savedInstanceState);
    }

    protected abstract void setContentView();

    protected void initView() {
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        adapter = new ListAdapter();
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        SmartRefreshLayout mSmartRefreshLayout = findViewById(R.id.mSmartRefreshLayout);
        smartRefreshHelper = new SmartRefreshHelper();
        smartRefreshHelper.inject(new SmartRefreshLayoutRecyclerView(mRecyclerView, mSmartRefreshLayout));
        smartRefreshHelper.setLayoutManager(new LinearLayoutManager(this));
        smartRefreshHelper.setRefreshEnable(true);
        smartRefreshHelper.setLoadMoreEnable(true);
        smartRefreshHelper.setOnRefreshListener(this);
        smartRefreshHelper.setOnLoadMoreListener(this);
    }

    protected abstract void initData(Bundle savedInstanceState);


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onFooterClick() {

    }

    public class ListAdapter extends BaseListAdapter {

        @Override
        protected int getItemViewCount() {
            return modules.size();
        }

        @Override
        public int getItemViewTypeByPosition(int position) {
            return BaseSmartRefreshLayoutListActivity.this.getItemViewTypeByPosition(position);
        }

        @NonNull
        @Override
        protected BaseViewHolder onCreateBaseViewHolder(@NotNull ViewGroup parent, int viewType) {
            return BaseSmartRefreshLayoutListActivity.this.onCreateBaseViewHolder(parent, viewType);
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateFooterHolder(@NotNull ViewGroup parent) {
            return BaseSmartRefreshLayoutListActivity.this.onCreateFooterHolder(parent);
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateHeaderHolder(@NotNull ViewGroup parent) {
            return BaseSmartRefreshLayoutListActivity.this.onCreateHeaderHolder(parent);
        }
    }

    protected int getItemViewTypeByPosition(int position) {
        return 0;
    }

    @Nullable
    protected BaseViewHolder onCreateFooterHolder(ViewGroup parent) {
        return null;
    }

    @Nullable
    protected BaseViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return null;
    }

    protected abstract BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType);
}