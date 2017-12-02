package com.example.lenovo.fourfirenotice;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
        noticeList = MainActivity.noticeList;
        timepicker = (TextView)findViewById(R.id.timechoice);
        buttonDate = (Button)findViewById(R.id.date);
        buttonTime = (Button)findViewById(R.id.time);
        button = (Button)findViewById(R.id.delete);
        button.setOnClickListener(this);
        editText = (EditText)findViewById(R.id.edit);
        intent = getIntent();
        position = intent.getIntExtra("position",0);
        years = noticeList.get(position).getYear();
        months = noticeList.get(position).getMonth();
        days = noticeList.get(position).getDay();
        hours = noticeList.get(position).getHour();
        minutes = noticeList.get(position).getMinute();
        setTime(years,months,days,hours,minutes);
        editText.setText(noticeList.get(position).getText());
        buttonDate.setOnClickListener(this);
        buttonTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.delete:
                noticeList.get(position).delete();
                noticeList.remove(position);
                isRemove = true;
                finish();
                break;
            case R.id.time:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        hours = hourOfDay;
                        minutes = minute;
                    }
                },hours,minutes,true).show();
                setTime(years,months,days,hours,minutes);
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
                    }
                },years,months,days).show();
                setTime(years,months,days,hours,minutes);
                break;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(isRemove != true)
        {
            noticeList.get(position).setText(editText.getText().toString());
            noticeList.get(position).save();
        }
    }

    public void setTime(int year,int month,int day,int hour,int minute)
    {
        this.hours = hour;
        this.minutes = minute;
        this.years = year;
        this.months = month;
        this.days = day;
        timepicker.setText(this.years + "." + (this.months + 1) + "." + this.days + "-" + this.hours + ":" + minutes);
        noticeList.get(position).setYear(year);
        noticeList.get(position).setMonth(month);
        noticeList.get(position).setDay(day);
        noticeList.get(position).setHour(hour);
        noticeList.get(position).setMinute(minute);
        noticeList.get(position).save();
    }
}
