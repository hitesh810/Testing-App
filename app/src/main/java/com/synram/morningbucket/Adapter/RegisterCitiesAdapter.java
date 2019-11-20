package com.synram.morningbucket.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.synram.morningbucket.Modal.CityDatum;
import com.synram.morningbucket.R;

import java.util.ArrayList;
import java.util.List;

public class RegisterCitiesAdapter extends RecyclerView.Adapter<RegisterCitiesAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<CityDatum> cityDatumList = new ArrayList<>();
    private List<CityDatum> cityListFiltered =new ArrayList<>();
    private ContactsAdapterListener contactsAdapterListener;

    public RegisterCitiesAdapter(Context context, List<CityDatum> cityDatumList,ContactsAdapterListener contactsAdapterListener) {
        this.context = context;
        this.cityDatumList = cityDatumList;
        this.cityListFiltered = cityDatumList;
        this.contactsAdapterListener = contactsAdapterListener;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
//                Log.d("CHARSTRING",charString);
                if (charString.isEmpty()) {
                    cityListFiltered = cityDatumList;
                } else {
                    List<CityDatum> filteredList = new ArrayList<>();
                    for (CityDatum row : cityDatumList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCityName().toLowerCase().contains(charString.toLowerCase()) || row.getCityName().contains(charSequence)) {


                            filteredList.add(row);
//                            Log.d("CHARSTRINGINIF",row.getCityName().toLowerCase()+"=="+charString.toLowerCase());

                        }
                    }

                    cityListFiltered = filteredList;

//                    Log.d("CITYNAME",filteredList.get(0).getCityName());

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = cityListFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                cityListFiltered = (ArrayList<CityDatum>) filterResults.values;
//                cityListFiltered = filteredList;

//                Log.d("CITYNAME_ACTUAL",cityListFiltered.get(0).getCityName());


                notifyDataSetChanged();
            }
        };
    }



    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView city_title;
        ConstraintLayout search_ll;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            city_title = itemView.findViewById(R.id.city_name);
            search_ll = itemView.findViewById(R.id.search_ll);



        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.homecity_row,viewGroup,false);

        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        final CityDatum cityDatum = cityListFiltered.get(position);

        myViewHolder.city_title.setText(cityDatum.getCityName());

        myViewHolder.search_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsAdapterListener.onContactSelected(cityDatum);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cityListFiltered.size();
    }


    public interface ContactsAdapterListener {
        void onContactSelected(CityDatum contact);
    }
}
