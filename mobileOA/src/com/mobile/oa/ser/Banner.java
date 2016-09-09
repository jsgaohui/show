package com.mobile.oa.ser;

import java.net.URLEncoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.mobile.oa.util.DataProcess;

@Path("/banners")
public class Banner {
	@GET
	@Produces("text/plain;charset=utf-8")	
	@Path("/{param}") 
	public String getBanners(@PathParam("param") String param){
		String result = "您访问路径有误，请核对后重试!";
		if(param.equals("Banners")){
			String cqlUrl = "";
			cqlUrl =  "select * from Banner "; 
			cqlUrl = URLEncoder.encode(cqlUrl); 
			String url = "https://api.leancloud.cn/1.1/cloudQuery?cql="+cqlUrl;
			String data = "";
			DataProcess dp = new DataProcess();
			result = dp.getClassData(url, "Banner", data, 1, 10);
		}
		return result ;
		
	}
	


}
