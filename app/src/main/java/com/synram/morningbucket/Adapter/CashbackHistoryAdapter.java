package com.synram.morningbucket.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synram.morningbucket.Modal.CashbackHistory;
import com.synram.morningbucket.Modal.TransactionHistory;
import com.synram.morningbucket.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

;


public class CashbackHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
//    private SharedPref sharedPref;
    private List<CashbackHistory> items = new ArrayList<>();



    private AlertDialog.Builder delcart_builder;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView t_date;
        public TextView t_amt;
        public TextView order_id;


        public ViewHolder(View v) {
            super(v);
            t_date = (TextView) v.findViewById(R.id.t_date);
            t_amt = (TextView) v.findViewById(R.id.t_amt);
            order_id = (TextView) v.findViewById(R.id.order_id);

        }
    }

    public CashbackHistoryAdapter(Context ctx, List<CashbackHistory> items) {
        this.ctx = ctx;
        this.items = items;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cashback_history_row, parent, false);
        vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        CashbackHistory product = items.get(position);

        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;

        vItem.t_amt.setText("₹"+product.getAmount());
            try {
                vItem.t_date.setText(formatdateReturnDateWithPlusone(product.getTDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            vItem.order_id.setText(product.getOrderId());

        }


    }


    public String formatdateReturnDateWithPlusone(String date_str) throws ParseException {

//        DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD");
//        Date date = null;
//
//        try {
//            date = (Date)formatter.parse(date_str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
//        String convertedate = sdf.format(date);
//
//        return date;
//


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date= new Date(sdf.parse(date_str).getTime());
        date.setMonth(date.getMonth());

        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf2.format(date);

        return convertedate;
    }


    public String formatdate(String date_str){

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD");
        Date date = null;

        try {
            date = (Date)formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf.format(date);

        return convertedate;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<CashbackHistory> getItem() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<CashbackHistory> items) {
        this.items = items;
        notifyDataSetChanged();
    }


//    public void dialogDeleteActiveConfirmation(final int id) {
//
//        delcart_builder = new AlertDialog.Builder();
//        delcart_builder.setTitle(R.string.title_delete_confirm);
//        delcart_builder.setMessage(getString(R.string.content_delete_confirm) + getString(R.string.title_activity_cart));
//        delcart_builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface di, int i) {
//                di.dismiss();
//                db.deleteActiveCart(id);
//                onResume();
//                Snackbar.make(parent_view, R.string.delete_success, Snackbar.LENGTH_SHORT).show();
//            }
//        });
//        delcart_builder.setNegativeButton(R.string.CANCEL, null);
//        delcart_builder.show();
//    }


}