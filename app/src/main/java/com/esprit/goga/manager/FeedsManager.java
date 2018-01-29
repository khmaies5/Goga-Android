package com.esprit.goga.manager;

import java.util.ArrayList;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.esprit.android.util.MxxHttpUtil;
import com.esprit.goga.bean.FeedItem;

public class FeedsManager {
	

	static String URL = "http://192.168.1.7:3000/api/posts/";
	public static final String TYPE_FRESH = "withRatings";
	public static final String TYPE_HOT = "withRatingsDesc";
	public static final String TYPE_TRENDING = "trending";
	private ArrayList<FeedItem> feedItems;
	private String next = "";
	private String base_url;
	private FinalDb finalDb;
	
	public FeedsManager(String type, Context context){
		this.feedItems = new ArrayList<FeedItem>();
		next = "";
		base_url = URL+type ;
		finalDb = FinalDb.create(context, type + "_db", false);
	}
	
	
	public ArrayList<FeedItem> getFeedItems() {
		return feedItems;
	}
	/**
	 * Loading finalDb stored data
	 * @return whether there is cache data
	 */
	public boolean loadDbData(){
		try {
			System.out.println("finaldb "+finalDb.findAll(FeedItem.class).get(0).getImages_normal());
			this.feedItems.addAll(finalDb.findAll(FeedItem.class));
			if (feedItems.size() > 0) {
				this.next = feedItems.get(feedItems.size() - 1).getNext();
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public void updateFirstPage(){
		this.next = "";
		updateListInBackground();
	}
	
	public void updateNextPage(){
		updateListInBackground();
	}


	/**
	 * retrieve data
	 * Must be in a non-UI thread
	 */
	private void updateListInBackground(){
		String json = MxxHttpUtil.get(getRequestUrl());

		if(TextUtils.isEmpty(json)) return;
		 ArrayList<FeedItem> feedItems_tmp = new ArrayList<FeedItem>();
		 String next_tmp = "";
//		Log.e("JSON", json);
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray feedList = jsonObject.getJSONArray("Posts");
			System.out.println("request url "+feedList);
			if(this.next.equals("")){
				finalDb.deleteByWhere(FeedItem.class, null);
			}
			next_tmp = "";//jsonObject.getJSONObject("paging").getString("next");
			for(int i=0;i<feedList.length();i++){
				FeedItem item = new FeedItem(feedList.getJSONObject(i));
				System.out.println("item "+item.getImages_normal()+" "+ item.getId());
				item.setCaption(feedList.getJSONObject(i).getString("title"));
				item.setImages_normal("http://192.168.1.7:3000/api/attachments/images/download/"+feedList.getJSONObject(i).getString("type"));
				//item.setNext(next_tmp);
				feedItems_tmp.add(item);
				finalDb.save(item);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("request url2 "+e.getMessage());

		}
		if(this.next.equals("")){
			feedItems.clear();
		}
		this.next = next_tmp;
		feedItems.addAll(feedItems_tmp);
	}
	/**
	 * Request link as http://infinigag-us.aws.af.cm/fresh/aAYLQ7p
	 * @return
	 */
	private String getRequestUrl(){
		return base_url /*+ next*/;
	}
			

}
