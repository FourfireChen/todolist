package com.example.lenovo.fourfirenotice;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static List<Notice> noticeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button button;
    private MyAdapter myAdapter;
    private android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connector.getDatabase();
        iniRes();
        iniNotice();
        myAdapter = new MyAdapter(noticeList);
        recyclerView.setAdapter(myAdapter);
        button.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        iniNotice();
        Collections.sort(noticeList,new TimeComparetor());
        for(Notice notice:noticeList)
            notice.save();
        myAdapter.updataList(noticeList);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.addbtn:
                Notice notice = new Notice("");
                notice.save();
                noticeList.add(notice);
                myAdapter.updataList(noticeList);
                break;
        }

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.fortime:
                Collections.sort(noticeList,new TimeComparetor());
                for(Notice notice:noticeList)
                    notice.save();
                myAdapter.updataList(noticeList);
            case R.id.deletemore:
                myAdapter.showDelete();
                break;
            case R.id.forlevel:
                Toast.makeText(this,"优先级的功能没做，感觉和时间排序一样",Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    public void iniRes()
    {
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbarlayout);
        ImageView imageView = (ImageView)findViewById(R.id.image);
        setSupportActionBar(toolbar);
        button = (Button)findViewById(R.id.addbtn);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        collapsingToolbarLayout.setTitle("四火便签");
        Glide.with(this).load(R.drawable.background).into(imageView);
    }
}
