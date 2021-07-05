package com.qw.recyclerview.sample;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.qw.recyclerview.core.adapter.BaseListAdapter;
import com.qw.recyclerview.core.adapter.BaseViewHolder;
import com.qw.recyclerview.core.OnLoadMoreListener;
import com.qw.recyclerview.core.OnRefreshListener;
import com.qw.recyclerview.core.SmartRefreshHelper;
import com.qw.recyclerview.sample.databinding.SmartRefreshLayoutActivityBinding;
import com.qw.recyclerview.smartrefreshlayout.SmartRefreshLayout1RecyclerView;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by qinwei on 2021/7/1 20:38
 */
public class SmartRefreshLayoutActivity extends AppCompatActivity {
    static {
//        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(android.R.color.black, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    private SmartRefreshLayoutActivityBinding bind;

    private SmartRefreshHelper smartRefreshHelper;

    private ListAdapter adapter;
    protected ArrayList<String> modules = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = SmartRefreshLayoutActivityBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        //1.配置RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListAdapter();
        mRecyclerView.setAdapter(adapter);

        //2.配置SmartRefreshLayout
        SmartRefreshLayout mSmartRefreshLayout = findViewById(R.id.mSmartRefreshLayout);


        //3.配置SmartRefreshHelper
        smartRefreshHelper = new SmartRefreshHelper();
        //SmartRefreshLayoutRecyclerView将mRecyclerView和mSmartRefreshLayout打包后，交给SmartRefreshHelper进行管理
        smartRefreshHelper.inject(new SmartRefreshLayout1RecyclerView(mRecyclerView, mSmartRefreshLayout));

        //设置下拉刷新可用
        smartRefreshHelper.setRefreshEnable(true);
        //设置加载更多可用
        smartRefreshHelper.setLoadMoreEnable(true);
        //设置下拉刷新监听
        smartRefreshHelper.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                smartRefreshHelper.getRecyclerView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        modules.clear();
                        for (int i = 0; i < 20; i++) {
                            modules.add("" + i);
                        }
                        adapter.notifyDataSetChanged();
                        smartRefreshHelper.finishRefresh(true);
                    }
                }, 1000);
            }
        });
        //设置加载更多监听
        smartRefreshHelper.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                smartRefreshHelper.getRecyclerView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int size = modules.size();
                        for (int i = size; i < size + 20; i++) {
                            modules.add("" + i);
                        }
                        if (modules.size() < 100) {
                            smartRefreshHelper.setLoadMore(true, false);
                        } else {
                            smartRefreshHelper.setLoadMore(true, true);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, 1000);
            }
        });
        smartRefreshHelper.autoRefresh();
    }

    class ListAdapter extends BaseListAdapter {

        @Override
        protected int getItemViewCount() {
            return modules.size();
        }

        @NotNull
        @Override
        protected BaseViewHolder onCreateBaseViewHolder(@NotNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(SmartRefreshLayoutActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false));
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

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recyclerview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_linearLayout) {
            smartRefreshHelper.setLayoutManager(getLinearLayoutManager());
        } else if (itemId == R.id.action_gridLayout) {
            smartRefreshHelper.setLayoutManager(getGridLayoutManager(2));
        } else if (itemId == R.id.action_staggeredGridLayout) {
            smartRefreshHelper.setLayoutManager(getStaggeredGridLayoutManager(2));
        }
        return super.onOptionsItemSelected(item);
    }


    public RecyclerView.LayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(this);
    }

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