package com.android.resource.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.resource.R;
import com.android.resource.data.CityData;
import com.android.utils.AppUtil;
import com.android.utils.JsonUtil;
import com.android.widget.ZdDialog;
import com.android.widget.ZdTopView;
import com.android.widget.adapter.ZdListAdapter;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/**
 * created by jiangshide on 2019-10-09.
 * email:18311271399@163.com
 */
public class CityDialog {
  private Context mContext;
  private ZdDialog provincedialogView;
  private ZdDialog cityDialogView;
  private OnCityListener mOnCityListener;
  private static List<CityData> cityDataList;
  private List<CityData.CityChildData> cityChildDataList;
  private int mIndex;

  public CityDialog(Context context) {
    this.mContext = context;
    init(context);
  }

  private static void init(Context context) {
    cityDataList = getFileToCitys(context, "citys_result.json");
  }

  public static ArrayList<CityData> getFileToCitys(Context context, String fileName) {
    return getCitys(JsonUtil.getJson(context, fileName));
  }

  public static ArrayList<CityData> getCitys(String result) {
    ArrayList<CityData> dataArrayList = new ArrayList<>();
    if (TextUtils.isEmpty(result)) return dataArrayList;
    try {
      JSONArray data = new JSONArray(result);
      Gson gson = new Gson();
      for (int i = 0; i < data.length(); i++) {
        CityData entity = gson.fromJson(data.optJSONObject(i).toString(), CityData.class);
        dataArrayList.add(entity);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dataArrayList;
  }

  public ZdDialog create() {
    cancelProvince();
    return provincedialogView =
        new ZdDialog(mContext, R.style.translucent, R.layout.dialog_citylist,
            new ZdDialog.DialogViewListener() {
              @Override
              public void onView(View view) {

               ZdTopView topView =  view.findViewById(R.id.topView);
                topView.setTitle("选择所在城市").setOnLeftClick(
                    new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                        cancelProvince();
                      }
                    });
                ListView dialogCityListView = view.findViewById(R.id.dialogCityListView);
                dialogCityListView.setAdapter(new ZdListAdapter<CityData>(mContext, cityDataList,
                    R.layout.dialog_citylist_item) {
                  @Override
                  protected void convertView(int position, View item, CityData cityData) {
                    TextView dialogCityItemName = get(item, R.id.dialogCityItemName);
                    dialogCityItemName.setText(cityData.name);
                    boolean isV = ((null != cityData.cities) && cityData.cities.size() > 1);
                    get(item, R.id.dialogCityItemArrow).setVisibility(isV ? View.VISIBLE : View.GONE);
                  }
                });
                dialogCityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    List<CityData.CityChildData> childDataList = cityDataList.get(position).cities;
                    boolean isV = ((null != childDataList) && childDataList.size() > 1);
                    if (isV) {
                      create(mContext, childDataList).show();
                    } else {
                      if (mOnCityListener != null) {
                        cancelProvince();
                        mOnCityListener.onResult(cityDataList.get(position).name,
                            cityDataList.get(position).cities.get(0).name,
                            cityDataList.get(position).cities.get(0).citycode);
                      }
                    }
                    mIndex = position;
                  }
                });
              }
            }).setAnim(R.style.bottomAnim).setFull(true);
  }

  private void cancelProvince() {
    if (null != provincedialogView) {
      provincedialogView.dismiss();
      provincedialogView = null;
    }
  }

  private void cancleCity() {
    if (null != cityDialogView) {
      cityDialogView.dismiss();
      cityDialogView = null;
    }
  }

  public ZdDialog create(Context context, final List<CityData.CityChildData> cityChildDataList) {
    this.mContext = context;
    this.cityChildDataList = cityChildDataList;
    cancleCity();
    return cityDialogView = new ZdDialog(context, R.style.translucent, R.layout.dialog_citylist,
        new ZdDialog.DialogViewListener() {
          @Override
          public void onView(View view) {
            ZdTopView topView =  view.findViewById(R.id.topView);
            topView.setTitle("选择所在城市").setOnLeftClick(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    cancleCity();
                  }
                });
            ListView dialogCityListView = view.findViewById(R.id.dialogCityListView);
            dialogCityListView.setAdapter(
                new ZdListAdapter<CityData.CityChildData>(mContext, cityChildDataList,
                    R.layout.dialog_citylist_item) {
                  @Override
                  protected void convertView(int position, View item,
                      CityData.CityChildData cityChildData) {
                    TextView dialogCityItemName = get(item, R.id.dialogCityItemName);
                    dialogCityItemName.setText(cityChildData.name);
                    get(item, R.id.dialogCityItemArrow).setVisibility(View.GONE);
                  }
                });
            dialogCityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityData.CityChildData cityChildData = cityChildDataList.get(position);
                if (mOnCityListener != null) {
                  cancelProvince();
                  cancleCity();
                  mOnCityListener.onResult(cityDataList.get(mIndex).name, cityChildData.name,
                      cityChildData.citycode);
                }
              }
            });
          }
        }).setAnim(R.style.bottomAnim).setFull(true);
  }

  public CityDialog setCityListener(OnCityListener listener) {
    this.mOnCityListener = listener;
    return this;
  }

  public interface OnCityListener {
    public void onResult(String province, String city, String cityCode);
  }

  public static String getCity(String cityCode) {
    if (TextUtils.isEmpty(cityCode)) return "";
    if (cityDataList == null) {
      init(AppUtil.getApplicationContext());
    }
    String city = "";
    if (null != cityDataList) {
      for (CityData cityData : cityDataList) {
        for (CityData.CityChildData cityChildData : cityData.cities) {
          if (cityCode.equals(cityChildData.citycode)) {
            city = cityData.cities.size() > 1 ? cityData.name.equals("其他国家") ? cityChildData.name
                : cityData.name + " " + cityChildData.name : cityChildData.name;
            break;
          }
        }
      }
    }
    return city;
  }
}
