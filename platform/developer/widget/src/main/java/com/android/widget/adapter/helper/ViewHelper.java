package com.android.widget.adapter.helper;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.utils.LogUtil;
import com.android.widget.adapter.ZdAdapter;
import com.android.widget.adapter.listener.IViewCreate;
import com.android.widget.adapter.listener.OnViewPagerListener;
import com.android.widget.adapter.manager.ViewPagerLayoutManager;
import java.util.Collections;
import java.util.List;

/**
 * created by jiangshide on 2019-10-18.
 * email:18311271399@163.com
 */
public class ViewHelper<T> {

  private Context context;
  private RecyclerView recyclerView;
  private ZdAdapter<T> adapter;
  private int startPageNumber = 1;
  private int currentPageNum;

  public ViewHelper(Builder<T> builder) {
    if (builder.create != null) {
      this.recyclerView = builder.create.createRecyclerView();
      this.adapter = builder.create.createRecycleViewAdapter();
      this.context = builder.create.context();
      this.currentPageNum = this.startPageNumber;
      init(builder);
    }
  }

  private void init(Builder<T> builder) {
    //RecycleView初始化
    if (recyclerView == null || context == null) return;

    if (builder.listener != null) {
      ViewPagerLayoutManager viewPagerLayoutManager = new ViewPagerLayoutManager(context,
          OrientationHelper.HORIZONTAL);
      viewPagerLayoutManager.setOnViewPagerListener(builder.listener);
      recyclerView.setLayoutManager(viewPagerLayoutManager);
    } else {
      recyclerView.setLayoutManager(builder.isGridView ? new GridLayoutManager(context
          , 3) : new LinearLayoutManager(context));
    }
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    if (!builder.isTouchDrag) return;
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
      @Override public int getMovementFlags(@NonNull RecyclerView recyclerView,
          @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags;
        int swipeFlags;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
          dragFlags = ItemTouchHelper.UP
              | ItemTouchHelper.DOWN
              | ItemTouchHelper.LEFT
              | ItemTouchHelper.RIGHT;
          swipeFlags = 0;
        } else {
          dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
          swipeFlags = 0;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
      }

      @Override public boolean onMove(@NonNull RecyclerView recyclerView,
          @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
          for (int i = fromPosition; i < fromPosition; i++) {
            Collections.swap(adapter.getDatas(), i, i + 1);
          }
        } else {
          for (int i = fromPosition; i > toPosition; i--) {
            Collections.swap(adapter.getDatas(), i, i - 1);
          }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
      }

      @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
      }
    });
    itemTouchHelper.attachToRecyclerView(recyclerView);
  }

  public void notifyAdapterDataSetChanged(List<T> datas) {
    if (adapter == null || recyclerView == null) return;
    if (currentPageNum == startPageNumber) {
      adapter.update(datas);
    } else {
      adapter.add(datas);
    }
    recyclerView.setAdapter(adapter);
  }

  public static class Builder<T> {
    private IViewCreate<T> create;//初始化接口
    private boolean isGridView;
    private boolean isTouchDrag;
    private OnViewPagerListener listener;

    public Builder(IViewCreate<T> create) {
      this.create = create;
    }

    public Builder setGridView(boolean isGridView) {
      this.isGridView = isGridView;
      return this;
    }

    public Builder setTouchDrag(boolean isTouchDrag) {
      this.isTouchDrag = isTouchDrag;
      return this;
    }

    public Builder setListener(OnViewPagerListener listener) {
      this.listener = listener;
      return this;
    }

    public ViewHelper build() {
      return new ViewHelper<>(this);
    }
  }
}
