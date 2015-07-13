package com.chris.lr.slidemenu;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.ctmpos.MPOSControllerCallback;
import com.centerm.mpos.util.StringUtil;

public class CommadTestActivity extends Activity {

	private LinearLayout queRen;
	private LinearLayout quXiao;
    private String[] data;
    private TextView tv_kahao;
    private TextView tv_yinhang;
    private TextView tv_kaihuming;
    private TextView tv_jine;
    private TextView tv_fukuanshijian;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.buy_confirm);
		Intent intent=getIntent();
		data=intent.getStringArrayExtra("data");
		queRen = (LinearLayout) findViewById(R.id.queren);
		quXiao = (LinearLayout) findViewById(R.id.quxiao);
		tv_kahao=(TextView) findViewById(R.id.kahao);
		tv_yinhang=(TextView)findViewById(R.id.yinhang);
		tv_kaihuming=(TextView)findViewById(R.id.kaihuming);
		tv_jine=(TextView)findViewById(R.id.jine);
		tv_fukuanshijian=(TextView)findViewById(R.id.fukuanshijian);
		tv_kahao.setText(Const.cardno);
		tv_yinhang.setText(Const.bank);
		tv_kaihuming.setText(Const.xingming);
		tv_jine.setText(Const.jine);
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
		String dateStr=sdf.format(date);
		tv_fukuanshijian.setText(dateStr);
		queRen.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				queRen.setClickable(false);
				// 1>>>>  开启消费流程
				startBuy();
			}
		});
		quXiao.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right); 
			}
		});

	}

	private void startBuy() {
		// 6>>>> 计算自定义MAC需要的map
		byte[] map = calMacMap(data[0],data[1],data[2],data[3],data[4],data[5],data[6],data[7],data[8]);
		// 7>>>> 计算自定义MAC
						MainActivity.controller.calMac(1, map,
								new MPOSControllerCallback() {
							// 计算MAC成功
									@Override
									public void onCalMACSuc(byte[] result) {
										Log.i("xys","计算自定义MAC:"+StringUtil.byte2HexStr(result));
										// 8>>>> 开启异步任务发送组装消费信息
										new AsyXiaoFeiTask().execute(data[0],data[1],data[2],data[3],data[4],data[5],data[6],StringUtil.byte2HexStr(result),data[7],data[8]);
									}
									@Override
									public void onError(int errId,String errMsg) {
										showToast(null, errId+ ":" + errMsg);
										//queRen.setClickable(true);
									}
								});
	}

	// 计算参与计算mac的map字节数组
	private byte[] calMacMap(String panID,String pan, String expireDT,String oneTD, String twoTD,String threeTD,
			final String pinBlock,String info,String amt) {
		byte[] map = null;
		String[] datas = new String[63];
		datas[0] = "0200";
		datas[1] = pan;
		datas[2] = "000000";
		if(amt.length()!=0){
			datas[3] = amt;	
		}
		datas[10] = "000015";
		datas[13] = expireDT;
		datas[21] = "0510";
		if(panID.length()!=0){
			datas[22] = "00"+panID;
		}
		
		datas[24] = "00";
		datas[25] = "12";
		if(twoTD.length()!=0){
			Log.i("xys","切割TWOTD："+twoTD+";"+twoTD.length());
			if(twoTD.substring(twoTD.length()-1, twoTD.length()).equals("F")){
				twoTD=twoTD.substring(0, twoTD.length()-1);
			}
			datas[34] = twoTD;
		}
		
		if(threeTD.length()!=0){
			if(threeTD.substring(threeTD.length()-1, threeTD.length()).equals("F")){
				threeTD=threeTD.substring(0, threeTD.length()-1);
			}	
			datas[35]=threeTD;
		}
		
		datas[40] = Const.terminal;
		datas[41] = Const.shanghuhao;
		datas[48] = "156";
		datas[51] = pinBlock;
		datas[52] = "2600000000000000";
		if(info.length()!=0){
			datas[54] = info;
		}
		
		datas[59] = Const.batchnum;
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < datas.length; i++) {
			if (datas[i] == null) {
				datas[i] = "";
			}
			sb.append(datas[i]);
		}
		
		String c = sb.toString();
		
		if(c.length()%2!=0){
			c=c+"0";
		}
		map = hexStringToBytes(c);
		Const.map = map;
		
		Log.i("xys", "map:"+Const.map[1]);
		return map;
	}
	public static byte[] hexStringToBytes(String hexString) {   
	    if (hexString == null || hexString.equals("")) {   
	        return null;   
	    }   
	    hexString = hexString.toUpperCase();   
	    int length = hexString.length() / 2;   
	    char[] hexChars = hexString.toCharArray();   
	    byte[] d = new byte[length];   
	    for (int i = 0; i < length; i++) {   
	        int pos = i * 2;   
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));   
	    }   
	    return d;   
	}  
	 private static byte charToByte(char c) {   
		    return (byte) "0123456789ABCDEF".indexOf(c);   
		}  
	// 异步任务发送自定义组装的消费信息到服务平台
	class AsyXiaoFeiTask extends AsyncTask<String, String, String> {
		String[] datas=null;
		public AsyXiaoFeiTask() {
             datas= new String[64];
		}
		@Override
		protected String doInBackground(String... args) {
			Log.i("xys", "开始发送消费数据>>>>>>");
			// 9 >>>>>组装消费信息
			String result = null;
			
			datas[0] = "0200";
			datas[1] = args[1];// 卡号
			datas[2] = "000000";
			if(args[9].length()!=0){
				datas[3] = args[9];
			}
			datas[10] = "000015";
			datas[13] = args[2];// 卡有效期
			datas[21] = "0510";
			if(args[0].length()!=0){
				datas[22] = "00"+args[0];
			}
			datas[24] = "00";
			datas[25] = "12";
			if(args[4].length()!=0){
				if(args[4].substring(args[4].length()-1, args[4].length()).equals("F")){
					args[4]=args[4].substring(0, args[4].length()-1);
				}
				datas[34] = args[4]; // 二磁道数据
			}
			
			if(args[5].length()!=0){
				if(args[5].substring(args[5].length()-1, args[5].length()).equals("F")){
					args[5]=args[5].substring(0, args[5].length()-1);
				}
				datas[35]=args[5];
			}
			
			
			datas[40] = Const.terminal;
			datas[41] = Const.shanghuhao;
			datas[48] = "156";
			datas[51] = args[6]; // pinBlock
			datas[52] = "2600000000000000";
			if(args[8].length()!=0){
				datas[54] = args[8];
			}
			
			datas[59] = Const.batchnum;
			datas[63] = args[7]; // MAC
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < datas.length; i++) {
				if (datas[i] == null) {
					datas[i] = "";
				}
				sb.append(datas[i]).append(">");
			}
			Const.data0=datas;
			sb = sb.deleteCharAt(sb.length() - 1);
			
			// 10>>>>发送组装数据
			List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
			data.add(new BasicNameValuePair("http","http://192.168.1.196:8080/XysAppWeb/AppMain.action"));
			data.add(new BasicNameValuePair("msg", "0200"));
			data.add(new BasicNameValuePair("logo", "service"));
			data.add(new BasicNameValuePair("data", sb.toString()));
			Log.i("xys", "data:"+sb.toString());
			try {
				// 服务平台响应数据
				result = Utils.HttpPostData(data);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) {
				Log.i("xys", ">>>>发生异常");
				showToast(null, ">>>>发生异常");
			} else {
				Log.i("xys", "通信正常>>>>>" + result);
				
				// 11 >>>>>>解析服务平台返回的数据
				ResultParser(result);
				//showToast(null, "通信正常>>>>>" + result);
			}
		}
	}

	public void ResultParser(String result) {
		try {
			JSONObject json = new JSONObject(result);
			final String head = (String) json.get("head");
			final String data = (String) json.get("data");
			// 服务平台返回的自定义MAC,用于校验
			final String mac = (String) json.get("mac");
			// 14 >>>> 计算签到MAC
			MainActivity.controller.calMac(1, hexStringToBytes(data),
					new MPOSControllerCallback() {
						@Override
						public void onCalMACSuc(byte[] result) {
							if(!StringUtil.byte2HexStr(result).equals(mac)){
								Log.i("xys","校验MAC1失败");
								showToast(null,"校验MAC1失败");
								return;
							}
							Log.i("xys","计算MAC1:"+StringUtil.byte2HexStr(result)+"RRRRRRR"+mac);
							// 15 >>>>组装发送8583消费数据到服务平台
							String datatemp=data;
							if(datatemp.length()%2!=0){
								datatemp=datatemp+"0";
								Log.i("xys","追加0");
							}
							MainActivity.controller.calMac(0, hexStringToBytes(datatemp),
									new MPOSControllerCallback() {
										@Override
										public void onCalMACSuc(byte[] result0) {
											Log.i("xys","计算签到MAC:"+StringUtil.byte2HexStr(result0));
											// 15 >>>>组装发送8583消费数据到服务平台
											new AsyPOSP().execute(head,data,StringUtil.byte2HexStr(result0));
										}

										@Override
										public void onError(int errId, String errMsg) {
											showToast(null, errId + ":" + errMsg);
										}
									});
							
						}

						@Override
						public void onError(int errId, String errMsg) {
							showToast(null, errId + ":" + errMsg);
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	// 异步发送8583消费数据
	class AsyPOSP extends AsyncTask<String, String, String> {

		public AsyPOSP(){
			
		}
		@Override
		protected String doInBackground(String... params) {
			Log.i("xys", "开始发送消费8583 POSP数据>>>>>>");
			
			// 16 >>>>组装8583消费数据
			String result = null;
			
			List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
			data.add(new BasicNameValuePair("http","http://192.168.1.196:8080/XysAppWeb/AppMain.action"));
			data.add(new BasicNameValuePair("msg", "0200"));
			data.add(new BasicNameValuePair("logo", "posp"));
			data.add(new BasicNameValuePair("data", params[0] + params[1]+ params[2]));
			try {
				// 服务平台响应数据
				result = Utils.HttpPostData(data);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) {
				Log.i("xys", "发生异常>>>>>");
			} else {
				// 17>>>>>>> 解析POSP响应报文
				Log.i("xys", "通信成功>>>>" + result);
				ParserPOSP(result);
			}
		}

	}

	// 解析POSP返回报文{"msg":"0200","data":"ok"}
	private void ParserPOSP(String result){
		try {
			JSONObject json=new JSONObject(result);
			String data=(String) json.get("data");
			Log.i("xys", "POSP响应:"+data);
			
			
			if(data.equals("ok")){
				showToast("收款成功!!!",null);
                startBackBuy(null);
				//back();
			}else if(data.equals("pinError")){
				showToast(null,"密码错误!!!");
				back();
			}else if(data.equals("moneyError")){
				showToast(null,"余额不足!!!");
				back();
			}else if(data.equals("no")){
                startBackBuy(null);
				showToast("消费no",null);
				back();
			}else if(data.equals("error")){
				startBackBuy((String) json.get("rpsNo"));
			}else{
                showToast("收款失败!!!",null);
                back();
            }
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	// 开启冲正
	private void startBackBuy(String rpsNo){
		final String[] czData=Const.data0;
		czData[0]="0400";
//		
		if(rpsNo==null){
			czData[38]="06";
		}else{
			czData[38]=rpsNo;
		}
		//czData[22]="0001";
//		String temp1=czData[34];
//		String temp2=czData[35];
//		czData[34]="";
//		czData[35]=temp1;
//		czData[36]=temp2;
		Log.i("xys", "错位:"+czData[35]+";"+czData[36]);
		czData[51]="";
		czData[52]="";
		
		String map="";
		for(int i=0;i<czData.length-1;i++){
			if(czData[i]==null){
				czData[i]="";
			}
			map=map+czData[i];
		}
		if(map.length()%2!=0){
			map+="0";
		}
		
		byte[] mab=hexStringToBytes(map);
		MainActivity.controller.calMac(1,mab ,new MPOSControllerCallback() {
			
			@Override
			public void onCalMACSuc(byte[] result) {
				Log.i("xys","计算冲正MAC1:"+StringUtil.byte2HexStr(result));
				// 15 >>>>组装发送8583消费数据到服务平台
				czData[czData.length-1]=StringUtil.byte2HexStr(result);
				new AsyPOSTBuyBack().execute(czData);
			}
			@Override
			public void onError(int arg0, String arg1) {
				showToast(null,arg1);
			}
		});
	}
	class AsyPOSTBuyBack extends AsyncTask<String[], String, String>{
public AsyPOSTBuyBack(){
	
}
		@Override
		protected String doInBackground(String[]... arg0) {
			String result=null;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < arg0[0].length; i++) {
				sb.append(arg0[0][i]).append(">");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
			List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
			data.add(new BasicNameValuePair("http","http://192.168.1.196:8080/XysAppWeb/AppMain.action"));
			data.add(new BasicNameValuePair("msg", "0400"));
			data.add(new BasicNameValuePair("logo", "service"));
			data.add(new BasicNameValuePair("data", sb.toString()));
			Log.i("xys", "冲正data:"+sb.toString());
			try {
				result=Utils.HttpPostData(data);
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
				Log.i("xys", "冲正>>>>>>发生异常");
			}else{
				Log.i("xys", "冲正>>>>>>"+result);
				ParserBuyBack(result);
			}
		}
	}
	private void ParserBuyBack(String result){
		try {
			JSONObject json=new JSONObject(result);
			final String head=(String) json.get("head");
			final String data=(String)json.get("data");
			final String mac=(String)json.get("mac");
			MainActivity.controller.calMac(1, hexStringToBytes(data),
					new MPOSControllerCallback() {
						@Override
						public void onCalMACSuc(byte[] result) {
							if(!StringUtil.byte2HexStr(result).equals(mac)){
								Log.i("xys","校验MAC失败");
								return;
							}
							Log.i("xys","校验MAC1:"+StringUtil.byte2HexStr(result)+"RRRRRRR"+mac);
							// 15 >>>>组装发送8583消费数据到服务平台
							String datatemp=data;
							//String datatemp="0400702406C012C08011166226900305226520000000000000005800000015320705100001001200376226900305226520D32075200000096000000030363637353333343238383230313437323730363733383339313536001422000001000501";
							if(datatemp.length()%2!=0){
								datatemp=datatemp+"0";
								Log.i("xys","追加0");
							}
							MainActivity.controller.calMac(0, hexStringToBytes(datatemp),
									new MPOSControllerCallback() {
										@Override
										public void onCalMACSuc(byte[] result0) {
											Log.i("xys","计算冲正MAC0:"+StringUtil.byte2HexStr(result0));
											Toast.makeText(getApplicationContext(), StringUtil.byte2HexStr(result0), Toast.LENGTH_SHORT).show();
											
											// 15 >>>>组装发送8583消费数据到服务平台
											new AsyPOSPStartBuy().execute(head,data,StringUtil.byte2HexStr(result0));
										}

										@Override
										public void onError(int errId, String errMsg) {
											showToast(null, errId + ":" + errMsg);
										}
									});
						}

						@Override
						public void onError(int errId, String errMsg) {
							showToast(null, errId + ":" + errMsg);
						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class AsyPOSPStartBuy extends AsyncTask<String, String, String> {

		public AsyPOSPStartBuy(){
			
		}
		@Override
		protected String doInBackground(String... params) {
			Log.i("xys", "开始发送消费8583 冲正POSP数据>>>>>>");
			
			// 16 >>>>组装8583消费数据
			String result = null;
			
			List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
			data.add(new BasicNameValuePair("http","http://192.168.1.196:8080/XysAppWeb/AppMain.action"));
			data.add(new BasicNameValuePair("msg", "0400"));
			data.add(new BasicNameValuePair("logo", "posp"));
			data.add(new BasicNameValuePair("data", params[0] + params[1]+ params[2]));
			try {
				// 服务平台响应数据
				result = Utils.HttpPostData(data);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) {
				Log.i("xys", "发生异常>>>>>");
			} else {
				// 17>>>>>>> 解析POSP响应报文
				Log.i("xys", "通信成功>>>>" + result);
				ParserPOSPStartBuy(result);
			}
		}

	}
	private void ParserPOSPStartBuy(String result){
		JSONObject json;
		Log.i("xys", "冲正POSP返回:"+result);
		try {
			json = new JSONObject(result);
			//String msg=(String) json.get("msg");
			String data=(String)json.get("data");
			if(data.equals("ok")){
				showToast("自动冲正成功!",null);
				back();
			}else{
				showToast(null,"自动冲正失败!");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void back(){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right); 
	}
	private void showToast(String sucMsg, String errMsg) {

		if (sucMsg != null) {
			Toast.makeText(CommadTestActivity.this, "SUC:" + sucMsg,
					Toast.LENGTH_SHORT).show();
		}
		if (errMsg != null) {
			Toast.makeText(CommadTestActivity.this, "FAIL:" + errMsg,
					Toast.LENGTH_SHORT).show();
		}

	}

}
