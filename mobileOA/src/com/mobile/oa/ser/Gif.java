package com.mobile.oa.ser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.mobile.oa.util.DataProcess;

@Path("/gif")
public class Gif {

	@GET
	@Produces("text/plain;charset=utf-8")	
	public String getGifs(){
		String result = "您访问路径有误，请核对后重试!";
		
			String cqlUrl = "";
			cqlUrl =  "select * from Gif "; 
			cqlUrl = URLEncoder.encode(cqlUrl); 
			String url = "https://api.leancloud.cn/1.1/cloudQuery?cql="+cqlUrl;
			String data = "";
			DataProcess dp = new DataProcess();
			result = dp.getClassData(url, "Banner", data, 1, 10);
		
		return result;
		
	}
	
	
	@GET
	@Produces("text/plain;charset=utf-8")	
	@Path("/page/{pagePar}/pagesize/{pasizePar}") 
	public String getVideosPage(@PathParam("pagePar") String pagePar,@PathParam("pasizePar") String pasizePar){
		
		String result = "";
		String cqlUrl = "";
		int page = 1;
		int pagesize = 10;
		String className = "Gif";
		cqlUrl =  "select * from Gif"; 			
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
	
	
	
}
