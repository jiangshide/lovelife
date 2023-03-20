package com.android.refresh.impl;

import android.annotation.SuppressLint;
import android.view.View;
import com.android.refresh.api.RefreshHeader;
import com.android.refresh.internal.InternalAbstract;

/**
 * created by jiangshide on 2016-07-24.
 * email:18311271399@163.com
 */
@SuppressLint("ViewConstructor")
public class RefreshHeaderWrapper extends InternalAbstract implements
    RefreshHeader/*, InvocationHandler*/ {

    public RefreshHeaderWrapper(View wrapper) {
        super(wrapper);
    }

}
