package com.android.widget.adapter;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.widget.adapter.holder.ViewHolder;
import com.android.widget.adapter.listener.IViewItem;
import com.android.widget.adapter.listener.ItemListener;
import com.android.widget.adapter.manager.ViewItemManager;
import java.util.ArrayList;
import java.util.List;

/**
 * created by jiangshide on 2019-10-18.
 * email:18311271399@163.com
 */
public class ZdAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
  private ViewItemManager<T> itemStyle;//item类型管理
  private ItemListener<T> itemListener;//item点击事件监听
  private List<T> datas;//数据源

  public void add(List<T> list) {
    this.add(-1, list);
  }

  public void add(int index, List<T> list) {
    this.add(index, list, false);
  }

  public void add(List<T> list, boolean isRefresh) {
    this.add(-1, list, isRefresh);
  }

  public void add(int index, List<T> list, boolean isRefresh) {
    if (list == null || list.size() == 0) return;
    if (datas == null) {
      datas = new ArrayList<>();
    }
    if (isRefresh) {
      datas.clear();
    }
    if (index != -1) {
      datas.addAll(index, list);
    } else {
      datas.addAll(list);
    }
    notifyDataSetChanged();
  }

  public void add(T t) {
    if (datas == null) {
      datas = new ArrayList<>();
    }
    datas.add(t);
    notifyDataSetChanged();
  }

  public void update(List<T> datas) {
    if (datas == null) return;
    this.datas = datas;
    notifyDataSetChanged();
  }

  public void update(T t) {
    if (datas == null || t == null) return;
    int size = datas.size() - 1;
    for (int i = 0; i < size; i++) {
      if (datas.get(i).equals(t)) {
        datas.set(i, t);
      }
    }
    notifyDataSetChanged();
  }

  public void remove(int position) {
    if (datas == null || datas.size() - 1 < position) return;
    datas.remove(position);
    notifyDataSetChanged();
  }

  public void remove(T t) {
    if (datas == null) return;
    datas.remove(t);
    notifyDataSetChanged();
  }

  public void top(T t) {
    if (datas == null) return;
    datas.remove(t);
    datas.add(0, t);
    notifyDataSetChanged();
  }

  public List<T> getDatas() {
    return this.datas;
  }

  public int getCount() {
    if (this.datas == null) return 0;
    return this.datas.size();
  }

  public T getData(int position) {
    return this.datas.get(position);
  }

  public void clear() {
    if(this.datas == null)return;
    this.datas.clear();
    notifyDataSetChanged();
  }

  //单一布局
  public ZdAdapter(List<T> datas) {
    if (datas == null) this.datas = new ArrayList<>();
    this.datas = datas;
    itemStyle = new ViewItemManager<>();
  }

  //嵌套(多样式布局)
  public ZdAdapter(List<T> datas, IViewItem<T> item) {
    if (datas == null) this.datas = new ArrayList<>();
    this.datas = datas;
    itemStyle = new ViewItemManager<>();

    //将一种新的item类型加入到多样式集合中
    addItemStyle(item);
  }

  public ZdAdapter<T> addItemStyle(IViewItem<T> item) {
    itemStyle.addStyles(item);
    return this;
  }

  //是否有多样式RViewItem
  private boolean hasMultiStyle() {
    return itemStyle.getItemViewStylesCount() > 0;
  }

  @Override public int getItemViewType(int position) {
    //如果有多种样式，需要判断分开加载
    if (hasMultiStyle()) return itemStyle.getItemViewType(datas.get(position), position);
    return super.getItemViewType(position);
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    //根据返回的ViewType,从集合中得到RViewItem
    IViewItem item = itemStyle.getRViewItem(viewType);
    //获取对象的属性
    int layoutId = item.getItemLayout();
    //创建ViewHolder对象
    ViewHolder holder = ViewHolder.createViewHolder(parent.getContext(), parent, layoutId);
    //拦截点击事件
    if (item.openClick()) setListener(holder);
    return holder;
  }

  private void setListener(ViewHolder holder) {
    holder.getConvertView().setOnClickListener(v -> {
      if (itemListener != null) {
        int position = holder.getAdapterPosition();
        itemListener.onItemClick(v, datas.get(position), position);
      }
    });
    holder.getConvertView().setOnLongClickListener(v -> {
      if (itemListener != null) {
        int position = holder.getAdapterPosition();
        itemListener.onItemLongClick(v, datas.get(position), position);
      }
      return true;
    });
  }

  @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    convert(holder, datas.get(position));
  }

  private void convert(ViewHolder holder, T entity) {
    itemStyle.convert(holder, entity, holder.getAdapterPosition());
  }

  @Override public int getItemCount() {
    return datas == null ? 0 : datas.size();
  }

  public ZdAdapter<T> setItemListener(ItemListener<T> itemListener) {
    this.itemListener = itemListener;
    return this;
  }
}
