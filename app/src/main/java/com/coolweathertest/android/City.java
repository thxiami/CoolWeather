package com.coolweathertest.android;

import org.litepal.crud.DataSupport;

/**
 * Created by liyiwei on 2018/2/8.
 */

public class City extends DataSupport{
    private int id;
    private int provinceId;
    private String cityName;
    private int cityCode;

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

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }
}
