package com.qw.recyclerview.sample.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.qw.recyclerview.sample.R;


/**
 * LoadingView解决了请求网络数据时ui显示的三种状态
 * 分别为加载中，加载失败，无数据
 * email: qinwei_it@163.com
 *
 * @author qinwei create by 2015/10/28
 * update time 2020/12/3
 */
public class LoadingView extends FrameLayout implements ILoading {

    private LinearLayout empty;
    private LinearLayout error;
    private LinearLayout loading;
    private State state;
    /**
     * 由外部注入
     */
    private View contentView;

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadingView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_refresh_view, this);
        empty = (LinearLayout) findViewById(R.id.empty);
        loading = (LinearLayout) findViewById(R.id.loading);
        error = (LinearLayout) findViewById(R.id.error);
        notifyDataChanged(State.done);
    }

    public void notifyDataChanged(State state) {
        this.state = state;
        switch (state) {
            case ing:
                empty.setVisibility(View.GONE);
                error.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                setVisibility(View.VISIBLE);
                if (contentView != null) {
                    contentView.setVisibility(View.GONE);
                }
                break;
            case empty:
                loading.setVisibility(View.GONE);
                error.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                setVisibility(View.VISIBLE);
                if (contentView != null) {
                    contentView.setVisibility(View.GONE);
                }
                break;
            case error:
                loading.setVisibility(View.GONE);
                empty.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
                setVisibility(View.VISIBLE);
                if (contentView != null) {
                    contentView.setVisibility(View.GONE);
                }
                break;
            case done:
                setVisibility(View.GONE);
                if (contentView != null) {
                    contentView.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    public void setEmptyView(View view) {
        empty.removeAllViews();
        empty.addView(view, getLayoutParams());
    }

    public void setEmptyView(int layoutId) {
        View view = LayoutInflater.from(getContext()).inflate(layoutId, null);
        setEmptyView(view);
    }

    @Override
    public void setErrorView(int layoutId) {
        View view = LayoutInflater.from(getContext()).inflate(layoutId, null);
        setErrorView(view);
    }

    @Override
    public void setLoadingView(View view) {
        loading.removeAllViews();
        loading.addView(view, getLayoutParams());
    }

    @Override
    public void setErrorView(View view) {
        loading.removeAllViews();
        loading.addView(view, getLayoutParams());
    }

    @Override
    public void setLoadingView(int layoutId) {
        View view = LayoutInflater.from(getContext()).inflate(layoutId, null);
        setLoadingView(view);
    }

    @Override
    public void setOnRetryListener(OnRetryListener listener) {
        if (listener == null) {
            return;
        }
        View retry = findViewById(R.id.retry);
        if (retry != null) {
            retry.setOnClickListener(v -> {
                if (state == State.error) {
                    listener.onRetry();
                }
            });
        } else {
            setOnClickListener(v -> {
                if (state == State.error) {
                    listener.onRetry();
                }
            });
        }
    }

    @Override
    public void setContentView(@NonNull View view) {
        this.contentView = view;
    }
}