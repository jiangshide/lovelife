package com.android.resource.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.img.Img;
import com.android.resource.R;
import com.android.resource.vm.publish.data.File;
import com.android.utils.LogUtil;
import com.android.utils.ScreenUtil;
import com.android.widget.ZdToast;

import java.util.ArrayList;
import java.util.List;

/**
 * created by jiangshide on 2020/3/17.
 * email:18311271399@163.com
 */
@Keep
public class ImgsView extends FrameLayout {

  private ImageView imgOne;
  private LinearLayout imgTwoL;
  private ImageView imgTwoOne;
  private ImageView imgTwoTwo;
  private LinearLayout imgThreeL;
  private ImageView imgThreeOne;
  private ImageView imgThreeTwo;
  private ImageView imgThreeThree;
  private LinearLayout imgFourL;
  private ImageView imgFourOne;
  private ImageView imgFourTwo;
  private ImageView imgFourThree;
  private ImageView imgFourFour;
  private TextView imgFourFourNum;
  private int imageWidth;

  private OnImgListener mOnImgListener;

  public ImgsView(@NonNull Context context) {
    super(context);
    init();
  }

  public ImgsView(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ImgsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    imageWidth = ScreenUtil.getScreenWidth(getContext());
    View view = LayoutInflater.from(getContext()).inflate(R.layout.imgs_view, null);
    imgOne = view.findViewById(R.id.imgOne);
    imgTwoL = view.findViewById(R.id.imgTwoL);
    imgTwoOne = view.findViewById(R.id.imgTwoOne);
    imgTwoTwo = view.findViewById(R.id.imgTwoTwo);
    imgThreeL = view.findViewById(R.id.imgThreeL);
    imgThreeOne = view.findViewById(R.id.imgThreeOne);
    imgThreeTwo = view.findViewById(R.id.imgThreeTwo);
    imgThreeThree = view.findViewById(R.id.imgThreeThree);
    imgFourL = view.findViewById(R.id.imgFourL);
    imgFourOne = view.findViewById(R.id.imgFourOne);
    imgFourTwo = view.findViewById(R.id.imgFourTwo);
    imgFourThree = view.findViewById(R.id.imgFourThree);
    imgFourFour = view.findViewById(R.id.imgFourFour);
    imgFourFourNum = view.findViewById(R.id.imgFourFourNum);
    addView(view);
  }

  public void setData(List<File> data) {
    imgOne.setVisibility(GONE);
    imgTwoL.setVisibility(GONE);
    imgThreeL.setVisibility(GONE);
    imgFourL.setVisibility(GONE);
    imgFourFourNum.setVisibility(GONE);
    if (data == null) return;
    int size = data.size();
    if (size == 0) return;
    if (size == 1) {
      FrameLayout.LayoutParams layoutParams =
          new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
              ScreenUtil.getScreenHeight(getContext())/3*2);
      imgOne.setLayoutParams(layoutParams);
      showImg(0, data.get(0).getUrl(), imgOne,data);
      imgOne.setVisibility(VISIBLE);
    } else if (size == 2) {
      FrameLayout.LayoutParams layoutParams =
          new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
              ScreenUtil.getScreenHeight(getContext()) / 2 - 200);
      imgTwoL.setLayoutParams(layoutParams);
      showImg(0, data.get(0).getUrl(), imgTwoOne,data);
      showImg(1, data.get(1).getUrl(), imgTwoTwo,data);
      imgTwoL.setVisibility(VISIBLE);
    } else if (size == 3) {
      showImg(0, data.get(0).getUrl(), imgThreeOne,data);
      showImg(1, data.get(1).getUrl(), imgThreeTwo,data);
      showImg(2, data.get(2).getUrl(), imgThreeThree,data);
      imgThreeL.setVisibility(VISIBLE);
    } else {
      showImg(0, data.get(0).getUrl(), imgFourOne,data);
      showImg(1, data.get(1).getUrl(), imgFourTwo,data);
      showImg(2, data.get(2).getUrl(), imgFourThree,data);
      showImg(3, data.get(3).getUrl(), imgFourFour,data);
      imgFourL.setVisibility(VISIBLE);
      int lastNum = size - 4;
      if (lastNum > 0) {
        imgFourFourNum.setText(lastNum + "+");
        imgFourFourNum.setVisibility(VISIBLE);
      }
    }
  }

  private void showImg(int index, String url, ImageView imgView,List<File> data) {
    Img.loadImage(Img.thumbAliOssUrl(url, imageWidth), imgView);
    imgView.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (mOnImgListener != null && data != null) {
          ArrayList<String> urls = new ArrayList<>();
          for(File file:data){
            urls.add(file.getUrl());
          }
          mOnImgListener.onClick(index, imgView,urls);
        }
      }
    });
  }

  public void setOnClickListener(OnImgListener listener) {
    this.mOnImgListener = listener;
  }

  public interface OnImgListener {
    void onClick(int index, View view,List<String> urls);
  }
}
