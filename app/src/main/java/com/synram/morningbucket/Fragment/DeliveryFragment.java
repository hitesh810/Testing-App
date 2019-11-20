package com.synram.morningbucket.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synram.morningbucket.Adapter.HorizontalCalanderAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.CategoryDatum;
import com.synram.morningbucket.Modal.HorizontalCalanderModal;
import com.synram.morningbucket.R;
import com.synram.morningbucket.Utilities.RecyclerTouchListener;
import com.synram.morningbucket.activities.MainActivity;
import com.synram.morningbucket.activities.Productslistactivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeliveryFragment extends Fragment {


    RecyclerView calander_rv;
    private List<Date> horizontalCalanderModalList = new ArrayList<>();

    private TextView clicked_Date;
    private HorizontalCalanderAdapter horizontalCalanderAdapter;
    public DeliveryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delivery, container, false);


        initView(view);
        return view;
    }

    public void initView(View view){

        calander_rv = view.findViewById(R.id.calander_rv);
        clicked_Date = view.findViewById(R.id.clicked_Date);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        calander_rv.setLayoutManager(layoutManager);
        calander_rv.setItemAnimator(new DefaultItemAnimator());


//        Calendar cal = Calendar.getInstance();
//
//
//
////        cal.set(Calendar.MONTH, 1);
////        cal.set(Calendar.DAY_OF_MONTH, 1);
//
//        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//        Log.d("MAXDAY",String.valueOf(maxDay));
//        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
//
//        System.out.print(df.format(cal.getTime()));
//        int current_date = cal.get(Calendar.DAY_OF_YEAR);
//        int current_month = cal.get(Calendar.DAY_OF_MONTH)+2;
//        int current_year = cal.get(Calendar.DAY_OF_YEAR)-1;
//
//        for (int i = current_date; i <=current_month+14; i++) {
//
//
//                cal.add(Calendar.DAY_OF_MONTH,i);
//
//
//
//
//
//            Log.d("DATA",df.format(cal.getTime()));
//            System.out.print(", " + df.format(cal.getTime()));
//        }



//        Calendar cal = GregorianCalendar.getInstance();
//        cal.setTime(new Date());
//        cal.add(Calendar.DAY_OF_YEAR, 7);
//        Date _7daysBeforeDate = cal.getTime();
//
//        Log.d("data",cal.getTime().toString());

        getdate();


        calander_rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), calander_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Date horizontalCalanderModal = horizontalCalanderModalList.get(position);

                clicked_Date.setText(formatdate(horizontalCalanderModal));

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }



    public void getdate(){
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date current = null;

        Calendar calendar = Calendar.getInstance();
        int dayToComapre = calendar.get(Calendar.DAY_OF_YEAR)-5;
        int monthToComapre = calendar.get(Calendar.MONTH) + 2;
        int yearToComapre = calendar.get(Calendar.YEAR) - 1;

        try {
            current = sdf.parse(dayToComapre + "/" + monthToComapre + "/" + yearToComapre);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i = dayToComapre; i < dayToComapre + 14; i++) {
            try {
                current = sdf.parse(i + "/" + monthToComapre + "/" + yearToComapre);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.e("date", current+"");

            horizontalCalanderModalList.add(current);


        }


        horizontalCalanderAdapter = new HorizontalCalanderAdapter(getActivity(),horizontalCalanderModalList);
        calander_rv.setAdapter(horizontalCalanderAdapter);

    }

    public String formatdate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf.format(date);

        return convertedate;
    }
}
