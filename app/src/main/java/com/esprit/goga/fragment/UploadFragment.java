package com.esprit.goga.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.esprit.goga.GogaMainActivity;
import com.esprit.goga.R;

import gun0912.tedbottompicker.TedBottomPicker;

import static android.content.Context.MODE_PRIVATE;
import static com.esprit.goga.LogInActivity.PREFS;


public class UploadFragment extends Fragment {

    private RelativeLayout rootView;
    private boolean isClose;



    public UploadFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       /* SharedPreferences editor = mContext.getSharedPreferences(PREFS, MODE_PRIVATE);

        userId = editor.getString("userId",null);
        token = editor.getString("token",null);*/
        rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_upload, null);
        rootView.setVisibility(View.INVISIBLE);
        return rootView ;
    }



    @Override
    public void onStart() {

        super.onStart();
        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(this.getContext())
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        // here is selected uri
                        System.out.println("selected image uri "+uri);
                    }
                })
                .create();

        tedBottomPicker.show(((GogaMainActivity)getActivity()).getSupportFragmentManager());
    }

    public void showUploadFragment(){
        rootView.setVisibility(View.VISIBLE);
    }

    public boolean canBack(){
        return rootView.getVisibility() == View.VISIBLE;
    }

    public void goBack(){
        if(!isClose){
            isClose = true;
            //mScaleImageView.startCloseScaleAnimation();
            rootView.setVisibility(View.GONE);
            ((GogaMainActivity)getActivity()).showUploadFragment(false);
            getActivity().supportInvalidateOptionsMenu();
            isClose = false;
        }
    }






}
