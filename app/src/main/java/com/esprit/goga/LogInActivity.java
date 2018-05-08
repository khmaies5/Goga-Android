package com.esprit.goga;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LogInActivity extends AppCompatActivity {


    ImageView signinback;
    MyTextView signinbtn,signUpBtn;
    MyEditText username, password;
    FrameLayout frameLayout;
    public static final String PREFS = "MyPrefs";

    boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //InstanceIdService in = new InstanceIdService();

      //  in.onTokenRefresh();

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

         isLoggedIn = prefs.getBoolean("isLoggedIn",false);

       if (isLoggedIn){

           startActivity(new Intent(LogInActivity.this,GogaMainActivity.class));

       } else {


           setContentView(R.layout.signin);

           frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
           signinback = (ImageView) findViewById(R.id.signinback);
           signinbtn = (MyTextView) findViewById(R.id.signin);
           signUpBtn = (MyTextView) findViewById(R.id.acc2);
           username = (MyEditText) findViewById(R.id.username);
           password = (MyEditText) findViewById(R.id.password);


           signUpBtn.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View view) {
                   Intent it = new Intent(LogInActivity.this, ActivitySignup.class);
                   startActivity(it);
               }
           });
           signinbtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                       Snackbar snackbar = Snackbar.make(frameLayout, "Please verify all fields", Snackbar.LENGTH_SHORT);
                       snackbar.show();
                   } else {
                       loginProcessWithRetrofit(username.getText().toString(), password.getText().toString());

                   }


               }
           });


           signinback.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   LogInActivity.super.onBackPressed();


               }
           });
       }
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
                            progressDoalog = new ProgressDialog(LogInActivity.this);
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
                                                            Intent i = new Intent(LogInActivity.this, GogaMainActivity.class);
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


private void SendVerifictionToken(final String userId){

    Retrofit retrofit = APIClient.getClient();
    UserService userService = retrofit.create(UserService.class);

    Observable<Response<ResponseBody>> sendverify = userService.sendVerification(userId);

    final ProgressDialog progressDoalog;
    progressDoalog = new ProgressDialog(LogInActivity.this);
    progressDoalog.setMessage("Loading....");

    sendverify.observeOn(AndroidSchedulers.mainThread())
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


                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    progressDoalog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Verification mail was sent", Toast.LENGTH_SHORT).show();
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


    private void loginProcessWithRetrofit(final String username, String password) {

        Retrofit retrofit = APIClient.getClient();
        UserService userService = retrofit.create(UserService.class);

        Observable<Response<ResponseBody>> authenticateUser = userService.authenticate(username, password);

        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(LogInActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();

        authenticateUser.observeOn(AndroidSchedulers.mainThread())
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

                                JsonObject jobject = parser.parse(responseBodyResponse.errorBody().string()).getAsJsonObject().get("error").getAsJsonObject();
                                if(jobject.get("code").getAsString().equals("LOGIN_FAILED_EMAIL_NOT_VERIFIED")){

                                   // .getAsJsonObject().get("userId")
                                    //System.out.println("iddd "+jobject.get("details").getAsJsonObject().get("userId").getAsString());
                                    String id = jobject.get("details").getAsJsonObject().get("userId").getAsString();
                                    SendVerifictionToken(id);
                                    ShowVerificationDialog(id);
                                }
                                progressDoalog.show();

                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        progressDoalog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Username or Password incorrect",Toast.LENGTH_SHORT).show();

                                    }

                                }, 1500);


                            }else {
                                System.out.println("parser " + responseBodyResponse);
                                JsonObject o = parser.parse(responseBodyResponse.body().string()).getAsJsonObject();
                                System.out.println("response"+o);
                                //System.out.println("ooooo"+o.get("error").getAsString());
                                progressDoalog.show();

                                //store user id in shared preferences
                                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                                editor.putString("userId", String.valueOf(o.get("userId").getAsString()));
                                editor.putString("token",String.valueOf(o.get("id").getAsString()));
                                editor.putBoolean("isLoggedIn",true);
                                editor.apply();

                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        progressDoalog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Logged Successfully",Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(LogInActivity.this, GogaMainActivity.class);
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

                        if (e instanceof HttpException) {
                            Toast.makeText(getApplicationContext(),"A problem occured",Toast.LENGTH_SHORT).show();
                            progressDoalog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(),"No network connection",Toast.LENGTH_SHORT).show();
                            progressDoalog.dismiss();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }
}
