package com.esprit.goga.manager;

/**
 * Created by khmai on 06/03/2018.
 */

import java.util.ArrayList;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.esprit.android.util.APIClient;
import com.esprit.android.util.APIInterface;
import com.esprit.goga.bean.Comments;

public class CommentsManager {

    static String URL = "https://goga-api.herokuapp.com/";
    private ArrayList<Comments> comments;
    private FinalDb finalDb;
    private String base_url;
    APIInterface apiService;


    public CommentsManager(String postId, Context context){
        this.comments = new ArrayList<Comments>();
        base_url = URL + postId;
        apiService = APIClient.getClient().create(APIInterface.class);
        finalDb = FinalDb.create(context, "postComments" + "_db", false);
        System.out.println("this is called"+base_url);
    }
    CommentsManager(){}

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public boolean loadDbData(String postId){
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
        updateListInBackground(postId);

    }



    /**
     * retrieve data
     * Must be in a non-UI thread
     */
    private void updateListInBackground(String postId){

     /*   Call<ListWrapper<Comments>> call = apiService.getComments(postId);
        try{
            Response<ListWrapper<Comments>> response = call.execute();
            System.out.println("response "+response);
        } catch (IOException e){
            System.out.println("comments list error "+e.getMessage());
        }
*/


        String json = "" ;//= MxxHttpUtil.get(URL+postId);
       // System.out.println("comment json "+json);

       // if(TextUtils.isEmpty(json)) return;
        ArrayList<Comments> comments_tmp = new ArrayList<Comments>();
        String next_tmp = "";
//		Log.e("JSON", json);
        try {
			JSONObject jsonObject = new JSONObject(json);
            JSONArray commentsList = jsonObject.getJSONArray("comments");

            System.out.println("comment request url "+commentsList);
            /*if(this.next.equals("")){
                finalDb.deleteByWhere(FeedItem.class, null);
            }*/
            next_tmp = "";//jsonObject.getJSONObject("paging").getString("next");
            for(int i=0;i<commentsList.length();i++){
                Comments item = new Comments(commentsList.getJSONObject(i));
                System.out.println("item "+item.getText()+" "+ item.getCommentId());
                //item.setNext(next_tmp);
                comments_tmp.add(item);
                finalDb.save(item);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println("request url2 "+e.getMessage());

            e.printStackTrace();

        }
       /* if(this.next.equals("")){
            feedItems.clear();
        }*/
       // this.next = next_tmp;
        comments.addAll(comments_tmp);
    }


    private String getRequestUrl(){
        return base_url /*+ next*/;
    }


}
