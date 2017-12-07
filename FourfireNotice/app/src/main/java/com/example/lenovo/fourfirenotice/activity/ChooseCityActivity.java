package com.example.lenovo.fourfirenotice.activity;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lenovo.fourfirenotice.tools.MyAdapter;
import com.example.lenovo.fourfirenotice.R;

public class ChooseCityActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private ImageView imageView;
    private Button button;
    private TextView cityName;
    private TextView temperature;
    private TextView pm25;
    private TextView txt;
    private TextView updateTime;
    private MyAdapter myAdapter;
    private android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        iniRes();
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
        recyclerView = (RecyclerView) findViewById(R.id.recycleView2);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        collapsingToolbarLayout.setTitle("请选择城市");
        Glide.with(this).load(R.drawable.weather).into(imageView);
    }
}
