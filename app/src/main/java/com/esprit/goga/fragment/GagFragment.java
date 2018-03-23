package com.esprit.goga.fragment;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.esprit.android.util.APIClient;
import com.esprit.android.util.APIInterface;
import com.esprit.android.util.MxxToastUtil;
import com.esprit.android.view.ListViewScrollObserver;
import com.esprit.goga.GogaMainActivity;
import com.esprit.goga.bean.FeedItem;
import com.esprit.android.util.MxxUiUtil;
import com.esprit.android.util.SystemBarTintManager;
import com.esprit.android.view.MxxRefreshableListView;
import com.esprit.goga.MainActivity;
import com.esprit.goga.R;
import com.esprit.goga.manager.CommentsManager;
import com.esprit.goga.manager.FeedsManager;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GagFragment extends Fragment {
	
	private MxxRefreshableListView mListView;
	private Context mContext;
	private FeedsManager mFeedsManager;
	private List<FeedItem> posts;
	private FinalBitmap mFinalBitmap;
	public String userId = "5a91a1896d89b042d452a897";
	public String token = "1sfcwSTdBpK0SicjA5xUlI1sEoAvc46QwToVyU6jFJSgvy6g8DoM70xEi0nAOx38";
    APIInterface apiService;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mListView = (MxxRefreshableListView) inflater.inflate(R.layout.mxx_base_refreshable_listview, null);
		initInsetTop(mListView);
        apiService = APIClient.getClient().create(APIInterface.class);

        return mListView;
	}
	
	
	
	private void initInsetTop(View rootView){
		SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());  
		SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();  
		rootView.setPadding(0, config.getPixelInsetTop(true) + MxxUiUtil.dip2px(getActivity(), 48), config.getPixelInsetRight(), config.getPixelInsetBottom());
		rootView.requestLayout();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		mFeedsManager = getFeedsManager();
		if(mFeedsManager==null){
			System.out.println("feeds manager null");
			return;}
		mFinalBitmap = ((GogaMainActivity)getActivity()).getFinalBitmap();
//		QuickAdapter<FeedItem> quickAdapter = new QuickAdapter<FeedItem>(mContext, R.layout.listitem_feed, mFeedsManager.getFeedItems()) {
//			
//			@Override
//			protected void convert(BaseAdapterHelper helper, FeedItem feedItem) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
		
		final CardsAnimationAdapter adapter = new CardsAnimationAdapter( new FeedsAdapter());
//		CardsAnimationAdapter adapter = new CardsAnimationAdapter( quickAdapter);
		adapter.setAbsListView(mListView);
		mListView.setAdapter(adapter); 
		
		mListView.setOnTopRefreshListener(new MxxRefreshableListView.OnTopRefreshListener() {
			@Override
			public void onStart() {}
			@Override
			public void onEnd() {}
			
			@Override
			public void onDoinBackground()
            {
				mFeedsManager.updateFirstPage();
                if(mFeedsManager.getFeedItems() != null){
                    posts = mFeedsManager.getFeedItems();

                }
            }
		});
		mListView.setOnBottomRefreshListener(new MxxRefreshableListView.OnBottomRefreshListener() {
			@Override
			public void onStart() {}
			@Override
			public void onEnd() {
				adapter.setShouldAnimateFromPosition(mListView.getLastVisiblePosition());
			}
			
			@Override
			public void onDoinBackground() {

				mFeedsManager.updateNextPage();
				if(mFeedsManager.getFeedItems() != null){
					posts = mFeedsManager.getFeedItems();

				}
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View convertView, int position,
					long arg3) {
				// TODO Auto-generated method stub
//				position = position - 1;//����һ��header
//				final FeedItem  item = mFeedsManager.getFeedItems().get(position);
//				Log.e("TEMP", item.getImages_large());
//				ImageViewerActivity.startWithData(getActivity(), item);
				final ImageView imageView = (ImageView) convertView.findViewById(R.id.feed_item_image);
				if(imageView.getDrawable()==null || imageView.getDrawable().getIntrinsicWidth() ==0){
					MxxToastUtil.showToast(getActivity(), "Please wait...");
					return;
				}
				//CommentsManager mCom = new CommentsManager(mFeedsManager.getFeedItems().get(position - 1).getId(),getActivity());
				((GogaMainActivity)getActivity()).showImageFragment(imageView,true,mFeedsManager.getFeedItems().get(position - 1));

			}
		});

		mListView.post(new Runnable() {
			@Override
			public void run() {
				if(mFeedsManager.loadDbData()){
					mListView.notifyDataSetChanged();
				}else{
					mListView.startUpdateImmediate();
                    MxxToastUtil.showToast(getActivity(), "Please wait or try to refresh");
				}
			}
		});
		initScrollListener();
	}



	public List getLikeList(){
        List<List<String>> likeList = new ArrayList<>();

        if(mFeedsManager.getFeedItems() != null){
            posts = mFeedsManager.getFeedItems();
            int i;
            for(i = 0;i<posts.size();i++){
            //likeList.add(posts.get(i).getUpVotesList());
            }

            return likeList;
        }
        return null;

    }

	private void initScrollListener(){
		final int max_tranY = MxxUiUtil.dip2px(mContext, 48);
		final View tabview = ((GogaMainActivity)getActivity()).getTabStripLayout();
		ListViewScrollObserver observer = new ListViewScrollObserver(mListView);
        observer.setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
			
			@Override
			public void onScrollUpDownChanged(int delta, int scrollPosition,
					boolean exact) {
				// TODO Auto-generated method stub
				if(exact){
					float tran_y = tabview.getTranslationY() + delta;
					if(tran_y >= 0){
						tabview.setTranslationY(0);
					}else if(tran_y < -max_tranY){
						tabview.setTranslationY(-max_tranY);
					}else{
						tabview.setTranslationY(tran_y);
					}
				}
				
			}
			
			@Override
			public void onScrollIdle() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void refresh(){

		mListView.startUpdateImmediate();
		if(mFeedsManager.getFeedItems() != null){
			posts = mFeedsManager.getFeedItems();

		}
	}
	
	protected FeedsManager getFeedsManager(){
		return null;
	}

	private class FeedsAdapter extends BaseAdapter{
		private Typeface typeface;
        ViewHolder holder = null;

        public FeedsAdapter(){
			super();
			typeface = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mFeedsManager.getFeedItems().size();
		}

		@Override
		public FeedItem getItem(int position) {
			// TODO Auto-generated method stub
			return mFeedsManager.getFeedItems().get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
        protected CommentsManager getCommentsManager(String postId){
          // int id = new Integer(postId);
            return new CommentsManager(postId,mContext);
        }
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_feed, null);
//				ImageView loadingImageView = (ImageView) convertView.findViewById(R.id.feed_item_image_loading);
//				AnimationDrawable animationDrawable = (AnimationDrawable) loadingImageView.getDrawable();
//				animationDrawable.start();
				((TextView)convertView.findViewById(R.id.feed_item_text_loading)).setTypeface(typeface);
				holder.title = (TextView) convertView.findViewById(R.id.feed_item_title);
				holder.title.setTypeface(typeface);
				holder.info = (TextView) convertView.findViewById(R.id.feed_item_text_info);
				holder.info.setTypeface(typeface);
				holder.down = convertView.findViewById(R.id.feed_item_text_down);
				holder.down.setTypeface(typeface);
				holder.comments = convertView.findViewById(R.id.feed_item_comments);
				holder.comments.setTypeface(typeface);
				holder.image = (ImageView) convertView.findViewById(R.id.feed_item_image);
				convertView.setTag(holder);
				holder.comments.setOnClickListener(new commentViewClickListene(position) {

					@Override
					public void onClick(View view) {
					    getCommentsManager(mFeedsManager.getFeedItems().get(position).getId());
                        ((GogaMainActivity)getActivity()).showCommentsFragment(true,mFeedsManager.getFeedItems().get(position).getId());

                        MxxToastUtil.showToast(getActivity(), "comments clicked "+mFeedsManager.getFeedItems().get(position).getId());
					}
				});
				holder.info.setOnClickListener(new commentViewClickListene(position) {

                    @Override
                    public void onClick(View view) {

//post a comment
                        System.out.println("upvote list "+mFeedsManager.getFeedItems().get(position).getUpVotesList());
                        if(mFeedsManager.getFeedItems() != null){
                        if (!mFeedsManager.getFeedItems().get(position).getUpVotesList().contains(userId)) {

                            Call<FeedItem> call1 = apiService.upvotePost(mFeedsManager.getFeedItems().get(position).getId(),token);
                            call1.enqueue(new Callback<FeedItem>() {
                                @Override
                                public void onResponse(Call<FeedItem> call, Response<FeedItem> response) {
//                            mCommentsManager.postComment(commentText.getText().toString(),"5a91a1896d89b042d452a897",postId);
                                    // refresh();
                                    // mListView.notifyDataSetChanged();
                                    mFeedsManager.getFeedItems().get(position).setUpvotes(+1);
                                    holder.info.setText("" + mFeedsManager.getFeedItems().get(position).getUpvotes());
                                    holder.info.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_up_bold, 0, 0, 0);


                                }

                                @Override
                                public void onFailure(Call<FeedItem> call, Throwable t) {
                                    call.cancel();
                                    MxxToastUtil.showToast(getActivity(), "network error");
                                }
                            });
                        } else MxxToastUtil.showToast(getActivity(), "already upvoted");
                    }


                        //MxxToastUtil.showToast(getActivity(), "like clicked "+mFeedsManager.getFeedItems().get(position).getId());
                    }
                });
				holder.down.setOnClickListener(new commentViewClickListene(position){
					@Override
					public void onClick(View view) {

//post a comment
						System.out.println("downvote list "+mFeedsManager.getFeedItems().get(position).getDownVotesList());
						if(mFeedsManager.getFeedItems() != null){
							if (!mFeedsManager.getFeedItems().get(position).getDownVotesList().contains(userId)) {

								Call<FeedItem> call1 = apiService.downvotePost(mFeedsManager.getFeedItems().get(position).getId(),token);
								call1.enqueue(new Callback<FeedItem>() {
									@Override
									public void onResponse(Call<FeedItem> call, Response<FeedItem> response) {
//                            mCommentsManager.postComment(commentText.getText().toString(),"5a91a1896d89b042d452a897",postId);
										// refresh();
										// mListView.notifyDataSetChanged();
										mFeedsManager.getFeedItems().get(position).setDownvotes(+1);
										mFeedsManager.getFeedItems().get(position).setUpvotes(-1);
										holder.down.setText("" + mFeedsManager.getFeedItems().get(position).getDownvotes());
										holder.down.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_down_bold, 0, 0, 0);
										holder.info.setText("" + mFeedsManager.getFeedItems().get(position).getUpvotes());
										holder.info.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_up_not_clicked, 0, 0, 0);


									}

									@Override
									public void onFailure(Call<FeedItem> call, Throwable t) {
										call.cancel();
										MxxToastUtil.showToast(getActivity(), "network error");
									}
								});
							} else MxxToastUtil.showToast(getActivity(), "already downvoted");
						}


						//MxxToastUtil.showToast(getActivity(), "like clicked "+mFeedsManager.getFeedItems().get(position).getId());
					}
				});
			}else{
				holder = (ViewHolder) convertView.getTag();
			}

			final FeedItem item = mFeedsManager.getFeedItems().get(position);
			if(item!=null){
				if(item.getCaption()!=null){
					holder.title.setText(item.getCaption());
				}else{
					holder.title.setText("unknown caption");
				}
				Glide.with(mContext)
						.load(item.getImages_normal()).override(200,200).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.image);
				//mFinalBitmap.display(holder.image, item.getImages_normal());
				//holder.image.setImageResource(R.drawable.esprit);
			//	System.out.println("items "+item.toString());
				if(item.getUpVotesList() != null) {
                    if (item.getUpVotesList().contains(userId)) {
						System.out.println("upvote list "+item.getUpVotesList().contains(userId));
                        holder.info.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_up_bold, 0, 0, 0);
                    }
                }
                if(item.getDownVotesList() != null) {
                    if (item.getDownVotesList().contains(userId)) {

						System.out.println("donwvote list "+item.getId());
						holder.down.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_down_bold, 0, 0, 0);
                    }
                }
				holder.info.setText(""+item.getUpvotes());
				holder.down.setText(""+item.getDownvotes());
				holder.comments.setText("comment");
			}
			
			
			return convertView;
		}
		class ViewHolder{
			public TextView title;
			public TextView info,down,comments;
			public ImageView image;
		}
		
	}
	class CardsAnimationAdapter extends AnimationAdapter {
	    private float mTranslationY = 400;

	    private float mRotationX = 15;

	    private long mDuration = 400;

	    public CardsAnimationAdapter(BaseAdapter baseAdapter) {
	        super(baseAdapter);
	    }

	    @Override
	    protected long getAnimationDelayMillis() {
	        return 30;
	    }

	    @Override
	    protected long getAnimationDurationMillis() {
	        return mDuration;
	    }

	    @Override
	    public Animator[] getAnimators(ViewGroup parent, View view) {
	        return new Animator[]{
	                ObjectAnimator.ofFloat(view, "translationY", mTranslationY, 0),
	                ObjectAnimator.ofFloat(view, "rotationX", mRotationX, 0)
	        };
	    }
	}

	private class commentViewClickListene implements View.OnClickListener {
		int position;
		public commentViewClickListene( int pos)
		{
			this.position = pos;
		}

		public void onClick(View v) {

			System.out.println("thisssssssssssssssssssssss issssssssssssssssssss spartaaaaaaaaaaaaaa");

		}
	}


}
