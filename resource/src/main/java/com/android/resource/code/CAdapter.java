package com.android.resource.code;

import android.app.Activity;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.android.event.ZdEvent;
import com.android.resource.R;
import java.util.List;

import static com.android.resource.ResourceKt.FINISH_USERCODE;

/**
 * created by jiangshide on 2020/3/22.
 * email:18311271399@163.com
 */
public class CAdapter extends PyAdapter<RecyclerView.ViewHolder>{

  private Activity mActivity;

  public CAdapter(List<? extends PyEntity> entities,Activity activity) {
    super(entities);
    this.mActivity = activity;
  }

  @Override
  public RecyclerView.ViewHolder onCreateLetterHolder(ViewGroup parent, int viewType) {
    return new LetterHolder(mActivity.getLayoutInflater().inflate(R.layout.item_letter, parent, false));
  }

  @Override
  public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
    return new VH(mActivity.getLayoutInflater().inflate(R.layout.item_country_large_padding, parent, false));
  }

  @Override
  public void onBindHolder(RecyclerView.ViewHolder holder, PyEntity entity, int position) {
    VH vh = (VH)holder;
    final Country country = (Country)entity;
    vh.ivFlag.setImageResource(country.flag);
    vh.tvName.setText(country.name);
    vh.tvCode.setText("+" + country.code);
    holder.itemView.setOnClickListener(v -> {
      ZdEvent.Companion.get().with(FINISH_USERCODE).post(country);
      mActivity.finish();
    });
  }

  @Override
  public void onBindLetterHolder(RecyclerView.ViewHolder holder, LetterEntity entity, int position) {
    ((LetterHolder)holder).textView.setText(entity.letter.toUpperCase());
  }
}
