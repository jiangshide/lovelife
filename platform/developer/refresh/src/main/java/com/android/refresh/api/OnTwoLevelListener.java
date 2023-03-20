package com.android.refresh.api;

import androidx.annotation.NonNull;

/**
 * created by jiangshide on 2016-07-24.
 * email:18311271399@163.com
 */
public interface OnTwoLevelListener {
    /**
     * 二级刷新触发
     *
     * @param refreshLayout 刷新布局
     * @return true 将会展开二楼状态 false 关闭刷新
     */
    boolean onTwoLevel(@NonNull RefreshLayout refreshLayout);
}