package com.synram.morningbucket.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synram.morningbucket.Modal.PlanDate;
import com.synram.morningbucket.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SubscribedCalanderAdapter extends RecyclerView.Adapter<SubscribedCalanderAdapter.MyViewHolder> {

    private Context mContext;

    private List<PlanDate> plan_dateList = new ArrayList<>();
    private int selected_position = -1;

    public SubscribedCalanderAdapter(Context mContext, List<PlanDate> plan_dateList) {
        this.mContext = mContext;
        this.plan_dateList = plan_dateList;
    }

    @NonNull
    @Override
    public SubscribedCalanderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_cal_row, viewGroup, false);

        return new MyViewHolder(itemview);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final SubscribedCalanderAdapter.MyViewHolder myViewHolder, final int i) {

        PlanDate horizontalcalander = plan_dateList.get(i);

        Typeface lato_bold = ResourcesCompat.getFont(mContext, R.font.lato_bold);
        Typeface lato = ResourcesCompat.getFont(mContext, R.font.lato);
        Typeface lato_light = ResourcesCompat.getFont(mContext, R.font.lato_light);


        myViewHolder.weak_name.setText(dataofweek(horizontalcalander.getDate()));

        myViewHolder.date.setText(getday(horizontalcalander.getDate()));


        if (horizontalcalander.getStatus().equals("0")) {

            myViewHolder.weak_name.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.weak_name.setTypeface(lato_bold);

            myViewHolder.date.setTextColor(Color.parseColor("#FFFFFF"));

            myViewHolder.cal_ll.setBackgroundColor(Color.parseColor("#eea236"));
//

            Log.d("RUNLOG", "IFRUNNING Status 0");


        } else if (horizontalcalander.getStatus().equals("2")) {
            myViewHolder.weak_name.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.weak_name.setTypeface(lato_bold);

            myViewHolder.date.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.cal_ll.setBackgroundColor(Color.parseColor("#dc3545"));

            Log.d("RUNLOG", "IFRUNNING Status 2");

        } else if (horizontalcalander.getStatus().equals("3")) {
            myViewHolder.weak_name.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.weak_name.setTypeface(lato_bold);

            myViewHolder.date.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.cal_ll.setBackgroundColor(Color.parseColor("#dc3545"));

            Log.d("RUNLOG", "IFRUNNING Status 2");

        } else if (horizontalcalander.getStatus().equals("1")) {
            myViewHolder.weak_name.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.weak_name.setTypeface(lato_bold);

            myViewHolder.date.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.cal_ll.setBackgroundColor(Color.parseColor("#398439"));
            Log.d("RUNLOG", "IFRUNNING Status 1");

        }else if (horizontalcalander.getStatus().equals("4")) {
            myViewHolder.weak_name.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.weak_name.setTypeface(lato_bold);

            myViewHolder.date.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.cal_ll.setBackgroundColor(Color.parseColor("#449285"));
            Log.d("RUNLOG", "IFRUNNING Status 1");

        }


/*        if (formatdate(horizontalcalander.getDate()).equals(Calendar.getInstance().getTime())) {

            myViewHolder.cal_ll.setBackgroundColor(Color.parseColor("#00FF00"));

            myViewHolder.weak_name.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.date.setTextColor(Color.parseColor("#FFFFFF"));
            myViewHolder.weak_name.setTypeface(lato_bold);

            myViewHolder.cal_ll.setBackgroundColor(Color.parseColor("#28a745"));


        }*/

        if (selected_position == i) {
            //changes background color of selected item in RecyclerView

//            if (horizontalcalander.getStatus().equals("2")) {

//            } else if (horizontalcalander.getStatus().equals("1")) {

            setMargins(myViewHolder.itemView,3,0,3,0);


//            }


//            myViewHolder.cal_ll.setBackgroundResource(R.drawable.btn_border_botm);

        } else {
//            myViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
            //this updated an order property by status in DB
            setMargins(myViewHolder.itemView,0,0,0,0);


        }
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //status switch and DB update
//                if (order.getProductStatus().equals("0")) {
//                    order.setProductStatus("1");
//                } else {
//                    if (order.getProductStatus().equals("1")){
//                        //calls for interface implementation in
//                        //MainActivity which opens a new fragment with
//                        //selected item details
////                        listener.onOrderSelected(order);
//                    }
//                }
                notifyItemChanged(selected_position);
                selected_position = i;
                notifyItemChanged(selected_position);

            }
        });

        //        used to click on first index recycler item by defalult


    }

    @Override
    public int getItemCount() {
        return plan_dateList.size();
    }

    public String dataofweek(String date_str) {

        DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date = null;
        try {
            date = (Date) formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EE");
        String dayOfTheWeek = sdf.format(date);

        return dayOfTheWeek;
    }

    public String getday(String date_str) {

        DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date = null;

        try {
            date = (Date) formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String dayOfTheWeek = sdf.format(date);

        return dayOfTheWeek;
    }

    public String formatdate(String date_str) {

        DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
//        DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD");
        Date date = null;

        try {
            date = (Date) formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf.format(date);

        return convertedate;
    }

    public String formatREALdate(Date date) {


        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf.format(date);

        return convertedate;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView weak_name, date;
        LinearLayout cal_ll;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            weak_name = itemView.findViewById(R.id.weak_name);
            date = itemView.findViewById(R.id.date);
            cal_ll = itemView.findViewById(R.id.cal_ll);

        }
    }


    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            final float scale = mContext.getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l =  (int)(left * scale + 0.5f);
            int r =  (int)(right * scale + 0.5f);
            int t =  (int)(top * scale + 0.5f);
            int b =  (int)(bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }
}
