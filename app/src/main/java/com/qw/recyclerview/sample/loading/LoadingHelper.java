package com.qw.recyclerview.sample.loading;

import android.view.View;


/**
 * Created by qinwei on 2021/7/2 16:41
 */
public class LoadingHelper implements ILoading {

    private ILoading loading;

    public void inject(ILoading loading) {
        this.loading = loading;
    }

    @Override
    public void notifyDataChanged(State state) {
        loading.notifyDataChanged(state);
    }

    @Override
    public void setEmptyView(View view) {
        loading.setEmptyView(view);
    }

    @Override
    public void setEmptyView(int layoutId) {
        loading.setEmptyView(layoutId);
    }

    @Override
    public void setErrorView(int layoutId) {
        loading.setErrorView(layoutId);
    }

    @Override
    public void setContentView(View view) {
        loading.setContentView(view);
    }

    @Override
    public void setLoadingView(View view) {
        loading.setLoadingView(view);
    }

    @Override
    public void setErrorView(View view) {
        loading.setErrorView(view);
    }

    @Override
    public void setLoadingView(int layoutId) {
        loading.setLoadingView(layoutId);
    }

    @Override
    public void setOnRetryListener(OnRetryListener listener) {
        loading.setOnRetryListener(listener);
    }
}