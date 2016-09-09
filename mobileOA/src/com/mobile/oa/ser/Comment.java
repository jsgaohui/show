package com.mobile.oa.ser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.oa.Constant.OaConstant;

public class Comment {

	public static void main(String[] args) {
		String url = "http://127.0.0.1:8080/users";
		HttpClient client = new HttpClient();
		PostMethod putMethod = new PostMethod(url);
		//GetMethod putMethod = new GetMethod(url);

		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
	
		String data = "579840090a2b580061e08a5e";
		putMethod.setRequestBody(data);
		int status = 0; String getUrl = "";
		try {
			status = client.executeMethod(putMethod);
			getUrl = putMethod.getResponseBodyAsString();
			
				System.out.println("getUrl---->"+getUrl);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
