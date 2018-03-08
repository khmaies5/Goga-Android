package com.esprit.goga.fragment;

import com.esprit.goga.manager.CommentsManager;
import com.esprit.goga.manager.FeedsManager;


public class GagFragmentHot extends GagFragment {
	
	@Override
	protected FeedsManager getFeedsManager() {
		// TODO Auto-generated method stub
		return new FeedsManager(FeedsManager.TYPE_HOT, getActivity());
	}


}
