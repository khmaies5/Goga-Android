package com.esprit.translate;

/**
 * Copyright (c) blackbear, Inc All Rights Reserved.
 */

import java.io.InputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * TranslateUtil
 * 
 * <pre>���g����
 * PS: ͸�^google translate
 * </pre>
 * 
 * @author catty
 * @version 1.0, Created on 2011/9/2
 */
public class TranslateUtil {

	protected static final String URL_TEMPLATE = "http://translate.google.com/?langpair={0}&text={1}";
	protected static final String ID_RESULTBOX = "result_box";
	protected static final String ENCODING = "UTF-8";

	protected static final String AUTO = "auto"; // google�Ԅ��Д���Դ�Zϵ
	public static final String TAIWAN = "zh-TW"; // ����
	public static final String CHINA = "zh-CN"; // ����
	public static final String ENGLISH = "en"; // Ӣ
	public static final String JAPAN = "ja"; // ��
	public static final String FRENCH = "fr";

	/**
	 * <pre>Google���g
	 * PS: ����google�Ԅ��Д���Դ�Zϵ
	 * </pre>
	 * 
	 * @param text
	 * @param target_lang Ŀ���Zϵ
	 * @return
	 * @throws Exception
	 */
	public static String translate(final String text, final String target_lang) throws Exception {
		return translate(text, AUTO, target_lang);
	}

	/**
	 * <pre>Google���g</pre>
	 * 
	 * @param text
	 * @param src_lang ��Դ�Zϵ
	 * @param target_lang Ŀ���Zϵ
	 * @return
	 * @throws Exception
	 */
	public static String translate(final String text, final String src_lang, final String target_lang)
			throws Exception {
		InputStream is = null;
		Document doc = null;
		Element ele = null;
		try {
			// create URL string
			String url = MessageFormat.format(URL_TEMPLATE,
					URLEncoder.encode(src_lang + "|" + target_lang, ENCODING),
					URLEncoder.encode(text, ENCODING));

			// connect & download html
			is = HttpClientUtil.downloadAsStream(url);

			// parse html by Jsoup
			doc = Jsoup.parse(is, ENCODING, "");
			ele = doc.getElementById(ID_RESULTBOX);
			String result = ele.text();
			return result;

		} finally {
			IOUtils.closeQuietly(is);
			is = null;
			doc = null;
			ele = null;
		}
	}

	/**
	 * <pre>Google���g: ����-->����</pre>
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String cn2tw(final String text) throws Exception {
		return translate(text, CHINA, TAIWAN);
	}

	/**
	 * <pre>Google���g: ����-->����</pre>
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String tw2cn(final String text) throws Exception {
		return translate(text, TAIWAN, CHINA);
	}

	/**
	 * <pre>Google���g: Ӣ��-->����</pre>
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String en2tw(final String text) throws Exception {
		return translate(text, ENGLISH, TAIWAN);
	}
	/**
	 * <pre>Google���g: Ӣ��-->��������</pre>
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String en2cn(final String text) throws Exception  {
		return translate(text, ENGLISH, CHINA);
	}

	/**
	 * <pre>Google���g: ����-->Ӣ��</pre>
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String tw2en(final String text) throws Exception {
		return translate(text, TAIWAN, ENGLISH);
	}

	/**
	 * <pre>Google���g: ����-->����</pre>
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String jp2tw(final String text) throws Exception {
		return translate(text, JAPAN, TAIWAN);
	}

	/**
	 * <pre>Google���g: ����-->��</pre>
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String tw2jp(final String text) throws Exception {
		return translate(text, TAIWAN, JAPAN);
	}

	public static String en2fr(final String text) throws Exception{
		return translate(text,ENGLISH,FRENCH);
	}

}