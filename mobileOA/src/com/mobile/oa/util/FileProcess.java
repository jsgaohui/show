package com.mobile.oa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.oa.Constant.*;;

public class FileProcess {
	
	 int uploadTimes = 1;

	public static void main(String[] args) {
		String urlFile = "https://api.leancloud.cn/1.1/files/zhuan1.mp4";
		String downFiles = "https://api.leancloud.cn/1.1/files";
		String downFile = "http://ac-oetcx4kr.clouddn.com/2x97YEk8vkHRbfkdQNbUUJgArijldH1a5I9otfDv.mp4";

		// FileProcess fu = new FileProcess();
		// fu.uploadFile(urlFile);

	}

	/**
	 * 
	 * @param url
	 * 服务器端地址(/attacchment....)
	 * @param data
	 * 添加信息成功 后返回的记录，主要是为了获取id，方便附件上传后，根据id再更新 附件地址
	 * @param className
	 * @return
	 */
	public String upload(String url, String data,String className) {
		InputStream ins = null;
		String getUrl = "", suffix = "", objectId = "";
		String urlF = "";
		int status = 0;
		JSONObject jso = null;
		JSONArray jsa = null;
		String urlFile = "https://api.leancloud.cn/1.1/files/";
		urlFile = urlFile + url.substring(url.lastIndexOf("/")+1);// 上传后的名字跟真实名称保持一致
		suffix = url.substring(url.lastIndexOf(".") + 1);// 后缀 jpg png gif MP3  mp4
		urlF = url.substring(1);
		urlF = "/usr/local/tomcat/webapps/" +urlF;
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(urlFile);
		if (suffix.equalsIgnoreCase("png")) {
			postMethod.addRequestHeader("Content-Type", "image/png");
		}else if (suffix.equalsIgnoreCase("jpg")) {
			postMethod.addRequestHeader("Content-Type", "image/jpeg");
		}else if (suffix.equalsIgnoreCase("gif")) {
			postMethod.addRequestHeader("Content-Type", "image/gif");
		}else if (suffix.equalsIgnoreCase("mp3")) {
			postMethod.addRequestHeader("Content-Type", "audio/mp3");
		}else if (suffix.equalsIgnoreCase("mp4")) {
			postMethod.addRequestHeader("Content-Type", "video/mpeg4");

		}
		postMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		postMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
	
		File file = new File(urlF);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		postMethod.setRequestBody(in);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		try {
			status = client.executeMethod(postMethod);
			//getUrl = postMethod.getResponseBodyAsString();
			//不使用getResponseBodyAsString()方法 ，返回信息超过1M会报错
			ins = postMethod.getResponseBodyAsStream();
			 BufferedReader br = new BufferedReader(new InputStreamReader(ins));
				StringBuffer stringBuffer = new StringBuffer();  
				String str= "";  
				try {
					while((str = br.readLine()) != null){  
					stringBuffer.append(str );  
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			 getUrl = stringBuffer.toString();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String fileUrl = getFileUrl(getUrl);
		if("".equals(fileUrl)&&FileProcess.this.uploadTimes<3){//如果fileUrl为空，说明上传失败，重传一次
			FileProcess fp = new FileProcess();
			fp.uploadTimes = fp.uploadTimes+1;
			upload(url, data, className);
		}
		JSONObject js = null;
		try {
			jso = new JSONObject(data);
			try {
				jsa = jso.getJSONArray("results");
				js = jsa.getJSONObject(0);
			} catch (Exception e) {
				// TODO: handle exception

			}

			if (js == null) {
				objectId = jso.getString("objectId");
			} else {
				objectId = js.getString("objectId");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//----------将服务器上附件地址存到leancloud开始----------
		DataProcess dp = new DataProcess();
		if(className.equals("Music")){
			String da = "{'strMusicURL':" + "'" + fileUrl + "'" + "}";
			dp.updateData("Music", da, objectId);
		}else if(className.equals("Banner")){
			String da = "{'strBannerImg':" + "'" + fileUrl + "'" + "}";
			dp.updateData("Banner", da, objectId);
		}else if(className.equals("users")){
			String da = "{'strUserLogo':" + "'" + fileUrl + "'" + "}";
			dp.updateData("users", da, objectId);
		}else if(className.equals("VideoFragment1")){
			String da = "{'strVideoFragmentScreenshot':" + "'" + fileUrl + "'" + "}";
			dp.updateData("VideoFragment", da, objectId);
		}else if(className.equals("VideoFragment2")){
			String da = "{'strVideoFragmentURL':" + "'" + fileUrl + "'" + "}";
			dp.updateData("VideoFragment", da, objectId);
		}else if(className.equals("VideoSubFragment1")){
			String da = "{'strVideoSubFragmentURL':" + "'" + fileUrl + "'" + "}";
			dp.updateData("VideoSubFragment", da, objectId);
		}else if(className.equals("VideoSubFragment2")){
			String da = "{'strVideoSubFragmentScreenShot':" + "'" + fileUrl + "'" + "}";
			dp.updateData("VideoSubFragment", da, objectId);
		}else if(className.equals("VideoSubFragment3")){
			String da = "{'strVideoSubFragmentAudioUrl':" + "'" + fileUrl + "'" + "}";
			dp.updateData("VideoSubFragment", da, objectId);
		}
		//----------将服务器上附件地址存到leancloud结束---------------
		return getUrl;
	}

	public void uploadFile(String fileUrl) {
		InputStream ins = null;
		int status = 0;
		String getUrl = "";
		String uploadedFileUrl;
		String uloadRrl = "https://api.leancloud.cn/1.1/files/"
				+ fileUrl.substring(fileUrl.lastIndexOf(File.separator) + 1);
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(uloadRrl);
		postMethod.addRequestHeader("Content-Type", "video/mpeg4");
		postMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		postMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		File file = new File(fileUrl);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		postMethod.setRequestBody(in);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		try {
			status = client.executeMethod(postMethod);
			//getUrl = postMethod.getResponseBodyAsString();
			ins = postMethod.getResponseBodyAsStream();
			 BufferedReader br = new BufferedReader(new InputStreamReader(ins));
				StringBuffer stringBuffer = new StringBuffer();  
				String str= "";  
				try {
					while((str = br.readLine()) != null){  
					stringBuffer.append(str );  
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			 getUrl = stringBuffer.toString();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		uploadedFileUrl = getFileUrl(getUrl);

	}

	/**
	 * 获取上传附件后的地址
	 * @param str
	 * @return
	 */
	public String getFileUrl(String str) {
System.out.println("str---->"+str);
		JSONObject jso;
		String fielUrl = "";
		try {
			jso = new JSONObject(str);
			if(null!=jso.getString("url")){
				fielUrl = jso.getString("url");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fielUrl;
	}

}
