package com.qw.recyclerview.sample.loading;

import android.view.View;

import androidx.annotation.NonNull;


/**
 * 加载接口
 */
public interface ILoading {

    /**
     * 更新ui
     *
     * @param state ui状态
     */
    void notifyDataChanged(State state);

    void setLoadingView(@NonNull View view);

    void setEmptyView(@NonNull View view);

    void setErrorView(@NonNull View view);

    void setLoadingView(int layoutId);

    void setEmptyView(int layoutId);

    void setErrorView(int layoutId);

    void setContentView(View view);

    void setOnRetryListener(OnRetryListener listener);
}