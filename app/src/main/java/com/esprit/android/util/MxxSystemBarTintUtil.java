package com.esprit.android.util;


import com.esprit.goga.R;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;

public class MxxSystemBarTintUtil {
	public static void setSystemBarTintColor(Activity activity){
		if(SystemBarTintManager.isKitKat()){
			SystemBarTintManager tintManager = new SystemBarTintManager(activity);  
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintDrawable(new ColorDrawable(activity.getResources().getColor(R.color.mxx_item_theme_color_alpha)));
		}
	}
}
