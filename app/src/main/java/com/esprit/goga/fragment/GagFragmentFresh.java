package com.esprit.goga.fragment;

import com.esprit.goga.bean.Comments;
import com.esprit.goga.manager.CommentsManager;
import com.esprit.goga.manager.FeedsManager;


public class GagFragmentFresh extends GagFragment {
	
	@Override
	protected FeedsManager getFeedsManager() {
		// TODO Auto-generated method stub
		return new FeedsManager(FeedsManager.TYPE_FRESH, getActivity());
	}


}
