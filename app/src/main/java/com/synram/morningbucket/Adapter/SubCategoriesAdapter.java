package com.synram.morningbucket.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.synram.morningbucket.Modal.CategoryDatum;
import com.synram.morningbucket.Modal.SubCatview;
import com.synram.morningbucket.R;

import java.util.List;

public class SubCategoriesAdapter extends RecyclerView.Adapter{


    private Context mContext;
    private List<SubCatview> subCatviewList;
    String parent_cat_name,getParent_cat_img;

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView parentCat_name;
        public ImageView parent_cat_img;
        public LinearLayout progressBar;

        public HeaderViewHolder(View view) {
            super(view);
            parentCat_name = (TextView) view.findViewById(R.id.parentCat_name);

            parent_cat_img = (ImageView) view.findViewById(R.id.parent_cat_img);

            progressBar =  view.findViewById(R.id.progressBar);
        }
    }

    public static class SubcatViewHolder extends RecyclerView.ViewHolder {
        public TextView subcat_pro_title;
        public ImageView childsubcat_img;
        public LinearLayout progressBar;

        public SubcatViewHolder(View view) {
            super(view);
            subcat_pro_title = (TextView) view.findViewById(R.id.subcat_pro_title);
             childsubcat_img = (ImageView) view.findViewById(R.id.childsubcat_img);
            progressBar =  view.findViewById(R.id.progressBar);
        }
    }



    public SubCategoriesAdapter(Context mContext, List<SubCatview> subCatviewList,String parent_cat_name,String parent_cat_img) {
        this.mContext = mContext;
        this.subCatviewList = subCatviewList;

        this.getParent_cat_img = parent_cat_img;
        this.parent_cat_name = parent_cat_name;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = null;

//        switch (viewType){
//
//            case 0:
//                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_layout, parent, false);
//                return new SubcatViewHolder(view);
//
////              view=  LayoutInflater.from(parent.getContext()).inflate(R.layout.catheader, parent, false);
////                return new HeaderViewHolder(view);
//
//            case 1:

               view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_layout, parent, false);
                return new SubcatViewHolder(view);

//        }

//        return null;


    }
//
//    @Override
//    public int getItemViewType(int position) {
//
//        if (position==0){
//            return 0;
//        }else {
//            return 1;
//        }
//    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
//        if (position==0){
//
//            SubCatview subCatview = subCatviewList.get(position);
//
//
//            Picasso.get().load(subCatview.getImage()).into(((SubcatViewHolder) holder).childsubcat_img, new Callback() {
//                @Override
//                public void onSuccess() {
//                    ((SubcatViewHolder)holder).progressBar.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onError(Exception e) {
//
//                }
//            });
//
//
//            ((SubcatViewHolder)holder).subcat_pro_title.setText(subCatview.getName());
//
//
////            Picasso.get().load(getParent_cat_img).into(((HeaderViewHolder) holder).parent_cat_img, new Callback() {
////                @Override
////                public void onSuccess() {
////                    ((HeaderViewHolder)holder).progressBar.setVisibility(View.GONE);
////                }
////
////                @Override
////                public void onError(Exception e) {
////
////                }
////            });
////
////
////            ((HeaderViewHolder)holder).parentCat_name.setText(parent_cat_name);
//
//        }else {

            SubCatview subCatview = subCatviewList.get(position);


            Picasso.get().load(subCatview.getImage()).into(((SubcatViewHolder) holder).childsubcat_img, new Callback() {
                @Override
                public void onSuccess() {
                    ((SubcatViewHolder)holder).progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });


            ((SubcatViewHolder)holder).subcat_pro_title.setText(subCatview.getName());


//        }





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
        return subCatviewList.size();
    }
}