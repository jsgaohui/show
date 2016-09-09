package com.mobile.oa.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.oa.Constant.*;;

public class PageUtil {
	

	 
	 private int pageSize;//每页显示的条�?
	 private int totalNumber = -1;//总共的条�?  返回
	 private int currentPage ;//当前页面
	 private String url;
	 private String getUrl = "";
	 private int status=0;
	 private int resultsFlag = 0;//判断结果中是否含有result
	 private List<JSONObject> list = new ArrayList<JSONObject>();
	 //private List<JSONObject> returnList = new ArrayList<JSONObject>();
	 public static JSONObject jso;
	   
	 
	 public PageUtil(int pageSize, int currentPage,String url)  { 
		 if(pageSize<=0){
			 pageSize = 1;
		 }
         this.pageSize = pageSize; 
         this.url = url;
         this.totalNumber = countTotalNumber();
         setCurrentPage(currentPage); 
     } 
	 
	 //构�?�方�?
	    public PageUtil(int pageSize,String url)  { 
	        this(pageSize,  1,url); 
	    } 
	    
	    //总页�? 
	    public int getPageCount()  { 
	        int size = totalNumber/pageSize;//总条�?/每页显示的条�?=总页�?  
	        int mod = totalNumber % pageSize;//�?后一页的条数 
	        if(mod != 0) 
	            size++; 
	        return totalNumber == 0 ? 1 : size; 
	    } 
	 
	 
	  //设置当前�? 
	    public void setCurrentPage(int currentPage) { 
	        int validPage = currentPage <= 0 ? 1 : currentPage; 
	        validPage = validPage > getPageCount() ? getPageCount() : validPage; 
	        this.currentPage = validPage; 
	    }
	    public int getCurrentPage(){
	    	
	    	return currentPage;
	    }
	 /**
	  *返回某一页的完整数据（包括该页的首条记录�?直到�?后一条记录）
	  */
	    public JSONObject getPageRecord(){
	 
	    	int recordBegin = (currentPage-1)*pageSize;
	    	JSONArray jsa = new JSONArray();
	    	JSONObject json = new JSONObject();
	    	JSONObject jso1 = new JSONObject();
	    	for(int i=recordBegin;i<pageSize+recordBegin;i++){
	    		if(i+1<=totalNumber){
	    		 json = list.get(i);
	    		 jsa.put(json);
	    		}
	    	}
	    	try {
	    		if(resultsFlag==1){
	    			jso1 = jso1.put("results", jsa);
	    		}else{//如果没有results，说明为空或者是按objectId(不�?�过cql查询)查找，只有一条数�?
	    			jso1 = json;
	    		}
	    		//jso1 = jso1.put("results", jsa);
	    		jso1 = jso1.put("count", totalNumber);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	    	return jso1;
	    }
	 
	 /**
	  * 计算�?查数据的总记录数
	  * @return
	  */
	 
	 public int countTotalNumber(){
		 InputStream in = null;
		 int count = 0;
		 JSONObject jo = null;
		 JSONArray jsa = null;
		 HttpClient client = new HttpClient();
		 GetMethod getMethod = new GetMethod(url);
		 getMethod.addRequestHeader("Content-Type","application/json");
		 getMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		 getMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);		  
		 client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");  		    
		//执行并返回状�?
			try{
				status = client.executeMethod(getMethod);
				 //getUrl = getMethod.getResponseBodyAsString();
				in = getMethod.getResponseBodyAsStream();
				 BufferedReader br = new BufferedReader(new InputStreamReader(in));
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
				 jo  = new JSONObject(getUrl);
				 JSONObject jso;
				 if(getUrl.contains("results")){//如果是通过objectId（或其它字段）获取的信息 则没有results字段
					 jsa = jo.getJSONArray("results");
					 count = jsa.length();					 
					 for (int i=0;i<count;i++){
						 resultsFlag = 1;
						 jso  = jsa.getJSONObject(i);
						 list.add(jso);
					 }
					 
				 }else if(!"".equals(getUrl)){//如果不为空，说明根据objectId（或其它字段）获取到了一条数�?
					 count = 1;
					 jso = jo;
					 list.add(jso);
				 }
				 
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
		return   count;
	}
	 
	    /**
	     * 得到总记录数
	     * @return
	     */
	    
	    public int getTotalNumber()  { 
	    	if(totalNumber ==-1){
	    		countTotalNumber();
	    	}
	        return totalNumber; 
	    }

	   
}
