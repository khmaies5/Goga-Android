package com.esprit.android.util;

/**
 * Created by khmai on 07/03/2018.
 */


import com.esprit.goga.bean.Comments;
import com.esprit.goga.bean.FeedItem;
import com.esprit.goga.bean.User;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {


     @GET("/api/comments/{id}/find_post_comments")
     Call<List<Comments>> getComments(@Path("id") String postId);

     @GET("api/posts")
     Call<List<FeedItem>> getPosts(@Query("filter") String filter);

     @FormUrlEncoded
     @POST("/api/posts")
     Observable<Response<ResponseBody>> uploadPost(@Field("title") String title,@Field("type") String type,@Field("userId") String userId);

    @Multipart
    @POST("/api/attachments/images/upload")
    Observable<Response<ResponseBody>> uploadSingleFile(@Part MultipartBody.Part file, @Part("name") RequestBody name);//, @Path("iduser") String userid);


    @FormUrlEncoded
    @POST("/api/comments")
    Call<Comments> postComment(@Field("text") String text, @Field("userId") String userId, @Field("postId") String postId);


    @POST("/api/comments/{id}/upvote")
    Call<Comments> upvoteComment(@Path("id") String id,@Query("access_token") String token);

    @POST("/api/comments/{id}/downvote")
    Call<Comments> downvoteComment(@Path("id") String id,@Query("access_token") String token);

    @POST("/api/posts/{id}/upvote")
    Call<FeedItem> upvotePost(@Path("id") String id,@Query("access_token") String token);

    @POST("/api/posts/{id}/downvote")
    Call<FeedItem> downvotePost(@Path("id") String id, @Query("access_token") String token);



}
