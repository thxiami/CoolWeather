package com.coolweathertest.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by liyiwei on 2018/2/8.
 */

public class City extends DataSupport{
    private int id;
    private int provinceId;
    private String cityName;
    private int cityId;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
