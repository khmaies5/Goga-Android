package com.esprit.android.util;



import com.esprit.goga.bean.User;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface UserService {


   /* @GET("/api/users/login")
    Call<User> authenticate(@Query("username") String username, @Query("password") String password);

    @POST("/api/user")
    Call<User> registration(@Query("email") String email,@Query("username") String username, @Query("password") String password);

    @POST("/api/users/logout")
    Call<Response> logout();

    @PUT("/api/users")
    Call<User> updateUser(@Query("email") String email, @Query("password") String password, @Query("profilepicture") String profilepicture);
*/

    @FormUrlEncoded
    @POST("/api/users/login")
    Observable<Response<ResponseBody>> authenticate(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/users")
    Observable<Response<ResponseBody>> register(@Field("username") String username, @Field("email") String email, @Field("password") String password);

    @PUT("updateprofile")
    Observable<Response<ResponseBody>> updateProfile(@Header("x-access-token") String token, @Body User user);

    @GET("api/users/{id}")
    Observable<User> getUserById(@Path("id") String userid);


    @Multipart
    @POST("updateprofilepic/{iduser}")
    Observable<Response<ResponseBody>> uploadSingleFile(@Part MultipartBody.Part file, @Part("name") RequestBody name, @Path("iduser") String userid);


    @POST("/api/users/logout")
    Observable<Response<ResponseBody>> logout();


    @GET("/api/users/confirm")
    Observable<Response<ResponseBody>> verify(@Query("uid") String uid, @Query("token") String token);


    @POST("/api/users/{userId}/verify")
    Observable<Response<ResponseBody>> sendVerification(@Path("userId") String userId);

}
