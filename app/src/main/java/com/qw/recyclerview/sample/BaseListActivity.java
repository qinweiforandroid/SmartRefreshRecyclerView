package com.qw.recyclerview.sample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qw.recyclerview.core.BaseViewHolder;
import com.qw.recyclerview.core.OnLoadMoreListener;
import com.qw.recyclerview.core.OnRefreshListener;
import com.qw.recyclerview.core.SmartRefreshHelper;
import com.qw.recyclerview.core.footer.FooterView;
import com.qw.recyclerview.core.footer.IFooter;
import com.qw.recyclerview.swiperefresh.BaseListAdapter;
import com.qw.recyclerview.swiperefresh.SwipeRefreshRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class BaseListActivity<T> extends AppCompatActivity implements FooterView.OnFooterViewListener, OnRefreshListener, OnLoadMoreListener {
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
        SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);
        smartRefreshHelper = new SmartRefreshHelper();
        smartRefreshHelper.inject(new SwipeRefreshRecyclerView(mRecyclerView, mSwipeRefreshLayout));
        smartRefreshHelper.setLayoutManager(new LinearLayoutManager(this));
        smartRefreshHelper.setRefreshEnable(true);
        smartRefreshHelper.setLoadMoreEnable(true);
        smartRefreshHelper.setOnRefreshListener(this);
        smartRefreshHelper.setOnLoadMoreListener(this);
        adapter = new ListAdapter();
        smartRefreshHelper.setAdapter(adapter);
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

        @NonNull
        @Override
        protected BaseViewHolder onCreateBaseViewHolder(@NotNull ViewGroup parent, int viewType) {
            return BaseListActivity.this.onCreateBaseViewHolder(parent, viewType);
        }

        @Override
        public int getItemViewTypeByPosition(int position) {
            return BaseListActivity.this.getItemViewTypeByPosition(position);
        }

        @NonNull
        @Override
        protected BaseViewHolder onCreateFooterHolder(@NotNull ViewGroup parent) {
            BaseViewHolder baseViewHolder = BaseListActivity.this.onCreateFooterHolder(parent);
            if (baseViewHolder == null) {
                FooterView footerView = new FooterView(BaseListActivity.this);
                footerView.setOnFooterViewListener(BaseListActivity.this);
                footerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                return new ListAdapter.FooterViewHolder(footerView);
            }
            return baseViewHolder;
        }

        @NonNull
        @Override
        protected BaseViewHolder onCreateHeaderHolder(@NotNull ViewGroup parent) {
            return BaseListActivity.this.onCreateHeaderHolder(parent);
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
                footer.onFooterChanged(adapter.getFooterState());
            }
        }
    }

    protected int getItemViewTypeByPosition(int position) {
        return 0;
    }

    protected BaseViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return null;
    }

    private BaseViewHolder onCreateFooterHolder(ViewGroup parent) {
        return null;
    }

    protected abstract BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType);

    /**
     * 得到GridLayoutManager
     *
     * @param spanCount 列数
     * @return
     */
    public GridLayoutManager getGridLayoutManager(int spanCount) {
        final GridLayoutManager manager = new GridLayoutManager(this, spanCount);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.isHeaderShow(position) || adapter.isFooterShow(position)) {
                    return manager.getSpanCount();
                }
                return 1;
            }
        });
        return manager;
    }

    /**
     * 得到StaggeredGridLayoutManager
     *
     * @param spanCount 列数
     * @return
     */
    public StaggeredGridLayoutManager getStaggeredGridLayoutManager(int spanCount) {
        return new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
    }
}