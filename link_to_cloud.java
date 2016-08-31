package link_to_cloud;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/*
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.HttpURLConnection;  
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vnc.RemoteDataServer;
*/

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
	private static int json_index;
	static JSONObject json_web_data;
	
	public link_to_cloud(String basic_web_link) 
	{
		conn_cloud = new DefaultHttpClient();			//��l�Ʀ�function ��http �s��
		
		if (basic_web_link == null)
			basic_web_link = "http://localhost:8080/meeting_cloud/device/";

		json_web_data = new JSONObject();
		
	}

			//�z�Lurl ���}�s�춳��,�u���s���M�������ݰe�Ӫ�data, ���|��data ������B�z
//==========================================================================================
	
	//�s��url, �è��o�Ӻ�������� (http)
	public static JSONObject request(String url) 
			throws ClientProtocolException, IOException 
	{
		url = url + basic_web_link;
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
	    
		json_index = web_data.indexOf('{');
		json_web_data = new JSONObject(data.substring(json_index));
	    
	    return json_web_data;
	  }
	
	
	//��g�����W�����ðe�X�h, �e�X��,�����|�۰ʸ���ܷs����, ��s���������}��U�Ө�return �^�h
	public static JSONObject submit_form_post(Map<String, String> form_data, String url) 
	throws ClientProtocolException, IOException
	{
		url = url + basic_web_link;
	    HttpPost post = new HttpPost(url);
	    
	    ArrayList<NameValuePair> post_form = new ArrayList<NameValuePair>();
	    for(Map.Entry<String, String> entry:form_data.entrySet())		//��map �O�����Ҧ����, �ç�Ҧ���Ʈ��X��
	    {   
	    	if (entry.getKey() != "post_link")
	    	post_form.add( new BasicNameValuePair( entry.getKey(), entry.getValue() ));	
	    }   
	    post.setEntity( new UrlEncodedFormEntity(post_form, "UTF-8"));

	    HttpResponse res = conn_cloud.execute(post);
	    BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
	    
	    post.abort();//����post �ШD?

	    url = res.getLastHeader("Location").getValue();   
	    
	    return request(url);
	  }
	
}

