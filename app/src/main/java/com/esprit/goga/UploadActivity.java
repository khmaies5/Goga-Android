package com.esprit.goga;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.esprit.android.fabprogresscircle.executor.ThreadExecutor;
import com.esprit.android.fabprogresscircle.interactor.MockAction;
import com.esprit.android.fabprogresscircle.interactor.MockActionCallback;
import com.esprit.android.fabprogresscircle.picasso.GrayscaleCircleTransform;
import com.esprit.android.util.APIClient;
import com.esprit.android.util.APIInterface;
import com.esprit.android.util.UserService;
import com.esprit.android.view.customfonts.MyEditText;
import com.esprit.goga.bean.User;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import gun0912.tedbottompicker.TedBottomPicker;
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

public class UploadActivity extends AppCompatActivity implements MockActionCallback, FABProgressListener {

    private FABProgressCircle fabProgressCircle;
    private boolean taskRunning;
    private TedBottomPicker pickerView;
    private ImageView image;
    String profilpicUrl;
    String token;
    String userid;
    User userlogged ;
    public static final String PREFS = "MyPrefs";
    Uri Fileuri;
    MyEditText title;




    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        userid = prefs.getString("userId", null);
        token = prefs.getString("token", null);
        setContentView(R.layout.activity_upload);
        Retrofit retrofit = APIClient.getClient();
        UserService userService = retrofit.create(UserService.class);


        initViews();
        loadAvatar(userService);
        attachListeners();
    }

    private void initViews() {
        fabProgressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
        image = (ImageView) findViewById(R.id.image_holder);
        title = (MyEditText) findViewById(R.id.upload_title);

       /* TedBottomPicker pickerView = new TedBottomPicker.Builder(this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        // here is selected uri
                        System.out.println("selected image uri "+uri);
                        Picasso.with(getApplicationContext())
                                .load(uri).error(R.drawable.error).into(image);

                    }
                })
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .create();

        pickerView.show(getSupportFragmentManager());*/

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TedBottomPicker pickerView = new TedBottomPicker.Builder(UploadActivity.this)
                        .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {
                                // here is selected uri
                                System.out.println("selected image uri "+uri);
                                Picasso.with(getApplicationContext())
                                        .load(uri).error(R.drawable.error).into(image);
                                Fileuri = uri;

                            }
                        })
                        .setPeekHeight(1600)
                        .showTitle(true)
                        .setCompleteButtonText("Done")
                        .setEmptySelectionText("No Select")
                        .create();

                pickerView.show(getSupportFragmentManager());

            }
        });

    }

    private void loadAvatar(UserService userService) {
       final ImageView avatarView = (ImageView) findViewById(R.id.avatar);

        Observable<User> getUserLogged = userService.getUserById(userid);

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

                profilpicUrl = user.getProfilePicture();
                System.out.println("profile pic "+profilpicUrl);

                Picasso.with(UploadActivity.this)
                        .load(R.drawable.profile)
                        .transform(new GrayscaleCircleTransform())
                        .into(avatarView);



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

    private void attachListeners() {
        fabProgressCircle.attachListener(this);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (!taskRunning) {
                    fabProgressCircle.show();
                    uploadPostImage();
                }
            }
        });
    }


    private void uploadPostImage(){

        if(Fileuri != null && !Fileuri.equals(Uri.EMPTY) && title.getText() != null && !title.getText().toString().equals(" ") && !title.getText().toString().isEmpty()){
            String filePath = getRealPathFromURIPath(Fileuri, UploadActivity.this);
            final File file = new File(filePath);
            Retrofit retrofit = APIClient.getClient();
            final APIInterface postService = retrofit.create(APIInterface.class);

            //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            Observable<Response<ResponseBody>> fileUpload = postService.uploadSingleFile(fileToUpload, filename);
            fileUpload.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Response<ResponseBody>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Response<ResponseBody> responseBodyResponse) {


                            System.out.println("upload response "+responseBodyResponse);
                            JsonParser parser = new JsonParser();
                            try {
                                JsonObject o = parser.parse(responseBodyResponse.body().string()).getAsJsonObject();
                                System.out.println(" file name "+file.getName().toString());
                                System.out.println("response upload"+o.getAsJsonObject("result").getAsJsonObject("fields").get("name").toString());
                                uploadPost(file.getName().toString());


                            } catch (IOException e) {
                                //e.printStackTrace();
                                System.out.println("error parser "+e.getMessage());
                            }

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {


                            System.out.println("error upload"+e.getMessage());



                        }

                        @Override
                        public void onComplete() {

                        }
                    });




        }else {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Make sure that title is not empty and your image is shown in the place holder", Toast.LENGTH_SHORT).show();

                }

            }, 1500);
        }


    }


    private void uploadPost(String filename){
        Retrofit retrofit = APIClient.getClient();
        final APIInterface postService = retrofit.create(APIInterface.class);
        Observable<Response<ResponseBody>> uploadPost = postService.uploadPost(title.getText().toString(),filename,userid);


        uploadPost.subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new Observer<Response<ResponseBody>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<ResponseBody> responseBodyResponse) {
                JsonParser parser = new JsonParser();
                try {
                    JsonObject o = parser.parse(responseBodyResponse.body().string()).getAsJsonObject();


                    System.out.println(" update post img name "+responseBodyResponse.code());
                    if (responseBodyResponse.code() == 200){

                        fabProgressCircle.beginFinalAnimation();

                       /* new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"updated Successfully", Toast.LENGTH_SHORT).show();
                            }

                        }, 1500);*/
                    }else{

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
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




    private void runMockInteractor() {
        ThreadExecutor executor = new ThreadExecutor();
        executor.run(new MockAction(this));
        taskRunning = true;
    }

    @Override public void onMockActionComplete() {
        taskRunning = false;
        fabProgressCircle.beginFinalAnimation();
        //fabProgressCircle.hide();
    }

    @Override public void onFABProgressAnimationEnd() {
        Snackbar.make(fabProgressCircle, R.string.cloud_upload_complete, Snackbar.LENGTH_LONG)
                .setAction("Back", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(UploadActivity.this,GogaMainActivity.class));

                    }
                })
                .show();


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