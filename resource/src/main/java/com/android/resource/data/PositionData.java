package com.android.resource.data;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.android.location.ZdLocation;
import com.android.utils.AppUtil;
import com.android.utils.DateUtil;
import com.google.gson.Gson;

/**
 * created by jiangshide on 2020/3/23.
 * email:18311271399@163.com
 */
public class PositionData implements AMapLocationListener {
  public Long uid;
  public Long fromId;
  public int event;
  public String err;
  public String ip;
  public Double latitude;
  public Double longitude;
  public String locationType;
  public String accuracy;
  public String provider;
  public String speed;
  public String bearing;
  public String satellites;
  public String country;
  public String province;
  public String city;
  public String district;
  public String cityCode;
  public String adCode;
  public String address;
  public String poiName;
  public String networkType;
  public String gpsStatus;
  public String gpsSatellites;
  public String timeZone;

  public PositionData() {
    ZdLocation.getInstance().startLocation(true,this);
  }

  public String getGson(){
    return new Gson().toJson(this);
  }

  @Override public void onLocationChanged(AMapLocation location) {
    ip = AppUtil.getIp();
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    locationType = location.getLocationType()+"";
    accuracy = location.getAccuracy() + "";
    provider = location.getProvider();
    speed = location.getSpeed() + "";
    bearing = location.getBearing() + "";
    satellites = location.getSatellites()+"";
    country = location.getCountry();
    province = location.getProvince();
    city = location.getCity();
    district = location.getDistrict();
    cityCode = location.getCityCode();
    adCode = location.getAdCode();
    address = location.getAddress();
    poiName = location.getPoiName();
    networkType = location.getLocationQualityReport().getNetworkType();
    gpsStatus = location.getLocationQualityReport().getGPSStatus()+"";
    gpsSatellites = location.getLocationQualityReport().getGPSSatellites()+"";
    timeZone = DateUtil.getGmtTimeZone();
  }
}
