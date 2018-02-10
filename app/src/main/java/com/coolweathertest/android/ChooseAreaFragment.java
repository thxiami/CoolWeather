package com.coolweathertest.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweathertest.android.db.City;
import com.coolweathertest.android.db.County;
import com.coolweathertest.android.db.Province;
import com.coolweathertest.android.util.HttpUtil;
import com.coolweathertest.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by liyiwei on 2018/2/9.
 */

public class ChooseAreaFragment extends Fragment {
    private static final String TAG = "ChooseAreaFragment";

    private static final String apiUrl = "http://guolin.tech/api/china/";

    private final int LEVEL_PROVINCE = 0;

    private final int LEVEL_CITY = 1;

    private final int LEVEL_COUNTY = 2;

    private int currentLevel;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;

    private List<City> cityityList;

    private List<County> countyList;

    /*
    * 当前选中的省份 对应的对象
    * */
    private Province selectedProcince;

    private City selectedCity;

    private County seletedCounty;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces(); // 从这里开始 currentLevel才被初始化

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 这里 currentLevel 还未初始化
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProcince = provinceList.get(position);
                    queryCities(); // 这里面就包含了 数据获取 和 UI 操作
//                    currentLevel = LEVEL_CITY;
                    // currentLevel 的设置要放在上面的 queryCities 方法中
                    // 因为在 backButton 被点击时做两件事：
                    // 1，拉取显示上一层级的数据
                    // 2，修改 currentLevel 为上一层级
                    // currentLevel 的修改总是伴随着 queryCities 方法
                    // 所以把 currentLevel 的修改放在方法中比较好
                    // 这样可以：减少重复的代码，防止忘了修改 currentLevel

                } else if (currentLevel == LEVEL_CITY) {
                    Log.d(TAG, "onItemClick: currentLevel == LEVEL_CITY");
                    selectedCity = cityityList.get(position);
                    queryCounties();
//                    currentLevel = LEVEL_COUNTY;

                } else if (currentLevel == LEVEL_COUNTY) {
                    Log.d(TAG, "onItemClick: currentLevel == LEVEL_COUNTY");
                    seletedCounty = countyList.get(position);
//                    queryCounties();
//                    currentLevel = LEVEL_COUNTY;
//                    String weatherCode = seletedCounty.getWeatherId();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
    }

    /*
    * 查询中国所有的省，优先从数据库查询，如果没有查询到记录则从服务器查询
    * */
    private void queryProvinces() {
        /*
        * 优先从 数据库 查询
        * 再从 服务器 查询
        * */
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);

        provinceList = DataSupport.findAll(Province.class);

        if (provinceList.size() > 0) {
            // From db
            dataList.clear();
            Log.d(TAG, "queryProvinces: from db");
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            // From server
            queryFromServer(apiUrl, "province");
            Log.d(TAG, "queryProvince: query form web");
        }
    }

    /*
    * 查询选中的省对应所有的市，优先从数据库查询，如果没有查询到记录则从服务器查询
    * */
    private void queryCities() {
        titleText.setText(selectedProcince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        cityityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectedProcince.getId())).find(City.class);
        if (cityityList.size() > 0) {
            dataList.clear();
            for (City city : cityityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;

        } else {
            Integer provinceId = selectedProcince.getProvinceId();
            String url = apiUrl + provinceId.toString();
            queryFromServer(url, "city");
        }
    }

    /*
    * 查询选中的 市 对应所有的 县，优先从数据库查询，如果没有查询到记录则从服务器查询
    * */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        Log.d(TAG, "queryCounties: click: " + selectedCity.getCityName());
        //TODO SQLite中数据库的列名都是小写的？与Class中的变量名如何对应
        countyList = DataSupport.where("cityid = ?",
                String.valueOf(selectedCity.getId())).find(County.class);

        if (countyList.size() > 0 ) {
            Log.d(TAG, "queryCounties: from db");
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int selectedProvinceId = selectedProcince.getProvinceId();
            int selectedCityId = selectedCity.getCityId();
            String url = apiUrl + String.valueOf(selectedProvinceId) + "/" + String.valueOf(selectedCityId);
            queryFromServer(url, "county");
        }
    }

    /*
    * 从服务器拉取省/市/县数据
    * */
    private void queryFromServer(String url, final String type) {
        // 显示框
        Log.d(TAG, "queryFromServer: task id:" + Thread.currentThread().getId());
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "Load failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "queryFromServer: onResponse task id:" + Thread.currentThread().getId());
                String responseText =  response.body().string();
                Boolean result = false;

                // 将数据存入数据库
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProcince.getId());
                } else if ("county".equals(type)) {
                    Log.d(TAG, "onResponse: before query county from web");
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                    Log.d(TAG, "onResponse: after query county from web");
                }
                // 如果存入成功，在主线程修改 UI ， 显示省/市/县
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                Log.d(TAG, "queryFromServer runOnUIthread");
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });

    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
