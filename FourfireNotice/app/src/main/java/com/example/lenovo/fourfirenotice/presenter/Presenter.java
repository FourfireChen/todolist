package com.example.lenovo.fourfirenotice.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import com.example.lenovo.fourfirenotice.model.InterModel;
import com.example.lenovo.fourfirenotice.model.Model;
import com.example.lenovo.fourfirenotice.model.TimeComparetor;
import com.example.lenovo.fourfirenotice.model.db.City;
import com.example.lenovo.fourfirenotice.model.db.County;
import com.example.lenovo.fourfirenotice.model.db.Notice;
import com.example.lenovo.fourfirenotice.model.db.Province;
import com.example.lenovo.fourfirenotice.model.gson.Weather;
import com.example.lenovo.fourfirenotice.view.InterView;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.lenovo.fourfirenotice.view.activity.MainActivity.noticeList;

/**
 * Created by lenovo on 2017/12/11.
 */

public class Presenter implements InterPresenter
{
    private InterModel MyModel;
    private InterView MyView;
    private static List<Province> provinceList;
    private static List<City> cityList;
    private static List<County> countyList;
    private static Province selectProvince;
    private static City selectCity;
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_COUNTY = 3;
    private SharedPreferences prefs;
    private String lastWeatherId;
    private List<String> provinceNames = new ArrayList<>();
    private List<String> cityNames = new ArrayList<>();
    private List<String> countyNames = new ArrayList<>();

    public Presenter(InterView myView)
    {
        MyView = myView;
        MyModel = new Model();
        prefs = PreferenceManager.getDefaultSharedPreferences((Context)myView);
        lastWeatherId = prefs.getString("weatherid",null);
    }

    public void queryProvinces()
    {
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size() > 0)
        {
            MyView.getProvinceNames().clear();
            for(Province province : provinceList)
            {
                MyView.getProvinceNames().add(province.getProvinceName());
            }
            MyView.notifyProvinceChange();
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
            MyView.getCityNames().clear();
            for(City city : cityList)
            {
                MyView.getCityNames().add(city.getCityName());
            }
            MyView.notifyCityChange();

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
            MyView.getCountyNames().clear();
            for(County county : countyList)
            {
                MyView.getCountyNames().add(county.getCountyName());
            }
            MyView.notifyCountyChange();
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
        MyView.ViewShowProgressDialog();
        MyModel.sendOkHttpRequest(address, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                MyView.ViewCloseProgressDialog();
                MyView.failGetToast();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String responseText = response.body().string();
                boolean result = false;
                if(level == LEVEL_PROVINCE)
                {
                    result = MyModel.handleProvinceResponse(responseText);
                }
                else if(level == LEVEL_CITY)
                {
                    result = MyModel.handleCityResponse(responseText,selectProvince.getId());
                }
                else if(level == LEVEL_COUNTY)
                {
                    result = MyModel.handleCountyResponse(responseText,selectCity.getId());
                }
                if(result)
                {
                    MyView.ViewCloseProgressDialog();
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
            }
        });
    }

    public void iniQuery()
    {
        MyView.getProvinceListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectProvince = provinceList.get(position);
                queryCities();
            }
        });
        MyView.getCityListview().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectCity = cityList.get(position);
                queryCouty();
            }
        });
        MyView.getCountyListview().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                requestWeather(countyList.get(position).getWeather());
            }
        });
        queryProvinces();
    }

    public void requestWeather(final String weatherId)
    {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=455105f8812c4e0c8548b9f52f9e0990";
        MyModel.sendOkHttpRequest(weatherUrl, new Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {

                MyView.failGetToast();
                String weatherText = prefs.getString("weather",null);
                if(weatherText != null)
                {
                    Weather weather = MyModel.handleWeatherResponse(weatherText);
                    MyView.ViewShowWeatherInfo(weather);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseText = response.body().string();
                final Weather weather = MyModel.handleWeatherResponse(responseText);
                if(weather != null && "ok".equals(weather.status))
                {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences((Context)MyView).edit();
                    editor.putString("weather",responseText);
                    editor.putString("weatherid",weather.basic.weatherId);
                    editor.commit();
                    MyView.ViewShowWeatherInfo(weather);
                }
                else
                {
                    MyView.failGetToast();
                }
            }
        });
    }

    public void iniWeather()
    {
        if(lastWeatherId != null)
        {
            requestWeather(lastWeatherId);
        }
        else
        {
            requestWeather("CN101200101");
        }
    }

    public void sort()
    {
        Connector.getDatabase();
        iniNotice();
        Collections.sort(noticeList,new TimeComparetor());
        for(Notice notice:noticeList)
            notice.save();
        MyView.updataList(noticeList);
    }

    public void iniNotice()
    {
        noticeList = DataSupport.findAll(Notice.class);
        if(noticeList.size() == 0)
        {
            Notice notice = new Notice("哇帅哥来了");
            notice.save();
            noticeList.add(notice);
        }
        MyView.updataList(noticeList);
    }

    public void add()
    {
        Notice notice = new Notice("");
        notice.save();
        noticeList.add(notice);
        MyView.notifyNoticeChange();
    }

    @Override
    public List<Notice> getNoticeList()
    {
        return noticeList;
    }

//    @Override
//    public List<String> getProvinceNames()
//    {
//        return provinceNames;
//    }
//
//    @Override
//    public List<String> getCityNames()
//    {
//        return provinceNames;
//    }
//
//    @Override
//    public List<String> getCountyNames()
//    {
//        return provinceNames;
//    }


}
