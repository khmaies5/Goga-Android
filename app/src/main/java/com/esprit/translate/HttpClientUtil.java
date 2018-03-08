package com.esprit.translate;

/**
 * Copyright (c) Blackbear, Inc All Rights Reserved.
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import org.apache.http.conn.ssl.SSLSocketFactory;


/**
 * PostUtil.java
 * 
 * @author catty
 * @version 1.0, Created on 2008/2/20
 */
public class HttpClientUtil {

//	protected static Log log = LogFactory.getLog(HttpClientUtil.class);
	protected static int maxTotal = 200;
	protected static int maxPerRoute = 20;
	protected static String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7";

	/**
	 * <pre>���d��؂�Inputstream</pre>
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static InputStream downloadAsStream(String url) throws Exception {
		InputStream is = (InputStream) download(url, null, null, false);
		return is;
	}

	/**
	 * <pre>���d�ღ�浽File</pre>
	 * 
	 * @param url
	 * @param saveFile
	 * @throws Exception
	 */
	public static void download(String url, File saveFile) throws Exception {
		download(url, saveFile, null, false);
	}

	/**
	 * <pre>���d</pre>
	 * 
	 * @param url
	 * @param saveFile
	 * @param params
	 * @param isPost
	 * @return ���saveFile==null�t�؂�inputstream, ��t�؂�saveFile
	 * @throws Exception
	 */
	public static Object download(final String url, final File saveFile, final Map<String, String> params,
			final boolean isPost) throws Exception {

		boolean saveToFile = saveFile != null;

		// check dir exist ??
		if (saveToFile && saveFile.getParentFile().exists() == false) {
			saveFile.getParentFile().mkdirs();
		}

		Exception err = null;

		FileOutputStream fos = null;
		Object result = null;

		try {


		} catch (Exception e) {
			err = e;
		} finally {

			// close
			IOUtils.closeQuietly(fos);



			if (err != null) {
				throw err;
			}

			return result;
		}

	}



}