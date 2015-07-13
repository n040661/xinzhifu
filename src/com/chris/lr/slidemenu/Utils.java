package com.chris.lr.slidemenu;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.centerm.mpos.util.Log;

public class Utils {
	public static String HttpPostData(List<BasicNameValuePair> params) throws JSONException, ClientProtocolException, IOException{
		 HttpPost httpPost = new HttpPost(params.get(0).getValue());  
		 String retSrc=null;
		// 绑定到请求 Entry   
         JSONObject json = new JSONObject();
         for(int i=1;i<params.size();i++){
        	 json.put(params.get(i).getName(), params.get(i).getValue());  
         }	 
         StringEntity se = new StringEntity(json.toString());
         Log.i("xys","发送数据:"+json.toString());
         httpPost.setEntity(se);  
         // 发送请求   
         HttpClient client=new DefaultHttpClient();
         client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
 				3000);
         HttpResponse httpResponse = client.execute(httpPost);  
             //判断响应是否成功   
             if (httpResponse.getStatusLine().getStatusCode() == 200) {
           	  // 得到应答的字符串，这也是一个 JSON 格式保存的数据   
		           retSrc = EntityUtils.toString(httpResponse.getEntity());  
             }else{
           	  return null;
             }
	
		return retSrc;
		
	}
	
	
}
