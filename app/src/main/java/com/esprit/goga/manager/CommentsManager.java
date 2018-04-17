package com.esprit.goga.manager;

/**
 * Created by khmai on 06/03/2018.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.esprit.android.util.APIClient;
import com.esprit.android.util.APIInterface;
import com.esprit.goga.bean.Comments;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CommentsManager {

    static String URL = "https://goga-api.herokuapp.com/";
    private ArrayList<Comments> comments;
    private FinalDb finalDb;
    private String base_url;
    APIInterface apiService;
    private String next = "";
    String postId = "";



    public CommentsManager(String postId, Context context){
        this.comments = new ArrayList<Comments>();
        this.postId = postId;
        base_url = URL + postId;
        apiService = APIClient.getClient().create(APIInterface.class);
        next = "";
        finalDb = FinalDb.create(context, "postComments" + "_db", false);
       // System.out.println("this is called"+base_url);
    }
    CommentsManager(){}

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public boolean loadDbData(){
        try {
            this.comments.addAll(finalDb.findAll(Comments.class));
            if(comments.size()>0){
                return true;
            }

        }catch (Exception e){
            System.out.println("commetn manager excep "+e.getMessage());
        }
        return false;
    }

    public void updateComments(String postId){
        this.next = "";
        updateListInBackground(this.postId);

    }

    public void getNextComments(String postId){
        updateListInBackground(this.postId);
    }

    public void postComment(String text,String userId,String postId){
        postCommentInBackground( text, userId, postId);
    }


    private void postCommentInBackground(String text,String userId,String postId){


        Call<Comments> call = apiService.postComment(text,userId,postId);

        try {

            Response<Comments> response = call.execute();
            System.out.println("add comment response "+response);

        }catch (IOException e){
            System.out.println("comments list error "+e.getMessage());
        }

    }

    private void updateListInBackground(String postId) {
        Call<List<Comments>> call = apiService.getComments(this.postId);
        ArrayList<Comments> comments_tmp = new ArrayList<Comments>();
        // ResponseBody body ;

        ResponseBody body = null;
        try {
            Response<List<Comments>> response = call.execute();
            if (!response.isSuccessful())
                return;


            comments_tmp = (ArrayList<Comments>) response.body();
            if (this.next.equals("")) {
                finalDb.deleteByWhere(Comments.class, null);
            }

            for (int i = 0; i < response.body().size(); i++) {
                //item.setNext(next_tmp);

                System.out.println("comments " + response.body().get(i));

                comments_tmp.get(i).getUser().setProfilePicture("https://goga-api.herokuapp.com/api/attachments/profilepicture/download/" + response.body().get(i).getUser().getProfilePicture());
                //item.setNext(next_tmp);
                finalDb.save(comments_tmp.get(i));
            }
        } catch (IOException e) {
            System.out.println("comments list error " + e.getMessage());
        }


        if (this.next.equals("")) {
            comments.clear();
        }
        comments.addAll(comments_tmp);

    }




    private String getRequestUrl(){
        return base_url /*+ next*/;
    }


}
