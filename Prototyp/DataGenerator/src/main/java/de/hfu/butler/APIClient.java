package de.hfu.butler;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIClient {
    // static variable single_instance of type Singleton
    private static APIClient single_instance = null;

    // variable of type String
    public String s;

    // static method to create instance of Singleton class
    public static APIClient getInstance()
    {
        if (single_instance == null)
            single_instance = new APIClient();

        return single_instance;
    }
    
    public JSONObject registerUser(final String email, final String password, final int type) {
    	String url = "http://192.52.32.250:3000/api/customers";
    	JSONObject responseJSON = null;
		final MediaType JSON = MediaType.get("application/json; charset=utf-8");
		
		OkHttpClient client = new OkHttpClient();
		 
		JSONObject json = new JSONObject();
		try {
			json.put("Usertype", type);
			json.put("email", email);
			json.put("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
		
		RequestBody body = RequestBody.create(JSON, json.toString());
		Request request = new Request.Builder()
		  .url(url)
		   .post(body)
		   .build();
		try (Response response = client.newCall(request).execute()) {
			String responseString = response.body().string();
			System.out.println("register: " + responseString);
			responseJSON = new JSONObject(responseString);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseJSON;
    }
    
    public JSONObject loginUser(final String email, final String password) {
    	JSONObject responseJSON = null;
    	String url = "http://192.52.32.250:3000/api/customers/login";
		final MediaType JSON = MediaType.get("application/json; charset=utf-8");
		
		OkHttpClient client = new OkHttpClient();
		 
		JSONObject json = new JSONObject();
		try {
			json.put("email", email);
			json.put("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
		
		RequestBody body = RequestBody.create(JSON, json.toString());
		Request request = new Request.Builder()
		  .url(url)
		   .post(body)
		   .build();
		try (Response response = client.newCall(request).execute()) {
			String responseString = response.body().string();
			System.out.println("login: " + responseString);
			responseJSON = new JSONObject(responseString);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseJSON;
    }
    
    public JSONObject createUserProfil(final String userId, final String token, final String firstname, final String lastname, final String location) {
    	JSONObject responseJSON = null;
    	String profileUrl = "http://192.52.32.250:3000/api/customers/";
    	String url = profileUrl + userId + "/profiles?access_token=" + token;
		final MediaType JSON = MediaType.get("application/json; charset=utf-8");
		
		OkHttpClient client = new OkHttpClient();
		 
		JSONObject json = new JSONObject();
		try {
			json.put("vorname", firstname);
			json.put("name", lastname);
			json.put("location", location);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
		
		RequestBody body = RequestBody.create(JSON, json.toString());
		Request request = new Request.Builder()
		  .url(url)
		   .post(body)
		   .build();
		try (Response response = client.newCall(request).execute()) {
			String responseString = response.body().string();
			System.out.println("profile: " + responseString);
			responseJSON = new JSONObject(responseString);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return responseJSON;
    }
}
