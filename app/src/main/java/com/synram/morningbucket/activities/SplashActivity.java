package com.synram.morningbucket.activities;

import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Interfaces.CallbackDialog;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.Utilities.DialogUtils;
import com.synram.morningbucket.Utilities.NetworkCheck;
import com.synram.morningbucket.Utilities.PermissionUtil;
import com.synram.morningbucket.Utilities.Tools;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {


    private boolean on_permission_result = false;
    private SharedPreference sharedPreference;
    private RelativeLayout layout;
    private RelativeLayout mountainLayout;
    private RelativeLayout lightMountain;
    private RelativeLayout yellowBackground;
    private Animation fadeOut;
    private Animation fadeIn;
    private Animation upAnim;
    private Animation backFadeIn;
    private ConstraintLayout container;
    private AnimationDrawable anim;
    private ImageView logo;
    private AnimationDrawable trans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        setContentView(R.layout.activity_splash);
        sharedPreference = new SharedPreference(this);

        logo = findViewById(R.id.logo);
        
        initView();



    }



    public void initView(){
        ImageView img = (ImageView) findViewById(R.id.sun);
        container = (ConstraintLayout) findViewById(R.id.container);

        //animation for sun grow

//        anim = (AnimationDrawable) container.getBackground();
//        anim.setEnterFadeDuration(3000);
//        anim.setExitFadeDuration(3000);
//        anim.start();

//        final Animation animationlogo = AnimationUtils.loadAnimation(this,R.anim.zoomin);
//
//        Animation animationRotate = AnimationUtils.loadAnimation(this,R.anim.rotate);
//        img.startAnimation(animationRotate);
//        animationRotate.setFillAfter(true);
//
//        upAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_anim);
//        img.startAnimation(upAnim);
//        upAnim.setFillAfter(true);
//
//        upAnim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                logo.setVisibility(View.VISIBLE);
//                logo.startAnimation(animationlogo);
//                animationlogo.setFillAfter(true);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });


// end of animation for sun grow

////
//        backFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.background_fadein);
//        yellowBackground.startAnimation(backFadeIn);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();

        // permission checker for android M or higher
        if (Tools.needRequestPermission() && !on_permission_result) {
            String[] permission = PermissionUtil.getDeniedPermission(this);
            if (permission.length != 0) {
                requestPermissions(permission, 200);
            } else {
                startProcess();
            }
        } else {
            startProcess();
        }


    }

    private void startProcess() {
        if (!NetworkCheck.isConnect(this)) {
            dialogNoInternet();
        } else {
//            check for Update
//            requestInfo();



            AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this).withListener(new AppUpdaterUtils.UpdateListener() {
                @Override
                public void onSuccess(Update update, Boolean aBoolean) {

                    if (aBoolean) {
                        new AppUpdater(SplashActivity.this)
                                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                                .setDisplay(Display.DIALOG)
                                .showAppUpdated(true).setCancelable(false)
                                .setCancelable(false)

                                .setButtonDismiss(null)
                                .setButtonDoNotShowAgain(null)
                                .start();

                    }else {

                        startActivityMainDelay();

                    }

                }

                @Override
                public void onFailed(AppUpdaterError appUpdaterError) {

                }
            });

            appUpdaterUtils.start();




        }
    }

    public void dialogNoInternet() {
        Dialog dialog = new DialogUtils(this).buildDialogWarning(R.string.title_no_internet, R.string.msg_no_internet, R.string.TRY_AGAIN, R.string.CLOSE, R.drawable.img_no_internet, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retryOpenApplication();
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                finish();
            }
        });
        dialog.show();
    }

    // make a delay to start next activity
    private void retryOpenApplication() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startProcess();
            }
        }, 3000);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            for (String perm : permissions) {
                boolean rationale = shouldShowRequestPermissionRationale(perm);
                Tools.setNeveraskAgain(SplashActivity.this, !rationale);
            }
            on_permission_result = true;
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startActivityMainDelay() {

        if (isIsappOpenFirstTime(SplashActivity.this)){
            // Show splash screen for 2 seconds
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    setIsappOpenFirstTime(SplashActivity.this,false);

                    Intent i = new Intent(SplashActivity.this, IntroActivity.class);
                    startActivity(i);
                    finish(); // kill current activity
                }
            };
            new Timer().schedule(task, 3000);

        }else {
            // Show splash screen for 2 seconds

            if (Tools.isLogedIn(SplashActivity.this)){
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {


                        Tools.setLogedin(SplashActivity.this,true);
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish(); // kill current activity
                    }
                };
                new Timer().schedule(task, 3000);


            }else {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {

                        Tools.setLogedin(SplashActivity.this,false);
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish(); // kill current activity
                    }
                };
                new Timer().schedule(task, 3000);

            }

        }

    }

    public void dialogServerNotConnect() {
        Dialog dialog = new DialogUtils(this).buildDialogWarning(R.string.title_unable_connect, R.string.msg_unable_connect, R.string.TRY_AGAIN, R.string.CLOSE, R.drawable.img_no_connect, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retryOpenApplication();
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                finish();
            }
        });
        dialog.show();
    }

    private void checkAppVersion(Boolean info) {
        if (!info) {
//            dialogOutDate();
        } else {
            startActivityMainDelay();
        }
    }

    public boolean isIsappOpenFirstTime(Context context) {


        return sharedPreference.getSharedPreferenceBoolean(context,Constant.ISAPPOPENFIRSTTIME,true);
    }

    public void setIsappOpenFirstTime(Context context, boolean isappOpenFirstTime) {

        sharedPreference.setSharedPreferenceBoolean(context,Constant.ISAPPOPENFIRSTTIME,isappOpenFirstTime);
    }
}