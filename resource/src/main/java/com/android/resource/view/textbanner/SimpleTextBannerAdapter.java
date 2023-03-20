package com.android.resource.view.textbanner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import com.android.resource.vm.channel.data.Word;
import java.util.List;
import java.util.Random;

/**
 * 简单的数据适配器（TextView）
 */
public class SimpleTextBannerAdapter extends BaseAdapter {

  private OnItemClickListener mItemClickListener;
  private int index;

  @LayoutRes
  private int mLayoutResId;

  /**
   * 简单的TextBanner适配器（只含有一个TextView）
   *
   * @param context 上下文
   * @param layoutResId 布局资源ID
   * @param data 字符串列表数据源
   */
  public SimpleTextBannerAdapter(Context context, @LayoutRes int layoutResId, List<Word> data) {
    super(context, data);
    this.mLayoutResId = layoutResId;
  }

  @Override
  public View onCreateView(@NonNull ViewGroup parent) {
    return mInflater.inflate(mLayoutResId, parent, false);
  }

  @Override
  public void onBindViewData(@NonNull View convertView, int position) {
    this.index = position;
    TextView textView = ((TextView) convertView);
    Word data = (Word) mData.get(position);
    textView.setText(data.getName());
    textView.setTextColor(randomColor());
    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mItemClickListener != null) {
          mItemClickListener.onItemClick(data, position);
        }
      }
    });
  }

  public Word getData() {
    return (Word) mData.get(index);
  }

  public int randomColor() {
    Random random = new Random();
    int ranColor = 0xff000000 | random.nextInt(0x00ffffff);
    return ranColor;
  }

  /**
   * 设置Item的点击事件
   */
  public void setItemClickListener(OnItemClickListener listener) {
    this.mItemClickListener = listener;
  }

  /**
   * Item的点击事件监听
   */
  public interface OnItemClickListener {
    /**
     * Item的点击事件
     *
     * @param position 位置
     */
    void onItemClick(Word data, int position);
  }
}