package link_to_cloud;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair; 

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

@SuppressWarnings("deprecation")
public class link_to_cloud {
	private static String web_link = "";
	private static String basic_web_link = "";
	private static String web_data = "";
	private static HttpClient conn_cloud = null; 
	private static Scanner scanner;
	static JSONObject json_web_data;
	
	public link_to_cloud(String basic_web_link) 
	{
		conn_cloud = new DefaultHttpClient();			//初始化此function 的http 連接
		
		if (basic_web_link == null)
			basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		json_web_data = new JSONObject();
		
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException
	{
		System.out.println(request("index.php").toString());
	}
			//透過url 網址連到雲端,只做連接和接收雲端送來的data, 不會對data 做任何處理
//==========================================================================================
	
	//連到url, 並取得該網頁的資料 (http)
	public static JSONObject request(String url) 
			throws ClientProtocolException, IOException 
	{
		int json_index = 0;
		basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		url = basic_web_link + url;
	    HttpPost post = new HttpPost(url);
	    HttpResponse res = conn_cloud.execute(post);
	    post.abort();
	    while (res.getStatusLine().getStatusCode() == 302) 
	    {   	
	    	url = basic_web_link + res.getLastHeader("Location").getValue();  
	    	post = new HttpPost(url);
		    res = conn_cloud.execute(post);
	    	post.abort();
	    }  
	    BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), "utf-8"));
	    post.abort();
	    String data = "";
	    String line = "";
	    while ((line = br.readLine()) != null) 
	    {   data = data + line + '\n';    }
		json_index = data.indexOf('{');
		json_web_data = new JSONObject(data.substring(json_index));
	    
	    return json_web_data;
	  }
	
	
	//填寫網頁上的表單並送出去, 送出後,網頁會自動跳轉至新網頁, 把新網頁的網址抓下來並return 回去
	public static JSONObject submit_form_post(Map<String, String> form_data, String url) 
	throws ClientProtocolException, IOException
	{
		url = basic_web_link + url;
	    HttpPost post = new HttpPost(url);
	    
	    ArrayList<NameValuePair> post_form = new ArrayList<NameValuePair>();
	    for(Map.Entry<String, String> entry:form_data.entrySet())		//用map 記錄表單所有資料, 並把所有資料拿出來
	    {   
	    	if (entry.getKey() != "post_link")
	    	post_form.add( new BasicNameValuePair( entry.getKey(), entry.getValue() ));	
	    }   
	    post.setEntity( new UrlEncodedFormEntity(post_form, "UTF-8"));

	    HttpResponse res = conn_cloud.execute(post);
	    BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
	    
	    post.abort();//釋放post 請求?

	    url = res.getLastHeader("Location").getValue();   
	    
	    return request(url);
	  }
	
}

