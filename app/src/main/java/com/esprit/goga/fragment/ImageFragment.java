package com.esprit.goga.fragment;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.frakbot.imageviewex.ImageViewEx;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.esprit.android.common.activity.MxxBrowserActivity;
import com.esprit.android.util.BitmapUtil;
import com.esprit.android.util.MxxDialogUtil;
import com.esprit.android.util.MxxFileUtil;
import com.esprit.android.util.MxxToastUtil;
import com.esprit.android.util.MxxUiUtil;
import com.esprit.android.util.SystemBarTintManager;
import com.esprit.android.view.ListViewScrollObserver;
import com.esprit.android.view.MxxRefreshableListView;
import com.esprit.android.view.MxxScaleImageView;
import com.esprit.android.youdaofanyi.MxxYoudaoFanyiManager;
import com.esprit.goga.GogaMainActivity;
import com.esprit.goga.bean.FeedItem;

import com.esprit.translate.MxxTranslateManager;
import com.esprit.android.blur.MxxBlurView;
import com.esprit.android.util.IntentUtil;
import com.esprit.android.util.MxxTextUtil;
import com.esprit.goga.MainActivity;
import com.esprit.goga.R;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

public class ImageFragment extends Fragment{
	
	private MxxScaleImageView mScaleImageView;
	private RelativeLayout rootView;
	private FeedItem mCurrentFeedItem;
	private MxxBlurView blurView;
    private RecyclerView mRecyclerView;
	private MxxRefreshableListView mListView;


	private Context mContext;


	private ProgressWheel mProgressWheel;
	private ImageViewEx mImageViewEx;
	private ImageView testImageView;
	private TextView mImageTitleTextView;
	private ObjectAnimator fadeInAnimator,fadeOutAnimator;
    private String postId;
	/**
	 * Used to identify whether this Fragment is being displayed, that is, if you are viewing a large page
	 */
	private boolean isClose;
	private MxxTranslateManager mTranslateManager;
	private MxxYoudaoFanyiManager mYoudaoFanyiManager;

	public void setmCurrentFeedItem(FeedItem mCurrentFeedItem) {
		this.mCurrentFeedItem = mCurrentFeedItem;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_image, null);
		rootView.setVisibility(View.INVISIBLE);
		mImageTitleTextView = (TextView) rootView.findViewById(R.id.fragment_image_title_textview); 
		((View)mImageTitleTextView.getParent()).setAlpha(0);
		mProgressWheel = (ProgressWheel) rootView.findViewById(R.id.fragment_image_progresswheel);
		mImageViewEx = (ImageViewEx) rootView.findViewById(R.id.fragment_image_imageViewex);
		testImageView = (ImageView) rootView.findViewById(R.id.fragment_image_imageViewex);
		mImageViewEx.setFillDirection(ImageViewEx.FillDirection.HORIZONTAL);

		blurView = (MxxBlurView) rootView.findViewById(R.id.fragment_image_blurview);

		this.mScaleImageView = (MxxScaleImageView) rootView.findViewById(R.id.fragment_image_scaleimageview);
		mScaleImageView.setBlurView(blurView);
		SystemBarTintManager manager = new SystemBarTintManager(getActivity());
		View view = (View) mScaleImageView.getParent();
		view.setPadding(0, manager.getConfig().getPixelInsetTop(true), 0, 0);
		mListView = rootView.findViewById(R.id.recycler_view);


        setListViewHeightBasedOnChildren(mListView);
		initInsetTop(mListView);


		return rootView;
	}

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(MxxRefreshableListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, MxxRefreshableListView.LayoutParams.WRAP_CONTENT));

            view.measure(View.MeasureSpec.makeMeasureSpec(desiredWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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
		isClose = false;
		mTranslateManager = new MxxTranslateManager(getActivity());
		mYoudaoFanyiManager = new MxxYoudaoFanyiManager(getActivity());
		mImageTitleTextView.setTypeface( Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf"));
//		LayoutTransition transition =new LayoutTransition();
		fadeInAnimator = ObjectAnimator.ofFloat(((View)mImageTitleTextView.getParent()), "alpha", 0f, 1f);
//		fadeInAnimator=ObjectAnimator.ofFloat(mImageTitleTextView, "translationY", -mImageTitleTextView.getHeight(), 0f);
		fadeInAnimator.setDuration(MxxScaleImageView.anim_duration/ 2);
		fadeInAnimator.setStartDelay(MxxScaleImageView.anim_duration / 2);
		fadeInAnimator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
//				mTranslateManager.translateNoDialog(mCurrentFeedItem.getCaption(), new MxxTranslateManager.TranslateListener() {
//					
//					@Override
//					public void onSuccess(String content, String result) {
//						mImageTitleTextView.setText(content + "\n" + result);
//					}
//					
//					@Override
//					public void onError() {
//						MxxToastUtil.showToast(getActivity(), "Translate error.");
//					}
//				});

				mImageTitleTextView.setText(mCurrentFeedItem.getCaption());
				System.out.println("image title "+mCurrentFeedItem.getCaption());

				/*mYoudaoFanyiManager.fanyi(mCurrentFeedItem.getCaption(), new YoudaoFanyiListener() {
					@Override
					public void onSuccess(YoudaoFanyiItem fanyiItem) {
						mImageTitleTextView.setText(fanyiItem.getTranslationContent(false));
					}
					
					@Override
					public void onError(String errMsg) {
						// TODO Auto-generated method stub
						mImageTitleTextView.setText(mCurrentFeedItem.getCaption());

					}
				},null);*/
			}
			@Override
			public void onAnimationRepeat(Animator animation) {}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {}
		});
		fadeOutAnimator=ObjectAnimator.ofFloat(((View)mImageTitleTextView.getParent()), "alpha", 1f, 0f);
		fadeOutAnimator.setDuration(MxxScaleImageView.anim_duration/ 2);





//		fadeOutAnimator.addListener(new AnimatorListener() {
//			
//			@Override
//			public void onAnimationStart(Animator animation) {}
//			@Override
//			public void onAnimationRepeat(Animator animation) {}
//			
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				mImageTitleTextView.setVisibility(View.GONE);
//			}
//			
//			@Override
//			public void onAnimationCancel(Animator animation) {}
//		});
//		transition.setAnimator(LayoutTransition.APPEARING, fadeInAnimator );
//		transition.setAnimator(LayoutTransition.DISAPPEARING, fadeOutAnimator);
//		((ViewGroup)mImageTitleTextView.getParent()).setLayoutTransition(transition);
		mScaleImageView.setImageViewListener(new MxxScaleImageView.ImageViewListener() {
			
			@Override
			public void onSingleTap() {
				// TODO Auto-generated method stub
//				mScaleImageView.resetScale();
				isClose = true;
				mScaleImageView.startCloseScaleAnimation();
				fadeOutAnimator.start();
			}
			
			@Override
			public void onScaleEnd() {
				// TODO Auto-generated method stub
				if(isClose){
					mScaleImageView.setImageDrawable(null);
					rootView.setVisibility(View.GONE);
					((GogaMainActivity)getActivity()).showImageFragment(null, false, null);
					getActivity().supportInvalidateOptionsMenu();
					isClose = false;
				}else{
					mScaleImageView.setTopCrop(false);
					mScaleImageView.initAttacher();

					System.out.println("feed item image frag "+postId);
					//setRecyclerView(mCurrentFeedItem.getId());
					//checkGif();
				}
			}
		});




//		mImageTitleTextView.setOnLongClickListener(new View.OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				// TODO Auto-generated method stub
//				showTitleTextViewTranslate();
//				return true;
//			}
//		});
		mImageTitleTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTitleTextViewTranslate(getActivity(), mCurrentFeedItem.getCaption(),mTranslateManager);
				//showTitleTextViewTranslate(getActivity(), mCurrentFeedItem.getCaption(),mYoudaoFanyiManager);
			}
		});
	}



    /*	public static void showTitleTextViewTranslate(final Context context, String content, final MxxYoudaoFanyiManager fanyiManager){
            String[] words = content.split(" ");
    //		ArrayList<String> strings = (ArrayList<String>) Arrays.asList(words);
            ArrayList<String> strings = new ArrayList<String>(words.length + 1);
            strings.add(content);
            for(String str : words){
                strings.add(str.replaceAll(",", ""));
            }
            Dialog dialog = MxxDialogUtil.creatListViewDialog(context, MxxTextUtil.getTypefaceSpannableString(context, "Select a text to translate", MxxTextUtil.Roboto_Light), strings, "Cancle", new MxxDialogUtil.MxxDialogListener() {
                @Override
                public void onRightBtnClick() {}
                @Override
                public void onListItemClick(int position, String string) {



                    fanyiManager.fanyi(string, new MxxYoudaoFanyiManager.YoudaoFanyiListener() {
                        @Override
                        public void onSuccess(YoudaoFanyiItem fanyiItem) {
                            MxxDialogUtil.creatConfirmDialog(context, MxxTextUtil.getTypefaceSpannableString(context, "Translation", MxxTextUtil.Roboto_Light), fanyiItem.getTranslationContent(true), "OK", null, true, true, null).show();
                        }

                        @Override
                        public void onError(String errMsg) {
                            MxxToastUtil.showToast(context, "Translate error. \n" + errMsg);
                        }
                    }, MxxDialogUtil.creatPorgressDialog(context, null, MxxTextUtil.getTypefaceSpannableString(context, "Translating...", MxxTextUtil.Roboto_Light, false), false, true,null));
                }
                @Override
                public void onLeftBtnClick() {}

                @Override
                public void onCancel() {}
                @Override
                public void onListItemLongClick(int position, String string) {
                    MxxTextUtil.copyString(context, string);
                }
            });
            dialog.show();
        }
        */
	public static void showTitleTextViewTranslate(final Context context, String content, final MxxTranslateManager translateManager){
		String[] words = content.split(" ");
//		ArrayList<String> strings = (ArrayList<String>) Arrays.asList(words);
		ArrayList<String> strings = new ArrayList<String>(words.length + 1);
		strings.add(content);
		for(String str : words){
			strings.add(str.replaceAll(",", ""));
		}
		Dialog dialog = MxxDialogUtil.creatListViewDialog(context, MxxTextUtil.getTypefaceSpannableString(context, "Select a text to translate", MxxTextUtil.Roboto_Light), strings, "Cancle", new MxxDialogUtil.MxxDialogListener() {
			@Override
			public void onRightBtnClick() {}
			@Override
			public void onListItemClick(int position, String string) {
				translateManager.showTranslationResultInDialog(string);
			}
			@Override
			public void onLeftBtnClick() {}
			
			@Override
			public void onCancel() {}
			@Override
			public void onListItemLongClick(int position, String string) {
				MxxTextUtil.copyString(context, string);
			}
		});
		dialog.show();
	}
	
	public void startScaleAnimation(ImageView smallImageView, FeedItem feedItem){
		this.postId = feedItem.getId();

		mImageTitleTextView.setText(feedItem.getCaption());
		rootView.setVisibility(View.VISIBLE);
//		mImageTitleTextView.setVisibility(View.VISIBLE);
		fadeInAnimator.start();
		//rootView.setHasBlured(false);
		//rootView.postInvalidate();
		blurView.drawBlurOnce();
		mScaleImageView.startScaleAnimation(smallImageView);
		getActivity().supportInvalidateOptionsMenu();
		mCurrentFeedItem = feedItem;

	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if(menu.findItem(R.id.fragment_image_action_weblink)!=null && rootView != null){
			menu.findItem(R.id.fragment_image_action_weblink).setVisible(rootView.getVisibility() == View.VISIBLE);
			menu.findItem(R.id.fragment_image_action_share).setVisible(rootView.getVisibility() == View.VISIBLE);
//			menu.findItem(R.id.fragment_image_action_copy).setVisible(rootView.getVisibility() == View.VISIBLE);
			menu.findItem(R.id.fragment_image_action_save).setVisible(rootView.getVisibility() == View.VISIBLE);
//			menu.findItem(R.id.fragment_image_action_translate).setVisible(rootView.getVisibility() == View.VISIBLE);
		}
		super.onPrepareOptionsMenu(menu);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.image_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub]
		if(item.getItemId()  == R.id.fragment_image_action_weblink){
			if(mCurrentFeedItem!=null){
				MxxBrowserActivity.startWithExtra(getActivity(), mCurrentFeedItem.getLink());
			}
			return true;
		}else if(item.getItemId() == R.id.fragment_image_action_share_all){
			Bitmap bitmap = BitmapUtil.drawableToBitmap(mScaleImageView.getDrawable());
			String chooserDialogTitleString = getResources().getString(R.string.menu_action_share);
			IntentUtil.shareBitmapWithText(getActivity(), bitmap, mCurrentFeedItem.getCaption(), chooserDialogTitleString);
			return true;
		}else if(item.getItemId() == R.id.fragment_image_action_share_image){
			Bitmap bitmap = BitmapUtil.drawableToBitmap(mScaleImageView.getDrawable());
			String chooserDialogTitleString = getResources().getString(R.string.menu_action_share);
			IntentUtil.shareBitmap(getActivity(), bitmap, chooserDialogTitleString);
			return true;
		}else if(item.getItemId() == R.id.fragment_image_action_share_text){
			String chooserDialogTitleString = getResources().getString(R.string.menu_action_share);
			IntentUtil.shareText(getActivity(), mCurrentFeedItem.getCaption(), chooserDialogTitleString);
			return true;
		}else if(item.getItemId() == R.id.fragment_image_action_copy){
			MxxTextUtil.copyTextViewString(mImageTitleTextView);
			return true;
		}
		else if(item.getItemId() == R.id.fragment_image_action_save){
			Bitmap bitmap = BitmapUtil.drawableToBitmap(mScaleImageView.getDrawable());
			String path = MxxFileUtil.getImagePath() + "/" + mCurrentFeedItem.getId() + ".jpg";
			if(new File(path).exists()){
				MxxToastUtil.showToast(getActivity(), "This image has already been saved.");
			}else{
//				ProgressDialog dialog = ProgressDialog.show(getActivity(), null, MxxTextUtil.getTypefaceSpannableString(getActivity(), "Saving...", MxxTextUtil.Roboto_Light, false));
				Dialog dialog = MxxDialogUtil.creatPorgressDialog(getActivity(), null, MxxTextUtil.getTypefaceSpannableString(getActivity(), "Saving...", MxxTextUtil.Roboto_Light, false), false, true,null);
				new BitmapUtil.SaveBitampTask(path, dialog).execute(bitmap);
			}
			return true;
		}else if(item.getItemId() == R.id.fragment_image_action_translate){
//			mTranslateManager.showTranslationResultInDialog( mCurrentFeedItem.getCaption());
//			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	private void showTranslationResult(String content ,String result){
//		String message = content + "\n\n" + result;
//		Dialog dialog = MxxDialogUtil.creatConfirmDialog(getActivity(), "Translation result",message, 
//				"OK", null, true, true, new MxxDialogUtil.MxxDialogListener() {
//					@Override
//					public void onRightBtnClick() {
//					}
//					
//					@Override
//					public void onLeftBtnClick() {
//					}
//					@Override
//					public void onCancel() {
//					}
//
//					@Override
//					public void onListItemClick(int position, String string) {
//					}
//
//					@Override
//					public void onListItemLongClick(int position, String string) {
//						// TODO Auto-generated method stub
//						
//					}
//				});
//		dialog.show();
//	}
	
	public boolean canBack(){
		return rootView.getVisibility() == View.VISIBLE;
	}
	
	public void goBack(){
		if(!isClose){
			isClose = true;
			mScaleImageView.startCloseScaleAnimation();
		}
	}
	
	public void checkGif(){
		if(!mCurrentFeedItem.getImages_large().endsWith(".gif")){
			return;
		}
		mProgressWheel.setVisibility(View.VISIBLE);
		mProgressWheel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File gifFile = new File(getDownloadPath(mCurrentFeedItem.getImages_large()));

				if(gifFile.exists()){
					loadGifInImageViewEx(mCurrentFeedItem.getImages_large());
				}else{
					downloadGif(mCurrentFeedItem.getImages_large());
				}
				
			}
		});
	}
	
	private void downloadGif(final String image_url){
		
		FinalHttp fh = new FinalHttp();
		// call the download method to start the download
		HttpHandler handler = fh.download (image_url, // Here is the download path
				getDownloadPathTmp (image_url), // This is the path to save to
				true, // true: breakpoint False false: keep point of resume (new download)
				new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {
						//textView.setText("Download progress" + current + "/" + count);
						//squareProgressBar.setProgress(current * 100 / count);
						
						mProgressWheel.setProgress((int) (current * 360 / count));
					}

					@Override
					public void onSuccess(File t) {
						Toast.makeText(getActivity(), "Loading completed", Toast.LENGTH_SHORT).show();
						new ReNameTask(image_url).execute(t);
					}
					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						super.onFailure(t, errorNo, strMsg);
						Toast.makeText(getActivity(), "Failed to load:" + t.toString() + "\n" + strMsg, Toast.LENGTH_SHORT).show();
					}

				});
	}
	private class ReNameTask extends AsyncTask<File, Void, Boolean>{
		
		private String image_url;
		
		ReNameTask(String image_url) {
			super();
			this.image_url = image_url;
		}

		@Override
		protected Boolean doInBackground(File... params) {
			// TODO Auto-generated method stub
			String oldFilePath = params[0].getAbsolutePath();
			String newFilePath = oldFilePath.substring(0, oldFilePath.length()-4);
			return params[0].renameTo(new File(newFilePath));
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result){
				if(!canBack()) return;
				loadGifInImageViewEx( image_url);
			}
		}
	}
	
	private void loadGifInImageViewEx(String image_url){
		/*try {
			File f = new File(getDownloadPath(image_url));
			//FileInputStream is = new FileInputStream(f);
			////////////////////

            long startTime = System.currentTimeMillis();

            Log.d("loadgif", "on do in background, url open connection");

            InputStream is = getResources().openRawResource(R.raw.g);
            Log.d("loadgif", "on do in background, url get input stream");
            BufferedInputStream bis = new BufferedInputStream(is);
            Log.d("loadgif", "on do in background, create buffered input stream");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Log.d("loadgif", "on do in background, create buffered array output stream");

            byte[] img = new byte[1024];

            int current = 0;

            Log.d("loadgif", "on do in background, write byte to baos");
            while ((current = bis.read()) != -1) {
                baos.write(current);
            }


            Log.d("loadgif", "on do in background, done write");

            Log.d("loadgif", "on do in background, create fos");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(baos.toByteArray());

            Log.d("loadgif", "on do in background, write to fos");
            fos.flush();

            fos.close();
            is.close();
            Log.d("loadgif", "on do in background, done write to fos");
			////////////////////////////
			mImageViewEx.setVisibility(View.VISIBLE);

			mImageViewEx.setSource(baos.toByteArray());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getActivity(), "loadGifInImageViewEx FileNotFoundException!!", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
            e.printStackTrace();
        }
*/
		//mImageViewEx.setImageURI(Uri.parse(image_url));
		System.out.println("image url "+image_url);
		Glide.with(rootView.getContext())
				.load(image_url).into(mImageViewEx);


    }
	
	private static String getDownloadPath(String image_url) {
		int start = image_url.lastIndexOf("/") + 1;
        System.out.println("get download path image frag "+MxxFileUtil.getDownloadPath() + "/" + image_url.substring(start));

        return MxxFileUtil.getDownloadPath() + "/" + image_url.substring(start);
	}
	private static String getDownloadPathTmp(String image_url) {
		int start = image_url.lastIndexOf("/") + 1;
		return MxxFileUtil.getDownloadPath() + "/" + image_url.substring(start) + ".tmp";
	}







}
