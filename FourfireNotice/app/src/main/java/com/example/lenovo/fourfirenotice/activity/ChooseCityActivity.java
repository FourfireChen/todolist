package com.example.lenovo.fourfirenotice.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.lenovo.fourfirenotice.db.City;
import com.example.lenovo.fourfirenotice.db.County;
import com.example.lenovo.fourfirenotice.db.Province;
import com.example.lenovo.fourfirenotice.gson.Weather;
import com.example.lenovo.fourfirenotice.net.HttpUtil;
import com.example.lenovo.fourfirenotice.net.Utility;
import com.example.lenovo.fourfirenotice.R;
import com.example.lenovo.fourfirenotice.tools.ChooseAdapter;
import com.example.lenovo.fourfirenotice.view.MoreListview;
import org.litepal.crud.DataSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseCityActivity extends AppCompatActivity
{
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_COUNTY = 3;
    private int currentLevel = LEVEL_PROVINCE;
    private MoreListview provinceListView;
    private MoreListview cityListview;
    private MoreListview countyListview;
    private ChooseAdapter provinceAdapter;
    private ChooseAdapter cityAdapter;
    private ChooseAdapter coutyAdapter;
    private ProgressDialog progressDialog;
    private ImageView imageView;
    private TextView cityName;
    private TextView temperature;
    private TextView pm25;
    private TextView txt;
    private TextView updateTime;
    private android.support.v7.widget.Toolbar toolbar;
    private static List<Province> provinceList;
    private static List<City> cityList;
    private static List<County> countyList;
    private List<String> provinceNames = new ArrayList<>();
    private List<String> cityNames = new ArrayList<>();
    private List<String> countyNames = new ArrayList<>();
    private static Province selectProvince;
    private static City selectCity;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        iniRes();
        iniQuery();
    }
    public void iniRes()
    {
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar2);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbarlayout2);
        imageView = (ImageView)findViewById(R.id.image2);
        setSupportActionBar(toolbar);
        temperature = (TextView)findViewById(R.id.temp2);
        pm25 = (TextView)findViewById(R.id.pm252);
        cityName = (TextView)findViewById(R.id.cityname2);
        txt = (TextView)findViewById(R.id.txt2);
        updateTime = (TextView)findViewById(R.id.updatetime2);
        provinceListView = (MoreListview) findViewById(R.id.prolist);
        cityListview = (MoreListview)findViewById(R.id.citylist);
        countyListview = (MoreListview)findViewById(R.id.countylist);
        cityAdapter = new ChooseAdapter(ChooseCityActivity.this,R.layout.city_item,cityNames);
        provinceAdapter = new ChooseAdapter(ChooseCityActivity.this,R.layout.city_item,provinceNames);
        coutyAdapter = new ChooseAdapter(ChooseCityActivity.this,R.layout.city_item,countyNames);
        provinceListView.setAdapter(provinceAdapter);
        cityListview.setAdapter(cityAdapter);
        countyListview.setAdapter(coutyAdapter);
        collapsingToolbarLayout.setTitle("请选择城市");
        Glide.with(this).load(R.drawable.weather).into(imageView);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null)
        {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }
        else
        {
            String weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }
    }

    public void showWeatherInfo(Weather weather)
    {
        String name = weather.basic.cityName;
        String time = weather.basic.update.updateTime.split(" ")[1];
        String temp = weather.now.temperature + "℃";
        String weatherTxt = weather.now.more.info;
        temperature.setText(temp);
        updateTime.setText(time);
        cityName.setText(name);
        txt.setText(weatherTxt);
    }

    public void requestWeather(final String weatherId)
    {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=455105f8812c4e0c8548b9f52f9e0990";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(ChooseCityActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(weather != null && "ok".equals(weather.status))
                        {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ChooseCityActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.commit();
                            showWeatherInfo(weather);
                        }
                        else
                        {
                            Toast.makeText(ChooseCityActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    public void queryProvinces()
    {
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size() > 0)
        {
            provinceNames.clear();
            for(Province province : provinceList)
            {
                provinceNames.add(province.getProvinceName());
            }
            provinceAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        }
        else
        {
            String address = "http://guolin.tech/api/china";
            queryFromInternet(address,LEVEL_PROVINCE);
        }
    }

    public void queryCities()
    {
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectProvince.getId())).find(City.class);
        if(cityList.size() > 0)
        {
            cityNames.clear();
            for(City city : cityList)
            {
                cityNames.add(city.getCityName());
            }
            cityAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
        }
        else
        {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromInternet(address,LEVEL_CITY);
        }
    }

    public void queryCouty()
    {
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectCity.getId())).find(County.class);
        if(countyList.size() > 0)
        {
            countyNames.clear();
            for(County county : countyList)
            {
                countyNames.add(county.getCountyName());
            }
            coutyAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
        }
        else
        {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromInternet(address,LEVEL_COUNTY);
        }
    }


    public void queryFromInternet(String address, final int level)
    {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        closeProgressDialog();
                        Toast.makeText(returnContext(),"没扒到",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String responseText = response.body().string();
                boolean result = false;
                if(level == LEVEL_PROVINCE)
                {
                    result = Utility.handleProvinceResponse(responseText);
                }
                else if(level == LEVEL_CITY)
                {
                    result = Utility.handleCityResponse(responseText,selectProvince.getId());
                }
                else if(level == LEVEL_COUNTY)
                {
                    result = Utility.handleCountyResponse(responseText,selectCity.getId());
                }
                if(result)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            closeProgressDialog();
                            if(level == LEVEL_PROVINCE)
                            {
                                queryProvinces();
                            }
                            else if(level == LEVEL_CITY)
                            {
                                queryCities();
                            }
                            else if(level == LEVEL_COUNTY)
                            {
                                queryCouty();
                            }
                        }
                    });
                }
            }
        });
    }

    public void showProgressDialog()
    {
        if(progressDialog == null)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在努力扒取信息");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    public void closeProgressDialog()
    {
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    public ChooseCityActivity returnContext()
    {
        return this;
    }

    public void iniQuery()
    {
        provinceListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectProvince = provinceList.get(position);
                queryCities();
            }
        });
        cityListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectCity = cityList.get(position);
                queryCouty();
            }
        });
        countyListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = getIntent();
                intent.putExtra("weatherid",countyList.get(position).getWeather());
                ChooseCityActivity.this.setResult(2,intent);
                ChooseCityActivity.this.finish();
            }
        });
        queryProvinces();
    }
}
