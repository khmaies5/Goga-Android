package com.esprit.goga.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;

import android.content.Context;

import com.esprit.android.util.APIClient;
import com.esprit.android.util.APIInterface;
import com.esprit.goga.bean.FeedItem;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Response;

public class FeedsManager {
	

	static String URL = "https://goga-api.herokuapp.com/api/posts/";
	public static final String TYPE_FRESH = "{\"order\":\"datepublication DESC\"}";
	public static final String TYPE_HOT = "{\"order\":\"numberOfUpVotes DESC\"}";
	//public static final String TYPE_TRENDING = "trending";
	private ArrayList<FeedItem> feedItems;
	private String next = "";
	private String base_url;
	private String filter;
	private FinalDb finalDb;
	APIInterface apiService;
	
	public FeedsManager(String type, Context context){
		this.feedItems = new ArrayList<FeedItem>();
		next = "";
		filter = type;
		base_url = URL+type ;
		apiService = APIClient.getClient().create(APIInterface.class);


		//finalDb = FinalDb.create(context, type + "_db", false);
	}
	FeedsManager(){}
	
	public ArrayList<FeedItem> getFeedItems() {
		return feedItems;
	}
	/**
	 * Loading finalDb stored data
	 * @return whether there is cache data
	 */
	public boolean loadDbData(){
		try {
			this.feedItems.addAll(Paper.book().read(filter,new ArrayList<FeedItem>()));
			if (feedItems.size() > 0) {
				this.next = feedItems.get(feedItems.size() - 1).getNext();
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("loadDbData fmanager "+e.getMessage());
		}
		return false;
	}
	
	public void updateFirstPage(){
		this.next = "";
		updateListInBackground();
	}
	
	public void updateNextPage(){

		this.next = "";
		updateListInBackground();
	}





	/**
	 * retrieve data
	 * Must be in a non-UI thread
	 */
	private void updateListInBackground(){
		Call<List<FeedItem>> call = apiService.getPosts(this.filter);
		ArrayList<FeedItem> feedItems_tmp = new ArrayList<FeedItem>();

		try{
			Response<List<FeedItem>> response = call.execute();
			if(!response.isSuccessful())
				return;
			feedItems_tmp = (ArrayList<FeedItem>) response.body();
            System.out.println("post list "+response.code());

            if(this.next.equals("")){
				//finalDb.deleteByWhere(FeedItem.class, null);
				Paper.book().delete(filter);
				}


			for(int i=0;i<response.body().size();i++){
				//item.setNext(next_tmp);

				response.body().get(i).setImages_large("https://goga-api.herokuapp.com/api/attachments/images/download/"+response.body().get(i).getImages_normal());

				response.body().get(i).setImages_normal("https://goga-api.herokuapp.com/api/attachments/images/download/"+response.body().get(i).getImages_normal());
				response.body().get(i).setLink(response.body().get(i).getImages_normal());
				System.out.println("up votes "+response.body().get(i).getUpVotesList());

				//item.setNext(next_tmp);
				//finalDb.save(response.body().get(i));

			}
		} catch (IOException e){
			System.out.println("comments list error "+e.getMessage());
		}


		if(this.next.equals("")){
			feedItems.clear();
		}
		Paper.book().write(filter,feedItems_tmp);
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
