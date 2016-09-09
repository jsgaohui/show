package com.mobile.oa.ser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mobile.oa.Constant.OaConstant;
import com.mobile.oa.util.DataProcess;

@Path("/videofragments")
public class Video {
	
	@GET
	@Produces("text/plain;charset=utf-8")	
	@Path("/{param}") 
	public String getVideos(@PathParam("param") String param){
		
		String result = "";
		String cqlUrl = "";
		String className = "VideoFragment";
		if(param.equals("HotVide")){
			cqlUrl =  "select * from VideoFragment where numVideoFragmentViewNumber>=30"; 
		
		}else if(param.equals("LatestV")){//默认两个月内的为最新视频
			Video v = new Video();
			String date = v.lastMonth(2);
			//将date转换成UTC格式，适应leancloud规则
			date = date+"T00:00:00.000Z";
			date = "'"+date+"'";
			cqlUrl =  "select * from VideoFragment where createdAt>date("+date+")"; 
		}else if(param.equals("VideosI")|param.equals("VideosB")){
			cqlUrl =  "select * from VideoFragment "; 
		}else if(param.equals("VideoCo")){
			className = "Comment";
			cqlUrl =  "select * from Comment ";
		}else{
			param = "'%"+param+"%'";
			cqlUrl =  "select * from VideoFragment where strVideoFragmentName like "+param; 
		}
		try {
			cqlUrl = URLEncoder.encode(cqlUrl,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String url = "https://api.leancloud.cn/1.1/cloudQuery?cql="+cqlUrl;
		String data = "";
		DataProcess dp = new DataProcess();
		result = dp.getClassData(url, className, data, 1, 10);
		
		return result ;
	}
	
	
	@GET
	@Produces("text/plain;charset=utf-8")	
	@Path("/{param}/page/{pagePar}/pagesize/{pasizePar}") 
	public String getVideosPage(@PathParam("param") String param,@PathParam("pagePar") String pagePar,@PathParam("pasizePar") String pasizePar){
		
		String result = "";
		String cqlUrl = "";
		int page = 1;
		int pagesize = 10;
		String className = "VideoFragment";
		//热门视频
		if(param.equals("HotVide")){
			cqlUrl =  "select * from VideoFragment where numVideoFragmentViewNumber>=30"; 		
		}else if(param.equals("LatestV")){//默认两个月内的为最新视频
			Video v = new Video();
			String date = v.lastMonth(2);
			//将date转换成UTC格式，适应leancloud规则
			date = date+"T00:00:00.000Z";
			date = "'"+date+"'";
			cqlUrl =  "select * from VideoFragment where createdAt>date("+date+")"; 
		}else if(param.equals("VideosI")|param.equals("VideosB")){
			cqlUrl =  "select * from VideoFragment "; 
		}else if(param.equals("VideoCo")){
			className = "Comment";
			cqlUrl =  "select * from Comment"; 	
		}else{
			
			return "wrong";
		}
		try{
			page = Integer.parseInt(pagePar);
			pagesize = Integer.parseInt(pasizePar);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			cqlUrl = URLEncoder.encode(cqlUrl,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String url = "https://api.leancloud.cn/1.1/cloudQuery?cql="+cqlUrl;
		String data = "";
		DataProcess dp = new DataProcess();
		result = dp.getClassData(url, className, data, page, pagesize);		
		return result ;
	}
	
	@GET
	@Produces("text/plain;charset=utf-8")	
	@Path("/VideosB/{param}") 
	public String getVideosBykey(@PathParam("param") String param){
		
		String result = "";
		String data = "";
		data = "{\"strVideoFragmentName\":"+"\""+param+"\"}";				
		String url = "https://api.leancloud.cn/1.1/classes/VideoFragment";		
		DataProcess dp = new DataProcess();
		result = dp.getClassData(url, "VideoFragment",data, 1, 10);
		
		
		return result;
	}

	@GET
	@Produces("text/plain;charset=utf-8")	
	@Path("/VideoCo/{param}") 
	public String getVideoComment(@PathParam("param") String param){
		
		String result = "";
		String data = "";
		data = "{\"objectId\":"+"\""+param+"\"}";				
		String url = "https://api.leancloud.cn/1.1/classes/Comment";		
		DataProcess dp = new DataProcess();
		result = dp.getClassData(url, "Comment",data, 1, 10);
		
		
		return result;
	}
	
	
	/**
	 * 计算几个月前的日期
	 * @param allMonth
	 * @return
	 */
	public String lastMonth(int allMonth) {
        Date date = new Date();
           int year=Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
           int month=Integer.parseInt(new SimpleDateFormat("MM").format(date))-allMonth;
           int day=Integer.parseInt(new SimpleDateFormat("dd").format(date));
           if(month <= 0){
               int yearFlag = (month*(-1))/12 + 1;
               int monthFlag = (month *(-1))%12;
               year -= yearFlag;
               month=monthFlag*(-1) +12;
           }
           else if(day>28){
               if(month==2){
                   if(year%400==0||(year %4==0&&year%100!=0)){
                       day=29;
                   }else day=28;
               }else if((month==4||month==6||month==9||month==11)&&day==31){
                   day=30;
               }
           }
           String y = year+"";String m ="";String d ="";
           if(month<10) m = "0"+month;
           else m=month+"";
           if(day<10) d = "0"+day;
           else d = day+"";
          
           return y+"-"+m+"-"+d;
    }

}
