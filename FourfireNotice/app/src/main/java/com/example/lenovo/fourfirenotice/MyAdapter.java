package com.example.lenovo.fourfirenotice;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * Created by lenovo on 2017/12/1.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{
    private List<Notice> noticeList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View noticeView;
        TextView textView;
        public ViewHolder(View itemView)
        {
            super(itemView);
            noticeView = itemView;
            textView = (TextView)itemView.findViewById(R.id.text);
        }
        public TextView getTextView()
        {
            return textView;
        }
    }

    public MyAdapter(List<Notice> noticeList)
    {
        this.noticeList = noticeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.getTextView().setText(noticeList.get(position).getText());
        holder.noticeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(holder.noticeView.getContext(),EditActivity.class);
                intent.putExtra("position",position);
                holder.noticeView.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return noticeList.size();
    }

    public void updataList(List<Notice> noticeList)
    {
        this.noticeList = noticeList;
        notifyDataSetChanged();
    }
}
