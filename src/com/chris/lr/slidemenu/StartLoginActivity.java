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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.centerm.mpos.util.Log;


public class StartLoginActivity extends Activity {
    private LinearLayout login;

    private EditText phoneNumber;
    private EditText passWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start_login);


        login= (LinearLayout) findViewById(R.id.login_login);

        phoneNumber=(EditText) findViewById(R.id.phonenumber);
        passWord=(EditText) findViewById(R.id.password);

        login.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                doLogin(phoneNumber.getText()+"",passWord.getText()+"");


            }
        });
    }
    private void doLogin(String str1,String str2){
        Log.i("xys", "开始登录EEEEEE");
        TelephonyManager telephonyManager = (TelephonyManager) this  
        		  .getSystemService(Context.TELEPHONY_SERVICE);  
        String IMEI = telephonyManager.getDeviceId();  
        new AsyLogin().execute(str1,str2,IMEI);
    }
    class AsyLogin extends AsyncTask<String, String, String> {
        public AsyLogin(){

        }
        @Override
        protected String doInBackground(String... args) {
            Log.i("xys","开始登录......");
            String result=null;
            List<BasicNameValuePair> data=new ArrayList<BasicNameValuePair>();
            data.add(new BasicNameValuePair("http", "http://192.168.1.196:8080/XysAppWeb/AppMain.action"));
            data.add(new BasicNameValuePair("msg", "0101"));
            data.add(new BasicNameValuePair("phone","15376764657"));
            data.add(new BasicNameValuePair("pwd","123456"));
            data.add(new BasicNameValuePair("code",args[2]));
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
            super.onPostExecute(result);
            if(result==null){
                Log.i("xys","网络通信发生异常");

            }else{
            	 goToMain(result);
                Log.i("xys",result);
              
            }
        }

    }

    private void goToMain(String result) {
    	
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



}
