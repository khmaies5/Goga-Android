package com.esprit.goga;

import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.esprit.android.util.MxxSystemBarTintUtil;
import com.esprit.android.view.TypefaceSpan;

import gun0912.tedbottompicker.TedBottomPicker;
import io.paperdb.Paper;

public class UploadActivity extends AppCompatActivity {

    private TedBottomPicker pickerView;
    private ImageView image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MxxSystemBarTintUtil.setSystemBarTintColor(this);
        SpannableString spannableString = new SpannableString("Goga");
        String font = "LockScreen_Clock.ttf";
        Paper.init(getApplicationContext());
        spannableString.setSpan(new TypefaceSpan(font, Typeface.createFromAsset(getAssets(), font)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(spannableString);
        setSupportActionBar(toolbar);

      // pickerView = (TedBottomPicker) getSupportFragmentManager().findFragmentById(R.id.picker_view);

        image = (ImageView) findViewById(R.id.feed_item_image);

        TedBottomPicker pickerView = new TedBottomPicker.Builder(this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        // here is selected uri
                        System.out.println("selected image uri "+uri);
                        Glide.with(getApplicationContext())
                                .load(uri).override(200,200).error(R.drawable.error).into(image);

                    }
                })
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .create();

        pickerView.show(getSupportFragmentManager());

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TedBottomPicker pickerView = new TedBottomPicker.Builder(UploadActivity.this)
                        .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {
                                // here is selected uri
                                System.out.println("selected image uri "+uri);
                                Glide.with(getApplicationContext())
                                        .load(uri).override(200,200).error(R.drawable.error).into(image);

                            }
                        })
                        .setPeekHeight(1600)
                        .showTitle(false)
                        .setCompleteButtonText("Done")
                        .setEmptySelectionText("No Select")
                        .create();

                pickerView.show(getSupportFragmentManager());

            }
        });
    }
}
