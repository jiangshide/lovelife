package com.android.resource.view.tag;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * created by jiangshide on 2020/4/3.
 * email:18311271399@163.com
 */
public class NOPTagsAdapter extends TagsAdapter{
  @Override public int getCount() {
    return 0;
  }

  @Override public View getView(Context context, int position, ViewGroup parent) {
    return null;
  }

  @Override public Object getItem(int position) {
    return null;
  }

  @Override public int getPopularity(int position) {
    return 0;
  }

  @Override public void onThemeColorChanged(View view, int themeColor, float alpha) {

  }
}
