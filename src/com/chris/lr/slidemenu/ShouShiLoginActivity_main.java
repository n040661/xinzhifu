package com.chris.lr.slidemenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.mpos.util.Log;
import com.chris.lr.slidemenu.StartLoginActivity.AsyLogin;
import com.chris.lr.slidemenu.locus.LocusPassWordView;
import com.chris.lr.slidemenu.locus.LocusPassWordView.OnCompleteListener;

public class ShouShiLoginActivity_main extends Activity {
	private LocusPassWordView lpwv;
	private Toast toast;
	private TextView tv;
	private void showToast(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}

		toast.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shou_shi_login_activity_main);
		
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView_main);
		tv=(TextView) findViewById(R.id.tv_wangji);
		tv.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				goToLogin();
			}
		});
		lpwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// 如果密码正确,则进入主页面。
				if (lpwv.verifyPassword(mPassword)) {
					showToast("密码输入正确!");
					doAsyinitialize();
					
				} else {
					showToast("密码输入错误,请重新输入");
					lpwv.markError(2000);
					//lpwv.clearPassword();
				}
			}
		});

	}

	protected void doAsyinitialize() {
		 TelephonyManager telephonyManager = (TelephonyManager) this  
       		  .getSystemService(Context.TELEPHONY_SERVICE);  
       String IMEI = telephonyManager.getDeviceId();  
       new AsyInitialize().execute(IMEI);
		// TODO Auto-generated method stub
		
	}
class AsyInitialize extends AsyncTask<String, String, String>{

	@Override
	protected String doInBackground(String... args) {
		// TODO Auto-generated method stub
		 Log.i("xys","开始初始化......");
         String result=null;
         List<BasicNameValuePair> data=new ArrayList<BasicNameValuePair>();
         data.add(new BasicNameValuePair("http", "http://192.168.1.196:8080/XysAppWeb/AppMain.action"));
         data.add(new BasicNameValuePair("msg", "0103"));
         data.add(new BasicNameValuePair("code",args[0]));
         try {
             result=Utils.HttpPostData(data);
             Log.i("xys","登录操作完成......");
         } catch (ClientProtocolException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         } catch (JSONException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
         return result;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		 if(result==null){
             Log.i("xys","网络通信发生异常");
         }else{
         	 shoushigoToMain(result);
             Log.i("xys",result);
         }
	}
	
}
	protected void goToLogin() {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		intent.setClass(this,StartLoginActivity.class );
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
		finish();
	}

	public void shoushigoToMain(String result) {
		try {
			JSONObject json=new JSONObject(result);
			//if(json.getString("data")!=null){
			    Const.mail=json.getString("mail");
				Const.shoujihao=json.getString("phone");
				Const.shanghuhao=json.getString("merno");
				Const.xingming=json.getString("name");
				Const.bank=json.getString("bank");
				Const.cardno=json.getString("cardno");
				Log.i("xys","商户号:"+ Const.shanghuhao);
				//Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		        Intent intent=new Intent();
		        intent.setClass(this,MainActivity.class);
		        startActivity(intent);
		        overridePendingTransition(R.anim.in_from_right,R.anim.out_of_left);
		        finish();
			//}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
