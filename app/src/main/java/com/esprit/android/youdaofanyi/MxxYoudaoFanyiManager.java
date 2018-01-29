package com.esprit.android.youdaofanyi;

import java.io.UnsupportedEncodingException;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

public class MxxYoudaoFanyiManager {
	
	public interface YoudaoFanyiListener{
		public void onError(String errMsg);
		public void onSuccess(YoudaoFanyiItem fanyiItem);
	}
	
	private Context context;
	private FinalDb finalDb;
	private FinalHttp finalHttp;// = new FinalHttp();

	public MxxYoudaoFanyiManager(Context context) {
		super();
		this.context = context;
		finalDb = FinalDb.create(context, "MxxYoudaoFanyiManager", false);
		finalHttp = new FinalHttp();
	}
	/**
	 * * Translate words or sentences
	 * @ param query to query the content
	 * @param fanyiListener
	 * @ Param progressDialog The loading dialog box that appears when getting network data is null and does not display
	 */
	public void fanyi(String query, YoudaoFanyiListener fanyiListener, Dialog progressDialog){
		if(TextUtils.isEmpty(query)){
			return;
		}
		YoudaoFanyiItem fanyiItem = finalDb.findById(query, YoudaoFanyiItem.class);
		if(fanyiItem!=null){
			if(fanyiListener!=null){
				fanyiListener.onSuccess(fanyiItem);
			}
		}else{
			startFanyiTask(query,fanyiListener,progressDialog);
		}
		
	}
	private void startFanyiTask(String query, final YoudaoFanyiListener fanyiListener, final Dialog progressDialog){
		String url = null;
		try {
			url = "http://fanyi.youdao.com/openapi.do?keyfrom=sasfasdfasf&key=1177596287&type=data&doctype=json&version=1.1&q=" + java.net.URLEncoder.encode(query,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(fanyiListener!=null){
				fanyiListener.onError("URLEncode error");
	    	}
//			MxxToastUtil.showToast(context, "URLEncode error");
			return;
		}
		finalHttp.get(url, new AjaxCallBack<String>(){
			@Override
		    public void onLoading(long count, long current) { //Callback is automatically made every 1 second
		    }

		    @Override
		    public void onSuccess(String string) {
		    	if(progressDialog!=null){
		    		progressDialog.dismiss();
		    	}
		    	YoudaoFanyiItem item = new YoudaoFanyiItem(string);
		    	if(TextUtils.isEmpty(item.getQuery())){
		    		if(fanyiListener!=null){
			    		fanyiListener.onError("The translate result query is null!");
			    	}
		    		return;
		    	}
		    	if(finalDb.findById(item.getQuery(), YoudaoFanyiItem.class)==null){//Check for existence
		    		finalDb.save(item);
		    	}else{
		    		finalDb.update(item);
		    	}
		    	
		    	if(fanyiListener!=null){
		    		fanyiListener.onSuccess(item);
		    	}
		    }

		    @Override
		    public void onStart() {
		        //Callback when starting http request
		    	if(progressDialog!=null){
		    		progressDialog.show();
		    	}
		    }
		    @Override
		    public void onFailure(Throwable t, int errorNo, String strMsg) {
		    	// TODO Auto-generated method stub
		    	super.onFailure(t, errorNo, strMsg);
		    	if(progressDialog!=null){
		    		progressDialog.dismiss();
		    	}
		    	if(fanyiListener!=null){
		    		fanyiListener.onError(strMsg);
		    	}
		    }

		});
		
	}
	

}
