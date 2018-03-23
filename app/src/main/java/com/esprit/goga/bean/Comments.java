package com.esprit.goga.bean;

import com.google.gson.annotations.SerializedName;

import net.tsz.afinal.annotation.sqlite.Id;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by khmai on 06/03/2018.
 */

public class Comments {

    @Id
    @SerializedName("id")
    private String commentId;

    @SerializedName("text")
   private String text;


    @SerializedName("numberOfLikes")
    private int numberOfLikes ;
    @SerializedName("numberOfDislikes")
    private int numberOfDislikes;
    @SerializedName("likes")
    private List<Object> likes ;
    @SerializedName("dislikes")
    private List<Object> dislikes;
    @SerializedName("createdDate")
   private String createdDate;

  //  private String id;

    @SerializedName("userId")
    private String userId;
    @SerializedName("postId")
    private String postId;

    @SerializedName("user")
    private User user;


    public Comments() {
    }

    public Comments(JSONObject jsonObject){
        try{
        this.commentId = jsonObject.getString("id");
        this.text = jsonObject.getString("text");
        this.createdDate = jsonObject.getString("createdDate:");
        this.numberOfDislikes = jsonObject.getInt("numberOfDislikes");
        this.numberOfLikes = jsonObject.getInt("numberOfLikes");
        this.user = new User(jsonObject.getJSONObject("user"));
    }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Comments(String text, int numberOfLikes, int numberOfDislikes, List likes, List dislikes, String createdDate, String id, String userId, String postId) {
        this.text = text;
        this.numberOfLikes = numberOfLikes;
        this.numberOfDislikes = numberOfDislikes;
        this.likes = likes;
        this.dislikes = dislikes;
        this.createdDate = createdDate;
        this.commentId = id;
        this.userId = userId;
        this.postId = postId;
    }

    public Comments(String text, String userId, String postId) {
        this.text = text;
        this.userId = userId;
        this.postId = postId;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentId() {
        return commentId;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfDislikes() {
        return numberOfDislikes;
    }

    public void setNumberOfDislikes(int numberOfDislikes) {
        this.numberOfDislikes = numberOfDislikes;
    }

    public List<Object> getLikes() {
        return likes;
    }

    public void setLikes(List<Object> likes) {
        this.likes = likes;
    }

    public List<Object> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<Object> dislikes) {
        this.dislikes = dislikes;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }




}
