package com.synram.morningbucket.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.CustomerDataUpdateResponse;
import com.synram.morningbucket.Modal.LoginResponse;
import com.synram.morningbucket.Modal.UserDataModal;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.Utilities.Tools;
import com.synram.morningbucket.Wedgit.OTPEditText;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {


    private static final int REQ_SEARCH_SOURCE_SELECT = 1;
    private static final String REGISTRATION_COMPLETE = "registrationComplete";
    private static final String PUSH_NOTIFICATION = "pushNotification";
    AwesomeValidation awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
    ConstraintLayout coordinator;
    FloatingActionButton register_btn, sendcode;
    private ViewFlipper viewFlipper;
    private OTPEditText etxtOne;
    private OTPEditText etxtTwo;
    private OTPEditText etxtThree;
    private OTPEditText etxtFour;
    private OTPEditText etxtFive;
    private EditText select_city;
    private OTPEditText etxtSix;
    private FirebaseAuth mAuth;
    private TextView timer, resend_btn;
    private SharedPreference sharedPreference;
    private EditText fname, lname, city, locality, pincode, email, block_no, landmark;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean isVerificationEnabled;

    private String otpCode;
    private LinearLayout llVerification;
    private TextView txtVerificationLabel;
    private EditText etxtPhone;
    private String TAG = "LoginActivity";
    private View.OnClickListener snackBarDismissOnClickListener;
    private CompositeDisposable compositeDisposable;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private int searchType;
    private String search_city;
    private String search_city_id;
    private String locationSelect;
    private ProgressDialog progressDialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        compositeDisposable = new CompositeDisposable();
        mAuth = FirebaseAuth.getInstance();

        sharedPreference = new SharedPreference(this);


//        FirebaseApp.initializeApp(this);
        initViews();


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
//                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();


                }
            }
        };

        displayFirebaseRegId();


    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        String regId = sharedPreference.getSharedPreferenceString(this, "regId", "");

        Log.e("TOKEN", "Firebase reg id: " + regId);


    }

    private void initViews() {


        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out);

        fname = findViewById(R.id.fname);

        email = findViewById(R.id.email);
        city = findViewById(R.id.city);
        pincode = findViewById(R.id.pin);
        locality = findViewById(R.id.locality);
        block_no = findViewById(R.id.bno);
        landmark = findViewById(R.id.landmark);


        awesomeValidation.addValidation(LoginActivity.this, R.id.fname, "^[a-zA-Z ]+$", R.string.err_fname);
        awesomeValidation.addValidation(LoginActivity.this, R.id.fname, RegexTemplate.NOT_EMPTY, R.string.err_fname_not_empty);


        awesomeValidation.addValidation(LoginActivity.this, R.id.city, RegexTemplate.NOT_EMPTY, R.string.err_city_not_empty);

//        awesomeValidation.addValidation(LoginActivity.this, R.id.locality, RegexTemplate.NOT_EMPTY, R.string.err_field_not_empty);

//        awesomeValidation.addValidation(LoginActivity.this, R.id.bno, RegexTemplate.NOT_EMPTY, R.string.err_field_not_empty);

//        awesomeValidation.addValidation(LoginActivity.this, R.id.pin, "^[1-9][0-9]{5}$", R.string.err_pin_not_valid);
//        awesomeValidation.addValidation(LoginActivity.this, R.id.pin, RegexTemplate.NOT_EMPTY, R.string.err_field_not_empty);


        awesomeValidation.addValidation(LoginActivity.this, R.id.email, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email);
        awesomeValidation.addValidation(LoginActivity.this, R.id.email, RegexTemplate.NOT_EMPTY, R.string.err_email_not_empty);


        searchType = Constant.SEARCH_CITY;
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper_registration);
        viewFlipper.setDisplayedChild(0);

        llVerification = (LinearLayout) findViewById(R.id.ll_registration_mobile_otp);
        txtVerificationLabel = (TextView) findViewById(R.id.txt_registration_mobile_otp_label);
        coordinator = findViewById(R.id.coordinator);
        sendcode = findViewById(R.id.send_code);
        register_btn = findViewById(R.id.register_btn);
        select_city = findViewById(R.id.select_city);


        etxtPhone = (EditText) findViewById(R.id.etxt_registration_phone);

        etxtOne = (OTPEditText) findViewById(R.id.etxt_registration_mobile_one);
        etxtTwo = (OTPEditText) findViewById(R.id.etxt_registration_mobile_two);
        etxtThree = (OTPEditText) findViewById(R.id.etxt_registration_mobile_three);
        etxtFour = (OTPEditText) findViewById(R.id.etxt_registration_mobile_four);
        etxtFive = (OTPEditText) findViewById(R.id.etxt_registration_mobile_five);
        etxtSix = (OTPEditText) findViewById(R.id.etxt_registration_mobile_six);
        timer = (TextView) findViewById(R.id.timer);
        resend_btn = (TextView) findViewById(R.id.resend_btn);


        viewFlipper.setDisplayedChild(0);
//


        setVerificationLayoutVisibility(false);


        etxtOne.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Integer textlength1 = etxtOne.getText().length();

                if (textlength1 >= 1) {
//                    etxtOne.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtTwo.requestFocus();
                } else {
//                    etxtOne.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
        });

        etxtTwo.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Integer textlength2 = etxtTwo.getText().length();

                if (textlength2 >= 1) {
//                    etxtTwo.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtThree.requestFocus();
                } else {
//                    etxtTwo.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
        });
        etxtThree.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Integer textlength3 = etxtThree.getText().length();

                if (textlength3 >= 1) {
//                    etxtThree.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtFour.requestFocus();
                } else {
//                    etxtThree.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
        });
        etxtFour.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength4 = etxtFour.getText().toString().length();

                if (textlength4 == 1) {
//                    etxtFour.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtFive.requestFocus();
                } else {
//                    etxtFour.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
        etxtFive.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength4 = etxtFive.getText().toString().length();

                if (textlength4 == 1) {
//                    etxtFive.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                    etxtSix.requestFocus();
                } else {
//                    etxtFive.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
        etxtSix.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength4 = etxtSix.getText().toString().length();

                if (textlength4 == 1) {
//                    etxtSix.setBackgroundResource(R.drawable.circle_white_with_app_edge);
                } else {
//                    etxtSix.setBackgroundResource(R.drawable.circle_white_with_gray_edge);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        etxtSix.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {

                int i = etxtSix.getText().toString().length();
                if (i == 0) {
                    etxtFive.setText("");
                    etxtFive.requestFocus();
                }
            }
        });
        etxtFive.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {

                int i = etxtFive.getText().toString().length();
                if (i == 0) {
                    etxtFour.setText("");
                    etxtFour.requestFocus();
                }
            }
        });
        etxtFour.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {

                int i = etxtFour.getText().toString().length();
                if (i == 0) {
                    etxtThree.setText("");
                    etxtThree.requestFocus();
                }
            }
        });

        etxtThree.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {

                int i = etxtThree.getText().toString().length();
                if (i == 0) {
                    etxtTwo.setText("");
                    etxtTwo.requestFocus();
                }
            }
        });

        etxtTwo.setOnDeleteKeyClick(new OTPEditText.OnDeleteKeyClick() {
            @Override
            public void onDeleteKeyClick(boolean isPressed) {

                int i = etxtTwo.getText().toString().length();
                if (i == 0) {
                    etxtOne.setText("");
                    etxtOne.requestFocus();
                }
            }
        });

        etxtSix.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    if (etxtOne.getText().toString().length() == 0) {
                        etxtOne.requestFocus();
                    } else if (etxtTwo.getText().toString().length() == 0) {
                        etxtTwo.requestFocus();
                    } else if (etxtThree.getText().toString().length() == 0) {
                        etxtThree.requestFocus();
                    } else if (etxtFour.getText().toString().length() == 0) {
                        etxtFour.requestFocus();
                    } else if (etxtFour.getText().toString().length() == 0) {
                        etxtFive.requestFocus();
                    }
                }
            }
        });

        etxtFive.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    if (etxtOne.getText().toString().length() == 0) {
                        etxtOne.requestFocus();
                    } else if (etxtTwo.getText().toString().length() == 0) {
                        etxtTwo.requestFocus();
                    } else if (etxtThree.getText().toString().length() == 0) {
                        etxtThree.requestFocus();
                    } else if (etxtFour.getText().toString().length() == 0) {
                        etxtFour.requestFocus();
                    }
                }
            }
        });

        etxtFour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    if (etxtOne.getText().toString().length() == 0) {
                        etxtOne.requestFocus();
                    } else if (etxtTwo.getText().toString().length() == 0) {
                        etxtTwo.requestFocus();
                    } else if (etxtThree.getText().toString().length() == 0) {
                        etxtThree.requestFocus();
                    }
                }
            }
        });

        etxtThree.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    if (etxtOne.getText().toString().length() == 0) {
                        etxtOne.requestFocus();
                    } else if (etxtTwo.getText().toString().length() == 0) {
                        etxtTwo.requestFocus();
                    }

                }
            }
        });

        etxtTwo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    if (etxtOne.getText().toString().length() == 0) {
                        etxtOne.requestFocus();
                    }
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                sendcode.setVisibility(View.GONE);
                register_btn.setVisibility(View.VISIBLE);


                signInWithPhoneAuthCredential(phoneAuthCredential);


            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onVerificationFailed(FirebaseException e) {

                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.i(TAG, "onVerificationFailed: " + e);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.i(TAG, "onVerificationFailed: " + e);
                }

                sendcode.setVisibility(View.VISIBLE);
                register_btn.setVisibility(View.GONE);

                /*Snackbar.make(coordinatorLayout, R.string.message_phone_verification_failed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
*/
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                sendcode.setVisibility(View.GONE);
                register_btn.setVisibility(View.VISIBLE);

                Snackbar.make(coordinator, getString(R.string.message_verification_code_sent_to),
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

                setVerificationLayoutVisibility(true);
//                swipeView.setRefreshing(false);

            }
        };


        sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (collectMobileNumber()) {

                    timer.setVisibility(View.VISIBLE);
                    resend_btn.setVisibility(View.VISIBLE);

                    initiatePhoneVerification(etxtPhone.getText().toString());

                } else {
                    timer.setVisibility(View.GONE);
                    resend_btn.setVisibility(View.GONE);
                }

            }
        });


        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (collectMobileNumber()) {


                    initiatePhoneVerification(etxtPhone.getText().toString());

                }
            }
        });

        select_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, SearchCitiesActivity.class);
                intent.putExtra("search_type", searchType);
                startActivityForResult(intent, REQ_SEARCH_SOURCE_SELECT);

            }
        });

    }

    private void setVerificationLayoutVisibility(boolean isVisible) {

        if (isVisible) {
            llVerification.setVisibility(View.VISIBLE);
            txtVerificationLabel.setVisibility(View.VISIBLE);
            etxtOne.requestFocus();
            isVerificationEnabled = true;
        } else {
            llVerification.setVisibility(View.GONE);
            txtVerificationLabel.setVisibility(View.GONE);
            etxtOne.setText("");
            etxtTwo.setText("");
            etxtThree.setText("");
            etxtFour.setText("");
            etxtFive.setText("");
            etxtSix.setText("");
            isVerificationEnabled = false;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private boolean collectMobileNumber() {


        if (etxtPhone.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(coordinator, getString(R.string.message_phone_number_is_required), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
            return false;
        } else if (!etxtPhone.getText().toString().trim().matches("^[0][1-9]\\d{9}$|^[1-9]\\d{9}$")) {
            Snackbar.make(coordinator, "Please enter valid mobile number", Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_dismiss), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
            return false;

        }
        return true;
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        swipeView.setRefreshing(true);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            swipeView.setRefreshing(false);
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            Log.i(TAG, "onComplete: " + new Gson().toJson(task));

//                            viewFlipper.setInAnimation(slideLeftIn);
//                            viewFlipper.setOutAnimation(slideLeftOut);
//                            getSupportActionBar().show();
//                            swipeView.setPadding(0, (int) mActionBarHeight, 0, 0);


                            login(etxtPhone.getText().toString());


                        } else {
//                            swipeView.setRefreshing(false);
                            // Sign in failed, display a message and update the UI

                            sendcode.setVisibility(View.VISIBLE);
                            register_btn.setVisibility(View.GONE);

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                                final Snackbar snackbar = Snackbar.make(coordinator, R.string.message_invalid_verification_code, Snackbar.LENGTH_LONG);
                                snackbar.setAction(R.string.btn_dismiss, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        snackbar.dismiss();
                                    }
                                }).show();

                            }
                        }
                    }
                });
    }


    public void onRegistrationMobileNumberSubmitClick(View view) {
        if (isVerificationEnabled) {
            otpCode = "" + etxtOne.getText().toString() + etxtTwo.getText().toString()
                    + etxtThree.getText().toString() + etxtFour.getText().toString()
                    + etxtFive.getText().toString() + etxtSix.getText().toString();

            if (!otpCode.equalsIgnoreCase("")) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
                signInWithPhoneAuthCredential(credential);
            } else {
                Snackbar.make(coordinator, getString(R.string.message_invalid_verification_code), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.btn_dismiss), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }

        } else {

            if (collectMobileNumber()) {
//            performPhoneRegistration();
                login(etxtPhone.getText().toString());
            }
        }
    }


    private void initiatePhoneVerification(String mobile_number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile_number,        // Phone number to verify
                2,                 // Timeout duration
                TimeUnit.MINUTES,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);


        Snackbar.make(coordinator, R.string.message_verification_code_sent_to, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
//        swipeView.setRefreshing(true);

        new CountDownTimer(120000, 1000) { // adjust the milli seconds here

            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            public void onTick(long millisUntilFinished) {
                timer.setText("" + String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
//                _tv.setText("done!");
            }
        }.start();

    }


    private void login(final String mobile_number) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.login(mobile_number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<LoginResponse>() {
                    @Override
                    public void onNext(LoginResponse value) {


                        Log.d("respo", new Gson().toJson(value));


                        if (!value.getError()) {

                            progressDialog.dismiss();
                            viewFlipper.setInAnimation(slideLeftIn);
                            viewFlipper.setOutAnimation(slideLeftOut);
                            viewFlipper.showNext();

                            SharedPreference.setSharedPreferenceString(LoginActivity.this, Constant.SELECTED_MOBILE_NO, mobile_number);


//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {
                            progressDialog.dismiss();

                            SharedPreference.setSharedPreferenceString(LoginActivity.this, Constant.SELECTED_MOBILE_NO, mobile_number);


                            viewFlipper.setInAnimation(slideLeftIn);
                            viewFlipper.setOutAnimation(slideLeftOut);
                            viewFlipper.showNext();
//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();

                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    private void update(String mobile_number, String name, String email, String address, String
            city, String locality, String bno, String landmark, String pincode, String token) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.updatecustomer(mobile_number, name, email, address, city, locality, bno, landmark, pincode, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<CustomerDataUpdateResponse>() {
                    @Override
                    public void onNext(CustomerDataUpdateResponse value) {


                        if (!value.getError()) {
                            progressDialog.dismiss();
                            Tools.setLogedin(LoginActivity.this, true);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                            finish();


//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    private void fetchuserdata(String mobile_number) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.fetchuserdata(mobile_number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<UserDataModal>() {


                    @Override
                    public void onNext(UserDataModal value) {

                        Log.d("Organic => ", new Gson().toJson(value));

                        if (!value.getError()) {

                            progressDialog.dismiss();
                            fname.setText(value.getUserData().get(0).getName());
                            email.setText(value.getUserData().get(0).getEmailId());
                            locality.setText(value.getUserData().get(0).getLocation());
                            block_no.setText(value.getUserData().get(0).getBuildingNo());
                            landmark.setText(value.getUserData().get(0).getLandMark());
                            pincode.setText(value.getUserData().get(0).getPincode());


                            sharedPreference.setSharedPreferenceString(LoginActivity.this, Constant.USER_ID, value.getUserData().get(0).getId());


                            city.setText(search_city);


//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    public void gotopersonaldetail(View view) {


        if (select_city.getText().toString().length() > 0) {
            viewFlipper.setInAnimation(slideLeftIn);
            viewFlipper.setOutAnimation(slideLeftOut);
            viewFlipper.showNext();


            fetchuserdata(etxtPhone.getText().toString());

        } else {


            Snackbar.make(coordinator, getString(R.string.selectcity),
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();


        }


    }


    public void gotoMain(View view) {


        String fname_str = fname.getText().toString();

        String email_str = email.getText().toString();
        String city_str = city.getText().toString();
        String locality_str = locality.getText().toString();
        String blockNo_str = block_no.getText().toString();
        String pincode_str = pincode.getText().toString();
        String landmark_str = landmark.getText().toString();
        String mobile_str = etxtPhone.getText().toString();


        if (awesomeValidation.validate()) {

//            viewFlipper.setInAnimation(slideLeftIn);
//            viewFlipper.setOutAnimation(slideLeftOut);
//            viewFlipper.showNext();


            String token = FirebaseInstanceId.getInstance().getToken();

            update(mobile_str, fname_str, email_str, locality_str, city_str, locality_str, blockNo_str, landmark_str, pincode_str, token);

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SEARCH_SOURCE_SELECT && resultCode == RESULT_OK) {

            search_city = (String) data.getStringExtra("city");

            search_city_id = (String) data.getStringExtra("city_id");
            locationSelect = (String) data.getStringExtra("locationSelect");


            SharedPreference.setSharedPreferenceString(this, Constant.SELECTED_CITY_ID, search_city_id);
            SharedPreference.setSharedPreferenceString(this, Constant.SELECTED_CITY_NAME, search_city);


            select_city.setText(search_city);
        }

    }


}
