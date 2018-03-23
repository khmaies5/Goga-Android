package com.esprit.goga.bean;

import com.google.gson.annotations.SerializedName;

import net.tsz.afinal.annotation.sqlite.Id;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class FeedItem {
	
	@Id
	@SerializedName("id")
	private String id;
	@SerializedName("title")
	private String caption;

	private String images_small;
	@SerializedName("type")
	private String images_normal;
	private String images_large;
	private String link;
	private String next;
	@SerializedName("numberOfUpVotes")
	private int upvotes;
	@SerializedName("numberOfDownVotes")
	private int downvotes;
	@SerializedName("userId")
	private String userId;
	@SerializedName("upvotes")
	private List<Object> upVotesList;
	@SerializedName("downvotes")
	private List<Object> downVotesList;
	private String test;

	//public FeedItem(){
	//	super();
	//}

public FeedItem(){}

	public FeedItem(JSONObject jsonObject){
		try {

			this.id = jsonObject.getString("id");
			this.link = "https://goga-api.herokuapp.com/api/attachments/images/download/"+jsonObject.getString("type");//jsonObject.getString("link");
			this.caption = jsonObject.getString("title");
			// TODO rewrite votes mothod in api
			this.downvotes  = jsonObject.getInt("numberOfDownVotes");//jsonObject.getJSONObject("ratings").getString("count");
			this.upvotes  = jsonObject.getInt("numberOfUpVotes");
			//JSONObject imageJsonObject = jsonObject.getJSONObject("images");
			//this.images_small = imageJsonObject.getString("small");
			this.images_normal = "https://goga-api.herokuapp.com/api/attachments/images/download/"+jsonObject.getString("type");//imageJsonObject.getString("normal");
			this.images_large = "https://goga-api.herokuapp.com/api/attachments/images/download/"+jsonObject.getString("type");//imageJsonObject.getString("large");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

	public List<Object> getUpVotesList() {
		return upVotesList;
	}

	public void setUpVotesList(List<Object> upVotesList) {
		this.upVotesList = upVotesList;
	}

	public List<Object> getDownVotesList() {
		return downVotesList;
	}

	public void setDownVotesList(List<Object> downVotesList) {
		this.downVotesList = downVotesList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public String getCaption() {
		return caption;
	}

	public String getImages_small() {
		return images_small;
	}

	public String getImages_normal() {
		return images_normal;
	}

	public String getImages_large() {
		return images_large;
	}

	public String getLink() {
		return link;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}


	public void setId(String id) {
		this.id = id;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setImages_small(String images_small) {
		this.images_small = images_small;
	}

	public void setImages_normal(String images_normal) {
		this.images_normal = images_normal;
	}

	public void setImages_large(String images_large) {
		this.images_large = images_large;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getUpvotes() {
		return upvotes;
	}

	public void setUpvotes(int upvotes) {
		this.upvotes = upvotes;
	}

	public int getDownvotes() {
		return downvotes;
	}

	public void setDownvotes(int downvotes) {
		this.downvotes = downvotes;
	}

	@Override
	public String toString() {
		return "FeedItem{" +
				"id='" + id + '\'' +
				", caption='" + caption + '\'' +
				", images_normal='" + images_normal + '\'' +
				", upvotes=" + upvotes +
				", downvotes=" + downvotes +
				", userId='" + userId + '\'' +
				", upVotesList=" + upVotesList +
				", downVotesList=" + downVotesList +
				'}';
	}
}
