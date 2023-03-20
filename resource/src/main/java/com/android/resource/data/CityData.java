package com.android.resource.data;

import java.io.Serializable;
import java.util.List;

/**
 * created by jiangshide on 2019-10-09.
 * email:18311271399@163.com
 */
public class CityData implements Serializable{
  public String name;
  public List<CityChildData> cities;

  public class CityChildData implements Serializable {
    public String citycode;
    public String name;
  }
}
