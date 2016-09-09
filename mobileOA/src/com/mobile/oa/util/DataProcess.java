package com.mobile.oa.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.oa.Constant.*;

public class DataProcess implements Runnable{

	String _fileUrl,_getUrl,_className;	

	public String get_fileUrl() {
		return _fileUrl;
	}

	public void set_fileUrl(String _fileUrl) {
		this._fileUrl = _fileUrl;
	}

	public String get_getUrl() {
		return _getUrl;
	}

	public void set_getUrl(String _getUrl) {
		this._getUrl = _getUrl;
	}

	public String get_className() {
		return _className;
	}

	public void set_className(String _className) {
		this._className = _className;
	}

	public String deletedata(String className, String objectId) {
		InputStream in = null;
		int status = 0;
		String getUrl = "";
		String url = "";
		if (className.equals("users") | className.equals("roles")) {//users roles属于leancloud自带表，访问方式与新建表不一�?
			url = "https://leancloud.cn:443/1.1/" + className + "/" + objectId;
		} else {
			url = "https://leancloud.cn:443/1.1/classes/" + className + "/" + objectId;
		}
		HttpClient client = new HttpClient();
		DeleteMethod delethod = new DeleteMethod(url);
		delethod.addRequestHeader("Content-Type", "application/json");
		delethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		delethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		try {
			status = client.executeMethod(delethod);
			//getUrl = delethod.getResponseBodyAsString();
			//不使用getResponseBodyAsString()方法，返回信息超�?1M会报�?
			in = delethod.getResponseBodyAsStream();
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

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getUrl;

	}

	/**
	 * 获取表中数据
	 * 
	 * @param url
	 * @param className
	 * @param data
	 *            查询条件{"key":"value"} 这些条件不是通过参数传输过来，是用户在前端的查询条件随机输入的数
	 *            前端传过来的是json方式
	 * @return
	 */
	public String getClassData(String url, String className, String data, int page, int pagesize) {
	
		int status = 0;
		String getUrl = "";
		JSONObject jso = null;
		JSONObject jso1 = null;
		if (!"".equals(data)) {
			try {
				jso = new JSONObject(data);
				url = url + "?where=" + URLEncoder.encode(jso.toString(),"utf-8");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
		PageUtil pu = null;
		if (page != -1 && pagesize != -1) {
			pu = new PageUtil(pagesize, page, url);

		} else if (page == -1 && pagesize != -1) {
			pu = new PageUtil(pagesize, url);
		} else if (pagesize == -1) {
			pu = new PageUtil(1000, 1, url);
		}
		jso1 = pu.getPageRecord();
	
		//如果是VideoFragent，则要联表查询发布人信息
		if (className.equals("VideoFragment")) {
			try {
				if(!jso1.getString("count").equals("0")){//0则表示没有相关记录
					getUrl = Json(jso1.toString());
					jso1 = new JSONObject(getUrl);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(className.equals("VideoTheme")){
			try {
				if(!jso1.getString("count").equals("0")){
					getUrl = JsonVideoTheme(jso1.toString());
					jso1 = new JSONObject(getUrl);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(className.equals("Banner")){
			try {
				if(!jso1.getString("count").equals("0")){
					
					getUrl = JsonBanner(jso1.toString());
					jso1 = new JSONObject(getUrl);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(className.equals("Music")){
			try {
				if(!jso1.getString("count").equals("0")){
					getUrl = JsonMusic(jso1.toString());
					jso1 = new JSONObject(getUrl);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(className.equals("Sound")){
			
			try {								
				if(!jso1.getString("count").equals("0")){
					getUrl = JsonSound(jso1.toString());
					jso1 = new JSONObject(getUrl);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(className.equals("Coment")){
			
			try {								
				if(!jso1.getString("count").equals("0")){
					getUrl = JsonComment(jso1.toString());
					jso1 = new JSONObject(getUrl);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jso1.toString();
	}

	/**
	 * 
	 * @param className
	 * @param data
	 * 前端封装（或srvlet中封装）�? 的json类型的data，将key作为条件，将value作为值插入（修改�?
	 * @param objectId
	 * @return
	 */
	public String updateData(String className, String data, String objectId) {
		_className = className;
		InputStream in = null;
		int status = 0;
		String _getUrl = "",_fileUrl = "",url ;
		if (className.equals("users") | className.equals("roles")) {
			url = "https://leancloud.cn:443/1.1" + className + "/" + objectId;
		} else {
			url = "https://leancloud.cn:443/1.1/classes/" + className + "/" + objectId;
		}
		HttpClient client = new HttpClient();
		PutMethod putMethod = new PutMethod(url);
		putMethod.addRequestHeader("Content-Type", "application/json,charset=utf-8");
		putMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		putMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		JSONObject json = null;
		try {
			json = new JSONObject(data);
			//如果不重新上传音乐，数据�? 去掉音乐地址的修改，否则将会使地�?为空，前台添加限制后，此语句应该去掉，否则如果确实想去掉连接地址则无法实�?
			if(className.equals("Music")&&"".equals(json.getString("strMusicURL"))){
			 json.remove("strMusicURL");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		putMethod.setRequestBody(json.toString());

		try {
			status = client.executeMethod(putMethod);
			//_getUrl = putMethod.getResponseBodyAsString();
			in = putMethod.getResponseBodyAsStream();
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
				_getUrl = stringBuffer.toString();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//----------将服务器上附件地�?存到leancloud�?�?----------
	    if(className.equals("Music")){
	    	try {	
	    		
				_fileUrl = json.getString("strMusicURL");
				if(_fileUrl!=null&&!"".equals(_fileUrl)&& !_fileUrl.startsWith("http:")&& !_fileUrl.equalsIgnoreCase("null")){
					DataProcess dp = new	DataProcess();
					dp.set_fileUrl(_fileUrl);
					dp.set_getUrl(_getUrl);
					dp.set_className(_className);
					//(new Thread(new TableProcess())).start(); 
					Thread td = new Thread(dp);
					td.start();						
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }else  if(className.equals("Banner")){
	    	try {		    		
				_fileUrl = json.getString("strBannerImg");
				if(_fileUrl!=null&&!"".equals(_fileUrl)&& !_fileUrl.startsWith("http:")){
					DataProcess dp = new	DataProcess();
					dp.set_fileUrl(_fileUrl);
					dp.set_getUrl(_getUrl);
					dp.set_className(_className);
					Thread td = new Thread(dp);
					td.start();		
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }else  if(className.equals("users")){
	    	try {		    		
				_fileUrl = json.getString("strUserLogo");
				if(_fileUrl!=null&&!"".equals(_fileUrl)&& !_fileUrl.startsWith("http:")){
					DataProcess dp = new	DataProcess();
					dp.set_fileUrl(_fileUrl);
					dp.set_getUrl(_getUrl);
					dp.set_className(_className);
					Thread td = new Thread(dp);
					td.start();		
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }else  if(className.equals("VideoFragment")){
	    	try {		    		
				_fileUrl = json.getString("strVideoFragmentScreenshot");
				if(_fileUrl!=null&&!"".equals(_fileUrl)&& !_fileUrl.startsWith("http:")){
					DataProcess dp = new	DataProcess();
					dp.set_fileUrl(_fileUrl);
					dp.set_getUrl(_getUrl);
					dp.set_className("VideoFragment1");
					Thread td = new Thread(dp);
					td.start();						
				}
				_fileUrl = json.getString("strVideoFragmentURL");
				if(_fileUrl!=null&&!"".equals(_fileUrl)&& !_fileUrl.startsWith("http:")){
					DataProcess dp = new	DataProcess();
					dp.set_fileUrl(_fileUrl);
					dp.set_getUrl(_getUrl);
					dp.set_className("VideoFragment2");
					Thread td = new Thread(dp);
					td.start();									
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }else  if(className.equals("VideoSubFragment")){
	    	try {		    		
				_fileUrl = json.getString("strVideoSubFragmentURL");
				if(_fileUrl!=null&&!"".equals(_fileUrl)&& !_fileUrl.startsWith("http:")){
					DataProcess dp = new	DataProcess();
					dp.set_fileUrl(_fileUrl);
					dp.set_getUrl(_getUrl);
					dp.set_className("VideoSubFragment1");
					Thread td = new Thread(dp);
					td.start();						
				}
				_fileUrl = json.getString("strVideoSubFragmentScreenShot");
				if(_fileUrl!=null&&!"".equals(_fileUrl)&& !_fileUrl.startsWith("http:")){
					DataProcess dp = new	DataProcess();
					dp.set_fileUrl(_fileUrl);
					dp.set_getUrl(_getUrl);
					dp.set_className("VideoSubFragment2");
					Thread td = new Thread(dp);
					td.start();						
				}
				_fileUrl = json.getString("strVideoSubFragmentAudioUrl");
				if(_fileUrl!=null&&!"".equals(_fileUrl)&& !_fileUrl.startsWith("http:")){
					DataProcess dp = new	DataProcess();
					dp.set_fileUrl(_fileUrl);
					dp.set_getUrl(_getUrl);
					dp.set_className("VideoSubFragment3");
					Thread td = new Thread(dp);
					td.start();						
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	  //----------将服务器上附件地�?存到leancloud结束---------------

		return _getUrl;
	}

	/**
	 * 处理VideoFragment联表查询问题
	 * @param data
	 * data为服务器端返回的查询（执行）信息
	 * 如{count:2,results:[{username:zhangsan},{username:lisi}]}
	 * 或{usename:zhangsan,count:1}
	 */
	public String Json(String data) {
		JSONObject jso = null;
		JSONArray jsa = null;
		String aa = "";
		String resu = "";
		String tt = "";
		String ul = "";
		String u = "https://api.leancloud.cn/1.1/cloudQuery";
		JSONObject js = null;
		try {
			jso = new JSONObject(data);
			if(data.contains("results")){
				jsa = (JSONArray) jso.get("results");

				for (int i = 0; i < jsa.length(); i++) {
					js = jsa.getJSONObject(i);
					if (!js.isNull("strVideoPubUserId") && !"".equals(js.getString("strVideoPubUserId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
						tt = js.getString("strVideoPubUserId");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  username from _User where objectId= " + tt;
						resu = jsonJ(u + "?cql=" + URLEncoder.encode(ul));
						js.put("videoPubUserName", resu);
					}
					
					if (!js.isNull("strVideoThemeID") && !"".equals(js.getString("strVideoThemeID"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
						tt = js.getString("strVideoThemeID");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  strThemeName from VideoTheme where objectId= " + tt;
						resu = jsonJT(u + "?cql=" + URLEncoder.encode(ul));
						js.put("strThemeName", resu);
					}
					
				}
			}else{
				js = jso;
				if (!js.isNull("strVideoPubUserId") && !"".equals(js.getString("strVideoPubUserId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
					tt = js.getString("strVideoPubUserId");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  username from _User where objectId= " + tt;
					resu = jsonJ(u + "?cql=" + URLEncoder.encode(ul));
					js.put("videoPubUserName", resu);
				}
				
				if (!js.isNull("strVideoThemeID") && !"".equals(js.getString("strVideoThemeID"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
					tt = js.getString("strVideoThemeID");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  strThemeName from VideoTheme where objectId= " + tt;
					resu = jsonJT(u + "?cql=" + URLEncoder.encode(ul));
					js.put("strThemeName", resu);
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return jso.toString();
	}

	/**
	 * 
	 * @param url
	 * 处理VideoFragment联表查询问题时被调用
	 * @return
	 */
	public String jsonJ(String url) {
		int stat = 0;
		InputStream in = null;
		String result = "";
		HttpClient client = new HttpClient();
		String bb = "";
		GetMethod getMethod;
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "application/json");
		getMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		getMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 执行并返回状�?
		try {
			stat = client.executeMethod(getMethod);
			//result = getMethod.getResponseBodyAsString();
			//避免 使用getResponseBodyAsString()方法，返回信息超�?1M会报�?
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
				result = stringBuffer.toString();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jso1 = null;
		JSONArray jsa1 = null;

		try {
			jso1 = new JSONObject(result);
			if (result.contains("results")) {// 根据Id查询返回结果将不包含results
				jsa1 = (JSONArray) jso1.get("results");
				for (int j = 0; j < jsa1.length(); j++) {
					JSONObject js = jsa1.getJSONObject(j);
					if(js.getString("username")!=null)
					bb = bb + js.getString("username") + ",";
				}

			} else {
				if(jso1.getString("username")!=null){
					bb = jso1.getString("username") + ",";
				}
				
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bb;

	}
	
	/**
	 * 
	 * @param url
	 * 处理VideoFragment联表查询问题时被调用,查询
	 * 另一参数strVideoThemeID时调�?
	 * @return
	 */
	public String jsonJT(String url) {
		// JSONObject jso = new JSONObject();
		int stat = 0;
		InputStream in = null;
		String result = "";
		HttpClient client = new HttpClient();
		String bb = "";
		GetMethod getMethod;
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "application/json");
		getMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		getMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 执行并返回状�?
		try {
			stat = client.executeMethod(getMethod);
			//result = getMethod.getResponseBodyAsString();
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
				result = stringBuffer.toString();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jso1 = null;
		JSONArray jsa1 = null;

		try {
			//将连接服务器后执行的结果转成json类型（传回的值经过处理，本身是json类型的字符串�?
			jso1 = new JSONObject(result);
			if (result.contains("results")) {// 根据Id查询返回结果将不包含results
				//如果存在多条记录，则将记录转化为jsonarray
				jsa1 = (JSONArray) jso1.get("results");
				for (int j = 0; j < jsa1.length(); j++) {
					JSONObject js = jsa1.getJSONObject(j);
					if(js.getString("strThemeName")!=null){
						bb = bb + js.getString("strThemeName") + ",";
					}
					
				}

			} else {
				if(jso1.getString("strThemeName")!=null){
					bb = jso1.getString("strThemeName") + ",";
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bb;

	}
	
	
	/**
	 * 处理VideoTheme联表查询问题
	 * @param data
	 */
	public String JsonVideoTheme(String data) {
		JSONObject jso = null;
		JSONArray jsa = null;
		String aa = "";
		String resu = "";
		String tt = "";
		String ul = "";
		String u = "https://api.leancloud.cn/1.1/cloudQuery";
		JSONObject js = null;
		try {
			jso = new JSONObject(data);
			if(data.contains("results")){
				jsa = (JSONArray) jso.get("results");
				for (int i = 0; i < jsa.length(); i++) {
					js = jsa.getJSONObject(i);
					if (!js.isNull("strCategoryObjectId") && !"".equals(js.getString("strCategoryObjectId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
						tt = js.getString("strCategoryObjectId");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  strVideoCategoryName from VideoCategory where objectId= " + tt;
						resu = jsonJVideoTheme(u + "?cql=" + URLEncoder.encode(ul));
						js.put("strVideoCategoryName", resu);
					}
				}
			}else{
				js = jso;
				if (!js.isNull("strCategoryObjectId") && !"".equals(js.getString("strCategoryObjectId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
					tt = js.getString("strCategoryObjectId");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  strVideoCategoryName from VideoCategory where objectId= " + tt;
					resu = jsonJVideoTheme(u + "?cql=" + URLEncoder.encode(ul));
					js.put("strVideoCategoryName", resu);
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jso.toString();
	}

	
	/**
	 * 
	 * @param url
	 * 处理VideoTheme联表查询问题时被调用
	 * @return
	 */
	public String jsonJVideoTheme(String url) {
		InputStream in = null;
		int stat = 0;
		String result = "";
		HttpClient client = new HttpClient();
		String bb = "";
		GetMethod getMethod;
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "application/json");
		getMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		getMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 执行并返回状�?
		try {
			stat = client.executeMethod(getMethod);
			//result = getMethod.getResponseBodyAsString();
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
				result = stringBuffer.toString();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jso1 = null;
		JSONArray jsa1 = null;
		try {
			jso1 = new JSONObject(result);
			if (result.contains("results")) {// 如果不�?�用cql，并且根据某字段查询的返回结果将不包含results
				jsa1 = (JSONArray) jso1.get("results");
				for (int j = 0; j < jsa1.length(); j++) {
					JSONObject js = jsa1.getJSONObject(j);
					if(js.getString("strVideoCategoryName")!=null){
						bb = bb + js.getString("strVideoCategoryName") + ",";
					}
					
				}
			} else {
				if(jso1.getString("strVideoCategoryName")!=null){
					bb = jso1.getString("strVideoCategoryName") + ",";
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bb;
	}
	
	
	
	/**
	 * 处理Banner联表查询问题
	 * @param data
	 */
	public String JsonBanner(String data) {
		JSONObject jso = null;
		JSONArray jsa = null;
		String aa = "";
		String resu = "";
		String tt = "";
		String ul = "";
		String u = "https://api.leancloud.cn/1.1/cloudQuery";
		JSONObject js = null;
		try {
			jso = new JSONObject(data);
			if(data.contains("results")){
				jsa = (JSONArray) jso.get("results");
				for (int i = 0; i < jsa.length(); i++) {
					js = jsa.getJSONObject(i);
					if (!js.isNull("strVideoFragementId") && !"".equals(js.getString("strVideoFragementId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
						tt = js.getString("strVideoFragementId");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  strVideoFragmentName from VideoFragment where objectId= " + tt;
						resu = jsonJBanner(u + "?cql=" + URLEncoder.encode(ul));
						js.put("strVideoFragmentName", resu);
					}
					if (!js.isNull("strVideoThemeID") && !"".equals(js.getString("strVideoThemeID"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
						tt = js.getString("strVideoThemeID");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  strThemeName from VideoTheme where objectId= " + tt;
						resu = jsonJBannerVideoTheme(u + "?cql=" + URLEncoder.encode(ul));
						js.put("strThemeName", resu);
					}
					if (!js.isNull("strCategeryID") && !"".equals(js.getString("strCategeryID"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
						tt = js.getString("strCategeryID");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  strVideoCategoryName from VideoCategory where objectId= " + tt;
						resu = jsonJBannerCategery(u + "?cql=" + URLEncoder.encode(ul));
						js.put("strVideoCategoryName", resu);
					}
					
					
				}
			}else{
				js = jso;
				if (!js.isNull("strVideoFragementId") && !"".equals(js.getString("strVideoFragementId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
					tt = js.getString("strVideoFragementId");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  strVideoFragmentName from VideoFragment where objectId= " + tt;
					resu = jsonJBanner(u + "?cql=" + URLEncoder.encode(ul));
					js.put("strVideoFragmentName", resu);
				}
				if (!js.isNull("strVideoThemeID") && !"".equals(js.getString("strVideoThemeID"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
					tt = js.getString("strVideoThemeID");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  strThemeName from VideoTheme where objectId= " + tt;
					resu = jsonJBannerVideoTheme(u + "?cql=" + URLEncoder.encode(ul));
					js.put("strThemeName", resu);
				}
				if (!js.isNull("strCategeryID") && !"".equals(js.getString("strCategeryID"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
					tt = js.getString("strCategeryID");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  strVideoCategoryName from VideoCategory where objectId= " + tt;
					resu = jsonJBannerCategery(u + "?cql=" + URLEncoder.encode(ul));
					js.put("strVideoCategoryName", resu);
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jso.toString();
	}

	
	/**
	 * 
	 * @param url
	 * 处理Banner联表查询问题时被调用
	 * @return
	 */
	public String jsonJBanner(String url) {
		InputStream in = null;
		int stat = 0;
		String result = "";
		HttpClient client = new HttpClient();
		String bb = "";
		GetMethod getMethod;
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "application/json");
		getMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		getMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 执行并返回状�?
		try {
			stat = client.executeMethod(getMethod);
			//result = getMethod.getResponseBodyAsString();
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
				result = stringBuffer.toString();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jso1 = null;
		JSONArray jsa1 = null;
		try {
			jso1 = new JSONObject(result);
			if (result.contains("results")) {// 如果不�?�用cql，并且根据某字段查询的返回结果将不包含results
				jsa1 = (JSONArray) jso1.get("results");
				for (int j = 0; j < jsa1.length(); j++) {
					JSONObject js = jsa1.getJSONObject(j);
					if(js.getString("strVideoFragmentName")!=null){
						bb = bb + js.getString("strVideoFragmentName") + ",";
					}
					
				}
			} else {
				if(jso1.getString("strVideoFragmentName")!=null){
					bb = jso1.getString("strVideoFragmentName") + ",";
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bb;
	}
	
	/**
	 * 
	 * @param url
	 * 处理Banner联表查询问题时被调用
	 * 嗲用strVideoThemeID时用
	 * @return
	 */
	public String jsonJBannerVideoTheme(String url) {
		InputStream in = null;
		int stat = 0;
		String result = "";
		HttpClient client = new HttpClient();
		String bb = "";
		GetMethod getMethod;
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "application/json");
		getMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		getMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 执行并返回状�?
		try {
			stat = client.executeMethod(getMethod);
			//result = getMethod.getResponseBodyAsString();
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
				result = stringBuffer.toString();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jso1 = null;
		JSONArray jsa1 = null;
		try {
			jso1 = new JSONObject(result);
			if (result.contains("results")) {// 如果不�?�用cql，并且根据某字段查询的返回结果将不包含results
				jsa1 = (JSONArray) jso1.get("results");
				for (int j = 0; j < jsa1.length(); j++) {
					JSONObject js = jsa1.getJSONObject(j);
					if(js.getString("strThemeName")!=null){
						bb = bb + js.getString("strThemeName") + ",";
					}
					
				}
			} else {
				if(jso1.getString("strThemeName")!=null){
					bb = jso1.getString("strThemeName") + ",";
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bb;
	}
	
	
	/**
	 * 
	 * @param url
	 * 处理Banner联表查询问题时被调用
	 * 调用另一个参数strCategeryID
	 * @return
	 */
	public String jsonJBannerCategery(String url) {
		InputStream in = null;
		int stat = 0;
		String result = "";
		HttpClient client = new HttpClient();
		String bb = "";
		GetMethod getMethod;
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "application/json");
		getMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		getMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 执行并返回状�?
		try {
			stat = client.executeMethod(getMethod);
			//result = getMethod.getResponseBodyAsString();
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
				result = stringBuffer.toString();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jso1 = null;
		JSONArray jsa1 = null;
		try {
			jso1 = new JSONObject(result);
			if (result.contains("results")) {// 如果不�?�用cql，并且根据某字段查询的返回结果将不包含results
				jsa1 = (JSONArray) jso1.get("results");
				for (int j = 0; j < jsa1.length(); j++) {
					JSONObject js = jsa1.getJSONObject(j);
					if(js.getString("strVideoCategoryName")!=null){
						bb = bb + js.getString("strVideoCategoryName") + ",";
					}
					
				}
			} else {
				if(jso1.getString("strVideoCategoryName")!=null){
					bb = jso1.getString("strVideoCategoryName") + ",";
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bb;
	}
	

	/**
	 * 处理Music联表查询问题
	 * @param data
	 */
	public String JsonMusic(String data) {
		JSONObject jso = null;
		JSONArray jsa = null;
		String aa = "";
		String resu = "";
		String tt = "";
		String ul = "";
		String u = "https://api.leancloud.cn/1.1/cloudQuery";
		JSONObject js = null;
		try {
			jso = new JSONObject(data);
			if(data.contains("results")){
				jsa = (JSONArray) jso.get("results");
				for (int i = 0; i < jsa.length(); i++) {
					js = jsa.getJSONObject(i);
					if (!js.isNull("strMusicCategoryID") && !"".equals(js.getString("strMusicCategoryID"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
						tt = js.getString("strMusicCategoryID");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  strMusicCategoryName from MusicCategory where objectId= " + tt;
						resu = jsonJMusic(u + "?cql=" + URLEncoder.encode(ul));
						js.put("strMusicCategoryName", resu);
					}
		
					
				}
			}else{
				js = jso;
				if (!js.isNull("strMusicCategoryID") && !"".equals(js.getString("strMusicCategoryID"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
					tt = js.getString("strMusicCategoryID");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  strMusicCategoryName from MusicCategory where objectId= " + tt;
					resu = jsonJMusic(u + "?cql=" + URLEncoder.encode(ul));
					js.put("strMusicCategoryName", resu);
				}
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jso.toString();
	}

	
	/**
	 * 
	 * @param url
	 * 处理Music联表查询问题时被调用
	 * @return
	 */
	public String jsonJMusic(String url) {
		InputStream in = null;
		int stat = 0;
		String result = "";
		HttpClient client = new HttpClient();
		String bb = "";
		GetMethod getMethod;
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "application/json");
		getMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		getMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 执行并返回状�?
		try {
			stat = client.executeMethod(getMethod);
			//result = getMethod.getResponseBodyAsString();
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
				result = stringBuffer.toString();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jso1 = null;
		JSONArray jsa1 = null;
		try {
			jso1 = new JSONObject(result);
			if (result.contains("results")) {// 如果不�?�用cql，并且根据某字段查询的返回结果将不包含results
				jsa1 = (JSONArray) jso1.get("results");
				for (int j = 0; j < jsa1.length(); j++) {
					JSONObject js = jsa1.getJSONObject(j);
					if(js.getString("strMusicCategoryName")!=null){
						bb = bb + js.getString("strMusicCategoryName") + ",";
					}
					
				}
			} else {
				if(jso1.getString("strMusicCategoryName")!=null){
					bb = jso1.getString("strMusicCategoryName") + ",";
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bb;
	}
	
	
	
	/**
	 * 处理Sound联表查询问题
	 * @param data
	 */
	public String JsonSound(String data) {
		JSONObject jso = null;
		JSONArray jsa = null;
		String aa = "";
		String resu = "";
		String tt = "";
		String ul = "";
		String u = "https://api.leancloud.cn/1.1/cloudQuery";
		JSONObject js = null;
		try {
			jso = new JSONObject(data);
			if(data.contains("results")){
				jsa = (JSONArray) jso.get("results");
				for (int i = 0; i < jsa.length(); i++) {
					js = jsa.getJSONObject(i);
					if (!js.isNull("strSoundCategoryID") && !"".equals(js.getString("strSoundCategoryID"))) {// 如果数据表中strSoundCategoryID为空，则查询结果中无此字�?
						tt = js.getString("strSoundCategoryID");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  strSoundCategoryName from SoundCategory where objectId= " + tt;
						resu = jsonJSound(u + "?cql=" + URLEncoder.encode(ul));
						js.put("strSoundCategoryName", resu);
					}
		
					
				}
			}else{
				js = jso;
				if (!js.isNull("strSoundCategoryID") && !"".equals(js.getString("strSoundCategoryID"))) {// 如果数据表中strSoundCategoryID为空，则查询结果中无此字�?
					tt = js.getString("strSoundCategoryID");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  strSoundCategoryName from SoundCategory where objectId= " + tt;
					resu = jsonJSound(u + "?cql=" + URLEncoder.encode(ul));
					js.put("strSoundCategoryName", resu);
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jso.toString();
	}

	
	/**
	 * 
	 * @param url
	 * 处理Sound联表查询问题时被调用
	 * @return
	 */
	public String jsonJSound(String url) {
		InputStream in = null;
		int stat = 0;
		String result = "";
		HttpClient client = new HttpClient();
		String bb = "";
		GetMethod getMethod;
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "application/json");
		getMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		getMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 执行并返回状�?
		try {
			stat = client.executeMethod(getMethod);
			//result = getMethod.getResponseBodyAsString();
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
				result = stringBuffer.toString();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jso1 = null;
		JSONArray jsa1 = null;
		try {
			jso1 = new JSONObject(result);
			if (result.contains("results")) {// 如果不用objectId查询，并且根据某字段查询的返回结果将不包含results
				jsa1 = (JSONArray) jso1.get("results");
				for (int j = 0; j < jsa1.length(); j++) {
					JSONObject js = jsa1.getJSONObject(j);
					if(js.getString("strSoundCategoryName")!=null){
						bb = bb + js.getString("strSoundCategoryName") + ",";
					}
					
				}
			} else {
				if(jso1.getString("strSoundCategoryName")!=null){
					bb = jso1.getString("strSoundCategoryName") + ",";
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bb;
	}
	
	
	
	
	/**
	 * 处理Comment联表查询问题
	 * @param data
	 */
	public String JsonComment(String data) {
		JSONObject jso = null;
		JSONArray jsa = null;
		String aa = "";
		String resu = "";
		String tt = "";
		String ul = "";
		String u = "https://api.leancloud.cn/1.1/cloudQuery";
		JSONObject js = null;
		try {
			jso = new JSONObject(data);
			if(data.contains("results")){
				jsa = (JSONArray) jso.get("results");
				for (int i = 0; i < jsa.length(); i++) {
					js = jsa.getJSONObject(i);
					if (!js.isNull("strCommentUserId") && !"".equals(js.getString("strCommentUserId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
						tt = js.getString("strCommentUserId");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  username from _User where objectId= " + tt;
						resu = jsonJComment(u + "?cql=" + URLEncoder.encode(ul));
						js.put("username", resu);
					}
					
					if (!js.isNull("strCommentVideoFragmentId") && !"".equals(js.getString("strCommentVideoFragmentId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
						tt = js.getString("strCommentVideoFragmentId");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  strVideoFragmentName from VideoFragment where objectId= " + tt;
						resu = jsonJComment(u + "?cql=" + URLEncoder.encode(ul));
						js.put("strVideoFragmentName", resu);
					}
					if (!js.isNull("strCommentId") && !"".equals(js.getString("strCommentId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
						tt = js.getString("strCommentId");
						tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
						ul = "select  strCommentContent from Comment where objectId= " + tt;
						resu = jsonJComment(u + "?cql=" + URLEncoder.encode(ul));
						js.put("strInitialComentName", resu);
					}
					
				}
			}else{
				js = jso;
				if (!js.isNull("strCommentUserId") && !"".equals(js.getString("strCommentUserId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
					tt = js.getString("strCommentUserId");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  username from _User where objectId= " + tt;
					resu = jsonJComment(u + "?cql=" + URLEncoder.encode(ul));
					js.put("username", resu);
				}
				if (!js.isNull("strCommentVideoFragmentId") && !"".equals(js.getString("strCommentVideoFragmentId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
					tt = js.getString("strCommentVideoFragmentId");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  strVideoFragmentName from VideoFragment where objectId= " + tt;
					resu = jsonJComment(u + "?cql=" + URLEncoder.encode(ul));
					js.put("strVideoFragmentName", resu);
				}
				if (!js.isNull("strCommentId") && !"".equals(js.getString("strCommentId"))) {// 如果数据表中strVideoPubUserId为空，则查询结果中无此字�?
					tt = js.getString("strCommentId");
					tt = "'" + tt + "'";// 为满足cql�?求，字段前后要加单引�?
					ul = "select  strCommentContent from Comment where objectId= " + tt;
					resu = jsonJComment(u + "?cql=" + URLEncoder.encode(ul));
					js.put("strInitialComentName", resu);
				}
				
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jso.toString();
	}

	
	/**
	 * 
	 * @param url
	 * 处理Comment联表查询问题时被调用
	 * @return
	 */
	public String jsonJComment(String url) {
		InputStream in = null;
		int stat = 0;
		String result = "";
		HttpClient client = new HttpClient();
		String bb = "";
		GetMethod getMethod;
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("Content-Type", "application/json");
		getMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
		getMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 执行并返回状�?
		try {
			stat = client.executeMethod(getMethod);
			//result = getMethod.getResponseBodyAsString();
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
				result = stringBuffer.toString();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jso1 = null;
		JSONArray jsa1 = null;
		try {
			jso1 = new JSONObject(result);
			if (result.contains("results")) {// 如果不�?�用cql，并且根据某字段查询的返回结果将不包含results
				jsa1 = (JSONArray) jso1.get("results");
				for (int j = 0; j < jsa1.length(); j++) {
					JSONObject js = jsa1.getJSONObject(j);
					if(js.getString("strSoundCategoryName")!=null){
						bb = bb + js.getString("strSoundCategoryName") + ",";
					}
					
				}
			} else {
				if(jso1.getString("strSoundCategoryName")!=null){
					bb = jso1.getString("strSoundCategoryName") + ",";
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bb;
	}
	
	
	
	
	
	public String checkVideoFragment(String[] ids, String strAuditFlag) {
		InputStream in = null;
		String back = "";
		int status = 0;
		int flag = 1;// 默认批量处理成功
		String result = "";
		String data = "{\"strAuditFlag\":" + strAuditFlag + "}";
		String url = "https://leancloud.cn:443/1.1/classes/VideoFragment/";
		JSONObject json = null;
		try {
			json = new JSONObject(data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpClient client = new HttpClient();
		for (int i = 0; i < ids.length; i++) {
			PutMethod putMethod = new PutMethod(url + ids[i]);
			putMethod.addRequestHeader("Content-Type", "application/json,charset=utf-8");
			putMethod.setRequestHeader("X-LC-Id", OaConstant.appId);
			putMethod.setRequestHeader("X-LC-Key", OaConstant.appKey);
			client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
			putMethod.setRequestBody(json.toString());
			try {
				status = client.executeMethod(putMethod);
				//result = result + putMethod.getResponseBodyAsString();
				in = putMethod.getResponseBodyAsStream();
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
					back = stringBuffer.toString();
					result = result+back;
				if (!result.contains("updatedAt")) {// 更新不成�?
					flag = 0;
				}

			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (flag == 0) {
			result = "wrong";
		} else {
			result = "sucess";
		}
		return result;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		FileProcess fp = new FileProcess();
		fp.upload(_fileUrl,_getUrl,_className);
	}

}
