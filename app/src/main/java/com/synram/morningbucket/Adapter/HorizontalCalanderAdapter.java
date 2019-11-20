package com.synram.morningbucket.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synram.morningbucket.Modal.HorizontalCalanderModal;
import com.synram.morningbucket.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HorizontalCalanderAdapter extends RecyclerView.Adapter<HorizontalCalanderAdapter.MyViewHolder>{

    private Context mContext;

    private List<Date> horizontalCalanderModalList  = new ArrayList<>();


    public HorizontalCalanderAdapter(Context mContext, List<Date> horizontalCalanderModalList) {
        this.mContext = mContext;
        this.horizontalCalanderModalList = horizontalCalanderModalList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView weak_name,date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            weak_name = itemView.findViewById(R.id.weak_name);
            date = itemView.findViewById(R.id.date);

        }
    }




    @NonNull
    @Override
    public HorizontalCalanderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_cal_row,viewGroup,false);

        return new MyViewHolder(itemview);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull HorizontalCalanderAdapter.MyViewHolder myViewHolder, int i) {

        Date horizontalcalander = horizontalCalanderModalList.get(i);

        Typeface lato_bold = ResourcesCompat.getFont(mContext,R.font.lato_bold);
        Typeface lato = ResourcesCompat.getFont(mContext,R.font.lato);
        Typeface lato_light = ResourcesCompat.getFont(mContext,R.font.lato_light);




        myViewHolder.weak_name.setText(dataofweek(horizontalcalander));

        myViewHolder.date.setText(getday(horizontalcalander));



        if (formatdate(horizontalcalander).equals(formatdate(Calendar.getInstance().getTime()))){

            myViewHolder.weak_name.setTextColor(R.color.colorPrimary);
            myViewHolder.weak_name.setTypeface(lato_bold);


        }







    }

    @Override
    public int getItemCount() {
        return horizontalCalanderModalList.size();
    }


    public String dataofweek(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("EE");
        String dayOfTheWeek = sdf.format(date);

        return dayOfTheWeek;
    }

    public String getday(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String dayOfTheWeek = sdf.format(date);

        return dayOfTheWeek;
    }

    public String formatdate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf.format(date);

        return convertedate;
    }
}
