package com.qw.recyclerview.sample;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.qw.recyclerview.core.adapter.BaseViewHolder;
import com.qw.recyclerview.sample.core.BaseSmartRefreshLayoutListActivity;
import com.qw.recyclerview.sample.databinding.SmartRefreshLayoutActivityBinding;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;

public class SmartRefreshLayoutPlusActivity extends BaseSmartRefreshLayoutListActivity<String> {
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

    @Override
    protected void setContentView() {
        bind = SmartRefreshLayoutActivityBinding.inflate(getLayoutInflater());
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
        }, 2000);
    }

    @Override
    protected BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(SmartRefreshLayoutPlusActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false));
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