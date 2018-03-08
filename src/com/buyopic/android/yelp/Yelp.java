package com.buyopic.android.yelp;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.util.Log;


/**
* Example for accessing the Yelp API.
*/
public class Yelp {

  OAuthService service;
  Token accessToken;

  /**
* Setup the Yelp API OAuth credentials.
*
* OAuth credentials are available from the developer site, under Manage API access (version 2 API).
*
* @param consumerKey Consumer key
* @param consumerSecret Consumer secret
* @param token Token
* @param tokenSecret Token secret
*/
  public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
    this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
    this.accessToken = new Token(token, tokenSecret);
  }

  /**
* Search with term and location.
*
* @param term Search term
* @param latitude Latitude
* @param longitude Longitude
* @return JSON string response
*/
  public String search(String term, double latitude, double longitude) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
    request.addQuerystringParameter("term", term);
    request.addQuerystringParameter("ll", latitude + "," + longitude);
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }
  
  
  /**
* Search with term and location.
*
* @param term Search term
* @param latitude Latitude
* @param longitude Longitude
* @return JSON string response
*/
  public String searchBusinessId(String id) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/business");
    request.addQuerystringParameter("id", id);
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }

  // CLI
  public static void main(String[] args) {
    // Update tokens here from Yelp developers site, Manage API access.
/*    String consumerKey = "";
    String consumerSecret = "";
    String token = "";
    String tokenSecret = "";
    
    Consumer Key	vJQNYXN_-QqL21STMI4IzA
    Consumer Secret	krEv49owa_Sg9ioLzoQUMQijT98
    Token	o5XFpdTADam6JLo90P1_krerCumfc2b2
    Token Secret	7WgktB5TAJvz_kPT3a4ldmgo0o8*/
    
	String consumerKey =	"vJQNYXN_-QqL21STMI4IzA";
	String consumerSecret = "krEv49owa_Sg9ioLzoQUMQijT98";
	String token	= "o5XFpdTADam6JLo90P1_krerCumfc2b2";
	String tokenSecret =	"7WgktB5TAJvz_kPT3a4ldmgo0o8";

    Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
    String response = yelp.search("burritos", 30.361471, -87.164326);

    System.out.println(response);
    Log.i("Yelp : response" , response);
  }
}
