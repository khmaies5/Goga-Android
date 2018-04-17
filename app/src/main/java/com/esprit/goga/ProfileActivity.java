package com.esprit.goga;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.esprit.android.util.APIClient;
import com.esprit.android.util.UserService;
import com.esprit.android.view.customfonts.MyEditText;
import com.esprit.goga.bean.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;



import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    ImageView backbtn;
    public static final String PREFS = "MyPrefs";
    MyEditText username,email,firstname,lastname;
    EditText adress;
    TextView btnupload,savebtn;
    CircleImageView profilepic;
    String profilpicUrl;
    String token;
    String userid;
    User userlogged ;
    private static final int REQUEST_GALLERY_CODE = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        backbtn = (ImageView) findViewById(R.id.pro_backbtn);
        profilepic = (CircleImageView) findViewById(R.id.profilepic);
        username = (MyEditText) findViewById(R.id.username);
        email = (MyEditText) findViewById(R.id.email);
        firstname = (MyEditText) findViewById(R.id.firstname);
        lastname = (MyEditText) findViewById(R.id.lastname);
        adress = (EditText) findViewById(R.id.adress);
        btnupload = (TextView) findViewById(R.id.uploadbtn);
        savebtn = (TextView) findViewById(R.id.savebtn);


        toolbar = (Toolbar) findViewById(R.id.proftoolbar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //get userid from shared pref
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        userid = prefs.getString("iduser", null);
        token = prefs.getString("idToken", null);


        Retrofit retrofit = APIClient.getClient();
        UserService userService = retrofit.create(UserService.class);

       loadUserData(userService);


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setUsername(username.getText().toString());
                user.setEmail(email.getText().toString());
              /*  user.setFirstname(firstname.getText().toString());
                user.setLastname(lastname.getText().toString());
                user.setAddress(adress.getText().toString());*/
                user.setProfilePicture(profilpicUrl);
                updateProfile(user);
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissions();
                    Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                    openGalleryIntent.setType("image/*");
                    startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
                }


            }
        });
    }

    private void loadUserData(UserService userService) {
        Observable<User> getUserLogged = userService.getUserById(userid.split("\"")[1]);

       /* final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(ActivitySignup.this);
        progressDoalog.setMessage("Loading....");*/

        getUserLogged.subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new Observer<User>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull User user) {
                System.out.println(user);
                userlogged = user;
                username.setText(user.getUsername());
               /* adress.setText(user.getAddress());
                firstname.setText(user.getFirstname());
                lastname.setText(user.getLastname());*/
                email.setText(user.getEmail());
                profilpicUrl = user.getProfilePicture();
                Picasso.with(ProfileActivity.this).load(user.getProfilePicture()).into(profilepic);


            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();

            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
           Uri uri = data.getData();
                String filePath = getRealPathFromURIPath(uri, ProfileActivity.this);
                File file = new File(filePath);
            Retrofit retrofit = APIClient.getClient();
            final UserService userService = retrofit.create(UserService.class);

            //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                Observable<Response<ResponseBody>> fileUpload = userService.uploadSingleFile(fileToUpload, filename,userid.split("\"")[1]);
                fileUpload.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Response<ResponseBody>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Response<ResponseBody> responseBodyResponse) {

                            Picasso.with(ProfileActivity.this).load(userlogged.getProfilePicture()).into(profilepic);


                            JsonParser parser = new JsonParser();
                            try {
                                JsonObject o = parser.parse(responseBodyResponse.body().string()).getAsJsonObject();
                                System.out.println("response"+o.toString());
                                loadUserData(userService);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {


                            System.out.println("error"+e.getMessage());



                        }

                        @Override
                        public void onComplete() {

                        }
                    });




        }else{

            System.out.println("image not picked");
        }


    }

    private void checkPermissions(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);

        }

    }


    private void updateProfile(User user) {
        Retrofit retrofit = APIClient.getClient();
        UserService userService = retrofit.create(UserService.class);

        Observable<Response<ResponseBody>> updateProfile = userService.updateProfile(token.split("\"")[1],user);

        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(ProfileActivity.this);
        progressDoalog.setMessage("Loading....");

        updateProfile.subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new Observer<Response<ResponseBody>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<ResponseBody> responseBodyResponse) {
                JsonParser parser = new JsonParser();
                try {
                    JsonObject o = parser.parse(responseBodyResponse.body().string()).getAsJsonObject();

                    if (o.get("success").getAsString().equals("true")){
                        progressDoalog.show();

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                progressDoalog.dismiss();
                                Toast.makeText(getApplicationContext(),"updated Successfully", Toast.LENGTH_SHORT).show();
                            }

                        }, 1500);
                    }else{
                        progressDoalog.show();

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                progressDoalog.dismiss();
                                Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();

                            }

                        }, 1500);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        } );


}

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }




}
