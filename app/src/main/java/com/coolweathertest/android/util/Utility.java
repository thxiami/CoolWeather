package com.coolweathertest.android.util;

import android.text.TextUtils;

import com.coolweathertest.android.db.City;
import com.coolweathertest.android.db.County;
import com.coolweathertest.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liyiwei on 2018/2/8.
 */

public class Utility {
    /*
    * 解析和处理服务器返回的 省级 数据
    * */
    private static final String TAG = "Utility";
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvince = new JSONArray(response);

                for (int i=0; i < allProvince.length(); i++) {
                    JSONObject provinceObject = allProvince.getJSONObject(i);

                    Province province = new Province();
                    province.setProvinceId(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));
                    province.save();

//                    LogUtil.d(TAG, "handleProvinceResponse: id:" + province.getId());
//                    LogUtil.d(TAG, "handleProvinceResponse: name:" + province.getProvinceName());
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    * 解析和处理服务器返回的 市级 数据
    * */
    public static boolean handleCityResponse(String response, Integer provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);

                for (int i=0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);

                    City city = new City();
                    city.setCityId(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();

//                    LogUtil.d(TAG, "handleCityResponse: id" + city.getId());
//                    LogUtil.d(TAG, "handleCityResponse: name" + city.getCityName());
//                    LogUtil.d(TAG, "handleCityResponse: provinceId" + city.getProvinceId());
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    * 解析和处理服务器返回的  县级 数据
    * */
    public static boolean handleCountyResponse(String response, Integer cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);

                for (int i=0; i < allCounties.length(); i++) {
                    JSONObject cityObject = allCounties.getJSONObject(i);

                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyId(cityObject.getInt("id"));
                    county.setCountyName(cityObject.getString("name"));
                    county.setWeatherCode(cityObject.getString("weather_id"));
                    county.save();

//                    LogUtil.d(TAG, "handleCountyResponse: id" + county.getCountyId());
//                    LogUtil.d(TAG, "handleCountyResponse: countyname" + county.getCountyName());
//                    LogUtil.d(TAG, "handleCountyResponse: cityId" + county.getCityId());
//                    LogUtil.d(TAG, "handleCountyResponse: weatherId" + county.getWeatherId());

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


}
