package main_server;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import windows.win_interface;
import vnc.vnc_server;

import Browser.Water_Crab;

@SuppressWarnings("deprecation")
public class main_server {
	private static String url = "";
	private static String basic_web_link = "";
	private static int choose = 0;

	private static vnc_server local_vnc_server_mouse;
	private static vnc_server local_vnc_server_screen;
	
	private static String local_server_ip = "192.168.137.1";	//你的會議主機ip, 自己設定
	
	private static final int SERVER_MOUSE_PORT = 6060;
	private static final int SERVER_SCREEN_PORT = 6080;
	
	private static Water_Crab browser;
	
	private static win_interface win;
	
	private static Map<String, String> commands = new LinkedHashMap();

	public static void main(String[] args) throws Exception 
	{
		url = "index.php";	//一開始預設是去登錄頁面
		basic_web_link = "http://localhost:8080/meeting_cloud/device/";//雲端網頁放在這邊
		browser = new Water_Crab(basic_web_link, null, local_server_ip);
		win = new win_interface();
	//	url = browser.download_file("back_end/download.php?download_path=user_upload_space/emaa&file_name=apple.txt", null);
		try {
			while (true)
			{

		        System.out.println("-----------------------------------new web!-----------------------------------");
		        System.out.println("");
		        
				//===================================================================================	
																//網頁資料都送進來處理, 把資料過濾後	|
		        browser.link(url);								//並讓使用者查看網頁內容				|
		        												//使用超連結或提交表單後, 返回超連結	|
		        //===================================================================================			        
//		        browser.show_json_data();
		        browser.show_web_data();
				
		        commands = browser.get_commond();
		        
		        command_process ();	//讓手機連到主機的func, 有甚麼需要就自己設定吧
											//command_process 主要是根據commands 來執行不同的工作

		        commands = null;	//而commands 是透過接收雲端送過來的 state 訊息, 當command_process處理完後便會清除 
		        
		        if (browser.get_state())
	        	{
		        	while(browser.get_state())
		        	{	Thread.currentThread().sleep(2000); 	}
		        	url = browser.get_now_url();
	        	}    	
		        else
		        {
					choose = browser.input();	//根據web 的界面做你想做的事, 其後會向cloud 發出請求, cloud 收到請求後會返會url
					
					if (choose > 0)
					{
						url = browser.chick_link( choose );		//選擇網址, 然後會loop 回最上面的 browser.link(url);
						if (url.indexOf("download") >= 0)
						{
							url = browser.download_file(url, null);
							System.out.println("download file");
						}
					}
					else
					{	url = browser.post_submit_form();	}
		        }
				
				System.out.println("");
		        System.out.println("-----------------------------------end web!-----------------------------------");
		        System.out.println("");


			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean command_process ()
	{
		boolean success = true;
		if (!(commands.isEmpty()))
		{
			if ( commands.get("vnc_start").equals("true"))
			{
	//			vnc_server local_vnc_server = new vnc_server();	//登錄雲端後才可以始動vnc server
				browser.set_state(true);
				local_vnc_server_mouse = new vnc_server(browser, SERVER_MOUSE_PORT, local_server_ip);
				local_vnc_server_mouse.start();
				
				local_vnc_server_screen = new vnc_server(null, SERVER_SCREEN_PORT, local_server_ip);
				local_vnc_server_screen.start();
		//		local_vnc_server.start();
			}
		}
		return success;
	}
	
}

