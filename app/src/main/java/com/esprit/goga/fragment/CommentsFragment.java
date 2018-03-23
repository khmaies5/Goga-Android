package com.esprit.goga.fragment;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.esprit.android.blur.MxxBlurView;
import com.esprit.android.util.APIClient;
import com.esprit.android.util.APIInterface;
import com.esprit.android.util.MxxToastUtil;
import com.esprit.android.util.MxxUiUtil;
import com.esprit.android.util.SystemBarTintManager;
import com.esprit.android.view.MxxRefreshableListView;
import com.esprit.android.view.MxxScaleImageView;
import com.esprit.goga.GogaMainActivity;
import com.esprit.goga.MainActivity;
import com.esprit.goga.R;
import com.esprit.goga.bean.Comments;
import com.esprit.goga.manager.CommentsManager;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment implements View.OnClickListener {

    private MxxRefreshableListView mListView;
    private Context mContext;
    private CommentsManager mCommentsManager;
    private RelativeLayout rootView;
    private RelativeLayout mpostCommentView;

    private MxxBlurView blurView;
    private MxxScaleImageView mScaleImageView;
    private Button postComment;
    private EditText commentText;
    private String postId;
    private TextView comment_up,comment_down;
    private List<Comments> commentsList;
    APIInterface apiService;


    private boolean isClose;

    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_comments, null);
        rootView.setVisibility(View.INVISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);

        blurView = (MxxBlurView) rootView.findViewById(R.id.fragment_image_blurview);
       // rootView.addView(button);
                //  this.mScaleImageView = (MxxScaleImageView) rootView.findViewById(R.id.fragment_image_scaleimageview);
      //  mScaleImageView.setBlurView(blurView);
   //     SystemBarTintManager manager = new SystemBarTintManager(getActivity());
//        View view = (View) mScaleImageView.getParent();
  //      view.setPadding(0, manager.getConfig().getPixelInsetTop(true), 0, 0);
       // mListView = (MxxRefreshableListView)
       // initInsetTop(mListView);
        postComment =  rootView.findViewById(R.id.postComment);
        postComment.setOnClickListener(this);
        commentText = rootView.findViewById(R.id.commentText);
        mListView = rootView.findViewById(R.id.recycler_view);
        comment_down = rootView.findViewById(R.id.comment_text_down);
        comment_up = rootView.findViewById(R.id.comment_text_up);


        //setListViewHeightBasedOnChildren(mListView);
        initInsetTop(mListView);
        return rootView;
    }

    public void showComments(String id){

        mCommentsManager = getmCommentsManager(id);
        blurView.drawBlurOnce();

        final CardsAnimationAdapter adapter = new CardsAnimationAdapter( new CommentsAdapter());
//		CardsAnimationAdapter adapter = new CardsAnimationAdapter( quickAdapter);
        adapter.setAbsListView(mListView);
        mListView.setAdapter(adapter);

        mListView.setOnTopRefreshListener(new MxxRefreshableListView.OnTopRefreshListener() {
            @Override
            public void onStart() {}
            @Override
            public void onEnd() {}

            @Override
            public void onDoinBackground() {
                mCommentsManager.updateComments("");
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
                mCommentsManager.getNextComments("");
            }
        });

        mListView.post(new Runnable() {
            @Override
            public void run() {
                if(mCommentsManager.loadDbData()){
                    mListView.notifyDataSetChanged();
                }else{
                    mListView.startUpdateImmediate();
                    MxxToastUtil.showToast(getActivity(), "Please wait or try to refresh");
                }
            }
        });
        refresh();
        commentsList = mCommentsManager.getComments();
        rootView.setVisibility(View.VISIBLE);


    }


    public boolean canBack(){
        return rootView.getVisibility() == View.VISIBLE;
    }

    public void goBack(){
        if(!isClose){
            isClose = true;
            //mScaleImageView.startCloseScaleAnimation();
            rootView.setVisibility(View.GONE);
            ((GogaMainActivity)getActivity()).showCommentsFragment(false,"");
            getActivity().supportInvalidateOptionsMenu();
            isClose = false;
        }
    }


    private void initInsetTop(View rootView){
        SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        rootView.setPadding(0, config.getPixelInsetTop(false) + MxxUiUtil.dip2px(getActivity(), 48), config.getPixelInsetRight(), config.getPixelInsetBottom());
        rootView.requestLayout();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        isClose = false;

        mCommentsManager = getCommentsManager();
        if(mCommentsManager==null){
            System.out.println("comments manager null");
            return;
        }

        final CardsAnimationAdapter adapter = new CardsAnimationAdapter( new CommentsAdapter());
//		CardsAnimationAdapter adapter = new CardsAnimationAdapter( quickAdapter);
        adapter.setAbsListView(mListView);
        mListView.setAdapter(adapter);

        mListView.setOnTopRefreshListener(new MxxRefreshableListView.OnTopRefreshListener() {
            @Override
            public void onStart() {}
            @Override
            public void onEnd() {}

            @Override
            public void onDoinBackground() {
                mCommentsManager.updateComments("");
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
                mCommentsManager.getNextComments("");
            }
        });

        mListView.post(new Runnable() {
            @Override
            public void run() {
                if(mCommentsManager.loadDbData()){
                    mListView.notifyDataSetChanged();
                }else{
                    mListView.startUpdateImmediate();
                    MxxToastUtil.showToast(getActivity(), "Please wait or try to refresh");
                }
            }
        });
       // initScrollListener();


    }
    public void refresh(){
        mListView.startUpdateImmediate();
    }
    public void clicktest(){
        MxxToastUtil.showToast(getActivity(), "can't post nothing");
    }
    protected CommentsManager getCommentsManager(){
        return null;
    }


    protected CommentsManager getmCommentsManager(String postId){
        // int id = new Integer(postId);
        this.postId = postId;
        return new CommentsManager(postId,mContext);
    }



    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.postComment:
            {
                //post a comment
                if(commentText.getText().length() > 0){
                Call<Comments> call1 = apiService.postComment(commentText.getText().toString(),"5a91a1896d89b042d452a897",postId);
                call1.enqueue(new Callback<Comments>() {
                    @Override
                    public void onResponse(Call<Comments> call, Response<Comments> response) {

//                            mCommentsManager.postComment(commentText.getText().toString(),"5a91a1896d89b042d452a897",postId);
                            refresh();
                            mListView.notifyDataSetChanged();
                            MxxToastUtil.showToast(getActivity(), "comment posted");
                            commentText.setText("");


                    }

                    @Override
                    public void onFailure(Call<Comments> call, Throwable t) {
                        call.cancel();
                        MxxToastUtil.showToast(getActivity(), "network error");
                    }

                });
                } else MxxToastUtil.showToast(getActivity(), "can't post nothing");
                break;
            }
            case R.id.comment_text_down:
                {
                    MxxToastUtil.showToast(getActivity(), "downvote");
                    break;
                }
            case R.id.comment_text_up:
            {
                MxxToastUtil.showToast(getActivity(), "upvote");
                break;
            }


        }
    }



    private class CommentsAdapter extends BaseAdapter {
        private Typeface typeface;
        public CommentsAdapter(){
            super();
            typeface = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        }

/*
        public boolean canBack(){
            return rootView.getVisibility() == View.VISIBLE;
        }

        public void goBack(){
            if(!isClose){
                isClose = true;
                mScaleImageView.startCloseScaleAnimation();
            }
        }
        */
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(mCommentsManager == null){
                return 0;
            }else
                return mCommentsManager.getComments().size();
        }

        @Override
        public Comments getItem(int position) {
            // TODO Auto-generated method stub
            return mCommentsManager.getComments().get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            CommentsFragment.CommentsAdapter.ViewHolder holder = null;
            if(convertView==null){
                holder = new CommentsFragment.CommentsAdapter.ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_list_row, null);
//				ImageView loadingImageView = (ImageView) convertView.findViewById(R.id.feed_item_image_loading);
//				AnimationDrawable animationDrawable = (AnimationDrawable) loadingImageView.getDrawable();
//				animationDrawable.start();
//				((TextView)convertView.findViewById(R.id.feed_item_text_loading)).setTypeface(typeface);
                holder.userName = convertView.findViewById(R.id.userName);
                holder.userName.setTypeface(typeface);
                holder.commentText = convertView.findViewById(R.id.commentText);
                holder.commentText.setTypeface(typeface);
                holder.commentDate = convertView.findViewById(R.id.commentDate);
                holder.commentDate.setTypeface(typeface);
                holder.userImage = convertView.findViewById(R.id.userImage);
                holder.comment_down = convertView.findViewById(R.id.comment_text_down);
                holder.comment_down.setOnClickListener(CommentsFragment.this);
                holder.comment_up = convertView.findViewById(R.id.comment_text_up);
                holder.comment_up.setOnClickListener(CommentsFragment.this);

                convertView.setTag(holder);
            }else{
                holder = (CommentsFragment.CommentsAdapter.ViewHolder) convertView.getTag();
            }
            final Comments comment = mCommentsManager.getComments().get(position);
            if(comment!=null){
                if(comment.getText()!=null){
                    holder.commentText.setText(comment.getText());
                }else{
                    holder.commentText.setText("be the first to comment");
                }
                Glide.with(mContext)
                        .load(R.drawable.ic_launcher_9gag).override(200,200).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.userImage);
                //mFinalBitmap.display(holder.image, item.getImages_normal());
                //holder.image.setImageResource(R.drawable.esprit);
                //if(comment.getUser().getUsername() == null){
                holder.userName.setText("unknown");
                holder.comment_down.setText(""+comment.getNumberOfDislikes());
                holder.comment_up.setText(""+comment.getNumberOfLikes());
                //}else
                //holder.userName.setText(comment.getUser().getUsername());

                holder.commentDate.setText((comment.getCreatedDate()));
            }


            return convertView;
        }
        class ViewHolder{
            public TextView userName, commentText, commentDate, comment_down, comment_up;
            public ImageView userImage;
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
        public com.nineoldandroids.animation.Animator[] getAnimators(ViewGroup parent, View view) {
            return new com.nineoldandroids.animation.Animator[]{
                    com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "translationY", mTranslationY, 0),
                    com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "rotationX", mRotationX, 0)
            };
        }
    }


}
