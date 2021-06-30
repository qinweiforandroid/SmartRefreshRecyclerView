package com.qw.recyclerview.swiperefresh.footer;

import com.qw.recyclerview.swiperefresh.State;

/**
 * 底部状态切换接口  你的自定义状态view需要impl此接口 框架会帮你call onFooterChanged 改变底部加载更多的ui
 * Created by qinwei on 2016/9/25 19:07
 * email:qinwei_it@163.com
 */

public interface IFooter {

    /**
     * 状态发生改变
     *
     * @param state view状态
     */
    void onFooterChanged(State state);
}
