package com.coolweathertest.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by liyiwei on 2018/2/8.
 */

public class Province extends DataSupport {
    private int id;
    private String provinceName;
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
