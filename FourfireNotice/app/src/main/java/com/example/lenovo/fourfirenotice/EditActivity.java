package com.example.lenovo.fourfirenotice;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.Calendar;
import java.util.List;

public class EditActivity extends AppCompatActivity implements View.OnClickListener
{
    private int years,months,days;
    private int hours,minutes;
    private TextView timepicker;
    private boolean isRemove;
    private EditText editText;
    private List<Notice> noticeList;
    private Notice thisNotice;
    private Button button;
    private Button buttonDate;
    private Button buttonTime;
    private Intent intent;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Connector.getDatabase();
        iniRes();
        thisNotice = noticeList.get(position);
        iniNotice();
        timepicker.setText(this.years + "." + (this.months + 1) + "." + this.days + "-" + this.hours + ":" + minutes);
        editText.setText(thisNotice.getText());
        button.setOnClickListener(this);
        buttonDate.setOnClickListener(this);
        buttonTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.delete:
                delete();
                break;
            case R.id.time:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        hours = hourOfDay;
                        minutes = minute;
                        setTime(years,months,days,hours,minutes);
                    }
                },hours,minutes,true).show();

                break;
            case R.id.date:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        years = year;
                        months = month;
                        days = dayOfMonth;
                        setTime(years,months,days,hours,minutes);
                    }
                },years,months,days).show();
                break;
        }
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        if(isRemove != true)
        {
            thisNotice.setText(editText.getText().toString());
            thisNotice.save();
        }
    }
    public void iniRes()
    {
        noticeList = MainActivity.noticeList;
        timepicker = (TextView)findViewById(R.id.timechoice);
        buttonDate = (Button)findViewById(R.id.date);
        buttonTime = (Button)findViewById(R.id.time);
        button = (Button)findViewById(R.id.delete);
        editText = (EditText)findViewById(R.id.edit);
        intent = getIntent();
        position = intent.getIntExtra("position",0);
    }
    public void iniNotice()
    {
        years = thisNotice.getYear();
        months = thisNotice.getMonth();
        days = thisNotice.getDay();
        hours = thisNotice.getHour();
        minutes = thisNotice.getMinute();
    }
    public void setTime(int year,int month,int day,int hour,int minute)
    {
        timepicker.setText(this.years + "." + (this.months + 1) + "." + this.days + "-" + this.hours + ":" + minutes);
        thisNotice.setYear(year);
        thisNotice.setMonth(month);
        thisNotice.setDay(day);
        thisNotice.setHour(hour);
        thisNotice.setMinute(minute);
        thisNotice.save();
    }
    public void delete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定删除?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                thisNotice.delete();
                noticeList.remove(position);
                isRemove = true;
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {}
        });
        builder.show();
    }
}
