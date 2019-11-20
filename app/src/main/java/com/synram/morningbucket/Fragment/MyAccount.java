package com.synram.morningbucket.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.synram.morningbucket.Adapter.MyAccountAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.AcountListModal;
import com.synram.morningbucket.Modal.CashbackHistory;
import com.synram.morningbucket.Modal.CityDatum;
import com.synram.morningbucket.Modal.TransactionHistory;
import com.synram.morningbucket.R;
import com.synram.morningbucket.Utilities.MyDividerItemDecoration;
import com.synram.morningbucket.Utilities.RecyclerTouchListener;
import com.synram.morningbucket.activities.CashbackHistoryActivity;
import com.synram.morningbucket.activities.MainActivity;
import com.synram.morningbucket.activities.OrderListActivity;
import com.synram.morningbucket.activities.Personaldetail;
import com.synram.morningbucket.activities.TransactionHistoryActivity;
import com.synram.morningbucket.activities.WalletActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccount extends Fragment {


    private RecyclerView myaccount_rv;
    private List<AcountListModal> acountListModalList = new ArrayList<>();

    private MyAccountAdapter myAccountAdapter;
    private Dialog dialog;

    public MyAccount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view= inflater.inflate(R.layout.fragment_my_account, container, false);


        initview(view);

        return view;
    }


    public void initview(View view){

        myaccount_rv = view.findViewById(R.id.myaccount_rv);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        myaccount_rv.setLayoutManager(layoutManager);
        myaccount_rv.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));

        populateaccountListItem();

        myAccountAdapter = new MyAccountAdapter(getActivity(),acountListModalList);


        myaccount_rv.setAdapter(myAccountAdapter);


        myaccount_rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), myaccount_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (position==0){
                    Intent intent = new Intent(getActivity(),Personaldetail.class);
                    startActivity(intent);

                }else if (position==1){
                    Intent intent = new Intent(getActivity(),WalletActivity.class);
                    startActivity(intent);

                }else if (position==3){
                    Intent intent = new Intent(getActivity(),OrderListActivity.class);
                    startActivity(intent);

                }else if (position==2){
                    Intent intent = new Intent(getActivity(),CashbackHistoryActivity.class);
                    startActivity(intent);

                }else if (position==4){


                    String playstorelink = "https://play.google.com/store/apps/details?id=com.synram.morningbucket";
                    String shareBody = "Get fresh daily essentials & Organic food products delivered to your place, instantly. Download The Morning Bucket app now & get cashback on monthly subscriptions" + System.getProperty("line.separator") + playstorelink;
                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(shareIntent, "Share application via.."));



                }else if(position==5){

                    showCitySelectorDialog(getActivity());
                }else if(position==6){

                    Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" +getActivity().getPackageName())));
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



    }

    public void showCitySelectorDialog(final Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_contact_selector_layout, null, false);
        /*HERE YOU CAN FIND YOU IDS AND SET TEXTS OR BUTTONS*/

       TextView contact_one = view.findViewById(R.id.contact_one);
       TextView contact_two = view.findViewById(R.id.contact_two);
       final TextView mail_id = view.findViewById(R.id.mail_id);

       contact_one.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(Intent.ACTION_DIAL);
               intent.setData(Uri.parse("tel:9713071117"));
               startActivity(intent);

           }
       });

       contact_two.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(Intent.ACTION_DIAL);
               intent.setData(Uri.parse("tel:9713081118"));
               startActivity(intent);
           }
       });

       mail_id.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String[] email_str = {mail_id.getText().toString()};
               shareToGMail(email_str,"Feedback","");
           }
       });


        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);
        dialog.show();
    }


    public void shareToGMail(String[] email, String subject, String content) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        final PackageManager pm = getActivity().getApplicationContext().getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for(final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        startActivity(emailIntent);
    }

    public void populateaccountListItem(){

        acountListModalList.clear();

        int[] menu_img = new int[]{
                R.drawable.ic_man_user,
                R.drawable.ic_wallet,
                R.drawable.ic_payment_method,
                R.drawable.ic_shopping_list,
                R.drawable.ic_referfrnd,
                R.drawable.ic_call_black_24dp,
                R.drawable.ic_rating
 };



        AcountListModal acountListModal = new AcountListModal(menu_img[0],"Profile");

        acountListModalList.add(acountListModal);


        acountListModal = new AcountListModal(menu_img[1],"Wallet");

        acountListModalList.add(acountListModal);

//        acountListModal = new AcountListModal(menu_img[2],"Transaction History");
//
//        acountListModalList.add(acountListModal);

        acountListModal = new AcountListModal(menu_img[2],"Cashback History");

        acountListModalList.add(acountListModal);

        acountListModal = new AcountListModal(menu_img[3],"Order History");

        acountListModalList.add(acountListModal);


        acountListModal = new AcountListModal(menu_img[4],"Share App to Friend");

        acountListModalList.add(acountListModal);
//
        acountListModal = new AcountListModal(menu_img[5],"+91-9713071117, +91-9713081118\nmorningbucket@gmail.com");
//
        acountListModalList.add(acountListModal);
//
        acountListModal = new AcountListModal(menu_img[6],"Rate us on Play Store");

        acountListModalList.add(acountListModal);


    }


}
