package com.android.widget.adapter.holder;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * created by jiangshide on 2019-10-18.
 * email:18311271399@163.com
 */
public class ViewHolder extends RecyclerView.ViewHolder {

  //控件收集起来作为缓存
  private SparseArray<View> mViews;//View控件集合
  //提供当前条目的view
  private View mConvertView;

  public ViewHolder(@NonNull View itemView) {
    super(itemView);
    mConvertView = itemView;
    mViews = new SparseArray<>();
  }

  //对外提供api,用来创建ViewHolder对象
  public static ViewHolder createViewHolder(Context context, ViewGroup parent, int layoutId) {
    View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
    return new ViewHolder(itemView);
  }

  //通过viewId获取控件
  public <T extends View> T getView(int viewId) {
    View view = mViews.get(viewId);
    //key:R.id.xxx value:TextView
    if (view == null) {
      view = mConvertView.findViewById(viewId);
      mViews.put(viewId, view);
    }
    return (T) view;
  }

  public View getConvertView(){
    return mConvertView;
  }
}
