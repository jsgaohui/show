package com.mobile.oa.ser;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mobile.oa.util.DataProcess;

@Path("/users")
public class User {
	
	
	@GET
	@Produces("text/plain;charset=utf-8")
	@Path("/{param}") 
	public String getUsers(@PathParam("param") String param){
		
		String result = "您访问路径有误，请核对后重试!";
		if(param.equals("RecommentUsers")){
			String url = "https://api.leancloud.cn/1.1/users";
			String data = "{\"isRecommended\":\"1\"}";
			DataProcess dp = new DataProcess();
			result = dp.getClassData(url, "users", data, 1, 2);
		}
		System.out.println(result);
		return result;
	}
	
	
	
	
	

}
