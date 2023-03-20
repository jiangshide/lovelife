package com.android.widget.adapter.manager;

import androidx.collection.SparseArrayCompat;
import com.android.widget.adapter.holder.ViewHolder;
import com.android.widget.adapter.listener.IViewItem;

/**
 * created by jiangshide on 2019-10-18.
 * email:18311271399@163.com
 */
public class ViewItemManager<T> {

  private SparseArrayCompat<IViewItem<T>> styles = new SparseArrayCompat<>();

  public void addStyles(IViewItem<T> item) {
    if (item != null) {
      styles.put(styles.size(), item);
    }
  }

  //获取所有item样式数量
  public int getItemViewStylesCount() {
    return styles.size();
  }

  //获取styles集合中的key
  public int getItemViewType(T entity, int position) {
    for (int i = styles.size() - 1; i >= 0; i--) {//避免开发者增删集合抛出异常
      IViewItem<T> item = styles.valueAt(i);
      //校验完成才返回viewType多样式
      item.getItemLayout();
      boolean isItem = item.isItemView(entity, position);
      if (isItem) {
        return styles.keyAt(i);
      }
    }
    return 0;
  }

  public IViewItem getRViewItem(int viewType) {
    return styles.get(viewType);
  }

  public void convert(ViewHolder holder, T entity, int position) {
    for (int i = 0; i < styles.size(); i++) {
      IViewItem<T> item = styles.valueAt(i);
      //检验完成才做数据绑定
      if (item.isItemView(entity, position)) {
        item.convert(holder, entity, position);
        return;
      }
    }
  }
}
