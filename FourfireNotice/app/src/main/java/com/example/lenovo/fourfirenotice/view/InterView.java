package com.example.lenovo.fourfirenotice.view;

import com.example.lenovo.fourfirenotice.model.db.Notice;
import com.example.lenovo.fourfirenotice.model.gson.Weather;
import java.util.List;

/**
 * Created by lenovo on 2017/12/11.
 */

public interface InterView
{
    void ViewShowWeatherInfo(final Weather weather);//将Presenter返回的天气数据显示出来
    void ViewShowProgressDialog();//打开读取的进度条
    void ViewCloseProgressDialog();//关不读取进度条
    List<String> getProvinceNames();//
    List<String> getCityNames();
    List<String> getCountyNames();
    void failGetToast();
    MoreListview getProvinceListView();
    MoreListview getCityListview();
    MoreListview getCountyListview();
    void notifyCityChange();
    void notifyCountyChange();
    void notifyNoticeChange();
    void notifyProvinceChange();
    void updataList(List<Notice> noticeList);
}
