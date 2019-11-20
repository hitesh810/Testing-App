package com.synram.morningbucket.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.synram.morningbucket.Modal.CategoryDatum;
import com.synram.morningbucket.Modal.TypeDatum;
import com.synram.morningbucket.R;

import java.util.List;

public class ProductsTagsAdapter extends RecyclerView.Adapter<ProductsTagsAdapter.MyViewHolder> {


    private Context mContext;
    private List<TypeDatum> typeDatumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tag_name;

        public MyViewHolder(View view) {
            super(view);
            tag_name = (TextView) view.findViewById(R.id.tag_name);
        }
    }


    public ProductsTagsAdapter(Context mContext, List<TypeDatum> typeDatumList) {
        this.mContext = mContext;
        this.typeDatumList = typeDatumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tags_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        TypeDatum typeDatum = typeDatumList.get(position);
        holder.tag_name.setText(typeDatum.getTypeName());


    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return typeDatumList.size();
    }
}