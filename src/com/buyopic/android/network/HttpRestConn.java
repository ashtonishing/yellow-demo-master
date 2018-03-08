package com.buyopic.android.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.Handler;
import android.os.Message;

import com.buyopic.android.utils.Constants;
import com.buyopic.android.utils.Utils;
import com.buyopic.android.yelp.Yelp;

public final class HttpRestConn {
	private static final int CONNECTION_TIMEOUT_INTERVAL = 60 * 1000;
	private static final int TIMEOUT_INTERVAL = 60 * 1000;

	protected static String getASCIIContentFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(in));
		StringBuffer out = new StringBuffer();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			out.append(line);
		}
		return out.toString();
	}
	
	public static void postRequestData(String url,
			List<NameValuePair> nameValuePairs, BuyopicRequestHandler handler) {
		HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				CONNECTION_TIMEOUT_INTERVAL);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_INTERVAL);
		HttpContext localContext = new BasicHttpContext();
		String text = null;
		try {
			HttpPost httppost = new HttpPost(url);
			 httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			HttpResponse response = httpClient.execute(httppost, localContext);
			if (response != null
					&& response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				
				text = getASCIIContentFromEntity(entity);
				if (text != null) {
					Utils.showLog("Response String ===>>>" + text);
					Message.obtain(handler, Constants.SUCCESS, text)
					.sendToTarget();
				} else {
					Message.obtain(handler, Constants.FAILURE, "Unknown Error")
					.sendToTarget();
				}
			} else {
				Message.obtain(handler, Constants.FAILURE,
						"Unable to process your request").sendToTarget();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Message.obtain(handler, Constants.FAILURE,
					"Unable to process your request" + e.toString())
					.sendToTarget();
		}
	}

	public static void GetRequestData(String url,
			Handler wayHandler) {
		Utils.showLog("Request-->" + url);
		HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
		HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);
		HttpParams httpParams = httpClient.getParams();

		HttpConnectionParams.setConnectionTimeout(httpParams,
				CONNECTION_TIMEOUT_INTERVAL);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_INTERVAL);
		HttpContext localContext = new BasicHttpContext();
		String text = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent", "Mobile");
			HttpResponse response = httpClient.execute(httpGet, localContext);
			if (response != null
					&& response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				

				text = getASCIIContentFromEntity(entity);
				if (text != null) {
					Utils.showLog("Response String ===>>>" + text);
					Message.obtain(wayHandler, Constants.SUCCESS, text)
							.sendToTarget();
				} else {
					Message.obtain(wayHandler, Constants.FAILURE,
							"Unknown Error").sendToTarget();
				}
			} else {
				Message.obtain(wayHandler, Constants.FAILURE,
						"An error has occurred").sendToTarget();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Message.obtain(wayHandler, Constants.FAILURE,
					"Unable to process your request" + e.toString())
					.sendToTarget();
		}
	}

	
	public static void getYelpReviewResponse(String businessId,Handler handler)
	{
		String consumerKey = "vJQNYXN_-QqL21STMI4IzA";
		String consumerSecret = "krEv49owa_Sg9ioLzoQUMQijT98";
		String token = "o5XFpdTADam6JLo90P1_krerCumfc2b2";
		String tokenSecret = "7WgktB5TAJvz_kPT3a4ldmgo0o8";

		Yelp yelp = new Yelp(consumerKey, consumerSecret, token,
				tokenSecret);
//		String resultFromYelpBusiness=yelp.search("burritos", 30.361471, -87.164326);
		String resultFromYelpBusiness = yelp.searchBusinessId(businessId);
		if(resultFromYelpBusiness!=null)
		{
			Utils.showLog("resultFromYelpBusiness : "+resultFromYelpBusiness);
			Message.obtain(handler,Constants.SUCCESS,resultFromYelpBusiness).sendToTarget();
		}
		else
		{
			Message.obtain(handler,Constants.FAILURE,"An Error Occured").sendToTarget();
		}
		

	}

}