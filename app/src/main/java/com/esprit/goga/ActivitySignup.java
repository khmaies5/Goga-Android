package com.esprit.goga;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.esprit.android.util.APIClient;
import com.esprit.android.util.UserService;
import com.esprit.android.view.customfonts.MyEditText;
import com.esprit.android.view.customfonts.MyTextView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ActivitySignup extends AppCompatActivity {

  ImageView signupback;
    MyEditText username,email,password,retype_password;
    MyTextView btnCreate;
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        signupback = (ImageView) findViewById(R.id.signupback);
        username = (MyEditText) findViewById(R.id.username);
        email = (MyEditText) findViewById(R.id.email);
        password = (MyEditText) findViewById(R.id.Password);
        retype_password = (MyEditText) findViewById(R.id.re_password);
        btnCreate = (MyTextView) findViewById(R.id.signin);


        signupback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent it = new Intent(ActivitySignup.this, LogInActivity.class);
                startActivity(it);

            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().isEmpty()|| password.getText().toString().isEmpty() || email.getText().toString().isEmpty()){
                    Snackbar snackbar = Snackbar.make(frameLayout,"Please verify all fields", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else if(!password.getText().toString().equals(retype_password.getText().toString())){
                    Snackbar snackbar = Snackbar.make(frameLayout,"Please Enter the same password", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else{
                    RegisterProcessWithRetrofit(username.getText().toString(),email.getText().toString(), password.getText().toString());

                }
            }
        });
    }



    private void ShowVerificationDialog(final String userId){


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.verification_popup, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final MyEditText userInputDialogEditText = (MyEditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here


                        if (userInputDialogEditText.getText().toString().isEmpty()){
                            Snackbar snackbar = Snackbar.make(frameLayout,"Please verify all fields", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }else {
                        Retrofit retrofit = APIClient.getClient();
                        UserService userService = retrofit.create(UserService.class);

                        Observable<Response<ResponseBody>> verifyUser = userService.verify(userId,userInputDialogEditText.getText().toString());


                            final ProgressDialog progressDoalog;
                            progressDoalog = new ProgressDialog(ActivitySignup.this);
                            progressDoalog.setMessage("Loading....");

                            verifyUser.observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Observer<Response<ResponseBody>>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {
                                        }

                                        @Override
                                        public void onNext(@NonNull Response<ResponseBody> responseBodyResponse) {


                                            JsonParser parser = new JsonParser();
                                            try {
                                                if(responseBodyResponse.code() != 204){

                                                    progressDoalog.show();
                                                    JsonObject o = parser.parse(responseBodyResponse.errorBody().string()).getAsJsonObject();
                                                    final String message = o.get("error").getAsJsonObject().get("message").getAsString();

                                                    new Handler().postDelayed(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            progressDoalog.dismiss();
                                                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

                                                        }

                                                    }, 8000);
                                                } else{

                                                    progressDoalog.show();
                                                    JsonObject o = parser.parse(responseBodyResponse.body().string()).getAsJsonObject();

                                                    final String userId = o.get("userId").toString();

                                                    new Handler().postDelayed(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            progressDoalog.dismiss();
                                                            Toast.makeText(getApplicationContext(),"Verified Successfully", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(ActivitySignup.this, LogInActivity.class);
                                        startActivity(i);

                                                        }

                                                    }, 1500);
                                                }



                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }



                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            Toast.makeText(getApplicationContext(),"No network connection", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });



                        }

                    }
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void RegisterProcessWithRetrofit(final String username, String email , String password) {

        Retrofit retrofit = APIClient.getClient();
        UserService userService = retrofit.create(UserService.class);

        Observable<Response<ResponseBody>> registerUser = userService.register(username,email,password);

        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(ActivitySignup.this);
        progressDoalog.setMessage("Loading....");

        registerUser.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Response<ResponseBody> responseBodyResponse) {


                        JsonParser parser = new JsonParser();
                        try {
                            if(responseBodyResponse.code() != 200){

                                progressDoalog.show();
                                JsonObject o = parser.parse(responseBodyResponse.errorBody().string()).getAsJsonObject();
                                final String message = o.get("error").getAsJsonObject().get("message").getAsString();

                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        progressDoalog.dismiss();
                                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

                                    }

                                }, 8000);
                            } else{

                                progressDoalog.show();
                                JsonObject o = parser.parse(responseBodyResponse.body().string()).getAsJsonObject();
                                System.out.println("response "+o);
                                final String userId = o.get("id").toString();

                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        progressDoalog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Registred Successfully", Toast.LENGTH_SHORT).show();
                                        /*Intent i = new Intent(ActivitySignup.this, LogInActivity.class);
                                        startActivity(i);*/
                                        ShowVerificationDialog(userId);

                                    }

                                }, 1500);
                            }



                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(getApplicationContext(),"No network connection", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    }

