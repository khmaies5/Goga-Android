package com.esprit.android.util;

/**
 * Created by khmai on 07/03/2018.
 */


import com.esprit.goga.bean.Comments;
import com.esprit.goga.bean.FeedItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {


     @GET("/api/posts/{id}/comments")
     Call<List<Comments>> getComments(@Path("id") String postId);

     @GET("api/posts")
     Call<List<FeedItem>> getPosts(@Query("filter") String filter);


}
